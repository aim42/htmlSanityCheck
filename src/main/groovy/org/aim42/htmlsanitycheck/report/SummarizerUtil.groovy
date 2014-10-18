package org.aim42.htmlsanitycheck.report

class SummarizerUtil {

    /**
     * returns the percentage of successful checks.
     *
     * Edge case:
     * 0 checks -> 100% successful
     *
     */
    public static int percentSuccessful(int totalChecks, int totalNrOfFindings) {


        // base case: if no checks performed, 100% successful
        if (totalChecks <= 0) {
            return 100
        }
        // at least one check was performed, calculate percentage
        else {
            return 100 - (100 * totalNrOfFindings) / totalChecks
        }
    }

    /**
     * rounds one down to at most 3 digits with two decimalplaces,
     * e.g. from
     * 33450 to 33.45, from 1_234_566 to 1.23
     */
    public static float threeDigitTwoDecimalPlaces( int bigNumber ) {


        if (bigNumber >= 1_000_000)
            return Math.round( bigNumber.div(10_000)).div(100)
        else return Math.round( bigNumber.div(10)).div(100)

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
