package org.aim42.htmlsanitycheck.check;

import org.aim42.htmlsanitycheck.Configuration;
import org.aim42.htmlsanitycheck.collect.Finding;
import org.aim42.htmlsanitycheck.collect.SingleCheckResults;
import org.aim42.htmlsanitycheck.html.HtmlPage;
import org.aim42.htmlsanitycheck.suggest.Suggester;

import java.util.List;

/**
 * Abstract class for those @see Checker subclasses that
 * can propose suggestions, not only identify errors.
 * Example: MissingImagesChecker might suggest names of existing images
 * that "could have been meant"
 */
public abstract class SuggestingChecker extends Checker {
    private List<String> validPossibilities;

    public SuggestingChecker(Configuration pConfig) {
        super(pConfig);
    }

    @Override
    protected abstract void initCheckingResultsDescription();

    /**
     * let the instance determine the list of possible values
     * Examples:
     * - MissingImageFilesChecker -> collect the names of images files
     * - BrokenCrossReferencesChecker -> collect all (internal) link targets
     **/
    protected abstract void setValidPossibilities();

    @Override
    protected abstract SingleCheckResults check(final HtmlPage pageToCheck);

    /**
     * a little tricky: call performCheck on the superclass and add a little behavior :-)
     * it's a Template-Method again.
     *
     * @return List of Findings (SingleCheckResults), but with suggestions for each finding
     */
    @Override
    public final SingleCheckResults performCheck(HtmlPage pageToCheck) {
        SingleCheckResults scResults = super.performCheck(pageToCheck);

        setValidPossibilities();

        determinSuggestionsForEveryFinding();

        return scResults;
    }

    /**
     * determines suggestions for every Finding agains the list
     * of valid possibilities
     */
    public void determinSuggestionsForEveryFinding() {
        getCheckingResults().getFindings().stream().forEach(finding ->
                determineSuggestionsForSingleFinding(finding));
    }

    /**
     *
     */
    public void determineSuggestionsForSingleFinding(Finding finding) {
        finding.setSuggestions(Suggester.determineNSuggestions(finding.getWhatIsTheProblem(), validPossibilities, 1));

    }

    public List<String> getValidPossibilities() {
        return validPossibilities;
    }

    public void setValidPossibilities(List<String> validPossibilities) {
        this.validPossibilities = validPossibilities;
    }
}
