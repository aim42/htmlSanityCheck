package org.aim42.htmlsanitycheck.report

import org.aim42.htmlsanitycheck.collect.Finding
import org.aim42.htmlsanitycheck.collect.SingleCheckResults
import org.aim42.htmlsanitycheck.collect.SinglePageResults
import org.junit.Test

// see end-of-file for license information


class ConsoleReporterTest extends GroovyTestCase {

    SingleCheckResults singleCheckResults
    Finding singleFinding

    Reporter reporter


    int expected
    int actual
    String whatToExpect


    public void setUp() {
        singleCheckResults = new SingleCheckResults()

        singleFinding = new Finding()

        // create empty Reporter without findings
        reporter = new ConsoleReporter( new SinglePageResults() )

    }


    @Test
    public void testEmptyReporter() {

        whatToExpect = "Empty reporter has no findings"
        expected = 0
        actual = reporter.totalNrOfFindings()
        assertEquals(whatToExpect, expected, actual)

        actual = reporter.totalNrOfChecks()
        whatToExpect = "Empty reporter has no checks performed"
        assertEquals(whatToExpect, expected, actual)
    }


    @Test
    public void testZeroChecks() {
        reporter.addCheckingField(singleCheckResults)

        assertEquals("Zero checks expected", 0, reporter.pageResults.totalNrOfItemsChecked())
        assertEquals("Zero findings expected", 0, reporter.pageResults.totalNrOfFindings())


        whatToExpect = "0 checks should mean 100% success rate"
        expected = 100
        actual = reporter.pageResults.percentSuccessful()
        assertEquals(whatToExpect, expected, actual);
    }

    @Test
    public void testSingleFindingWithoutChecks() {
        // now add one finding, but no check.. (nonsense, should never occur)
        singleCheckResults.addFinding(singleFinding)
        reporter.addCheckingField(singleCheckResults)

        whatToExpect = "0 checked, 1 finding -> nonsense, but 100% success rate"
        expected = 100
        actual = reporter.pageResults.percentSuccessful()
        assertEquals(whatToExpect, expected, actual)
    }

    @Test
    public void testOneFindingOneCheck() {
        // one finding, one check.. 0% successful
        singleCheckResults.addFinding(singleFinding)
        singleCheckResults.incNrOfChecks()

        reporter.addCheckingField(singleCheckResults)

        assertEquals("Expect one finding", 1, reporter.pageResults.totalNrOfFindings())
        assertEquals("Expect one check", 1, reporter.pageResults.totalNrOfItemsChecked())

        whatToExpect = "1 checked, 1 finding -> 0% successful"
        expected = 0
        actual = reporter.pageResults.percentSuccessful()
        assertEquals(whatToExpect, expected, actual)

    }

    @Test
    public void testOneFindingTenChecks() {
        // one finding, ten checks.. 90% successful
        singleCheckResults.addFinding(singleFinding)
        singleCheckResults.nrOfItemsChecked = 10

        reporter.addCheckingField(singleCheckResults)

        assertEquals("Expect one finding", 1, reporter.pageResults.totalNrOfFindings())
        assertEquals("Expect ten checks", 10, reporter.pageResults.totalNrOfItemsChecked())

        whatToExpect = "10 checked, 1 finding -> 90% successful"
        expected = 90
        actual = reporter.pageResults.percentSuccessful()
        assertEquals(whatToExpect, expected, actual)
    }

    @Test
    public void testThreeFindingsTenChecks() {
        // three findings, ten checks.. 70% successful
        for (int i = 1; i<=3; i++) {
            singleCheckResults.addFinding( new Finding("finding $i"))
        }
        singleCheckResults.nrOfItemsChecked = 10

        reporter.addCheckingField(singleCheckResults)

        assertEquals("Expect three findings", 3, reporter.pageResults.totalNrOfFindings())
        assertEquals("Expect ten checks", 10, reporter.pageResults.totalNrOfItemsChecked())

        whatToExpect = "10 checked, 3 findings -> 70% successful"
        expected = 70
        actual = reporter.pageResults.percentSuccessful()
        assertEquals(whatToExpect, expected, actual)
    }

    @Test
    public void testOneFindingSixChecks() {
        singleCheckResults.addFinding(singleFinding)
        singleCheckResults.nrOfItemsChecked = 6

        reporter.addCheckingField(singleCheckResults)


        assertEquals("Expect one finding", 1, reporter.pageResults.totalNrOfFindings())
        assertEquals("Expect six checks", 6, reporter.pageResults.totalNrOfItemsChecked())

        whatToExpect = "6 checks, 1 finding -> 83% successful"
        expected = 83
        actual = reporter.pageResults.percentSuccessful()
        assertEquals(whatToExpect, expected, actual)
    }

    @Test
    public void test99Findings200Checks() {

        for (int i = 1; i <= 99; i++) {
            singleCheckResults.addFinding( new Finding( "finding $i"))
        }
        singleCheckResults.nrOfItemsChecked = 200

        reporter.addCheckingField(singleCheckResults)

        assertEquals("Expect 99 findings", 99, reporter.pageResults.totalNrOfFindings() )
        assertEquals("Expect ten checks", 200, reporter.pageResults.totalNrOfItemsChecked() )

        whatToExpect = "200 checks, 99 findings -> 50.5 (=50%) successful"
        expected = 50
        actual = reporter.pageResults.percentSuccessful()
        assertEquals(whatToExpect, expected, actual)
    }
}
