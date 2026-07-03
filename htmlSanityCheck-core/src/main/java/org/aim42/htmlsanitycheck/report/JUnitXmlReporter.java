package org.aim42.htmlsanitycheck.report;

import org.aim42.htmlsanitycheck.Configuration;
import org.aim42.htmlsanitycheck.collect.Finding;
import org.aim42.htmlsanitycheck.collect.PerRunResults;
import org.aim42.htmlsanitycheck.collect.SingleCheckResults;
import org.aim42.htmlsanitycheck.collect.SinglePageResults;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.UUID;

/************************************************************************
 * This is free software - without ANY guarantee!
 *
 *
 * Copyright 2016, Patrick Double, https://github.com/double16
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 *********************************************************************** */

/**
 * Write the findings' report to JUnit XML. Allows tools processing JUnit to
 * include the findings.
 * <p>
 * Supports two output styles:
 * <ul>
 *   <li>{@link Configuration.JunitOutputStyle#FLAT} - All files in one directory with encoded paths (default, backwards compatible)</li>
 *   <li>{@link Configuration.JunitOutputStyle#HIERARCHICAL} - Files organized in subdirectories mirroring source structure</li>
 * </ul>
 */
public class JUnitXmlReporter extends Reporter {
    File outputPath;
    Configuration.JunitOutputStyle outputStyle;

    public JUnitXmlReporter(PerRunResults runResults, String outputPath) {
        this(runResults, outputPath, Configuration.JunitOutputStyle.FLAT);
    }

    public JUnitXmlReporter(PerRunResults runResults, String outputPath, Configuration.JunitOutputStyle outputStyle) {
        super(runResults);
        this.outputPath = new File(outputPath);
        this.outputStyle = outputStyle != null ? outputStyle : Configuration.JunitOutputStyle.FLAT;
    }

    @Override
    protected void initReport() {
        if (!outputPath.canWrite() && !outputPath.mkdirs()) {
            throw new RuntimeException("Cannot create or write to " + outputPath); //NOSONAR(S112)
        }
    }

    // tag::reportPageSummary[]
    @Override
    protected void reportPageSummary(SinglePageResults singlePageResults) {
        String name = filenameOrTitleOrRandom(singlePageResults);

        File testOutputFile = (outputStyle == Configuration.JunitOutputStyle.HIERARCHICAL)
            ? getHierarchicalOutputFile(name)
            : getFlatOutputFile(name);
        // end::reportPageSummary[]

        XMLOutputFactory factory = XMLOutputFactory.newInstance();
        try (FileWriter fileWriter = new FileWriter(testOutputFile)) {
            XMLStreamWriter writer = factory.createXMLStreamWriter(fileWriter);

            writer.writeStartDocument();
            writer.writeStartElement("testsuite");
            writer.writeAttribute("tests", String.valueOf(singlePageResults.nrOfItemsCheckedOnPage()));
            writer.writeAttribute("failures", String.valueOf(singlePageResults.nrOfFindingsOnPage()));
            writer.writeAttribute("errors", "0");
            writer.writeAttribute("time", "0");
            writer.writeAttribute("name", name);

            for (SingleCheckResults singleCheckResult : singlePageResults.getSingleCheckResults()) {
                writer.writeStartElement("testcase");
                writer.writeAttribute("assertions", String.valueOf(singleCheckResult.getNrOfItemsChecked()));
                writer.writeAttribute("time", "0");
                writer.writeAttribute("name", singleCheckResult.getWhatIsChecked() != null ? singleCheckResult.getWhatIsChecked() : "");

                for (Finding finding : singleCheckResult.getFindings()) {
                    writer.writeStartElement("failure");
                    writer.writeAttribute("type", singleCheckResult.getSourceItemName() + " - " + singleCheckResult.getTargetItemName());
                    writer.writeAttribute("message", finding.getWhatIsTheProblem());
                    writer.writeCharacters(finding.getSuggestions() != null ? String.join(", ", finding.getSuggestions()) : "");
                    writer.writeEndElement(); // end of <failure>
                }

                writer.writeEndElement(); // end of <testcase>
            }

            writer.writeEndElement(); // end of <testsuite>
            writer.writeEndDocument();

            writer.flush();
        } catch (IOException | XMLStreamException e) {
            throw new RuntimeException(e); //NOSONAR(S112)
        }
    }

