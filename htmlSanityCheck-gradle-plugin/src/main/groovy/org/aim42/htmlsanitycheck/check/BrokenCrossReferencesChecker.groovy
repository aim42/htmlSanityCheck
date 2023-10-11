package org.aim42.htmlsanitycheck.check

import org.aim42.htmlsanitycheck.Configuration
import org.aim42.htmlsanitycheck.collect.SingleCheckResults
import org.aim42.htmlsanitycheck.html.HtmlPage
import org.aim42.htmlsanitycheck.html.URLUtil

// see end-of-file for license information


class BrokenCrossReferencesChecker extends SuggestingChecker {

    private List<String> listOfIds    // id="XYZ"
    private List<String> hrefList
    private Set<String> hrefSet

    BrokenCrossReferencesChecker(Configuration pConfig) {
        super(pConfig)
    }       // <a href="XYZ"...>

    @Override
    protected void initCheckingResultsDescription() {
        checkingResults.whatIsChecked = "Broken Internal Links Check"
        checkingResults.sourceItemName = "href"
        checkingResults.targetItemName = "missing id"
    }

    @Override
    /**
     set valid possibilities, where suggester can choose from.
     Here: List of (internal) id's, meaning link-targets.
     */
    protected void setValidPossibilities() {
        validPossibilities = listOfIds
    }

    @Override
    protected SingleCheckResults check(final HtmlPage pageToCheck) {
        //get list of all a-tags "<a href=..." in html file
        hrefList = pageToCheck.getAllHrefStrings()
        hrefSet = hrefList.toSet().sort()

        // get list of all id="XYZ"
        listOfIds = pageToCheck.getAllIdStrings()

        checkAllInternalLinks()

        return checkingResults
    }

    /**
     * check all internal links against the existing id's
     */
    private void checkAllInternalLinks() {
        // for all hrefSet check if the corresponding id exists
        hrefSet.each { href ->
            //if (URLUtil.isValidURL(href))
            checkSingleInternalLink(href)
        }
    }

    /**
     * check a single internal link (href) against the existing id's within
     * the html document
     */
    private void checkSingleInternalLink(String href) {
        checkingResults.incNrOfChecks()
        if (URLUtil.containsInvalidChars(href)) {
            // we found link with illegal characters!
            String findingText = "link \"$href\" contains illegal characters"
            // now count occurrences - how often is it referenced
            int nrOfReferences = countNrOfReferences(href)
            if (nrOfReferences > 1) {
                findingText += ", reference count: $nrOfReferences"
            }
            checkingResults.newFinding(findingText, nrOfReferences)
        } else
        // we check only cross-references, that means we exclude
        // remote-urls and references to local files
        if (URLUtil.isCrossReference(href)) {

            // bookkeeping:
            checkingResults.incNrOfChecks()

            doesLinkTargetExist(href)
        }
    }

    /**
     * check if the id for the href parameter exists
     *
     * @param href = "#XYZ" in id="XYZ"
     * */
    private void doesLinkTargetExist(String href) {
        if (href == '#') {
            return
        }

        // strip href of its leading "#"
        String linkTarget = (href.startsWith("#")) ? href[1..-1] : href
        // fragment can be URL-encoded
        linkTarget = URLDecoder.decode(linkTarget, 'UTF-8')

        if (!listOfIds.contains(linkTarget)) {
            // we found a broken link!
            addBrokenLinkToResults(linkTarget, href)
        }
    }

    /**
     * bookkeeping the broken links that we found
     */
    private void addBrokenLinkToResults(String linkTarget, String href) {
        String findingText = "link target \"$linkTarget\" missing"

        // now count occurrences - how often is it referenced
        int nrOfReferences = countNrOfReferences(href)
        if (nrOfReferences > 1) {
            findingText += ", reference count: $nrOfReferences"
        }

        // determine suggestions "what could have been meant?"

        checkingResults.newFinding(findingText, nrOfReferences)
    }

    private int countNrOfReferences(String href) {
        int nrOfReferences = hrefList.findAll { it == href }.size()
        return nrOfReferences
    }

}
/*========================================================================
 Copyright Gernot Starke and aim42 contributors

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
