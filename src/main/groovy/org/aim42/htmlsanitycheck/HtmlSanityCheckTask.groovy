package org.aim42.htmlsanitycheck

import org.aim42.filesystem.FileCollector

// see end-of-file for license information

import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.file.FileCollection
import org.gradle.api.tasks.*

/**
 * Entry class for the gradle-plugin.
 * Handles parameter-passing from gradle build scripts,
 * initializes the {link AllChecksRunner},
 * which does all the work.
 *
 *
 */
class HtmlSanityCheckTask extends DefaultTask {

    //
    // we support checking several named files
    @Optional
    @Input Set<String> sourceDocuments

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

    // shall we also check external resources?
    @Optional
	@Input
    Boolean checkExternalLinks = false

    // fail build on errors?
    @Optional
    @Input
    Boolean failOnErrors = false

    //
    private Set<File> allFilesToCheck

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

        // give sensible default for output directory
        checkingResultsDir = new File(project.buildDir, '/report/htmlchecks/')
		junitResultsDir = new File(project.buildDir, '/test-results/htmlchecks/')

        // we start with an empty Set
        allFilesToCheck = new HashSet<File>()

    }

    /**
     * entry point for several html sanity checks
     * @author Gernot Starke <gs@gernotstarke.de>
     */
    @TaskAction
    public void sanityCheckHtml() {

        logBuildParameter()

        // if we have no valid input file, abort with exception
        if (isValidConfiguration(sourceDir, sourceDocuments)) {

            allFilesToCheck = FileCollector.getConfiguredHtmlFiles(sourceDir, sourceDocuments)

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
            def allChecksRunner = new AllChecksRunner(
                    allFilesToCheck,
                    checkingResultsDir,
					junitResultsDir,
                    checkExternalLinks
            )
            allChecksRunner.consoleReport = false

            // perform the actual checks
            def allChecks = allChecksRunner.performAllChecks()

            // check for findings and fail build if requested
            def nrOfFindingsOnAllPages = allChecks.nrOfFindingsOnAllPages()
            logger.debug("Found ${nrOfFindingsOnAllPages} error(s) on all checked pages")

            if (failOnErrors && nrOfFindingsOnAllPages > 0)
                throw new GradleException("Found ${nrOfFindingsOnAllPages} error(s) on all checked pages")

        } else
            logger.warn("""Fatal configuration errors preventing checks:\n
              sourceDir : $sourceDir \n
              sourceDocs: $sourceDocuments\n""", "fatal error")
    }

    /**
     * checks plausibility of input parameters:
     * we need at least one html file as input, maybe several
     * @param srcDir
     * @param srcDocs needs to be of type {@link FileCollection} to be Gradle-compliant
     */
    public static Boolean isValidConfiguration(File srcDir, Set<String> srcDocs) {

        // cannot check if source director is null (= unspecified)
        if ((srcDir == null)) {
            throw new MisconfigurationException("source directory must not be null")
        }

        // cannot check if both input params are null
        if ((srcDir == null) && (srcDocs == null)) {
            throw new IllegalArgumentException("both sourceDir and sourceDocs were null")
        }

        // no srcDir was given and empty SrcDocs
        if ((!srcDir) && (srcDocs != null)) {
            if ((srcDocs?.empty))
                throw new IllegalArgumentException("both sourceDir and sourceDocs must not be empty")
        }
        // non-existing srcDir is absurd too
        if ((!srcDir.exists())) {
            throw new IllegalArgumentException("given sourceDir " + srcDir + " does not exist.")
        }

        // if srcDir exists but is empty... no good :-(
        if ((srcDir.exists())
                && (srcDir.isDirectory())
                && (srcDir.directorySize() == 0)) {
            throw new IllegalArgumentException("given sourceDir " + srcDir + " is empty")
        }

        // if srcDir exists but does not contain any html file... no good
        if ((srcDir.exists())
                && (srcDir.isDirectory())
                && (FileCollector.getAllHtmlFilesFromDirectory(srcDir).size() == 0)) {
            throw new MisconfigurationException("no html file found in", srcDir)
        }


       // if no exception has been thrown until now,
        // the configuration seems to be valid..
        return true
    }


    private void logBuildParameter() {
        logger.info "=" * 70
        logger.info "Parameters given to sanityCheck plugin from gradle buildfile..."
        logger.info "Files to check  : $sourceDocuments"
        logger.info "Source directory: $sourceDir"
        logger.info "Results dir     : $checkingResultsDir"
        logger.info "JUnit dir       : $junitResultsDir"
        logger.info "Check externals : $checkExternalLinks"
        logger.info "Fail on errors  : $failOnErrors"

    }


}

/*========================================================================
 Copyright 2014 Gernot Starke and aim42 contributors

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

