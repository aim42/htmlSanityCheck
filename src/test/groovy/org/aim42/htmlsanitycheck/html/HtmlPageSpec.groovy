package org.aim42.htmlsanitycheck.html

import spock.lang.Specification

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

class HtmlPageSpec extends Specification {

    private final String HTML_HEAD = "<!DOCTYPE HTML> <html><head></head><body>"
    private final String HTML_END  = "</body></html>"

    private HtmlPage htmlPage
    private ArrayList qualifiedImageTags

    def "get image tags with non-empty alt attributes"(int nrOfAltAttributes, String imageTags )
    {

        when:
        String html = HTML_HEAD + imageTags + HTML_END

        htmlPage = new HtmlPage( html )
        qualifiedImageTags = htmlPage.getAllImageTagsWithNonEmptyAltAttribute()

        then:
            qualifiedImageTags.size() == nrOfAltAttributes

        where:

        nrOfAltAttributes | imageTags
                       0  | """<img src="a.jpg">"""
                       0  | """<img src="a.jpg" alt="" >"""   // pathological case: empty alt attribute
                       1  | """<img src="a.jpg" alt="a" >"""
                       2  | """<img src="a.jpg" alt="a" >  <img src="b.png" alt="b" """



        }

}
