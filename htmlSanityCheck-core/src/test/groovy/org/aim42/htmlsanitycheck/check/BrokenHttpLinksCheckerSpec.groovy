package org.aim42.htmlsanitycheck.check

import org.aim42.htmlsanitycheck.Configuration
import org.aim42.htmlsanitycheck.collect.SingleCheckResults
import org.aim42.htmlsanitycheck.html.HtmlConst
import org.aim42.htmlsanitycheck.html.HtmlPage
import org.aim42.htmlsanitycheck.test.dns.CustomHostNameResolver
import org.wiremock.integrations.testcontainers.WireMockContainer
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

import java.lang.reflect.Field
import java.lang.reflect.Proxy
import java.util.regex.Pattern

// see end-of-file for license information


class BrokenHttpLinksCheckerSpec extends Specification {

    Checker brokenHttpLinksChecker
    HtmlPage htmlPage
    SingleCheckResults collector

    private Configuration myConfig
    static private int port

    @Shared
    WireMockContainer wireMockServer = new WireMockContainer("wiremock/wiremock:3.9.1-1")
            .withMappingFromResource("mappings.json")
            .withExposedPorts(8080)

    @Shared
    CustomHostNameResolver customHostNameResolver = new CustomHostNameResolver()

    /** executed once before all specs are executed **/
    def setupSpec() {
        wireMockServer.start()
        port = wireMockServer.getMappedPort(8080)
        registerCustomDnsResolver()
    }

    /* executed before every single spec */
    def setup() {
        myConfig = new Configuration()
        brokenHttpLinksChecker = new BrokenHttpLinksChecker( myConfig )

        collector = new SingleCheckResults()
    }


    /** executed once after all specs are executed **/
    def cleanupSpec() {
        wireMockServer.stop()
    }


