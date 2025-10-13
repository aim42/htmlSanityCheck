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


class JUnitXmlReporterTest {

    Finding singleFinding
    SingleCheckResults singleCheckResults
    SinglePageResults  singlePageResults
    PerRunResults      runResults

    JUnitXmlReporter reporter
	File outputPath

    @Before
    void setUp() {
        singleCheckResults = new SingleCheckResults()

        singleFinding = new Finding("")

        singlePageResults = new SinglePageResults()

        runResults = new PerRunResults()

        outputPath = File.createTempDir()
        reporter = new JUnitXmlReporter( runResults, outputPath.absolutePath )
    }

	@After
	void tearDown() {
		if (outputPath) {
			outputPath.traverse {
				if (it.isFile()) {
					System.err.println "${it}: ${it.text}"
				} else {
					System.err.println "${it}: [directory]"
				}
			}
		}
		outputPath?.deleteDir()
	}
	
    @Test(expected = RuntimeException.class)
    void testInitReportWithNonWritableDirectory() throws IOException {
        // Create a temporary directory
        File tempDir = tempFolder.newFolder()

        // Make the directory non-writable
        assertTrue("Could not make temp directory non-writable", tempDir.setWritable(false))

        // Create a new JUnitXmlReporter with the non-writable directory
        PerRunResults runResults = new PerRunResults()
        new JUnitXmlReporter(runResults, tempDir.getAbsolutePath()).initReport()
    }

    @Test
    void testEmptyFilepath() {
        SinglePageResults singlePageResultsWithoutFilepath
            =new SinglePageResults(
                "test.html",
                null,
                "Test Page",
                1000,
                new ArrayList<>())
        PerRunResults runResults = new PerRunResults()
        runResults.addPageResults(singlePageResultsWithoutFilepath)
        new JUnitXmlReporter( runResults, outputPath.absolutePath ).reportPageSummary(singlePageResultsWithoutFilepath)
        def testsuite = new XmlSlurper().parse(outputPath.listFiles()[0])
        assertEquals("Test Page", testsuite.@name.text())
    }

    @Test
    void testEmptyReporter() {
		reporter.reportFindings()
		assertEquals("Empty reporter has no JUnit results", 0, outputPath.listFiles().length)
    }


    @Test
    void testZeroChecks() {
        addSingleCheckResultsToReporter( singleCheckResults )

		reporter.reportFindings()
		def testsuite = new XmlSlurper().parse(findFirstXmlFile(outputPath))
        assertEquals("Zero checks expected", "0", testsuite.@tests.text())
        assertEquals("Zero findings expected", "0", testsuite.@failures.text())
        assertEquals("Zero testcases expected", 1, testsuite.testcase.size())
    }

    @Test
    void testSingleFindingWithoutChecks() {
        // now add one finding, but no check.. (nonsense, should never occur)
        singleCheckResults.addFinding(singleFinding)
        addSingleCheckResultsToReporter( singleCheckResults )

		reporter.reportFindings()
		def testsuite = new XmlSlurper().parse(findFirstXmlFile(outputPath))
        assertEquals("expected no check", "0", testsuite.@tests.text())
        assertEquals("expected one finding", "1", testsuite.@failures.text())
        assertEquals("One testcase expected", 1, testsuite.testcase.size())
        assertEquals("One testcase failures expected", 1, testsuite.testcase.failure.size())
    }


    @Test
    void testOneFindingOneCheck() {
        singleCheckResults.addFinding(singleFinding)
        singleCheckResults.incNrOfChecks()

        addSingleCheckResultsToReporter( singleCheckResults )

		reporter.reportFindings()
		def testsuite = new XmlSlurper().parse(findFirstXmlFile(outputPath))
        assertEquals("Expect one finding", "1", testsuite.@failures.text())
        assertEquals("Expect one check", "1", testsuite.@tests.text())
        assertEquals("One testcase expected", 1, testsuite.testcase.size())
        assertEquals("One testcase failure expected", 1, testsuite.testcase.failure.size())
    }

