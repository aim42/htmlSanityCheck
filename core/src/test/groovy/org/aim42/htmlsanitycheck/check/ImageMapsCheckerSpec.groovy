package org.aim42.htmlsanitycheck.check

import org.aim42.htmlsanitycheck.collect.SingleCheckResults
import org.aim42.htmlsanitycheck.html.HtmlConst
import org.aim42.htmlsanitycheck.html.HtmlPage
import spock.lang.Specification
import spock.lang.Subject
import spock.lang.Unroll


class ImageMapsCheckerSpec extends Specification {

    @Subject
    public Checker imageMapsChecker


    private HtmlPage htmlPage
    private SingleCheckResults collector

    /**
     * specify behavior of isThereOneMapForEveryUsemapReference()
     * @param html
     * @param nrOfChecks
     * @param nrOfFindings
     *
     */

    @Unroll
    def "is there one map for every usemap"(int nrOfChecks, int nrOfFindings, String imageMapStr  ) {

        given:
        String html = HtmlConst.HTML_HEAD + imageMapStr + HtmlConst.HTML_END
        htmlPage = new HtmlPage( html )


        when:
        imageMapsChecker = new ImageMapChecker( pageToCheck: htmlPage)
        collector = imageMapsChecker.performCheck()

        then:
        collector.nrOfProblems() == nrOfFindings
        collector.nrOfItemsChecked == nrOfChecks

        where:

        nrOfChecks | nrOfFindings | imageMapStr
        //0          | 0          | """<img src="x.jpg">"""
        2          | 0          | ONE_IMAGE_WITH_MAP
        //1          | 1          | ONE_IMAGE_NO_MAP
        //1          | 1          | ONE_IMAGE_TWO_MAPS

    }



    // bad: too many maps for on
    private static final String ONE_IMAGE_TWO_MAPS =
            """<img src="image.jpg" usemap="#yourmap">
<map name="yourmap">
    <area shape="rect" coords="0,0,1,1" href="#test1" >
    <area shape="circle" coords="0,1,1" href="#test2">
</map>
<map name="yourmap">
    <area shape="rect" coords="1,1,1,1" href="#test3" >
</map>
"""

    private static final String ONE_IMAGE_NO_MAP = """<img src="image.jpg" usemap="#yourmap"> """
    private static final String ONE_IMAGE_WITH_MAP =
           ONE_IMAGE_NO_MAP +
"""<map name="yourmap">
    <area shape="rect" coords="0,0,1,1" href="#test1" >
    <area shape="circle" coords="0,1,1" href="#test2">
</map>"""




}

/************************************************************************
 * This is free software - without ANY guarantee!
 *
 *
 * Copyright 2015, Dr. Gernot Starke, arc42.org
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
