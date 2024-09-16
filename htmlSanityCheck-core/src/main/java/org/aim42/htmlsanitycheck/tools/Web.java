package org.aim42.htmlsanitycheck.tools;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.validator.routines.InetAddressValidator;

import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

@Slf4j
public class Web {

    private Web() {
        // Intentionally left empty
    }

    /**
     * Functions to identify categories of string-representations of URLs and URIs,
     * e.g., isRemote, isCrossReference, isValidIP
     */

    // these are regarded as "Success" when checking
    // http(s) links
    public static final Set<Integer> HTTP_SUCCESS_CODES = Collections.unmodifiableSet(new HashSet<>(Arrays.asList(
            // tag::HTTP_SUCCESS_CODES[]
            200, 201, 202, 203, 204,
            205, 206, 207, 208, 226
            // end::HTTP_SUCCESS_CODES[]
    )));

    public static final Set<Integer> HTTP_REDIRECT_CODES = Collections.unmodifiableSet(new HashSet<>(Arrays.asList(
            // tag::HTTP_REDIRECT_CODES[]
            301, 302, 303, 307, 308
            // end::HTTP_REDIRECT_CODES[]
    )));

    private static Set<Integer> extendedByRedirects(Set<Integer> first) {
        Set<Integer> result = new HashSet<>(first);
        result.addAll(Web.HTTP_REDIRECT_CODES);

        return Collections.unmodifiableSet(result);
    }

    @SuppressWarnings("squid:S2386") // The Set is computed
    public static final Set<Integer> HTTP_WARNING_CODES = extendedByRedirects(new HashSet<>(Arrays.asList(
            // tag::HTTP_WARNING_CODES[]
            100, 101, 102
            // end::HTTP_WARNING_CODES[]
    )));

    public static final Set<Integer> HTTP_ERROR_CODES = Collections.unmodifiableSet(new HashSet<>(Arrays.asList(
            // tag::HTTP_ERROR_CODES[]
            400, 401, 402, 403, 404,
            405, 406, 407, 408, 409,
            410, 411, 412, 413, 414,
            415, 416, 417, 418, 421,
            422, 423, 424, 425, 426,
            428, 429, 431, 451,
            500, 501, 502, 503, 504,
            505, 506, 507, 508, 510,
            511
            // end::HTTP_ERROR_CODES[]
    )));

    @SuppressWarnings("squid:S2386") // The Set is computed
    public static final Set<String> POSSIBLE_EXTENSIONS = initExtensions();

    private static final Pattern httpPattern = Pattern.compile("^https?:");

    private static final Pattern mailPattern = Pattern.compile("^(?i)(mailto):.*$");

    private static final Pattern dataImagePattern = Pattern.compile("^(?i)(data:image).*$");

    private static final Pattern remoteImagePattern = Pattern.compile("^(?i)(https?|ftp|telnet|ssh|ssl|gopher|localhost)://.*");

    private static final Pattern linkPattern = Pattern.compile("^//.*$");

    private static Set<String> initExtensions() {
        Set<String> result = new HashSet<>(8);
        result.add("html");
        result.add("htm");
        result.add("shtml");
        result.add("phtml");
        result.add("php");
        result.add("asp");
        result.add("aspx");
        result.add("xml");
        return result;
    }


    /**
     * We try to check if there is a usable Internet connection available.
     * Our approximation is DNS resolution: if google.com can be resolved to an IP address,
     * there should be an active and usable internet connection available.
     *
     * @return true if the Internet is seemingly available
     */
    public static boolean isInternetConnectionAvailable() {
        return isInternetConnectionAvailable("google.com");
    }

    protected static boolean isInternetConnectionAvailable(String host) {
        try {
            // if we can get google's address, there is the Internet...
            //noinspection ResultOfMethodCallIgnored
            InetAddress.getByName(host);
            return true;
        } catch (UnknownHostException e) {
            // we cannot resolve Google, there might be no internet connection
            return false;
        }
    }

