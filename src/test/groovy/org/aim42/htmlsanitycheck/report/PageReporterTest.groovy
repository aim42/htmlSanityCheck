package org.aim42.htmlsanitycheck.report

import org.aim42.htmlsanitycheck.collect.Finding
import org.aim42.htmlsanitycheck.collect.SingleCheckResults
import org.aim42.htmlsanitycheck.collect.SinglePageResults
import org.junit.Test

// see end-of-file for license information


class PageReporterTest extends GroovyTestCase {

    SingleCheckResults singleCheckResults
    Finding singleFinding

    FindingsForPageReporter reporter


    int expected
    int actual
    String whatToExpect


    public void setUp() {
        singleCheckResults = new SingleCheckResults()

        singleFinding = new Finding()

        // create empty Reporter without findings
        reporter = new FindingsForPageConsoleReporter( new SinglePageResults() )

    }


    @Test
    public void testEmptyReporter() {

        whatToExpect = "Empty reporter has no findings"
        expected = 0
        actual = reporter.statistics.totalNrOfFindings
        assertEquals(whatToExpect, expected, actual)

        actual = reporter.statistics.totalNrOfChecksPerformed
        whatToExpect = "Empty reporter has no checks performed"
        assertEquals(whatToExpect, expected, actual)
    }


    @Test
    public void testZeroChecks() {
        reporter.addCheckingField(singleCheckResults)

        assertEquals("Zero checks expected", 0, reporter.statistics.totalNrOfChecksPerformed)
        assertEquals("Zero findings expected", 0, reporter.statistics.totalNrOfFindings)


        whatToExpect = "0 checks should mean 100% success rate"
        expected = 100
        actual = reporter.statistics.percentSuccessful
        assertEquals(whatToExpect, expected, actual);
    }

    @Test
    public void testSingleFindingWithoutChecks() {
        // now add one finding, but no check... (nonsense, should never occur)
        singleCheckResults.addFinding(singleFinding)
        reporter.addCheckingField(singleCheckResults)

        whatToExpect = "0 checked, 1 finding -> nonsense, but 100% success rate"
        expected = 100
        actual = reporter.statistics.percentSuccessful
        assertEquals(whatToExpect, expected, actual)
    }

    @Test
    public void testOneFindingOneCheck() {
        // one finding, one check... 0% successful
        singleCheckResults.addFinding(singleFinding)
        singleCheckResults.incNrOfChecks()

        reporter.addCheckingField(singleCheckResults)

        assertEquals("Expect one finding", 1, reporter.statistics.totalNrOfFindings)
        assertEquals("Expect one check", 1, reporter.statistics.totalNrOfChecksPerformed)

        whatToExpect = "1 checked, 1 finding -> 0% successful"
        expected = 0
        actual = reporter.statistics.percentSuccessful
        assertEquals(whatToExpect, expected, actual)

    }

    @Test
    public void testOneFindingTenChecks() {
        // one finding, ten checks... 90% successful
        singleCheckResults.addFinding(singleFinding)
        singleCheckResults.nrOfItemsChecked = 10

        reporter.addCheckingField(singleCheckResults)

        assertEquals("Expect one finding", 1, reporter.statistics.totalNrOfFindings)
        assertEquals("Expect ten checks", 10, reporter.statistics.totalNrOfChecksPerformed)

        whatToExpect = "10 checked, 1 finding -> 90% successful"
        expected = 90
        actual = reporter.statistics.percentSuccessful
        assertEquals(whatToExpect, expected, actual)
    }

    @Test
    public void testThreeFindingsTenChecks() {
        // three findings, ten checks... 70% successful
        (1..3).each { singleCheckResults.addFinding(singleFinding) }
        singleCheckResults.nrOfItemsChecked = 10

        reporter.addCheckingField(singleCheckResults)

        assertEquals("Expect three findings", 3, reporter.statistics.totalNrOfFindings)
        assertEquals("Expect ten checks", 10, reporter.statistics.totalNrOfChecksPerformed)

        whatToExpect = "10 checked, 3 findings -> 70% successful"
        expected = 70
        actual = reporter.statistics.percentSuccessful
        assertEquals(whatToExpect, expected, actual)
    }

    @Test
    public void testOneFindingSixChecks() {
        singleCheckResults.addFinding(singleFinding)
        singleCheckResults.nrOfItemsChecked = 6

        reporter.addCheckingField(singleCheckResults)


        assertEquals("Expect one finding", 1, reporter.statistics.totalNrOfFindings)
        assertEquals("Expect six checks", 6, reporter.statistics.totalNrOfChecksPerformed)

        whatToExpect = "6 checks, 1 finding -> 83% successful"
        expected = 83
        actual = reporter.statistics.percentSuccessful
        assertEquals(whatToExpect, expected, actual)
    }

    @Test
    public void test99Findings200Checks() {
        // 99 findings, 200 checks... 50% successful
        (1..99).each { singleCheckResults.addFinding(singleFinding) }
        singleCheckResults.nrOfItemsChecked = 200

        reporter.addCheckingField(singleCheckResults)

        assertEquals("Expect three findings", 99, reporter.statistics.totalNrOfFindings)
        assertEquals("Expect ten checks", 200, reporter.statistics.totalNrOfChecksPerformed)

        whatToExpect = "200 checks, 99 findings -> 50.5 (=50%) successful"
        expected = 50
        actual = reporter.statistics.percentSuccessful
        assertEquals(whatToExpect, expected, actual)
    }
}
