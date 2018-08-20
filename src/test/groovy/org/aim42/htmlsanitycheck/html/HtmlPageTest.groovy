package org.aim42.htmlsanitycheck.html

import org.junit.Before
import org.junit.Test

// see end-of-file for license information

/**
 * test that important HTML elements are correctly parsed and extracted from {@link HtmlPage}.
 */

class HtmlPageTest extends GroovyTestCase {

    /**
     * this file resides in git - so we can
     * bake the filename into the tests
     */
    final static String FILENAME = 'file-to-test.html'

    /**
     * the local (relative) path to the test/resources directory
     * is fix - so we can hardcode the name into the tests
     */
    final static String LOCAL_PATH = "/src/test/resources/"

    final static String HTML_HEAD = """<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN"> <html><head></head>"""


    final String HTML_WITH_ONE_IMG_TAG =
            """$HTML_HEAD
              <body>
                   <h1>dummy-heading-1</h1>
                   <img src="images/test_xyz_uvw.jpg" >
              </body>
           </html>"""

    final String HTML_WITH_TWO_IMG_TAGS =
            """$HTML_HEAD
              <body>
                   <img src='test.jpg'/>
                   <img src='test.jpg'/>
              </body>
           </html>"""

    private File tmpFile
    private HtmlPage htmlPage


    @Before
    public void setUp() {
        tmpFile = File.createTempFile("testfile", "html")
    }

    @Test
    public void testStaticParseHtml() {
        tmpFile.write(HTML_WITH_TWO_IMG_TAGS)

        htmlPage = HtmlPage.parseHtml(tmpFile)

        // if parsing is successful, should yield two image tag!
        assertEquals("ONE image expected", 2, htmlPage.getAllImageTags().size())

    }


    @Test
    public void testGetTwoImagesFromHtml() {

        htmlPage = new HtmlPage(HTML_WITH_TWO_IMG_TAGS)

        ArrayList<HtmlElement> images = htmlPage.getAllImageTags()

        // should yield TWO image tags!
        assertEquals("TWO images expected", 2, images.size())


    }


    @Test
    public void testGetOneImageFromHtml() {

        htmlPage = new HtmlPage(HTML_WITH_ONE_IMG_TAG)

        ArrayList images = htmlPage.getAllImageTags()

        // should yield exactly ONE image tag!
        assertEquals("ONE image expected", 1, images.size())

    }

    @Test
    public void testGetOneImageFromHtmlFile() {
        tmpFile.write(HTML_WITH_ONE_IMG_TAG)

        htmlPage = new HtmlPage(tmpFile)

        ArrayList images = htmlPage.getAllImageTags()

        // should yield exactly ONE image tag!
        assertEquals("ONE image expected", 1, images.size())

    }

    @Test
    public void testGetTwoImagesFromHtmlFile() {
        tmpFile.write(HTML_WITH_TWO_IMG_TAGS)

        htmlPage = new HtmlPage(tmpFile)

        ArrayList images = htmlPage.getAllImageTags()

        assertEquals("two images expected", 2, images.size())

    }


    @Test
    public void testGetHtmlImgTagsFromFile() {
        String userDir = System.getProperty("user.dir")
        String filePath = userDir + LOCAL_PATH + FILENAME

        // make sure the generated file exists...
        assertTrue("file $filePath  does NOT exist (but should!)",
                new File(filePath).exists())

        htmlPage = new HtmlPage(new File(filePath))

        ArrayList images = htmlPage.getAllImageTags()
        assertEquals("expected 4 images", 4, images.size())

    }

