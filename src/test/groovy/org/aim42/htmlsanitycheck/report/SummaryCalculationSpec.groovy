package org.aim42.htmlsanitycheck.report

import org.aim42.htmlsanitycheck.collect.Finding
import org.aim42.htmlsanitycheck.collect.SingleCheckResults
import org.aim42.htmlsanitycheck.collect.SinglePageResults
import spock.lang.Specification

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

class SummaryCalculationSpec extends Specification {

    def "CalculateSimpleSummarySpec"() {


        expect:
        SummarizerUtil.percentSuccessful(0, 0) == 100
    }

    def "CalculateSummarySpec"(int nrChecks, int nrFindings, int successRate) {
        expect:

        SummarizerUtil.percentSuccessful(nrChecks, nrFindings) == successRate

        where:
        nrChecks | nrFindings | successRate
        0        | 0          | 100
        1        | 1          | 0
        10       | 1          | 90
        10       | 0          | 100
        10       | 2          | 80
        10       | 3          | 70
        3        | 1          | 66
        3        | 2          | 33
        1000     | 250        | 75

    }

    def "nrOfFindingsSpec"(int nrChecks, int nrFindings) {

        setup:

        SingleCheckResults singleCheckResults = new SingleCheckResults()
        SinglePageResults pageResults = new SinglePageResults()

        when:
        singleCheckResults.nrOfItemsChecked = nrChecks
        setNrOfFindings(nrFindings, singleCheckResults)
        pageResults.addResultsForSingleCheck(singleCheckResults)

        then:
        pageResults.howManyCheckersHaveRun() == 1
        pageResults.totalNrOfFindings() == nrFindings
        pageResults.totalNrOfItemsChecked() == nrChecks


        where:
        nrChecks | nrFindings
        0        | 0
        0        | 1
        1        | 1
        2        | 1
        10       | 2

    }
    // helper method
    def void setNrOfFindings(int nrOfFindings, SingleCheckResults scr) {
        Finding finding = new Finding("a finding")

        for (int i = 0; i < nrOfFindings; i++) {
            scr.addFinding(finding)
        }
    }
}
