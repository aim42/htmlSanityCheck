package org.aim42.htmlsanitycheck.maven;

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

 This Mojo-Class is the core of the maven plugin.
 It uses the maven capabilities to take the configuration parameters from the mavens pom.xml and calls the hsc core
 with the parameters.

 It does not include any checking functionality itself.
 */


@Mojo(name = "sanity-check", defaultPhase = LifecyclePhase.VERIFY, requiresDependencyResolution = ResolutionScope.RUNTIME)
public class HtmlSanityCheckMojo extends AbstractMojo {

    /**
     * (optional) Directory where the checking results written to.
     * <p>
     * Type: Directory.
     * <p>
     * Default: {buildDir}/reports/htmlSanityCheck/
     */
    @Parameter(defaultValue = "${project.build.directory}/reports/htmlSanityCheck/", required = true)
    private File checkingResultsDir;


    /**
     * (optional) Directory where the results are written to in JUnit XML format. JUnit XML can be read by many tools, including CI environments.
     * <p>
     * Type: Directory.
     * <p>
     * Default: {buildDir}/test-results/htmlchecks/
     */
    @Parameter(defaultValue = "${project.build.directory}/test-results/htmlSanityCheck/", required = true)
    private File junitResultsDir;

    /**
     * (mandatory)
     * Directory where the HTML files are located.
     */
    @Parameter
    private File sourceDir;

    /**
     * (optional) An override to process several source files, which may be a subset of all files available in {sourceDir}.
     * <p>
     * Type: org.maven.api.file.FileCollection.
     * <p>
     * Default: All files in {sourceDir} whose names end with .html.
     */
    @Parameter
    private Set<File> sourceDocuments;

    /**
     * (optional)
     * Fail the build if any error was found in the checked pages.
     * <p>
     * Type: Boolean.
     * <p>
     * Default: false.
     */
    @Parameter(defaultValue = "false")
    private boolean failOnErrors;

    /**
     * (optional) Timeout for http requests in ms.
     * <p>
     * Type: Integer.
     * <p>
     * Default: 5000.
     */
    @Parameter(defaultValue = "5000")
    private int httpConnectionTimeout;

    /**
     * (optional) Ignore localhost as hostname.
     * <p>
     * Type: Boolean.
     * <p>
     * Default: false.
     */
    @Parameter(defaultValue = "false")
    private boolean ignoreLocalHost;

    /**
     * (optional) Ignore IP addresses as hostname.
     * <p>
     * Type: Boolean.
     * <p>
     * Default: false.
     */
    @Parameter(defaultValue = "false")
    private boolean ignoreIPAddresses;

    /**
     * (optional) Additional HTTP response codes treated as warning.
     * <p>
     * Type: List.
     * <p>
     * Default:
     * <p>
     * 100, 101, 102
     * // Redirects included
     * 301, 302, 303, 307, 308
     * HTTP Redirects
     * Note that HTTP redirects are treated as a warning to make the user aware of the correct or new location (cf. Issue 244). Some HSC reports often contain the respective location.
     */
    @Parameter
    private Set<Integer> httpWarningCodes;

    /**
     * (optional) Additional HTTP response codes treated as error.
     * <p>
     * Type: List.
     * <p>
     * Default:
     * <p>
     * 400, 401, 402, 403, 404,
     * 405, 406, 407, 408, 409,
     * 410, 411, 412, 413, 414,
     * 415, 416, 417, 418, 421,
     * 422, 423, 424, 425, 426,
     * 428, 429, 431, 451,
     * 500, 501, 502, 503, 504,
     * 505, 506, 507, 508, 510,
     * 511
     */
    @Parameter
    private Set<Integer> httpErrorCodes;

    /**
     * (optional) Additional HTTP response codes treated as success.
     * <p>
     * Type: List.
     * <p>
     * Default:
     * <p>
     * 200, 201, 202, 203, 204,
     * 205, 206, 207, 208, 226
     */
    @Parameter
    private Set<Integer> httpSuccessCodes;

