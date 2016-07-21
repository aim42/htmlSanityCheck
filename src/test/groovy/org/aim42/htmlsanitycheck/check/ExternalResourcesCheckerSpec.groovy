package org.aim42.htmlsanitycheck.check

import spock.lang.Specification

class ExternalResourcesCheckerSpec extends Specification {

    HttpURLConnection mockURL

    def setup() {
        mockURL= Mock( sun.net.www.protocol.http.HttpURLConnection )
    }


    def "one spec to rule them all"() {
        expect:
        true
    }


    def "if google is not available network must be down"() {
        given:
          mockURL.getResponseCode() >> 200

        expect:
            mockURL.getResponseCode() == 200

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

