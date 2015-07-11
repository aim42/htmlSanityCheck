package org.aim42.htmlsanitycheck.check

import org.aim42.htmlsanitycheck.collect.SingleCheckResults
import org.aim42.htmlsanitycheck.html.HtmlConst
import org.aim42.htmlsanitycheck.html.HtmlPage
import spock.lang.Specification
import spock.lang.Subject


class MissingAltInImageTagsCheckerSpec extends Specification {

    @Subject
    public Checker missingAltInImageTagsChecker


    private HtmlPage htmlPage
    private SingleCheckResults collector

    /**
     * data-driven test to specify behavior of MissingAltInImageTagsChecker
     * @param imageTags
     * @param nrOfFindings
     */
    def "missingAltAttributeLeadsToFinding"(int nrOfFindings, int nrOfChecks, String imageTags) {

        given:
        String html = HtmlConst.HTML_HEAD + imageTags + HtmlConst.HTML_END
        htmlPage = new HtmlPage( html )


        when:
        missingAltInImageTagsChecker = new MissingAltInImageTagsChecker( )
        collector = missingAltInImageTagsChecker.performCheck( htmlPage )

        then:
        collector.nrOfProblems() == nrOfFindings
        collector.nrOfItemsChecked == nrOfChecks


        where:

        nrOfFindings | nrOfChecks | imageTags
        0            | 1          | """<img src="t.png" alt="t">"""
        1            | 1          | """<img src="t.png">"""
        2            | 2          | """<img src="a.png"> <img src= "b.jpg" >"""

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
