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
        Set<String> excludes = new HashSet<>();
        excludes.add("^.*\\.invalid.*");

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


}
