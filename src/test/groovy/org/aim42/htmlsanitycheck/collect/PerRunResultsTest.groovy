package org.aim42.htmlsanitycheck.collect

import org.junit.Test

// see end-of-file for license information


class PerRunResultsTest extends GroovyTestCase {

    @Test
    public void testEmptyRunResultHasNoPages() {
        PerRunResults runResults = new PerRunResults()

        assertEquals( "expect no page checked", 0, runResults.nrOfPagesChecked())
        assertEquals( "zero checks expected", 0, runResults.nrOfChecksPerformedOnAllPages())
    }

    // a run with just a single page...
    @Test
    public void testRunWithSinglePage() {
        int nrOfFindings = 3
        int nrOfChecks   = 10

        SingleCheckResults scr = new SingleCheckResults()
        (1..nrOfFindings).each { scr.addFinding(new Finding("$it Finding"))}
        (1..nrOfChecks).each { scr.incNrOfChecks()}
        assertEquals( "expect $nrOfChecks checks on SingleCheckResults", nrOfChecks,
                        scr.nrOfItemsChecked)

        SinglePageResults spr = new SinglePageResults()
        spr.addResultsForSingleCheck( scr )

        PerRunResults runResults = new PerRunResults()
        runResults.addPageResults( spr )

        assertEquals("expect one page", 1, runResults.nrOfPagesChecked())
        assertEquals("expect $nrOfChecks checks on PerRunResults", nrOfChecks,
            runResults.nrOfChecksPerformedOnAllPages())

    }
}