    // Custom method to register the DNS resolver
    private void registerCustomDnsResolver() {
        try {
            Field implField = InetAddress.class.getDeclaredField("impl")
            implField.setAccessible(true)
            Object currentImpl = implField.get(null)

            Proxy newImpl = (Proxy) Proxy.newProxyInstance(
                    currentImpl.getClass().getClassLoader(),
                    currentImpl.getClass().getInterfaces(),
                    (proxy, method, args) -> {
                        if ("lookupAllHostAddr".equals(method.getName()) && args.length == 1 && args[0] instanceof String) {
                            return customHostNameResolver.resolve((String) args[0])
                        }
                        return method.invoke(currentImpl, args)
                    }
            )

            implField.set(null, newImpl)
        } catch (Exception e) {
            throw new RuntimeException("Failed to register custom DNS resolver", e)
        }
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


    def "one syntactically correct http URL is ok"() {
        given: "an HTML page with a single correct anchor/link"
        String HTML = """$HtmlConst.HTML_HEAD 
                <a href="http://google.com:$port">google</a>
                $HtmlConst.HTML_END """

        htmlPage = new HtmlPage(HTML)

        when: "page is checked"
        collector = brokenHttpLinksChecker.performCheck(htmlPage)

        then: "a single item is checked"
        collector.nrOfItemsChecked == 1

        and: "the result is ok"
        collector.nrOfProblems() == 0

    }


    def "regression for issue 272"(String goodUrl) {
        given: "an HTML page with a single correct anchor/link"
        String HTML = """$HtmlConst.HTML_HEAD 
                <a href="${goodUrl}">url that lead to unknown host</a>
                $HtmlConst.HTML_END """

        htmlPage = new HtmlPage(HTML)

        when: "Douglas Cayers url is checked"
        collector = brokenHttpLinksChecker.performCheck(htmlPage)

        then: "a single item is checked"
        collector.nrOfItemsChecked == 1

        and: "the result is ok"
        collector.nrOfProblems() == 0

        where:
           goodUrl << ["http://junit.org:$port/junit4",
                       "http://plumelib.org:$port/plume-util",
                       "http://people.csail.mit.edu:$port/cpacheco"
           ]
    }


    def "single bad link is identified as problem"() {

        given: "an HTML page with a single (bad) link"
        String badhref = "http://arc42.org:$port/ui98jfuhenu87djch"
        String HTML = """$HtmlConst.HTML_HEAD 
                <a href="${badhref}">nonexisting arc42 link</a>
                $HtmlConst.HTML_END """

        htmlPage = new HtmlPage(HTML)

        when: "page is checked"
        collector = brokenHttpLinksChecker.performCheck(htmlPage)

        then: "then collector contains the appropriate error message"
        collector.findings[0].whatIsTheProblem.contains(badhref)

    }

    /**
     * regression for weird behavior of certain Amazon.com links,
     * where HEAD requests are always answered with 405 instead of 200...
     */


    def "amazon does not deliver 405 statuscode for links that really exist"() {
        given: "an HTML page with a single (good) amazon link"
        String goodAmazonLink = "http://www.amazon.com:$port/dp/B01A2QL9SS"
        String HTML = """$HtmlConst.HTML_HEAD 
                <a href="${goodAmazonLink}">Amazon</a>
                $HtmlConst.HTML_END """

        htmlPage = new HtmlPage(HTML)

        when: "page is checked"
        collector = brokenHttpLinksChecker.performCheck(htmlPage)

        then: "a single item is checked"
        collector.nrOfItemsChecked == 1

        and: "the result is ok"
        collector.nrOfProblems() == 0

    }


    def "bad amazon link is identified as problem"() {

        given: "an HTML page with a single (good) amazon link"
        String badAmazonLink = "https://www.amazon.com:$port/dp/4242424242"
        String HTML = """$HtmlConst.HTML_HEAD 
                <a href="${badAmazonLink}">Amazon</a>
                $HtmlConst.HTML_END """

        htmlPage = new HtmlPage(HTML)

        when: "page is checked"
        collector = brokenHttpLinksChecker.performCheck(htmlPage)

        then: "then collector contains the appropriate error message"
        collector.findings[0].whatIsTheProblem.contains(badAmazonLink)

    }


    @Unroll
    def 'bad link #badLink is recognized as such'() {

        given: "an HTML page with a single (broken) link"
        String goodURL = "http://mock.codes$port/${badLink}"
        String HTML = """$HtmlConst.HTML_HEAD 
                <a href="${goodURL}">${badLink}</a>
                $HtmlConst.HTML_END """

        htmlPage = new HtmlPage(HTML)

        when: "page is checked"
        collector = brokenHttpLinksChecker.performCheck(htmlPage)

        then: "then collector contains one error message"
        collector.getFindings().size() == 1

        where:

        badLink << [400, 401, 403, 404, 405, 406, 408, 409, 410, 429, 431, 500, 501, 502, 504, 505, 506, 507]

    }


    def 'redirects are recognized and their new location is contained in warning message'() {

        given: "the old arc42 (http!) page "
        String HTML = """$HtmlConst.HTML_HEAD 
                <a href="http://arc42.de:$port/old"></a>
                $HtmlConst.HTML_END """

        htmlPage = new HtmlPage(HTML)

        when: "page is checked"
        collector = brokenHttpLinksChecker.performCheck(htmlPage)

        then: "then collector contains one error message"
        collector.getFindings().size() == 1

        collector?.getFindings()?.first()?.whatIsTheProblem?.contains("https://arc42.de")

    }

    /**
     * guys from OpenRepose (https://github.com/rackerlabs/gradle-linkchecker-plugin/) came up with the
     * corner-case of "localhost" and "127.0.0.1"
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

        then: "warning is given"

        collector?.getFindings()?.first()?.whatIsTheProblem?.contains("Warning")
        collector?.getFindings()?.first()?.whatIsTheProblem?.contains("suspicious")

    }


    def 'excludes are not checked'() {
        given: "HTML page with excludes"
        String HTML = """$HtmlConst.HTML_HEAD
                         <a href="http://exclude-this-host.com:8080">Excluded HOST</a>
                         <a href="https://exclude-this-host.com">Excluded HOST</a>
                         <a href="https://exclude-this-host.org:9090">Excluded HOST</a>
                         <a href="https://exclude-also-this-host.org">Excluded HOST</a>

                         <a href="http://exclude-this-url.com/page">Excluded URL</a>
                         <a href="http://exclude-this-url.org:8443/page">Excluded URL</a>
                         <a href="http://exclude-also-this-url.com/page">Excluded URL</a>
                         <a href="http://exclude-also-this-url.com:7070/page">Excluded URL</a>
                         $HtmlConst.HTML_END """

        htmlPage = new HtmlPage(HTML)
        Set<Pattern> excludes = [Pattern.compile("(http|https)://exclude-this-(host|url).*(:\\d+)?(/.+)?"),
                                 Pattern.compile("(http|https)://exclude-also-this-(host|url).(com|org)(:\\d+)?(/.+)?")]
        Configuration config = Configuration.builder().excludes(excludes).build()
        BrokenHttpLinksChecker brokenHttpLinksChecker = new BrokenHttpLinksChecker(config)

        when: "page is checked"
        collector = brokenHttpLinksChecker.performCheck(htmlPage)

        then: "no findings are reported"
        collector.getFindings().isEmpty()
    }

    def 'check same URLs only once'() {
        given: "HTML pages with same targets"
        String goodAmazonLink = "http://www.amazon.com:$port/dp/B01A2QL9SS"
        String HTML = """$HtmlConst.HTML_HEAD 
                <a href="${goodAmazonLink}">Amazon</a>
                $HtmlConst.HTML_END """

        htmlPage = new HtmlPage(HTML)

        when: "page (including URL) is checked"
        brokenHttpLinksChecker.performCheck(htmlPage)

        and: "page (including URL) is checked again"
        SingleCheckResults collector = brokenHttpLinksChecker.performCheck(htmlPage)

        then: "a single item is checked"
        collector.nrOfItemsChecked == 1

        and: "the result is still ok (but coverage should be increased)"
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