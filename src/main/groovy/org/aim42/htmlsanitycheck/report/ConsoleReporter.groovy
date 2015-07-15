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


    // from AllChecksRunner - create ConsoleReporter with PerRunResults
    public ConsoleReporter( PerRunResults runResults ) {
        super( runResults )
    }


    @Override
    void initReport() {
        Long millis = runResults.checkingTookHowManyMillis()

        println "********* HTML Sanity Checker findings report *********"
        println "created on $createdOnDate by version ${ProductVersion.getVersion()}"
        println "checking took $millis msecs."
        println ""
    }


    @Override
    void reportOverallSummary() {
        int percentageSuccessful =
                SummarizerUtil.percentSuccessful( totalNrOfChecks(), totalNrOfFindings())

        String pageStr = (totalNrOfPages() > 1) ? "pages" : "page"
        String issueStr = (totalNrOfFindings() > 1) ? "issues" : "issue"

        println "Summary for all pages:"
        println "======================"
        println "checked ${totalNrOfChecks()} items on ${totalNrOfPages()} $pageStr, "
        println "found ${totalNrOfFindings()} $issueStr, $percentageSuccessful% successful."
        println "-" * 50
    }


    @Override
    void reportPageSummary( SinglePageResults pageResult ) {
        println "Summary for file ${pageResult.pageFileName}\n"
        println "page path  : " + pageResult.pageFilePath
        println "page title : " + pageResult.pageTitle
        println "page size  : " + pageResult.pageSize + " bytes"

    }



    @Override
    protected void reportPageFooter() {
        println "="*50
    }

    @Override
    protected void reportSingleCheckSummary( SingleCheckResults checkResults ) {
        println "\n"
        println "-"*50
        checkResults.each { result ->
            println "Results for ${result.whatIsChecked}"
            println "${result.nrOfItemsChecked} $result.sourceItemName checked,"
            println "${result.nrOfProblems()} $result.targetItemName found.\n"
        }
    }

    @Override
    protected void reportSingleCheckDetails( SingleCheckResults checkResults  ) {
        checkResults.findings.each { finding ->
            println finding.toString()

        }

        println "-" * 50

    }

    @Override
    void closeReport() {
        println "thanx for using HtmlSanityChecker."
    }

}

