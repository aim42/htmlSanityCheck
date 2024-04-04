package org.aim42.htmlsanitycheck.report

import org.aim42.htmlsanitycheck.collect.PerRunResults
import org.aim42.htmlsanitycheck.collect.SingleCheckResults
import org.aim42.htmlsanitycheck.collect.SinglePageResults
import org.junit.Before
import org.junit.Test

import static org.junit.Assert.assertEquals

// see end-of-file for license information


class ReporterTest {

    private Reporter reporter
    private PerRunResults runResults

    @Before
    void setUp() {
        runResults = new PerRunResults()
        reporter = new Reporter(runResults) {
            @Override
            protected void reportOverallSummary() {
            }

            @Override
            protected void reportPageSummary(SinglePageResults pageResult) {
            }

            @Override
            protected void reportPageFooter() {
            }

            @Override
            protected void reportSingleCheckSummary(SingleCheckResults singleCheckResults) {
            }

            @Override
            protected void reportSingleCheckDetails(SingleCheckResults singleCheckResults) {
            }
        }

    }

    @Test
    public void testNothingReportedWithEmptyResults() {
        SinglePageResults spr = new SinglePageResults()
        runResults.addPageResults(spr)

        reporter.reportFindings()

        assertEquals("Empty ConsoleReporter has no check", 0, reporter.totalNrOfChecks())
        assertEquals("Empty Reporter shall have no findings", 0, reporter.totalNrOfFindings())
    }
}

/*======================================================================

Copyright Gernot Starke and aim42 contributors

 Licensed under the Apache License, Version 2.0 (the "License")
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
