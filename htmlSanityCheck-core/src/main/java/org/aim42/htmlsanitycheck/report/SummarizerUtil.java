package org.aim42.htmlsanitycheck.report;

public class SummarizerUtil {
    /**
     * returns the percentage of successful checks.
     * <p>
     * Edge case:
     * 0 checks -> 100% successful
     */
    public static int percentSuccessful(int totalChecks, int totalNrOfFindings) {


        // base case: if no checks performed, 100% successful
        if (totalChecks <= 0) {
            return 100;
        } else {

            return 100 - Math.round((100f * totalNrOfFindings) / (float)totalChecks);
        }

    }

    /**
     * rounds one down to at most 3 digits with two decimalplaces,
     * e.g. from
     * 33450 to 33.45, from 1_234_566 to 1.23
     */
    public static float threeDigitTwoDecimalPlaces(int bigNumber) {


        if (bigNumber >= 1_000_000)
            return Math.round(bigNumber/ 10000f) / 100f;
        else
            return Math.round(bigNumber/ 10f) / 100f;

    }

}
