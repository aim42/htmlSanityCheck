package org.aim42.htmlsanitycheck.check;

import lombok.extern.slf4j.Slf4j;
import org.aim42.htmlsanitycheck.Configuration;
import org.aim42.htmlsanitycheck.collect.Finding;
import org.aim42.htmlsanitycheck.collect.SingleCheckResults;
import org.aim42.htmlsanitycheck.html.HtmlPage;
import org.aim42.htmlsanitycheck.tools.Web;
import org.aim42.net.TrustAllCertificates;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;


/**
 * Check html anchor href attributes
 *
 * @see <a href="https://www.w3schools.com/tags/att_a_href.asp">https://www.w3schools.com/tags/att_a_href.asp</a>
 */
@Slf4j
class BrokenHttpLinksChecker extends Checker {
    static {
        TrustAllCertificates.install();
    }

    // get the (configured) statusCodes, just syntactic sugar...
    private final Set<Integer> successCodes;
    private final Set<Integer> warningCodes;
    private final Set<Integer> errorCodes;
    // all href attributes with http(s) protocol,
    // including potential duplicates
    // need that to calculate "nrOfOccurrences"
    // the pure http/https-hrefs a set, duplicates are removed here
    private Set<String> hrefSet;
    private Set<Pattern> excludePatterns;


    BrokenHttpLinksChecker(Configuration pConfig) {
        super(pConfig);

        errorCodes = getMyConfig().getHttpErrorCodes();
        warningCodes = getMyConfig().getHttpWarningCodes();
        successCodes = getMyConfig().getHttpSuccessCodes();
        Set<String> exclude = getMyConfig().getExclude();
        setExclude(exclude);
    }

    @Override
    protected void initCheckingResultsDescription() {
        getCheckingResults().setWhatIsChecked("External Links Check");
        getCheckingResults().setSourceItemName("anchor href attribute");
        getCheckingResults().setTargetItemName("broken external link");

    }

    @Override
    protected SingleCheckResults check(final HtmlPage pageToCheck) {
        log.trace("Checking '{}'", pageToCheck.getFile());

        //get set of all a-tags "<a href=..." in html file,
        // restricted to http(s) links

        hrefSet = pageToCheck.getAllHttpHrefStringsAsSet();

        // if there's no internet, issue a general warning
        // but continue trying
        addWarningIfNoInternetConnection();

        checkAllHttpLinks();

        return getCheckingResults();

    }


    private void addWarningIfNoInternetConnection() {
        if (!Web.isInternetConnectionAvailable()) {
            getCheckingResults().setGeneralRemark("There seems to be a general problem with internet connectivity, all other checks for http/https links might yield incorrect results!");
        }
    }

    /**
      * check all http(s) links
      * TODO: use GPARS to check several links in parallel, as sequential checking might take too long
      **/
    private void checkAllHttpLinks() {
        // for all hrefSet check if the corresponding link is valid
        hrefSet.forEach(this::doubleCheckSingleHttpLink);
    }

    /**
      * Double-Check a single http(s) link:
      * Some servers don't accept head request and send errors like 403 or 405,
      * instead of 200.
      * Therefore, we double-check: in case of errors or warnings,
      * we try again with a GET, to get the "finalResponseCode" -
      * which we then categorize as success, error or warning
      */


    protected void doubleCheckSingleHttpLink(String href) {
        // Check if the href matches any of the regular expressions in the exclude set
        if (excludePatterns != null) {
            for (Pattern pattern : excludePatterns) {
                if (pattern.matcher(href).matches()) {
                    // Skip checking this URL
                    return;
                }
            }
        }
        // bookkeeping:
        getCheckingResults().incNrOfChecks();

        try {
            URL url = new URL(href);
            checkIfLocalhostURL(url, href);
            checkIfIPAddress(url, href);
            checkHttpLinkWithRetry(url, href);
        } catch (MalformedURLException exception) {
            Finding malformedURLFinding = new Finding("malformed URL exception with href=" + href);
            getCheckingResults().addFinding(malformedURLFinding);
        }
    }

