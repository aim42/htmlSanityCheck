package org.aim42.htmlsanitycheck.check

import org.aim42.htmlsanitycheck.collect.SingleCheckResults
import org.aim42.htmlsanitycheck.html.HtmlConst
import org.aim42.htmlsanitycheck.html.HtmlPage
import spock.lang.Specification
import spock.lang.Unroll

class MissingLocalResourceRefCountSpec extends Specification {

    // missing local resource name
    private static final String MIS_LOC_RES = "MissingLocalResource.html"

    private static final String badLocalRef =  """<a href="${MIS_LOC_RES}">missing</a>"""


    private static final String msgPrefix = MissingLocalResourcesChecker.MLRC_MESSAGE_PREFIX
    private static final String msgMissing = MissingLocalResourcesChecker.MLRC_MESSAGE_MISSING
    private static final String msgRefCount = MissingLocalResourcesChecker.MLRC_REFCOUNT


    private File tmpDir
    private File tmpHtmlFile
    private File tmpLocalResourceFile


    /*
     * data-driven test to specify behavior of MissingLocalResourceChecker reference counter:
     * if a single local resource is referenced multiple times,
     * it will only result in a single line of output of the following form:
     * "local resource <name> missing missing, reference count: 5"
     *
     * @param htmlsnippet
     * @param nrOfChecks
     * @param nrOfFindings
     * @param resultingText
     */

    @Unroll
    def "MLR checker has reference counter"(int nrOfChecks,
                                            int nrOfFindings,
                                            String htmlSnippet,
                                            String result) {

        given:

        String html = HtmlConst.HTML_HEAD + htmlSnippet + HtmlConst.HTML_END

        // create a file
        (tmpHtmlFile, tmpDir) = createTempLocalResources("index.html", html)

        HtmlPage htmlPage = new HtmlPage( tmpHtmlFile )

        when:
        def missingLocalResourcesChecker = new MissingLocalResourcesChecker( baseDirPath: tmpDir )
        SingleCheckResults collector = missingLocalResourcesChecker.performCheck( htmlPage )


        then:

        println "testing with $nrOfChecks, $nrOfFindings, $htmlSnippet, expected: $result "

        // our temporary  files still exists
        tmpHtmlFile.exists()

        // we get the correct nr of checks and findings
        collector.nrOfItemsChecked == nrOfChecks
        collector.nrOfProblems() == nrOfFindings

        // we get the correct finding-message
        collector.findings.first().item == result


        where:

        nrOfChecks | nrOfFindings | htmlSnippet | result
         // one bad local reference:
        1 | 1 | badLocalRef | """${msgPrefix} "${MIS_LOC_RES}" ${msgMissing}"""

        // two bad local references to the same file -> reference count == 2
        1 | 2 | badLocalRef*2 | """${msgPrefix} "${MIS_LOC_RES}" ${msgMissing}${msgRefCount}2"""

        // five bad local references to the same file -> reference count == 5
        1 | 5 | badLocalRef*5 | """${msgPrefix} "${MIS_LOC_RES}" ${msgMissing}${msgRefCount}5"""
    }

    /*
    * helper to create local resource
     */

    def List createTempLocalResources(String htmlFileName, String htmlContent) {
        // 1.) create tmp directory tmpDir
        File tmpDir = File.createTempDir()

        // 2.) create tmp html file "index.html" linking to localResourceFile in directory tmpDir
        File indexFile = new File(tmpDir, htmlFileName) << htmlContent

        assert indexFile.exists()

        return [indexFile, tmpDir]
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


