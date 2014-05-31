package org.aim42.htmlsanitycheck.html

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.junit.Test

// see end-of-file for license information


class HtmlParserTest extends GroovyTestCase {

    final String HTML_WITH_IMG_TAG =
            '''
           <html>
             $HTML_HEAD
              <body>
                   <img src="images/test_xyz_uvw.jpg" >
              </body>
           </html>'''

    final static String IMG_SRC = "images/test_xyz_uvw.jpg"


    @Test
    public void testBasicParser() {

        String html =
                '''<html>
                <head>
                    <title>First parse</title>
                </head>
                <body>
                    <p>very basic html</p>
                </body>
            </html>''';

        Document doc = Jsoup.parse(html);

        boolean actual = (doc.body() != null)
        assertTrue("parsed basic html doc contains body", actual)
    }


    @Test
    public void testGetSrcAttributeFromImageTag() {

        HtmlPage htmlpage = new HtmlPage(HTML_WITH_IMG_TAG)

        ArrayList<HtmlElement> images = htmlpage.getAllImageTags()

        assertEquals("expect exactly one image tag", 1, images.size())

        String expected = IMG_SRC
        String actual = images.first().getSrcAttribute()
        assertEquals("src-path $IMG_SRC expected but got $actual",
                expected, actual)

    }

}

/*=============================================================
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
 =============================================================*/
