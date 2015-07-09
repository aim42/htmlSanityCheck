package org.aim42.htmlsanitycheck

import org.aim42.htmlsanitycheck.check.Checker
import org.aim42.htmlsanitycheck.check.CheckerCreator
import org.aim42.htmlsanitycheck.collect.PerRunResults
import org.aim42.htmlsanitycheck.collect.SinglePageResults
import org.aim42.htmlsanitycheck.html.HtmlPage
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import java.lang.reflect.Array

/**
 * runs one or several checks on HTML input
 */
class ChecksRunner {

    // we check a collection of files:
    private Collection<File> filesToCheck

    // where do we put our results
    private File checkingResultsDir

    // checker instances
    private Set<Checker> checkers

    private HtmlPage pageToCheck

    // keep all results
    private PerRunResults resultsForAllPages

    private static Logger logger = LoggerFactory.getLogger(ChecksRunner.class);


    // standard constructor
    public ChecksRunner(
            Set<Class> checkerCollection,
            Set<File> filesToCheck,
            File checkingResultsDir,
            Boolean checkExternalResources
    ) {
        this.resultsForAllPages = new PerRunResults()

        this.filesToCheck = filesToCheck
        this.checkingResultsDir = checkingResultsDir

        this.checkers = CheckerCreator.createCheckerClassesFrom( checkerCollection )

        logger.debug("ChecksRunner created")
    }

    /**
     *  performs all known checks on a single HTML file.
     *
     *  Creates a {@link org.aim42.htmlsanitycheck.collect.SinglePageResults} instance to keep checking results.
     */
    public SinglePageResults performAllChecksForOneFile(File thisFile) {

        pageToCheck = parseHtml(thisFile)
        String baseDir = thisFile.parent

        SinglePageResults resultsCollector =
                new SinglePageResults(
                        pageFilePath: thisFile.canonicalPath,
                        pageFileName: thisFile.name,
                        pageTitle: pageToCheck.getDocumentTitle(),
                        pageSize: pageToCheck.documentSize
                )

        // the actual checks
        resultsCollector.with {
            addResultsForSingleCheck(missingImageFilesCheck(baseDir))
            addResultsForSingleCheck(duplicateIdCheck())
            addResultsForSingleCheck(brokenCrossReferencesCheck())
            addResultsForSingleCheck(missingLocalResourcesCheck(baseDir))
            addResultsForSingleCheck(missingAltAttributesCheck())
        }

        return resultsCollector
    }

    /**
     * performs all configured checks on pageToCheck
     */
    public PerRunResults performAllChecks() {

        logger.debug "entered performAllChecks"

        filesToCheck.each { file ->
               resultsForAllPages.addPageResults(
                    performAllChecksForOneFile(file))
        }

        // after all checks, stop the timer...
        resultsForAllPages.stopTimer()

        // and then report the results
        reportCheckingResultsOnConsole()
        reportCheckingResultsAsHTML(checkingResultsDir.absolutePath)
    }

}

/************************************************************************
 * This is free software - without ANY guarantee!
 *
 *
 * Copyright Dr. Gernot Starke, arc42.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 *********************************************************************** */

