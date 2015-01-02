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

    /**
     * @param file
     * @return an HtmlPage
     */
    public HtmlPage(File file) {
        assert file.exists()
        document = Jsoup.parse(file, "UTF-8")
    }

    /**
     * get document meta info (e.g. filename, title, size etc.)
     */
    public int getDocumentSize() {
        return document.toString().length()
    }

    public String getDocumentTitle() {
        return document.title()
    }

    public String getDocumentURL() {
        return document.nodeName()
    }

    public String getDocument() {
        return document.toString()
    }

    /**
     * builds a list of all imageMaps
     * @return ArrayList of imageMaps
     */
    public final ArrayList<HtmlElement> getAllImageMaps() {
        Elements elements = document?.select("map")
        return toHtmlElementsCollection(elements)
    }

    /**
     * builds a list from all '<img src="XYZ"/>' tags
     * @return immutable ArrayList
     */
    public final ArrayList<HtmlElement> getAllImageTags() {
        Elements elements = document?.getElementsByTag("img")

        return toHtmlElementsCollection(elements)

        // alternative: document?.getElementsByTag("img").asList()
    }

    /**
     * builds an immutable list of '<img src="xxx" alt="yz">,
     * where "yz" is non-empty.
     */
    public final ArrayList<HtmlElement> getAllImageTagsWithNonEmptyAltAttribute() {
        // regex "\S" matches any word
        Elements elements = document?.select("img[alt~=(\\S)]")

        return toHtmlElementsCollection(elements)
    }

    /**
     * builds an immutable list of <img...> tags, where
     * the alt-tag is missing or empty ("").
     */
    public final ArrayList<HtmlElement> getAllImageTagsWithMissingAltAttribute() {
        Elements elements = document?.select("img") - document?.select("img[alt~=(\\S)]")

        return toHtmlElementsCollection(elements)
    }

    /**
     * builds a list of all '<a href="XYZ"> tags
     * @return ArrayList of all hrefs, including the "#"
     */
    public final ArrayList<HtmlElement> getAllAnchorHrefs() {
        Elements elements = document.select("a[href]")

        return toHtmlElementsCollection(elements)
    }

    /**
     * builds a list of all 'id="XYZ"' attributes
     * @return ArrayList of all hrefs
     */
    public final ArrayList<HtmlElement> getAllIds() {
        Elements elements = document.getElementsByAttribute("id")

        return toHtmlElementsCollection(elements)
    }

    /**
     *
     * @return ArrayList < String >  of all href-attributes
     *
     * common pitfalls with hrefs:
     * - local hrefs start with # (like "#appendix")
     * - remote hrefs should be valid URLs (like "https://google.com")
     * - remote hrefs might start with other than http (e.g. https, mailto, telnet, ssh)
     * - hrefs might start with file://
     * - href might be empty string (nobody knows wtf this is good for, but html parsers usually accept it)
     */
    public final ArrayList<String> getAllHrefStrings() {
        Elements elements = document.select("a[href]")

        ArrayList<String> hrefStrings = new ArrayList<>()

        elements.each { element ->
            String href = element.attr("href")

            //hrefStrings.add( normalizeHrefString( href ))
            hrefStrings.add(href)
        }

        return hrefStrings
    }

    /**
     * @return immutable List of img-tags with "usemap=xyz" declaration
     */
    public final ArrayList<HtmlElement> getImagesWithUsemapDeclaration() {
        Elements elements = document?.select("img[usemap]")

        return toHtmlElementsCollection(elements)
    }

    /**
     * html-map has the following form:
     * <map name="mapName"><area...><area...></map>
     *
     * collect all area elements for a given map.
     * If more than one map exists with this name, areas
     * for all maps are combined into one.
     * @param mapName name of the map
     * @return
     */
    ArrayList<HtmlElement> getAllAreasForMapName(String mapName) {
        // get all maps with name==mapName
        Elements mapsWithName = document?.select("map[name=${mapName}]")

        ArrayList<HtmlElement> areas = new ArrayList()

        mapsWithName.each { map ->
            areas += map.children().select("area")
        }
        return areas
    }


    public final ArrayList<String> getAllHrefsForMapName( String mapName ) {
        ArrayList<String> hrefs = new ArrayList()

        ArrayList<HtmlElement> areas = getAllAreasForMapName( mapName )

        areas?.each{ area ->
            hrefs += area.attr("href")
        }

        return hrefs
    }

/**
 * getAllIdStrings return ArrayList<String> of all id="xyz" definitions
 */

    public final ArrayList<String> getAllIdStrings() {
        Elements elements = document.getElementsByAttribute("id")

        ArrayList<String> idList = new ArrayList<>()

        elements.each { element ->
            idList.add(element.attr("id"))
        }

        return idList
    }

/**
 * convert JSoup Elements to ArrayList<HtmlElement>
 */
    private final ArrayList<HtmlElement> toHtmlElementsCollection(Elements elements) {

        ArrayList<HtmlElement> arrayList = new ArrayList<>()

        elements.each { element ->
            arrayList.add(new HtmlElement(element))
        }

        return arrayList
    }

    /*
      convert href to string
     */

    private String normalizeHrefString(String href) {
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

