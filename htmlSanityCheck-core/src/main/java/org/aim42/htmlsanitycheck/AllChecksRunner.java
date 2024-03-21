package org.aim42.htmlsanitycheck;

import org.aim42.htmlsanitycheck.check.Checker;
import org.aim42.htmlsanitycheck.check.CheckerCreator;
import org.aim42.htmlsanitycheck.collect.PerRunResults;
import org.aim42.htmlsanitycheck.collect.SingleCheckResults;
import org.aim42.htmlsanitycheck.collect.SinglePageResults;
import org.aim42.htmlsanitycheck.html.HtmlPage;
import org.aim42.htmlsanitycheck.report.ConsoleReporter;
import org.aim42.htmlsanitycheck.report.HtmlReporter;
import org.aim42.htmlsanitycheck.report.JUnitXmlReporter;
import org.aim42.htmlsanitycheck.report.LoggerReporter;
import org.aim42.htmlsanitycheck.report.Reporter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Coordinates and runs all available html sanity checks.  Convenience class,
 * delegates (most) work to a @see ChecksRunner
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

public class AllChecksRunner {

    // we check a collection of files:
    private final Set<File> filesToCheck;

    // where do we put our results?
    private final File checkingResultsDir;

    // where do we put our junit results?
    private final File junitResultsDir;

    // checker instances
    private final List<Checker> checkers;

    // keep all results
    private final PerRunResults resultsForAllPages;

    private static final Logger logger = LoggerFactory.getLogger(AllChecksRunner.class);

    /**
     * runs all available checks
     *
     */

    public AllChecksRunner(Configuration configuration) {
        super();

        this.filesToCheck = configuration.getSourceDocuments();

        // TODO: #185 (checker classes shall be detected automatically (aka CheckerFactory)
        // CheckerFactory needs the configuration
        List<Class<? extends Checker>> checkerClasses = configuration.getChecksToExecute();
        this.checkers = CheckerCreator.createCheckerClassesFrom(checkerClasses, configuration);

        this.resultsForAllPages = new PerRunResults();

        this.checkingResultsDir = configuration.getCheckingResultsDir();
        this.junitResultsDir = configuration.getJunitResultsDir();

        logger.debug("AllChecksRunner created with " + this.checkers.size() + " checkers for " + filesToCheck.size() + " files");
    }

    /**
     * Performs all available checks on pageToCheck
     *
     */
    public PerRunResults performAllChecks() throws IOException {

        logger.debug("entered performAllChecks");

        for (File file : filesToCheck) {
            resultsForAllPages.addPageResults(
                    performChecksForOneFile(file));
        }

        // after all checks, stop the timer...
        resultsForAllPages.stopTimer();

        // and then report the results
        reportCheckingResultsOnLogger();
        /* Determines if the report is output to the console. */
        reportCheckingResultsOnConsole();
        if (checkingResultsDir != null) {
            reportCheckingResultsAsHTML(checkingResultsDir.getAbsolutePath());
        }
        if (junitResultsDir != null) {
            reportCheckingResultsAsJUnitXml(junitResultsDir.getAbsolutePath());
        }

        return resultsForAllPages;

    }

    /**
     *  Performs all configured checks on a single HTML file.
     * <p>
     *  Creates a {@link org.aim42.htmlsanitycheck.collect.SinglePageResults} instance to keep checking results.
     */
    protected SinglePageResults performChecksForOneFile(File thisFile) throws IOException {

        // the currently processed (parsed) HTML page
        HtmlPage pageToCheck = HtmlPage.parseHtml(thisFile);

        // initialize results for this page
        SinglePageResults collectedResults =
                new SinglePageResults(
                        thisFile.getName(),
                        thisFile.getCanonicalPath(),
                        pageToCheck.getDocumentTitle(),
                        pageToCheck.getDocumentSize(),
                        new ArrayList<>()
                );

        // apply every checker to this page
        // ToDo: parallelize with GPARS?

        for (Checker checker : checkers) {
            SingleCheckResults singleCheckResults = checker.performCheck(pageToCheck);
            collectedResults.addResultsForSingleCheck(singleCheckResults);
        }

        return collectedResults;
    }

    /**
     * reports results on stdout
     * TODO:
     */
    private void reportCheckingResultsOnConsole() {
        Reporter reporter = new ConsoleReporter(resultsForAllPages);

        reporter.reportFindings();

    }

    /**
     * reports results to logger
     * TODO: report results to logger
     */
    private void reportCheckingResultsOnLogger() {
        Reporter reporter = new LoggerReporter(resultsForAllPages, logger);

        reporter.reportFindings();

    }

    /**
     * Report results in HTML file(s)
     */
    private void reportCheckingResultsAsHTML(String resultsDir) {

        Reporter reporter = new HtmlReporter(resultsForAllPages, resultsDir);
        reporter.reportFindings();
    }

    /**
     * Report results in JUnit XML
     */
    private void reportCheckingResultsAsJUnitXml(String resultsDir) {

        Reporter reporter = new JUnitXmlReporter(resultsForAllPages, resultsDir);
        reporter.reportFindings();
    }

    /**
     * Runs the checks from the command
     * line with default settings...
     */
    public static void main(String[] args) {
        // TODO: read parameter from command line

        // Empty method is currently marked "prio-2" issue by CodeNarc... we'll ignore that
    }
}