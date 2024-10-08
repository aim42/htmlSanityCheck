package org.aim42.htmlsanitycheck.report;

import org.aim42.htmlsanitycheck.ProductInformation;
import org.aim42.htmlsanitycheck.collect.PerRunResults;
import org.aim42.htmlsanitycheck.collect.SingleCheckResults;
import org.aim42.htmlsanitycheck.collect.SinglePageResults;

import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * superclass for reporting results.
 * Subclasses will define the concrete output format
 */
public abstract class Reporter {
    /**
     * create the reporter
     */
    protected Reporter() {
        this.createdOnDate = new SimpleDateFormat("dd. MMMM yyyy, HH:mm").format(new Date());
        this.createdByHSCVersion = ProductInformation.VERSION;
    }

    /**
     * Usually a Reporter instance shall be constructed with its appropriate
     *
     * @param runResults the results for the report
     * @see PerRunResults as the latter contains all findings.
     */
    protected Reporter(PerRunResults runResults) {
        this();
        this.runResults = runResults;
        this.pageResults = runResults.getResultsForAllPages();

    }

    /**
     * add checking results for one page
     */
    public void addCheckingResultsForOnePage(SinglePageResults singlePageResults) {
        pageResults.add(singlePageResults);
    }

    /**
     * main entry point for reporting - to be called when a report is requested
     * <p>
     * Uses template-method to delegate most concrete implementations to subclasses
     */
    public void reportFindings() {
        initReport();
        reportOverallSummary();
        reportAllPages();
        closeReport();
    }

    private void reportAllPages() {
        for (SinglePageResults pageResult : pageResults) {
            reportPageSummary(pageResult);// delegated to subclass
            reportPageDetails(pageResult);// implemented below
            reportPageFooter();// delegated to subclass
        }
    }

    protected void reportPageDetails(SinglePageResults singlePageResults) {
        for (SingleCheckResults resultForOneCheck : singlePageResults.getSingleCheckResults()) {
            reportSingleCheckSummary(resultForOneCheck);
            reportSingleCheckDetails(resultForOneCheck);
        }

    }

    protected int totalNrOfPages() {
        return pageResults.size();
    }

    protected int totalNrOfChecks() {
        return runResults.nrOfChecksPerformedOnAllPages();
    }

    protected int totalNrOfFindings() {
        return runResults.nrOfFindingsOnAllPages();
    }

    protected void initReport() {
        // default: do nothing
    }

    protected abstract void reportOverallSummary();

    protected abstract void reportPageSummary(SinglePageResults singlePageResults);

    protected abstract void reportPageFooter();

    protected abstract void reportSingleCheckSummary(SingleCheckResults singleCheckResults);

    protected abstract void reportSingleCheckDetails(SingleCheckResults singleCheckResults);

    protected void closeReport() {
        // default: do nothing
    }

    protected List<SinglePageResults> pageResults;
    protected PerRunResults runResults;
    protected final String createdOnDate;
    protected final String createdByHSCVersion;
}
