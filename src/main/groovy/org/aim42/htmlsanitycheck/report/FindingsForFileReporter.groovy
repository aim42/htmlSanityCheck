/**
 * Report the results of one or more checks.
 * Applies the Template-Method-Pattern (in @see reportFindings())
 * to determine concrete output format (e.g. HTML or text) or destination
 * (file or console).
 *
 */

// see end-of-file for license information

package org.aim42.htmlsanitycheck.report

import org.aim42.htmlsanitycheck.collect.SingleCheckResults
import org.aim42.htmlsanitycheck.html.HtmlPageMetaInfo


abstract class FindingsForFileReporter {

    private HtmlPageMetaInfo metaInfo

    ArrayList<SingleCheckResults> checkingResults

    int totalNrOfChecksPerformed
    int totalNrOfFindings
    int percentSuccessful

    String createdOnDate



    public FindingsForFileReporter( ArrayList<SingleCheckResults> checkingResults  ) {

        this.checkingResults = checkingResults
        this.totalNrOfChecksPerformed = 0

        this.percentSuccessful = 0
        this.totalNrOfFindings = 0

        this.createdOnDate = new Date().format('dd. MMMM YYYY, HH:mm')

     }

    /**
     * Presents a report of a subtype (e.g. HTML, console)
     * using the Template-Method-Pattern.
     */
    public void reportFindings() {

        // sum up numbers, calculate percentages
        calculateSummary()

        // e.g. setup filename, open output file
        initializeReport()

        // output general information about checks
        reportHeader()

        // output statistics, percentages etc.
        reportOverallSummary()

        // output details
        reportSingleCheckSummary()

        closeReport()
    }

    // primarily used for testing
    public void addCheckingField(SingleCheckResults resultsCollector) {
        this.checkingResults.add(resultsCollector)
    }

    public void calculateSummary() {
        totalNrOfChecksPerformed = 0
        totalNrOfFindings = 0
        checkingResults.each { checkingResult ->
            totalNrOfChecksPerformed += checkingResult.nrOfItemsChecked
            totalNrOfFindings += checkingResult.nrOfProblems()
        }

        // base case: if no checks performed, 100% successful
        if (totalNrOfChecksPerformed <= 0) {
            percentSuccessful = 100
        }
        // at least one check was performed, calculate percentage
        else {
            percentSuccessful =
                    100 - (100 * totalNrOfFindings) / totalNrOfChecksPerformed
        }

    }


    // stuff delegated to subclasses
    // ************************************

    abstract void initializeReport()

    abstract void reportHeader()

    abstract void reportOverallSummary()

    abstract void reportSingleCheckSummary()

    abstract void closeReport()

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