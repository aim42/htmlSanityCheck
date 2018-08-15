package org.aim42.htmlsanitycheck.html

import spock.lang.Specification
import spock.lang.Unroll

class HtmlPageSpec extends Specification {

    private HtmlPage htmlPage
    private ArrayList qualifiedImageTags


    @Unroll
    def "can extract alt attributes from imageTag '#imageTags'"() {
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

    @Unroll
    def "detect missing alt attributes in imageTag '#imageTag'"() {
        when:
        String html = HtmlConst.HTML_HEAD + imageTag + HtmlConst.HTML_END

        htmlPage = new HtmlPage(html)
        qualifiedImageTags = htmlPage.getAllImageTagsWithMissingAltAttribute()

        then:
        qualifiedImageTags.size() == missingAltAttrs

        where:

        missingAltAttrs | imageTag
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

    @Unroll
    def "detect correct number of external http links in anchors '#anchors' "() {
        ArrayList externalLinks

        when:
        String html = HtmlConst.HTML_HEAD + anchors + HtmlConst.HTML_END

        htmlPage = new HtmlPage(html)
        externalLinks = htmlPage.getAllHttpHrefStringsAsSet()

        then:
        externalLinks.size() == nrOfHttpLinks

        where:

        nrOfHttpLinks | anchors
        0             | """<img src="a.jpg"> """
        0             | """<a href="file://arc42.org">arc42</a>"""
        0             | """<a href="htpp://">bla</a> """

        1             | """<a href="http://arc42.org">arc42</a>"""
        1             | """<a href="http://arc42.org">http</a>"""

        4             | """<a href="http://arc42.org">arc42</a> and some text
                                <a href="http://arc42.de">arc42.de</a> and some more text
                                <a href="https://arc42.org">arc42 over https</a> even more
                                <a href="local-file.jpg">local file</a> again, text
                                <a href="http://aim.org">improve</a>"""

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
