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
package org.sonar.server.qualityprofile.ws;

import org.junit.Rule;
import org.junit.Test;
import org.sonar.api.utils.System2;
import org.sonar.core.permission.GlobalPermissions;
import org.sonar.db.DbClient;
import org.sonar.db.DbTester;
import org.sonar.db.qualityprofile.QualityProfileDao;
import org.sonar.server.language.LanguageTesting;
import org.sonar.server.qualityprofile.QProfileFactory;
import org.sonar.server.tester.UserSessionRule;
import org.sonar.server.ws.TestResponse;
import org.sonar.server.ws.WsActionTester;
import org.sonar.test.JsonAssert;
import org.sonarqube.ws.MediaTypes;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

public class CreateActionTest {

  @Rule
  public DbTester db = DbTester.create(System2.INSTANCE);

  @Rule
  public UserSessionRule userSessionRule = UserSessionRule.standalone();

  QualityProfileDao profileDao = new QualityProfileDao(db.myBatis(), mock(System2.class));
  DbClient deprecatedDbClient = new DbClient(db.database(), db.myBatis(), profileDao);

  @Test
  public void should_not_fail_on_no_importers() throws Exception {
    CreateAction underTest = new CreateAction(db.getDbClient(), new QProfileFactory(deprecatedDbClient), null, LanguageTesting.newLanguages("xoo"), userSessionRule);
    WsActionTester wsTester = new WsActionTester(underTest);

    userSessionRule.login("admin").setGlobalPermissions(GlobalPermissions.QUALITY_PROFILE_ADMIN);

    TestResponse response = wsTester.newRequest()
      .setMethod("POST")
      .setMediaType(MediaTypes.JSON)
      .setParam("language", "xoo")
      .setParam("name", "Yeehaw!")
      .execute();
    JsonAssert.assertJson(response.getInput()).isSimilarTo(getClass().getResource("CreateActionTest/create-no-importer.json"));
    assertThat(response.getMediaType()).isEqualTo(MediaTypes.JSON);
  }
}
