package org.aim42.htmlsanitycheck

import org.aim42.htmlsanitycheck.check.*
import org.aim42.htmlsanitycheck.collect.PerRunResults
import org.aim42.htmlsanitycheck.collect.SingleCheckResults
import org.aim42.htmlsanitycheck.collect.SinglePageResults

// see end-of-file for license information
import org.aim42.htmlsanitycheck.html.HtmlPage
import org.aim42.htmlsanitycheck.report.FindingsConsoleReporter
import org.slf4j.Logger
import org.slf4j.LoggerFactory

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

    // we process one single input file
    private File fileToCheck

    // our base directory: image-links within HTML are supposed to be relative to this
    private String baseDirPath

    // where do we expect images?
    // (later we need to convert this to String)
    private File imagesDir

    // where do we put our results
    private File checkingResultsDir

    // TODO: handle checking of external resources
    private Boolean checkExternalResources = false

    // the checker instances
    private Checker missingImagesChecker
    private Checker undefinedCrossReferencesChecker
    private Checker duplicateIdChecker
    private Checker missingLocalResourcesChecker


    // collections for the results
    private SingleCheckResults imageCheckingResults
    private SingleCheckResults crossReferencesCheckingResults
    private SingleCheckResults duplicateIdsCheckingResults
    private SingleCheckResults missingLocalResourcesCheckingResults


    private HtmlPage pageToCheck

    // keep all results
    private PerRunResults  resultsForAllPages

    private static Logger logger = LoggerFactory.getLogger(AllChecksRunner.class);

    /**
     * runs all available checks on the file
     *
     * @param fileToCheck all available checks are run against this file
     *        from this we can deduce the baseDirPath
     * TODO: enhance to fileSet

     * @param
     * @param checkingResultsDir

     */

    // TODO: remove imagesDir
    public AllChecksRunner(
            File fileToCheck,
            File imagesDir,
            File checkingResultsDir,
            Boolean checkExternalResources
    ) {
        this.fileToCheck = fileToCheck
        this.checkingResultsDir = checkingResultsDir
        this.imagesDir = imagesDir
        this.checkExternalResources = checkExternalResources

        this.baseDirPath = fileToCheck.getParent()

        this.resultsForAllPages = new PerRunResults()

        logger.info("AlLChecksRunner created")
    }

    public AllChecksRunner( File fileToCheck ) {
        this(
            fileToCheck,
            fileToCheck.parent,
            fileToCheck.parent,
            false)
    }

    /**
     * performs all available checks
     * on pageToCheck
     * TODO: enhance to support FileSet instead of just one file
     */
    public PerRunResults performAllChecks() {

        //logger.info(this.toString())

        // TODO: this works for just ONE file...
        resultsForAllPages.addPageResults(
                performAllChecksForOneFile( fileToCheck ))

        //
        // reportCheckingResultsOnConsole()
    }


    /**
     *  performs all known checks on a single HTML file.
     *
     *  Creates a {@link SinglePageResults} instance to keep checking results.
     */
    public SinglePageResults performAllChecksForOneFile( File fileToCheck ) {

        pageToCheck = parseHtml( fileToCheck )

        // TODO: to handle #15 and #30 (enhancement for FileSet)
        SinglePageResults resultsCollector =
                new SinglePageResults(
                        pageFileName: fileToCheck.canonicalPath.toString(),
                        pageToCheck: pageToCheck.getDocumentTitle(),
                        pageSize:    pageToCheck.documentSize
                )

        // the actual checks
        resultsCollector.addResultsForSingleCheck( missingImageFilesCheck() )

        duplicateIdCheck()
        brokenCrossReferencesCheck()
        missingLocalResourcesCheck()

        return resultsCollector
    }


    /**
     * reports results on stdout
     * TODO: this is now completely broken - FIXME
     */
    private void reportCheckingResultsOnConsole() {
       /* def results = new ArrayList<SingleCheckResults>( Arrays.asList(
                        imageCheckingResults,
                        crossReferencesCheckingResults,
                        duplicateIdsCheckingResults,
                        missingLocalResourcesCheckingResults ))

        logger.info "results = " + results

        new FindingsConsoleReporter(results).reportFindings()
*/
        //reporter.reportFindings()

    }

    /**
     * check if the referenced image files exist
     *
     * This check is useful only for local images, where
     * the img-src attribute looks like src="images/test.jpg" or
     * src="file://image.jpg"
     */
    private SingleCheckResults missingImageFilesCheck() {

        missingImagesChecker = new MissingImageFilesChecker(
                pageToCheck: pageToCheck,
                baseDirPath: baseDirPath
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

    private SingleCheckResults missingLocalResourcesCheck() {
        missingLocalResourcesChecker = new MissingLocalResourcesChecker(
                pageToCheck: pageToCheck,
                baseDirPath: baseDirPath
        )
        return missingLocalResourcesChecker.performCheck()

    }

    /**
     * invokes the parser for the html page
     * @param input file
     */
    private static HtmlPage parseHtml( File fileToCheck ) {
        assert fileToCheck.exists()
        return new HtmlPage( fileToCheck )
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

