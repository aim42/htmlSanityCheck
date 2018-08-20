package org.aim42.htmlsanitycheck

// see end-of-file for license information

/**
 * Summarizes all possible configuration options.
 *
 * This class needs to be updated if additional configuration options are added.
 *
 * Ideas for additional config options:
 *
 * - verbosity level on console during checks
 *
 * - which HTTP status codes shall result in warnings, and which shall map to errors
 * - HTTP connection timeout
 * - list of URLs to exclude from httpLinkchecks
 * - list of hosts to exclude from httpLinkChecks
 */

class Configuration {
    private Map configurationItems = [:]


    int nrOfConfigurationItems() {
        return configurationItems.size()
    }

    // add a single configuration item
    void addConfigurationItem( String itemName, Object itemValue) {
        configurationItems.put( itemName, itemValue)
    }

    // retrieve a single configuration item
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
