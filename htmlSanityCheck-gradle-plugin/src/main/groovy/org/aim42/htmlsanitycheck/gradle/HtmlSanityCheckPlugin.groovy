// see end-of-file for license information

package org.aim42.htmlsanitycheck.gradle

import groovy.transform.TypeChecked
import org.gradle.api.Plugin
import org.gradle.api.Project

@TypeChecked
// tag::gradle-plugin-implementation[]
class HtmlSanityCheckPlugin implements Plugin<Project> {

    final static String HTML_SANITY_CHECK = "htmlSanityCheck"

    void apply(Project project) {
        project.tasks.register( HTML_SANITY_CHECK, HtmlSanityCheckTask.class)
    }
}
// end::gradle-plugin-implementation[]


/*========================================================================
 Copyright Gernot Starke and aim42 contributors

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an
 "AS IS" BASIS,WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 ========================================================================*/

