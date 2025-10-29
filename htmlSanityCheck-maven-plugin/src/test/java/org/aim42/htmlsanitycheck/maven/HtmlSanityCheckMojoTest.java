package org.aim42.htmlsanitycheck.maven;

import org.aim42.htmlsanitycheck.Configuration;
import org.aim42.htmlsanitycheck.check.AllCheckers;
import org.apache.maven.plugin.MojoExecutionException;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.Assumptions;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

class HtmlSanityCheckMojoTest {

    static final String VALID_HTML = "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01//EN\">  " +
            "<html> " +
            "  <head></head> " +
            "  <body>" +
            "    This <a href=\"https://tld.invalid/\">Invalid TLD</a> should not make a problem! " +
            "  </body> " +
            "<html>";

    @Test
    void setupConfiguration() {
        HtmlSanityCheckMojo mojo = new HtmlSanityCheckMojo();
        Configuration config = mojo.setupConfiguration();
        Assertions.assertThat(config).isNotNull();
        Assertions.assertThat(config.getFailOnErrors()).isFalse();
    }

    @Test
    void setupConfigurationWithHttpSuccessCodes() throws Exception {
        // Create mojo with custom HTTP success codes
        Set<Integer> customSuccessCodes = new HashSet<>();
        customSuccessCodes.add(299);

        HtmlSanityCheckMojo mojo = new HtmlSanityCheckMojo();
        setField(mojo, "httpSuccessCodes", customSuccessCodes);

        Configuration config = mojo.setupConfiguration();

        Assertions.assertThat(config).isNotNull();
        Assertions.assertThat(config.getHttpSuccessCodes()).contains(299);
    }

    @Test
    void setupConfigurationWithHttpErrorCodes() throws Exception {
        // Create mojo with custom HTTP error codes
        Set<Integer> customErrorCodes = new HashSet<>();
        customErrorCodes.add(599);

        HtmlSanityCheckMojo mojo = new HtmlSanityCheckMojo();
        setField(mojo, "httpErrorCodes", customErrorCodes);

        Configuration config = mojo.setupConfiguration();

        Assertions.assertThat(config).isNotNull();
        Assertions.assertThat(config.getHttpErrorCodes()).contains(599);
    }

    @Test
    void setupConfigurationWithHttpWarningCodes() throws Exception {
        // Create mojo with custom HTTP warning codes
        Set<Integer> customWarningCodes = new HashSet<>();
        customWarningCodes.add(199);

        HtmlSanityCheckMojo mojo = new HtmlSanityCheckMojo();
        setField(mojo, "httpWarningCodes", customWarningCodes);

        Configuration config = mojo.setupConfiguration();

        Assertions.assertThat(config).isNotNull();
        Assertions.assertThat(config.getHttpWarningCodes()).contains(199);
    }

    @Test
    void setupConfigurationWithEmptyHttpStatusCodesShouldNotOverride() throws Exception {
        // Create mojo with empty HTTP status code sets (should not override defaults)
        HtmlSanityCheckMojo mojo = new HtmlSanityCheckMojo();
        setField(mojo, "httpSuccessCodes", new HashSet<Integer>());
        setField(mojo, "httpErrorCodes", new HashSet<Integer>());
        setField(mojo, "httpWarningCodes", new HashSet<Integer>());

        Configuration config = mojo.setupConfiguration();

        // Verify that default codes are still present (not overridden by empty sets)
        Assertions.assertThat(config).isNotNull();
        Assertions.assertThat(config.getHttpSuccessCodes()).contains(200);  // Default success code
        Assertions.assertThat(config.getHttpErrorCodes()).contains(404);    // Default error code
        Assertions.assertThat(config.getHttpWarningCodes()).contains(301);  // Default warning code (redirect)
    }


