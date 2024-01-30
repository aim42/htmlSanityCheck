package org.aim42.htmlsanitycheck.report;

/**
 * trivial class to convert filenames to html link targets.
 * E.g. the string "/dir/onefile.html" can be converted
 * to "XdirXonefileXhtml" or similar.
 */
public class CreateLinkUtil {
    // \W is regex for all non-word characters
    static final String regex = "[\\W ]";

    public static String convertToLink(String stringWithNonWordChars) {
        return stringWithNonWordChars.replaceAll(regex, "X");
    }
}
