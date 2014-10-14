package org.aim42.htmlsanitycheck

import org.aim42.filesystem.FileCollector

// see end-of-file for license information

import org.gradle.api.DefaultTask
import org.gradle.api.file.FileCollection
import org.gradle.api.internal.file.collections.SimpleFileCollection
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
    @InputFiles
    private FileCollection sourceDocuments

    // or all (html) files in a directory
    @InputDirectory
    File sourceDir

    // where do we store checking results
    @Optional
    @OutputDirectory
    File checkingResultsDir

    // shall we also check external resources?
    @Optional
    Boolean checkExternalLinks = false

    //
    private FileCollection allFilesToCheck

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

        // we start with an empty (input)FileCollection
        allFilesToCheck = new SimpleFileCollection()

    }

    /**
     * entry point for several html sanity checks
     * @author Gernot Starke <gs@gernotstarke.de>
     */
    @TaskAction
    public void sanityCheckHtml() {

        logBuildParameter()

        // if we have no valid input file, abort with exception
        allFilesToCheck = validateAndCollectInputFiles(sourceDir, sourceDocuments)

        // create output directory for checking results
        checkingResultsDir.mkdirs()

        // TODO: unclear: do we need to adjust pathnames if running on Windows(tm)??

        // create an AllChecksRunner...
        // ======================================
        def allChecksRunner = new AllChecksRunner(
                allFilesToCheck,
                checkingResultsDir,
                checkExternalLinks
        )

        // perform the actual checks
        // ======================================
        allChecksRunner.performAllChecks()


    }

    /**
     * checks plausibility of input parameters:
     * we need at least one html file as input, maybe several
     * @param srcDirectory must exist and be non-empty (if no srcDocument is defined)
     * @param srcDocuments may be empty (if srcDirectory is defined)
     * @return ( basically a Set ) of files
     */
    public FileCollection validateInputFiles(File srcDirectory, FileCollection srcDocuments) {
        // throw exception if input combination does not make sense
        validateInputs(srcDirectory, srcDocuments)


    }

    /**
     * Examples for acceptable param combinations:
     * 1.) "/Users/xxx/projects/build", "[a.html, b.html]"
     * @param srcDir
     * @param srcDocs
     */
    public static Boolean validateInputs(File srcDir, FileCollection srcDocs) {

        // cannot check if both input params are null
        if ((srcDir == null) && (srcDocs == null)) {
            throw new IllegalArgumentException("both sourceDir and sourceDocs were null")
        }

        // no srcDir was given and empty FileCollection
        if ((!srcDir) && (srcDocs.empty)) {
            throw new IllegalArgumentException("both sourceDir and sourceDocs must not be empty")
        }
        // non-existing srcDir is absurd too
        if ((!srcDir.exists())) {
            throw new IllegalArgumentException("given sourceDir $srcDir does not exist.")
        }

        // if srcDir exists but is empty... no good :-(
        if ((srcDir.exists())
                && (srcDir.isDirectory())
                && (srcDir.directorySize() == 0)) {
            throw new IllegalArgumentException("given sourceDir $srcDir is empty")
        }
    }



    private void logBuildParameter() {
        logger.info "=" * 70
        logger.info "Parameters given to sanityCheck plugin from gradle buildfile..."
        logger.info "Files to check  : $sourceDocuments"
        logger.info "Source directory: $sourceDir"
        logger.info "Results dir     : $checkingResultsDir"
        logger.info "Check externals : $checkExternalLinks"

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

