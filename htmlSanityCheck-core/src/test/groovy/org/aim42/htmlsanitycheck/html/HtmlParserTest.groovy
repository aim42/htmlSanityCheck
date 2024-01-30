package org.aim42.htmlsanitycheck.html

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.junit.Test

import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertTrue

// see end-of-file for license information

class HtmlParserTest {


    final static String LOCAL_IMG_SRC = "images/test_xyz_uvw.jpg"
    final static String REMOTE_IMG_SRC = "https://www.google.com/images/srpr/logo11w.png"



    final String HTML_WITH_IMG_TAG =
          """${HtmlConst.HTML_HEAD}
              <body>
                   <img src="$LOCAL_IMG_SRC" >
              ${HtmlConst.HTML_END}"""


    @Test
    void testBasicParser() {

        String html =
                '''<html>
                <head>
                    <title>First parse</title>
                </head>
                <body>
                    <p>very basic html</p>
                </body>
            </html>'''

        Document doc = Jsoup.parse(html)

        boolean actual = (doc.body() != null)
        assertTrue("parsed basic html doc contains body", actual)
    }


    @Test
    void testGetSrcAttributeFromImageTag() {

        HtmlPage htmlpage = new HtmlPage(HTML_WITH_IMG_TAG)

        List<HtmlElement> imageTags = htmlpage.getAllImageTags()

        assertEquals("expect exactly one image tag", 1, imageTags.size())

        String expected = LOCAL_IMG_SRC
        String actual = imageTags.first().getImageSrcAttribute()
        assertEquals("src-path $LOCAL_IMG_SRC expected but got $actual",
                expected, actual)
    }


    @Test
    void testGetSrcAttributeFromRemoteImageTag() {
        String HTML = """${HtmlConst.HTML_HEAD} <body>

            <img src="$REMOTE_IMG_SRC" >
            <img src="$LOCAL_IMG_SRC" >

         ${HtmlConst.HTML_END}"""

        HtmlPage htmlPage = new HtmlPage( HTML )

        List<HtmlElement> imageTags = htmlPage.getAllImageTags()

        String expected = REMOTE_IMG_SRC
        String actual   = imageTags.first().getImageSrcAttribute()

        assertEquals( "$REMOTE_IMG_SRC could not be found in imageTags", expected, actual )

    }

    @Test
    void testGetHrefFromAnchorTag() {
        final String REMOTE_URL = "http://github.com/aim42"
        final String HTML = """${HtmlConst.HTML_HEAD} <body>
            <a href=$REMOTE_URL ></a>
            <a href="" ></a>
            <a href="#local"></a>
         ${HtmlConst.HTML_END}"""


        HtmlPage htmlPage = new HtmlPage( HTML )
        List<String> hrefs = htmlPage.getAllHrefStrings()

        assertEquals( "expected three hrefs", 3, hrefs.size())

        // is remote URL contained in hrefs?
        assertTrue( "expected $REMOTE_URL to be contained in hrefs, but wasn't.", hrefs.contains( REMOTE_URL ))

        assertTrue( "expect empty url to be contained in hrefs, but wasn't", hrefs.contains(""))

        assertTrue( "expect \"#local\" to be contained in hrefs, but wasn't", hrefs.contains("#local"))



    }
}

/*=============================================================
 Copyright Gernot Starke and aim42 contributors

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
 =============================================================*/
