package org.aim42.htmlsanitycheck

// see end-of-file for license information

import org.aim42.htmlsanitycheck.checker.Checker
import org.aim42.htmlsanitycheck.checker.CheckingResultsCollector
import org.aim42.htmlsanitycheck.checker.DuplicateIdChecker
import org.aim42.htmlsanitycheck.checker.ImageFileExistChecker
import org.aim42.htmlsanitycheck.checker.InternalLinksChecker

import org.aim42.htmlsanitycheck.html.HtmlPage

/**
 * Coordinates and runs all available html sanity checks.
 * <p>
 * <ol>
 *     <li>parse the html file </li>
 *     <li>initialize image file checker </li>
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
    private  File fileToCheck

    // where do we put our results
    private File checkingResultsDir

    // where do we expect images?
    private File imageDir

    // TODO: handle checking of external resources
    private  Boolean checkExternalResources = false


    private Checker imageChecker
    private Checker undefinedInternalLinksChecker
    private Checker duplicateIdChecker

    private CheckingResultsCollector imageCheckingResults
    private CheckingResultsCollector internalLinkCheckingResults
    private CheckingResultsCollector duplicateIdsResults



    private HtmlPage pageToCheck

    /**
     * runs all available checks on the file with name "fileName",
     * contained in the directory "docDirPath". Images are expected in
     * imageDirPath.
     *
     * @param checkingResultsDir
     * @param fileToCheck
     * @param imagesDir
     */

    public AllChecksRunner(
            File fileToCheck,
            File imageDir,
            File checkingResultsDir,
            Boolean checkExternalResources
            ) {
        this.fileToCheck = fileToCheck
        this.checkingResultsDir = checkingResultsDir
        this.imageDir = imageDir
        this.checkExternalResources = checkExternalResources

    }



    /**
     * performs all available checks
     * on pageToCheck
     */
    public void performChecks( Boolean verbose ) {

        if (verbose) {
            println "\nrunning ImageCheck"
            runImageCheck()
        }

        runDuplicateIdCheck( )
        runInternalLinkCheck()
    }



    public void runImageCheck() {
        imageChecker = new ImageFileExistChecker(
                pageToCheck: pageToCheck,
                baseDir: imageDir,
                headline: "Image File Exist Check",
                whatToCheck: "img links",
                sourceItemName: "img link",
                targetItemName: "image file")

        imageCheckingResults = imageChecker.check()
        println imageCheckingResults.toString()
    }


    public void runInternalLinkCheck() {
        undefinedInternalLinksChecker = new InternalLinksChecker(
                pageToCheck: pageToCheck,
                headline: "Undefined Internal Links Check",
                whatToCheck: "matching id\'s for hrefs",
                sourceItemName: "href",
                targetItemName: "id" )

        internalLinkCheckingResults = undefinedInternalLinksChecker.check()

        println internalLinkCheckingResults.toString()
    }


    public void runDuplicateIdCheck() {
        duplicateIdChecker = new  DuplicateIdChecker(
                pageToCheck: pageToCheck,
                headline: "Duplicate Ids Check",
                whatToCheck: "multiple definition of id\'s",
                sourceItemName: "id",
                targetItemName: "id"
        )
        duplicateIdsResults = duplicateIdChecker.check()

        println duplicateIdsResults.toString()
    }



    /**
     * reads the html page
     */
    private void parseHtml() {
        assert fileToCheck.exists()
        pageToCheck = new HtmlPage( fileToCheck )
    }



    /**
     * runs the checks from the command
     * line with default settings...
     * @param args
     */
    public static void main(String[] args) {
        // TODO: read parameter from command line
        AllChecksRunner allChecksRunner = new AllChecksRunner()

        allChecksRunner.fileToCheck = new File( "file-to-test.html")
        //allChecksRunner.docDirPath = System.getProperty("user.dir") + "/src/test/resources/"
        //allChecksRunner.pathToHtmlFile = docDirPath + fileName
        //allChecksRunner.imageDirPath = docDirPath

        // TODO: main-method is completely broken!!
        allChecksRunner.parseHtml()

        allChecksRunner.runImageCheck()
        allChecksRunner.runInternalLinkCheck()
        allChecksRunner.runDuplicateIdCheck()

    }

    @Override
    public String toString() {
        return "AllChecksRunner{\n" +
                "   which file to check  : " + fileToCheck +
                "   where to put results : " + checkingResultsDir +
                "   where to find images : " + imageDir   +
                '\n}';
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

