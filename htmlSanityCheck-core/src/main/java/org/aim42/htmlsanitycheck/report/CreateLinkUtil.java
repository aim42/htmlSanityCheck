package org.aim42.htmlsanitycheck.report;

/**
 * trivial class to convert filenames to html link targets.
 * E.g. the string "/dir/onefile.html" can be converted
 * to "XdirXonefileXhtml" or similar.
 */
public class CreateLinkUtil { // NOSONAR(S1118)
    // \W is regex for all non-word characters
    private static final String NON_WORD_PATTERN = "\\W";

    public static String convertToLink(String stringWithNonWordChars) {
        return stringWithNonWordChars.replaceAll(NON_WORD_PATTERN, "X");
    }
}
