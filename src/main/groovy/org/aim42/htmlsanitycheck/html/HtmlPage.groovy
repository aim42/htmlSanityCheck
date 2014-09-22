package org.aim42.htmlsanitycheck.html

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.select.Elements

// see end-of-file for license information

/**
 * Encapsulates a "real" html parser and provides
 * convenience methods to access anchor and image links
 * from html.
 *
 * Relies on http://jsoup.org parser
 */
class HtmlPage {

    // jsoup Document
    private Document document

    /**
     *
     * @param text html as text (string)
     * @return an HtmlPage
     */
    public HtmlPage(String text) {
        // Jsoup promises to parse without exception -
        // we believe it, as our wrapper is for checking
        // purposes only
        document = Jsoup.parse(text, "UTF-8")
    }


    public HtmlPage(File file) {
        assert file.exists()
        document = Jsoup.parse(file, "UTF-8")
    }

    /**
     * builds a list from all '<img src="XYZ"/>' tags
     * @return immutable ArrayList
     */
    public final ArrayList<HtmlElement> getAllImageTags() {
        Elements elements = document?.getElementsByTag("img")

        return toHtmlElementsCollection( elements )

        // alternative: document?.getElementsByTag("img").asList()
    }

    /**
     * builds a list of all '<a href="XYZ"> tags
     * @return ArrayList of all hrefs, including the "#"
     */
    public final ArrayList<HtmlElement> getAllAnchorHrefs() {
        Elements elements = document.select("a[href]")

        return toHtmlElementsCollection( elements )
    }

    /**
     * builds a list of all 'id="XYZ"' attributes
     * @return ArrayList of all hrefs
     */
    public final ArrayList<HtmlElement> getAllIds() {
        Elements elements = document.getElementsByAttribute( "id")

        return toHtmlElementsCollection( elements )
    }



    /**
     *
     * @return ArrayList<String> of all href-attributes
     *
     * common pitfalls with hrefs:
     * - local hrefs start with # (like "#appendix")
     * - remote hrefs should be valid URLs (like "https://google.com")
     * - remote hrefs might start with other than http (e.g. https, mailto, telnet, ssh)
     * - hrefs might start with file://
     * - href might be empty string (nobody knows wtf this is good for, but html parsers usually accept it)
     */
    public final ArrayList<String> getAllHrefStrings( ) {
        Elements elements = document.select("a[href]")

        ArrayList<String> hrefStrings = new ArrayList<>()

        elements.each { element ->
            String href = element.attr("href")

            hrefStrings.add( normalizeHrefString( href ))

            // the following line was caused http and https links to treated as local links...
            // hrefStrings.add(href[1..href.length()-1])
        }

        return hrefStrings
    }

    /*
     convert href to string
     */
    private String normalizeHrefString( String href ) {
        String normalizedHref

        // local href, starting with "#" (e.g. #appendix or #_appendix
        if (href.startsWith("#")) {
           normalizedHref =  href[1..-1] // cut off first letter
        }
        // empty href might be treated differently one day...
        else if (href=="") {
            normalizedHref = ""
        }
        else normalizedHref = href

        return normalizedHref
    }

    /**
     * getAllIdStrings return ArrayList<String> of all id="xyz" definitions
     */

    public final ArrayList<String> getAllIdStrings() {
        Elements elements = document.getElementsByAttribute( "id")

        ArrayList<String> idList = new ArrayList<>()

        elements.each { element ->
            idList.add( element.attr("id"))
        }

        return idList
    }

    /**
     * convert JSoup Elements to ArrayList<HtmlElement>
     */
    private final ArrayList<HtmlElement> toHtmlElementsCollection( Elements elements ) {

        ArrayList<HtmlElement> arrayList = new ArrayList<>()

        elements.each { element ->
            arrayList.add(new HtmlElement(element))
        }

        return arrayList

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

