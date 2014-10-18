// see end-of-file for license information
package org.aim42.htmlsanitycheck.report


import org.aim42.htmlsanitycheck.collect.PerRunResults
import org.aim42.htmlsanitycheck.collect.SingleCheckResults
/**
 * write the findings report to HTML
 */
public class HtmlReporter extends Reporter {

    //
    private final static String REPORT_FILENAME = "index.html"
    private String resultsOutputDir

    private String completeOutputFilePath

    private FileWriter writer

    private final static String BACK_TO_TOP_LINK =
    "<div id=\"backtotoplink\"><a href=\"#top\" align=\"right\">back to top</a></div>"


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
<link href="htmlsc-style.css" rel="stylesheet" type="text/css"/>
</head><body><div id="content">"""
    }


    /*
    create and open a FileWriter to write the generated HTML
     */

    private FileWriter initWriter(String directoryAndFileName) {
        writer = new FileWriter(directoryAndFileName)

    }

    @Override
    void reportOverallSummary() {

        writer << "<img class='logo' src='https://github.com/aim42/htmlSanityCheck/blob/master/htmlsanitycheck-logo.png' alt='htmlSC' align='right'/>"
        writer << "<h1 id=\"top\">HTML Sanity Check Results </h1>"

        writer << overallSummaryInfoBox()

        writer << allPagesSummaryTable()

        // TODO: table of all pages-checked incl. links to details

        writer << "<hr>"

    }

    private String overallSummaryInfoBox() {
        int percentageSuccessful = SummarizerUtil.percentSuccessful(totalNrOfChecks(), totalNrOfFindings())
        String pageStr = (totalNrOfPages() > 1) ? "pages" : "page"
        String issueStr = (totalNrOfFindings() > 1) ? "issues" : "issue"
        Float f = runResults.checkingTookHowManyMillis() / 1000
        String duration = f.trunc(3).toString() + "sec"


        return ( infoBoxHeader()
            // pages
        + infoBoxColumn( "pages", totalNrOfPages().toString(), pageStr)
            // checks
        + infoBoxColumn( "checks", totalNrOfChecks().toString(), "checks")
            // findings/issues
        + infoBoxColumn( "findings", totalNrOfFindings().toString(), issueStr)
            // timer
        + infoBoxColumn( "duration", duration, "duration")

        + infoBoxSeparator()

        + infoBoxPercentage( percentageSuccessful )

        + infoBoxFooter() )
    }

    // TODO: write summary table for pages
    private String allPagesSummaryTable() {

        return ("<h2>Results by Page </h2>" +
        allPagesSummaryTableHead() +
        allPagesSummaryTableBody() +
        allPagesSummaryTableFooter())

    }

    private static String allPagesSummaryTableHead() {
        return """
<table><thead><tr>
<th>Page</th>
<th>Checks</th>
<th>Findings</th>
<th>Success rate</th>
</tr>
</thead><tbody>"""

    }

    private String allPagesSummaryTableBody() {
        String resultStr = ""
        pageResults.each { pageResult ->
            resultStr += pageSummaryTableLine( pageResult )
        }
        return resultStr
    }

    private String pageSummaryTableLine( SinglePageResults spr ) {
        String classStr = (spr.nrOfFindingsOnPage() == 0) ? "success" : "failures"
        int percentageSuccessful =
                SummarizerUtil.percentSuccessful(spr.nrOfItemsCheckedOnPage(), spr.nrOfFindingsOnPage())

        String pageHref =
                CreateLinkUtil.convertToLink( spr.pageFileName )

        return """
<tr><td class="$classStr">
<a href=\"#${pageHref}\">${spr.pageFileName}</a></td>
<td class="number">${spr.nrOfItemsCheckedOnPage()}</td>
<td class="number">${spr.nrOfFindingsOnPage()}</td>
<td class="number $classStr">$percentageSuccessful%</td>
</tr>"""
    }

    private static String allPagesSummaryTableFooter() {
        return """</tbody></table>"""
    }
    private static String infoBoxHeader() {
        // outer table
        return """
<table><tr><td><div class="summaryGroup">
<table>\n<tr>"""
    }


    private static String infoBoxColumn(String id, String countStr, String label) {
        return """
<td>\n<div class=\"infoBox\" id=\"$id\">\n
<div class=\"counter\">$countStr</div>\n
<p>$label</p></div></td>"""
    }

    private static String infoBoxSeparator() {
        return """</tr></table></div></td>"""
    }

    private static String infoBoxPercentage( int percentageSuccessful ) {
        String percentageClass = (percentageSuccessful != 100) ? "infoBox failures" : "infoBox success"
        return """
<td><div class="$percentageClass" id="successRate">
<div class="percent">$percentageSuccessful%</div><p>successful</p>
</div></td>"""
    }

    private static String infoBoxFooter() {
        return """
</tr></table>
"""
    }


    @Override
    void reportPageSummary(SinglePageResults pageResult) {

        String pageID =
                CreateLinkUtil.convertToLink( pageResult.pageFileName)

        writer << """<h1 id=\"${pageID}\">Results for ${pageResult.pageFileName} </h1>"""

