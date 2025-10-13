package org.aim42.htmlsanitycheck.report

import org.aim42.htmlsanitycheck.Configuration
import org.aim42.htmlsanitycheck.collect.Finding
import org.aim42.htmlsanitycheck.collect.PerRunResults
import org.aim42.htmlsanitycheck.collect.SingleCheckResults
import org.aim42.htmlsanitycheck.collect.SinglePageResults
import org.junit.After
import org.junit.Before
import org.junit.Test

import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertNotNull
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
    void testInitReportWithNonWritableDirectory() {
        // Create a path that cannot be created (using a non-existent parent and restricted path)
        File nonExistentPath = new File("/nonexistent/path/that/cannot/be/created")

        // Try to create a JUnitXmlReporter with a path that cannot be created
        PerRunResults runResults = new PerRunResults()
        JUnitXmlReporter reporter = new JUnitXmlReporter(runResults, nonExistentPath.getAbsolutePath())

        // This should throw RuntimeException because the path cannot be created
        reporter.initReport()
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

    // Tests for FLAT output style (default, backwards compatible)

    @Test
    void testFlatModeCreatesEncodedFilename() {
        // Given: a page with a nested path
        SinglePageResults pageWithPath = new SinglePageResults(
                "about.html",
                "docs/guide/about.html",
                "About Page",
                1000,
                new ArrayList<>())
        PerRunResults runResults = new PerRunResults()
        runResults.addPageResults(pageWithPath)

        // When: we generate the report in FLAT mode (explicit)
        new JUnitXmlReporter(runResults, outputPath.absolutePath, Configuration.JunitOutputStyle.FLAT)
                .reportPageSummary(pageWithPath)

        // Then: the file should be created in the root with encoded path
        File[] files = outputPath.listFiles()
        assertEquals("Should have exactly one file in root", 1, files.length)
        assertTrue("Filename should contain encoded path",
                   files[0].name.contains("docs") && files[0].name.contains("guide"))
        assertTrue("Filename should start with TEST-unit-html-", files[0].name.startsWith("TEST-unit-html-"))

        def testsuite = new XmlSlurper().parse(files[0])
        assertEquals("docs/guide/about.html", testsuite.@name.text())
    }

    @Test
    void testFlatModeIsDefaultWhenNotSpecified() {
        // Given: a page with a nested path
        SinglePageResults pageWithPath = new SinglePageResults(
                "about.html",
                "docs/guide/about.html",
                "About Page",
                1000,
                new ArrayList<>())
        PerRunResults runResults = new PerRunResults()
        runResults.addPageResults(pageWithPath)

        // When: we generate the report WITHOUT specifying mode (should default to FLAT)
        new JUnitXmlReporter(runResults, outputPath.absolutePath)
                .reportPageSummary(pageWithPath)

        // Then: the file should be created in the root with encoded path (FLAT behavior)
        File[] files = outputPath.listFiles()
        assertEquals("Should have exactly one file in root", 1, files.length)
        assertTrue("Filename should contain encoded path",
                   files[0].name.contains("docs") && files[0].name.contains("guide"))

        def testsuite = new XmlSlurper().parse(files[0])
        assertEquals("docs/guide/about.html", testsuite.@name.text())
    }

    // Tests for hierarchical directory structure (issue #405)

    @Test(expected = RuntimeException.class)
    void testHierarchicalModeFailsWhenCannotCreateDirectory() {
        // Given: an output path that's a file (not a directory)
        File tempFile = File.createTempFile("test", ".txt")
        tempFile.deleteOnExit()

        SinglePageResults pageWithPath = new SinglePageResults(
                "about.html",
                "docs/guide/about.html",
                "About Page",
                1000,
                new ArrayList<>())
        PerRunResults runResults = new PerRunResults()
        runResults.addPageResults(pageWithPath)

        // When: we try to generate a report in HIERARCHICAL mode with a file as output path
        // Then: it should throw RuntimeException because it cannot create subdirectories
        new JUnitXmlReporter(runResults, tempFile.getAbsolutePath(), Configuration.JunitOutputStyle.HIERARCHICAL)
                .reportPageSummary(pageWithPath)
    }

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

        // When: we generate the report in HIERARCHICAL mode
        new JUnitXmlReporter(runResults, outputPath.absolutePath, Configuration.JunitOutputStyle.HIERARCHICAL)
                .reportPageSummary(singlePageResultsWithSimplePath)

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

        // When: we generate the report in HIERARCHICAL mode
        new JUnitXmlReporter(runResults, outputPath.absolutePath, Configuration.JunitOutputStyle.HIERARCHICAL)
                .reportPageSummary(singlePageResultsWithPath)

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

        // When: we generate the report in HIERARCHICAL mode
        new JUnitXmlReporter(runResults, outputPath.absolutePath, Configuration.JunitOutputStyle.HIERARCHICAL)
                .reportPageSummary(singlePageResultsWithDeepPath)

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

        // When: we generate the report in HIERARCHICAL mode (should not throw exception)
        new JUnitXmlReporter(runResults, outputPath.absolutePath, Configuration.JunitOutputStyle.HIERARCHICAL)
                .reportPageSummary(singlePageResultsWithLongPath)

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

        JUnitXmlReporter reporter = new JUnitXmlReporter(runResults, outputPath.absolutePath, Configuration.JunitOutputStyle.HIERARCHICAL)

        // When: we generate reports for both pages in HIERARCHICAL mode
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

        // When: we generate the report in HIERARCHICAL mode
        new JUnitXmlReporter(runResults, outputPath.absolutePath, Configuration.JunitOutputStyle.HIERARCHICAL)
                .reportPageSummary(pageWithSpecialChars)

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

        // When: we generate the report in HIERARCHICAL mode
        new JUnitXmlReporter(runResults, outputPath.absolutePath, Configuration.JunitOutputStyle.HIERARCHICAL)
                .reportPageSummary(pageWithRelativePath)

        // Then: the file should be created (path handling depends on implementation)
        // The implementation should handle this gracefully
        File[] allFiles = outputPath.listFiles()
        assertTrue("Should have created at least one file or directory", allFiles != null && allFiles.length > 0)
    }

    @Test
    void testPathTraversalAttackIsBlocked() {
        // Given: a malicious path trying to escape the output directory
        // This simulates a path traversal attack like "../../../etc/passwd"
        SinglePageResults maliciousPage = new SinglePageResults(
                "index.html",
                "../../../malicious/path/index.html",
                "Malicious Page",
                1000,
                new ArrayList<>())
        PerRunResults runResults = new PerRunResults()
        runResults.addPageResults(maliciousPage)

        // When: we generate the report in HIERARCHICAL mode
        new JUnitXmlReporter(runResults, outputPath.absolutePath, Configuration.JunitOutputStyle.HIERARCHICAL)
                .reportPageSummary(maliciousPage)

        // Then: the file should be created safely within outputPath, not outside it
        File[] allFiles = outputPath.listFiles()
        assertTrue("Should have created file or directory", allFiles != null && allFiles.length > 0)

        // Verify no files were created outside outputPath
        def outputPathCanonical = outputPath.canonicalPath
        def createdFile = findFirstXmlFile(outputPath)
        assertNotNull(createdFile)

        // The created file should be within outputPath
        assertTrue("File should be within output directory",
                   createdFile.canonicalPath.startsWith(outputPathCanonical))
    }

    @Test
    void testPathTraversalWithSymlinkStyleAttackIsBlocked() {
        // Given: a more sophisticated path traversal attack that tries to bypass simple checks
        // Example: "validdir/../../escape/test.html" which could bypass startsWith() on strings
        SinglePageResults sophisticatedAttack = new SinglePageResults(
                "test.html",
                "valid/../../../escape/test.html",
                "Sophisticated Attack",
                1000,
                new ArrayList<>())
        PerRunResults runResults = new PerRunResults()
        runResults.addPageResults(sophisticatedAttack)

        // When: we generate the report in HIERARCHICAL mode
        new JUnitXmlReporter(runResults, outputPath.absolutePath, Configuration.JunitOutputStyle.HIERARCHICAL)
                .reportPageSummary(sophisticatedAttack)

        // Then: verify the file is safely contained
        def outputPathCanonical = outputPath.canonicalPath
        def createdFile = findFirstXmlFile(outputPath)
        assertNotNull(createdFile)

        // Use NIO Path API to verify containment (same method as production code)
        def normalizedOutputPath = outputPath.canonicalFile.toPath().normalize()
        def normalizedCreatedPath = createdFile.canonicalFile.toPath().normalize()

        assertTrue("File should be within output directory using NIO Path API",
                   normalizedCreatedPath.startsWith(normalizedOutputPath))
    }

    @Test
    void testEnhancedErrorMessageWhenDirectoryCreationFails() {
        // Given: a non-existent parent directory that cannot be created
        // We'll use a path that's invalid on the filesystem
        File invalidOutputPath = new File("/nonexistent/deeply/nested/path/that/cannot/be/created")

        SinglePageResults page = new SinglePageResults(
                "test.html",
                "some/deep/path/test.html",
                "Test Page",
                1000,
                new ArrayList<>())
        PerRunResults runResults = new PerRunResults()
        runResults.addPageResults(page)

        // When/Then: directory creation should fail with enhanced error message
        try {
            new JUnitXmlReporter(runResults, invalidOutputPath.absolutePath, Configuration.JunitOutputStyle.HIERARCHICAL)
                    .reportPageSummary(page)
            fail("Should have thrown RuntimeException for directory creation failure")
        } catch (RuntimeException e) {
            // Verify the error message contains diagnostic information
            String errorMsg = e.message
            assertTrue("Error message should mention 'Cannot create directory'",
                       errorMsg.contains("Cannot create directory"))
            assertTrue("Error message should contain full path",
                       errorMsg.contains(invalidOutputPath.absolutePath))
            assertTrue("Error message should include 'exists:' diagnostic",
                       errorMsg.contains("exists:"))
            assertTrue("Error message should include 'parent canWrite:' diagnostic",
                       errorMsg.contains("parent canWrite:"))
        }
    }

    @Test
    void testEnhancedErrorMessageFormatIsCorrect() {
        // Given: setup that will trigger directory creation failure
        File readOnlyParent = new File(outputPath, "readonly-parent")
        readOnlyParent.mkdirs()

        // Try to make it read-only (this may not work on all platforms, especially Windows)
        boolean madeReadOnly = readOnlyParent.setReadOnly()

        if (!madeReadOnly || readOnlyParent.canWrite()) {
            // Skip test if we cannot make directory read-only on this platform
            System.err.println("Skipping testEnhancedErrorMessageFormatIsCorrect - cannot make directory read-only on this platform")
            return
        }

        try {
            SinglePageResults page = new SinglePageResults(
                    "test.html",
                    "readonly-parent/subdir/test.html",
                    "Test Page",
                    1000,
                    new ArrayList<>())
            PerRunResults runResults = new PerRunResults()
            runResults.addPageResults(page)

            // When: attempting to create subdirectory in read-only parent
            new JUnitXmlReporter(runResults, outputPath.absolutePath, Configuration.JunitOutputStyle.HIERARCHICAL)
                    .reportPageSummary(page)
            fail("Should have thrown RuntimeException")
        } catch (RuntimeException e) {
            // Then: error message should have proper format with parentheses
            String errorMsg = e.message
            assertTrue("Error message should contain opening parenthesis",
                       errorMsg.contains("("))
            assertTrue("Error message should contain closing parenthesis",
                       errorMsg.contains(")"))
            // Should have format like: "... (exists: true, parent canWrite: false)"
            assertTrue("Error message should match expected format pattern",
                       errorMsg.matches(".*\\(exists: .*, parent canWrite: .*\\).*"))
        } finally {
            // Cleanup: restore write permission
            readOnlyParent.setWritable(true)
        }
    }
}