    @Test
    void logBuildParameter() {

        // Write System.out and System.err to a stream. Keep the originals
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outContent));

        // Run the code
        HtmlSanityCheckMojo mojo = new HtmlSanityCheckMojo();
        mojo.logBuildParameter(Configuration.builder().build());

        // Reset  System.out and System.err
        System.setOut(originalOut);


        // Check Output
        Assertions.assertThat(outContent.toString())
                .contains("[info] Parameters given to sanityCheck plugin from Maven buildfile...")
                .contains("[info] Files to check  : null");
    }

    @Test
    void createoutputDirs() throws IOException, MojoExecutionException {

        // Set stage - Get a directory to safely work on and a mojo
        Path tempDir = Files.createTempDirectory("MojoTest");

        Assumptions.assumeThat(tempDir).isNotNull();

        Path path = tempDir.resolve("testdir/anotherTestdir/dir");

        HtmlSanityCheckMojo mojo = new HtmlSanityCheckMojo();

        // The code to test
        mojo.createoutputDirs(path.toFile(), "Fehlertext");

        // Check
        Assertions.assertThat(path.toFile()).exists();
        Assertions.assertThat(path.toFile()).canWrite();
        Assertions.assertThat(path.toFile()).isDirectory();

        // Clean up
        deleteDirectory(tempDir.toFile());
    }

    @Test
    void createoutputDirsFail() throws IOException {

        // Set stage - Create a File, that is no dir to provoke an exception  and create a mojo
        Path tempDir = Files.createTempFile("MojoTest", "");

        Assumptions.assumeThat(tempDir).isNotNull();

        HtmlSanityCheckMojo mojo = new HtmlSanityCheckMojo();

        // Check
        Assertions.assertThatThrownBy(() -> mojo.createoutputDirs(tempDir.toFile(), "Fehlertext"))
                .isInstanceOf(MojoExecutionException.class)
                .hasMessageContaining("Fehlertext");

        // Clean up
        Files.deleteIfExists(tempDir);
    }


    @Test
    void handleFindingsThrowsException() throws IOException {
        // Set stage - Get a directory to safely work on, a configuration and a mojo
        Path tempDir = Files.createTempDirectory("MojoTest");
        Configuration config = Configuration.builder()
                .failOnErrors(true)
                .checkingResultsDir(tempDir.toFile())
                .build();
        HtmlSanityCheckMojo mojo = new HtmlSanityCheckMojo();

        //Check
        Assertions.assertThatThrownBy(() -> mojo.handleFindings(2, config))
                .isInstanceOf(MojoExecutionException.class)
                .hasMessageContaining("2 error(s)");

        // Clean up
        Files.deleteIfExists(tempDir);
    }

    @Test
    void handleFindingsNoExceptionWehenNoFailIsSet() throws IOException, MojoExecutionException {
        // Set stage - Get a directory to safely work on, a configuration and a mojo
        Path tempDir = Files.createTempDirectory("MojoTest");
        Configuration config = Configuration.builder()
                .failOnErrors(false)
                .checkingResultsDir(tempDir.toFile())
                .build();
        HtmlSanityCheckMojo mojo = new HtmlSanityCheckMojo();

        //Check, dass keine Exception fliegt

        mojo.handleFindings(2, config);

        // Clean up
        Files.deleteIfExists(tempDir);
    }


    @Test
    void handleFindingsNoExceptionIfNoFindings() throws IOException, MojoExecutionException {
        // Set stage - Get a directory to safely work on, a configuration and a mojo
        Path tempDir = Files.createTempDirectory("MojoTest");
        Configuration config = Configuration.builder()
                .failOnErrors(true)
                .checkingResultsDir(tempDir.toFile())
                .build();
        HtmlSanityCheckMojo mojo = new HtmlSanityCheckMojo();

        //Check, dass keine Exception fliegt

        mojo.handleFindings(0, config);

        // Clean up
        Files.deleteIfExists(tempDir);
    }

    @Test
    void execuuserte() throws IOException, MojoExecutionException {
        Path junitDir = Files.createTempDirectory("MojoJunit");
        Path resultDir = Files.createTempDirectory("MojoJunit");
        Path sourceDir = Files.createTempDirectory("MojoSource");
        sourceDir.toFile().deleteOnExit();
        File sourceFile = new File(sourceDir.toFile(), "test.html");
        Files.write(sourceFile.toPath(), VALID_HTML.getBytes(StandardCharsets.UTF_8));
        Set<File> fileset = new HashSet<>();
        fileset.add(sourceFile);
        Set<Pattern> excludes = new HashSet<>();
        excludes.add(Pattern.compile("^.*\\.invalid.*"));

        Configuration myConfig = Configuration.builder()
                .checksToExecute(AllCheckers.CHECKER_CLASSES)
                .junitResultsDir(junitDir.toFile())
                .checkingResultsDir(resultDir.toFile())
                .sourceDir(sourceDir.toFile())
                .sourceDocuments(fileset)
                .excludes(excludes)
                .build();
        HtmlSanityCheckMojo mojo = new HtmlSanityCheckMojo();

        mojo.execute(myConfig);


        // Clean up
        deleteDirectory(junitDir.toFile());
        deleteDirectory(resultDir.toFile());
    }

    @Test
    void executeWithOnlySourceDir_ShouldSucceed() throws IOException, MojoExecutionException {
        // Setup: Create temp directories
        Path junitDir = Files.createTempDirectory("MojoJunit");
        Path resultDir = Files.createTempDirectory("MojoResult");
        Path sourceDir = Files.createTempDirectory("MojoSource");

        // Create HTML file in root of sourceDir
        File rootHtmlFile = new File(sourceDir.toFile(), "root.html");
        Files.write(rootHtmlFile.toPath(), VALID_HTML.getBytes(StandardCharsets.UTF_8));

        // Create subdirectory with another HTML file
        File subDir = new File(sourceDir.toFile(), "subdir");
        boolean mkdirSuccess = subDir.mkdirs();
        Assertions.assertThat(mkdirSuccess).isTrue();
        File subHtmlFile = new File(subDir, "nested.html");
        Files.write(subHtmlFile.toPath(), VALID_HTML.getBytes(StandardCharsets.UTF_8));

        // Create Mojo and set only sourceDir field (NOT sourceDocuments)
        // This simulates a Maven pom.xml with only <sourceDir> configured
        HtmlSanityCheckMojo mojo = new TestableHtmlSanityCheckMojo(
                sourceDir.toFile(),
                null, // sourceDocuments explicitly NOT set
                resultDir.toFile(),
                junitDir.toFile()
        );

        // This should succeed - setupConfiguration() will auto-populate sourceDocuments
        mojo.execute();

        // Clean up
        deleteDirectory(sourceDir.toFile());
        deleteDirectory(junitDir.toFile());
        deleteDirectory(resultDir.toFile());
    }

    @Test
    void findHtmlFilesWithNullDirectory() throws Exception {
        HtmlSanityCheckMojo mojo = new HtmlSanityCheckMojo();
        setField(mojo, "sourceDir", null);
        setField(mojo, "sourceDocuments", null);

        Configuration config = mojo.setupConfiguration();

        // When both sourceDir and sourceDocuments are null, the config should have null sourceDocuments
        // (This will be caught by validation)
        Assertions.assertThat(config.getSourceDocuments()).isNull();
    }

    @Test
    void findHtmlFilesWithNonExistentDirectory() throws Exception {
        Path nonExistentDir = java.nio.file.Paths.get("/tmp/this-directory-does-not-exist-" + System.currentTimeMillis());
        HtmlSanityCheckMojo mojo = new HtmlSanityCheckMojo();
        setField(mojo, "sourceDir", nonExistentDir.toFile());
        setField(mojo, "sourceDocuments", null);

        Configuration config = mojo.setupConfiguration();

        // Should return empty set when directory doesn't exist
        Assertions.assertThat(config.getSourceDocuments()).isEmpty();
    }

    @Test
    void findHtmlFilesWithEmptyDirectory() throws Exception {
        Path emptyDir = Files.createTempDirectory("MojoEmpty");

        HtmlSanityCheckMojo mojo = new HtmlSanityCheckMojo();
        setField(mojo, "sourceDir", emptyDir.toFile());
        setField(mojo, "sourceDocuments", null);

        Configuration config = mojo.setupConfiguration();

        // Should return empty set when directory is empty
        Assertions.assertThat(config.getSourceDocuments()).isEmpty();

        // Clean up
        Files.deleteIfExists(emptyDir);
    }

    @Test
    void findHtmlFilesIgnoresNonHtmlFiles() throws Exception {
        Path sourceDir = Files.createTempDirectory("MojoSource");

        // Create various non-HTML files
        File txtFile = new File(sourceDir.toFile(), "readme.txt");
        Files.write(txtFile.toPath(), "Text content".getBytes(StandardCharsets.UTF_8));

        File pdfFile = new File(sourceDir.toFile(), "document.pdf");
        Files.write(pdfFile.toPath(), "PDF content".getBytes(StandardCharsets.UTF_8));

        File xmlFile = new File(sourceDir.toFile(), "config.xml");
        Files.write(xmlFile.toPath(), "<xml/>".getBytes(StandardCharsets.UTF_8));

        // Create one HTML file
        File htmlFile = new File(sourceDir.toFile(), "page.html");
        Files.write(htmlFile.toPath(), VALID_HTML.getBytes(StandardCharsets.UTF_8));

        HtmlSanityCheckMojo mojo = new HtmlSanityCheckMojo();
        setField(mojo, "sourceDir", sourceDir.toFile());
        setField(mojo, "sourceDocuments", null);

        Configuration config = mojo.setupConfiguration();

        // Should only find the HTML file
        Assertions.assertThat(config.getSourceDocuments()).hasSize(1);
        Assertions.assertThat(config.getSourceDocuments())
                .extracting(File::getName)
                .containsExactly("page.html");

        // Clean up
        deleteDirectory(sourceDir.toFile());
    }

    @Test
    void executeWithOnlySourceDir_ShouldIncludeHtmFiles() throws IOException, MojoExecutionException {
        // Setup: Create temp directories
        Path junitDir = Files.createTempDirectory("MojoJunit");
        Path resultDir = Files.createTempDirectory("MojoResult");
        Path sourceDir = Files.createTempDirectory("MojoSource");

        // Create .htm file in root
        File rootHtmFile = new File(sourceDir.toFile(), "document.htm");
        Files.write(rootHtmFile.toPath(), VALID_HTML.getBytes(StandardCharsets.UTF_8));

        // Create .html file for comparison
        File rootHtmlFile = new File(sourceDir.toFile(), "page.html");
        Files.write(rootHtmlFile.toPath(), VALID_HTML.getBytes(StandardCharsets.UTF_8));

        // Create subdirectory with .htm file
        File subDir = new File(sourceDir.toFile(), "docs");
        boolean mkdirSuccess = subDir.mkdirs();
        Assertions.assertThat(mkdirSuccess).isTrue();
        File nestedHtmFile = new File(subDir, "nested.htm");
        Files.write(nestedHtmFile.toPath(), VALID_HTML.getBytes(StandardCharsets.UTF_8));

        // Create Mojo and set only sourceDir field
        HtmlSanityCheckMojo mojo = new TestableHtmlSanityCheckMojo(
                sourceDir.toFile(),
                null, // sourceDocuments explicitly NOT set
                resultDir.toFile(),
                junitDir.toFile()
        );

        // Get the configuration to verify sourceDocuments includes .htm files
        Configuration config = mojo.setupConfiguration();

        // Verify that both .html and .htm files are discovered
        Assertions.assertThat(config.getSourceDocuments()).isNotNull();
        Assertions.assertThat(config.getSourceDocuments()).hasSize(3);
        Assertions.assertThat(config.getSourceDocuments())
                .extracting(File::getName)
                .containsExactlyInAnyOrder("document.htm", "page.html", "nested.htm");

        // Execute should succeed with both file types
        mojo.execute();

        // Clean up
        deleteDirectory(sourceDir.toFile());
        deleteDirectory(junitDir.toFile());
        deleteDirectory(resultDir.toFile());
    }

    /**
     * Helper class to allow setting private fields for testing
     */
    static class TestableHtmlSanityCheckMojo extends HtmlSanityCheckMojo {
        TestableHtmlSanityCheckMojo(File sourceDir, Set<File> sourceDocuments,
                                   File checkingResultsDir, File junitResultsDir) {
            // Use reflection to set private fields
            try {
                HtmlSanityCheckMojoTest.setField(this, "sourceDir", sourceDir);
                HtmlSanityCheckMojoTest.setField(this, "sourceDocuments", sourceDocuments);
                HtmlSanityCheckMojoTest.setField(this, "checkingResultsDir", checkingResultsDir);
                HtmlSanityCheckMojoTest.setField(this, "junitResultsDir", junitResultsDir);
                HtmlSanityCheckMojoTest.setField(this, "checkerClasses", AllCheckers.CHECKER_CLASSES);
                HtmlSanityCheckMojoTest.setField(this, "excludes", new HashSet<String>());
            } catch (Exception e) {
                throw new RuntimeException("Failed to set fields", e);
            }
        }
    }


    // Helper functions

    void deleteDirectory(File directoryToBeDeleted) throws IOException {
        File[] allContents = directoryToBeDeleted.listFiles();
        if (allContents != null) {
            for (File file : allContents) {
                deleteDirectory(file);
            }
        }
        Files.deleteIfExists(directoryToBeDeleted.toPath());
    }

    /**
     * Helper method to set private fields on HtmlSanityCheckMojo for testing
     */
    private static void setField(Object target, String fieldName, Object value)
            throws NoSuchFieldException, IllegalAccessException {
        java.lang.reflect.Field field = HtmlSanityCheckMojo.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }


}
