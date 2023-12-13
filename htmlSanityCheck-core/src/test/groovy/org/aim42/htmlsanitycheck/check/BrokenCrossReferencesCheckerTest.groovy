package org.aim42.htmlsanitycheck.check

import org.aim42.htmlsanitycheck.collect.SingleCheckResults
import org.aim42.htmlsanitycheck.html.HtmlPage
import org.junit.Test

import static org.aim42.htmlsanitycheck.html.HtmlConst.HTML_END
import static org.aim42.htmlsanitycheck.html.HtmlConst.HTML_HEAD

// see end-of-file for license information


class BrokenCrossReferencesCheckerTest extends GroovyTestCase {

    SingleCheckResults collector = new SingleCheckResults()

    void check(String htmlBody) {
        String html = HTML_HEAD + htmlBody + HTML_END
        HtmlPage htmlPage = new HtmlPage(html)
        collector = new BrokenCrossReferencesChecker().performCheck(htmlPage)
    }

    @Test
    void testExternalLinkShallBeIgnored() {
        check '''
           <h1>dummy-heading-1</h1>
           <a href="http://github.com/aim42">aim42</a>
           <a href="https://github.com/arc42">arc42</a>
        '''

        assertEquals( "expected zero finding", 0, collector.nrOfProblems())
        assertEquals( "expected 2 checks", 2, collector.nrOfItemsChecked)
    }

    @Test
    void testOneGoodOneBrokenLink() {
        String HTML_WITH_A_TAGS_AND_ID = '''
           <h1>dummy-heading-1</h1>
           <a href="#aim42">link-to-aim42</a>
           <h2 id="aim42">aim42 Architecture Improvement</h3>
           <a href="#nonexisting">non-existing-link</a>
        '''

        check HTML_WITH_A_TAGS_AND_ID

        assertEquals( "expected one finding", 1, collector.nrOfProblems())
        assertEquals( "expected four checks", 4, collector.nrOfItemsChecked)

        String actual = collector.findings.first()
        String expected = "link target \"nonexisting\" missing\n (Suggestions: aim42)"
        String message = "expected $expected"

        assertEquals(message, expected, actual)
    }

    @Test
    void testLinkWithIllegalCharacter() {
        String HTML_WITH_BAD_LINK = '''
           <h1>dummy-heading-1</h1>
           <a href="#Context Analysis">context</a>
        '''

        check HTML_WITH_BAD_LINK

        assertEquals( "expected one finding", 1, collector.nrOfProblems())
        assertEquals( "expected one check", 1, collector.nrOfItemsChecked)

        String actual = collector.findings.first()
        String expected = "link \"#Context Analysis\" contains illegal characters"
        String message = "expected $expected"
        assertEquals(message, expected, actual)
    }

    @Test
    void testTwoGoodLinks() {
        String HTML_WITH_TWO_TAGS_AND_ID = '''
           <h1>dummy-heading-1</h1>
           <a href="#aim42">link-to-aim42</a>
           <h2 id="aim42">aim42 Architecture Improvement</h3>
           <a href="#arc42">arc42</a>
           <h2 id="arc42">arc42</a>
        '''

        check HTML_WITH_TWO_TAGS_AND_ID

        assertEquals( "expected zero finding", 0, collector.nrOfProblems())
        assertEquals( "expected four checks", 4, collector.nrOfItemsChecked)
    }

    @Test
    void testTwoBrokenLinks() {
        String HTML_WITH_TWO_LINKS_NO_ID = '''
           <a href="#aim42">link-to-aim42</a>
           <a href="#arc42">non-existing-link</a>
        '''

        check HTML_WITH_TWO_LINKS_NO_ID

        assertEquals( "expected two findings", 2, collector.nrOfProblems())
        assertEquals( "expected four checks", 4, collector.nrOfItemsChecked)

        // first finding: aim42 link missing
        String actual = collector.findings.first()
        String expected = "link target \"aim42\" missing"
        String message = "expected $expected"
        assertEquals(message, expected, actual)

        // second finding: arc42 link missing
        actual = collector.findings[1]
        expected = "link target \"arc42\" missing"
        assertEquals(message, expected, actual)
    }

    @Test
    void testReferenceCount() {
        check '''
           <a href="#aim42">link-0</a>
           <a href="#aim42">link-1</a>
           <a href="#aim42">link-2</a>
           <a href="#aim42">link-3</a>
           <a href="#aim42">link-4</a>
        '''

        assertEquals( "expected five findings", 5, collector.nrOfProblems())
        assertEquals( "expected two checks", 2, collector.nrOfItemsChecked)

        // finding: aim42 link missing, reference count 5
        String actual = collector.findings.first()
        String expected = "link target \"aim42\" missing, reference count: 5"
        String message = "expected $expected"

        assertEquals(message, expected, actual)
   }

    @Test
    void testMailtoinkIsIgnored() {
        check '''
           <a href="mailto:chuck.norris@example.org">mail him</a>
        '''

        assertEquals( "expected one check", 1, collector.nrOfItemsChecked)
        assertEquals( "expected zero finding", 0, collector.nrOfProblems())
    }

    @Test
    void testLinkToLocalFileShallNotBeChecked() {
        check '''
            <a href="api/nonexisting.html">internal-link-to-file</a>
        '''

        // for local references, no checks shall be performed
        assertEquals( "expected one check", 1, collector.nrOfItemsChecked)
    }

    @Test
    void testLinkToHashtagShallPass() {
        check '''
            <a href="#">internal-link-to-file</a>
        '''

        assertEquals( "expected checks", 2, collector.nrOfItemsChecked)
        assertEquals( "expected zero finding", 0, collector.nrOfProblems())
    }

    @Test
    void testUrlEncodedFragmentIsUnderstood() {
        check '''
            <a href="#%3Cinit%3E(int%5B%5D)">constructor</a>
            <a id="<init>(int[])">...</a>
        '''

        assertEquals( "expected checks", 2, collector.nrOfItemsChecked)
        assertEquals( "expected zero finding", 0, collector.nrOfProblems())
    }

}


/*========================================================================
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
 ========================================================================*/
