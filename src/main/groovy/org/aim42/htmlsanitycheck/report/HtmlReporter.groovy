// see end-of-file for license information
package org.aim42.htmlsanitycheck.report

import groovy.util.logging.Slf4j
import org.aim42.htmlsanitycheck.collect.PerRunResults
import org.aim42.htmlsanitycheck.collect.SingleCheckResults
import org.aim42.htmlsanitycheck.collect.SinglePageResults
import org.gradle.util.GFileUtils

/**
 * write the findings report to HTML
 */
@Slf4j
public class HtmlReporter extends Reporter {

    //
    private final static String REPORT_FILENAME = "index.html"
    private String resultsOutputDir

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
        writer = createWriter(completeOutputFilePath)

        initWriterWithHtmlHeader()


        // we have a writer, therefore an existing output directory!

        def requiredResourceFiles = ["arrow-up.png", "htmlsanitycheck-logo.png",
                                     "htmlsc-style.css", "scroll.css",
                                     "jquery.min.js"]
        copyRequiredResourceFiles( resultsOutputDir, requiredResourceFiles )
    }


    /*
    we need some static files next to the report html.. css, js and logo stuff.

    Originally I posted this as a question to the gradle forum:
    http://forums.gradle.org/gradle/topics/-html-checking-plugin-how-to-copy-required-css-to-output-directory

    Answers were:
    http://stackoverflow.com/questions/10308221/how-to-copy-file-inside-jar-to-outside-the-jar

    https://github.com/gradle/gradle/blob/master/subprojects/performance/src/testFixtures/groovy/org/gradle/performance/results/ReportGenerator.java#L50-50

     */
    private void copyResourceFromJarToDirectory(String resourceName, File outputDirectory) {
        URL resource = getClass().getClassLoader().getResource(resourceName);
        // String dir = StringUtils.substringAfterLast(resourceName, ".");
        //GFileUtils.copyURLToFile(resource, new File(outputDirectory, dir + "/" + resourceName));
        GFileUtils.copyURLToFile(resource, new File(outputDirectory, resourceName));
    }


    private initWriterWithHtmlHeader() {
        writer << """
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html><head><meta httpEquiv="Content-Type" content="text/html"; charset="utf-8"/>
<title>HTML Sanity Check Results</title>
<link href="scroll.css" rel="stylesheet" type="text/css"/>
<link href="htmlsc-style.css" rel="stylesheet" type="text/css"/>

<script type="text/javascript" src="jquery.min.js"></script>

<script>
\$(function(){

    \$(document).on( 'scroll', function(){
      if (\$(window).scrollTop() > 100) {
            \$('.scroll-top-wrapper').addClass('show');
        } else {
            \$('.scroll-top-wrapper').removeClass('show');
        }
    });
});

\$(function(){
    \$(document).on( 'scroll', function(){
        if (\$(window).scrollTop() > 80) {
            \$('.scroll-top-wrapper').addClass('show');
        } else {
            \$('.scroll-top-wrapper').removeClass('show');
        }
    });
    \$('.scroll-top-wrapper').on('click', scrollToTop);
});

function scrollToTop() {
    verticalOffset = typeof(verticalOffset) != 'undefined' ? verticalOffset : 0;
    element = \$('body');
    offset = element.offset();
    offsetTop = offset.top;
    \$('html, body').animate({scrollTop: offsetTop}, 500, 'linear');
}
</script>

</head>
<body id="top">
"""
    }

    /*
    * copy css, javaScript and image/icon files to the html output directory,
    *
     */
    private void  copyRequiredResourceFiles( String outputDirectoryPath, List<String> requiredResources ) {
        File outputDir = new File( outputDirectoryPath )

        requiredResources.each {  resource ->
            copyResourceFromJarToDirectory( resource, outputDir )
        }

    }



    /*
    create and open a FileWriter to write the generated HTML
     */

    private FileWriter createWriter(String directoryAndFileName) {
        writer = new FileWriter(directoryAndFileName)

    }

    @Override
    void reportOverallSummary() {

        writer << "<img class=\"logo\" src=\"htmlsanitycheck-logo.png\" alt=\"htmlSC\" align=\"right\"/>"
        writer << "<h1>HTML Sanity Check Results </h1>"

        writer << overallSummaryInfoBox()

        writer << allPagesSummaryTable()

        writer << "<hr>"

    }

    private String overallSummaryInfoBox() {
        int percentageSuccessful = SummarizerUtil.percentSuccessful(totalNrOfChecks(), totalNrOfFindings())
        String pageStr = (totalNrOfPages() > 1) ? "pages" : "page"
        String issueStr = (totalNrOfFindings() > 1) ? "issues" : "issue"
        Float f = runResults.checkingTookHowManyMillis() / 1000
        String duration = f.trunc(3).toString() + "sec"


        return (infoBoxHeader()
                // pages
                + infoBoxColumn("pages", totalNrOfPages().toString(), pageStr)
                // checks
                + infoBoxColumn("checks", totalNrOfChecks().toString(), "checks")
                // findings/issues
                + infoBoxColumn("findings", totalNrOfFindings().toString(), issueStr)
                // timer
                + infoBoxColumn("duration", duration, "duration")

                + infoBoxSeparator()

                + infoBoxPercentage(percentageSuccessful)

                + infoBoxFooter())
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
<table>
   <thead>
     <tr>
       <th>Page</th> <th>Checks</th> <th>Findings</th> <th>Success rate</th>
     </tr>
   </thead>
   <tbody>"""

    }

    private String allPagesSummaryTableBody() {
        String resultStr = ""
        pageResults.each { pageResult ->
            resultStr += pageSummaryTableLine(pageResult)
        }
        return resultStr
    }

    private static String pageSummaryTableLine(SinglePageResults spr) {
        String classStr = (spr.nrOfFindingsOnPage() == 0) ? "success" : "failures"
        int percentageSuccessful =
                SummarizerUtil.percentSuccessful(spr.nrOfItemsCheckedOnPage(), spr.nrOfFindingsOnPage())

        String pageHref =
                CreateLinkUtil.convertToLink(spr.pageFileName)

        return """
      <tr>
        <td class="$classStr"><a href=\"#${pageHref}\">${spr.pageFileName}</a></td>
        <td class="number">${spr.nrOfItemsCheckedOnPage()}</td>
        <td class="number">${spr.nrOfFindingsOnPage()}</td>
        <td class="number $classStr">$percentageSuccessful%</td>
      </tr>"""
    }

    private static String allPagesSummaryTableFooter() {
        return """\n
    </tbody>
  </table>"""
    }

    private static String infoBoxHeader() {
        // outer table
        return """\n
<table><tr><td><div class="summaryGroup">
<table>\n<tr>"""
    }


    private static String infoBoxColumn(String id, String countStr, String label) {
        return """\n
    <td>\n
      <div class=\"infoBox\" id=\"$id\">\n
          <div class=\"counter\">$countStr</div>\n
          $label</div>\n
    </td>"""
 }

    private static String infoBoxSeparator() {
        return "</tr>\n</table></div></td  >\n"
    }

    private static String infoBoxPercentage(int percentageSuccessful) {
        String percentageClass = (percentageSuccessful != 100) ? "infoBox failures" : "infoBox success"
        return """
    <td><div class="$percentageClass" id="successRate">\n
        <div class="percent">$percentageSuccessful%</div>
        successful</div>
    </td>"""
    }

    private static String infoBoxFooter() {
        return "</tr></table>\n\n"
    }


    @Override
    void reportPageSummary(SinglePageResults pageResult) {

        String pageID =
                CreateLinkUtil.convertToLink(pageResult.pageFileName)

        writer << """\n\n<h1 id=\"${pageID}\">Results for ${pageResult.pageFileName} </h1>\n"""

        writer << """location : ${pageResult.pageFilePath} <p>"""

        // generate the InfoBox for this page

        int nrOfItemsChecked = pageResult.nrOfItemsCheckedOnPage()
        int nrOfFindings = pageResult.nrOfFindingsOnPage()

        int percentageSuccessful =
                SummarizerUtil.percentSuccessful(
                        nrOfItemsChecked,
                        nrOfFindings)
        String issueStr = (nrOfFindings > 1) ? "issues" : "issue"

        writer << infoBoxHeader()

        // size
        int pageSize = pageResult.pageSize
        String sizeUnit = (pageSize >= 1_000_000) ? "MByte" : "kByte"

        String pageSizeStr = SummarizerUtil.threeDigitTwoDecimalPlaces(pageSize)

        writer << infoBoxColumn("size", pageSizeStr, sizeUnit)

        // checks
        writer << infoBoxColumn("checks", nrOfItemsChecked.toString(), "checks")

        // findings/issues
        writer << infoBoxColumn("findings", nrOfFindings.toString(), issueStr)
        // end left table
        writer << infoBoxSeparator()

        writer << infoBoxPercentage(percentageSuccessful)

        writer << infoBoxFooter()

    }


    @Override
    protected void reportPageFooter() {
        writer << "<hr><p>"

    }


    // TODO: add "GeneralRemark" to output, if it exists
    @Override
    protected void reportSingleCheckSummary(SingleCheckResults singleCheckResults) {

        singleCheckResults.each { result ->
            // colorize failed checks with failure-class, successful with success-class
            String headerClass = (result.nrOfProblems() > 0) ? "failures" : "success"

            writer << """\n<div class="${headerClass}"><h3>${result.whatIsChecked}</h3></div>"""

            writer << """\n\n
 ${result.nrOfItemsChecked} $result.sourceItemName checked,
 ${result.nrOfProblems()} $result.targetItemName found.\n"""

        }
    }

    protected void reportSingleCheckDetails(SingleCheckResults checkResults) {
        if (checkResults.findings.size() > 0) {
            writer << "\n  <ul>"
            checkResults.findings.each { finding ->
                writer << """\n      <li> ${finding.toString()} </li>"""
            }
            writer << "\n  </ul>\n"
        }
    }

    /**
     * tries to find a writable directory. First tries dirName,
     * if that does not work takes User.dir as second choice.
     * @param dirName : e.g. /Users/aim42/projects/htmlsc/build/report/htmlchecks
     * @param fileName : default "index.html"
     * @return complete path to a writable file that does not currently exist.
     */
    private static String determineOutputFilePath(String dirName, String fileName) {
        String realPath = "${dirName}${File.separator}"

        File realDir = new File(realPath)

        if (realDir.isDirectory() && realDir.canWrite()) {
            realPath = realPath + fileName
        } else realPath = System.getProperty("user.dir") + File.separator + fileName

        // make sure we really have an existing file!
        assert realDir.exists()

        return realPath
    }


    @Override
    void closeReport() {
        writer << """
<!-- scroll-to-top icon -->
<div class="scroll-top-wrapper">
   <img src="arrow-up.png" alt="to top"/>
</div>

<div id="footer">
  Generated by <a href="https://www.aim42.org">htmlSanityCheck</a> (version ${createdByHSCVersion}) at ${createdOnDate} 
</div>"""
        writer << "</body></html>"
        writer.flush()

        log.info "wrote report to ${resultsOutputDir}${File.separatorChar}$REPORT_FILENAME"
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
