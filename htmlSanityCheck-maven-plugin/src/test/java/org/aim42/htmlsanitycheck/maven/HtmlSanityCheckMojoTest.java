package org.aim42.htmlsanitycheck.maven;

import org.aim42.htmlsanitycheck.Configuration;
import org.apache.maven.plugin.MojoExecutionException;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.Assumptions;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;

class HtmlSanityCheckMojoTest {
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
        ByteArrayOutputStream errContent = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        PrintStream originalErr = System.err;
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));

        // Run the code
        HtmlSanityCheckMojo mojo = new HtmlSanityCheckMojo();
        mojo.logBuildParameter();

        // Reset  System.out and System.err
        System.setOut(originalOut);
        System.setErr(originalErr);


        // Check Output
        Assertions.assertThat(errContent.toString()).isEmpty();
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
        Assertions.assertThat(path.toFile().exists()).isTrue();
        Assertions.assertThat(path.toFile().canWrite()).isTrue();
        Assertions.assertThat(path.toFile().isDirectory()).isTrue();

        // Clean up
        Files.deleteIfExists(path);
        Files.deleteIfExists(tempDir.resolve("testdir/anotherTestdir"));
        Files.deleteIfExists(tempDir.resolve("testdir"));
        Files.deleteIfExists(tempDir);
    }


    @Test
    void handleFindings() throws IOException {
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

        // Clea uo
        Files.deleteIfExists(tempDir);
    }


}
