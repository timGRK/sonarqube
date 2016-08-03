/*
 * SonarQube
 * Copyright (C) 2009-2016 SonarSource SA
 * mailto:contact AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package org.sonar.server.component.ws;

import org.sonar.api.server.ws.Request;
import org.sonar.api.server.ws.Response;
import org.sonar.api.server.ws.WebService;
import org.sonar.db.DbClient;
import org.sonar.db.DbSession;
import org.sonar.db.component.ComponentDto;
import org.sonar.server.component.ComponentFinder;
import org.sonar.server.component.ComponentFinder.ParamNames;
import org.sonar.server.component.ComponentService;
import org.sonarqube.ws.client.component.BulkUpdateWsRequest;

import static org.sonar.core.util.Uuids.UUID_EXAMPLE_01;
import static org.sonarqube.ws.client.component.ComponentsWsParameters.PARAM_FROM;
import static org.sonarqube.ws.client.component.ComponentsWsParameters.PARAM_ID;
import static org.sonarqube.ws.client.component.ComponentsWsParameters.PARAM_KEY;
import static org.sonarqube.ws.client.component.ComponentsWsParameters.PARAM_TO;

public class BulkUpdateKeyAction implements ComponentsWsAction {
  private final DbClient dbClient;
  private final ComponentFinder componentFinder;
  private final ComponentService componentService;

  public BulkUpdateKeyAction(DbClient dbClient, ComponentFinder componentFinder, ComponentService componentService) {
    this.dbClient = dbClient;
    this.componentFinder = componentFinder;
    this.componentService = componentService;
  }

  @Override
  public void define(WebService.NewController context) {
    WebService.NewAction action = context.createAction("bulk_update_key")
      .setDescription("Bulk update a project or module key and all its sub-components keys. " +
        "The bulk update allows to replace a part of the current key by another string on the current project and all its sub-modules.<br>" +
        "Either '%s' or '%s' must be provided, not both.<br> " +
        "Requires one of the following permissions: " +
        "<ul>" +
        "<li>'Administer System'</li>" +
        "<li>'Administer' rights on the specified project</li>" +
        "<li>'Browse' on the specified project</li>" +
        "</ul>", PARAM_ID, PARAM_KEY)
      .setSince("6.1")
      .setPost(true)
      .setHandler(this);

    action.createParam(PARAM_ID)
      .setDescription("Project or module id")
      .setExampleValue(UUID_EXAMPLE_01);

    action.createParam(PARAM_KEY)
      .setDescription("Project or module key")
      .setExampleValue("my_old_project");

    action.createParam(PARAM_FROM)
      .setDescription("String to match in components keys")
      .setRequired(true)
      .setExampleValue("_old");

    action.createParam(PARAM_TO)
      .setDescription("String replacement in components keys")
      .setRequired(true)
      .setExampleValue("_new");
  }

  @Override
  public void handle(Request request, Response response) throws Exception {
    doHandle(toWsRequest(request));
    response.noContent();
  }

  private void doHandle(BulkUpdateWsRequest request) {
    DbSession dbSession = dbClient.openSession(false);
    try {
      ComponentDto projectOrModule = componentFinder.getByUuidOrKey(dbSession, request.getId(), request.getKey(), ParamNames.ID_AND_KEY);
      componentService.bulkUpdateKey(dbSession, projectOrModule.key(), request.getFrom(), request.getTo());
      dbSession.commit();
    } finally {
      dbClient.closeSession(dbSession);
    }
  }

  private static BulkUpdateWsRequest toWsRequest(Request request) {
    return BulkUpdateWsRequest.builder()
      .setId(request.param(PARAM_ID))
      .setKey(request.param(PARAM_KEY))
      .setFrom(request.mandatoryParam(PARAM_FROM))
      .setTo(request.mandatoryParam(PARAM_TO))
      .build();
  }
}
