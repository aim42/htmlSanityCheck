package org.aim42.htmlsanitycheck.collect

/**
 * Collects checking results of 1..n html files which are checked together in one "run".
 *
 * Can keep results spanning more than one file (e.g. unused-image-files).
 *
 */
class PerRunResults {

    private ArrayList<SinglePageResults> listOfPageResults

    SingleCheckResults unusedImagesResultsCollector


    public PerRunResults() {
        this.listOfPageResults = new ArrayList<SinglePageResults>()

    }

    /**
     * adds one kind of checking results.
     * @param pageResults: checking results for a single HTML page
     */
    public void addPageResults( SinglePageResults pageResults ) {
        assert listOfPageResults != null

        listOfPageResults.add(pageResults)
    }

    /**
     *
     * @return how many distinct CheckingResultCollectors have been added (so far)?
     */
    public int howManyPagesChecked() {
        return listOfPageResults.size()
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

