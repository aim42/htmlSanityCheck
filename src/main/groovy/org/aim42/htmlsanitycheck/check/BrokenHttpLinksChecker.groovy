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

    // get the (configured) statusCodes, just syntactic sugar...
    private final Collection<Integer> successCodes
    private final Collection<Integer> warningCodes
    private final Collection<Integer> errorCodes



    BrokenHttpLinksChecker(Configuration pConfig) {
        super(pConfig)

        successCodes = myConfig.getConfigItemByName(Configuration.ITEM_NAME_httpSuccessCodes)
        warningCodes = myConfig.getConfigItemByName(Configuration.ITEM_NAME_httpWarningCodes)
        errorCodes   = myConfig.getConfigItemByName(Configuration.ITEM_NAME_httpErrorCodes)

    }

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
            doubleCheckSingleHttpLink(href)
        }
    }

    /**
     * Double-Check a single http(s) link:
     * Some servers don't accept head request and send errors like 403 or 405,
     * instead of 200.
     * Therefore we double-check: in case of errors or warnings,
     * we try again with a GET, to get the "finalResponseCode" -
     * which we then categorize as success, error or warning
     */


    protected void doubleCheckSingleHttpLink(String href) {

        // to create appropriate error messages
        String problem

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
                        myConfig?.getConfigItemByName(Configuration.ITEM_NAME_httpConnectionTimeout)
                );

                // try to connect
                connection.connect()
                int responseCode = connection.getResponseCode()

                // issue 218 and 219: some webservers respond with 403 or 405
                // when given HEAD requests. Therefore, try GET
                if (responseCode in successCodes) return

                // in case of errors or warnings,
                // try again with GET.

                else {
                    connection.setRequestMethod("GET")
                    int finalResponseCode = connection.getResponseCode()

                    switch (finalResponseCode) {
                        case successCodes: return
                        case warningCodes: problem = "Warning:"; break
                        case errorCodes: problem = "Error:"; break
                        default: problem = "Error: Unknown or unclassified response code:"
                    }

                    problem += """ ${href} returned statuscode ${responseCode}."""

                    checkingResults.addFinding(new Finding(problem))

                } // else

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
        if (!myConfig.getConfigItemByName(Configuration.ITEM_NAME_ignoreIPAddresses)) {
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
        if (!myConfig.getConfigItemByName(Configuration.ITEM_NAME_ignoreLocalhost)) {
            String host = url.getHost()
            if ((host == "localhost") || host.startsWith("127.0.0")) {
                Finding localhostWarning = new Finding("""Warning: localhost urls indicates suspicious environment dependency: href=${
                    href
                }""")
                checkingResults.addFinding(localhostWarning)
            }
        }
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

