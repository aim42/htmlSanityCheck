package org.aim42.htmlsanitycheck.check

import org.aim42.htmlsanitycheck.collect.SingleCheckResultsCollector
import org.aim42.htmlsanitycheck.html.HtmlPage
import org.junit.Before
import org.junit.Test

// see end-of-file for license information


class DuplicateIdCheckerTest extends GroovyTestCase {

    Checker duplicateIdChecker
    HtmlPage htmlPage
    SingleCheckResultsCollector collector

    @Before
    public void setUp() {
        collector = new SingleCheckResultsCollector()
    }


    @Test
    public void testOneDuplicateId() {
        String HTML_WITH_DUPLICATE_ID = '''
           <html>
             <head></head>
              <body>
                   <h1 id="aim42">dummy-heading-1</h1>
                   <a href="#aim42">link-to-aim42</a>
                   <h2 id="aim42">aim42 Architecture Improvement</h3>
              </body>
           </html>'''

        htmlPage = new HtmlPage(HTML_WITH_DUPLICATE_ID)

        duplicateIdChecker = new DuplicateIdChecker(
                pageToCheck: htmlPage
        )
        collector = duplicateIdChecker.performCheck()

        // expect ONE check, as we check the SET of ids
        assertEquals("expected one check", 1, collector?.nrOfItemsChecked)
        assertEquals("expected one finding", 1, collector?.nrOfProblems())
    }

    @Test
    public void testManyDuplicateId() {
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

        htmlPage = new HtmlPage(HTML_WITH_DUPLICATE_ID)

        duplicateIdChecker = new DuplicateIdChecker(
                pageToCheck: htmlPage
        )
        collector = duplicateIdChecker.performCheck()

        // expect THREE checks, as we check the SET of ids
        assertEquals("expected four checks", 4, collector?.nrOfItemsChecked)
        assertEquals("expected three findings", 3, collector?.nrOfProblems())
    }

    @Test
    public void testGetAllTagsWithSpecificId() {
        String HTML_WITH_DUPLICATE_ID = '''
           <html>
             <head></head>
              <body>
                   <h1 id="aim42">dummy-heading-1</h1>
                   <a href="#aim42">link-to-aim42</a>
                   <h2 id="aim42">aim42 </h3>
                   <h2 id="aim43">aim43 </h3>
                   <h2 id="aim44">aim44 </h3>

              </body>
           </html>'''

        htmlPage = new HtmlPage(HTML_WITH_DUPLICATE_ID)
        ArrayList tagsWithId = htmlPage.getAllIds()
        //Set idStringsSet = htmlPage.getAllIdStrings().toSet()

        assertEquals("Expected 4 tags with ids", 4, tagsWithId.size())

        ArrayList<String> expectedAim43Ids =
                DuplicateIdChecker.getAllTagsWithSpecificId( "aim43", tagsWithId)
        assertEquals(" Expected ONE tag with id='aim43", 1, expectedAim43Ids.size() )


        ArrayList<String> expectedAim42Ids =
                DuplicateIdChecker.getAllTagsWithSpecificId( "aim42", tagsWithId)
        assertEquals(" Expected TWO tags with id='aim42", 2, expectedAim42Ids.size() )



    }

}

/*=====================================================================
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
 =====================================================================*/

