package org.aim42.htmlsanitycheck.checker

import org.aim42.htmlsanitycheck.URLUtil

// see end-of-file for license information


class BrokenCrossReferencesChecker extends Checker {


    private List<String> listOfIds    // id="XYZ"
    private List<String> hrefList
    private Set<String> hrefSet       // a href="XYZ"



    @Override
    protected void initCheckingResultsDescription() {
        checkingResults.whatIsChecked =  "Broken Internal Links Check"
        checkingResults.sourceItemName =  "href"
        checkingResults.targetItemName = "missing id"
    }


    @Override
    protected CheckingResultsCollector check() {


        //get list of all a-tags "<a src=..." in html file
        hrefList = pageToCheck.getAllHrefStrings()
        hrefSet  = hrefList.toSet()

        // get list of all id="XYZ"
        listOfIds = pageToCheck.getAllIdStrings()

        checkAllInternalLinks( )

        return checkingResults
    }

    /*
     * check all internal links against the existing id's
     */
    private void checkAllInternalLinks() {

        // for all hrefSet check if the corresponding id exists
        hrefSet.each { href ->
            checkSingleInternalLink( href )
        }
    }

    /*
    * check a single internal link (href) against the existing id's within
    * the html document
     */
    private void checkSingleInternalLink( String href ) {

        // we check only cross-references, that means we exclude
        // remote-urls and references to local files
        if (URLUtil.isCrossReference( href )) {

            // bookkeeping:
            checkingResults.incNrOfChecks()

            doesLinkTargetExist(href)
        }
    }

    /**
     * check if the id for the href parameter exists
     *
     * @param href= "#XYZ" in id="XYZ"
     **/
    private void doesLinkTargetExist(String href) {

        // strip href of its leading "#"
        String linkTarget = (href.startsWith("#")) ? href[1..-1] : href


        if (!listOfIds.contains( linkTarget )) {

            // we found a broken link!

            // now count occurences - how often is it referenced
            int nrOfReferences = hrefList.findAll{  it == href }.size()

            String findingText = "link target \"$linkTarget\" missing (reference count $nrOfReferences)"

            checkingResults.newFinding(findingText)
        }

    }


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


