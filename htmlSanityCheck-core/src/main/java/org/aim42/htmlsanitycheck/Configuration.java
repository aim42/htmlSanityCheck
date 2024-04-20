package org.aim42.htmlsanitycheck;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.aim42.htmlsanitycheck.check.AllCheckers;
import org.aim42.htmlsanitycheck.check.Checker;
import org.aim42.htmlsanitycheck.tools.Web;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Handles (and can verify) configuration options.
 * <p>
 * Ideas for additional config options:
 * ------------------------------------
 * - verbosity level on console during checks
 * - exclusion lists for hosts or particular URLs
 */
@Getter
@Builder
@AllArgsConstructor
@ToString
@Slf4j
public class Configuration {
    Set<File> sourceDocuments;
    File sourceDir;
    File checkingResultsDir;
    File junitResultsDir;
    Boolean consoleReport;
    Boolean failOnErrors;
    Integer httpConnectionTimeout;
    Boolean ignoreLocalhost;
    Boolean ignoreIPAddresses;
    /*
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
     */
    // Make modifiable copies!
    final Set<Integer> httpWarningCodes = new HashSet<>(Web.HTTP_WARNING_CODES);
    final Set<Integer> httpErrorCodes = new HashSet<>(Web.HTTP_ERROR_CODES);
    final Set<Integer> httpSuccessCodes = new HashSet<>(Web.HTTP_SUCCESS_CODES);
    Set<String> prefixOnlyHrefExtensions;
    List<Class<? extends Checker>> checksToExecute;

    public Configuration() {
        this.httpConnectionTimeout = 5000;// 5 secs as default timeout
        this.ignoreIPAddresses = false;// warning if numerical IP addresses
        this.ignoreLocalhost = false;// warning if localhost-URLs

        this.prefixOnlyHrefExtensions = Web.POSSIBLE_EXTENSIONS;

        this.checksToExecute = AllCheckers.CHECKER_CLASSES;
    }

    /**
     * Convenience method for simplified testing
     */
    public synchronized void setSourceConfiguration(final File sourceDir, final Set<File> sourceDocuments) {
        this.sourceDir = sourceDir;
        this.sourceDocuments = sourceDocuments;
    }

    /**
     * overwrites httpSuccessCodes configuration
     */
    public void overrideHttpSuccessCodes(Set<Integer> additionalSuccessCodes) {
        if (null != additionalSuccessCodes) {
            additionalSuccessCodes.forEach(co -> {
                        httpSuccessCodes.add(co);
                        httpWarningCodes.remove(co);
                        httpErrorCodes.remove(co);
                    }
            );
        } else {
            log.warn("Trying to override http success codes with empty set of codes");
        }
    }

    /**
     * overwrites httpWarningCodes configuration
     */
    public void overrideHttpWarningCodes(Set<Integer> additionalWarningCodes) {
        if (null != additionalWarningCodes) {
            additionalWarningCodes.forEach(co -> {
                        httpSuccessCodes.remove(co);
                        httpWarningCodes.add(co);
                        httpErrorCodes.remove(co);
                    }
            );
        } else {
            log.warn("Trying to override http warning codes with empty set of codes");
        }
    }

    /**
     * overwrites httpErrorCodes configuration
     */
    public void overrideHttpErrorCodes(Set<Integer> additionalErrorCodes) {
        if (null != additionalErrorCodes) {
            additionalErrorCodes.forEach(co -> {
                        httpSuccessCodes.remove(co);
                        httpWarningCodes.remove(co);
                        httpErrorCodes.add(co);
                    }
            );
        } else {
            log.warn("Trying to override http error codes with empty set of codes");
        }
    }

    /**
     * checks plausibility of configuration:
     * We need at least one html file as input, maybe several
     */
    public Boolean isValid() throws MisconfigurationException {

        // cannot check if source director is null (= unspecified)
        if ((sourceDir == null)) {
            throw new MisconfigurationException("source directory must not be null");
        }


        if ((!sourceDir.exists())) {
            throw new MisconfigurationException("given sourceDir " + sourceDir + " does not exist.");
        }


        // cannot check if both input params are null
        if (sourceDocuments == null) {
            throw new MisconfigurationException("source documents must not be null");
        }


        // empty SrcDocs
        if (sourceDocuments.isEmpty()) {
            throw new MisconfigurationException("source documents must not be empty");
        }


        if (checksToExecute == null || checksToExecute.isEmpty()) {
            throw new MisconfigurationException("checks to execute have to be a non-empty list");
        }


        // if no exception has been thrown until now,
        // the configuration seems to be valid..
        return true;
    }
}