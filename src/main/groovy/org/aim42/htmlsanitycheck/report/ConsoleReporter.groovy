package org.aim42.htmlsanitycheck.report

import groovy.transform.InheritConstructors
import org.aim42.htmlsanitycheck.ProductVersion
import org.aim42.htmlsanitycheck.collect.PerRunResults
import org.aim42.htmlsanitycheck.collect.SingleCheckResults
import org.aim42.htmlsanitycheck.collect.SinglePageResults

/************************************************************************
 * This is free software - without ANY guarantee!
 *
 *
 * Copyright 2013, Dr. Gernot Starke, arc42.org
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

@InheritConstructors
class ConsoleReporter extends Reporter {
    protected Closure printer = { line -> println(line) }

    // from AllChecksRunner - create ConsoleReporter with PerRunResults
    public ConsoleReporter( PerRunResults runResults ) {
        super( runResults )
    }


    @Override
    void initReport() {
        Long millis = runResults.checkingTookHowManyMillis()

        printer "********* HTML Sanity Checker findings report *********"
        printer "created on $createdOnDate by version ${ProductVersion.getVersion()}"
        printer "checking took $millis msecs."
        printer ""
    }


    @Override
    void reportOverallSummary() {
        int percentageSuccessful =
                SummarizerUtil.percentSuccessful( totalNrOfChecks(), totalNrOfFindings())

        String pageStr = (totalNrOfPages() > 1) ? "pages" : "page"
        String issueStr = (totalNrOfFindings() > 1) ? "issues" : "issue"

        printer "Summary for all pages:"
        printer "======================"
        printer "checked ${totalNrOfChecks()} items on ${totalNrOfPages()} $pageStr, "
        printer "found ${totalNrOfFindings()} $issueStr, $percentageSuccessful% successful."
        printer "-" * 50
    }


    @Override
    void reportPageSummary( SinglePageResults pageResult ) {
        printer "Summary for file ${pageResult.pageFileName}\n"
        printer "page path  : " + pageResult.pageFilePath
        printer "page title : " + pageResult.pageTitle
        printer "page size  : " + pageResult.pageSize + " bytes"

    }



    @Override
    protected void reportPageFooter() {
        printer "="*50
    }

    @Override
    protected void reportSingleCheckSummary( SingleCheckResults checkResults ) {
        printer "\n"
        printer "-"*50
        checkResults.each { result ->
            printer "Results for ${result.whatIsChecked}"
            printer "${result.nrOfItemsChecked} $result.sourceItemName checked,"
            printer "${result.nrOfProblems()} $result.targetItemName found.\n"
            printer "${result.generalRemark}"
        }
    }

    @Override
    protected void reportSingleCheckDetails( SingleCheckResults checkResults  ) {
        checkResults.findings.each { finding ->
            printer finding.toString()

        }

        printer "-" * 50

    }

    @Override
    void closeReport() {
        printer "thanx for using HtmlSanityChecker."
    }

}

