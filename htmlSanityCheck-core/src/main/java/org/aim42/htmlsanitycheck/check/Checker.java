package org.aim42.htmlsanitycheck.check;

import lombok.Getter;
import lombok.Setter;
import org.aim42.htmlsanitycheck.Configuration;
import org.aim42.htmlsanitycheck.collect.SingleCheckResults;
import org.aim42.htmlsanitycheck.html.HtmlPage;

/**
 * Base class for the different concrete checkers (i.e. ImageChecker),
 * following the template-method-pattern.
 * <p>
 * No constructor is defined, allowing for arbitrary "named parameters"
 * in constructor calls.
 * <p>
 * While checking, every subclass builds an instance of {@link SingleCheckResults}
 *
 * @author Gernot Starke <gs@gernotstarke.de>
 */
@Setter
@Getter
public abstract class Checker {
    private SingleCheckResults checkingResults;
    private Configuration myConfig;

    public Checker(Configuration pConfig) {
        this.myConfig = pConfig;
    }

    /**
     * * template method for performing a single type of checks on the given @see HtmlPage.
     * <p>
     * Prerequisite: pageToCheck has been successfully parsed,
     * prior to constructing this Checker instance.
     **/
    public SingleCheckResults performCheck(final HtmlPage pageToCheck) {
        // assert non-null htmlPage
        assert pageToCheck != null;

        checkingResults = new SingleCheckResults();

        // description is set by subclasses
        initCheckingResultsDescription();

        return check(pageToCheck);// <1> delegate check() to subclass
    }

    /**
     * Initialize with suitable description.
     */
    protected abstract void initCheckingResultsDescription();

    /**
     * Perform a particular kind of checks, i.e. missing-local-images-check
     * <p>
     * Called by {@link #performCheck(HtmlPage)} as part of the template method pattern.
     *
     * @return collected results of this Checker instance
     */
    protected abstract SingleCheckResults check(final HtmlPage pageToCheck);

}
