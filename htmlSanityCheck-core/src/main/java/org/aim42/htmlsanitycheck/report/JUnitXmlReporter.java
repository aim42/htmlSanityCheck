package org.aim42.htmlsanitycheck.report;

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
 */
public class JUnitXmlReporter extends Reporter {
    File outputPath;

    public JUnitXmlReporter(PerRunResults runResults, String outputPath) {
        super(runResults);
        this.outputPath = new File(outputPath);
    }

    @Override
    protected void initReport() {
        if (!outputPath.canWrite() && !outputPath.mkdirs()) {
            throw new RuntimeException("Cannot create or write to " + outputPath);
        }
    }

    @Override
    protected void reportPageSummary(SinglePageResults pageResult) {
        String name = filenameOrTitleOrRandom(pageResult);
        String sanitizedPath = name.replaceAll("[^A-Za-z0-9_-]+", "_");
        File testOutputFile = new File(outputPath, "TEST-unit-html-" + sanitizedPath + ".xml");

        XMLOutputFactory factory = XMLOutputFactory.newInstance();
        try (FileWriter fileWriter = new FileWriter(testOutputFile)) {
            XMLStreamWriter writer = factory.createXMLStreamWriter(fileWriter);

            writer.writeStartDocument();
            writer.writeStartElement("testsuite");
            writer.writeAttribute("tests", String.valueOf(pageResult.nrOfItemsCheckedOnPage()));
            writer.writeAttribute("failures", String.valueOf(pageResult.nrOfFindingsOnPage()));
            writer.writeAttribute("errors", "0");
            writer.writeAttribute("time", "0");
            writer.writeAttribute("name", name);

            for (SingleCheckResults singleCheckResult : pageResult.getSingleCheckResults()) {
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
            throw new RuntimeException(e);
        }
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
