package org.aim42.htmlsanitycheck.html

import java.util.regex.Matcher
import java.util.regex.Pattern

/**
 * functions to identify categories of string-representations of URLs and URIs,
 * e.g. isRemote, isCrossReference, isValidIP
 */

class URLUtil {

    // the foundation for the ip_address_pattern:
    // http://stackoverflow.com/questions/5667371/validate-ipv4-address-in-java and
    // http://groovy.codehaus.org/Regular+Expressions
    protected static
    final Pattern ip_address_pattern = ~/(([01]?\d\d?|2[0-4]\d|25[0-5])\.){3}([01]?\d\d?|2[0-4]\d|25[0-5]).*$/

    /**
     * Checks if this String represents a valid URI/URL
     * @param link
     * @return boolean
     */
    public static boolean isValidURL(String link) {
        // TODO: refactor this code to use  org.apache.commons.validator.routines.*

        // TODO: this fails for cross-references - but should not!
        boolean isValid = false

        if (isCrossReference(link)) {
            return true
        } else {
            try {
                URI aUri = new URL(link).toURI()
                isValid = true
            }
            catch (MalformedURLException e) {
                // ignore

            }
            catch (URISyntaxException e1) {
                // ignore too
            }
        }
    }

    /**
     * Checks if this String represents a remote URL
     * (startsWith http, https, ftp, telnet...)
     * @param link
     * */

    public static boolean isRemoteURL(String link) {
        // simple regular expression to match http://, https:// and ftp://

        return (link ==~ (/^(?i)(https?|ftp|telnet|ssh|ssl|gopher|localhost):\/\/.*$/)
                ||

                // special case for mailto-links
                link ==~ (/^(?i)(mailto):.*$/)
                ||

                // special case for URLs starting with a valid IP address
                ip_address_pattern.matcher(link).matches()
        )
    }

    /**
     * Checks if this String represents a local resource, either:
     *   (1) "file://path/filename.ext" or
     *   (2) is a path, e.g. "directory/filename.ext" or directory or
     *   (3) starts with //, e.g. "index.html"
     *
     * @see class URLUtilSpec for details
     */
    public static boolean isLocalResource(String link) {

        // handle corner cases
        if ((link == null)
                || containsInvalidChars( link )
                || (link == "")
                || isCrossReference( link )      // "#link" or similar
                || isRemoteURL( link )           // "mailto:", "http" etc

        )
            return false

        else {
            URI aUri = new URI(link)

            return (
                    (isLinkToFile(aUri)) // (1)
                            ||
                            (link ==~ (/^\/\/.*$/))  // (3)
                            ||
                            (aUri.getPath() != "") // (2)
            )
        }

    }

    /**
     * helper to identify invalid characters in link
     * @param aLink
     */
    public static boolean containsInvalidChars(String aLink) {
        // TODO finding illegal chars with a regex is overly simple,
        // as different chars are allowed in different parts of an URI...
        // simple solution works for htmlSanityCheck


        String illegalCharsRegex = / |!|\*|\$/

        Matcher matcher = (aLink =~ illegalCharsRegex)

        // assert matcher instanceof Matcher

        return matcher.find()
    }

    /*
    ** helper to identify "file scheme"
     */

    private static Boolean isLinkToFile(URI aUri) {

        aUri?.getScheme()?.toUpperCase() == "FILE"
    }

    /**
     * Checks if this String represents a cross-reference,
     * that is an intra-document link
     * @param xref
     */
    public static boolean isCrossReference(String xref) {

        // the simple test is if the xref starts with "#"

        return ( xref.startsWith("#")  && !containsInvalidChars( xref ) )

    }

    /**
     * validate an IP address
     * @see URLUtilSpec for details
     * @param ipa - the candidate ip address
     */
    public static boolean isValidIP(String ipa) {
        return ipa ==~ /^(([01]?\d\d?|2[0-4]\d|25[0-5])\.){3}([01]?\d\d?|2[0-4]\d|25[0-5])$/
    }
}

/************************************************************************
 * This is free software - without ANY guarantee!
 *
 *
 * Copyright 2013, Dr. Gernot Starke, arc42.org
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

