package org.aim42.htmlsanitycheck;

import org.aim42.htmlsanitycheck.check.AllCheckers;
import org.aim42.htmlsanitycheck.tools.Web;

import java.io.File;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * Handles (and can verify) configuration options.
 * <p>
 * Implemented as REGISTRY pattern
 * <p>
 * <p>
 * Explanation for configuring http status codes:
 * The standard http status codes are defined in class @link NetUtil and can
 * be overwritten by configuration:
 * <p>
 * Example: You want 503 to be ok instead of error:
 * httpSuccessCodes = [503]
 * <p>
 * During configuration initialization, the value(s) of httpSuccessCodes will be:
 * 1.) set-added to httpSuccessCodes,
 * 2.) set-subtracted from the warnings and errors.
 * <p>
 * <p>
 * This class needs to be updated if additional configuration options are added.
 * <p>
 * <p>
 * Ideas for additional config options:
 * ------------------------------------
 * - verbosity level on console during checks
 */
public class Configuration {
    /*****************************************
     * configuration item names
     *
     * NEVER use any string constants for configuration
     * item names within source code!
     ****************************************/
    private static final String ITEM_NAME_sourceDocuments = "sourceDocuments";
    private static final String ITEM_NAME_sourceDir = "sourceDir";
    private static final String ITEM_NAME_checkingResultsDir = "checkingResultsDir";
    private static final String ITEM_NAME_junitResultsDir = "junitResultsDir";
    private static final String ITEM_NAME_consoleReport = "consoleReport";
    private static final String ITEM_NAME_failOnErrors = "failOnErrors";
    private static final String ITEM_NAME_httpConnectionTimeout = "httpConnectionTimeout";
    private static final String ITEM_NAME_ignoreLocalhost = "ignoreLocalHost";
    private static final String ITEM_NAME_ignoreIPAddresses = "ignoreIPAddresses";
    private static final String ITEM_NAME_httpWarningCodes = "httpWarningCodes";
    private static final String ITEM_NAME_httpErrorCodes = "httpErrorCodes";
    private static final String ITEM_NAME_httpSuccessCodes = "httpSuccessCodes";
    private static final String ITEM_NAME_urlsToExclude = "urlsToExclude";
    private static final String ITEM_NAME_hostsToExclude = "hostsToExclude";
    private static final String ITEM_NAME_prefixOnlyHrefExtensions = "prefixOnlyHrefExtensions";
    private static final String ITEM_NAME_checksToExecute = "checksToExecute";
    /***************************
     * private member
     **************************/
    private final Map configurationItems = new LinkedHashMap<Object, Object>();

    public Configuration() {

        this.configurationItems.put(ITEM_NAME_httpErrorCodes, Web.HTTP_ERROR_CODES);
        this.configurationItems.put(ITEM_NAME_httpSuccessCodes, Web.HTTP_SUCCESS_CODES);
        this.configurationItems.put(ITEM_NAME_httpWarningCodes, Web.HTTP_WARNING_CODES);

        this.configurationItems.put(ITEM_NAME_httpConnectionTimeout, 5000);// 5 secs as default timeout
        this.configurationItems.put(ITEM_NAME_ignoreIPAddresses, false);// warning if numerical IP addresses
        this.configurationItems.put(ITEM_NAME_ignoreLocalhost, false);// warning if localhost-URLs

        this.configurationItems.put(ITEM_NAME_prefixOnlyHrefExtensions, Web.POSSIBLE_EXTENSIONS);

        this.configurationItems.put(ITEM_NAME_checksToExecute, AllCheckers.checkerClazzes);
    }

    public static String getITEM_NAME_sourceDocuments() {
        return ITEM_NAME_sourceDocuments;
    }

    public static String getITEM_NAME_sourceDir() {
        return ITEM_NAME_sourceDir;
    }

    public static String getITEM_NAME_checkingResultsDir() {
        return ITEM_NAME_checkingResultsDir;
    }

