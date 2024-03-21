package org.aim42.htmlsanitycheck.report;

import org.aim42.htmlsanitycheck.ProductVersion;
import org.aim42.htmlsanitycheck.collect.Finding;
import org.aim42.htmlsanitycheck.collect.PerRunResults;
import org.aim42.htmlsanitycheck.collect.SingleCheckResults;
import org.aim42.htmlsanitycheck.collect.SinglePageResults;

import java.util.Collections;
import java.util.function.Consumer;

/**
 * This is free software - without ANY guarantee!
 * <p>
 * Copyright 2013, Dr. Gernot Starke, arc42.org
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * <a href="http://www.apache.org/licenses/LICENSE-2.0">...</a>
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

public class ConsoleReporter extends Reporter {
    protected Consumer<String> printer = System.out::println;

    // from AllChecksRunner - create ConsoleReporter with PerRunResults
    public ConsoleReporter(PerRunResults runResults) {
        super(runResults);
    }

    @Override
    public void initReport() {
        Long millis = runResults.checkingTookHowManyMillis();

        printer.accept("********* HTML Sanity Checker findings report *********");
        printer.accept(String.format("created on %s by version %s", createdOnDate, ProductVersion.getVersion()));
        printer.accept(String.format("checking took %s msecs.", millis));
        printer.accept("");
    }

    @Override
    protected void reportOverallSummary() {
        int percentageSuccessful = SummarizerUtil.percentSuccessful(totalNrOfChecks(), totalNrOfFindings());

        String pageStr = (totalNrOfPages() > 1) ? "pages" : "page";
        String issueStr = (totalNrOfFindings() > 1) ? "issues" : "issue";

        printer.accept("Summary for all pages:");
        printer.accept("======================");
        printer.accept(String.format("checked %d items on %d %s,", totalNrOfChecks(), totalNrOfPages(), pageStr));
        printer.accept(String.format("found %d %s, %d%% successful.", totalNrOfFindings(), issueStr, percentageSuccessful));
        printer.accept(String.join("", Collections.nCopies(50, "-")));
    }

    @Override
    protected void reportPageSummary(SinglePageResults pageResult) {
        printer.accept(String.format("Summary for file %s\n", pageResult.getPageFileName()));
        printer.accept(String.format("page path  : %s", pageResult.getPageFilePath()));
        printer.accept(String.format("page title : %s", pageResult.getPageTitle()));
        printer.accept(String.format("page size  : %d bytes", pageResult.pageSize));
    }

    @Override
    protected void reportPageFooter() {
        printer.accept(String.join("", Collections.nCopies(50, "=")));
    }

    @Override
    protected void reportSingleCheckSummary(SingleCheckResults singleCheckResults) {
        for (Finding finding : singleCheckResults.getFindings()) {
            printer.accept(finding.toString());
        }

        printer.accept(String.join("", Collections.nCopies(50, "-")));
    }

    @Override
    protected void reportSingleCheckDetails(SingleCheckResults singleCheckResults) {
        for (Finding finding : singleCheckResults.findings) {
            printer.accept(finding.toString());
        }
        printer.accept("\n");
        printer.accept(String.join("", Collections.nCopies(50, "-")));
    }

    @Override
    protected void closeReport() {
        printer.accept("thanx for using HtmlSanityChecker.");
    }

}