    /**
     * (optional) The set of checker classes to be executed.
     * <p>
     * Type: List.
     * <p>
     * Default: All available checker classes.
     * <p>
     * Checker Classes
     * // Keep the list ordering to ensure
     * // report ordering comparability
     * // with HSC 1.x versions
     * MissingAltInImageTagsChecker.class,
     * MissingImageFilesChecker.class,
     * DuplicateIdChecker.class,
     * BrokenHttpLinksChecker.class,
     * ImageMapChecker.class,
     * BrokenCrossReferencesChecker.class,
     * MissingLocalResourcesChecker.class
     */
    @Parameter
    private List<Class<? extends Checker>> checkerClasses = AllCheckers.CHECKER_CLASSES;

    static PerRunResults performChekcs(Configuration myConfig) throws MojoExecutionException {
        try {
            AllChecksRunner allChecksRunner = new AllChecksRunner(myConfig);
            return allChecksRunner.performAllChecks();
        } catch (IOException e) {
            throw new MojoExecutionException(e);
        }
    }

    // tag::maven-plugin-implementation[]
    public void execute() throws MojoExecutionException {
        execute(setupConfiguration());
    }

     void execute(Configuration myConfig) throws MojoExecutionException {
        // Check if configuration is valid
         logBuildParameter(myConfig);
        try {
            myConfig.validate();
        } catch (MisconfigurationException e) {
            throw new MojoExecutionException(e);
        }

        // Create output directories
        createoutputDirs(myConfig.getCheckingResultsDir(), "Cannot write to checking results directory.");
        if (myConfig.getJunitResultsDir() != null) {
            createoutputDirs(myConfig.getJunitResultsDir(), "Cannot write to JUnit results directory.");
        }

        // Perform checks
        PerRunResults allChecks = performChekcs(myConfig);

        // Handle findings
        handleFindings(allChecks.nrOfFindingsOnAllPages(), myConfig);
    }

    void handleFindings(int nrOfFindingsOnAllPages, Configuration config) throws MojoExecutionException {
        getLog().debug("Found " + nrOfFindingsOnAllPages + " error(s) on all checked pages");

        if (config.getFailOnErrors() && nrOfFindingsOnAllPages > 0) {
            String failureMsg = String.format(
                    "Your build configuration included 'failOnErrors=true', and %d error(s) were found on all checked pages. See %s for a detailed report.",
                    nrOfFindingsOnAllPages, config.getJunitResultsDir()
            );
            throw new MojoExecutionException(failureMsg);
        }
    }
    // end::maven-plugin-implementation[]

    void createoutputDirs(File dir, String failMessage) throws MojoExecutionException {
        dir.mkdirs();
        if (!dir.isDirectory() || !dir.canWrite()) {
            throw new MojoExecutionException(failMessage);
        }
    }


    void logBuildParameter(Configuration myConfig) {
        // Log build parameters
        getLog().info(String.join("", Collections.nCopies(70, "=")));
        getLog().info("Parameters given to sanityCheck plugin from Maven buildfile...");
        getLog().info("Files to check  : " + myConfig.getSourceDocuments());
        getLog().info("Source directory: " + myConfig.getSourceDir());
        getLog().info("Results dir     : " + myConfig.getCheckingResultsDir());
        getLog().info("JUnit dir       : " + myConfig.getJunitResultsDir());
        getLog().info("Fail on errors  : " + myConfig.getFailOnErrors());
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
        if (httpErrorCodes != null && !httpErrorCodes.isEmpty()) {
            result.overrideHttpErrorCodes(httpErrorCodes);
        }
        if (httpErrorCodes != null && !httpWarningCodes.isEmpty()) {
            result.overrideHttpWarningCodes(httpWarningCodes);
        }

        return result;
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
