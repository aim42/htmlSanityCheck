package org.aim42.htmlsanitycheck.html

import org.jsoup.select.Elements
import spock.lang.Specification

/**
 * specifies ImageMap Checking
 *
 * (correct) imageMaps in HTML files need to fulfill the following conditions:
 * 1.) every map is referenced by at least one img
 *     we need the following methods for that:
 *     * getAllImageMaps
 *     * getAllImagesWithUsemapReferences
 *
 */
class ImageMapParserSpec extends Specification {

    private HtmlPage htmlPage

    private ArrayList<HtmlElement> imageMaps
    private ArrayList<HtmlElement> imageTagsWithUsemap


    private static final String ONE_IMG_ONE_MAP_TWO_AREAS =
            """<img src="image.gif" usemap="#mymap">
<map name="mymap">
    <area shape="rect" coords="0,0,1,1" href="#test1" >
    <area shape="circle" coords="0,1,1" href="#test2">
</map> """

    private static final String ONE_IMG_ONE_MAP_ONE_AREA =
            """<img src="image.gif" usemap="#mymap">
<map name="mymap">
    <area shape="rect" coords="0,0,1,1" href="#test1" >
</map> """


    private static final String TWO_IMAGE_TWO_MAPS =
            """<img src="image.jpg" usemap="#yourmap">
<map name="yourmap">
    <area shape="rect" coords="0,0,1,1" href="#test1" >
    <area shape="circle" coords="0,1,1" href="#test2">
</map>
<img src="image.jpg" usemap="#mymap">
<map name="mymap">
    <area shape="rect" coords="0,0,1,1" href="#test1" >
</map>
"""

    private static final String ONE_IMAGE_NO_MAP =
            """<img src="image.jpg" usemap="#yourmap">  """

    // find all imageMaps within htmlPage
    def "find all ImageMaps within htmlPage"(int nrOfIMaps, String imageMapString) {
        ArrayList<HtmlElement> imageMaps = new ArrayList()

        when:
        String html = HtmlConst.HTML_HEAD + imageMapString + HtmlConst.HTML_END

        htmlPage = new HtmlPage(html)
        imageMaps = htmlPage.getAllImageMaps()

        then:
        imageMaps.size() == nrOfIMaps

        where:

        nrOfIMaps | imageMapString
        0 | """<a href="#test">test</a>"""
        0 | ONE_IMAGE_NO_MAP
        1 | ONE_IMG_ONE_MAP_TWO_AREAS
        2 | TWO_IMAGE_TWO_MAPS


    }

    // find all img-tags with usemap-reference
    //@Unroll
    def "find all image tags with usemap declaration"(String htmlBody, int nrOfImgs) {

        when:
        String html = HtmlConst.HTML_HEAD + htmlBody + HtmlConst.HTML_END
        htmlPage = new HtmlPage(html)
        imageTagsWithUsemap = htmlPage.getImagesWithUsemapDeclaration()

        then:
        nrOfImgs == imageTagsWithUsemap.size()

        where:
        nrOfImgs | htmlBody
        1        | ONE_IMAGE_NO_MAP
        1        | ONE_IMG_ONE_MAP_ONE_AREA
        2        | TWO_IMAGE_TWO_MAPS
        0        | """<img src="image.jpg" alt="test">"""
    }

    // find the area-tags within a named imageMap
    def "find all area tags within imageMap"() {
        ArrayList<HtmlElement> hrefsInMap
        when:
        String html = HtmlConst.HTML_HEAD + imageMapString + HtmlConst.HTML_END

        htmlPage = new HtmlPage(html)
        hrefsInMap = htmlPage.getAllAreasForMap(mapName)


        then:
        //hrefsInMap.size() == nrOfAreas
        false

        where:

        nrOfAreas | mapName | imageMapString

        // 2 areas in one imageMap
        2 | "mymap" | ONE_IMG_ONE_MAP_TWO_AREAS

        // 1 area in named map, 2 in other
        1 | "mymap" | ONE_IMG_ONE_MAP_ONE_AREA


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
