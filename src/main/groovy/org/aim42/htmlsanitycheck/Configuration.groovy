package org.aim42.htmlsanitycheck

// see end-of-file for license information

/**
 * Handles (and can verify) configuration options.
 *
 * This class needs to be updated if additional configuration options are added.
 *
 * Ideas for additional config options:
 *
 * - verbosity level on console during checks
 *
 * - which HTTP status codes shall result in warnings, and which shall map to errors
 * - list of URLs to exclude from httpLinkchecks
 * - list of hosts to exclude from httpLinkChecks
 */

class Configuration {

    /*****************************************
     * possible configuration items
     *
     * NEVER use any string constants for configuration
     * item names within source code!
     ****************************************/
    final static String ITEM_NAME_sourceDocuments = "sourceDocuments"
    final static String ITEM_NAME_sourceDir = "sourceDir"

    final static String ITEM_NAME_checkingResultsDir = "checkingResultsDir"
    final static String ITEM_NAME_junitResultsDir = "junitResultsDir"

    final static String ITEM_NAME_failOnErrors = "failOnErrors"

    final static String ITEM_NAME_httpConnectionTimeout = "httpConnectionTimeout"

    // currently unused - planned for future enhancements
    final static String ITEM_NAME_httpWarningStatusCodes = "httpWarningStatusCodes"
    final static String ITEM_NAME_httpErrorStatusCodes = "httpErrorStatusCodes"
    final static String ITEM_NAME_urlsToExclude = "urlsToExclude"
    final static String ITEM_NAME_hostsToExclude = "hostsToExclude"

    /***************************
     * private member
     **************************/
    private Map configurationItems = [:]


    // special HtmlSanityChecker methods for mandatory configuration items
    // *******************************************************************

    /**
     *
     */
    void addSourceFileConfiguration() {

    }

    /**
     * @return true if item is already present, false otherwise
     */
    boolean checkIfItemPresent(String itemName ) {
        boolean result = false
       if (configurationItems.get(itemName) == null) {
           result = false
       }
        return result
    }

    /**
     * @return the number of configuration items
     */
    int nrOfConfigurationItems() {
        return configurationItems.size()
    }


    /** add a single configuration item
     *
     * @param itemName
     * @param itemValue
     */
    void addConfigurationItem( String itemName, Object itemValue) {
        configurationItems.put( itemName, itemValue)
    }


    /** retrieve a single configuration item
     *
     * @param itemName
     * @return
     */
    Object getConfigurationItemByName( String itemName ) {
        return configurationItems.get( itemName)
    }



    @Override
    public String toString() {
        return "Configuration{" +
                "configurationItems=" + configurationItems +
                '}';
    }
}
