package org.aim42.htmlsanitycheck.report

import org.aim42.htmlsanitycheck.collect.Finding
import org.aim42.htmlsanitycheck.collect.PerRunResults
import org.aim42.htmlsanitycheck.collect.SingleCheckResults
import org.aim42.htmlsanitycheck.collect.SinglePageResults
import org.junit.After
import org.junit.Before
import org.junit.Test

import static org.junit.Assert.assertEquals

// see end-of-file for license information


class JUnitXmlReporterTest {

    Finding singleFinding
    SingleCheckResults singleCheckResults
    SinglePageResults  singlePageResults
    PerRunResults      runResults

    JUnitXmlReporter reporter
	File outputPath

    String whatToExpect

    @Before
    public void setUp() {
        singleCheckResults = new SingleCheckResults()

        singleFinding = new Finding("")

        singlePageResults = new SinglePageResults()

        runResults = new PerRunResults()

        outputPath = File.createTempDir()
        reporter = new JUnitXmlReporter( runResults, outputPath.absolutePath )
    }

	@After
	public void tearDown() {
		if (outputPath) {
			outputPath.traverse {
				System.err.println "${it}: ${it.text}"
			}
		}
		outputPath?.deleteDir()
	}
	
	private generateReportAndReturnTestsuiteNode() {
		reporter.reportFindings()
		new XmlSlurper().parse(new File(reporter.outputPath))
	}

    @Test
    public void testEmptyReporter() {
		reporter.reportFindings()
		assertEquals("Empty reporter has no JUnit results", 0, outputPath.listFiles().length)
    }


    @Test
    public void testZeroChecks() {
        addSingleCheckResultsToReporter( singleCheckResults )

		reporter.reportFindings()
		def testsuite = new XmlSlurper().parse(outputPath.listFiles()[0])
        assertEquals("Zero checks expected", "0", testsuite.@tests.text())
        assertEquals("Zero findings expected", "0", testsuite.@failures.text())
        assertEquals("Zero testcases expected", 1, testsuite.testcase.size())
    }

    @Test
    public void testSingleFindingWithoutChecks() {
        // now add one finding, but no check.. (nonsense, should never occur)
        singleCheckResults.addFinding(singleFinding)
        addSingleCheckResultsToReporter( singleCheckResults )

		reporter.reportFindings()
		def testsuite = new XmlSlurper().parse(outputPath.listFiles()[0])
        assertEquals("expected no check", "0", testsuite.@tests.text())
        assertEquals("expected one finding", "1", testsuite.@failures.text())
        assertEquals("One testcase expected", 1, testsuite.testcase.size())
        assertEquals("One testcase failures expected", 1, testsuite.testcase.failure.size())
    }


    @Test
    public void testOneFindingOneCheck() {
        singleCheckResults.addFinding(singleFinding)
        singleCheckResults.incNrOfChecks()

        addSingleCheckResultsToReporter( singleCheckResults )

		reporter.reportFindings()
		def testsuite = new XmlSlurper().parse(outputPath.listFiles()[0])
        assertEquals("Expect one finding", "1", testsuite.@failures.text())
        assertEquals("Expect one check", "1", testsuite.@tests.text())
        assertEquals("One testcase expected", 1, testsuite.testcase.size())
        assertEquals("One testcase failure expected", 1, testsuite.testcase.failure.size())
    }

    @Test
    public void testOneFindingTenChecks() {
        // one finding, ten checks.. 90% successful
        singleCheckResults.addFinding(singleFinding)
        singleCheckResults.nrOfItemsChecked = 10

        addSingleCheckResultsToReporter( singleCheckResults )

		reporter.reportFindings()
		def testsuite = new XmlSlurper().parse(outputPath.listFiles()[0])
        assertEquals("Expect one finding", "1", testsuite.@failures.text())
        assertEquals("Expect ten checks", "10", testsuite.@tests.text())
        assertEquals("Expect one testcase", 1, testsuite.testcase.size())
        assertEquals("One testcase failure expected", 1, testsuite.testcase.failure.size())
    }

    @Test
    public void testThreeFindingsTenChecks() {
        // three findings, ten checks.. 70% successful
        for (int i = 1; i<=3; i++) {
            singleCheckResults.addFinding( new Finding("finding $i"))
        }
        singleCheckResults.nrOfItemsChecked = 10

        addSingleCheckResultsToReporter( singleCheckResults )

		reporter.reportFindings()
		def testsuite = new XmlSlurper().parse(outputPath.listFiles()[0])
        assertEquals("Expect three findings", "3", testsuite.@failures.text())
        assertEquals("Expect ten checks", "10", testsuite.@tests.text())
        assertEquals("Expect one testcases", 1, testsuite.testcase.size())
        assertEquals("Expect three testcase failures", 3, testsuite.testcase.failure.size())
    }


    @Test
    public void testOneFindingSixChecks() {
        singleCheckResults.addFinding(singleFinding)
        singleCheckResults.nrOfItemsChecked = 6

        addSingleCheckResultsToReporter( singleCheckResults )

		reporter.reportFindings()
		def testsuite = new XmlSlurper().parse(outputPath.listFiles()[0])
        assertEquals("Expect one finding", "1", testsuite.@failures.text())
        assertEquals("Expect six checks", "6", testsuite.@tests.text())
        assertEquals("Expect one testcases", 1, testsuite.testcase.size())
        assertEquals("Expect six testcase failures", 1, testsuite.testcase.failure.size())
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

		reporter.reportFindings()
		def testsuite = new XmlSlurper().parse(outputPath.listFiles()[0])
        assertEquals("Expect $nrOfFindings findings", nrOfFindings as String, testsuite.@failures.text() )
        assertEquals("Expect $nrOfChecks checks", nrOfChecks as String, testsuite.@tests.text() )
        assertEquals("Expect one testcase", 1, testsuite.testcase.size())
        assertEquals("Expect $nrOfChecks testcase failures", nrOfFindings, testsuite.testcase.failure.size())
    }

	
    private void addSingleCheckResultsToReporter( SingleCheckResults scr ) {
        SinglePageResults spr = new SinglePageResults()
        spr.addResultsForSingleCheck( scr )
        reporter.addCheckingResultsForOnePage( spr )
    }
}
