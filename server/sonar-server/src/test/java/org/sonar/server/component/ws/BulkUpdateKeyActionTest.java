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

import javax.annotation.Nullable;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.sonar.api.server.ws.WebService;
import org.sonar.api.server.ws.WebService.Param;
import org.sonar.api.utils.System2;
import org.sonar.db.DbClient;
import org.sonar.db.DbSession;
import org.sonar.db.DbTester;
import org.sonar.db.component.ComponentDbTester;
import org.sonar.db.component.ComponentDto;
import org.sonar.server.component.ComponentFinder;
import org.sonar.server.component.ComponentService;
import org.sonar.server.exceptions.NotFoundException;
import org.sonar.server.ws.TestRequest;
import org.sonar.server.ws.WsActionTester;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.sonar.db.component.ComponentTesting.newProjectDto;
import static org.sonarqube.ws.client.component.ComponentsWsParameters.PARAM_FROM;
import static org.sonarqube.ws.client.component.ComponentsWsParameters.PARAM_ID;
import static org.sonarqube.ws.client.component.ComponentsWsParameters.PARAM_KEY;
import static org.sonarqube.ws.client.component.ComponentsWsParameters.PARAM_TO;

public class BulkUpdateKeyActionTest {
  static final String MY_PROJECT_KEY = "my_project_key";
  static final String FROM = "my_";
  static final String TO = "your_";

  @Rule
  public ExpectedException expectedException = ExpectedException.none();
  @Rule
  public DbTester db = DbTester.create(System2.INSTANCE);
  ComponentDbTester componentDb = new ComponentDbTester(db);
  DbClient dbClient = db.getDbClient();

  ComponentService componentService = mock(ComponentService.class);

  WsActionTester ws = new WsActionTester(new BulkUpdateKeyAction(dbClient, new ComponentFinder(dbClient), componentService));

  @Test
  public void call_by_key() {
    ComponentDto project = insertMyProject();

    callByKey(project.key(), FROM, TO);

    assertCallComponentService(project.key());
  }

  @Test
  public void call_by_uuid() {
    ComponentDto project = insertMyProject();

    callByUuid(project.uuid(), FROM, TO);

    assertCallComponentService(project.key());
  }

  @Test
  public void fail_if_from_string_is_not_provided() {
    expectedException.expect(IllegalArgumentException.class);

    ComponentDto project = insertMyProject();

    callByKey(project.key(), null, TO);
  }

  @Test
  public void fail_if_to_string_is_not_provided() {
    expectedException.expect(IllegalArgumentException.class);

    ComponentDto project = insertMyProject();

    callByKey(project.key(), FROM, null);
  }

  @Test
  public void fail_if_uuid_nor_key_provided() {
    expectedException.expect(IllegalArgumentException.class);

    call(null, null, FROM, TO);
  }

  @Test
  public void fail_if_uuid_and_key_provided() {
    expectedException.expect(IllegalArgumentException.class);

    ComponentDto project = insertMyProject();

    call(project.uuid(), project.key(), FROM, TO);
  }

  @Test
  public void fail_if_project_does_not_exist() {
    expectedException.expect(NotFoundException.class);

    callByUuid("UNKNOWN_UUID", FROM, TO);
  }

  @Test
  public void api_definition() {
    WebService.Action definition = ws.getDef();

    assertThat(definition.since()).isEqualTo("6.1");
    assertThat(definition.isPost()).isTrue();
    assertThat(definition.key()).isEqualTo("bulk_update_key");
    assertThat(definition.params())
      .hasSize(4)
      .extracting(Param::key)
      .containsOnlyOnce("id", "key", "from", "to");
  }

  private void assertCallComponentService(String oldKey) {
    verify(componentService).bulkUpdateKey(any(DbSession.class), eq(oldKey), eq(FROM), eq(TO));
  }

  private ComponentDto insertMyProject() {
    return componentDb.insertComponent(newProjectDto().setKey(MY_PROJECT_KEY));
  }

  private String callByUuid(@Nullable String uuid, @Nullable String from, @Nullable String to) {
    return call(uuid, null, from, to);
  }

  private String callByKey(@Nullable String key, @Nullable String from, @Nullable String to) {
    return call(null, key, from, to);
  }

  private String call(@Nullable String uuid, @Nullable String key, @Nullable String from, @Nullable String to) {
    TestRequest request = ws.newRequest();

    if (uuid != null) {
      request.setParam(PARAM_ID, uuid);
    }
    if (key != null) {
      request.setParam(PARAM_KEY, key);
    }
    if (from != null) {
      request.setParam(PARAM_FROM, from);
    }
    if (to != null) {
      request.setParam(PARAM_TO, to);
    }

    return request.execute().getInput();
  }
}
