// see end-of-file for license information

package org.aim42.htmlsanitycheck.check

import org.aim42.htmlsanitycheck.collect.SingleCheckResults
import org.aim42.htmlsanitycheck.html.HtmlElement
import org.aim42.htmlsanitycheck.html.HtmlPage
import org.junit.Before
import org.junit.Test
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class MissingImageFilesCheckerTest extends GroovyTestCase {

    Checker checker
    HtmlPage htmlPage

    String userDir
    String fileName
    String localPath
    String filePath
    String imagesDir

    SingleCheckResults checkingResults

    // logging stuff
    private final static Logger logger = LoggerFactory.getLogger(MissingImageFilesCheckerTest.class);


    @Before
    public void setUp() {

        userDir = System.getProperty("user.dir")
        fileName = 'file-to-test.html'
        localPath = "/src/test/resources/"
        //completePath = new File(userDir).absolutePath
        imagesDir = userDir + localPath
        filePath = userDir + localPath + fileName

        checkingResults = new SingleCheckResults()

        logger.debug( "imagesDir: $imagesDir")
    }


    @Test
    public void testCheckImageDirectoryExists() {
        assertTrue( "new image directory $imagesDir does not exist ",
                new File( imagesDir ).exists())
    }

    @Test
    public void testImageMustNotBeDirectory() {
        // we try to use userDir as an image...
        File userDirectory = new File( userDir )
        assertTrue( "userDir does not exist", userDirectory.exists())
        assertTrue(" userDir is no directory", userDirectory.isDirectory())

        String HTML_WITH_DIR_AS_IMAGE = """
           <html>
             <head></head>
              <body>
                   <h1 id=\"aim42\">dummy-heading-1</h1>
                   <img src=\"$userDir\">
              </body>
           </html>"""

        htmlPage = new HtmlPage(HTML_WITH_DIR_AS_IMAGE)

        List<HtmlElement> images = htmlPage.getAllImageTags()
        assertEquals( "expected 1 image", 1, images.size())


        checker = new MissingImageFilesChecker( baseDirPath: "")

        checkingResults = checker.performCheck( htmlPage )

        // checker must check one whatIsTheProblem
        int expected = 1
        int actual = checkingResults.nrOfItemsChecked

        assertEquals("expected $expected images, found $actual",
                expected, actual)



    }


    @Test
    public void testCheckSingleImageWithMissingImage() {

        // make sure the generated file exists...
        assertTrue( "file $filePath  does NOT exist (but should!)",
                new File(filePath).exists())

        htmlPage = new HtmlPage( new File( filePath) )

        List<HtmlElement> images = htmlPage.getAllImageTags()
        assertEquals( "expected 4 images", 4, images.size())

        assertNotNull("htmlpage must not be null", htmlPage )

        checker = new MissingImageFilesChecker( baseDirPath: imagesDir)

        checkingResults = checker.performCheck( htmlPage )

        int expected = 4
        int actual = checkingResults.nrOfItemsChecked

        assertEquals("expected $expected images, found $actual",
                expected, actual)

        // we have one problem in the file (missing image)
        expected = 2
        actual = checkingResults.nrOfProblems()

        assertEquals( "extected $expected finding, found $actual",
                expected, actual)

    }

    @Test
    public void testCheckAbsoluteImage() {
		File tempFolder = File.createTempDir()

		File imageFolder = new File(tempFolder, "images")
		assertTrue("Cannot create ${imageFolder}", imageFolder.mkdirs())
		new File(imageFolder, "bg.jpg").bytes = new byte[0]
		
		File htmlFile = new File(tempFolder, "index.html")
		htmlFile.text = """<html><head><title>absolute image test</title></head>
<body><p><img src="/images/bg.jpg" /></p></body>
</html>"""
		
        htmlPage = new HtmlPage( htmlFile )

        List<HtmlElement> images = htmlPage.getAllImageTags()
        assertEquals( "expected 1 image", 1, images.size())

        checker = new MissingImageFilesChecker( baseDirPath: tempFolder)

        checkingResults = checker.performCheck( htmlPage )

        int expected = 1
        int actual = checkingResults.nrOfItemsChecked

        assertEquals("expected $expected images, found $actual",
                expected, actual)

        expected = 0
        actual = checkingResults.nrOfProblems()

        assertEquals( "extected $expected finding, found $actual",
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
