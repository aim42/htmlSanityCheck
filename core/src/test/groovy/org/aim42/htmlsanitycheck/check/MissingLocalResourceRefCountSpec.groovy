package org.aim42.htmlsanitycheck.check

import org.aim42.htmlsanitycheck.html.HtmlConst
import org.aim42.htmlsanitycheck.html.HtmlPage
import spock.lang.Specification
import spock.lang.Unroll

class MissingLocalResourceRefCountSpec extends Specification {

    private static final String LOCAL_RESOURCE_NAME = "ExistingLocalResource.html"

    // missing local resource name
    private static final String MIS_LOC_RES = "MissingLocalResource.html"

    private static final String badLocalRef =  """<a href="${MIS_LOC_RES}">missing</a>"""


    private File tmpDir
    private File tmpHtmlFile
    private File tmpLocalResourceFile


    /*
     * data-driven test to specify behavior of MissingLocalResourceChecker reference counter:
     * if a single local resource is referenced multiple times,
     * it will only result in a single line of output of the following form:
     * "local resource <name> missing missing (reference count 5)"
     *
     * @param htmlsnippet
     * @param nrOfChecks
     * @param nrOfFindings
     * @param resultingText
     */

    @Unroll
    def "MLR checker has reference counter"(int nrOfFindings,
                                            int nrOfChecks,
                                            String htmlSnippet,
                                            String result) {

        given:

        String html = HtmlConst.HTML_HEAD + htmlSnippet + HtmlConst.HTML_END
        HtmlPage htmlPage = new HtmlPage(html)

        (tmpHtmlFile, tmpDir, tmpLocalResourceFile) = createTempLocalResources("index.html", html, LOCAL_RESOURCE_NAME)

        when:
        def missingLocalResourcesChecker = new MissingLocalResourcesChecker(
                pageToCheck: htmlPage,
                baseDirPath: tmpDir )
        def collector = missingLocalResourcesChecker.performCheck()


        then:

        // our temporary  files still exists
        tmpHtmlFile.exists()
        tmpLocalResourceFile.exists()


        collector.nrOfProblems() == nrOfFindings
        collector.nrOfItemsChecked == nrOfChecks

        if (nrOfFindings > 0) {
            // we specify only the FIRST message
            collector.findings.first().item == result
        }


        where:

        nrOfFindings | nrOfChecks | htmlSnippet | result
        0 | 1 | """<a href="./${LOCAL_RESOURCE_NAME}">existing</a>"""|  """"""
        1 | 1 | badLocalRef | """local resource ${MIS_LOC_RES} missing"""
        2 | 2 | badLocalRef*2 | """local resource ${MIS_LOC_RES} missing"""

    }

    /*
    * helper to create local resource
     */

    def List createTempLocalResources(String htmlFileName, String htmlContent, String localResourceName) {
        // 1.) create tmp directory tmpDir
        File tmpDir = File.createTempDir()

        // 2.) create local resource file localResourceFile
        File localResourceFile = new File(tmpDir, localResourceName) << "someContent"

        // make sure the just-created (local resource) file really exists
        assert localResourceFile.exists()

        // 3.) create tmp html file "index.html" linking to localResourceFile in directory tmpDir
        File indexFile = new File(tmpDir, htmlFileName) << htmlContent

        assert indexFile.exists()

        return [indexFile, tmpDir, localResourceFile]
    }
}

/************************************************************************
 * This is free software - without ANY guarantee!
 *
 *
 * Copyright Dr. Gernot Starke, arc42.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 *********************************************************************** */