        writer << "location : ${pageResult.pageFilePath} <p>"

        // generate the InfoBox for this page

        int nrOfItemsChecked = pageResult.nrOfItemsCheckedOnPage()
        int nrOfFindings     = pageResult.nrOfFindingsOnPage()

        int percentageSuccessful =
                SummarizerUtil.percentSuccessful(
                        nrOfItemsChecked,
                        nrOfFindings)
        String issueStr = (nrOfFindings > 1) ? "issues" : "issue"

        writer << infoBoxHeader()

        // size
        int pageSize = pageResult.pageSize
        String sizeUnit = (pageSize >= 1_000_000) ? "MByte" : "kByte"

        String pageSizeStr = SummarizerUtil.threeDigitTwoDecimalPlaces( pageSize )

        writer << infoBoxColumn( "size", pageSizeStr, sizeUnit )

        // checks
        writer << infoBoxColumn( "checks", nrOfItemsChecked.toString(), "checks")

        // findings/issues
        writer << infoBoxColumn( "findings", nrOfFindings.toString(), issueStr)
        // end left table
        writer << infoBoxSeparator()

        writer << infoBoxPercentage( percentageSuccessful )

        writer << infoBoxFooter()

        writer << BACK_TO_TOP_LINK

    }


    @Override
    protected void reportPageFooter() {
        writer << "<hr><p>"

    }


    @Override
    protected void reportSingleCheckSummary(SingleCheckResults singleCheckResults) {
        singleCheckResults.each { result ->
            // colorize failed checks with failure-class
            String failureClassBegin = ""
            String failureClassEnd   = ""

            if (result.nrOfProblems() > 0) {
                failureClassBegin = "<div class=\"failures\">"
                failureClassEnd   = "</div>"
            }

            writer << "${failureClassBegin}<h3>${result.whatIsChecked}</h3>${failureClassEnd}"

            writer << "${result.nrOfItemsChecked} $result.sourceItemName checked, "
            writer << "${result.nrOfProblems()} $result.targetItemName found.\n"

        }
    }

    protected void reportSingleCheckDetails(SingleCheckResults checkResults) {
        writer << """ <p>"""
        checkResults.findings.each { finding ->
            writer << finding.toString() + "<p>"
        }
        writer << "<p>"
    }

    /**
     * tries to find a writable directory. First tries dirName,
     * if that does not work takes User.dir as second choice.
     * @param dirName : e.g. /Users/aim42/projects/htmlsc/build/report/htmlchecks
     * @param fileName : default "index.html"
     * @return complete path to a writable file that does not currently exist.
     */
    private String determineOutputFilePath(String dirName, String fileName) {
        String realPath = "${dirName}${File.separator}"

        File realDir = new File(realPath)

        if (realDir.isDirectory() && realDir.canWrite()) {
            realPath = realPath + fileName
        } else realPath = System.getProperty("user.dir") + File.separator + fileName

        return realPath
    }


    @Override
    void closeReport() {
        writer << """<div id="footer"><p>Generated by
               <a href="http://www.aim42.org">htmlSanityChecker</a> at ${createdOnDate}</p>
                 </div>"""
        writer << """</div></body></html>"""
        writer.flush()

        println "wrote report to ${resultsOutputDir}${File.separatorChar}$REPORT_FILENAME"
    }
}
import org.aim42.htmlsanitycheck.collect.SinglePageResults

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