    @Test
    public void testGetLocalAnchorHrefsFromHtml() {

        String HREF_ONE = "#aim42"
        String HREF_TWO = "#nonexisting"

        String HTML_WITH_A_TAGS_AND_ID = """ $HTML_HEAD
              <body>
                   <h1>dummy-heading-1</h1>
                   <a href="$HREF_ONE">link-to-aim42</a>
                   <h2 id="aim42">aim42 Architecture Improvement</h3>
                   <a href="$HREF_TWO">non-existing-link</a>
              </body>
           </html>"""

        htmlPage = new HtmlPage(HTML_WITH_A_TAGS_AND_ID)

        ArrayList anchors = htmlPage.getAllAnchorHrefs()

        assertEquals("two anchor hrefs expected", 2, anchors.size())

        assertEquals("href $HREF_ONE", HREF_ONE, anchors.first().getHrefAttribute())
        assertEquals("href $HREF_TWO", HREF_TWO, anchors.last().getHrefAttribute())

    }

    @Test
    public void testGetLocalAndRemoteAnchorsFromHtml() {
        String ONE = "downloads/aim42.pdf"
        String TWO = "File.docx"

        String HTML = """$HTML_HEAD<body>
                <h1>dummy-heading-1</h1>
            <a href="downloads/aim42.pdf">aim42.pdf</a>
            <a href="./downloads/aim42.pdf">aim42.pdf</a>
            <a href="$TWO">download</a>
        </body></html>
        """

        htmlPage = new HtmlPage(HTML)

        ArrayList anchors = htmlPage.getAllAnchorHrefs()

        assertEquals("three anchor hrefs expected", 3, anchors.size())

        assertEquals("href $ONE expected", ONE, anchors.first().getHrefAttribute())
        assertEquals("href $TWO expected", TWO, anchors.last().getHrefAttribute())

    }

/**
 * make sure we get the hrefs (id="XYZ") from html
 **/
    @Test
    public void testGetOneIdFromHtml() {

        String HREF_ONE = "aim42"

        String HTML_WITH_A_TAG_AND_ID = """$HTML_HEAD<body>
              <a href="#aim42">link-to-aim42</a>
              <h2 id="aim42">aim42 Architecture Improvement</h3>
          </body> </html>"""

        htmlPage = new HtmlPage(HTML_WITH_A_TAG_AND_ID)

        ArrayList bookmarks = htmlPage.getAllIds()

        // there's ONE id contained in the sample html
        assertEquals("only ONE id is expected in bookmark list", 1, bookmarks.size())

        assertEquals("id shall equal $HREF_ONE", HREF_ONE, bookmarks.first().getIdAttribute())

    }

    @Test
    public void testGetManyIdFromHtml() {

        String HREF_ONE = "aim42"

        String HTML_WITH_A_TAG_AND_ID = '''
           <html>
             $HTML_PREFIX
              <body>
                   <a href="#aim42">link-to-aim42</a>
                   <h2 id="aim42">aim42 Architecture Improvement</h3>
                   <h2 id="aim43">second heading</h3>
                   <h2 id="aim44">third heading</h3>
                  </body>
           </html>'''

        htmlPage = new HtmlPage(HTML_WITH_A_TAG_AND_ID)
        ArrayList bookmarks = htmlPage.getAllIds()

        // there's TWO ids contained in the sample html
        assertEquals("TWO ids expected in bookmark list", 3, bookmarks.size())
        assertEquals("id shall equal $HREF_ONE", HREF_ONE, bookmarks.first().getIdAttribute())
    }


    @Test
    public void testAnchorsToStringList() {
        String HTML_WITH_A_TAGS_AND_ID = '''
           <html>
             $HTML_PREFIX
              <body>
                   <h1>dummy-heading-1</h1>
                   <a href="#aim42">link-to-aim42</a>
                   <h2 id="aim42">aim42 Architecture Improvement</h3>
                   <a href="#nonexisting">non-existing-link</a>
              </body>
           </html>'''

        htmlPage = new HtmlPage(HTML_WITH_A_TAGS_AND_ID)
        ArrayList hrefs = htmlPage.getAllHrefStrings()

        assertEquals("expected [#aim42, #nonexisting]", ['#aim42', '#nonexisting'], hrefs)

    }

/*
 LocalResourceHrefs have one of the following forms:
   (1) <a href="file://path"> or
   (2) <a href="directory/file.ext">...
 */