    private void checkHttpLinkWithRetry(URL url, String href) {
        String problem;
        try {
            HttpURLConnection firstConnection = getNewURLConnection(url);

            // try to connect
            firstConnection.connect();
            int responseCode = firstConnection.getResponseCode();

            // issue 218 and 219: some web servers respond with 403 or 405
            // when given HEAD requests. Therefore, try to GET
            if (successCodes.contains(responseCode)) {
                return;
            }
            // issue 244: special case for redirects
            // thanks to https://stackoverflow.com/questions/39718059/read-from-url-in-groovy-with-redirect
            else if (Web.HTTP_REDIRECT_CODES.contains(responseCode)) {
                String newLocation;
                if (firstConnection.getHeaderField("Location") != null) {
                    newLocation = firstConnection.getHeaderField("Location");

                    problem = String.format("Warning: %s returned statuscode %d, new location: %s", href, responseCode, newLocation);
                    getCheckingResults().addFinding(new Finding(problem));

                }
            }
            // in case of errors or warnings,
            // try again with GET.
            else {
                HttpURLConnection secondConnection = getNewURLConnection(url);
                secondConnection.setRequestMethod("GET");
                int finalResponseCode = secondConnection.getResponseCode();
                secondConnection.disconnect();

                if (successCodes.contains(finalResponseCode)) {
                    return;
                } else if (warningCodes.contains(finalResponseCode)) {
                    problem = "Warning:";
                } else if (errorCodes.contains(finalResponseCode)) {
                    problem = "Error:";
                } else {
                    problem = "Error: Unknown or unclassified response code:";
                }

                problem += String.format(" %s returned statuscode %d.", href, responseCode);

                getCheckingResults().addFinding(new Finding(problem));

            } // else

            // cleanup firstConnection
            firstConnection.disconnect();

        } catch (UnknownHostException exception) {
            Finding unknownHostFinding = new Finding("Unknown host with href=" + href);
            getCheckingResults().addFinding(unknownHostFinding);
        } catch (IOException exception) {
            Finding someException = new Finding("exception " + exception + " with href=" + href);
            getCheckingResults().addFinding(someException);
        }
    }


    private HttpURLConnection getNewURLConnection(URL url) throws IOException {

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("HEAD");

        // httpConnectionTimeout is a configuration parameter
        // that defaults to 5000 (msec)
        connection.setConnectTimeout(
                getMyConfig().getHttpConnectionTimeout()
        );

        // to avoid nasty 403 errors (forbidden), we set a referrer and user-agent
        //
        connection.setRequestProperty("Referer", "https://aim42.org");
        connection.setRequestProperty("User-Agent", "Mozilla/5.0 (X11; Linux i686; rv:10.0) Gecko/20100101 Firefox/10.0");

        // TODO followRedirects should be a configuration parameter
        // that defaults to false

        return connection;
    }

    // if configured, ip addresses in URLs yield warnings
    private void checkIfIPAddress(URL url, String href) {
        if (!getMyConfig().isIgnoreIPAddresses()) {
            String host = url.getHost();

            if (host.matches("\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}")) {
                Finding localhostWarning = new Finding("Warning: numerical urls (ip address) indicates suspicious environment dependency: href=" + href);
                getCheckingResults().addFinding(localhostWarning);
            }
        }
    }

    // if configured ,localhost-URLs yield warnings!
    private void checkIfLocalhostURL(URL url, String href) {
        if (!getMyConfig().isIgnoreLocalhost()) {
            String host = url.getHost();
            if (("localhost".equals(host)) || host.startsWith("127.0.0")) {
                Finding localhostWarning = new Finding("Warning: localhost urls indicates suspicious environment dependency: href=" + href);
                getCheckingResults().addFinding(localhostWarning);
            }
        }
    }


    public void setExclude(Set<String> exclude) {
        // Create patterns from exclude
        excludePatterns = new HashSet<>();
        if (exclude != null) {
            for (String url : exclude) {
                excludePatterns.add(Pattern.compile(url));
            }
        }
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

