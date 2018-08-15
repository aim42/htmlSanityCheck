package org.aim42.htmlsanitycheck.collect

import org.aim42.htmlsanitycheck.check.Checker
import org.aim42.htmlsanitycheck.check.MissingImageFilesChecker
import org.aim42.htmlsanitycheck.html.HtmlElement
import org.aim42.htmlsanitycheck.html.HtmlPage
import org.junit.Test

// see end-of-file for license information


class SingleCheckResultsTest extends GroovyTestCase {

    final String whatIsCheckedMessage = "A Headline for Testing Purpose"

    final String localPath = "/src/test/resources"

    String imageDir

    SingleCheckResults checkingResults

    Checker checker

    public void setUp() {
        checkingResults =
                new SingleCheckResults(
                        whatIsChecked: whatIsCheckedMessage
                )

        // not working in IntelliJ multi-module setup
        // imagesDir = System.getProperty("user.dir") + localPath

        imageDir = new File(".").getCanonicalPath() + localPath


    }

    @Test
    public void testCheckingResultConstruction() {
        String expected = "empty checkingResult shall have "

        assertEquals(expected + "proper whatIsChecked",
                whatIsCheckedMessage, checkingResults.whatIsChecked)

        assertEquals(expected + "zero findings",
                0, checkingResults.nrOfProblems())


    }

    @Test
    public void testAddManyFindingToCheckingResult() {
        int maxNrOfFindinds = 2

        for (int i = 1; i < maxNrOfFindinds; i++) {
            checkingResults.addFinding(new Finding("googlygoob + $i"))
            assertEquals("$i nr of findings expected", i, checkingResults.nrOfProblems())
        }
    }

    @Test
    public void testAddFindingToCheckingResult() {
        checkingResults.addFinding(new Finding("googlygoob"))

        String expected = "One finding expected"
        assertEquals(expected, 1, checkingResults.nrOfProblems())
    }

    @Test
    public void testCheckTwoImageTagsOneMissingFile() {

        String oneGoodOneBadImageHTML = """<html><body>
                   <h1>dummy-heading-1</h1>
                   <img src="./images/aim42.png" >
                   <img src="./images/test_xyz_uvw.jpg" >
              </body></html>"""

        HtmlPage htmlPage = new HtmlPage(oneGoodOneBadImageHTML)

        List<HtmlElement> images = htmlPage.getAllImageTags()
        assertEquals("expected 2 images", 2, images.size())

        checker = new MissingImageFilesChecker( baseDirPath: imageDir)

        SingleCheckResults checkingResults = checker.performCheck( htmlPage )


        int expected = 2
        int actual = checkingResults.nrOfItemsChecked

        assertEquals("expected $expected images, found $actual",
                expected, actual)

        // one file is missing, should yield ONE finding
        expected = 1
        actual = checkingResults.nrOfProblems()

        assertEquals("expected $expected findings, found $actual",
                expected, actual)

        // we expect the finding to be ./images/test_xyz_uvw.jpg
        String expectedImageSrc = "image \"./images/test_xyz_uvw.jpg\" missing"
        String actualFinding = checkingResults.findings.first().whatIsTheProblem

        assertEquals("expected \"/images/test_xyz_uvw.jpg\" as finding",
                expectedImageSrc, actualFinding)


    }

}

/*======================================================================
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
 ======================================================================*/


