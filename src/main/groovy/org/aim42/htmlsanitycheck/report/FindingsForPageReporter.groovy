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
import org.aim42.htmlsanitycheck.collect.SinglePageResults
import org.aim42.htmlsanitycheck.html.HtmlPageMetaInfo


abstract class FindingsForPageReporter {

    public SinglePageResults pageResults

    public Statistics statistics

    public String createdOnDate



    public FindingsForPageReporter( SinglePageResults pageResults  ) {

        this.pageResults = pageResults
        this.statistics = new Statistics(
          pageResults.totalNrOfItemsChecked(),
          pageResults.totalNrOfFindings()
        )


        this.createdOnDate = new Date().format('dd. MMMM YYYY, HH:mm')

     }

    /**
     * Presents a report of a subtype (e.g. HTML, console)
     * using the Template-Method-Pattern.
     */
    public void reportFindings() {

        // sum up numbers, calculate percentages


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
    public void addCheckingField( SingleCheckResults resultsCollector) {
        this.pageResults.singleCheckResults.add( resultsCollector )

        // we need to re-calculate the summary after every new resultsCollector
        this.statistics.calculateSummary()
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