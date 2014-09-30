package org.aim42.htmlsanitycheck.report

import org.aim42.htmlsanitycheck.collect.Finding
import org.aim42.htmlsanitycheck.collect.SingleCheckResults
import org.junit.Test

// see end-of-file for license information


class CalculateSummaryTest extends GroovyTestCase {

    SingleCheckResults checkingField
    Finding singleFinding

    FindingsForPageReporter reporter


    int expected
    int actual
    String whatToExpect


    public void setUp() {
        checkingField = new SingleCheckResults()

        // create empty Reporter without findings
        reporter = new FindingsForPageConsoleReporter( new ArrayList<SingleCheckResults>( ))

        singleFinding = new Finding()
    }


    @Test
    public void testEmptyReporter() {
        whatToExpect = "Empty reporter has no findings"
        expected = 0
        actual = reporter.totalNrOfFindings
        assertEquals(whatToExpect, expected, actual)

        actual = reporter.totalNrOfChecksPerformed
        whatToExpect = "Empty reporter has no checks performed"
        assertEquals(whatToExpect, expected, actual)
    }


    @Test
    public void testZeroChecks() {
        // add CheckingResults without Finding (note: singular vs plural)
        reporter.addCheckingField(checkingField)

        assertEquals("Zero checks expected", 0, reporter.totalNrOfChecksPerformed)
        assertEquals("Zero findings expected", 0, reporter.totalNrOfFindings)

        reporter.calculateSummary()

        whatToExpect = "0 checks should mean 100% success rate"
        expected = 100
        actual = reporter.percentSuccessful
        assertEquals(whatToExpect, expected, actual);
    }

    @Test
    public void testSingleFindingWithoutChecks() {
        // now add one finding, but no check... (nonsense, should never occur)
        checkingField.addFinding(singleFinding)
        reporter.addCheckingField(checkingField)
        whatToExpect = "0 checked, 1 finding -> nonsense, but 100% success rate"
        expected = 100
        reporter.calculateSummary()
        actual = reporter.percentSuccessful
        assertEquals(whatToExpect, expected, actual)
    }

    @Test
    public void testOneFindingOneCheck() {
        // one finding, one check... 0% successful
        checkingField.addFinding(singleFinding)
        checkingField.nrOfItemsChecked = 1

        reporter.addCheckingField(checkingField)
        reporter.calculateSummary()

        assertEquals("Expect one finding", 1, reporter.totalNrOfFindings)
        assertEquals("Expect one check", 1, reporter.totalNrOfChecksPerformed)

        whatToExpect = "1 checked, 1 finding -> 0% successful"
        expected = 0
        actual = reporter.percentSuccessful
        assertEquals(whatToExpect, expected, actual)

        // now 1 check, 1 finding -> 0% success
        whatToExpect = "1 check, 1 finding -> 0% success rate"

        // 1 checked, 1 finding = 0% success rate

        // 2 checked, 1 finding = 50% success rate


    }

    @Test
    public void testOneFindingTenChecks() {
        // one finding, ten checks... 90% successful
        checkingField.addFinding(singleFinding)
        checkingField.nrOfItemsChecked = 10

        reporter.addCheckingField(checkingField)
        reporter.calculateSummary()

        assertEquals("Expect one finding", 1, reporter.totalNrOfFindings)
        assertEquals("Expect ten checks", 10, reporter.totalNrOfChecksPerformed)

        whatToExpect = "10 checked, 1 finding -> 90% successful"
        expected = 90
        actual = reporter.percentSuccessful
        assertEquals(whatToExpect, expected, actual)
    }

    @Test
    public void testThreeFindingsTenChecks() {
        // three findings, ten checks... 70% successful
        (1..3).each { checkingField.addFinding(singleFinding) }
        checkingField.nrOfItemsChecked = 10

        reporter.addCheckingField(checkingField)
        reporter.calculateSummary()

        assertEquals("Expect three findings", 3, reporter.totalNrOfFindings)
        assertEquals("Expect ten checks", 10, reporter.totalNrOfChecksPerformed)

        whatToExpect = "10 checked, 3 findings -> 70% successful"
        expected = 70
        actual = reporter.percentSuccessful
        assertEquals(whatToExpect, expected, actual)
    }

    @Test
    public void testOneFindingSixChecks() {
        checkingField.addFinding(singleFinding)
        checkingField.nrOfItemsChecked = 6

        reporter.addCheckingField(checkingField)
        reporter.calculateSummary()

        assertEquals("Expect one finding", 1, reporter.totalNrOfFindings)
        assertEquals("Expect six checks", 6, reporter.totalNrOfChecksPerformed)

        whatToExpect = "6 checks, 1 finding -> 83% successful"
        expected = 83
        actual = reporter.percentSuccessful
        assertEquals(whatToExpect, expected, actual)
    }

    @Test
    public void test99Findings200Checks() {
        // 99 findings, 200 checks... 50% successful
        (1..99).each { checkingField.addFinding(singleFinding) }
        checkingField.nrOfItemsChecked = 200

        reporter.addCheckingField(checkingField)
        reporter.calculateSummary()

        assertEquals("Expect three findings", 99, reporter.totalNrOfFindings)
        assertEquals("Expect ten checks", 200, reporter.totalNrOfChecksPerformed)

        whatToExpect = "200 checks, 99 findings -> 50.5 (=50%) successful"
        expected = 50
        actual = reporter.percentSuccessful
        assertEquals(whatToExpect, expected, actual)
    }
}
