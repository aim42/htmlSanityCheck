package org.aim42.htmlsanitycheck.collect

import spock.lang.Specification

class FindingSpec extends Specification {

    Finding finding


    def "finding toString returns item"() {
        given:
        String error = "error"
        finding = new Finding(error)

        when:

        String result = finding.toString()

        then:
        finding.nrOfOccurrences == 1

        result == error

    }

    def "finding with multiple occurences returns refcount"() {
        given:
        String error = "error"
        finding = new Finding( error, 3)

        when:
        String result = finding.toString()

        then:
        finding.nrOfOccurrences == 3
        result == error + " (reference count: 3)"

    }
}

/************************************************************************
 * This is free software - without ANY guarantee!
 *
 *
 * Copyright Dr. Gernot Starke, arc42.org
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

