package org.aim42.htmlsanitycheck.check

import org.aim42.htmlsanitycheck.collect.SingleCheckResults
import org.aim42.htmlsanitycheck.html.HtmlElement
import org.aim42.htmlsanitycheck.html.HtmlPage
import org.jsoup.nodes.Element
import org.junit.Before
import org.junit.Test

import static org.junit.Assert.assertEquals

// see end-of-file for license information


class DuplicateIdCheckerTest {

    Checker duplicateIdChecker
    HtmlPage pageToCheck
    SingleCheckResults collector

    @Before
    void setUp() {
        collector = new SingleCheckResults()
    }


    @Test
    void testOneDuplicateId() {
        String HTML_WITH_DUPLICATE_ID = '''
           <html>
             <head></head>
              <body>
                   <h1 id="aim42">dummy-heading-1</h1>
                   <a href="#aim42">link-to-aim42</a>
                   <h2 id="aim42">aim42 Architecture Improvement</h3>
              </body>
           </html>'''

        pageToCheck = new HtmlPage(HTML_WITH_DUPLICATE_ID)

        duplicateIdChecker = new DuplicateIdChecker()

        collector = duplicateIdChecker.performCheck( pageToCheck )

        // expect ONE check, as we check the SET of ids
        assertEquals("expected one check", 1, collector?.nrOfItemsChecked)
        assertEquals("expected one finding", 1, collector?.nrOfProblems())
    }

    @Test
    void testManyDuplicateId() {
        String HTML_WITH_DUPLICATE_ID = '''
           <html>
             <head></head>
              <body>
                   <h1 id="unique">unique</h1>
                   <h1 id="aim42">t1</h1>
                   <h2 id="aim42">t1</h2>

                   <h1 id="aim43">t3</h1>
                   <h2 id="aim43">t3</h2>

                   <h1 id="aim44">t4</h1>
                   <h2 id="aim44">t4</h2>
              </body>
           </html>'''

        pageToCheck = new HtmlPage(HTML_WITH_DUPLICATE_ID)

        duplicateIdChecker = new DuplicateIdChecker()
        collector = duplicateIdChecker.performCheck( pageToCheck )

        // expect THREE checks, as we check the SET of ids
        assertEquals("expected four checks", 4, collector?.nrOfItemsChecked)
        assertEquals("expected three findings", 3, collector?.nrOfProblems())
    }

}

/*=====================================================================
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
 =====================================================================*/