    public static String getITEM_NAME_junitResultsDir() {
        return ITEM_NAME_junitResultsDir;
    }

    public static String getITEM_NAME_consoleReport() {
        return ITEM_NAME_consoleReport;
    }

    public static String getITEM_NAME_failOnErrors() {
        return ITEM_NAME_failOnErrors;
    }

    public static String getITEM_NAME_httpConnectionTimeout() {
        return ITEM_NAME_httpConnectionTimeout;
    }

    public static String getITEM_NAME_ignoreLocalhost() {
        return ITEM_NAME_ignoreLocalhost;
    }

    public static String getITEM_NAME_ignoreIPAddresses() {
        return ITEM_NAME_ignoreIPAddresses;
    }

    public static String getITEM_NAME_httpWarningCodes() {
        return ITEM_NAME_httpWarningCodes;
    }

    public static String getITEM_NAME_httpErrorCodes() {
        return ITEM_NAME_httpErrorCodes;
    }

    public static String getITEM_NAME_httpSuccessCodes() {
        return ITEM_NAME_httpSuccessCodes;
    }

    public static String getITEM_NAME_urlsToExclude() {
        return ITEM_NAME_urlsToExclude;
    }

    public static String getITEM_NAME_hostsToExclude() {
        return ITEM_NAME_hostsToExclude;
    }

    public static String getITEM_NAME_prefixOnlyHrefExtensions() {
        return ITEM_NAME_prefixOnlyHrefExtensions;
    }

    public static String getITEM_NAME_checksToExecute() {
        return ITEM_NAME_checksToExecute;
    }

    /**
     * retrieve a single configuration item
     *
     * @param itemName
     * @return
     */
    public synchronized Object getConfigItemByName(final String itemName) {
        return configurationItems.get(itemName);
    }

    public Set<Integer> getConfigItemByNameSetOfIntegers(final String itemName) {
        Object rawConfig = getConfigItemByName(itemName);
        if (rawConfig instanceof Set ) {
            return (Set<Integer>) rawConfig;
        }
        throw new IllegalArgumentException("The Configuration property \"" + itemName + "\" should be a set of integers");
    }

    /**
     * convenience method for simplified testing
     */
    public synchronized void addSourceFileConfiguration(File srcDir, Collection<File> srcDocs) {
        addConfigurationItem(ITEM_NAME_sourceDir, srcDir);
        addConfigurationItem(ITEM_NAME_sourceDocuments, srcDocs);
    }

    /**
     * @return true if item is already present, false otherwise
     */
    public boolean checkIfItemPresent(String itemName) {
        return configurationItems.get(itemName) != null;

    }

    /**
     * @return the number of configuration items
     */
    public int nrOfConfigurationItems() {
        return configurationItems.size();
    }

    /**
     * add a single configuration item, unless its value is null
     *
     * @param itemName
     * @param itemValue
     */
    public void addConfigurationItem(String itemName, Object itemValue) {
        if (itemValue != null) {
            configurationItems.put(itemName, itemValue);
        }

    }

    /**
     * overwrites httpSuccessCodes configuration
     */
    public void overwriteHttpSuccessCodes(Collection<Integer> additionalSuccessCodes) {
        final Set<Integer> errCodes = (Set<Integer>) getConfigItemByName(Configuration.getITEM_NAME_httpErrorCodes());
        final Set<Integer> warnCodes = (Set<Integer>) getConfigItemByName(Configuration.getITEM_NAME_httpWarningCodes());
        final Set<Integer> successCodes = (Set<Integer>) getConfigItemByName(Configuration.getITEM_NAME_httpSuccessCodes());

        additionalSuccessCodes.forEach(co -> {
                    successCodes.add(co);
                    errCodes.remove(co);
                    warnCodes.remove(co);
                }
        );

        updateSuccessWarningErrorCodesConfiguration(errCodes, warnCodes, successCodes);
    }

