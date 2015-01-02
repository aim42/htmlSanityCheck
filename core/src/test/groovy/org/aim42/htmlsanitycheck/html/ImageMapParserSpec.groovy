package org.aim42.htmlsanitycheck.html

import org.jsoup.select.Elements
import spock.lang.Specification

/**
 * specifies ImageMap Parsing - prerequisites for the ImageMapChecker
 *
 * (correct) imageMaps in HTML files need to fulfill the following conditions:
 * 1.) every map is referenced by at least one img
 * 2.) is every map-name referenced in an image present in the page?
 * 3.) is every map-name unique?
 *    for 1-3  we need the following methods for that:
 *     * getAllImageMaps
 *     * getAllImagesWithUsemapReferences
 *
 * 4.) is at least one area-tag defined for every map?
 *     * getAllAreasForMap
 *
 * 5.) has every area-tag one non-empty href attribute?
 *
 */
class ImageMapParserSpec extends Specification {

    private HtmlPage htmlPage



    // find all imageMaps within htmlPage
    def "find all maps within page"(int nrOfIMaps, String imageMapString) {
        ArrayList<HtmlElement> imageMaps = new ArrayList()

        when:
        String html = HtmlConst.HTML_HEAD + imageMapString + HtmlConst.HTML_END

        htmlPage = new HtmlPage(html)
        imageMaps = htmlPage.getAllImageMaps()

        then:
        imageMaps.size() == nrOfIMaps

        where:

        nrOfIMaps | imageMapString
        0 | """<smap href="no"></smap>"""
        0 | """<maps><mad></mad></maps>"""
        0 | """<a href="#test">test</a>"""
        0 | ONE_IMAGE_NO_MAP
        1 | ONE_IMG_ONE_MAP_TWO_AREAS
        2 | TWO_IMAGE_TWO_MAPS
        3 | FOUR_IMAGES_THREE_MAPS

    }


    // find all img-tags with usemap-reference
    //@Unroll
    def "find all image tags with usemap declaration"(int nrOfImgs, String htmlBody ) {
        ArrayList<HtmlElement> imageTagsWithUsemap

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
        4        | FOUR_IMAGES_THREE_MAPS
        0        | """<img src="image.jpg" alt="test">"""
    }


    // find the area-tags within a named imageMap
    def "find all areas within map"(int nrOfAreas, String mapName, String htmlBody) {
        ArrayList<HtmlElement> areasInMap

        when:
        String html = HtmlConst.HTML_HEAD + htmlBody + HtmlConst.HTML_END

        htmlPage = new HtmlPage(html)
        areasInMap = htmlPage.getAllAreasForMapName(mapName)

        then:
        nrOfAreas == areasInMap.size()

        where:

        nrOfAreas | mapName | htmlBody

        // 2 areas in one imageMap
        2 | "mymap" | ONE_IMG_ONE_MAP_TWO_AREAS

        // 1 area in named map, 2 in other
        1 | "mymap" | ONE_IMG_ONE_MAP_ONE_AREA


    }


    // find the area-tags within a named imageMap
    def "find all hrefs within map"(int nrOfHrefs, String mapName, String htmlBody, ArrayList<String> hrefs) {
        ArrayList<String> hrefsInMap

        when:
        String html = HtmlConst.HTML_HEAD + htmlBody + HtmlConst.HTML_END

        htmlPage = new HtmlPage(html)
        hrefsInMap = htmlPage.getAllHrefsForMapName( mapName )

        then:
        // size matters
        nrOfHrefs == hrefsInMap.size()



        where:

        nrOfHrefs | mapName | htmlBody  | hrefs

        // 2 areas in one imageMap               |
        2 | "mymap" | ONE_IMG_ONE_MAP_TWO_AREAS  | ["#test1", "#test2"]

        // 1 area in named map, 2 in other
        1 | "mymap" | ONE_IMG_ONE_MAP_ONE_AREA   | ["#test1"]

    }

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
    <area shape="rect" coords="1,1,1,1" href="#test3" >
</map>
"""

    private static final String ONE_IMAGE_NO_MAP =
            """<img src="image.jpg" usemap="#yourmap">  """



    private static final String FOUR_IMAGES_THREE_MAPS =
            """<img src="image1.jpg" usemap="#map1">
<map name="map1">
    <area shape="rect" coords="0,0,1,1" href="#test1">
    <area shape="circle" coords="0,1,1" href="#test2">
</map>
<img src="image2.jpg" usemap="#map2">
<map name="map2">
    <area shape="rect" coords="0,0,1,1" href="#test1">
</map>
<img src="image3.jpg" usemap="#map3">
<map name="map3">
    <area shape="rect" coords="0,0,1,1" href="#test1">
    <area shape="rect" coords="1,1,1,1" href="#test2">
</map>
<img src="image4.jpg" usemap="#map4">
"""


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
