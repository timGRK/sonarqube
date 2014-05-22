/*
 * SonarQube, open source software quality management tool.
 * Copyright (C) 2008-2014 SonarSource
 * mailto:contact AT sonarsource DOT com
 *
 * SonarQube is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * SonarQube is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.sonar.server.platform.ws;

import com.google.common.collect.ImmutableSet;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.sonar.core.i18n.DefaultI18n;
import org.sonar.server.user.MockUserSession;
import org.sonar.server.ws.WsTester;
import org.sonar.server.ws.WsTester.Result;

import java.util.Locale;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class L10nWsTest {

  @Mock
  DefaultI18n i18n;

  @Test
  public void should_return_all_l10n_messages_using_accept_header() throws Exception {
    Locale locale = Locale.PRC;
    MockUserSession.set().setLocale(locale);

    String key1 = "key1";
    String key2 = "key2";
    String key3 = "key3";

    when(i18n.getPropertyKeys()).thenReturn(ImmutableSet.of(key1, key2, key3));
    when(i18n.message(locale, key1, key1)).thenReturn(key1);
    when(i18n.message(locale, key2, key2)).thenReturn(key2);
    when(i18n.message(locale, key3, key3)).thenReturn(key3);

    Result result = new WsTester(new L10nWs(i18n)).newGetRequest("api/l10n", "index").execute();
    verify(i18n).getPropertyKeys();
    verify(i18n).message(locale, key1, key1);
    verify(i18n).message(locale, key2, key2);
    verify(i18n).message(locale, key3, key3);

    result.assertJson("{key1:'key1',key2:'key2',key3:'key3'}");
  }

  @Test
  public void should_override_locale_when_locale_param_is_set() throws Exception {
    Locale locale = Locale.PRC;
    MockUserSession.set().setLocale(locale);
    Locale override = Locale.JAPANESE;

    String key1 = "key1";
    String key2 = "key2";
    String key3 = "key3";

    when(i18n.getPropertyKeys()).thenReturn(ImmutableSet.of(key1, key2, key3));
    when(i18n.message(override, key1, key1)).thenReturn(key1);
    when(i18n.message(override, key2, key2)).thenReturn(key2);
    when(i18n.message(override, key3, key3)).thenReturn(key3);

    Result result = new WsTester(new L10nWs(i18n)).newGetRequest("api/l10n", "index").setParam("locale", override.toString()).execute();
    verify(i18n).getPropertyKeys();
    verify(i18n).message(override, key1, key1);
    verify(i18n).message(override, key2, key2);
    verify(i18n).message(override, key3, key3);

    result.assertJson("{key1:'key1',key2:'key2',key3:'key3'}");
  }
}