    /**
     * overwrites httpWarningCodes configuration
     */
    public void overwriteHttpWarningCodes(Collection<Integer> additionalWarningCodes) {
        final Set<Integer> errCodes = (Set<Integer>) getConfigItemByName(Configuration.getITEM_NAME_httpErrorCodes());
        final Set<Integer> warnCodes = (Set<Integer>) getConfigItemByName(Configuration.getITEM_NAME_httpWarningCodes());
        final Set<Integer> successCodes = (Set<Integer>) getConfigItemByName(Configuration.getITEM_NAME_httpSuccessCodes());

        additionalWarningCodes.forEach(co -> {
                    successCodes.add(co);
                    errCodes.remove(co);
                    warnCodes.remove(co);
                }
        );

        updateSuccessWarningErrorCodesConfiguration(errCodes, warnCodes, successCodes);
    }

    /**
     * overwrites httpErrorCodes configuration
     */
    public void overwriteHttpErrorCodes(Collection<Integer> additionalErrorCodes) {
        final Set<Integer> errCodes = (Set<Integer>) getConfigItemByName(Configuration.getITEM_NAME_httpErrorCodes());
        final Set<Integer> warnCodes = (Set<Integer>) getConfigItemByName(Configuration.getITEM_NAME_httpWarningCodes());
        final Set<Integer> successCodes = (Set<Integer>) getConfigItemByName(Configuration.getITEM_NAME_httpSuccessCodes());

        additionalErrorCodes.forEach(co -> {
                    successCodes.add(co);
                    errCodes.remove(co);
                    warnCodes.remove(co);
                }
        );

        updateSuccessWarningErrorCodesConfiguration(errCodes, warnCodes, successCodes);
    }

    public void updateSuccessWarningErrorCodesConfiguration(Object errCodes, Object warnCodes, Object successCodes) {
        addConfigurationItem(Configuration.getITEM_NAME_httpErrorCodes(), errCodes);
        addConfigurationItem(Configuration.getITEM_NAME_httpWarningCodes(), warnCodes);
        addConfigurationItem(Configuration.getITEM_NAME_httpSuccessCodes(), successCodes);
    }

    /**
     * overwrites prefixOnlyHrefExtensions
     */
    public void overwritePrefixOnlyHrefExtensions(Collection<String> prefixesToBeConsidered) {
        addConfigurationItem(Configuration.getITEM_NAME_prefixOnlyHrefExtensions(), prefixesToBeConsidered);
    }

    /**
     * checks plausibility of configuration:
     * We need at least one html file as input, maybe several
     *
     * @param configuration instance
     *                      <p>
     *                      srcDocs needs to be of type {@link FileCollection}
     *                      to be Gradle-compliant
     */
    public Boolean isValid() throws MisconfigurationException {

        // we need at least srcDir and srcDocs!!
        File srcDir = (File) getConfigItemByName(Configuration.getITEM_NAME_sourceDir());
        Set<String> srcDocs = (Set<String>) getConfigItemByName(Configuration.getITEM_NAME_sourceDocuments());

        // cannot check if source director is null (= unspecified)
        if ((srcDir == null)) {
            throw new MisconfigurationException("source directory must not be null");
        }


        if ((!srcDir.exists())) {
            throw new MisconfigurationException("given sourceDir " + srcDir + " does not exist.");
        }


        // cannot check if both input params are null
        if (srcDocs == null) {
            throw new MisconfigurationException("source documents must not be null");
        }


        // empty SrcDocs
        if (srcDocs.isEmpty()) {
            throw new MisconfigurationException("source documents must not be empty");
        }


        Object checksToExecute = getConfigItemByName(Configuration.getITEM_NAME_checksToExecute());
        if (!(checksToExecute == null || checksToExecute instanceof Collection)) {
            throw new MisconfigurationException("checks to execute have to be a non empty collection");
        }


        // if no exception has been thrown until now,
        // the configuration seems to be valid..
        return true;
    }

    @Override
    public String toString() {
        return "Configuration{" + "configurationItems=" + configurationItems + "}";
    }

}
