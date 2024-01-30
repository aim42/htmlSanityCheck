package org.aim42.htmlsanitycheck.check


import org.aim42.htmlsanitycheck.collect.SingleCheckResults
import org.aim42.htmlsanitycheck.html.HtmlConst
import org.aim42.htmlsanitycheck.html.HtmlPage
import spock.lang.Specification
import spock.lang.Unroll

class ImageMapsCheckerSpec extends Specification {

    public Checker imageMapsChecker

    private HtmlPage htmlPage
    private SingleCheckResults collector


    @Unroll
    def "find image map issues"(int nrOfFindings, String imageMapStr, String msg) {

        given:
        String html = HtmlConst.HTML_HEAD + imageMapStr + HtmlConst.HTML_END
        htmlPage = new HtmlPage(html)

        when:
        imageMapsChecker = new ImageMapChecker()
        collector = imageMapsChecker.performCheck(htmlPage)

        then:
        collector?.getFindingMessages()?.contains(msg)
        collector?.nrOfProblems() == nrOfFindings


        where:

        nrOfFindings | imageMapStr              | msg
        // no imagemap, no check, no problem
        //        0            | """<img src="x.jpg">"""  | ""

        // a correct imagemap
        //        0            | IMG1 + MAP1 + ID1        | ""

        // image with usemap-ref but no map!
        1            | IMG1                     | """ImageMap "map1" (referenced by image "image1.jpg") missing."""

        // one image but TWO maps with identical name
        1            | IMG1 + MAP1 + MAP1 + ID1 | """2 imagemaps with identical name "map1" exist."""

        // no image, dangling map
        1            | MAP1 + ID1               | """ImageMap "map1" not referenced by any image."""

        // empty map
        1            | IMG1 + MAP1_EMPTY        | "ImageMap \"map1\" has no area tags."

        // href link target missing
        1            | IMG1 + MAP1 + ID2        | "ImageMap \"map1\" refers to missing link \"id1\"."
    }


    private static final String ID1 = """<h2 id="id1">aim42 header</h2>"""
    private static final String ID2 = """<h2 id="foo" >bad header</h2>"""

    private static final String IMG1 = """<img src="image1.jpg" usemap="#map1">"""
    private static final String IMG2 = """<img src="image2.jpg" usemap="#map2">"""

    private static final String MAP1 =
            """<map name="map1">
    <area shape="rect" coords="0,0,1,1" href="#id1" >
</map>
"""
    private static final String MAP1_EMPTY =
            """<map name="map1">
</map>
"""


    private static final String TWO_IMAGES_ONE_MAP =
            """<img src="image1.jpg" usemap="#map1">
   <map name="map1">
     <area shape="rect" coords="0,0,1,1" href="#test1" >
     <area shape="circle" coords="0,1,1" href="#test2">
</map>
"""

    private static final String TWO_IMAGES_NO_MAP =
            """<img src="image1.jpg" usemap="#map1">
   <img src="image2.jpg" usemap="#map2">
"""


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
