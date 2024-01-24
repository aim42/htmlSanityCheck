package org.aim42.htmlsanitycheck.collect

import org.junit.Test

import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertTrue

// see end-of-file for license information


class PerRunResultsTest {

    @Test
    public void testEmptyRunResultHasNoPages() {
        PerRunResults runResults = new PerRunResults()

        assertEquals("expect no page checked", 0, runResults.nrOfPagesChecked())
        assertEquals("zero checks expected", 0, runResults.nrOfChecksPerformedOnAllPages())
    }

    // a run with just a single page...
    @Test
    public void testRunWithSinglePage() {
        int nrOfFindings = 3
        int nrOfChecks = 10

        SingleCheckResults scr = new SingleCheckResults()
        (1..nrOfFindings).each { scr.addFinding(new Finding("$it Finding")) }
        (1..nrOfChecks).each { scr.incNrOfChecks() }
        assertEquals("expect $nrOfChecks checks on SingleCheckResults", nrOfChecks,
                scr.nrOfItemsChecked)

        SinglePageResults spr = new SinglePageResults()
        spr.addResultsForSingleCheck(scr)

        PerRunResults runResults = new PerRunResults()
        runResults.addPageResults(spr)

        assertEquals("expect one page", 1, runResults.nrOfPagesChecked())
        assertEquals("expect $nrOfChecks checks on PerRunResults", nrOfChecks,
                runResults.nrOfChecksPerformedOnAllPages())

    }

    @Test
    public void testTimerForRun() {
        int nrOfFindings = 17
        int nrOfChecks = 100
        Long magicNumber = PerRunResults.ILLEGAL_TIMER

        // create instance and start timer
        PerRunResults runResults = new PerRunResults()

        Long actual = runResults.checkingTookHowManyMillis()
        assertEquals("illegal timer should yield magic number", magicNumber, actual)


        SingleCheckResults scr = new SingleCheckResults()
        (1..nrOfFindings).each { scr.addFinding(new Finding("$it Finding")) }
        (1..nrOfChecks).each { scr.incNrOfChecks() }

        SinglePageResults spr = new SinglePageResults()
        spr.addResultsForSingleCheck(scr)
        runResults.addPageResults(spr)

        runResults.stopTimer()

        actual = runResults.checkingTookHowManyMillis()
        assertTrue ("timer should yield positive number", actual > 1)

    }
}
