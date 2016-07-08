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
package org.sonar.core.util.stream;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class CollectorsTest {
  @Test
  public void toList_builds_an_ArrayList() {
    List<Integer> res = Arrays.asList(1, 2, 3, 4, 5).stream().collect(Collectors.toList());
    assertThat(res).isInstanceOf(ArrayList.class)
      .containsExactly(1, 2, 3, 4, 5);
  }
  @Test
  public void toList_with_size_builds_an_ArrayList() {
    List<Integer> res = Arrays.asList(1, 2, 3, 4, 5).stream().collect(Collectors.toList(30));
    assertThat(res).isInstanceOf(ArrayList.class)
      .containsExactly(1, 2, 3, 4, 5);
  }

  @Test
  public void toSet_builds_an_HashSet() {
    Set<Integer> res = Arrays.asList(1, 2, 3, 4, 5).stream().collect(Collectors.toSet());
    assertThat(res).isInstanceOf(HashSet.class)
      .containsExactly(1, 2, 3, 4, 5);
  }
  @Test
  public void toSet_with_size_builds_an_ArrayList() {
    Set<Integer> res = Arrays.asList(1, 2, 3, 4, 5).stream().collect(Collectors.toSet(30));
    assertThat(res).isInstanceOf(HashSet.class)
      .containsExactly(1, 2, 3, 4, 5);
  }
}