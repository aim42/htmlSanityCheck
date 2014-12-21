package org.aim42.htmlsanitycheck.check

import org.aim42.htmlsanitycheck.html.HtmlConst
import org.aim42.htmlsanitycheck.html.HtmlPage
import spock.lang.Specification

class MissingLocalResourceRefCountSpec extends Specification {

    private File tmpDir
    private File tmpHtmlFile
    private File tmpLocalResource

    // missing resource name
    private static final String mrn = "MissingLocalResource.html"

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
    def "MLR checker has reference counter"( int nrOfFindings,
                                             int nrOfChecks,
                                             String htmlSnippet,
                                             String resultingSuffix) {

        given:

        String html = HtmlConst.HTML_HEAD + htmlSnippet + HtmlConst.HTML_END
        HtmlPage htmlPage = new HtmlPage( html )

        [tmpHtmlFile, tmpDir] = createTempLocalResources("index.html", html, "localResource.html")

        when:
        missingLocalResourceChecker = new MissingLocalResourcesChecker(
                pageToCheck: htmlPage)
        collector = missingLocalResourcesChecker.performCheck()

        then:
        collector.nrOfProblems() == nrOfFindings
        collector.nrOfItemsChecked == nrOfChecks


        where:

        nrOfFindings | nrOfChecks | htmlSnippet |  resultingSuffix
      //  0            | 1          | """<a href="${lrfname}">existing</a>""" |  """"""
        1            | 1          | """<a href="${mrn}">missing</a>"""      |  """local resource ${mrn} missing"""

    }

    /*
    * helper to create local resource
     */
    def List createTempLocalResources( String htmlFileName, String htmlContent, String localResourceName ) {
        // 1.) create tmp directory tmpDir
        File tmpDir = File.createTempDir()

        // 2.) create local resource file localResourceFile
        File localResourceFile = new File(tmpDir, localResourceName) << "someContent"

       assertTrue("newly created artificial file exists", localResourceFile.exists())

        // 3.) create tmp html file "index.html" linking to localResourceFile in directory tmpDir
        File indexFile = new File(tmpDir, htmlFileName ) << htmlContent

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


