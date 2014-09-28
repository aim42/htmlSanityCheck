// see end-of-file for license information

package org.aim42.htmlsanitycheck

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.*

import javax.inject.Inject

/**
 * Entry class for the gradle-plugin.
 * Handles parameter-passing from gradle build scripts,
 * initializes the {link AllChecksRunner},
 * which does all the work.
 */
class HtmlSanityCheckTask extends DefaultTask {

    // currently we only support checking a SINGLE FILE
    // will make this a FileCollection soon
    @InputFile
    File fileToCheck

    // where do we store checking results
    @Optional
    @OutputDirectory
    File checkingResultsDir

    // shall we also check external resources?
    @Optional
    Boolean checkExternalLinks = false



    // use constructor to care for _run-always_
    HtmlSanityCheckTask() {

        // Never consider this up to date.
        // thx https://github.com/stevesaliman/gradle-cobertura-plugin/commit/d61191f7d5f4e8e89abcd5f3839a210985526648
        outputs.upToDateWhen { false }
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

        // create an AllChecksRunner...
        // ======================================
        def allChecksRunner = new AllChecksRunner(
                fileToCheck,
                imageDir,
                checkingResultsDir,
                checkExternalLinks
        )

        // perform the actual checks
        // ======================================
        allChecksRunner.performAllChecks()


    }


    private void logBuildParameter() {
        logger.info "=" * 70
        logger.info "Parameters given to sanityCheck plugin from gradle buildfile..."
        logger.info "File to check   : $fileToCheck"
        logger.info "Image dir       : $imageDir"
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

