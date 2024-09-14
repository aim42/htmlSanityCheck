package org.aim42.htmlSanityCheck.htmlsanitycheckmavenplugin;

import org.aim42.htmlsanitycheck.AllChecksRunner;
import org.aim42.htmlsanitycheck.Configuration;
import org.aim42.htmlsanitycheck.MisconfigurationException;
import org.aim42.htmlsanitycheck.check.AllCheckers;
import org.aim42.htmlsanitycheck.check.Checker;
import org.aim42.htmlsanitycheck.collect.PerRunResults;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/*
 No Javadoc for class due to https://stackoverflow.com/questions/28428833/maven-error-when-doing-packaging
 */


@Mojo(name = "sanity-check", defaultPhase = LifecyclePhase.VERIFY, requiresDependencyResolution = ResolutionScope.RUNTIME)
public class HtmlSanityCheckMojo extends AbstractMojo {


    @Parameter(defaultValue = "${project.build.directory}/reports/htmlSanityCheck/", required = true)
    private File checkingResultsDir;

    @Parameter(defaultValue = "${project.build.directory}/test-results/htmlSanityCheck/", required = true)
    private File junitResultsDir;

    @Parameter
    private File sourceDir;

    @Parameter
    private Set<File> sourceDocuments;

    @Parameter(defaultValue = "false")
    private boolean failOnErrors;

    @Parameter(defaultValue = "5000")
    private int httpConnectionTimeout;

    @Parameter(defaultValue = "false")
    private boolean ignoreLocalHost;

    @Parameter(defaultValue = "false")
    private boolean ignoreIPAddresses;

    @Parameter
    private Set<Integer> httpWarningCodes;

    @Parameter
    private Set<Integer> httpErrorCodes;

    @Parameter
    private Set<Integer> httpSuccessCodes;

    @Parameter
    private List<Class<? extends Checker>> checkerClasses = AllCheckers.CHECKER_CLASSES;

    public void execute() throws MojoExecutionException {
        logBuildParameter();

// Setup configuration
        Configuration myConfig = setupConfiguration();

// Check if configuration is valid
        try {
            myConfig.validate();
            // Create output directories
            checkingResultsDir.mkdirs();
            if (!checkingResultsDir.isDirectory() || !checkingResultsDir.canWrite()) {
                throw new MojoExecutionException("Cannot write to checking results directory.");
            }
            if (junitResultsDir != null) {
                junitResultsDir.mkdirs();
                if (!junitResultsDir.isDirectory() || !junitResultsDir.canWrite()) {
                    throw new MojoExecutionException("Cannot write to JUnit results directory.");
                }
            }

            // Perform checks
            AllChecksRunner allChecksRunner = new AllChecksRunner(myConfig);
            PerRunResults allChecks = allChecksRunner.performAllChecks();

            // Handle findings
            int nrOfFindingsOnAllPages = allChecks.nrOfFindingsOnAllPages();
            getLog().debug("Found " + nrOfFindingsOnAllPages + " error(s) on all checked pages");

            if (failOnErrors && nrOfFindingsOnAllPages > 0) {
                String failureMsg = String.format(
                        "Your build configuration included 'failOnErrors=true', and %d error(s) were found on all checked pages. See %s for a detailed report.",
                        nrOfFindingsOnAllPages, checkingResultsDir
                );
                throw new MojoExecutionException(failureMsg);
            }
            if (junitResultsDir != null) {
                junitResultsDir.mkdirs();
                if (!junitResultsDir.isDirectory() || !junitResultsDir.canWrite()) {
                    throw new MojoExecutionException("Cannot write to JUnit results directory.");
                }
            }

            // Perform checks
            AllChecksRunner allChecksRunner = new AllChecksRunner(myConfig);
            PerRunResults allChecks = allChecksRunner.performAllChecks();

            // Handle findings
            int nrOfFindingsOnAllPages = allChecks.nrOfFindingsOnAllPages();
            getLog().debug("Found " + nrOfFindingsOnAllPages + " error(s) on all checked pages");

            if (failOnErrors && nrOfFindingsOnAllPages > 0) {
                String failureMsg = String.format(
                        "Your build configuration included 'failOnErrors=true', and %d error(s) were found on all checked pages. See %s for a detailed report.",
                        nrOfFindingsOnAllPages, checkingResultsDir
                );
                throw new MojoExecutionException(failureMsg);
            }


        } catch (MisconfigurationException e) {
            throw new MojoExecutionException(e);
        } catch (IOException e) {
            throw new MojoExecutionException(e);
        }
    }

    private void logBuildParameter() {
        // Log build parameters
        getLog().info(String.join("", Collections.nCopies(70, "=")));
        getLog().info("Parameters given to sanityCheck plugin from Maven buildfile...");
        getLog().info("Files to check  : " + sourceDocuments);
        getLog().info("Source directory: " + sourceDir);
        getLog().info("Results dir     : " + checkingResultsDir);
        getLog().info("JUnit dir       : " + junitResultsDir);
        getLog().info("Fail on errors  : " + failOnErrors);
    }

    protected Configuration setupConfiguration() {
        Configuration result = Configuration.builder()
                .sourceDocuments(sourceDocuments)
                .sourceDir(sourceDir)
                .checkingResultsDir(checkingResultsDir)
                .junitResultsDir(junitResultsDir)

                // consoleReport is always FALSE for Gradle based builds
                .consoleReport(false)
                .failOnErrors(failOnErrors)
                .httpConnectionTimeout(httpConnectionTimeout)

                .ignoreLocalhost(ignoreLocalHost)
                .ignoreIPAddresses(ignoreIPAddresses)

                .checksToExecute(checkerClasses)
                .build();

        // in case we have configured specific interpretations of http status codes
        if (httpSuccessCodes != null && !httpSuccessCodes.isEmpty()) {
            result.overrideHttpSuccessCodes(httpSuccessCodes);
        }
        if (httpErrorCodes != null &&  !httpErrorCodes.isEmpty()) {
            result.overrideHttpErrorCodes(httpErrorCodes);
        }
        if (httpErrorCodes != null && !httpWarningCodes.isEmpty()) {
            result.overrideHttpWarningCodes(httpWarningCodes);
        }

        return result;
    }
}
