package org.aim42.htmlsanitycheck
// see end-of-file for license information
import spock.lang.Specification
import spock.lang.Unroll

class HtmlSanityCheckTaskSpec extends Specification {

    private final static HTML_HEADER = """<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN"> <head></head><html>"""

    private File tempDir
    private File htmlFile
    private File nonHtmlFile



    def "configuring a single html file is ok"() {

        given:
            tempDir = File.createTempDir()
            htmlFile = new File( tempDir, "a.html") << HTML_HEADER

        when: "we create a configuration with this single file"
            Configuration.addSourceFileConfiguration(tempDir, new HashSet(["a.html"]))

            Configuration.isValid( )

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
            Configuration.addSourceFileConfiguration(tempDir, new HashSet(["zypern.txt"]))

        when:
            Configuration.isValid()

        then:
            thrown MisconfigurationException

    }

    /*
     * giving no srcFiles and no srcDir is absurd...
     *
     */

    @Unroll
    def "configuring file #srcDocs in directory #srcDocs is absurd"() {

        given: "configuration with #srcDir and #srcDocs"
        Configuration.addSourceFileConfiguration( srcDir, srcDocs)

        when: "configuration is validated..."
        def tmpResult = Configuration.isValid()

        then: "an exception is thrown"
        thrown MisconfigurationException


        where:

        srcDir                        | srcDocs
        null                          | null
        new File("/_non/exis_/d_ir/") | null
        new File("/_non/exis_/d_ir/") | new HashSet<String>()

        // existing but empty directory is absurd too...
        File.createTempDir()          | null

        // existing directory with nonexisting files too...
        File.createTempDir()          | ["non.existing", "blurb.htm"].toSet()

    }

    // this spec is a syntactic variation of the data (table-)driven test
    def "empty configuration makes no sense"() {

        when:
        Configuration.isValid()

        then:
        thrown MisconfigurationException
    }


}

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

