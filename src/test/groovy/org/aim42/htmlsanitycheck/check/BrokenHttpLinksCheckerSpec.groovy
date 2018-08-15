package org.aim42.htmlsanitycheck.check

import org.aim42.htmlsanitycheck.collect.SingleCheckResults
import org.aim42.htmlsanitycheck.html.HtmlConst
import org.aim42.htmlsanitycheck.html.HtmlPage
import org.aim42.inet.NetUtil
import spock.lang.Specification
import spock.lang.Unroll


// see end-of-file for license information


class BrokenHttpLinksCheckerSpec extends Specification {

    Checker brokenHttpLinksChecker
    HtmlPage htmlPage
    SingleCheckResults collector

    /** executed once before all specs are executed **/
    def beforeSpec() {

    }

    /* executed before every single spec */

    def setup() {
        brokenHttpLinksChecker = new BrokenHttpLinksChecker()

        collector = new SingleCheckResults()
    }

    /**
     * checking for internet connectivity is a somewhat brittle - as there's no such thing as "the internet"
     * (the checker will most likely use google.com as a proxy for "internet"
     */
    // todo: test that properly
    def "recognize if there is internet connectivity"() {

        expect: "if there is no internet connection, testing should fail"
        NetUtil.isInternetConnectionAvailable() == true

    }

    def "empty page has no errors"() {
        given: "an empty HTML page"
        String HTML = """$HtmlConst.HTML_HEAD $HtmlConst.HTML_END """
        htmlPage = new HtmlPage(HTML)

        when: "page is checked"
        collector = brokenHttpLinksChecker.performCheck(htmlPage)

        then: "no checks are performed"
        collector.nrOfItemsChecked == 0

        and: "no error is found (aka: checkingResult is empty"
        collector.nrOfProblems() == 0

    }


    def "one syntactically correct http URL yields one check"() {
        given: "an HTML page with a single correct anchor/link"
        String HTML = """$HtmlConst.HTML_HEAD 
                <a href="https://google.com">google</a>
                $HtmlConst.HTML_END """

        htmlPage = new HtmlPage(HTML)

        when: "page is checked"
        collector = brokenHttpLinksChecker.performCheck(htmlPage)

        then:
        collector.nrOfItemsChecked == 1

    }

    def "single bad link is identified as problem"() {

        given: "an HTML page with a single (bad) link"
        String badhref = "http://arc42.org/ui98jfuhenu87djch"
        String HTML = """$HtmlConst.HTML_HEAD 
                <a href=${badhref}>nonexisting arc42 link</a>
                $HtmlConst.HTML_END """

        htmlPage = new HtmlPage(HTML)

        when: "page is checked"
        collector = brokenHttpLinksChecker.performCheck(htmlPage)

        then: "then collector contains the appropriate error message"
        collector.findings[0].whatIsTheProblem.contains(badhref)

    }

    @Unroll
    def 'bad link #badLink is identified as problem'() {

        given: "an HTML page with a single (bad) link"
        String badURL = "https://httpstat.us/${badLink}"
        String HTML = """$HtmlConst.HTML_HEAD 
                <a href=${badURL}>${badLink}</a>
                $HtmlConst.HTML_END """

        htmlPage = new HtmlPage(HTML)

        when: "page is checked"
        collector = brokenHttpLinksChecker.performCheck(htmlPage)

        then: "then collector contains the appropriate error message"
        collector.findings[0].whatIsTheProblem.contains("${badLink}")

        collector.findings[0].whatIsTheProblem.contains("Error: ${badURL}")

        where:

        badLink << [400, 401, 403, 404, 405, 406, 408, 409, 410]

    }
    /**
     * guys from OpenRepose (https://github.com/rackerlabs/gradle-linkchecker-plugin/) came up with the
     * cornercase of "localhost" and "127.0.0.1"
     */
    def "urls with localhost leads to errors due to suspicious dependency to environment"() {
        // todo
        given: "HTML page with localhost url"
        String HTML = """$HtmlConst.HTML_HEAD 
                         <a href="http://localhost:9001/">localhost</a>
                         $HtmlConst.HTML_END """

        htmlPage = new HtmlPage(HTML)

        when: "page is checked"
        collector = brokenHttpLinksChecker.performCheck(htmlPage)

        then: "error is thrown"
        collector.nrOfItemsChecked == 1

        and:
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