    public static boolean isWebUrl(String possibleUrl) {
        return httpPattern.matcher(possibleUrl).find();
    }

    public static boolean isIP(String ip) {
        return InetAddressValidator.getInstance().isValid(ip);
    }

    public static boolean startsWithIP(String url) {
        int slashPos = url.indexOf('/');
        if (slashPos > 0) {
            String host = url.substring(0, slashPos);
            return isIP(host);
        }
        return false;
    }

    public static boolean isSuccessCode(int responseCode) {
        return HTTP_SUCCESS_CODES.contains(responseCode);
    }

    public static boolean isWarningCode(int responseCode) {
        return HTTP_WARNING_CODES.contains(responseCode);
    }

    public static boolean isErrorCode(int responseCode) {
        return HTTP_ERROR_CODES.contains(responseCode);
    }


    /**
     * Checks if this String represents a remote URL
     * (startsWith http, https, ftp, telnet...)
     *
     * @param imgSrc the image URL to be checked
     */
    public static boolean isRemoteURL(String imgSrc) {
        return remoteImagePattern.matcher(imgSrc).find()
                || mailPattern.matcher(imgSrc).find()
                || isIP(imgSrc)
                || startsWithIP(imgSrc);
    }

    public static boolean isVirtualURL(String resource) {
        return resource.startsWith("javascript:");
    }

    /**
     * Checks if this String represents a data-image-URI
     * (startsWith "data:image"
     *
     * @param imgSrc the image URL to be checked
     */
    public static boolean isDataURI(String imgSrc) {
        return dataImagePattern.matcher(imgSrc).find();
    }


    /**
     * Checks if this String represents a cross-reference,
     * that is an intra-document link
     *
     * @param xref the cross-reference to be checked
     */
    public static boolean isCrossReference(String xref) {

        // the simple test is if the xref starts with "#"

        return (xref.startsWith("#") && !containsInvalidChars(xref));

    }

    private static final Pattern ILLEGAL_CHARS_REGEX = Pattern.compile("[ *$<>]");

    /**
     * helper to identify invalid characters in link
     *
     * @param aLink the link to be checked
     */
    public static boolean containsInvalidChars(String aLink) {
        // TODO finding illegal chars with a regex is overly simple,
        // as different chars are allowed in different parts of an URI...
        // simple solution works for htmlSanityCheck
        return ILLEGAL_CHARS_REGEX.matcher(aLink).find();
    }

    /**
     * Checks if this String represents a local resource, either:
     * (1) "file://path/filename.ext" or
     * (2) is a path, e.g. "directory/filename.ext" or directory or
     * (3) starts with //, e.g. "index.html"
     */
    public static boolean isLocalResource(String link) {
        // handle corner cases
        if ((link == null)
                || containsInvalidChars(link)
                || (link.isEmpty())
                || isCrossReference(link)      // "#link" or similar
                || isRemoteURL(link)           // "mailto:", "http" etc
                || isVirtualURL(link)          // "javascript:" ...
        ) {
            return false;
        } else {
            URI aUri;
            try {
                log.trace("Trying to resolve URI for '{}'", link);
                aUri = new URI(link);
            } catch (URISyntaxException e) {
                throw new InvalidUriSyntaxException(e);
            }

            if (null == aUri.getPath()) {
                log.warn("'{}' does not have a valid (URI) path", link);
                return false;
            }

            return (
                    (isLinkToFile(aUri)) // (1)
                            ||
                            linkPattern.matcher(link).find()  // (3)
                            ||
                            (!aUri.getPath().isEmpty()) // (2)
            );
        }
    }

    /**
     * helper to identify "file scheme"
     */
    private static Boolean isLinkToFile(URI aUri) {
        if (aUri == null || aUri.getScheme() == null) {
            return false;
        }

        return aUri.getScheme().equalsIgnoreCase("file");
    }
}


/*************************************************************************
 * This is free software - without ANY guarantee!
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