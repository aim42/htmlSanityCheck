package org.aim42.htmlsanitycheck

import org.aim42.htmlsanitycheck.checker.Checker
import org.aim42.htmlsanitycheck.checker.CheckingResultsCollector
import org.aim42.htmlsanitycheck.checker.DuplicateIdChecker
import org.aim42.htmlsanitycheck.checker.ImageFileExistChecker
import org.aim42.htmlsanitycheck.checker.InternalLinksChecker
import org.aim42.htmlsanitycheck.html.HtmlPage

// see end-of-file for license information

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

    static Checker imageChecker
    static Checker undefinedInternalLinksChecker
    static Checker duplicateIdChecker

    static CheckingResultsCollector imageCheckingResults
    static CheckingResultsCollector internalLinkCheckingResults
    static CheckingResultsCollector duplicateIdsResults

    static String fileName
    static String docDirPath
    static String pathToHtmlFile
    static String imageDirPath


    private HtmlPage pageToCheck


    public AllChecksRunner(
            String docDirPath,
            String fileName,
            String imageDirPath) {
        this.pathToHtmlFile =  docDirPath + fileName
        this.fileName = fileName
        this.imageDirPath = imageDirPath
    }


    public AllChecksRunner() {

        this.fileName = "index.html"
        this.docDirPath = System.getProperty("user.dir") + "/build/docs/"

        this.imageDirPath = docDirPath + "images/";
    }

    public void runImageCheck() {
        imageChecker = new ImageFileExistChecker(
                pageToCheck: pageToCheck,
                baseDir: imageDirPath,
                headline: "Image File Exist Check",
                name: "img links",
                sourceItemName: "img link",
                targetItemName: "image file")

        imageCheckingResults = imageChecker.check()
        println imageCheckingResults.toString()
    }

    public void runInternalLinkCheck() {
        undefinedInternalLinksChecker = new InternalLinksChecker(
                pageToCheck: pageToCheck,
                headline: "Undefined Internal Links Check",
                name: "matching id\'s for hrefs",
                sourceItemName: "href",
                targetItemName: "id" )

        internalLinkCheckingResults = undefinedInternalLinksChecker.check()

        println internalLinkCheckingResults.toString()
    }


    public void runDuplicateIdCheck() {
        duplicateIdChecker = new  DuplicateIdChecker(
                pageToCheck: pageToCheck,
                headline: "Duplicate Ids Check",
                name: "multiple definition of id\'s",
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
        pageToCheck = new HtmlPage( new File( pathToHtmlFile ))
    }

    /**
     * runs the checks from the command
     * line with default settings...
     * @param args
     */
    public static void main(String[] args) {
        // TODO: read parameter from command line
        AllChecksRunner allChecksRunner = new AllChecksRunner()

        allChecksRunner.fileName = "file-to-test.html"
        allChecksRunner.docDirPath = System.getProperty("user.dir") + "/src/test/resources/"
        allChecksRunner.pathToHtmlFile = docDirPath + fileName
        allChecksRunner.imageDirPath = docDirPath

        allChecksRunner.parseHtml()

        allChecksRunner.runImageCheck()
        allChecksRunner.runInternalLinkCheck()
        allChecksRunner.runDuplicateIdCheck()

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

