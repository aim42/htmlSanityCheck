package org.aim42.htmlsanitycheck


import org.aim42.htmlsanitycheck.check.Checker
import org.aim42.htmlsanitycheck.check.CheckerCreator
import org.aim42.htmlsanitycheck.collect.PerRunResults
import org.aim42.htmlsanitycheck.collect.SinglePageResults
import org.aim42.htmlsanitycheck.html.HtmlPage
import org.aim42.htmlsanitycheck.report.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory

// see end-of-file for license information
/**
 * Coordinates and runs all available html sanity checks.  Convenience class,
 * delegates (most) work to an @see ChecksRunner
 * <p>
 * <ol>
 *     <li>parse the html file </li>
 *     <li>initialize and run image file checker </li>
 *     <li></li>
 *     <li></li>
 * </ol>
 * <p>
 * Uses @see Checker instances (they implement the
 * <a href="http://en.wikipedia.org/wiki/Template_method_pattern">
 * template pattern</a>)
 **/

class AllChecksRunner {

    // we check a collection of files:
    private Collection<File> filesToCheck = new HashSet<File>()

    // where do we put our results
    private File checkingResultsDir

    // where do we put our junit results
    private File junitResultsDir

    /** Determines if the report is output to the console. */
    boolean consoleReport = true

    // checker instances
    private Set<Checker> checkers

    // keep all results
    private PerRunResults resultsForAllPages


    // keep your own configuration (in case we have multiple parallel instances running...)
    private Configuration myConfig


    private static final Logger logger = LoggerFactory.getLogger(AllChecksRunner.class);

    /**
     * runs all available checks
     *
     */

    public AllChecksRunner( Configuration pConfig ) {
        super()

        myConfig = pConfig

        this.filesToCheck = myConfig.getConfigItemByName(Configuration.ITEM_NAME_sourceDocuments)

        // TODO: #185 (checker classes shall be detected automatically (aka CheckerFactory)
        // CheckerFactory needs the configuration
        Set<Class> checkerClasses = myConfig.getConfigItemByName(Configuration.ITEM_NAME_checksToExecute)
        this.checkers = CheckerCreator.createCheckerClassesFrom( checkerClasses, myConfig )

        this.resultsForAllPages = new PerRunResults()

        this.checkingResultsDir = myConfig.getConfigItemByName(Configuration.ITEM_NAME_checkingResultsDir)
        this.junitResultsDir = myConfig.getConfigItemByName(Configuration.ITEM_NAME_junitResultsDir)

        logger.debug("AllChecksRunner created with ${this.checkers.size()} checkers for ${filesToCheck.size()} files")
    }



    /**
     * performs all available checks on pageToCheck
     *
     */
    public PerRunResults performAllChecks() {

        logger.debug "entered performAllChecks"

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
     *  performs all configured checks on a single HTML file.
     *
     *  Creates a {@link org.aim42.htmlsanitycheck.collect.SinglePageResults} instance to keep checking results.
     */
    protected SinglePageResults performChecksForOneFile(File thisFile) {

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
            def singleCheckResults = checker.performCheck(pageToCheck )
            collectedResults.addResultsForSingleCheck(singleCheckResults)
        }

        return collectedResults
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
    /**
     * runs the checks from the command
     * line with default settings...
     * @param args
     */
    public static void main(String[] args) {
        // TODO: read parameter from command line

        // empty method is currently marked "prio-2" issue by CodeNarc... we'll ignore that
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

