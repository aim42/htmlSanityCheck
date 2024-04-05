package org.aim42.htmlsanitycheck.report;

import org.aim42.htmlsanitycheck.collect.PerRunResults;
import org.aim42.htmlsanitycheck.collect.SingleCheckResults;
import org.aim42.htmlsanitycheck.collect.SinglePageResults;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;

public class HtmlReporter extends Reporter {

    private static final Logger log = LoggerFactory.getLogger(HtmlReporter.class);

    private static final String REPORT_FILENAME = "index.html";
    private final String resultsOutputDir;
    private FileWriter writer;

    private void write(String content) {
        try {
            writer.write(content);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public HtmlReporter(PerRunResults runResults, String outputDir) {
        super(runResults);
        this.resultsOutputDir = outputDir;
    }

    @Override
    public void initReport() {
        // determine a path where we can write our output file...
        File outputFile = createOutputFile(resultsOutputDir);

        // init the private writer object
        try {
            writer = createWriter(outputFile);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }

        initWriterWithHtmlHeader();

        // we have a writer, therefore an existing output directory!

        List<String> requiredResourceFiles = Arrays.asList("arrow-up.png", "htmlsanitycheck-logo.png",
                "htmlsc-style.css", "scroll.css",
                "jquery.min.js");
        copyRequiredResourceFiles(resultsOutputDir, requiredResourceFiles);
    }

    /*
    We need some static files next to the report html.. css, js and logo stuff.

    Originally I posted this as a question to the gradle forum:
    http://forums.gradle.org/gradle/topics/-html-checking-plugin-how-to-copy-required-css-to-output-directory

    Answers were:
    http://stackoverflow.com/questions/10308221/how-to-copy-file-inside-jar-to-outside-the-jar

    https://github.com/gradle/gradle/blob/master/subprojects/performance/src/testFixtures/groovy/org/gradle/performance/results/ReportGenerator.java#L50-50

     */
    private void copyResourceFromJarToDirectory(String resourceName, File outputDirectory) throws IOException {
        URL resource = getClass().getClassLoader().getResource(resourceName);

        if (resource == null) {
            throw new IOException("Resource not found: " + resourceName);
        }

        try (InputStream stream = resource.openStream()) {
            Files.copy(stream, new File(outputDirectory, resourceName).toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (final IOException e) {
            throw new UncheckedIOException(e);
        }
    }


    private void initWriterWithHtmlHeader() {
        write(
                "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01//EN\" \"http://www.w3.org/TR/html4/strict.dtd\">\n" +
                        "<html><head><meta httpEquiv=\"Content-Type\" content=\"text/html\"; charset=\"utf-8\"/>\n" +
                        "<title>HTML Sanity Check Results</title>\n" +
                        "<link href=\"scroll.css\" rel=\"stylesheet\" type=\"text/css\"/>\n" +
                        "<link href=\"htmlsc-style.css\" rel=\"stylesheet\" type=\"text/css\"/>\n" +
                        "<script type=\"text/javascript\" src=\"jquery.min.js\"></script>\n" +
                        "<script>\n" +
                        "$(function(){\n" +
                        "    $(document).on( 'scroll', function(){\n" +
                        "      if ($(window).scrollTop() > 100) {\n" +
                        "            $('.scroll-top-wrapper').addClass('show');\n" +
                        "        } else {\n" +
                        "            $('.scroll-top-wrapper').removeClass('show');\n" +
                        "        }\n" +
                        "    });\n" +
                        "});\n" +
                        "$(function(){\n" +
                        "    $(document).on( 'scroll', function(){\n" +
                        "        if ($(window).scrollTop() > 80) {\n" +
                        "            $('.scroll-top-wrapper').addClass('show');\n" +
                        "        } else {\n" +
                        "            $('.scroll-top-wrapper').removeClass('show');\n" +
                        "        }\n" +
                        "    });\n" +
                        "    $('.scroll-top-wrapper').on('click', scrollToTop);\n" +
                        "});\n" +
                        "function scrollToTop() {\n" +
                        "    verticalOffset = typeof(verticalOffset) != 'undefined' ? verticalOffset : 0;\n" +
                        "    element = $('body');\n" +
                        "    offset = element.offset();\n" +
                        "    offsetTop = offset.top;\n" +
                        "    $('html, body').animate({scrollTop: offsetTop}, 500, 'linear');\n" +
                        "}\n" +
                        "</script>\n" +
                        "</head>\n" +
                        "<body id=\"top\">\n");
    }

    /*
     * copy css, javaScript and image/icon files to the html output directory,
     *
     */
    private void copyRequiredResourceFiles(String outputDirectoryPath, List<String> requiredResources) {
        File outputDir = new File(outputDirectoryPath);

        requiredResources.forEach(resource -> {
            try {
                copyResourceFromJarToDirectory(resource, outputDir);
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        });
    }


    /*
    create and open a FileWriter to write the generated HTML
     */

    private FileWriter createWriter(File outputFile) throws IOException {
        return new FileWriter(outputFile);
    }

    @Override
    public void reportOverallSummary() {
        write("<img class=\"logo\" src=\"htmlsanitycheck-logo.png\" alt=\"htmlSC\" align=\"right\"/>");
        write("<h1>HTML Sanity Check Results</h1>");
        write(overallSummaryInfoBox());
        write(allPagesSummaryTable());
        write("<hr>");
    }

    private String overallSummaryInfoBox() {
        int percentageSuccessful = SummarizerUtil.percentSuccessful(totalNrOfChecks(), totalNrOfFindings());
        String pageStr = (totalNrOfPages() > 1) ? "pages" : "page";
        String issueStr = (totalNrOfFindings() != 1) ? "issues" : "issue";
        Float f = Float.valueOf(runResults.checkingTookHowManyMillis()) / 1000;
        String duration = String.format("%.3f", f) + "sec";

        // pages
        // checks
        // findings/issues
        // timer
        return String.format("%s%s%s%s%s%s%s%s",
                infoBoxHeader(),
                infoBoxColumn("pages", String.valueOf(totalNrOfPages()), pageStr),
                infoBoxColumn("checks", String.valueOf(totalNrOfChecks()), "checks"), // NOSONAR(S1192)
                infoBoxColumn("findings", String.valueOf(totalNrOfFindings()), issueStr),
                infoBoxColumn("duration", duration, "duration"),
                infoBoxSeparator(), infoBoxPercentage(percentageSuccessful), infoBoxFooter());
    }

    // TODO: write summary table for pages
    private String allPagesSummaryTable() {
        return ("<h2>Results by Page </h2>" +
                allPagesSummaryTableHead() +
                allPagesSummaryTableBody() +
                allPagesSummaryTableFooter());

    }

    private static String allPagesSummaryTableHead() {
        return "<table>\n" +
                "   <thead>\n" +
                "     <tr>\n" +
                "       <th>Page</th> <th>Checks</th> <th>Findings</th> <th>Success rate</th>\n" +
                "     </tr>\n" +
                "   </thead>\n" +
                "   <tbody>";
    }

    private String allPagesSummaryTableBody() {
        StringBuilder resultStr = new StringBuilder();
        for (SinglePageResults pageResult : pageResults) {
            resultStr.append(pageSummaryTableLine(pageResult));
        }
        return resultStr.toString();
    }

    private static String pageSummaryTableLine(SinglePageResults spr) {
        String classStr = (spr.nrOfFindingsOnPage() == 0) ? "success" : "failures";
        int percentageSuccessful =
                SummarizerUtil.percentSuccessful(spr.nrOfItemsCheckedOnPage(), spr.nrOfFindingsOnPage());

        String pageHref =
                CreateLinkUtil.convertToLink(spr.getPageFileName());

        return String.format(
                "<tr>%n" +
                        "  <td class=\"%s\"><a href=\"#%s\">%s</a></td>%n" +
                        "  <td class=\"number\">%d</td>%n" +
                        "  <td class=\"number\">%d</td>%n" +
                        "  <td class=\"number %s\">%d%%</td>%n" +
                        "</tr>",
                classStr, pageHref, spr.getPageFileName(), spr.nrOfItemsCheckedOnPage(), spr.nrOfFindingsOnPage(), classStr, percentageSuccessful);
    }

    private static String allPagesSummaryTableFooter() {
        return "</tbody>\n" +
                "</table>";
    }

    private static String infoBoxHeader() {
        return "\n<table><tr><td><div class=\"summaryGroup\">\n" +
                "<table>\n" +
                "<tr>";
    }

    private static String infoBoxColumn(String id, String countStr, String label) {
        return String.format(
                "%n<td>%n" +
                        "  <div class=\"infoBox\" id=\"%s\"><div class=\"counter\">%s</div>%s</div>%n" +
                        "</td>",
                id, countStr, label
        );
    }

    private static String infoBoxSeparator() {
        return "</tr>\n" +
                "</table></div></td  >\n";
    }

    private static String infoBoxPercentage(int percentageSuccessful) {
        String percentageClass = (percentageSuccessful != 100) ? "infoBox failures" : "infoBox success";
        return String.format(
                "<td>%n" +
                        "<div class=\"%s\" id=\"successRate\"><div class=\"percent\">%d%%</div>successful</div>%n" +
                        "</td>%n",
                percentageClass, percentageSuccessful
        );
    }

    private static String infoBoxFooter() {
        return "</tr></table>\n" +
                "\n";
    }

    @Override
    protected void reportPageSummary(SinglePageResults pageResult) {
        String pageID = CreateLinkUtil.convertToLink(pageResult.getPageFileName());


        write(String.format(
                "%n%n<h1 id=\"%s\">Results for %s </h1>%n",
                pageID, pageResult.getPageFileName()
        ));

        write(String.format(
                "location : %s <p>%n",
                pageResult.getPageFilePath()
        ));

        int nrOfItemsChecked = pageResult.nrOfItemsCheckedOnPage();
        int nrOfFindings = pageResult.nrOfFindingsOnPage();

        int percentageSuccessful = SummarizerUtil.percentSuccessful(
                nrOfItemsChecked,
                nrOfFindings
        );
        String issueStr = (nrOfFindings > 1) ? "issues" : "issue";

        write(infoBoxHeader());

        int pageSize = pageResult.getPageSize();
        String sizeUnit = (pageSize >= 1_000_000) ? "MByte" : "kByte";

        String pageSizeStr = String.valueOf(SummarizerUtil.threeDigitTwoDecimalPlaces(pageSize));

        write(infoBoxColumn("size", pageSizeStr, sizeUnit));

        write(infoBoxColumn("checks", String.valueOf(nrOfItemsChecked), "checks"));

        write(infoBoxColumn("findings", String.valueOf(nrOfFindings), issueStr));

        write(infoBoxSeparator());

        write(infoBoxPercentage(percentageSuccessful));

        write(infoBoxFooter());
    }

    @Override
    protected void reportPageFooter() {
        String footerContent = "Your footer content here";

        write(String.format("%s%n", footerContent));
    }

    // TODO: add "GeneralRemark" to output, if it exists
    @Override
    protected void reportSingleCheckSummary(SingleCheckResults singleCheckResults) {
        String headerClass = (singleCheckResults.nrOfProblems() > 0) ? "failures" : "success";

        write(String.format(
                "%n<div class=\"%s\"><h3>%s</h3></div>%n%n",
                headerClass, singleCheckResults.getWhatIsChecked()
        ));

        write(String.format(
                "%d %s checked,%n" +
                        "%d %s found.<br>%n" +
                        "%s%n",
                singleCheckResults.getNrOfItemsChecked(),
                singleCheckResults.getSourceItemName(),
                singleCheckResults.nrOfProblems(),
                singleCheckResults.getTargetItemName(),
                singleCheckResults.getGeneralRemark()
        ));
    }

    @Override
    protected void reportSingleCheckDetails(SingleCheckResults checkResults) {
        if (!checkResults.getFindings().isEmpty()) {

            write("\n  <ul>\n");
            checkResults.getFindings().forEach(finding -> write(String.format("      <li> %s </li>%n", finding.toString())));
            write("  </ul>\n");
        }
    }

    /**
     * Tries to find a writable directory. First tries dirName,
     * if that does not work takes User.dir as second choice.
     *
     * @param dirName : e.g. /Users/aim42/projects/htmlsc/build/report/htmlchecks
     * @return complete path to a writable file that does not currently exist.
     */
    private File createOutputFile(String dirName) {
        File outputFolder = new File(dirName);

        if (!outputFolder.isDirectory() || !outputFolder.canWrite()) {
            outputFolder = new File(System.getProperty("user.dir"));
            log.warn("Could not write to '{}', using '{}' instead.", dirName, outputFolder);
        }

        // make sure we really have an existing file!
        return new File(outputFolder, HtmlReporter.REPORT_FILENAME);
    }


    @Override
    protected void closeReport() {
        try {
            write(String.format(
                    "<!-- scroll-to-top icon -->%n" +
                            "<div class=\"scroll-top-wrapper\">%n" +
                            "<img src=\"arrow-up.png\" alt=\"to top\"/>%n" +
                            "</div>%n" +
                            "<div id=\"footer\">%n" +
                            "Generated by <a href=\"https://www.aim42.org\">htmlSanityCheck</a> (version %s) at %s%n" +
                            "</div>%n", createdByHSCVersion, createdOnDate
            ));
            write("</body></html>\n");
            writer.flush();

            String logMessage = String.format(
                    "wrote report to %s%s%s%n",
                    resultsOutputDir, File.separatorChar, REPORT_FILENAME
            );
            log.info(logMessage);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

}
/*======================================================================

Copyright Gernot Starke and aim42 contributors

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an
 "AS IS" BASIS,WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 ======================================================================*/
