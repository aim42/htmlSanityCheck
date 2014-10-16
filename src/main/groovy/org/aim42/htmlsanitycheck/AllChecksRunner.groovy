package org.aim42.htmlsanitycheck

import org.aim42.htmlsanitycheck.check.*
import org.aim42.htmlsanitycheck.collect.PerRunResults
import org.aim42.htmlsanitycheck.collect.SingleCheckResults
import org.aim42.htmlsanitycheck.collect.SinglePageResults
import org.aim42.htmlsanitycheck.html.HtmlPage
import org.aim42.htmlsanitycheck.report.ConsoleReporter
import org.aim42.htmlsanitycheck.report.HtmlReporter
import org.aim42.htmlsanitycheck.report.Reporter
import org.slf4j.Logger
import org.slf4j.LoggerFactory

// see end-of-file for license information
/**
 * Coordinates and runs all available html sanity checks.
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
    private Collection<File> filesToCheck

    // where do we put our results
    private File checkingResultsDir

    // TODO: handle checking of external resources
    private Boolean checkExternalResources = false

    // the checker instances
    private Checker missingImagesChecker
    private Checker undefinedCrossReferencesChecker
    private Checker duplicateIdChecker
    private Checker missingLocalResourcesChecker


    private HtmlPage pageToCheck

    // keep all results
    private PerRunResults resultsForAllPages


    private static Logger logger = LoggerFactory.getLogger(AllChecksRunner.class);

    /**
     * runs all available checks on the file
     *
     * @param fileToCheck all available checks are run against this file
     *        from this we can deduce the baseDirPath

     * @param
     * @param checkingResultsDir

     */

    public AllChecksRunner(
            Collection<File> filesToCheck,
            File checkingResultsDir,
            Boolean checkExternalResources
    ) {
        this.resultsForAllPages = new PerRunResults()

        this.filesToCheck           = filesToCheck
        this.checkingResultsDir     = checkingResultsDir
        this.checkExternalResources = checkExternalResources

        logger.info("AlLChecksRunner created")
    }



    /**
     * performs all available checks
     * on pageToCheck
     *
     * TODO: simplify checking... collect all checker instances in one collection,
     * then iteratively call it.performCheck() on those...
     */
    public PerRunResults performAllChecks() {

        logger.info "entered performAllChecks"

        filesToCheck.each { file ->

            resultsForAllPages.addPageResults(
                    performAllChecksForOneFile( file ))
        }


        // after all checks, stop the timer...
        resultsForAllPages.stopTimer()

        // and then report the results
        reportCheckingResultsOnConsole()
        reportCheckingResultsAsHTML( checkingResultsDir.absolutePath )
    }

    /**
     *  performs all known checks on a single HTML file.
     *
     *  Creates a {@link SinglePageResults} instance to keep checking results.
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
            addResultsForSingleCheck(missingImageFilesCheck( baseDir ))
            addResultsForSingleCheck(duplicateIdCheck())
            addResultsForSingleCheck(brokenCrossReferencesCheck())
            addResultsForSingleCheck(missingLocalResourcesCheck( baseDir ))
        }

        return resultsCollector
    }

    /**
     * reports results on stdout
     * TODO:
     */
    private void reportCheckingResultsOnConsole() {
        Reporter reporter = new ConsoleReporter( resultsForAllPages )

        reporter.reportFindings()

    }

    /**
     * report results in HTML file(s)
     */
    private void reportCheckingResultsAsHTML( String resultsDir ) {

        // TODO: handle file i/o issues, as creating the html output file can go wrong!!
        //
        Reporter reporter = new HtmlReporter( resultsForAllPages, resultsDir)
        reporter.reportFindings()
    }

    /**
     * check if the referenced image files exist
     *
     * This check is useful only for local images, where
     * the img-src attribute looks like src="images/test.jpg" or
     * src="file://image.jpg"
     */
    private SingleCheckResults missingImageFilesCheck(String baseDir) {

        missingImagesChecker = new MissingImageFilesChecker(
                pageToCheck: pageToCheck,
                baseDirPath: baseDir
        )

        return missingImagesChecker.performCheck()
        //logger.info imageCheckingResults.toString()
    }

    /**
     * checks for broken intra-document links (aka cross-references)
     */
    private SingleCheckResults brokenCrossReferencesCheck() {
        undefinedCrossReferencesChecker = new BrokenCrossReferencesChecker(
                pageToCheck: pageToCheck
        )

        return undefinedCrossReferencesChecker.performCheck()
    }

    /**
     * checks for duplicate definitions of id's (link-targets)
     */
    private SingleCheckResults duplicateIdCheck() {
        duplicateIdChecker = new DuplicateIdChecker(
                pageToCheck: pageToCheck
        )
        return duplicateIdChecker.performCheck()
    }

    /**
     * checks for missing local resources, e.g. download-files or
     * referenced local HTML files.
     */

    private SingleCheckResults missingLocalResourcesCheck(String baseDir) {
        missingLocalResourcesChecker = new MissingLocalResourcesChecker(
                pageToCheck: pageToCheck,
                baseDirPath: baseDir
        )
        return missingLocalResourcesChecker.performCheck()

    }

    /**
     * invokes the parser for the html page
     * @param input file
     */
    private static HtmlPage parseHtml(File fileToCheck) {
        assert fileToCheck.exists()
        return new HtmlPage(fileToCheck)
    }

    /**
     * runs the checks from the command
     * line with default settings...
     * @param args
     */
    public static void main(String[] args) {
        // TODO: read parameter from command line
    }


    @Override
    public String toString() {
        return "   file(s) to check : $fileToCheck\n" +
                "   put results in  : $checkingResultsDir\n" +
                "   base dir        : $baseDirPath\n";
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

