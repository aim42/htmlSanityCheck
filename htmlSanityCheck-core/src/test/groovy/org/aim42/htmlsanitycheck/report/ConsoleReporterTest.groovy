package org.aim42.htmlsanitycheck.report

import org.aim42.htmlsanitycheck.collect.Finding
import org.aim42.htmlsanitycheck.collect.PerRunResults
import org.aim42.htmlsanitycheck.collect.SingleCheckResults
import org.aim42.htmlsanitycheck.collect.SinglePageResults
import org.junit.After
import org.junit.Before
import org.junit.Test

import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertTrue

// see end-of-file for license information


class ConsoleReporterTest {

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream()
    private final PrintStream originalOut = System.out

    Finding singleFinding
    SingleCheckResults singleCheckResults
    SinglePageResults singlePageResults
    PerRunResults runResults

    Reporter reporter

    String whatToExpect

    @Before
    void setUp() {
        System.setOut(new PrintStream(outContent))
        singleCheckResults = new SingleCheckResults()
        singleFinding = new Finding("")
        singlePageResults = new SinglePageResults()
        runResults = new PerRunResults()
        runResults.addPageResults(singlePageResults)

        // create empty Reporter without findings
        reporter = new ConsoleReporter(runResults)
    }

    @Test
    void testInitReport() {
        reporter.initReport()
        String expectedOutput = "********* HTML Sanity Checker findings report *********"
        assertTrue(outContent.toString().contains(expectedOutput))
    }

    @Test
    void testReportOverallSummary() {
        // Prepare the ConsoleReporter with some data
        singleCheckResults.incNrOfChecks()
        singleCheckResults.addFinding(singleFinding)
        addSingleCheckResultsToReporter(singleCheckResults)

        // Call the method to test
        reporter.reportOverallSummary()

        // Check the output
        String expectedOutput1 = "Summary for all pages:"
        String expectedOutput2 = "======================"
        String expectedOutput3 = "checked 1 items on 2 pages,"
        String expectedOutput4 = "found 1 issue, 0% successful."
        String expectedOutput5 = String.join("", Collections.nCopies(50, "-"))

        assertTrue(outContent.toString().contains(expectedOutput1))
        assertTrue(outContent.toString().contains(expectedOutput2))
        assertTrue(outContent.toString().contains(expectedOutput3))
        assertTrue(outContent.toString().contains(expectedOutput4))
        assertTrue(outContent.toString().contains(expectedOutput5))
    }

    @Test
    void testReportPageSummary() {
        // Prepare the ConsoleReporter with some data
        singlePageResults = new SinglePageResults(
                "test.html",
                "/path/to/test.html",
                "Test Page",
                1000,
                new ArrayList<>())
        runResults.addPageResults(singlePageResults)

        // Call the method to test
        reporter.reportPageSummary(singlePageResults)

        // Check the output
        String expectedOutput1 = "Summary for file test.html"
        String expectedOutput2 = "page path  : /path/to/test.html"
        String expectedOutput3 = "page title : Test Page"
        String expectedOutput4 = "page size  : 1000 bytes"

        assertTrue(outContent.toString().contains(expectedOutput1))
        assertTrue(outContent.toString().contains(expectedOutput2))
        assertTrue(outContent.toString().contains(expectedOutput3))
        assertTrue(outContent.toString().contains(expectedOutput4))
    }

    @Test
    void testReportPageFooter() {
        // Call the method to test
        reporter.reportPageFooter()

        // Check the output
        String expectedOutput = String.join("", Collections.nCopies(50, "="))

        assertTrue(outContent.toString().contains(expectedOutput))
    }

    @Test
    void testReportSingleCheckSummary() {
        // Prepare the ConsoleReporter with some data
        singleCheckResults.setWhatIsChecked("Test Check")
        singleCheckResults.setSourceItemName("Test Source")
        singleCheckResults.setTargetItemName("Test Target")
        singleCheckResults.setGeneralRemark("Test Remark")
        singleCheckResults.incNrOfChecks()
        singleCheckResults.addFinding(singleFinding)
        addSingleCheckResultsToReporter(singleCheckResults)

        // Call the method to test
        reporter.reportSingleCheckSummary(singleCheckResults)

        // Check the output
        String expectedOutput1 = "\n"
        String expectedOutput2 = String.join("", Collections.nCopies(50, "-"))
        String expectedOutput3 = "Results for Test Check"
        String expectedOutput4 = "1 Test Source checked,"
        String expectedOutput5 = "1 Test Target found.\n"
        String expectedOutput6 = "Test Remark"

        assertTrue(outContent.toString().contains(expectedOutput1))
        assertTrue(outContent.toString().contains(expectedOutput2))
        assertTrue(outContent.toString().contains(expectedOutput3))
        assertTrue(outContent.toString().contains(expectedOutput4))
        assertTrue(outContent.toString().contains(expectedOutput5))
        assertTrue(outContent.toString().contains(expectedOutput6))
    }

