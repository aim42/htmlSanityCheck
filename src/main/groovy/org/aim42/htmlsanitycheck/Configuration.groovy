package org.aim42.htmlsanitycheck

import org.aim42.filesystem.FileCollector
import org.aim42.inet.NetUtil

// see end-of-file for license information

/**
 * Handles (and can verify) configuration options.
 *
 * Implemented as REGISTRY pattern
 *
 *
 * Explanation for configuring http status codes:
 * The standard http status codes are defined in class @link NetUtil and can
 * be overwritten by configuration:
 *
 * Example: You want 503 to be ok instead of error:
 * httpSuccessCodes = [503]
 *
 * During configuration initialization, the value(s) of httpSuccessCodes will be:
 * 1.) set-added to httpSuccessCodes,
 * 2.) set-subtracted from the warnings and errors.
 *
 *
 * This class needs to be updated if additional configuration options are added.
 *
 *
 * Ideas for additional config options:
 * ------------------------------------
 * - verbosity level on console during checks
 *
 *
 */

class Configuration {

    /*****************************************
     * configuration item names
     *
     * NEVER use any string constants for configuration
     * item names within source code!
     ****************************************/

    // sourceDocuments is a collection of Strings, maybe only a single String
    final static String ITEM_NAME_sourceDocuments = "sourceDocuments"
    final static String ITEM_NAME_sourceDir = "sourceDir"

    final static String ITEM_NAME_checkingResultsDir = "checkingResultsDir"
    final static String ITEM_NAME_junitResultsDir = "junitResultsDir"

    final static String ITEM_NAME_consoleReport = "consoleReport"

    // e.g. for Gradle based builds: fail the build if errors are found in Html file(s)
    final static String ITEM_NAME_failOnErrors = "failOnErrors"

    // in case of slow internet connections, this timeout might be helpful
    final static String ITEM_NAME_httpConnectionTimeout = "httpConnectionTimeout"

    // if (ignoreLocalhost == false) localhost-based URLs are marked as "Warning"
    final static String ITEM_NAME_ignoreLocalhost = "ignoreLocalHost"

    // if (ignoreIPAddresses) then urls with numeric IP addresses are marked as "Warning"
    final static String ITEM_NAME_ignoreIPAddresses = "ignoreIPAddresses"

    final static String ITEM_NAME_httpWarningCodes = "httpWarningCodes"
    final static String ITEM_NAME_httpErrorCodes = "httpErrorCodes"
    final static String ITEM_NAME_httpSuccessCodes = "httpSuccessCodes"

    final static String ITEM_NAME_urlsToExclude = "urlsToExclude"
    final static String ITEM_NAME_hostsToExclude = "hostsToExclude"

    /***************************
     * private member
     **************************/
    private Map configurationItems = [:]


    // constructor to set (some) default values
    private Configuration() {

        this.configurationItems.put(ITEM_NAME_httpErrorCodes, NetUtil.HTTP_ERROR_CODES)
        this.configurationItems.put(ITEM_NAME_httpSuccessCodes, NetUtil.HTTP_SUCCESS_CODES)
        this.configurationItems.put(ITEM_NAME_httpWarningCodes, NetUtil.HTTP_WARNING_CODES)

        this.configurationItems.put(ITEM_NAME_httpConnectionTimeout, 5000)   // 5 secs as default timeout
        this.configurationItems.put(ITEM_NAME_ignoreIPAddresses, false)      // warning if numerical IP addresses
        this.configurationItems.put(ITEM_NAME_ignoreLocalhost, false)        // warning if localhost-URLs
    }

    /** retrieve a single configuration item
     *
     * @param itemName
     * @return
     */
    public synchronized Object getConfigItemByName(final String itemName) {
        return configurationItems.get(itemName)
    }

    // special HtmlSanityChecker methods for mandatory configuration items
    // *******************************************************************

    /**
     * convenience method for simplified testing
     */
     synchronized void addSourceFileConfiguration(File srcDir, Set<String> srcDocs) {
        addConfigurationItem(ITEM_NAME_sourceDir, srcDir)
        addConfigurationItem(ITEM_NAME_sourceDocuments, srcDocs)
    }

    /**
     * @return true if item is already present, false otherwise
     */
    public boolean checkIfItemPresent(String itemName) {
        boolean result = false
        if (configurationItems.get(itemName) != null) {
            result = true
        }
        return result
    }

    /**
     * @return the number of configuration items
     */
     int nrOfConfigurationItems() {
        return configurationItems.size()
    }

    /** add a single configuration item, unless its value is null
     *
     * @param itemName
     * @param itemValue
     */
    void addConfigurationItem(String itemName, Object itemValue) {
        if (itemValue != null) {
            configurationItems.put(itemName, itemValue)
        }
    }

