package org.aim42.htmlsanitycheck.check

import org.aim42.htmlsanitycheck.Configuration
import org.aim42.htmlsanitycheck.collect.SingleCheckResults
import org.aim42.htmlsanitycheck.html.HtmlConst
import org.aim42.htmlsanitycheck.html.HtmlPage
import spock.lang.Specification

class MissingLocalResourcesCheckerSpec extends Specification {
    Checker missingLocalResourcesChecker
    HtmlPage htmlPage
    SingleCheckResults collector

    private Configuration myConfig

    /* executed before every single spec */

    def setup() {
        myConfig = new Configuration()
        missingLocalResourcesChecker = new MissingLocalResourcesChecker( myConfig)

        collector = new SingleCheckResults()
    }

    /**
     * short relative links like "xx/example" are often used as shorthand for
     * "xx/example.html"
     */

    def "link to existing local file is identified as correct"() {
        given: "link within index.html to local file example.html"

        String linkToLocalFile = """<a href="example.html">aim42</a>"""
        String HTML = """$HtmlConst.HTML_HEAD $linkToLocalFile $HtmlConst.HTML_END"""

        // 1.) create tmp directory d1
        File d1 = File.createTempDir()

        // 2.) create index.html
        File mainFile   = new File(d1, "index.html") << HTML

        // 3.) create local resource file example.html
        File exampleFile = new File(d1, "example.html") << HtmlConst.HTML_HEAD + HtmlConst.HTML_END

        // 4.) configure checker with temp directory
        myConfig.addConfigurationItem( Configuration.ITEM_NAME_sourceDir, d1)
        missingLocalResourcesChecker = new MissingLocalResourcesChecker(myConfig)

        // 5.)
        htmlPage = new HtmlPage( mainFile )
        when: "checks are performed"
        collector = missingLocalResourcesChecker.performCheck(htmlPage)

        then:
        collector.nrOfProblems() == 0


    }


    def "empty page has no errors"() {
        given: "an empty HTML page"
        String HTML = """$HtmlConst.HTML_HEAD $HtmlConst.HTML_END """
        htmlPage = new HtmlPage(HTML)

        when: "page is checked"
        collector = missingLocalResourcesChecker.performCheck(htmlPage)

        then: "no checks are performed"
        collector.nrOfItemsChecked == 0

        and: "no error is found (aka: checkingResult is empty"
        collector.nrOfProblems() == 0
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


