/*
 * Copyright 2023 the original author or authors.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * https://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.openrewrite.table;

import org.junit.jupiter.api.Test;
import org.openrewrite.DocumentExample;
import org.openrewrite.test.RewriteTest;

import java.io.ByteArrayInputStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.openrewrite.test.SourceSpecs.text;

class RepositoryResultsTest implements RewriteTest {

    @DocumentExample
    @Test
    void hierarchical() {
        rewriteRun(
          spec -> spec
            .recipe(
              new ByteArrayInputStream(
                //language=yml
                """
                  type: specs.openrewrite.org/v1beta/recipe
                  name: test.ChangeTextToHello
                  displayName: Change text to hello
                  description: Hello world.
                  recipeList:
                      - org.openrewrite.text.FindAndReplace:
                          find: Hi
                          replace: Hello!
                      - org.openrewrite.text.FindAndReplace:
                          find: Hey
                          replace: '"Hello!"'
                  """.getBytes()
              ),
              "test.ChangeTextToHello"
            ).dataTable(RepositoryResults.Row.class, rows -> {
                assertThat(rows).hasSize(2);
                assertThat(rows.stream().map(RepositoryResults.Row::getOccurrences))
                  .containsExactly(1, 1);
                assertThat(rows.stream().map(RepositoryResults.Row::getRecipe))
                  .containsExactly("org.openrewrite.text.FindAndReplace", "org.openrewrite.text.FindAndReplace");
                assertThat(rows.stream().map(RepositoryResults.Row::getRecipeOptions))
                  .containsExactly("[find=\"Hi\", replace=\"Hello!\"]", "[find=\"Hey\", replace=`\"Hello!\"`]");
            }),
          text(
            "Hi",
            "Hello!"
          ),
          text(
            "Hey",
            "\"Hello!\""
          )
        );
    }
}