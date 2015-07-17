package org.aim42.htmlsanitycheck.report

import org.aim42.htmlsanitycheck.check.BrokenCrossReferencesChecker
import org.aim42.htmlsanitycheck.collect.PerRunResults
import org.aim42.htmlsanitycheck.html.HtmlPage
import spock.lang.Specification

/**
 * Specification to show integration of a SuggestingChecker (BrokenInternalLinksChecker)
 * with a Reporter: The Reporter is supposed to report suggestions, if those are possible.
 *
 * Idea:
 * 1.) create HTML page with mis-spelled internal link
 * 2.) run Checker on this page - shall determine suggestions
 * 3.) verify that Reporter reports accordingly
 *
 */
class SuggestingCheckerReportingIntegrationSpec extends Specification {

    def "broken internal link is found by Checker and reported"() {
        PerRunResults runResults

        given:
        // define buggy HTML
            String HTML_WITH_FINDING = """

        """

            // create page
            def htmlPage = new HtmlPage( HTML_WITH_FINDING )


        when:
        // run checker
        def brokenCrossRefChecker = new BrokenCrossReferencesChecker( )

        def collector = brokenCrossRefChecker.performCheck( htmlPage )

        then:
        // reporter shall report finding + suggestions
        1 == 1
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

