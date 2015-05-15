package org.aim42.htmlsanitycheck.report

import org.aim42.htmlsanitycheck.collect.Finding
import org.aim42.htmlsanitycheck.collect.PerRunResults
import org.aim42.htmlsanitycheck.collect.SingleCheckResults
import org.aim42.htmlsanitycheck.collect.SinglePageResults
import org.junit.Before
import org.junit.Test

// see end-of-file for license information


class ConsoleReporterTest extends GroovyTestCase {

    Finding singleFinding
    SingleCheckResults singleCheckResults
    SinglePageResults  singlePageResults
    PerRunResults      runResults

    Reporter reporter

    String whatToExpect

    @Before
    public void setUp() {
        singleCheckResults = new SingleCheckResults()

        singleFinding = new Finding()

        singlePageResults = new SinglePageResults()

        runResults = new PerRunResults()
        runResults.addPageResults(singlePageResults)

        // create empty Reporter without findings
        reporter = new ConsoleReporter( runResults )

    }


    @Test
    public void testEmptyReporter() {

        //Reporter reporter = new ConsoleReporter( singlePageResults )
        int expected = 0

        int actual = reporter.totalNrOfFindings()
        assertEquals("Empty reporter has no findings", expected, actual)

        actual = reporter.totalNrOfChecks()
        assertEquals("Empty reporter has no checks performed", expected, actual)
    }


    @Test
    public void testZeroChecks() {
        addSingleCheckResultsToReporter( singleCheckResults )

        assertEquals("Zero checks expected", 0, reporter.totalNrOfChecks())
        assertEquals("Zero findings expected", 0, reporter.totalNrOfFindings())

    }

    @Test
    public void testSingleFindingWithoutChecks() {
        // now add one finding, but no check.. (nonsense, should never occur)
        singleCheckResults.addFinding(singleFinding)
        addSingleCheckResultsToReporter( singleCheckResults )

        assertEquals("expected no check", 0, reporter.totalNrOfChecks())
        assertEquals("expected one finding", 1, reporter.totalNrOfFindings())
    }


    @Test
    public void testOneFindingOneCheck() {
        singleCheckResults.addFinding(singleFinding)
        singleCheckResults.incNrOfChecks()

        addSingleCheckResultsToReporter( singleCheckResults )

        assertEquals("Expect one finding", 1, reporter.totalNrOfFindings())
        assertEquals("Expect one check", 1, reporter.totalNrOfChecks())


    }

    @Test
    public void testOneFindingTenChecks() {
        // one finding, ten checks.. 90% successful
        singleCheckResults.addFinding(singleFinding)
        singleCheckResults.nrOfItemsChecked = 10

        addSingleCheckResultsToReporter( singleCheckResults )

        assertEquals("Expect one finding", 1, reporter.totalNrOfFindings())
        assertEquals("Expect ten checks", 10, reporter.totalNrOfChecks())

        whatToExpect = "10 checked, 1 finding -> 90% successful"
        int expected = 90
        int actual = SummarizerUtil.percentSuccessful( 10, 1 )
        assertEquals(whatToExpect, expected, actual)
    }

    @Test
    public void testThreeFindingsTenChecks() {
        // three findings, ten checks.. 70% successful
        for (int i = 1; i<=3; i++) {
            singleCheckResults.addFinding( new Finding("finding $i"))
        }
        singleCheckResults.nrOfItemsChecked = 10

        addSingleCheckResultsToReporter( singleCheckResults )

        assertEquals("Expect three findings", 3, reporter.totalNrOfFindings())
        assertEquals("Expect ten checks", 10, reporter.totalNrOfChecks())

        whatToExpect = "10 checked, 3 findings -> 70% successful"
        int expected = 70
        int actual = SummarizerUtil.percentSuccessful( 10, 3)
        assertEquals(whatToExpect, expected, actual)
    }


    @Test
    public void testOneFindingSixChecks() {
        singleCheckResults.addFinding(singleFinding)
        singleCheckResults.nrOfItemsChecked = 6

        addSingleCheckResultsToReporter( singleCheckResults )


        assertEquals("Expect one finding", 1, reporter.totalNrOfFindings())
        assertEquals("Expect six checks", 6, reporter.totalNrOfChecks())

        whatToExpect = "6 checks, 1 finding -> 83% successful"
        int expected = 83
        int actual = SummarizerUtil.percentSuccessful(6, 1)
        assertEquals(whatToExpect, expected, actual)
    }

    @Test
    public void test99Findings200Checks() {
        int nrOfChecks = 200
        int nrOfFindings = 99

        for (int i = 1; i <= nrOfFindings; i++) {
            singleCheckResults.addFinding( new Finding( "finding $i"))
        }
        singleCheckResults.nrOfItemsChecked = nrOfChecks

        addSingleCheckResultsToReporter( singleCheckResults )

        assertEquals("Expect $nrOfFindings findings", nrOfFindings, reporter.totalNrOfFindings() )
        assertEquals("Expect $nrOfChecks checks", nrOfChecks, reporter.totalNrOfChecks() )

        whatToExpect = "200 checks, 99 findings -> 50.5 (=50%) successful"
        int expected = 50
        int actual = SummarizerUtil.percentSuccessful( nrOfChecks, nrOfFindings )
        assertEquals(whatToExpect, expected, actual)
    }



    private void addSingleCheckResultsToReporter( SingleCheckResults scr ) {
        SinglePageResults spr = new SinglePageResults()
        spr.addResultsForSingleCheck( scr )
        reporter.addCheckingResultsForOnePage( spr )

    }
}
