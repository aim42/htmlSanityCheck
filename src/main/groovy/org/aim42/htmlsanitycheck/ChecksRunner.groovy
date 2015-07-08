package org.aim42.htmlsanitycheck

import org.aim42.htmlsanitycheck.check.Checker
import org.aim42.htmlsanitycheck.collect.PerRunResults
import org.aim42.htmlsanitycheck.html.HtmlPage
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * runs one or several checks on HTML input
 */
class ChecksRunner {

    // we check a collection of files:
    private Collection<File> filesToCheck

    // where do we put our results
    private File checkingResultsDir

    // checker instances
    private List<Checker> checkers

    private HtmlPage pageToCheck

    // keep all results
    private PerRunResults resultsForAllPages

    private static Logger logger = LoggerFactory.getLogger(ChecksRunner.class);



}

/************************************************************************
 * This is free software - without ANY guarantee!
 *
 *
 * Copyright Dr. Gernot Starke, arc42.org
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

