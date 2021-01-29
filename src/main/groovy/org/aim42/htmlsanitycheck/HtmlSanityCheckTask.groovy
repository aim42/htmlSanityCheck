package org.aim42.htmlsanitycheck

import org.aim42.htmlsanitycheck.check.AllCheckers
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.file.FileCollection
import org.gradle.api.tasks.*

// see end-of-file for license information

/**
 * Entry class for the gradle-plugin.
 * Handles parameter-passing from gradle build scripts,
 * initializes the {link AllChecksRunner},
 * which does all the checking and reporting work.
 *
 * @author Gernot Starke
 */
class HtmlSanityCheckTask extends DefaultTask {

    //
    // we support checking several named files
    @InputFiles
    FileCollection sourceDocuments

    // or all (html) files in a directory
    @InputDirectory
    File sourceDir

    // where do we store checking results
    @Optional
    @OutputDirectory
    File checkingResultsDir

    // where do we store junit results
    @Optional
    @OutputDirectory
    File junitResultsDir

    // fail build on errors?
    @Optional
    @Input
    Boolean failOnErrors = false

    // configurable timeout for http-requests (used by @BrokenHttpLinksChecker)
    // defaults to 5000 (msecs)
    // java primitives must not be marked as @Optional
    @Input
    int httpConnectionTimeout = 5000

    // shall localhost-URLs lead to warnings?
    // java primitives must not be marked as @Optional
    @Input
    boolean ignoreLocalHost = false

    // shall numerical IP addresses lead to warnings?
    // java primitives must not be marked as @Optional
    @Input
    boolean ignoreIPAddresses = false

    // shall certain http status codes be treated differently from the standard?
    @Optional
    @Input
    Collection<Integer> httpWarningCodes
    @Optional
    @Input
    Collection<Integer> httpErrorCodes
    @Optional
    @Input
    Collection<Integer> httpSuccessCodes

    @Input
    Set<Class> checkerClasses = AllCheckers.checkerClazzes

    // private stuff
    // **************************************************************************

    private Set<File> allFilesToCheck

    private Configuration myConfig


    /**
     * Sets sensible defaults for important attributes.
     *
     * Ensures that task is _run-always_,
     * by setting outputs.upToDateWhen to false.
     */
    HtmlSanityCheckTask() {

        // Never consider this task up-to-date.
        // thx https://github.com/stevesaliman/gradle-cobertura-plugin/commit/d61191f7d5f4e8e89abcd5f3839a210985526648
        outputs.upToDateWhen { false }

        // give sensible default for output directory, see https://github.com/aim42/htmlSanityCheck/issues/205
        checkingResultsDir = new File(project.buildDir, '/reports/htmlSanityCheck/')
        junitResultsDir = new File(project.buildDir, '/test-results/htmlSanityCheck/')


    }

    void setSourceDir(File sourceDir) {
        this.sourceDir = sourceDir
        if (sourceDocuments == null) {
            sourceDocuments = project.fileTree(sourceDir)
            sourceDocuments.include('**/*.html')
        }
    }

/**
 * entry point for several html sanity checks
 * @author Gernot Starke <gs@gernotstarke.de>
 */
    @TaskAction
    public void sanityCheckHtml() {

        // tell us about these parameters
        logBuildParameter()

        // get configuration parameters from gradle
        myConfig = this.setupConfiguration()

        // if we have no valid configuration, abort with exception
        if (myConfig.isValid()) {

            // create output directory for checking results
            checkingResultsDir.mkdirs()
            assert checkingResultsDir.isDirectory()
            assert checkingResultsDir.canWrite()
            if (junitResultsDir) {
                junitResultsDir.mkdirs()
                assert junitResultsDir.isDirectory()
                assert junitResultsDir.canWrite()
            }

            // TODO: unclear: do we need to adjust pathnames if running on Windows(tm)??

            logger.info("buildfile-info", sourceDocuments?.toString())
            logger.info("allFilesToCheck" + allFilesToCheck.toString(), "")

            // create an AllChecksRunner...
            def allChecksRunner = new AllChecksRunner(myConfig)

            // ... and perform the actual checks
            def allChecks = allChecksRunner.performAllChecks()

            // check for findings and fail build if requested
            def nrOfFindingsOnAllPages = allChecks.nrOfFindingsOnAllPages()
            logger.debug("Found ${nrOfFindingsOnAllPages} error(s) on all checked pages")

            if (failOnErrors && nrOfFindingsOnAllPages > 0) {
                def failureMsg = """
Your build configuration included 'failOnErrors=true', and ${nrOfFindingsOnAllPages} error(s) were found on all checked pages.
See ${checkingResultsDir} for a detailed report."""
                throw new GradleException(failureMsg)
            }
        } else {
            logger.warn("""Fatal configuration errors preventing checks:\n
            ${myConfig.toString()}""")
        }
    }

    /**
     * setup a @Configuration instance containing all given configuration parameters
     * from the gradle buildfile.
     *
     * This method has to be updated in case of new configuration parameters!!
     *
     * Note: It does not check this configuration for plausibility or mental health...
     * @return @Configuration
     */
    protected Configuration setupConfiguration() {

        Configuration tmpConfig = new Configuration()

        tmpConfig.with {
            addConfigurationItem(Configuration.ITEM_NAME_sourceDocuments, sourceDocuments.files)
            addConfigurationItem(Configuration.ITEM_NAME_sourceDir, sourceDir)
            addConfigurationItem(Configuration.ITEM_NAME_checkingResultsDir, checkingResultsDir)
            addConfigurationItem(Configuration.ITEM_NAME_junitResultsDir, junitResultsDir)

            // consoleReport is always FALSE for Gradle based builds
            addConfigurationItem(Configuration.ITEM_NAME_consoleReport, false)
            addConfigurationItem(Configuration.ITEM_NAME_failOnErrors, failOnErrors)
            addConfigurationItem(Configuration.ITEM_NAME_httpConnectionTimeout, httpConnectionTimeout)

            addConfigurationItem(Configuration.ITEM_NAME_ignoreLocalhost, ignoreLocalHost)
            addConfigurationItem(Configuration.ITEM_NAME_ignoreIPAddresses, ignoreIPAddresses)

            addConfigurationItem(Configuration.ITEM_NAME_checksToExecute, checkerClasses)

            // in case we have configured specific interpretations of http status codes
            overwriteHttpSuccessCodes(httpSuccessCodes)
            overwriteHttpErrorCodes(httpErrorCodes)
            overwriteHttpWarningCodes(httpWarningCodes)
        }

        return tmpConfig
    }


    private void logBuildParameter() {
        logger.info "=" * 70
        logger.info "Parameters given to sanityCheck plugin from gradle buildfile..."
        logger.info "Files to check  : $sourceDocuments"
        logger.info "Source directory: $sourceDir"
        logger.info "Results dir     : $checkingResultsDir"
        logger.info "JUnit dir       : $junitResultsDir"
        logger.info "Fail on errors  : $failOnErrors"

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

