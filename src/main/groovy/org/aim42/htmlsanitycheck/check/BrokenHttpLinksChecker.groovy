package org.aim42.htmlsanitycheck.check

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

        boolean internetAvailable //= NetUtil.internetConnectionAvailable()



        if (NetUtil.isInternetConnectionAvailable() == false) {
            // TODO: add this as a "general remark" in this checkes' results,
            checkingResults.generalRemark = "There seems to be a general problem with internet connectivity, all other checks for http/https links might yield incorrect results!"
        }

        checkAllExternalLinks()

        return checkingResults

    }


    private void addWarningIfNoInternetConnection() {


    }


    private void checkAllExternalLinks() {
        // for all hrefSet check if the corresponding link is valid
        hrefSet.each { href ->
            //if (URLUtil.isValidURL(href))
            checkSingleHttpLink(href)
        }
    }

    /**
     * Check a single external link
     *
     * TODO: use GPARS to check several links in parallel, as sequential checking might take too long
     *
     */
    protected void checkSingleHttpLink(String href) {

        // bookkeeping:
        checkingResults.incNrOfChecks()

        try {
            URL url = new URL(href)
            try {
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("HEAD");

                // TODO add httpConnectionTimeout parameter!!
                //if (httpConnectionTimeout >= 0) {
                //    connection.setConnectTimeout(httpConnectionTimeout);
                //}
                connection.connect();
                int responseCode = connection.getResponseCode();

                // interpret response code
                // TODO: make this configurable
                decideHowToTreatResponseCode( responseCode, href)

            }
            catch (InterruptedIOException | ConnectException | UnknownHostException exception) {
            // surely an error
            }
            catch (IOException exception) {
            // surely an error
            }
        }
        catch (MalformedURLException exception) {
            Finding malformedURLFinding = new Finding("""malformed URL exception with href=${href}""")
            checkingResults.addFinding(malformedURLFinding)
        }
    }

    /**
     * response codes other than 200 might be treated as errors or warnings,
     * sometimes even information.
     * TODO: add configuration and logic to "decideHowToTreatResponseCode"
     *
     * @param responseCode
     */
    protected void decideHowToTreatResponseCode( int responseCode, String href ) {

        String problem

        if (!responseCode in NetUtil.HTTP_SUCCESS_CODES) {

            switch (responseCode) {
                case (responseCode in NetUtil.HTTP_WARNING_CODES): problem = "Warning:"; break;
                case (responseCode >= 400): problem = "Error:"; break;
                default: problem = "Error: Unknown response code:"
            }

            problem += """ Link ${href} returned code ${responseCode}."""

            checkingResults.addFinding(new Finding(problem))
        }

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

