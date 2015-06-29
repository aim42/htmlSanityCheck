package org.aim42.htmlsanitycheck.suggest

import org.aim42.htmlsanitycheck.check.BrokenCrossReferencesChecker
import org.aim42.htmlsanitycheck.check.Checker
import org.aim42.htmlsanitycheck.collect.Finding
import org.aim42.htmlsanitycheck.collect.SingleCheckResults
import org.aim42.htmlsanitycheck.html.HtmlPage
import org.junit.Before
import org.junit.Test

// see end of file for licence information

class BrokenCrossReferenceCheckerSuggestionTest extends GroovyTestCase {


        private final static String HTMLHEAD = "<html><head></head><body>"

        Checker brokenCrossRefChecker
        HtmlPage htmlPage
        SingleCheckResults collector

        @Before
        public void setUp() {
            collector = new SingleCheckResults()
        }


        @Test
        public void testBrokenInternalLinkGoodSuggestion() {
            String HTML_WITH_A_TAGS_AND_ID = '''
           <html>
             <head></head>
              <body>
                   <h1>dummy-heading-1</h1>
                   <a href="#aim42">link-to-aim42</a>
                   <h2 id="aim43">aim42 Architecture Improvement</h3>
                   <h2 id="zzz">zzz Improvement</h3>
                   <h2 id="uuu">uuu Improvement</h3>
                   <h2 id="bbb">bbb Improvement</h3>


              </body>
           </html>'''

            htmlPage = new HtmlPage( HTML_WITH_A_TAGS_AND_ID )

            brokenCrossRefChecker = new BrokenCrossReferencesChecker(
                    pageToCheck: htmlPage)
            collector = brokenCrossRefChecker.performCheck()

            Finding onlyFinding  = collector?.findings?.first()
            String expected = "link target \"aim42\" missing"
            String message = "expeted $expected"

            // expected one finding
            assertEquals("expected ONE finding", 1, collector.findings.size())

            // message shall be "link target aim42 missing
            assertEquals(message, expected, onlyFinding.item )

            // checker shall determine aim43 as suggestion
            assertEquals("expected aim43 as suggestion", "aim43", onlyFinding.suggestions.first())

        }


    @Test
    public void testBrokenInternalWithoutSuggestion() {
        String HTML_WITH_A_TAGS_AND_ID = '''
           <html>
             <head></head>
              <body>
                   <a href="#aim42">link-to-aim42</a>
                   <h1>This HTML does not contain any id</h3>
                     Without other id's, checker must not return any suggestion...
              </body>
           </html>'''

        htmlPage = new HtmlPage( HTML_WITH_A_TAGS_AND_ID )

        brokenCrossRefChecker = new BrokenCrossReferencesChecker(
                pageToCheck: htmlPage)
        collector = brokenCrossRefChecker.performCheck()

        Finding onlyFinding  = collector?.findings?.first()
        String expected = "link target \"aim42\" missing"
        String message = "expeted $expected"


        // checker shall determine no as suggestion
        assertEquals("no suggestion expected", 0, onlyFinding.suggestions.size())

    }

}

/************************************************************************
 * This is free software - without ANY guarantee!
 *
 *
 * Copyright Dr. Gernot Starke, arc42.org
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

