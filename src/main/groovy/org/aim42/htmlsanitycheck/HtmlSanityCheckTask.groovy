// see end-of-file for license information

package org.aim42.htmlsanitycheck

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction




/**
 * Entry class for the gradle-plugin.
 * Handles parameter-passing from gradle build scripts,
 * initializes the {link AllChecksRunner},
 * which does all the work.
 */
class HtmlSanityCheckTask extends DefaultTask {
    @Optional Boolean verbose  = false

    // currently we check only ONE SINGLE DOCUMENT
    @Optional @InputFile File sourceDocumentName

    // where do we expect images?
    @Optional @InputDirectory File imageDir

    // where do we put our results
    @OutputDirectory File outputDir

    // for future usage - we might check several files
    @Optional @InputDirectory File sourceDir

    //@Optional Boolean checkExternalLinks = false


    private AllChecksRunner allChecksRunner


    public HtmlSanityCheckTask() {
        allChecksRunner = new AllChecksRunner(
                imageDir: imageDir,
                outputDir: outputDir,
                checkExternalLinks: checkExternalLinks
        )
    }

    /**
     * entry point for several html sanity checks
     * @author Gernot Starke <gs@gernotstarke.de>
     */
    @TaskAction
    public void sanityCheckHtml() {

        println "=============== HtmlSanityCheck Gradle Plugin ==============="

        if (verbose) printCurrentOptions()

        allChecksRunner = new AllChecksRunner

    }

    private void printCurrentOptions() {
        println "---- current option settings ----"
        println "sourceDocumentName = $sourceDocumentName"
        println "imageDir  = $imageDir"
        println "outputDir = $outputDir"
        println "checkExternalLinks = $checkExternalLinks"
        println "---------------------------------"
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

