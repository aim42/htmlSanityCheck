package org.aim42.htmlsanitycheck.html;

import org.jsoup.nodes.Element;

// see end-of-file for license information

/**
 * Encapsulates a single HTML element with attributes
 * Relies on jsoup.select.Element
 */
public class HtmlElement {

    private final Element element;

    HtmlElement(Element node) {
        this.element = node;
    }

    private static String normalizeHrefString(String href) {
        // remove leading #
        return href.startsWith("#") ? href.substring(1) : href;
    }

    /**
     * @return XYZ for img src="XYZ" tags
     */
    public String getImageSrcAttribute() {
        if (element.tagName().equals("img"))
            return element.attr("src");
        else return "";
    }

    /**
     * @return XYZ for <img src="..." alt="XYZ">
     */
    public String getImageAltAttribute() {
        if (element.tagName().equals("img"))
            return element.attr("alt");
        else return "";
    }

    /**
     * @return XYZ for 'a href="XYZ"' tags
     */
    public String getHrefAttribute() {
        if (element.tagName().equals("a")) {
            return element.attr("href");
        } else return "";
    }

    /**
     * @return XYZ for 'id="XYZ"' attributes
     */
    public String getIdAttribute() {
        return element.attr("id");

    }

    /**
     * @return x for '<img src="y" usemap="x">
     */
    public String getUsemapRef() {
        if (element.tagName().equals("img")) {
            return normalizeHrefString(element.attr("usemap"));
        }
        return "";
    }

    public String getHref() {
        return element.attributes().get("href");
    }

    public boolean hasImageAlt() {
        return element.hasAttr("alt") && !element.attr("alt").isEmpty();
    }

    public String getImgSrc() {
        return element.hasAttr("src") ? element.attr("src") : "";
    }

    public Element node() {
        return element;
    }

    @Override
    public String toString() {
        return element.toString();
    }
}



