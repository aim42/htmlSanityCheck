// see end-of-file for license information
package org.aim42.htmlsanitycheck.report

import org.aim42.htmlsanitycheck.collect.PerRunResults
import org.aim42.htmlsanitycheck.collect.SingleCheckResults
import org.aim42.htmlsanitycheck.collect.SinglePageResults

/**
 * write the findings report to HTML
 */
public class HtmlReporter extends Reporter {

    //
    private final static REPORT_FILENAME = "index.html"
    private String resultsOutputDir

    // the completeOutputFilePath will be set by
    private String completeOutputFilePath

    private FileWriter writer


    HtmlReporter(PerRunResults runResults, String outputDir) {
        super(runResults)
        this.resultsOutputDir = outputDir
    }


    @Override
    void initReport() {

        // determine a path where we can write our output file...
        completeOutputFilePath = determineOutputFilePath(resultsOutputDir, REPORT_FILENAME)

        // init the private writer object
        writer = initWriter(completeOutputFilePath)


        writer << """
            <!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
            <html><head><meta httpEquiv="Content-Type" content="text/html; charset=utf-8"/>
    <title>HTML Sanity Check Results</title>
    <link href="css/base-style.css" rel="stylesheet" type="text/css"/>
    <link href="css/style.css" rel="stylesheet" type="text/css"/>
    </head><body><div id="content">"""
    }

    /*
    create and open a FileWriter to write the generated HTML
     */
    private FileWriter initWriter(String directoryAndFileName) {
        writer = new FileWriter( directoryAndFileName )

    }

    @Override
    void reportOverallSummary() {
        int percentageSuccessful = SummarizerUtil.percentSuccessful(totalNrOfChecks(), totalNrOfFindings())
        String pageStr = (totalNrOfPages() > 1) ? "pages" : "page"
        String issueStr = (totalNrOfFindings() > 1) ? "issues" : "issue"


        writer << "<h1>Summary of all pages</h1>"

        // outer table
        writer << """<table><tr><td><div class="summaryGroup">"""

        // left table with pages, checks, findings + timing
        writer << " <table>\n<tr>"
        // pages
        writer << """<td>\n<div class=\"infoBox\" id=\"pages\">\n
                        <div class=\"counter\">${totalNrOfPages()}</div>\n
                <p>$pageStr</p></div></td>"""

        // checks
        writer << """<td>\n<div class=\"infoBox\" id=\"checks\">\n
                        <div class=\"counter\">${totalNrOfChecks()}</div>\n
                <p>checks</p></div></td>"""

        // findings/issues
        writer << """<td>\n<div class=\"infoBox\" id=\"findings\">\n
                        <div class=\"counter\">${totalNrOfFindings()}</div>\n
                <p>$issueStr</p></div></td>"""

        // end left table
        writer << """</tr></table></div></td>"""

        writer << """ <td>
        <div class="infoBox failures" id="successRate">
        <div class="percent">$percentageSuccessful%</div><p>successful</p>
        </div></td>"""

        writer << """</tr></table></div>"""


        // TODO: table of all pages-checked incl. links to details


    }

    @Override
    void reportPageSummary(SinglePageResults pageResult) {

        writer.write "<h2>Results for ${pageResult.pageFileName} </h2>"
        writer << "filename   : " + pageResult.pageFileName
        writer << "located : " + pageResult.pageFilePath
        writer << "page size  : " + pageResult.pageSize + " bytes"

    }



    @Override
    protected void reportPageFooter() {
        writer << "<hr><p>"

    }



    @Override
    protected void reportSingleCheckSummary(SingleCheckResults singleCheckResults) {
        singleCheckResults.each { result ->
            writer << "<h3>Results for ${result.whatIsChecked}</h3>"
            writer << "${result.nrOfItemsChecked} $result.sourceItemName checked,"
            writer << "${result.nrOfProblems()} $result.targetItemName found.\n"

        }
    }

    protected void reportSingleCheckDetails(SingleCheckResults checkResults) {
        writer << """ <p>"""
        checkResults.findings.each { finding ->
            writer << finding.toString()
        }
        writer << "<p>"
    }

    /**
     * tries to find a writable directory. First tries dirName,
     * if that does not work takes User.dir as second choice.
     * @param dirName : e.g. /Users/aim42/projects/htmlsc/build/check
     * @param fileName : default "index.html"
     * @return complete path to a writable file that does not currently exist.
     */
    private String determineOutputFilePath(String dirName, String fileName) {
        String realPath = "${dirName}${File.separator}"

        File realDir = new File(realPath)

        if (realDir.isDirectory() && realDir.canWrite()) {
            realPath = realPath + fileName
        }
        else realPath = System.getProperty("user.dir") + File.separator + fileName

        return realPath
    }

    void writeSummary(String what, int howMany) {
        writer << """<td><div class="infoBox">
                       <div class="counter">$howMany</div>
                       <p>$what</p>
                   </div></td>"""
    }

    void writeSummaryPostfix(int percentage) {
        writer << """</tr>
                        </table>
                    </div>
                </td>
                <td>
                    <div class="infoBox success" id="successRate">
                        <div class="percent">$percentage%
                        </div>
                        <p>successful</p>
                    </div>
                </td>
            </tr>
        </table>"""
    }

    private void writeSummaryPrefix() {
        writer << """<h1>Test Summary</h1>
    <div id="summary">
        <table>
            <tr>
                <td>
                    <div class="summaryGroup">
                        <table>
                            <tr>"""
    }


    @Override
    void closeReport() {
        writer << """</div></body></html>"""
        writer.flush()

        println "wrote report to ${resultsOutputDir}${File.separatorChar}$REPORT_FILENAME"
    }
}

/*======================================================================

Copyright 2014 Gernot Starke and aim42 contributors

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
