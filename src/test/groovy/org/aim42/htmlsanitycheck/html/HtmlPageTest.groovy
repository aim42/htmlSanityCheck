package org.aim42.htmlsanitycheck.html

import org.junit.Test

// see end-of-file for license information


class HtmlPageTest extends GroovyTestCase {

    /**
     * this file resides in git - so we can
     * hardcode the filename into the tests
     */
    final String fileName = 'file-to-test.html'

    /**
     * the local (relative) path to the test/resources directory
     * is fix - so we can hardcode the name into the tests
     */
    final String localPath = "/src/test/resources/"


    final String HTML_WITH_IMG_TAG =
            '''
           <html>
             $HTML_HEAD
              <body>
                   <h1>dummy-heading-1</h1>
                   <img src="images/test_xyz_uvw.jpg" >
              </body>
           </html>'''

    final String HTML_WITH_TWO_IMG_TAGS =
            '''
           <html>
             $HTML_HEAD
              <body>
                   <img src='test.jpg'/>
                   <img src='test.jpg'/>
              </body>
           </html>'''

    File tmpFile


    public void setUp() {
        tmpFile = File.createTempFile("testfile", "html")
    }


    @Test
    public void testGetTwoImagesFromHtml() {

        HtmlPage htmlpage = new HtmlPage(HTML_WITH_TWO_IMG_TAGS)

        ArrayList<HtmlElement> images = htmlpage.getAllImageTags()

        // should yield TWO image tags!
        assertEquals("TWO images expected", 2, images.size())


    }


    @Test
    public void testGetOneImageFromHtml() {

        HtmlPage htmlpage = new HtmlPage(HTML_WITH_IMG_TAG)

        ArrayList images = htmlpage.getAllImageTags()

        // should yield exactly ONE image tag!
        assertEquals("ONE image expected", 1, images.size())

    }

    @Test
    public void testGetOneImageFromHtmlFile() {
        tmpFile.write(HTML_WITH_IMG_TAG)

        HtmlPage htmlpage = new HtmlPage(tmpFile)

        ArrayList images = htmlpage.getAllImageTags()

        // should yield exactly ONE image tag!
        assertEquals("ONE image expected", 1, images.size())

    }

    @Test
    public void testGetTwoImagesFromHtmlFile() {
        tmpFile.write(HTML_WITH_TWO_IMG_TAGS)

        HtmlPage htmlpage = new HtmlPage(tmpFile)

        ArrayList images = htmlpage.getAllImageTags()

        // should yield exactly ONE image tag!
        assertEquals("ONE image expected", 2, images.size())

    }


    @Test
    public void testGetHtmlImgTagsFromFile() {
        String userDir = System.getProperty("user.dir")
        String filePath = userDir + localPath + fileName

        // make sure the generated file exists...
        assertTrue("file $filePath  does NOT exist (but should!)",
                new File(filePath).exists())

        HtmlPage htmlPage = new HtmlPage(new File(filePath))

        ArrayList images = htmlPage.getAllImageTags()
        assertEquals("expected 2 images", 2, images.size())

    }

    @Test
    public void testGetAnchorHrefsFromHtml() {

        String HREF_ONE = "aim42"
        String HREF_TWO = "nonexisting"

        String HTML_WITH_A_TAGS_AND_ID = '''
           <html>
             $HTML_HEAD
              <body>
                   <h1>dummy-heading-1</h1>
                   <a href="#aim42">link-to-aim42</a>
                   <h2 id="aim42">aim42 Architecture Improvement</h3>
                   <a href="#nonexisting">non-existing-link</a>
              </body>
           </html>'''

        HtmlPage htmlPage = new HtmlPage(HTML_WITH_A_TAGS_AND_ID)

        ArrayList anchors = htmlPage.getAllAnchorHrefs()

        assertEquals("two anchor hrefs expected", 2, anchors.size())

        assertEquals("href $HREF_ONE", HREF_ONE, anchors.first().getHrefAttribute())
        assertEquals("href $HREF_TWO", HREF_TWO, anchors.last().getHrefAttribute())

    }

