package org.aim42.htmlsanitycheck.collect;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * Collects checking results {@link Finding} of a single html page.
 * <p>
 * Contains information about the page itself, e.g., its filename and title.
 * Instances of this class will be contained in {@link SingleCheckResults}
 */
@AllArgsConstructor
@Getter
public class SinglePageResults implements PageResults {

    private String pageFileName; // from where we read the HTML
    private String pageFilePath; // the complete path from where we read
    private String pageTitle;    // as given in the <title> attribute
    private int pageSize;     // size in byte
    private List<SingleCheckResults> singleCheckResults;


    // some variants for construction
    public SinglePageResults() {
        this.singleCheckResults = new ArrayList<>();
        this.pageFileName = "";
        this.pageFilePath = "";
        this.pageTitle = "";
        this.pageSize = 0;
    }

    /**
     * Allows adding the results of a single check
     **/
    public void addResultsForSingleCheck(SingleCheckResults resultsForSingleCheck) {
        singleCheckResults.add(resultsForSingleCheck);
    }


    // overhead for Groovy code - but useful for Interface documentation
    @Override
    public String getPageTitle() {
        return pageTitle;
    }

    @Override
    public String getPageFileName() {
        return pageFileName;
    }

    @Override
    public String getPageFilePath() {
        return pageFilePath;
    }


    // query the results
    @Override
    public int nrOfItemsCheckedOnPage() {
        return singleCheckResults.stream().map(SingleCheckResults::getNrOfItemsChecked).reduce(0, Integer::sum);
    }

    @Override
    public int nrOfFindingsOnPage() {
        return singleCheckResults.stream().map(SingleCheckResults::nrOfProblems).reduce(0, Integer::sum);
    }

    /**
     * Returns the number of distinct checker types that have run
     * (by the number of SingleCheckResults available)
     *
     */
    @Override
    public int howManyCheckersHaveRun() {
        return singleCheckResults.size();
    }
}

/************************************************************************
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
