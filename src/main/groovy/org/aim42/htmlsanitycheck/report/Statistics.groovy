package org.aim42.htmlsanitycheck.report

class Statistics {

    final int totalNrOfChecksPerformed
    final int totalNrOfFindings
    final int percentSuccessful


    public Statistics( int nrOfChecksPerformed, int nrOfFindings) {
        this.totalNrOfChecksPerformed = nrOfChecksPerformed
        this.totalNrOfFindings = nrOfFindings
        this.percentSuccessful = calculateSummary()
    }


    private int calculateSummary() {
        // base case: if no checks performed, 100% successful
        if (totalNrOfChecksPerformed <= 0) {
            return 100
        }
        // at least one check was performed, calculate percentage
        else {
            return 100 - (100 * totalNrOfFindings) / totalNrOfChecksPerformed
        }
    }
}
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


