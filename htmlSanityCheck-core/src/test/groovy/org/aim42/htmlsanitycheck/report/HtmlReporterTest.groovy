package org.aim42.htmlsanitycheck.report

import org.aim42.htmlsanitycheck.collect.Finding
import org.aim42.htmlsanitycheck.collect.PerRunResults
import org.aim42.htmlsanitycheck.collect.SingleCheckResults
import org.aim42.htmlsanitycheck.collect.SinglePageResults
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

import java.nio.file.Files

import static org.junit.Assert.assertTrue
import static org.junit.Assert.fail

class HtmlReporterTest {

    private HtmlReporter htmlReporter
    private PerRunResults runResults

    @Rule
    public TemporaryFolder tempDir = new TemporaryFolder()

    @Before
    void setUp() {
        runResults = new PerRunResults()
        htmlReporter = new HtmlReporter(runResults, tempDir.getRoot().getAbsolutePath())
    }

    @Test
    void testReportPageSummary() {
        htmlReporter.initReport()
        // Prepare the HtmlReporter with some data
        SinglePageResults singlePageResults = new SinglePageResults(
                "test.html",
                "/path/to/test.html",
                "Test Page",
                1000,
                new ArrayList<>())
        runResults.addPageResults(singlePageResults)

        // Call the method to test
        htmlReporter.reportPageSummary(singlePageResults)

        // Check the output
        String content = getResultContents()
        assertTrue(content.contains("Results for test.html"))
        assertTrue(content.contains("location : /path/to/test.html"))
        assertTrue(content.contains("<title>HTML Sanity Check Results</title>"))
        assertTrue(content.contains("<div class=\"infoBox\" id=\"findings\"><div class=\"counter\">0</div>issue</div>"))
        assertTrue(content.contains("<div class=\"infoBox\" id=\"size\"><div class=\"counter\">1.0</div>kByte</div>"))
        assertTrue(content.contains("<div class=\"infoBox\" id=\"checks\"><div class=\"counter\">0</div>checks</div>"))
        assertTrue(content.contains("<div class=\"infoBox success\" id=\"successRate\"><div class=\"percent\">100%</div>successful</div>"))
    }

    @Test
    void testReportOverallSummary() {
        htmlReporter.initReport()
        // Prepare the HtmlReporter with some data
        SinglePageResults singlePageResults = new SinglePageResults(
                "test.html",
                "/path/to/test.html",
                "Test Page",
                1000,
                new ArrayList<>())

        runResults.addPageResults(singlePageResults)

        // Call the method to test
        htmlReporter.reportOverallSummary()

        String content = getResultContents()
        assertTrue(content.contains("<img class=\"logo\" src=\"htmlsanitycheck-logo.png\" alt=\"htmlSC\" align=\"right\"/>"))
        assertTrue(content.contains("<h1>HTML Sanity Check Results</h1>"))
    }

    @Test
    void testReportPageFooter() {
        htmlReporter.initReport()
        // Prepare the HtmlReporter with some data
        SinglePageResults singlePageResults = new SinglePageResults(
                "test.html",
                "/path/to/test.html",
                "Test Page",
                1000,
                new ArrayList<>())
        runResults.addPageResults(singlePageResults)

        // Call the method to test
        htmlReporter.reportPageFooter()

        // Check the output
        String expectedOutput = "Your footer content here"

        String content = getResultContents()
        assertTrue(content.contains(expectedOutput))
    }

    @Test
    void testReportSingleCheckSummary() {
        htmlReporter.initReport()
        // Prepare the HtmlReporter with some data
        SinglePageResults singlePageResults = new SinglePageResults(
                "test.html",
                "/path/to/test.html",
                "Test Page",
                1000,
                [new Finding("Issue 1"), new Finding("Issue 2")])
        SingleCheckResults singleCheckResults = new SingleCheckResults()
        singleCheckResults.setWhatIsChecked("Test Checks")
        singleCheckResults.setSourceItemName("Test Source")
        singleCheckResults.setTargetItemName("Test Target")
        singleCheckResults.setGeneralRemark("Test Remark")
        singleCheckResults.incNrOfChecks()
        singleCheckResults.addFinding(new Finding(""))
        singlePageResults.addResultsForSingleCheck(singleCheckResults)
        runResults.addPageResults(singlePageResults)

        // Call the method to test
        htmlReporter.reportSingleCheckSummary(singleCheckResults)

        // Check the output
        String content = getResultContents()
        assertTrue(content.contains("<div class=\"failures\"><h3>Test Checks</h3></div>\n"))
        assertTrue(content.contains("1 Test Source checked"))
        assertTrue(content.contains("1 Test Target found.<br>"))
        assertTrue(content.contains("Test Remark"))
    }

    @Test
    void testReportSingleCheckDetails() {
        htmlReporter.initReport()
        // Prepare the HtmlReporter with some data
        SinglePageResults singlePageResults = new SinglePageResults(
                "test.html",
                "/path/to/test.html",
                "Test Page",
                1000,
                [new Finding("Issue 1"), new Finding("Issue 2")])
        SingleCheckResults singleCheckResults = new SingleCheckResults()
        singleCheckResults.setWhatIsChecked("Test Checks")
        singleCheckResults.setSourceItemName("Test Source")
        singleCheckResults.setTargetItemName("Test Target")
        singleCheckResults.setGeneralRemark("Test Remark")
        singleCheckResults.incNrOfChecks()
        singleCheckResults.addFinding(new Finding("A simple Problem", 7, ["Fix properly"]))
        singlePageResults.addResultsForSingleCheck(singleCheckResults)
        runResults.addPageResults(singlePageResults)

        // Call the method to test
        htmlReporter.reportSingleCheckDetails(singleCheckResults)

        // Check the output
        String content = getResultContents()
        assertTrue(content.contains("<li> A simple Problem (reference count: 7)"))
        assertTrue(content.contains(" (Suggestions: Fix properly) </li>"))
    }

    private String getResultContents() {
        htmlReporter.closeReport()
        File reportFile = new File(tempDir.getRoot(), "index.html")
        try {
            String content = new String(Files.readAllBytes(reportFile.toPath()))
            return content
        } catch (IOException e) {
            fail("Failed to read report file '${reporFile}': ${e}")
        }
    }
}

/*======================================================================

Copyright Gernot Starke and aim42 contributors

 Licensed under the Apache License, Version 2.0 (the "License")
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an
 "AS IS" BASIS,WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 ======================================================================*/
