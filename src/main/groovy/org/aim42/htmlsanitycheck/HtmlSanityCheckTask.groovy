// see end-of-file for license information

package org.aim42.htmlsanitycheck

import org.gradle.api.DefaultTask
import org.gradle.api.file.FileCollection
import org.gradle.api.tasks.*

/**
 * Entry class for the gradle-plugin.
 * Handles parameter-passing from gradle build scripts,
 * initializes the {link AllChecksRunner},
 * which does all the work.
 */
class HtmlSanityCheckTask extends DefaultTask {

    // we support checking a SINGLE FILE
    @Optional
    @InputFile File oneFileToCheck

    // we also support checking several named files
    @Optional
    @InputFiles FileCollection someFilesToCheck

    @Optional
    @InputDirectory File theDirToCheck


    // where do we store checking results
    @Optional
    @OutputDirectory File checkingResultsDir

    // shall we also check external resources?
    @Optional
    Boolean checkExternalLinks = false


    // after input validation,
    private ArrayList<File> filesToCheck

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
    }

    /**
     * entry point for several html sanity checks
     * @author Gernot Starke <gs@gernotstarke.de>
     */
    @TaskAction
    public void sanityCheckHtml() {

        logBuildParameter()

        // validate parameters
        // ======================================
        // TODO: validate parameter

        // TODO: adjust pathnames if running on Windows(tm)

        // create an AllChecksRunner...
        // ======================================
        def allChecksRunner = new AllChecksRunner(
                oneFileToCheck,
                checkingResultsDir,
                checkExternalLinks
        )

        // perform the actual checks
        // ======================================
        allChecksRunner.performAllChecks()


    }


    private void validateInputFiles() {

    }


    private void logBuildParameter() {
        logger.info "=" * 70
        logger.info "Parameters given to sanityCheck plugin from gradle buildfile..."
        logger.info "Files to check  : $someFilesToCheck"
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