    /**
     * overwrites httpSuccessCodes configuration
     */
    void overwriteHttpSuccessCodes(Collection<Integer> additionalSuccessCodes) {
        def errCodes = getConfigItemByName(Configuration.ITEM_NAME_httpErrorCodes)
        def warnCodes = getConfigItemByName(Configuration.ITEM_NAME_httpWarningCodes)
        def successCodes = getConfigItemByName(Configuration.ITEM_NAME_httpSuccessCodes)

        additionalSuccessCodes.each { code ->
            successCodes += code // add to success codes
            errCodes -= code // the new success code cannot be error code any longer
            warnCodes -= code // neither warning
        }

        updateSuccessWarningErrorCodesConfiguration(errCodes, warnCodes, successCodes)
    }

    /**
     * overwrites httpWarningCodes configuration
     */
    void overwriteHttpWarningCodes(Collection<Integer> additionalWarningCodes) {
        def errCodes = getConfigItemByName(Configuration.ITEM_NAME_httpErrorCodes)
        def warnCodes = getConfigItemByName(Configuration.ITEM_NAME_httpWarningCodes)
        def successCodes = getConfigItemByName(Configuration.ITEM_NAME_httpSuccessCodes)

        additionalWarningCodes.each { code ->
            warnCodes += code // add to warning codes
            successCodes -= code // remove from success codes
            errCodes -= code // and remove from error codes
        }

        updateSuccessWarningErrorCodesConfiguration(errCodes, warnCodes, successCodes)
    }

    /**
     * overwrites httpErrorCodes configuration
     */
    void overwriteHttpErrorCodes(Collection<Integer> additionalErrorCodes) {
        def errCodes = getConfigItemByName(Configuration.ITEM_NAME_httpErrorCodes)
        def warnCodes = getConfigItemByName(Configuration.ITEM_NAME_httpWarningCodes)
        def successCodes = getConfigItemByName(Configuration.ITEM_NAME_httpSuccessCodes)

        additionalErrorCodes.each { code ->
            errCodes += code // add to error codes
            successCodes -= code
            warnCodes -= code
        }

        updateSuccessWarningErrorCodesConfiguration(errCodes, warnCodes, successCodes)
    }


    void updateSuccessWarningErrorCodesConfiguration(errCodes, warnCodes, successCodes) {
        addConfigurationItem(Configuration.ITEM_NAME_httpErrorCodes, errCodes)
        addConfigurationItem(Configuration.ITEM_NAME_httpWarningCodes, warnCodes)
        addConfigurationItem(Configuration.ITEM_NAME_httpSuccessCodes, successCodes)
    }

    /**
     * checks plausibility of configuration:
     * We need at least one html file as input, maybe several
     * @param configuration instance
     *
     * srcDocs needs to be of type {@link org.gradle.api.file.FileCollection}
     * to be Gradle-compliant
     */
    public Boolean isValid() {

        // we need at least srcDir and srcDocs!!
        File srcDir = getConfigItemByName(Configuration.ITEM_NAME_sourceDir)
        Set<String> srcDocs = getConfigItemByName(Configuration.ITEM_NAME_sourceDocuments)

        // cannot check if source director is null (= unspecified)
        if ((srcDir == null)) {
            throw new MisconfigurationException("source directory must not be null")
        }

        // cannot check if both input params are null
        if ((srcDir == null) && (srcDocs == null)) {
            throw new MisconfigurationException("both sourceDir and sourceDocs were null")
        }

        // no srcDir was given and empty SrcDocs
        if ((!srcDir) && (srcDocs != null)) {
            if ((srcDocs?.empty)) {
                throw new MisconfigurationException("both sourceDir and sourceDocs must not be empty")
            }
        }
        // non-existing srcDir is absurd too
        if ((!srcDir.exists())) {
            throw new MisconfigurationException("given sourceDir $srcDir does not exist.")
        }

        // if srcDir exists but is empty... no good :-(
        if ((srcDir.exists())
                && (srcDir.isDirectory())
                && (srcDir.directorySize() == 0)) {
            throw new MisconfigurationException("given sourceDir $srcDir is empty")
        }

        // if srcDir exists but does not contain any html file... no good
        if ((srcDir.exists())
                && (srcDir.isDirectory())
                && (FileCollector.getAllHtmlFilesFromDirectory(srcDir).size() == 0)) {
            throw new MisconfigurationException("no html file found in $srcDir")
        }

        // if no exception has been thrown until now,
        // the configuration seems to be valid..
        return true
    }


    @Override
    public String toString() {
        return "Configuration{" +
                "configurationItems=" + configurationItems +
                '}';
    }
}
