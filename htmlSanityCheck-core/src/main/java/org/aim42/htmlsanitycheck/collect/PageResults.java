package org.aim42.htmlsanitycheck.collect;

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

// tag::PageResultInterface[]
public interface PageResults {

    // what's the title of this page?
    String getPageTitle();

    // what's the filename and path?
    String getPageFileName();

    String getPageFilePath();

    // how many items have been checked?
    int nrOfItemsCheckedOnPage();

    // how many problems were found on this page?
    int nrOfFindingsOnPage();

    // how many different checks have run on this page?
    int howManyCheckersHaveRun();
}
// end::PageResultInterface[]
