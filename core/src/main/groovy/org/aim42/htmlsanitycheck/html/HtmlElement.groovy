package org.aim42.htmlsanitycheck.html

import org.jsoup.nodes.Element

// see end-of-file for license information

/**
 * Encapsulates a single HTML element with attributes
 * Relies on jsoup.select.Element
 */
class HtmlElement {

    private Element element

    public HtmlElement(Element element) {
        this.element = element
    }

    /**
     * @return XYZ for img src="XYZ" tags
     */
    public String getImageSrcAttribute() {
        if (element.tagName().equals("img"))
            element.attr("src")
        else return ""
    }

    /**
     * @return XYZ for <img src="..." alt="XYZ">
     */
    public String getImageAltAttribute() {
        if (element.tagName().equals("img"))
            element.attr("alt")
        else return ""
    }

    /**
     * @return XYZ for 'a href="XYZ"' tags
     */
    public String getHrefAttribute() {
        if (element.tagName().equals("a")) {
            return element.attr("href")
        } else return ""
    }

    /**
     * @return XYZ for 'id="XYZ"' attributes
     */
    public String getIdAttribute() {
        return element.attr("id")

    }

    /**
     * @return x for '<img src="y" usemap="x">
     */
    public String getUsemapRef() {
        String tmpUsemapRef = ""

        if (element.tagName().equals("img")) {
             tmpUsemapRef = HtmlElement.normalizeHrefString( element.attr("usemap") )
        }
        return tmpUsemapRef
    }

    @Override
    public String toString() {
        return element.toString()
    }

    /*
     convert href to string
    */

    private static String normalizeHrefString(String href) {
        String normalizedHref

        // local href, starting with "#" (e.g. #appendix or #_appendix
        if (href.startsWith("#")) {
            normalizedHref = href[1..-1] // cut off first letter
        }
        // empty href might be treated differently one day...
        else if (href == "") {
            normalizedHref = ""
        } else normalizedHref = href

        return normalizedHref
    }

}

/*========================================================================
 Copyright 2014 Gernot Starke and aim42 contributors

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an
 "AS IS" BASIS,WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 ========================================================================*/
