package org.aim42.htmlsanitycheck.collect;

import java.util.List;

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

// tag::RunResultInterface[]
public interface RunResults {

    // returns results for all pages which have been checked
    List<SinglePageResults> getResultsForAllPages();

    // how many pages were checked in this run?
    int nrOfPagesChecked();

    // how many checks were performed in all?
    int nrOfChecksPerformedOnAllPages();

    // how many findings (errors and issues) were found in all?
    int nrOfFindingsOnAllPages();

    // how long took checking (in milliseconds)?
    Long checkingTookHowManyMillis();
}
// end::RunResultInterface[]