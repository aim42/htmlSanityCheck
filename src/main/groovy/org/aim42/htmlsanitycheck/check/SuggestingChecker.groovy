package org.aim42.htmlsanitycheck.check

import org.aim42.htmlsanitycheck.collect.Finding
import org.aim42.htmlsanitycheck.collect.SingleCheckResults
import org.aim42.htmlsanitycheck.html.HtmlPage
import org.aim42.htmlsanitycheck.suggest.Suggester


/**
 * Abstract class for those @see Checker subclasses that
 * can propose suggestions, not only identify errors.
 * Example: MissingImagesChecker might suggest names of existing images
 * that "could have been meant"
 *
 */
abstract class SuggestingChecker extends Checker {

    // valid possibilities for e.g. image-file-names or link-targets
    ArrayList<String> validPossibilities

    @Override
    abstract protected void initCheckingResultsDescription()

    /**  let the instance determine the list of possible values
     * Examples:
     * - MissingImageFilesChecker -> collect the names of images files
     * - BrokenCrossReferencesChecker -> collect all (internal) link targets
     **/
    abstract protected void setValidPossibilities()


    @Override
    abstract protected SingleCheckResults check( final HtmlPage pageToCheck)


    /**
     * a little tricky: call performCheck on the superclass and add a little behavior :-)
     * it's a Template-Method again.
     * @return List of Findings (SingleCheckResults), but with suggestions for each finding
     */
    @Override
    public SingleCheckResults performCheck(final HtmlPage pageToCheck) {
        SingleCheckResults scResults = super.performCheck( pageToCheck )

        setValidPossibilities()

        determinSuggestionsForEveryFinding()

        return scResults
    }


    /**
     * determines suggestions for every Finding agains the list
     * of valid possibilities
     */
    public void determinSuggestionsForEveryFinding() {
        checkingResults.findings.each { finding ->
            determineSuggestionsForSingleFinding( finding )
        }
    }

    /**
     *
     */
    public void determineSuggestionsForSingleFinding( Finding finding ) {
        finding.setSuggestions( Suggester.determineNSuggestions(finding.item, validPossibilities, 1))

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
