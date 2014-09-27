package org.aim42.htmlsanitycheck.check

import org.aim42.htmlsanitycheck.collect.SingleCheckResultsCollector
import org.aim42.htmlsanitycheck.html.HtmlPage
import org.junit.Before
import org.junit.Test

class MissingLocalResourcesCheckerTest extends GroovyTestCase {

    private final static String HTMLHEAD = "<html><head></head><body>"

    Checker missingLocalResourcesChecker
    HtmlPage htmlPage
    SingleCheckResultsCollector collector

    @Before
    public void setUp() {
        collector = new SingleCheckResultsCollector()
    }


    @Test
    public void testCrossReferenceIsNotChecked() {
        String HTML = """$HTMLHEAD
            <h1>dummy-heading-1</h1>
            <a href="#aim42">aim42</a>
           </body>
         </html>"""

        htmlPage = new HtmlPage( HTML )

        missingLocalResourcesChecker = new MissingLocalResourcesChecker(
                pageToCheck: htmlPage )
        collector = missingLocalResourcesChecker.performCheck()


        assertEquals( "expected zero finding", 0, collector.nrOfProblems())
        assertEquals( "expected zero checks", 0, collector.nrOfItemsChecked)

    }



    @Test
    public void testReferenceToLocalFileIsChecked() {
        String HTML = """$HTMLHEAD
            <h1>dummy-heading-1</h1>
            <a href="a_nonexisting_doc_uh7mP0R3YfR2__.pdf">nonexisting-doc</a>
            <a href="no/nex/ist/ing/dire/tory/test.pdf">another nonexisting download</a>
           </body>
         </html>"""

        htmlPage = new HtmlPage( HTML )

        missingLocalResourcesChecker = new MissingLocalResourcesChecker(
                pageToCheck: htmlPage )
        collector = missingLocalResourcesChecker.performCheck()

        int expected = 2

        assertEquals( "expected $expected findings", expected, collector.nrOfProblems())
        assertEquals( "expected $expected checks", expected, collector.nrOfItemsChecked)

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
