package org.aim42.htmlsanitycheck.collect;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Collects checking results of 1..n html files which are checked together in one "run".
 * <p>
 * Can keep results spanning more than one file (e.g. unused-image-files).
 */
public class PerRunResults implements RunResults {

    public final static Long ILLEGAL_TIMER = -7353315L;
    // magic number - also used in tests
    private final static Long TIMER_STILL_RUNNING = 42L;
    // unused images is the only check concerning all pages...
    private final List<SinglePageResults> resultsForAllPages;
    // checking time is important - therefore we maintain a timer
    private final Long startedCheckingTimeMillis;
    private Long finishedCheckingTimeMillis;


    /**
     * constructs a container for all checking results, including:
     * + checking results for every page (contained in @see SinglePageResults instances)
     * + results for the rather quirky @see UnusedImagesChecker
     * + a simple timer to validate the checks ran fast enough
     */
    public PerRunResults() {
        this.startedCheckingTimeMillis = System.currentTimeMillis();
        this.finishedCheckingTimeMillis = TIMER_STILL_RUNNING;

        this.resultsForAllPages = new ArrayList<>();

    }

    /**
     * return the List of results for the pages
     */
    @Override
    public List<SinglePageResults> getResultsForAllPages() {
        return resultsForAllPages;
    }

    /**
     * stop the checking timer
     */
    public void stopTimer() {
        finishedCheckingTimeMillis = System.currentTimeMillis();
    }

    /**
     * query the timer
     * if timer has not yet been stopped - return a crazy number
     */
    @Override
    public Long checkingTookHowManyMillis() {
        long itTookSoLong;
        if (Objects.equals(finishedCheckingTimeMillis, TIMER_STILL_RUNNING)) {
            itTookSoLong = ILLEGAL_TIMER; // if read upside down: "Sie Esel"
        } else {
            itTookSoLong = finishedCheckingTimeMillis - startedCheckingTimeMillis;
        }
        return itTookSoLong;
    }


    /**
     * adds one kind of checking results.
     *
     * @param pageResults : checking results for a single HTML page
     */
    public void addPageResults(SinglePageResults pageResults) {
        assert resultsForAllPages != null;

        resultsForAllPages.add(pageResults);
    }

    /**
     * @return how many distinct CheckingResultCollectors have been added (so far)?
     */
    @Override
    public int nrOfPagesChecked() {
        return resultsForAllPages.size();
    }

    /**
     * returns the total number of checks performed on all pages
     */
    @Override
    public int nrOfChecksPerformedOnAllPages() {

        return resultsForAllPages.stream()
                .map(SinglePageResults::nrOfItemsCheckedOnPage)
                .reduce(Integer::sum).orElse(0);


    }

    /**
     * returns the total number of findings on all pages
     */
    @Override
    public int nrOfFindingsOnAllPages() {

        return resultsForAllPages.stream()
                .map(SinglePageResults::nrOfFindingsOnPage)
                .reduce(Integer::sum).orElse(0);
    }


}
/* ***********************************************************************
 * This is free software - without ANY guarantee!
 *
 *
 * Copyright 2013, Dr. Gernot Starke, arc42.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 *********************************************************************** */