    @Test
    void testOneFindingTenChecks() {
        // one finding, ten checks.. 90% successful
        singleCheckResults.addFinding(singleFinding)
        singleCheckResults.nrOfItemsChecked = 10

        addSingleCheckResultsToReporter( singleCheckResults )

		reporter.reportFindings()
		def testsuite = new XmlSlurper().parse(findFirstXmlFile(outputPath))
        assertEquals("Expect one finding", "1", testsuite.@failures.text())
        assertEquals("Expect ten checks", "10", testsuite.@tests.text())
        assertEquals("Expect one testcase", 1, testsuite.testcase.size())
        assertEquals("One testcase failure expected", 1, testsuite.testcase.failure.size())
    }

    @Test
    void testThreeFindingsTenChecks() {
        // three findings, ten checks.. 70% successful
        for (int i = 1; i<=3; i++) {
            singleCheckResults.addFinding( new Finding("finding $i"))
        }
        singleCheckResults.nrOfItemsChecked = 10

        addSingleCheckResultsToReporter( singleCheckResults )

		reporter.reportFindings()
		def testsuite = new XmlSlurper().parse(findFirstXmlFile(outputPath))
        assertEquals("Expect three findings", "3", testsuite.@failures.text())
        assertEquals("Expect ten checks", "10", testsuite.@tests.text())
        assertEquals("Expect one testcases", 1, testsuite.testcase.size())
        assertEquals("Expect three testcase failures", 3, testsuite.testcase.failure.size())
    }


    @Test
    void testOneFindingSixChecks() {
        singleCheckResults.addFinding(singleFinding)
        singleCheckResults.nrOfItemsChecked = 6

        addSingleCheckResultsToReporter( singleCheckResults )

		reporter.reportFindings()
		def testsuite = new XmlSlurper().parse(findFirstXmlFile(outputPath))
        assertEquals("Expect one finding", "1", testsuite.@failures.text())
        assertEquals("Expect six checks", "6", testsuite.@tests.text())
        assertEquals("Expect one testcases", 1, testsuite.testcase.size())
        assertEquals("Expect six testcase failures", 1, testsuite.testcase.failure.size())
    }

