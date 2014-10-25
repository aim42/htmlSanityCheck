package org.aim42.htmlsanitycheck.collect

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

// tag::CheckResultsInterface[]
public interface CheckResults {

    // return a description of what is checked
    // (e.g. "Missing Images Checker" or "Broken Cross-References Checker"
    public String description()

    // returns all findings/problems found during this check
    public  ArrayList<Finding> getFindings()

    }
// end::CheckResultsInterface[]