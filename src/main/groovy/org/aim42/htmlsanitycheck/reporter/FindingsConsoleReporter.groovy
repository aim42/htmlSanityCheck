package org.aim42.htmlsanitycheck.reporter

import org.aim42.htmlsanitycheck.checker.CheckingResultsCollector


// see end-of-file for license information


class FindingsConsoleReporter extends FindingsForFileReporter {


    public FindingsConsoleReporter(ArrayList<CheckingResultsCollector> results) {
        super(results)

    }


    @Override
    void initializeReport() {
        // as we output to console, we don't need initialization
        println ""
    }

    @Override
    void reportHeader() {
        println "********* HTML Sanity Checker findings report *********"
        println "created on $createdOnDate"
        println ""

    }

    @Override
    void reportOverallSummary() {
        println "Summary:"
        println "========"
        println "performed ${checkingResults.size()} types of checks"
        println "checked $totalNrOfChecksPerformed items total, "
        println "found   $totalNrOfFindings issues, $percentSuccessful% successful."
        println "-" * 50
    }

    @Override
    void reportSingleCheckSummary() {
        println "\nDetails:\n"

        checkingResults.each { result ->
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