    @Test
    void test99Findings200Checks() {
        int nrOfChecks = 200
        int nrOfFindings = 99

        for (int i = 1; i <= nrOfFindings; i++) {
            singleCheckResults.addFinding( new Finding( "finding $i"))
        }
        singleCheckResults.nrOfItemsChecked = nrOfChecks

        addSingleCheckResultsToReporter( singleCheckResults )

		reporter.reportFindings()
		def testsuite = new XmlSlurper().parse(findFirstXmlFile(outputPath))
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

    // Helper method to find XML files recursively in a directory
    private File findFirstXmlFile(File dir) {
        File[] files = dir.listFiles()
        if (files == null) return null

        // First look for XML files in current directory
        for (File file : files) {
            if (file.isFile() && file.name.endsWith('.xml')) {
                return file
            }
        }

        // Then recurse into subdirectories
        for (File file : files) {
            if (file.isDirectory()) {
                File found = findFirstXmlFile(file)
                if (found != null) {
                    return found
                }
            }
        }

        return null
    }

    // Tests for hierarchical directory structure (issue #405)

    @Test
    void testSimpleFilenameCreatesFileInRootDirectory() {
        // Given: a page with a simple filename (no directory path)
        SinglePageResults singlePageResultsWithSimplePath = new SinglePageResults(
                "index.html",
                "index.html",
                "Home Page",
                1000,
                new ArrayList<>())
        PerRunResults runResults = new PerRunResults()
        runResults.addPageResults(singlePageResultsWithSimplePath)

        // When: we generate the report
        new JUnitXmlReporter(runResults, outputPath.absolutePath).reportPageSummary(singlePageResultsWithSimplePath)

        // Then: the test file should be created directly in the output directory
        File expectedFile = new File(outputPath, "TEST-index.html.xml")
        assertTrue("Expected file in root: ${expectedFile.absolutePath}", expectedFile.exists())

        def testsuite = new XmlSlurper().parse(expectedFile)
        assertEquals("index.html", testsuite.@name.text())
    }

    @Test
    void testSingleLevelDirectoryCreatesSubdirectory() {
        // Given: a page with a single-level directory path
        SinglePageResults singlePageResultsWithPath = new SinglePageResults(
                "about.html",
                "docs/about.html",
                "About Page",
                1000,
                new ArrayList<>())
        PerRunResults runResults = new PerRunResults()
        runResults.addPageResults(singlePageResultsWithPath)

        // When: we generate the report
        new JUnitXmlReporter(runResults, outputPath.absolutePath).reportPageSummary(singlePageResultsWithPath)

        // Then: the test file should be created in a subdirectory
        File expectedDir = new File(outputPath, "docs")
        File expectedFile = new File(expectedDir, "TEST-about.html.xml")
        assertTrue("Expected directory to exist: ${expectedDir.absolutePath}", expectedDir.exists())
        assertTrue("Expected file to exist: ${expectedFile.absolutePath}", expectedFile.exists())

        def testsuite = new XmlSlurper().parse(expectedFile)
        assertEquals("docs/about.html", testsuite.@name.text())
    }

    @Test
    void testDeepNestedDirectoryCreatesFullHierarchy() {
        // Given: a page with a deeply nested directory path
        String deepPath = "docs/guide/user/installation/linux.html"
        SinglePageResults singlePageResultsWithDeepPath = new SinglePageResults(
                "linux.html",
                deepPath,
                "Linux Installation Guide",
                1000,
                new ArrayList<>())
        PerRunResults runResults = new PerRunResults()
        runResults.addPageResults(singlePageResultsWithDeepPath)

        // When: we generate the report
        new JUnitXmlReporter(runResults, outputPath.absolutePath).reportPageSummary(singlePageResultsWithDeepPath)

        // Then: the full directory hierarchy should be created
        File expectedDir = new File(outputPath, "docs/guide/user/installation")
        File expectedFile = new File(expectedDir, "TEST-linux.html.xml")
        assertTrue("Expected directory hierarchy to exist: ${expectedDir.absolutePath}", expectedDir.exists())
        assertTrue("Expected file to exist: ${expectedFile.absolutePath}", expectedFile.exists())

        def testsuite = new XmlSlurper().parse(expectedFile)
        assertEquals(deepPath, testsuite.@name.text())
    }

    @Test
    void testVeryLongPathDoesNotExceedFilenameLimit() {
        // Given: a page with a very long path (reproducing issue #405)
        // This creates a path longer than 255 characters when flattened to a single filename
        String longPath = "very/long/path/with/many/nested/directories/that/would/exceed/filesystem/limits/" +
                "if/flattened/into/a/single/filename/this/is/a/test/case/for/issue/405/" +
                "more/directories/to/make/it/really/long/and/problematic/for/flat/structure/" +
                "final/level/index.html"

        SinglePageResults singlePageResultsWithLongPath = new SinglePageResults(
                "index.html",
                longPath,
                "Deep Page",
                1000,
                new ArrayList<>())
        PerRunResults runResults = new PerRunResults()
        runResults.addPageResults(singlePageResultsWithLongPath)

        // When: we generate the report (should not throw exception)
        new JUnitXmlReporter(runResults, outputPath.absolutePath).reportPageSummary(singlePageResultsWithLongPath)

        // Then: the file should be created successfully with proper directory structure
        File parentPath = new File(longPath).parentFile
        File expectedDir = new File(outputPath, parentPath.path)
        File expectedFile = new File(expectedDir, "TEST-index.html.xml")
        assertTrue("Expected directory hierarchy to exist: ${expectedDir.absolutePath}", expectedDir.exists())
        assertTrue("Expected file to exist: ${expectedFile.absolutePath}", expectedFile.exists())

        // Verify the filename itself is short
        assertTrue("Filename should be short", expectedFile.name.length() < 50)

        def testsuite = new XmlSlurper().parse(expectedFile)
        assertEquals(longPath, testsuite.@name.text())
    }

    @Test
    void testMultiplePagesCreateSeparateDirectories() {
        // Given: multiple pages in different directories
        SinglePageResults page1 = new SinglePageResults(
                "index.html",
                "docs/api/index.html",
                "API Index",
                1000,
                new ArrayList<>())
        SinglePageResults page2 = new SinglePageResults(
                "index.html",
                "docs/guide/index.html",
                "Guide Index",
                1000,
                new ArrayList<>())

        PerRunResults runResults = new PerRunResults()
        runResults.addPageResults(page1)
        runResults.addPageResults(page2)

        JUnitXmlReporter reporter = new JUnitXmlReporter(runResults, outputPath.absolutePath)

        // When: we generate reports for both pages
        reporter.reportPageSummary(page1)
        reporter.reportPageSummary(page2)

        // Then: separate directory structures should be created
        File apiDir = new File(outputPath, "docs/api")
        File guideDir = new File(outputPath, "docs/guide")
        File apiFile = new File(apiDir, "TEST-index.html.xml")
        File guideFile = new File(guideDir, "TEST-index.html.xml")

        assertTrue("API directory should exist", apiDir.exists())
        assertTrue("Guide directory should exist", guideDir.exists())
        assertTrue("API test file should exist", apiFile.exists())
        assertTrue("Guide test file should exist", guideFile.exists())

        // Verify content of both files
        def apiTestsuite = new XmlSlurper().parse(apiFile)
        assertEquals("docs/api/index.html", apiTestsuite.@name.text())

        def guideTestsuite = new XmlSlurper().parse(guideFile)
        assertEquals("docs/guide/index.html", guideTestsuite.@name.text())
    }

    @Test
    void testFilenameWithSpecialCharactersIsSanitized() {
        // Given: a filename with special characters
        SinglePageResults pageWithSpecialChars = new SinglePageResults(
                "my file (2024).html",
                "docs/my file (2024).html",
                "Special Page",
                1000,
                new ArrayList<>())
        PerRunResults runResults = new PerRunResults()
        runResults.addPageResults(pageWithSpecialChars)

        // When: we generate the report
        new JUnitXmlReporter(runResults, outputPath.absolutePath).reportPageSummary(pageWithSpecialChars)

        // Then: the filename should be sanitized but directory structure preserved
        File expectedDir = new File(outputPath, "docs")
        assertTrue("Directory should exist", expectedDir.exists())

        // Find the generated file (name will be sanitized)
        File[] files = expectedDir.listFiles()
        assertTrue("Should have exactly one file", files != null && files.length == 1)
        assertTrue("Filename should start with TEST-", files[0].name.startsWith("TEST-"))
        assertTrue("Filename should be sanitized (no parentheses or spaces)",
                   !files[0].name.contains("(") && !files[0].name.contains(")"))

        def testsuite = new XmlSlurper().parse(files[0])
        assertEquals("docs/my file (2024).html", testsuite.@name.text())
    }

    @Test
    void testRelativePathWithDotDotIsHandledCorrectly() {
        // Given: a page with relative path containing .. (parent directory reference)
        // Note: This tests edge case handling - in practice, paths should be normalized
        SinglePageResults pageWithRelativePath = new SinglePageResults(
                "index.html",
                "docs/../public/index.html",
                "Relative Path Page",
                1000,
                new ArrayList<>())
        PerRunResults runResults = new PerRunResults()
        runResults.addPageResults(pageWithRelativePath)

        // When: we generate the report
        new JUnitXmlReporter(runResults, outputPath.absolutePath).reportPageSummary(pageWithRelativePath)

        // Then: the file should be created (path handling depends on implementation)
        // The implementation should handle this gracefully
        File[] allFiles = outputPath.listFiles()
        assertTrue("Should have created at least one file or directory", allFiles != null && allFiles.length > 0)
    }
}