    /**
     * make sure we get the hrefs (id="XYZ") from html
     **/
    @Test
    public void testGetOneIdFromHtml() {

        String HREF_ONE = "aim42"
        String HREF_TWO = "nonexisting"

        String HTML_WITH_A_TAG_AND_ID = '''
           <html>
             $HTML_HEAD
              <body>
                   <a href="#aim42">link-to-aim42</a>
                   <h2 id="aim42">aim42 Architecture Improvement</h3>
              </body>
           </html>'''

        HtmlPage htmlPage = new HtmlPage(HTML_WITH_A_TAG_AND_ID)

        ArrayList bookmarks = htmlPage.getAllIds()

        // there's ONE id contained in the sample html
        assertEquals( "only ONE id is expected in bookmark list", 1, bookmarks.size())

        assertEquals( "id shall equal $HREF_ONE", HREF_ONE, bookmarks.first().getIdAttribute())

    }

    @Test
    public void testGetManyIdFromHtml() {

        String HREF_ONE = "aim42"

        String HTML_WITH_A_TAG_AND_ID = '''
           <html>
             $HTML_HEAD
              <body>
                   <a href="#aim42">link-to-aim42</a>
                   <h2 id="aim42">aim42 Architecture Improvement</h3>
                   <h2 id="aim43">second heading</h3>
                   <h2 id="aim44">third heading</h3>
                  </body>
           </html>'''

        HtmlPage htmlPage = new HtmlPage(HTML_WITH_A_TAG_AND_ID)

        ArrayList bookmarks = htmlPage.getAllIds()

        // there's TWO ids contained in the sample html
        assertEquals( "TWO ids expected in bookmark list", 3, bookmarks.size())

        assertEquals( "id shall equal $HREF_ONE", HREF_ONE, bookmarks.first().getIdAttribute())

    }
    @Test
    public void testAnchorsToStringList() {
        String HTML_WITH_A_TAGS_AND_ID = '''
           <html>
             $HTML_HEAD
              <body>
                   <h1>dummy-heading-1</h1>
                   <a href="#aim42">link-to-aim42</a>
                   <h2 id="aim42">aim42 Architecture Improvement</h3>
                   <a href="#nonexisting">non-existing-link</a>
              </body>
           </html>'''

        HtmlPage htmlPage = new HtmlPage( HTML_WITH_A_TAGS_AND_ID )
        ArrayList hrefs = htmlPage.getAllHrefStrings()

        assertEquals( "expected []", ['aim42', 'nonexisting'], hrefs)
    }

    @Test
    public void testGetManyIdStrings() {
        String HTML_WITH_A_TAGS_AND_ID = '''
           <html>
             $HTML_HEAD
              <body>
                   <a href="#aim42">link-to-aim42</a>
                   <h2 id="aim42">aim42 Architecture Improvement</h3>
                   <h2 id="aim43">aim43 </h3>
                   <h2 id="aim44">aim44 </h3>
                   <h2 id="aim45">aim45 </h3>
                   <h2 id="aim46">aim46 </h3>
              </body>
           </html>'''

        HtmlPage htmlPage = new HtmlPage( HTML_WITH_A_TAGS_AND_ID )
        ArrayList ids = htmlPage.getAllIdStrings()

        def expected = ['aim42', 'aim43', 'aim44', 'aim45', 'aim46']
        assertEquals( "expected $expected", expected, ids )
    }


    @Test
    public void testGetIdStringsAndAllIds() {
        String HTML_WITH_A_TAGS_AND_ID = '''
           <html>
             $HTML_HEAD
              <body>
                   <a href="#aim42">link-to-aim42</a>
                   <h2 id="aim42">aim42 Architecture Improvement</h3>
                   <h2 id="aim43">aim43 </h3>
                   <h2 id="aim44">aim44 </h3>
              </body>
           </html>'''

        HtmlPage htmlPage = new HtmlPage( HTML_WITH_A_TAGS_AND_ID )
        ArrayList idStrings = htmlPage.getAllIdStrings()

        def expectedIdStrings = ['aim42', 'aim43', 'aim44']
        assertEquals( "expected $expectedIdStrings", expectedIdStrings, idStrings)

        ArrayList tagsWithId = htmlPage.getAllIds()
        assertEquals( "expected 3 tags with ids", 3, tagsWithId.size())

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