    @Test
    public void testGetAllLocalResourceHrefStrings() {
        final String LOC1 = "file://path/filename.html"
        final String LOC2 = "dir/ectory/filename.pdf"

        String HTML = """${HTML_HEAD}<body>
             <h1>dummy-heading-1</h1>
             <a href="${LOC1}">a local file resource</a>
             <a href="${LOC2}">another local resource</a>
             <a href="#crossref">a cross reference</a>
        </body></html>"""

        htmlPage = new HtmlPage(HTML)
        ArrayList<String> hrefs = htmlPage.getAllHrefStrings()

        assertEquals("expected 3 href-strings", 3, hrefs.size())

        // now filter the local resources
        List<String> localHrefStrings = hrefs.findAll { hrefString ->
            URLUtil.isLocalResource(hrefString)
        }

        assertEquals("expected 2 local resources", 2, localHrefStrings.size())
    }


    @Test
    public void testGetManyIdStrings() {
        String HTML_WITH_A_TAGS_AND_ID = '''
           <html>
             $HTML_PREFIX
              <body>
                   <a href="#aim42">link-to-aim42</a>
                   <h2 id="aim42">aim42 Architecture Improvement</h3>
                   <h2 id="aim43">aim43 </h3>
                   <h2 id="aim44">aim44 </h3>
                   <h2 id="aim45">aim45 </h3>
                   <h2 id="aim46">aim46 </h3>
              </body>
           </html>'''

        htmlPage = new HtmlPage(HTML_WITH_A_TAGS_AND_ID)
        ArrayList ids = htmlPage.getAllIdStrings()

        def expected = ['aim42', 'aim43', 'aim44', 'aim45', 'aim46']
        assertEquals("expected $expected", expected, ids)
    }


    @Test
    public void testGetIdStringsAndAllIds() {
        String HTML_WITH_A_TAGS_AND_ID = """$HTML_HEAD<body>
                   <a href="#aim42">link-to-aim42</a>
                   <h2 id="aim42">aim42 Architecture Improvement</h3>
                   <h2 id="aim43">aim43 </h3>
                   <h2 id="aim44">aim44 </h3>
              </body>
           </html>"""

        htmlPage = new HtmlPage(HTML_WITH_A_TAGS_AND_ID)
        ArrayList idStrings = htmlPage.getAllIdStrings()

        def expectedIdStrings = ['aim42', 'aim43', 'aim44']
        assertEquals("expected $expectedIdStrings", expectedIdStrings, idStrings)

        ArrayList tagsWithId = htmlPage.getAllIds()
        assertEquals("expected 3 tags with ids", 3, tagsWithId.size())

    }

/**
 * Tests thousands of anchor elements
 *
 * Due to discussion on StackOverFlow (jsoup tag), the parser might be
 * restricted to about 4300 links/anchor elements - we verify our upper limit.
 * http://stackoverflow.com/questions/18573915/jsoup-finds-only-half-of-9000-a-tags-in-the-document
 * (
 */
    @Test
    public void testManyAnchorTags() {

        final int NR_OF_ANCHORS = 3000

        // create file with proper html content
        tmpFile = File.createTempFile("testfile", "html")
        tmpFile.write(HTML_HEAD)
        tmpFile.append("<body>")

        for (int i = 0; i < NR_OF_ANCHORS; i++) {
            tmpFile.append("<a href=\"#link$i\">link number $i</a>")
        }
        tmpFile.append("</body></html>")

        htmlPage = new HtmlPage(tmpFile)

        List<String> hrefs = htmlPage.getAllHrefStrings()

        // assert we find the correct number of anchors
        assertEquals("expected $NR_OF_ANCHORS anchors", NR_OF_ANCHORS, hrefs.size())

        String link
        // assert we find the correct anchors - all of them
        for (int i = 0; i < NR_OF_ANCHORS; i++) {
            link = "#link$i"
            assertTrue("expect $link in results, but wasn't", hrefs.contains(link))
        }
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

