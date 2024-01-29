package org.aim42.htmlsanitycheck.html;

import org.aim42.htmlsanitycheck.tools.Web;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


// see end-of-file for license information

/**
 * Encapsulates a "real" html parser and provides
 * convenience methods to access anchor and image links
 * from html.
 * <p>
 * Relies on http://jsoup.org parser
 */
public class HtmlPage {

    /**
     * Pattern to check for HTTP/S scheme, includes the
     * scheme separator (colon).
     */
    private static final Pattern HTTP_SCHEME_PATTERN = Pattern.compile("(?i)^https?:");

    // jsoup Document
    private final Document document;

    /**
     * The HTML file.
     */
    private File file;

    /**
     * @param text html as text (string)
     * @return an HtmlPage
     */
    public HtmlPage(String text) {
        // Jsoup promises to parse without exception -
        // we believe it, as our wrapper is for checking
        // purposes only
        document = Jsoup.parse(text, "UTF-8");
    }

    /**
     * @param file
     * @return an HtmlPage
     */
    public HtmlPage(File file) throws IOException {
        assert file.exists();
        this.file = file;
        document = Jsoup.parse(file, "UTF-8");
    }

    /**
     * invokes the parser for the html page
     *
     * @param input file
     */
    public static HtmlPage parseHtml(File fileToCheck) throws IOException {
        assert fileToCheck.exists();
        return new HtmlPage(fileToCheck);
    }

    /**
     * Gets the file of the HTML page.
     *
     * @return the file, or null if the HTML is not from a file.
     */
    public File getFile() {
        return file;
    }

    /**
     * get document meta info (e.g. filename, title, size etc.)
     */
    public int getDocumentSize() {
        return document.toString().length();
    }

    public String getDocumentTitle() {
        return document.title();
    }

    public String getDocumentURL() {
        return document.nodeName();
    }

    public String getDocument() {
        return document.toString();
    }

    /**
     * builds a list of all imageMaps
     *
     * @return ArrayList of imageMaps
     */
    public final List<HtmlElement> getAllImageMaps() {
        return document.select("map").stream()
                .map(HtmlElement::new)
                .collect(Collectors.toList());
    }

    /**
     * @return list of all imageMap-names
     */
    public final List<String> getAllMapNames() {
        return document.select("map").stream()
                .map(m -> m.attr("name"))
                .collect(Collectors.toList());
    }

    /**
     * @return list of all usemap-references y with <img src="x" usemap="y"
     */
    public List<String> getAllUsemapRefs() {
        return getImagesWithUsemapDeclaration().stream()
                .map(HtmlElement::getUsemapRef)
                .collect(Collectors.toList());
    }

    /**
     * builds a list from all '<img src="XYZ"/>' tags
     *
     * @return immutable ArrayList
     */
    public final List<HtmlElement> getAllImageTags() {
        return document.getElementsByTag("img").stream()
                .map(HtmlElement::new)
                .collect(Collectors.toList());
    }

    /**
     * builds an immutable list of '<img src="xxx" alt="yz">,
     * where "yz" is non-empty.
     */
    public final List<HtmlElement> getAllImageTagsWithNonEmptyAltAttribute() {
        return document.select("img[alt~=(\\S)]").stream()
                .map(HtmlElement::new)
                .collect(Collectors.toList());
    }

    /**
     * builds an immutable list of <img...> tags, where
     * the alt-tag is missing or empty ("").
     */
    public final List<HtmlElement> getAllImageTagsWithMissingAltAttribute() {
        return document.select("img[alt~=(\\S)]").stream()
                .map(HtmlElement::new)
                .collect(Collectors.toList());
    }

    /**
     * builds a list of all '<a href="XYZ"> tags
     *
     * @return ArrayList of all hrefs, including the "#"
     */
    public List<HtmlElement> getAllAnchorHrefs() {
        return document.getElementsByAttribute("href").stream()
                .map(HtmlElement::new)
                .collect(Collectors.toList());
    }

    /**
     * builds a list of all 'id="XYZ"' attributes
     *
     * @return ArrayList of all hrefs
     */
    public final List<HtmlElement> getAllIds() {
        return document.getElementsByAttribute("id").stream()
                .map(HtmlElement::new)
                .collect(Collectors.toList());
    }

    /**
     * @return ArrayList < String >  of all href-attributes
     * <p>
     * common pitfalls with hrefs:
     * - local hrefs start with # (like "#appendix")
     * - remote hrefs should be valid URLs (like "https://google.com")
     * - remote hrefs might start with other than http (e.g. https, mailto, telnet, ssh)
     * - hrefs might start with file://
     * - href might be empty string (nobody knows wtf this is good for, but html parsers usually accept it)
     */
    public final List<String> getAllHrefStrings() {
        return document.select("a[href]").stream()
                .map(m -> m.attr("href"))
                .collect(Collectors.toList());
    }

    /**
     * @return immutable set of all href-attributes that start with http or https
     */
    public final Set<String> getAllHttpHrefStringsAsSet() {
        return document.select("a[href]")
                .stream()
                .filter(e -> e.hasAttr("href"))
                .map(e -> e.attr("href"))
                .filter(Web::isWebUrl)
                .collect(Collectors.toSet());
    }

    /**
     * @return immutable List of img-tags with "usemap=xyz" declaration
     */
    public List<HtmlElement> getImagesWithUsemapDeclaration() {
        return document.select("img[usemap]")
                .stream().map(HtmlElement::new)
                .collect(Collectors.toList());
    }

    /**
     * html-map has the following form:
     * <map name="mapName"><area...><area...></map>
     * <p>
     * collect all area elements for a given map.
     * If more than one map exists with this name, areas
     * for all maps are combined into one.
     *
     * @param mapName name of the map
     * @return
     */
    public List<Elements> getAllAreasForMapName(String mapName) {
        return document.select("map[name=" + mapName + "]").stream()
                .map(m -> m.children().select("area"))
                .collect(Collectors.toList());

    }


    public List<String> getAllHrefsForMapName(String mapName) {
        return getAllAreasForMapName(mapName).stream()
                .map(a -> a.attr("href"))
                .collect(Collectors.toList());
    }

    /**
     * getAllIdStrings return ArrayList<String> of all id="xyz" definitions
     */

    public List<String> getAllIdStrings() {
        return document.getAllElements().stream()
                .map(Element::id)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
    }


}
/*========================================================================
 Copyright Gernot Starke and aim42 contributors

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

