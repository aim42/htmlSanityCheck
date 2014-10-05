package org.aim42.htmlsanitycheck.report

import org.aim42.htmlsanitycheck.collect.SinglePageResults
import org.junit.Test

// see end-of-file for license information


class ReporterTest extends GroovyTestCase {

    @Test
    public void testCreateReporter() {
        SinglePageResults spr = new SinglePageResults()
        Reporter reporter = new ConsoleReporter(spr)

        assertEquals( "Empty ConsoleReporter has no check", 0, reporter.totalNrOfChecks())

        assertEquals( "Empty Reporter shall have no findings", 0, reporter.totalNrOfFindings())
    }
}
