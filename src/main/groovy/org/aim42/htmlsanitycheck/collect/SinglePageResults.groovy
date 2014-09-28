package org.aim42.htmlsanitycheck.collect

/**
 * Collects checking results {@link Finding} of a single html page.
 *
 * Contains information about the page itself, e.g. its filename and title.
 * Instances of this class will be contained in {@link SingleCheckResults}
 */
class SinglePageResults {

    public String pageFileName // from where we read the HTML
    public String pageTitle    // as given in the <title> attribute
    public int    pageSize     // size in byte


    // TODO: make private
    public List<SingleCheckResults> singleCheckResults

    public SinglePageResults() {
        this.singleCheckResults = new ArrayList<SingleCheckResults>()

    }

    public void addResultsForSingleCheck( SingleCheckResults resultsForSingleCheck ) {
        assert singleCheckResults != null

        singleCheckResults.add( resultsForSingleCheck )
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
