package org.aim42.htmlsanitycheck.report;

/**
 * trivial class to convert filenames to html link targets.
 * E.g. the string "/dir/onefile.html" can be converted
 * to "XdirXonefileXhtml" or similar.
 */
public class CreateLinkUtil {
    public static String convertToLink(String stringWithNonWordChars) {

        // \W is regex for all non-word characters
        String regex = "\\W ";

        return stringWithNonWordChars.replaceAll(regex, "X");
    }

}
