package org.aim42.htmlsanitycheck.check

import org.aim42.htmlsanitycheck.collect.SingleCheckResults
import org.aim42.htmlsanitycheck.html.HtmlPage
import org.junit.Before
import org.junit.Test

// see end-of-file for license information


class BrokenCrossReferencesCheckerTest extends GroovyTestCase {

    private final static String HTMLHEAD = "<html><head></head><body>"

    Checker undefinedInternalLinksChecker
    HtmlPage htmlPage
    SingleCheckResults collector

    @Before
    public void setUp() {
      collector = new SingleCheckResults()
    }


    @Test
    public void testExternalLinkShallBeIgnored() {
        String HTML = """$HTMLHEAD

                   <h1>dummy-heading-1</h1>
                   <a href="http://github.com/aim42">aim42</a>
                   <a href="https://github.com/arc42">arc42</a>

              </body>
           </html>"""

        htmlPage = new HtmlPage( HTML )

        undefinedInternalLinksChecker = new BrokenCrossReferencesChecker()
        collector = undefinedInternalLinksChecker.performCheck(htmlPage)


        assertEquals( "expected zero finding", 0, collector.nrOfProblems())
        assertEquals( "expected 2 checks", 2, collector.nrOfItemsChecked)

    }


    @Test
    public void testOneGoodOneBrokenLink() {
        String HTML_WITH_A_TAGS_AND_ID = '''
           <html>
             <head></head>
              <body>
                   <h1>dummy-heading-1</h1>
                   <a href="#aim42">link-to-aim42</a>
                   <h2 id="aim42">aim42 Architecture Improvement</h3>
                   <a href="#nonexisting">non-existing-link</a>
              </body>
           </html>'''

        htmlPage = new HtmlPage( HTML_WITH_A_TAGS_AND_ID )

        undefinedInternalLinksChecker = new BrokenCrossReferencesChecker()
        collector = undefinedInternalLinksChecker.performCheck(htmlPage)

        assertEquals( "expected one finding", 1, collector.nrOfProblems())
        assertEquals( "expected four checks", 4, collector.nrOfItemsChecked)

        String actual = collector.findings.first()
        String expected = "link target \"nonexisting\" missing\n (Suggestions: aim42)"
        String message = "expected $expected"

        assertEquals(message, expected, actual)
    }


    @Test
    public void testLinkWithIllegalCharacter() {
        String HTML_WITH_BAD_LINK = '''
           <html>
             <head></head>
              <body>
                   <h1>dummy-heading-1</h1>
                   <a href="#Context Analysis">context</a>
              </body>
           </html>'''

        htmlPage = new HtmlPage( HTML_WITH_BAD_LINK )

        undefinedInternalLinksChecker = new BrokenCrossReferencesChecker()
        collector = undefinedInternalLinksChecker.performCheck(htmlPage)

        assertEquals( "expected one finding", 1, collector.nrOfProblems())
        assertEquals( "expected one check", 1, collector.nrOfItemsChecked)

        String actual = collector.findings.first()
        String expected = "link \"#Context Analysis\" contains illegal characters"
        String message = "expected $expected"

        assertEquals(message, expected, actual)
    }


    @Test
    public void testTwoGoodLinks() {
        String HTML_WITH_TWO_TAGS_AND_ID = '''
           <html>
             <head></head>
              <body>
                   <h1>dummy-heading-1</h1>
                   <a href="#aim42">link-to-aim42</a>
                   <h2 id="aim42">aim42 Architecture Improvement</h3>
                   <a href="#arc42">arc42</a>
                   <h2 id="arc42">arc42</a>
              </body>
           </html>'''

        htmlPage = new HtmlPage( HTML_WITH_TWO_TAGS_AND_ID )

        undefinedInternalLinksChecker = new BrokenCrossReferencesChecker()
        collector = undefinedInternalLinksChecker.performCheck( htmlPage )

        assertEquals( "expected zero finding", 0, collector.nrOfProblems())
        assertEquals( "expected four checks", 4, collector.nrOfItemsChecked)
    }

    @Test
    public void testTwoBrokenLinks() {
        String HTML_WITH_TWO_LINKS_NO_ID = '''
           <html>
             <head></head>
              <body>
                   <a href="#aim42">link-to-aim42</a>
                   <a href="#arc42">non-existing-link</a>
              </body>
           </html>'''

        htmlPage = new HtmlPage( HTML_WITH_TWO_LINKS_NO_ID)

        undefinedInternalLinksChecker = new BrokenCrossReferencesChecker()
        collector = undefinedInternalLinksChecker.performCheck(htmlPage)

        assertEquals( "expected two findings", 2, collector.nrOfProblems())
        assertEquals( "expected four checks", 4, collector.nrOfItemsChecked)

        // first finding: aim42 link missing
        String actual = collector.findings.first()
        String expected = "link target \"arc42\" missing"
        String message = "expected $expected"

        assertEquals(message, expected, actual)

        // second finding: arc42 link missing
        actual = collector.findings[1]
        expected = "link target \"aim42\" missing"
        assertEquals(message, expected, actual)
    }

    @Test
    public void testReferenceCount() {
        String HTML = """$HTMLHEAD
                   <a href="#aim42">link-0</a>
                   <a href="#aim42">link-1</a>
                   <a href="#aim42">link-2</a>
                   <a href="#aim42">link-3</a>
                   <a href="#aim42">link-4</a>

              </body>
           </html>"""

        htmlPage = new HtmlPage( HTML)

        undefinedInternalLinksChecker = new BrokenCrossReferencesChecker()

        collector = undefinedInternalLinksChecker.performCheck(htmlPage)

        assertEquals( "expected five findings", 5, collector.nrOfProblems())
        assertEquals( "expected two checks", 2, collector.nrOfItemsChecked)

        // finding: aim42 link missing, reference count 5
        String actual = collector.findings.first()
        String expected = "link target \"aim42\" missing, reference count: 5"
        String message = "expected $expected"

        assertEquals(message, expected, actual)
   }

    @Test
    public void testMailtoinkIsIgnored() {
        String HTML = """$HTMLHEAD
                   <a href="mailto:chuck.norris@example.org">mail him</a>
              </body></html>"""

        htmlPage = new HtmlPage( HTML )

        undefinedInternalLinksChecker = new BrokenCrossReferencesChecker()
        collector = undefinedInternalLinksChecker.performCheck(htmlPage)

        assertEquals( "expected one check", 1, collector.nrOfItemsChecked)
        assertEquals( "expected zero finding", 0, collector.nrOfProblems())
    }

    @Test
    public void testLinkToLocalFileShallNotBeChecked() {
        String HTML = """$HTMLHEAD
           <a href="api/nonexisting.html">internal-link-to-file</a>
         </body>
           </html>
        """
        htmlPage = new HtmlPage( HTML )

        undefinedInternalLinksChecker = new BrokenCrossReferencesChecker( )

        collector = undefinedInternalLinksChecker.performCheck( htmlPage )

        // for local references, no checks shall be performed
        assertEquals( "expected one check", 1, collector.nrOfItemsChecked)

    }

    @Test
    public void testLinkToHashtagShallPass() {
        String HTML = """$HTMLHEAD
           <a href="#">internal-link-to-file</a>
         </body>
           </html>
        """
        htmlPage = new HtmlPage( HTML )

        undefinedInternalLinksChecker = new BrokenCrossReferencesChecker( )

        collector = undefinedInternalLinksChecker.performCheck( htmlPage )

        assertEquals( "expected checks", 2, collector.nrOfItemsChecked)

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
