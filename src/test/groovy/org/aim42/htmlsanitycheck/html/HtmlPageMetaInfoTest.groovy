package org.aim42.htmlsanitycheck.html

import org.junit.Before
import org.junit.Test

/**
 * @deprecated as of version 0.5.0 - all meta-info is now contained in
 * {@link org.aim42.htmlsanitycheck.collect.SinglePageResults}
 */
class HtmlPageMetaInfoTest extends GroovyTestCase {

    final static String HTML_PREFIX = '<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN"> '

    private final String HTML = """$HTML_PREFIX
           <head>
             <title>CoolTitle</title>
           </head>
           <html>
           <body>
           </body>
        </html>"""


    private HtmlPageMetaInfo metaInfo

    private HtmlPage htmlPage

    private File tmpFile

    @Before
    public void setUp() {

    }


    @Test
    public void testDocumentSizeAndTitle() {

        htmlPage = new HtmlPage(HTML)
        metaInfo = new HtmlPageMetaInfo(htmlPage)

        assertEquals("html page size should be 132", 132, metaInfo.documentSize)
        assertEquals("expected CoolTitle", "CoolTitle", metaInfo.documentTitle)
    }


    @Test
    public void testEmptyTitle() {
        String HTML = """$HTML_PREFIX
           <head>
           </head>
           <html>
           <body>
           </body>
        </html>"""

        htmlPage = new HtmlPage(HTML)
        metaInfo = new HtmlPageMetaInfo(htmlPage)

        assertEquals("expected empty", "", metaInfo.documentTitle)
    }

    @Test
    public void testEmptyHtml() {

        htmlPage = new HtmlPage("")
        metaInfo = new HtmlPageMetaInfo(htmlPage)

        assertEquals("expected empty", "", metaInfo.documentTitle)

        assertEquals("expected 44 char document", 44, metaInfo.documentSize)
        // parsing an empty string as html-document yields the following
        // document:
        //    <html><head></head><body></body></html>
    }

    @Test
    public void testHtmlFromFile() {

        // create file with proper html content
        tmpFile = File.createTempFile("testfile", "html")
        tmpFile.write( HTML )

        htmlPage = new HtmlPage(tmpFile)
        metaInfo = new HtmlPageMetaInfo(htmlPage)

        //println tmpFile.absolutePath

        assertEquals("expected CoolTitle", "CoolTitle", metaInfo.documentTitle)
        //assertEquals("expected testfile.html as filename", "testfile.html", metaInfo.documentFileName)

        assertEquals("expected 136 char document", 132, metaInfo.documentSize)


    }
}

/************************************************************************
 * This is free software - without ANY guarantee!
 *
 *
 * Copyright 2013, Dr. Gernot Starke, arc42.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 *********************************************************************** */
