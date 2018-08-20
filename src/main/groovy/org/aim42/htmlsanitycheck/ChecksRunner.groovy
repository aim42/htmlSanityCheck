package org.aim42.htmlsanitycheck

import org.aim42.filesystem.FileUtil
import org.aim42.htmlsanitycheck.check.Checker
import org.aim42.htmlsanitycheck.check.CheckerCreator
import org.aim42.htmlsanitycheck.collect.PerRunResults
import org.aim42.htmlsanitycheck.collect.SinglePageResults
import org.aim42.htmlsanitycheck.html.HtmlPage
import org.aim42.htmlsanitycheck.report.ConsoleReporter
import org.aim42.htmlsanitycheck.report.HtmlReporter
import org.aim42.htmlsanitycheck.report.JUnitXmlReporter
import org.aim42.htmlsanitycheck.report.LoggerReporter
import org.aim42.htmlsanitycheck.report.Reporter
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * runs one or several checks on HTML input
 */
class ChecksRunner {

    // we check a collection of files:
    private Collection<File> filesToCheck

    // where do we put our results
    private File checkingResultsDir

    // where do we put our junit results
    private File junitResultsDir

	/** Determines if the report is output to the console. */
	boolean consoleReport = true

    // TODO: handle checking of external resources
    private Boolean checkExternalResources = false

    // checker instances
    private Set<Checker> checkers

    // keep all results
    private PerRunResults resultsForAllPages


    private static final Logger logger = LoggerFactory.getLogger(ChecksRunner.class);


    // convenience constructors, mainly  for tests
    // ------------------------------------------------

    // just ONE file to check and distinct directory
    public ChecksRunner(Set<Class> checkerCollection,
                        File fileToCheck,
                        File checkingResultsDir,
						File junitResultsDir) {
        this( checkerCollection, [fileToCheck], checkingResultsDir, junitResultsDir)
    }

    // with just ONE file to check
    public ChecksRunner(Set<Class> checkerCollection,
                        File fileToCheck ) {
        this( checkerCollection, [fileToCheck], fileToCheck.getParentFile())
    }

    // with ONE checker and ONE file and target directory
    public ChecksRunner( Class checkerCollection,
                         File fileToCheck,
                         File checkingResultsDir,
						 File junitResultsDir) {
        this( [checkerCollection], [fileToCheck], checkingResultsDir, junitResultsDir)
    }

    // with just ONE checker and ONE file...
    public ChecksRunner( Class checkerCollection,
                         File fileToCheck ) {
        this( [checkerCollection], [fileToCheck], fileToCheck.getParentFile())
    }

    // with ONE checker and a set of files...
    public ChecksRunner( Class checker,
                         SortedSet<File> filesToCheck ) {
        this( [checker], filesToCheck, File.createTempDir())
    }


    // standard constructor
    public ChecksRunner(
            Set<Class> checkerCollection,
            Collection<File> filesToCheck,
            File checkingResultsDir,
			File junitResultsDir
    ) {
        this.resultsForAllPages = new PerRunResults()

        this.filesToCheck = filesToCheck
        this.checkingResultsDir = checkingResultsDir
		this.junitResultsDir = junitResultsDir

		def params = [baseDirPath: FileUtil.commonPath(filesToCheck).canonicalPath]

        this.checkers = CheckerCreator.createCheckerClassesFrom( checkerCollection, params )

        logger.debug("ChecksRunner created with ${checkerCollection.size()} checkers for ${filesToCheck.size()} files")
    }

    /**
     *  performs all configured checks on a single HTML file.
     *
     *  Creates a {@link org.aim42.htmlsanitycheck.collect.SinglePageResults} instance to keep checking results.
     */
    public SinglePageResults performChecksForOneFile(File thisFile) {

        // the currently processed (parsed) HTML page
        HtmlPage pageToCheck = HtmlPage.parseHtml(thisFile)

        // initialize results for this page
        SinglePageResults collectedResults =
                new SinglePageResults(
                        pageFilePath: thisFile.canonicalPath,
                        pageFileName: thisFile.name,
                        pageTitle: pageToCheck.getDocumentTitle(),
                        pageSize: pageToCheck.documentSize
                )

        // apply every checker to this page
        // ToDo: parallelize with GPARS?
        checkers.each { checker ->
            def singleCheckResults = checker.performCheck(pageToCheck)
            collectedResults.addResultsForSingleCheck(singleCheckResults)
        }

        return collectedResults
    }

    /**
     * performs all configured checks on pageToCheck
     */
    public PerRunResults performChecks() {

        logger.debug "entered performChecks"

        filesToCheck.each { file ->
               resultsForAllPages.addPageResults(
                    performChecksForOneFile(file))
        }

        // after all checks, stop the timer...
        resultsForAllPages.stopTimer()

        // and then report the results
        reportCheckingResultsOnLogger()
        if (consoleReport) {
            reportCheckingResultsOnConsole()
        }
        if (checkingResultsDir) {
            reportCheckingResultsAsHTML(checkingResultsDir.absolutePath)
        }
		if (junitResultsDir) {
			reportCheckingResultsAsJUnitXml(junitResultsDir.absolutePath)			
		}

        return resultsForAllPages
    }


    /**
     * reports results on stdout
     * TODO:
     */
    private void reportCheckingResultsOnConsole() {
        Reporter reporter = new ConsoleReporter(resultsForAllPages)

        reporter.reportFindings()

    }

    /**
     * reports results to logger
     * TODO: report results to logger
     */
    private void reportCheckingResultsOnLogger() {
        Reporter reporter = new LoggerReporter(resultsForAllPages, logger)

        reporter.reportFindings()

    }

    /**
     * report results in HTML file(s)
     */
    private void reportCheckingResultsAsHTML(String resultsDir) {

        Reporter reporter = new HtmlReporter(resultsForAllPages, resultsDir)
        reporter.reportFindings()
    }

    /**
     * report results in JUnit XML
     */
    private void reportCheckingResultsAsJUnitXml(String resultsDir) {

        Reporter reporter = new JUnitXmlReporter(resultsForAllPages, resultsDir)
        reporter.reportFindings()
    }
}

/************************************************************************
 * This is free software - without ANY guarantee!
 *
 *
 * Copyright Dr. Gernot Starke, aim42.org
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