    /**
     * Creates output file using flat structure (all files in one directory).
     * Encodes the full path into the filename using underscores.
     *
     * @param name The source file path
     * @return The output file for the JUnit XML report
     */
    private File getFlatOutputFile(String name) {
        String sanitizedPath = name.replaceAll("[^A-Za-z0-9_-]+", "_");
        return new File(outputPath, "TEST-unit-html-" + sanitizedPath + ".xml");
    }

    /**
     * Creates output file using hierarchical structure (subdirectories mirror source structure).
     * Solves filename length issues with deeply nested directories.
     *
     * @param name The source file path
     * @return The output file for the JUnit XML report
     */
    private File getHierarchicalOutputFile(String name) {
        // Parse the path to extract directory structure and filename
        File sourcePath = new File(name);
        File parentDir = sourcePath.getParentFile();
        String fileName = sourcePath.getName();

        // Create directory structure under outputPath to mirror the source file hierarchy
        File testOutputDir;
        if (parentDir != null) {
            // Normalize the path to handle relative references like ".."
            // This ensures we stay within the outputPath and don't try to escape it
            try {
                File tempPath = new File(outputPath, parentDir.getPath());
                testOutputDir = tempPath.getCanonicalFile();

                // Verify the canonical path is still under outputPath using NIO Path API
                // This provides better security against path traversal attacks
                Path normalizedOutputPath = outputPath.getCanonicalFile().toPath().normalize();
                Path normalizedTestOutputDir = testOutputDir.toPath().normalize();

                if (!normalizedTestOutputDir.startsWith(normalizedOutputPath)) {
                    // Path tries to escape outputPath, so just use outputPath directly
                    testOutputDir = outputPath;
                }
            } catch (Exception e) {
                // If normalization fails, fall back to outputPath
                testOutputDir = outputPath;
            }
        } else {
            testOutputDir = outputPath;
        }

        // Ensure the directory exists
        if (!testOutputDir.exists() && !testOutputDir.mkdirs()) {
            StringBuilder errorMsg = new StringBuilder("Cannot create directory: ")
                .append(testOutputDir.getAbsolutePath());
            errorMsg.append(" (exists: ").append(testOutputDir.exists())
                    .append(", parent canWrite: ")
                    .append(testOutputDir.getParentFile() != null ? testOutputDir.getParentFile().canWrite() : "unknown")
                    .append(")");
            throw new RuntimeException(errorMsg.toString()); //NOSONAR(S112)
        }

        // Create the test file with a simple, sanitized filename
        String sanitizedFileName = fileName.replaceAll("[^A-Za-z0-9_.-]+", "_");
        return new File(testOutputDir, "TEST-" + sanitizedFileName + ".xml");
    }

    private static String filenameOrTitleOrRandom(SinglePageResults pageResult) {
        if (pageResult.getPageFilePath() != null) {
            return pageResult.getPageFilePath();
        }
        return (pageResult.getPageTitle() != null) ? pageResult.getPageTitle() : UUID.randomUUID().toString();
    }

    // JUnit's reports are completely written by one method; therefore, all others are empty
    @Override
    protected void reportOverallSummary() { // NOSONAR(S1186)
    }

    @Override
    protected void reportPageFooter() { // NOSONAR(S1186)
    }

    @Override
    protected void reportSingleCheckSummary(SingleCheckResults checkResults) { // NOSONAR(S1186)
    }

    @Override
    protected void reportSingleCheckDetails(SingleCheckResults checkResults) { // NOSONAR(S1186)
    }

}
