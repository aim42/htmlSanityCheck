package org.aim42.htmlsanitycheck

import org.aim42.htmlsanitycheck.checker.*

// see end-of-file for license information
import org.aim42.htmlsanitycheck.html.HtmlPage
import org.aim42.htmlsanitycheck.reporter.FindingsConsoleReporter
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
    private Checker imageChecker
    private Checker undefinedInternalLinksChecker
    private Checker duplicateIdChecker

    // collections for the results
    private CheckingResultsCollector imageCheckingResults
    private CheckingResultsCollector internalLinkCheckingResults
    private CheckingResultsCollector duplicateIdsCheckingResults

    // our input html
    private HtmlPage pageToCheck

    // logging stuff
    private static Logger logger = LoggerFactory.getLogger(AllChecksRunner.class);

    /**
     * runs all available checks on the file
     *
     * @param fileToCheck all available checks are run against this file
     * TODO: enhance to fileSet

     * @param imagesDir : images are expected here
     * @param checkingResultsDir

     */

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

        logger.info("AlLChecksRunner created")

    }

    /**
     * performs all available checks
     * on pageToCheck
     */
    public void performAllChecks() {

        logger.info(this.toString())

        pageToCheck = parseHtml()

        // the actual checks
        runImageFileExistCheck()
        runDuplicateIdCheck()
        runInternalLinkCheck()

        reportCheckingResultsOnConsole()
    }

    /**
     * reports results on stdout
     */
    public void reportCheckingResultsOnConsole() {
        def results = new ArrayList<CheckingResultsCollector>( Arrays.asList(
                        imageCheckingResults,
                        internalLinkCheckingResults,
                        duplicateIdsCheckingResults ))

        logger.info "results = " + results

        new FindingsConsoleReporter(results).reportFindings()

        //reporter.reportFindings()

    }

    /**
     * check if the referenced image files exist
     *
     * This check is useful only for local images, where
     * the img-src attribute looks like src="images/test.jpg" or
     * src="file://image.jpg"
     */
    public void runImageFileExistCheck() {
        // from gradle we get a File object for imageDirPath
        imageChecker = new MissingImageFilesChecker(
                pageToCheck: pageToCheck,
                baseDirPath: baseDirPath
              )

        imageCheckingResults = imageChecker.performCheck()
        logger.info imageCheckingResults.toString()
    }


    public void runInternalLinkCheck() {
        undefinedInternalLinksChecker = new BrokenInternalLinksChecker(
                pageToCheck: pageToCheck
        )

        internalLinkCheckingResults = undefinedInternalLinksChecker.performCheck()

        logger.info internalLinkCheckingResults.toString()
    }


    public void runDuplicateIdCheck() {
        duplicateIdChecker = new DuplicateIdChecker(
                pageToCheck: pageToCheck
        )
        duplicateIdsCheckingResults = duplicateIdChecker.performCheck()

        logger.info duplicateIdsCheckingResults.toString()
    }

    /**
     * reads the html page
     */
    private HtmlPage parseHtml() {
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
        AllChecksRunner allChecksRunner = new AllChecksRunner()

        allChecksRunner.fileToCheck = new File("file-to-test.html")
        //allChecksRunner.docDirPath = System.getProperty("user.dir") + "/src/test/resources/"
        //allChecksRunner.pathToHtmlFile = docDirPath + fileName
        //allChecksRunner.imageDirPath = docDirPath

        // TODO: main-method is completely broken!!
        allChecksRunner.parseHtml()

        allChecksRunner.runImageFileExistCheck()
        allChecksRunner.runInternalLinkCheck()
        allChecksRunner.runDuplicateIdCheck()

    }

    @Override
    public String toString() {
        return "   file to check : $fileToCheck\n" +
                "   put results   : $checkingResultsDir\n" +
                "   images dir    : $imagesDir\n";
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

