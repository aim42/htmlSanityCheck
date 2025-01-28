package org.aim42.htmlsanitycheck

import org.aim42.htmlsanitycheck.check.AllCheckers
import org.aim42.htmlsanitycheck.check.Checker
import org.aim42.htmlsanitycheck.collect.PageResults
import org.aim42.htmlsanitycheck.collect.PerRunResults
import org.junit.Test

import static junit.framework.TestCase.assertEquals

class AllChecksRunnerTest {

    final static String HTML_HEAD = '<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN"> <head></head><html>'
    public static final String TESTFILE = "testfile.html"

    private static AllChecksRunner prepareAllChecksRunner(final String htmlContent,
                                                          final List<Class<? extends Checker>> checkerClasses
                                                                  = AllCheckers.CHECKER_CLASSES) {
        File tmpDir = File.createTempDir()
        File tmpFile = new File(tmpDir, TESTFILE) << htmlContent
        Configuration config = Configuration
                .builder()
                .sourceDir(tmpFile.parentFile)
                .sourceDocuments([tmpFile] as Set)
                .checksToExecute(checkerClasses)
                .build()
        AllChecksRunner allChecksRunner = new AllChecksRunner(config)
        return allChecksRunner
    }

    @Test
    void testPerformAllChecksWithCorrectInput() {
        String HTML = """$HTML_HEAD<title>hsc</title><body></body></html>"""
        AllChecksRunner allChecksRunner = prepareAllChecksRunner(HTML)

        PerRunResults allResults = allChecksRunner.performAllChecks()
        PageResults pageResults = allResults.resultsForAllPages.get(0)
        // expectation:
        // 7 checks run
        // 0 items checked
        // 0 findings
        // title = "hsc"
        int expected = AllCheckers.CHECKER_CLASSES.size()

        assertEquals("expected $expected kinds of checks", expected, pageResults.singleCheckResults.size())
        assertEquals("expected 0 items checked", 0, pageResults.nrOfItemsCheckedOnPage())
        assertEquals("expected 0 findings", 0, pageResults.nrOfFindingsOnPage())
        assertEquals("expected hsc title", "hsc", pageResults.pageTitle)

        assertEquals("expected ${TESTFILE} as fileName", TESTFILE, pageResults.pageFileName)
    }

    @Test
    void testSingleBrokenHtmlFile() {
        String HTML = """$HTML_HEAD<body><title>Faulty Dragon</title></body>
                   <h1 id="aim42">dummy-heading-1</h1>
                   <h2 id="aim42">duplicate id</h2>
                   <a href="#nonexisting">broken cross reference</a>
                </html>"""

        AllChecksRunner allChecksRunner = prepareAllChecksRunner(HTML)

        PerRunResults allResults = allChecksRunner.performAllChecks()
        PageResults pageResults = allResults.resultsForAllPages.get(0)

        int expected = AllCheckers.CHECKER_CLASSES.size()
        assertEquals("expected $expected kinds of checks", expected, pageResults.singleCheckResults.size())

        assertEquals("expected 2 findings", 2, pageResults.nrOfFindingsOnPage())

    }

    @Test
    void testUsingSubsetOfChecks() {
        AllChecksRunner allChecksRunner
                = prepareAllChecksRunner("""$HTML_HEAD<body><title>hsc</title></body></html>""",
                [AllCheckers.CHECKER_CLASSES.first()])

        PerRunResults allResults = allChecksRunner.performAllChecks()
        PageResults pageResults = allResults.resultsForAllPages.get(0)

        assertEquals(1, pageResults.singleCheckResults.size())
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



