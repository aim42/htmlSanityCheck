package org.aim42.htmlsanitycheck.check

import org.aim42.htmlsanitycheck.collect.SingleCheckResults
import org.aim42.htmlsanitycheck.html.HtmlPage
import org.junit.Before
import org.junit.Test

class MissingLocalResourcesCheckerTest extends GroovyTestCase {

    private final static String HTMLHEAD = "<html><head></head><body>"

    Checker missingLocalResourcesChecker
    HtmlPage htmlPage
    SingleCheckResults collector

    @Before
    public void setUp() {
        collector = new SingleCheckResults()
    }

    @Test
    public void testExistingLocalResourceIsFound() {
        // 1.) create tmp directory d1 with subdir d2
        File d1 = File.createTempDir()
        File d2 = new File(d1 , "/d2")
        d2.mkdirs()

        // 2.) create local resource file f1 in subdir d2
        final String fname = "fname.html"
        File f1 = new File( d2, fname) << HTMLHEAD

        assertEquals( "created an artificial file","d2/fname.html",
                f1.canonicalPath - d1.canonicalPath - "/")

        // 3.) create tmp html file "index.html" linking to f1 in directory d1
        File index = new File( d1, "index.html") << HTMLHEAD
        index << """<a href="d2/$fname">link to local resource"</a></body></html>"""

        // 4.) check
        htmlPage = new HtmlPage( index )

        missingLocalResourcesChecker = new MissingLocalResourcesChecker(
                pageToCheck: htmlPage )
        collector = missingLocalResourcesChecker.performCheck()

        // assert that no issue is found (== the local resource d2/fname.html is found)
        assertEquals( "expected zero finding", 0, collector.nrOfProblems())
        assertEquals( "expected one check", 0, collector.nrOfItemsChecked)


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
