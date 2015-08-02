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
package org.sonar.server.issue.ws;

import com.google.common.collect.Lists;
import java.util.Collection;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.CheckForNull;
import org.sonar.api.server.ws.Request;

public enum SearchAdditionalField {

  ACTIONS("actions"),
  ACTION_PLANS("actionPlans"),
  COMMENTS("comments"),
  LANGUAGES("languages"),
  RULES("rules"),
  TRANSITIONS("transitions"),
  USERS("users");

  public static final String ALL_ALIAS = "_all";
  public static final EnumSet<SearchAdditionalField> ALL_ADDITIONAL_FIELDS = EnumSet.allOf(SearchAdditionalField.class);

  private final String label;

  SearchAdditionalField(String label) {
    this.label = label;
  }

  private static final Map<String, SearchAdditionalField> BY_LABELS = new HashMap<>();

  static {
    for (SearchAdditionalField f : values()) {
      BY_LABELS.put(f.label, f);
    }
  }

  @CheckForNull
  public static SearchAdditionalField findByLabel(String label) {
    return BY_LABELS.get(label);
  }

  public static Collection<String> possibleValues() {
    List<String> possibles = Lists.newArrayList(ALL_ALIAS);
    possibles.addAll(BY_LABELS.keySet());
    return possibles;
  }

  public static EnumSet<SearchAdditionalField> getFromRequest(Request request) {
    List<String> labels = request.paramAsStrings(Search2Action.ADDITIONAL_FIELDS);
    if (labels == null) {
      return EnumSet.noneOf(SearchAdditionalField.class);
    }
    EnumSet<SearchAdditionalField> fields = EnumSet.noneOf(SearchAdditionalField.class);
    for (String label : labels) {
      if (label.equals(ALL_ALIAS)) {
        return EnumSet.allOf(SearchAdditionalField.class);
      }
      fields.add(findByLabel(label));
    }
    return fields;
  }
}