    @Test
    void testReportSingleCheckDetails() {
        // Prepare the ConsoleReporter with some data
        singleCheckResults.setWhatIsChecked("Test Check")
        singleCheckResults.setSourceItemName("Test Source")
        singleCheckResults.setTargetItemName("Test Target")
        singleCheckResults.setGeneralRemark("Test Remark")
        singleCheckResults.incNrOfChecks()
        Finding finding = new Finding("Test Finding")
        singleCheckResults.addFinding(finding)
        addSingleCheckResultsToReporter(singleCheckResults)

        // Call the method to test
        reporter.reportSingleCheckDetails(singleCheckResults)

        // Check the output
        String expectedOutput1 = finding.toString()
        String expectedOutput2 = String.join("", Collections.nCopies(50, "-"))

        assertTrue(outContent.toString().contains(expectedOutput1))
        assertTrue(outContent.toString().contains(expectedOutput2))
    }

    @Test
    void testCloseReport() {
        // Call the method to test
        reporter.closeReport()

        // Check the output
        String expectedOutput = "thanx for using HtmlSanityChecker."

        assertTrue(outContent.toString().contains(expectedOutput))
    }

    @Test
    void testEmptyReporter() {
        int expected = 0

        int actual = reporter.totalNrOfFindings()
        assertEquals("Empty reporter has no findings", expected, actual)

        actual = reporter.totalNrOfChecks()
        assertEquals("Empty reporter has no checks performed", expected, actual)
    }

    @Test
    void testZeroChecks() {
        addSingleCheckResultsToReporter(singleCheckResults)

        assertEquals("Zero checks expected", 0, reporter.totalNrOfChecks())
        assertEquals("Zero findings expected", 0, reporter.totalNrOfFindings())

    }

    @Test
    void testSingleFindingWithoutChecks() {
        // now add one finding, but no check.. (nonsense, should never occur)
        singleCheckResults.addFinding(singleFinding)
        addSingleCheckResultsToReporter(singleCheckResults)

        assertEquals("expected no check", 0, reporter.totalNrOfChecks())
        assertEquals("expected one finding", 1, reporter.totalNrOfFindings())
    }


    @Test
    void testOneFindingOneCheck() {
        singleCheckResults.addFinding(singleFinding)
        singleCheckResults.incNrOfChecks()

        addSingleCheckResultsToReporter(singleCheckResults)

        assertEquals("Expect one finding", 1, reporter.totalNrOfFindings())
        assertEquals("Expect one check", 1, reporter.totalNrOfChecks())


    }

    @Test
    void testOneFindingTenChecks() {
        // one finding, ten checks.. 90% successful
        singleCheckResults.addFinding(singleFinding)
        singleCheckResults.nrOfItemsChecked = 10

        addSingleCheckResultsToReporter(singleCheckResults)

        assertEquals("Expect one finding", 1, reporter.totalNrOfFindings())
        assertEquals("Expect ten checks", 10, reporter.totalNrOfChecks())

        whatToExpect = "10 checked, 1 finding -> 90% successful"
        int expected = 90
        int actual = SummarizerUtil.percentSuccessful(10, 1)
        assertEquals(whatToExpect, expected, actual)
    }

    @Test
    void testThreeFindingsTenChecks() {
        // three findings, ten checks.. 70% successful
        for (int i = 1; i <= 3; i++) {
            singleCheckResults.addFinding(new Finding("finding $i"))
        }
        singleCheckResults.nrOfItemsChecked = 10

        addSingleCheckResultsToReporter(singleCheckResults)

        assertEquals("Expect three findings", 3, reporter.totalNrOfFindings())
        assertEquals("Expect ten checks", 10, reporter.totalNrOfChecks())

        whatToExpect = "10 checked, 3 findings -> 70% successful"
        int expected = 70
        int actual = SummarizerUtil.percentSuccessful(10, 3)
        assertEquals(whatToExpect, expected, actual)
    }


    @Test
    void testOneFindingSixChecks() {
        singleCheckResults.addFinding(singleFinding)
        singleCheckResults.nrOfItemsChecked = 6

        addSingleCheckResultsToReporter(singleCheckResults)


        assertEquals("Expect one finding", 1, reporter.totalNrOfFindings())
        assertEquals("Expect six checks", 6, reporter.totalNrOfChecks())

        whatToExpect = "6 checks, 1 finding -> 83% successful"
        int expected = 83
        int actual = SummarizerUtil.percentSuccessful(6, 1)
        assertEquals(whatToExpect, expected, actual)
    }

    @Test
    void test99Findings200Checks() {
        int nrOfChecks = 200
        int nrOfFindings = 99

        for (int i = 1; i <= nrOfFindings; i++) {
            singleCheckResults.addFinding(new Finding("finding $i"))
        }
        singleCheckResults.nrOfItemsChecked = nrOfChecks

        addSingleCheckResultsToReporter(singleCheckResults)

        assertEquals("Expect $nrOfFindings findings", nrOfFindings, reporter.totalNrOfFindings())
        assertEquals("Expect $nrOfChecks checks", nrOfChecks, reporter.totalNrOfChecks())

        whatToExpect = "200 checks, 99 findings -> 50.5 (=50%) successful"
        int expected = 50
        int actual = SummarizerUtil.percentSuccessful(nrOfChecks, nrOfFindings)
        assertEquals(whatToExpect, expected, actual)
    }

    private void addSingleCheckResultsToReporter(SingleCheckResults scr) {
        SinglePageResults spr = new SinglePageResults()
        spr.addResultsForSingleCheck(scr)
        reporter.addCheckingResultsForOnePage(spr)
    }

    // reset the System.out after the test
    @After
    void restoreStreams() {
        System.setOut(originalOut)
    }
}
