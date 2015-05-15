package org.aim42.htmlsanitycheck.html

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.select.Elements

// see end-of-file for license information

/**
 * demo-code for the Jsoup html parser - largely taken
 * from their website.
 *
 * Parses the plugin's own readme.html file...
 * and reports some links.
 *
 */

class ParserSample {

    public static void main(String[] args) {
        final String userDir = System.getProperty("user.dir")
        final String fileName = 'README.html'
        final String localPath = "/"
        final String filePath = userDir + localPath + fileName
        final String pathToThisClass  = new File(".").getAbsolutePath()


        println "canonicalPath = $pathToThisClass"


        final File file = new File(filePath)
        print("Fetching %s...", filePath);

        println "file $filePath exists: " + new File(filePath).exists()
        Document doc = Jsoup.parse( file, "UTF-8" );

        HtmlPage page = new HtmlPage( file )
        ArrayList<HtmlElement> imgs = page.getAllImageTags()

        print "found %d images", imgs.size()

        print "first image"
        println imgs.first().getImageSrcAttribute()

        Elements links = doc.select("a[href]");
        Elements media = doc.select("[src]");
        Elements imports = doc.select("link[href]");

        print("\nMedia: (%d)", media.size());
        for (Element src : media) {
            if (src.tagName().equals("img"))
                print(" * %s: <%s> %sx%s (%s)",
                        src.tagName(), src.attr("src"), src.attr("width"), src.attr("height"),
                        trim(src.attr("alt"), 20));
            else
                print(" * %s: <%s>", src.tagName(), src.attr("src"));
        }

        print("\nImports: (%d)", imports.size());
        for (Element link : imports) {
            print(" * %s <%s> (%s)", link.tagName(),link.attr("abs:href"), link.attr("rel"));
        }

        // how to get src-attribute from img-tags:
        Elements images = doc.getElementsByTag("img")

        print("\n Images: (%d)", images.size() )
        images.each { imageTag ->
            print(" * %s <%s> %s",
                    imageTag.tagName(),     // img
                    imageTag.attributes(),  // src="XYZ"
                    imageTag.attr("src"))   // XYZ
        }

        println "Elements are of class " + links.getClass()

        print("\nLinks: (%d)", links.size());
        for (Element link : links) {
            print(" * a: <%s>  (%s)", link.attr("abs:href"), trim(link.text(), 35));
        }
    }

    private static void print(String msg, Object... args) {
        System.out.println(String.format(msg, args));
    }

    private static String trim(String s, int width) {
        if (s.length() > width)
            return s.substring(0, width-1) + ".";
        else
            return s;
    }

}

