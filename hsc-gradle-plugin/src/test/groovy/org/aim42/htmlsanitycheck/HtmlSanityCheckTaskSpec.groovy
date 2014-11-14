package org.aim42.htmlsanitycheck
// see end-of-file for license information


import org.gradle.api.file.FileCollection
import org.gradle.api.internal.file.collections.SimpleFileCollection
import spock.lang.Specification


class HtmlSanityCheckTaskSpec extends Specification {

    private final static HTML_HEADER = """<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN"> <head></head><html>"""

    private File tempDir
    private File htmlFile
    private File nonHtmlFile


    def "configuring a single html file is ok"() {

        given:
            tempDir = File.createTempDir()
            htmlFile = new File( tempDir, "a.html") << HTML_HEADER

        when:
            HtmlSanityCheckTask.isValidConfiguration(tempDir, new HashSet(["a.html"]))

        then:
            htmlFile.exists()
            tempDir.isDirectory()
            tempDir.directorySize() > 0
            notThrown( Exception )

    }


    def "configuring a single non-html file is not ok"() {
        given:
            tempDir = File.createTempDir()
            nonHtmlFile = new File( tempDir, "zypern.txt") << "this is no html"

        when:
            HtmlSanityCheckTask.isValidConfiguration(tempDir, new HashSet(["zypern.txt"]))

        then:
            thrown MisconfigurationException

    }

    /*
     * giving no srcFiles and no srcDir is absurd...
     *
     */

    def "configuring no files to check is absurd"(File srcDir, Set<String> srcDocs) {

        when:
        HtmlSanityCheckTask.isValidConfiguration(srcDir, srcDocs)


        then:
        thrown Exception


        where:

        srcDir                        | srcDocs
        null                          | null
        new File("/_non/exis_/d_ir/") | null
        new File("/_non/exis_/d_ir/") | new HashSet<String>()

        // existing but empty directory is absurd too...
        File.createTempDir()          | []

        // existing directory with nonexisting files too...
        File.createTempDir()          | ["non.existing", "blurb.htm"]

    }

    // this spec is a syntactic variation of the data (table-)driven test
    def "No directory and no files make no sense"() {

        when:
        HtmlSanityCheckTask.isValidConfiguration(null, new HashSet<String>() )

        then:
        thrown MisconfigurationException
    }

    def "Nonexisting directory makes no sense"() {
        File nonExistingDir = new File("/_non/existing/directory/")

        when:
        HtmlSanityCheckTask.isValidConfiguration(nonExistingDir, new HashSet<String>())

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

