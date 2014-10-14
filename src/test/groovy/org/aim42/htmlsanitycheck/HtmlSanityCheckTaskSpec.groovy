package org.aim42.htmlsanitycheck
// see end-of-file for license information


import org.gradle.api.file.FileCollection
import org.gradle.api.internal.file.collections.SimpleFileCollection
import spock.lang.Specification
import spock.lang.Unroll


class HtmlSanityCheckTaskSpec extends Specification {

    /*
     * giving no srcFiles and no srcDir is absurd...
     *
     */

    def "configuring no files to check is absurd"(File srcDir, FileCollection srcDocs) {

        when:
        HtmlSanityCheckTask.validateInputs(srcDir, srcDocs)


        then:
        thrown IllegalArgumentException

        where:

        srcDir                                | srcDocs
        null                                  | null
        new File("/_non/existing/directory/") | null

        new File("/_non/existing/directory/") | new SimpleFileCollection()

        // existing but empty directory is absurd too...
        File.createTempDir() | new SimpleFileCollection()

        // existing directory with nonexisting files too...
        File.createTempDir() | new SimpleFileCollection(
                new File("non-existing", "blurb"))
    }

    // this spec is a syntactic variation of the data (table-)driven test
    def "No directory and no files make no sense"() {
        File noFile // deliberately left null...

        when:
        HtmlSanityCheckTask.validateInputs(noFile, new SimpleFileCollection())

        then:
        thrown(IllegalArgumentException)
    }

    def "Nonexisting directory makes no sense"() {
        File nonExistingDir = new File("/_non/existing/directory/")

        when:
        HtmlSanityCheckTask.validateInputs(nonExistingDir, new SimpleFileCollection())

        then:
        thrown IllegalArgumentException

    }


}

/*========================================================================
 Copyright 2014 Gernot Starke and aim42 contributors

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

