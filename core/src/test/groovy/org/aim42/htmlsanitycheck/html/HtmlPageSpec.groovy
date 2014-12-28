package org.aim42.htmlsanitycheck.html

import spock.lang.Specification

class HtmlPageSpec extends Specification {

    private HtmlPage htmlPage
    private ArrayList qualifiedImageTags

    private ArrayList imageMaps

    // find all imageMaps within htmlPage
    def "find all ImageMaps within htmlPage"(int nrOfIMaps, String imageMaps) {
        when:
        String html = HtmlConst.HTML_HEAD + imageMaps + HtmlConst.HTML_END

        htmlPage = new HtmlPage( html )
        imageMaps = htmlPage.getAllImageMaps()

        then:
        imageMaps.size() == nrOfIMaps

        where:

        nrOfIMaps | imageMaps
        0    | """<a href="#test">test</a>"""

        // ===================================================

        1    | """<img src="image.gif" usemap="#mymap">
<map name="mymap">
    <area shape="rect" coords="0,0,1,1" href="#test1" >
    <area shape="circle" coords="0,1,1" href="#test2">
</map> """

        // ===================================================

        2    | """<img src="image.jpg" usemap="#yourmap>
<map name="yourmap">
    <area shape="rect" coords="0,0,1,1" href="#test1" >
    <area shape="circle" coords="0,1,1" href="#test2">
</map>
<img src="image.jpg" usemap="#mymap>
<map name="mymap">
    <area shape="rect" coords="0,0,1,1" href="#test1" >
</map>
"""
    }




    def "get image tags with non-empty alt attributes"(int nrOfAltAttributes, String imageTags) {
        when:
        String html = HtmlConst.HTML_HEAD + imageTags + HtmlConst.HTML_END

        htmlPage = new HtmlPage(html)
        qualifiedImageTags = htmlPage.getAllImageTagsWithNonEmptyAltAttribute()

        then:
        qualifiedImageTags.size() == nrOfAltAttributes

        where:

        nrOfAltAttributes | imageTags
        0                 | """<img src="a.jpg"> """
        0                 | """<img src="a.jpg" alt=""> """   // pathological case: empty alt attribute
        0                 | """<img src="a.jpg" alt="" >  <img src="b.png" alt> """

        1                 | """<img src="a.jpg" alt="a" > """
        2                 | """<img src="a.jpg" alt="a" >  <img src="b.png" alt="b"> """
        2                 | """<img src="a.jpg" alt="a-a aa a" >  <img src="b.png" alt="22"> """

        3                 | """ <img alt="1" >
                                <img src="" alt="2">
                                <img src="t.doc" alt="r"> """

    }

    /*
    here: missing alt-attribute either
    - alt-attribute not present (e.g. <img src="a.jpg"> or
    - alt-attribute empty
     */

    def "get image tags with missing alt attributes"(int missingAltAttrs, String imageTags) {
        when:
        String html = HtmlConst.HTML_HEAD + imageTags + HtmlConst.HTML_END

        htmlPage = new HtmlPage(html)
        qualifiedImageTags = htmlPage.getAllImageTagsWithMissingAltAttribute()

        then:
        qualifiedImageTags.size() == missingAltAttrs

        where:

        missingAltAttrs | imageTags
        1               | """<img src="a.jpg"> """
        1               | """<img src="a.jpg" alt=""> """   // pathological case: empty alt attribute
        2               | """<img src="a.jpg" alt="" >  <img src="b.png" alt> """

        0               | """<img src="a.jpg" alt="a" > """
        0               | """<img src="a.jpg" alt="a" >  <img src="b.png" alt="b"> """
        0               | """<img src="a.jpg" alt="a-a aa a" >  <img src="b.png" alt="22"> """

        0               | """ <img alt="1" >
                                <img src="" alt="2">
                                <img src="t.doc" alt="r"> """

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
