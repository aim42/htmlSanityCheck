// see end-of-file for license information

package org.aim42.htmlsanitycheck.checker

import org.aim42.htmlsanitycheck.html.HtmlElement
import org.aim42.htmlsanitycheck.html.HtmlPage
import org.junit.Test

class ImageFileExistCheckerTest extends GroovyTestCase {

    Checker checker
    HtmlPage htmlPage

    String userDir
    String fileName
    String localPath
    String filePath
    String imageDir


    public void setUp() {

        userDir = System.getProperty("user.dir")
        fileName = 'file-to-test.html'
        localPath = "/src/test/resources/"
        imageDir = userDir + localPath + "images/"
        filePath = userDir + localPath + fileName

    }

    @Test
    public void testCheckImageDirectoryExists() {
        assertTrue( "image directory $imageDir does not exist ",
                new File( imageDir ).exists())
    }

    @Test
    public void testCheckSingleImageWithMissingImage() {

        // make sure the generated file exists...
        assertTrue( "file $filePath  does NOT exist (but should!)",
                new File(filePath).exists())

        htmlPage = new HtmlPage( new File( filePath) )

        List<HtmlElement> images = htmlPage.getAllImageTags()
        assertEquals( "expected 2 images", 2, images.size())

        assertNotNull("htmlpage must not be null", htmlPage )

        checker = new ImageFileExistChecker(
                pageToCheck: htmlPage,
                baseDir: imageDir,
                headline: "Image File Exist Check",
                whatToCheck: "img links",
                sourceItemName: "img link",
                targetItemName: "image file")

        CheckingResultsCollector checkingResults =
                checker.check()

        int expected = 2
        int actual = checkingResults.nrOfItemsChecked

        assertEquals("expected $expected images, found $actual",
                expected, actual)

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
