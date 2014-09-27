package org.aim42.htmlsanitycheck.check

import org.aim42.htmlsanitycheck.collect.SingleCheckResultsCollector
import org.aim42.htmlsanitycheck.html.HtmlPage


// see end-of-file for license information


/**
 * Base class for the different concrete checkers (i.e. ImageChecker),
 * following the template-method-pattern.
 *
 * No constructor is defined, allowing for arbitrary "named parameters"
 * in constructor calls.
 *
 * While checking, every subclass builds an instance of {@link SingleCheckResultsCollector}
 *
 * @author Gernot Starke <gs@gernotstarke.de>
 */

abstract class Checker {

     // i.e. image-links, internal links (anchors/hrefs)
    SingleCheckResultsCollector checkingResults

    HtmlPage pageToCheck


    /**
    ** template method for performing a single type of checks on the given @see HtmlPage.
     *
     * Prerequisite: pageToCheck has been successfully parsed,
     * prior to constructing this Checker instance.
    **/
    public SingleCheckResultsCollector performCheck() {
        // assert non-null htmlPage
        assert pageToCheck != null

        checkingResults = new SingleCheckResultsCollector()

        // description is set by subclasses
        initCheckingResultsDescription()

        return check() // execute the actual checking algorithm
    }


    /**
     * Initialize with suitable description.
     *
     */
    abstract protected void initCheckingResultsDescription()


    /**
     * Perform a particular kind of checks, i.e. missing-local-images-check
     *
     * Called by {@link #performCheck()} as part of the template method pattern.
     * @return collected results of this Checker instance
     */
    abstract protected SingleCheckResultsCollector check( )



}


/*========================================================================
 Copyright 2014 Gernot Starke and aim42 contributors

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an
 "AS IS" BASIS,WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 ========================================================================*/


