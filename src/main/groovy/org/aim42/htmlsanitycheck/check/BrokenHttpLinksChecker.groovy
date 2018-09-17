package org.aim42.htmlsanitycheck.check

import org.aim42.htmlsanitycheck.Configuration
import org.aim42.htmlsanitycheck.collect.Finding
import org.aim42.htmlsanitycheck.collect.SingleCheckResults
import org.aim42.htmlsanitycheck.html.HtmlElement
import org.aim42.htmlsanitycheck.html.HtmlPage
import org.aim42.inet.NetUtil


/**
 * Check html anchor href attributes
 * see https://www.w3schools.com/tags/att_a_href.asp
 *
 */
class BrokenHttpLinksChecker extends Checker {

    // all href attributes with http(s) protocol,
    // including potential duplicates
    // need that to calculate "nrOfOccurrences"
    private List<String> hrefList

    // the pure http/https-hrefs a set, duplicates are removed here
    private Set<HtmlElement> hrefSet


    @Override
    protected void initCheckingResultsDescription() {
        checkingResults.whatIsChecked = "External links Check"
        checkingResults.sourceItemName = "anchor href attribute"
        checkingResults.targetItemName = "broken external link"
    }

    @Override
    protected SingleCheckResults check(final HtmlPage pageToCheck) {

        //get set of all a-tags "<a href=..." in html file,
        // restricted to http(s) links

        hrefSet = pageToCheck.getAllHttpHrefStringsAsSet()

        // if there's no internet, issue a general warning
        // but continue trying
        addWarningIfNoInternetConnection()

        checkAllHttpLinks()

        return checkingResults

    }


    private void addWarningIfNoInternetConnection() {
        if (NetUtil.isInternetConnectionAvailable() == false) {
            checkingResults.generalRemark = "There seems to be a general problem with internet connectivity, all other checks for http/https links might yield incorrect results!"
        }
    }

    /**
     * check all http(s) links
     * TODO: use GPARS to check several links in parallel, as sequential checking might take too long
     **/
    private void checkAllHttpLinks() {
        // for all hrefSet check if the corresponding link is valid
        hrefSet.each { href ->
            checkSingleHttpLink(href)
        }
    }

    /**
     * Check a single external link
     *

     */
    protected void checkSingleHttpLink(String href) {

        // bookkeeping:
        checkingResults.incNrOfChecks()

        try {
            URL url = new URL(href)

            // check if localhost-URL
            checkIfLocalhostURL(url, href)

            // check if (numerical) IP address
            checkIfIPAddress(url, href)

            try {
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("HEAD");

                // httpConnectionTimeout is a configuration parameter
                // that defaults to 5000 (msec)
                connection.setConnectTimeout(
                        Configuration?.getConfigItemByName(Configuration.ITEM_NAME_httpConnectionTimeout)
                );

                // try to connect
                connection.connect();
                int responseCode = connection.getResponseCode();

                // interpret response code
                decideHowToTreatResponseCode(responseCode, href)

            }
            catch (InterruptedIOException | ConnectException | UnknownHostException | IOException exception) {
                Finding someException = new Finding("""exception ${exception.toString()} with href=${href}""")
                checkingResults.addFinding(someException)
            }
        }
        catch (MalformedURLException exception) {
            Finding malformedURLFinding = new Finding("""malformed URL exception with href=${href}""")
            checkingResults.addFinding(malformedURLFinding)
        }
    }

    // if configured, ip addresses in URLs yield warnings
    private void checkIfIPAddress(URL url, String href) {
        if (!Configuration.getConfigItemByName(Configuration.ITEM_NAME_ignoreIPAddresses)) {
            String host = url.getHost()

            if (host.matches("\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}")) {
                Finding localhostWarning = new Finding("""Warning: numerical urls (ip address) indicates suspicious environment dependency: href=${
                    href
                }""")
                checkingResults.addFinding(localhostWarning)
            }
        }
    }

    // if configured ,localhost-URLs yield warnings!
    private void checkIfLocalhostURL(URL url, String href) {
        if (!Configuration.getConfigItemByName(Configuration.ITEM_NAME_ignoreLocalhost)) {
            String host = url.getHost()
            if ((host == "localhost") || host.startsWith("127.0.0")) {
                Finding localhostWarning = new Finding("""Warning: localhost urls indicates suspicious environment dependency: href=${
                    href
                }""")
                checkingResults.addFinding(localhostWarning)
            }
        }
    }

    /**
     * response codes other than 200 might be treated as errors or warnings,
     * sometimes even information.
     *
     * IF a warning or error is found, a @Finding is added to checkingResults
     * TODO: add configuration and logic to "decideHowToTreatResponseCode"
     *
     * @param responseCode
     */
    protected void decideHowToTreatResponseCode(int responseCode, String href) {

        String problem

        Collection<Integer> successCodes = Configuration.getConfigItemByName(Configuration.ITEM_NAME_httpSuccessCodes)
        Collection<Integer> warningCodes = Configuration.getConfigItemByName(Configuration.ITEM_NAME_httpWarningCodes)
        Collection<Integer> errorCodes   = Configuration.getConfigItemByName(Configuration.ITEM_NAME_httpErrorCodes)

        switch (responseCode) {
            case successCodes: return
            case warningCodes: problem = "Warning:"; break
            case errorCodes:   problem = "Error:"; break
            default: problem = "Error: Unknown or unclassified response code:"
        }

        problem += """ ${href} returned statuscode  ${responseCode}."""

        checkingResults.addFinding(new Finding(problem))
        return
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

