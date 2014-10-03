package org.aim42.htmlsanitycheck.report

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

class ConsoleReporter extends Reporter {



    @Override
    void initReport() {
        println "********* HTML Sanity Checker findings report *********"
        println "created on $createdOnDate"
        println ""
    }


    @Override
    void reportOverallSummary() {
        int percentageSuccessful = SummarizerUtil.percentSuccessful( totalNrOfChecks(), totalNrOfFindings())

        println "Summary:"
        println "========"
        println "checked ${totalNrOfChecks()} items on ${totalNrOfPages()} pages, "
        println "found   ${totalNrOfFindings()}, $percentageSuccessful% successful."
        println "-" * 50
    }


    @Override
    void reportPageFindings(SinglePageResults pageResult) {
          reportSingleCheckSummary( pageResult )

    }


    private void reportSingleCheckSummary( SingleCheckResults checkResults ) {
        println "\nDetails:\n"

        checkResults.each { result ->
            println "Results for ${result.whatIsChecked}"
            println "${result.nrOfItemsChecked} $result.sourceItemName checked,"
            println "${result.nrOfProblems()} $result.targetItemName found.\n"
            result.findings.each {
                println it.toString()
            }

            println "-" * 50
        }  // each result
    }


    @Override
    void closeReport() {
        println "thanx for using HtmlSanityChecker."
    }

}

