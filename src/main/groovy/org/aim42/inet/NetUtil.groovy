package org.aim42.inet

// see end-of-file for license information


class NetUtil {

    // these are regarded as "Success" when checking
    // http(s) links
    final static def HTTP_SUCCESS_CODES = (200..208) + [226]

    // for codes in the HTTP_WARNING_CODES, a warning is added to the findings
    final static def HTTP_WARNING_CODES = (100..102) + (300..308)

    /**
     * We try to check if there is a usable Internet connection available.
     * Our approximation is DNS resolution: if google.com can be resolved to an IP address,
     * there should be an active and usable internet connection available.
     *
     * @return true if Internet is (seemingly available
     */
    static boolean isInternetConnectionAvailable() {

        try {
            // if we can get google's address, there is Internet...
            InetAddress.getByName("google.com");
            return true
        } catch (UnknownHostException e) {
            // we cannot resolve google, there might be no internet connection
            return false
        }
    }
}

