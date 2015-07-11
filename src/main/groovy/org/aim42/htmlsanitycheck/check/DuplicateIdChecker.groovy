package org.aim42.htmlsanitycheck.check

import org.aim42.htmlsanitycheck.collect.SingleCheckResults
import org.aim42.htmlsanitycheck.html.HtmlElement
import org.aim42.htmlsanitycheck.html.HtmlPage


// see end-of-file for license information

class DuplicateIdChecker extends Checker {

    // the pure Id's as a set (duplicates are already removed here)
    // we take this set as basis for our checks!
    Set<String> idStringsSet

    // all html-tags containing ids including potential duplicates
    List<String> idStringsList


    @Override
    protected void initCheckingResultsDescription() {
        checkingResults.whatIsChecked  = "Duplicate Definition of id Check"
        checkingResults.sourceItemName = "id"
        checkingResults.targetItemName = "duplicate id"
     }


    @Override
    protected SingleCheckResults check( final HtmlPage pageToCheck) {

        //get list of all tagsWithId '<... id="XYZ"...' in html file

        idStringsList = pageToCheck.getAllIdStrings()
        idStringsSet = idStringsList.toSet()

        checkForDuplicateIds( idStringsSet )

        return checkingResults

    }

    /*
    * iterate over all id's to check for duplicate definitions
     */
    private void checkForDuplicateIds( Set<String> idStringsSet ) {

       idStringsSet.each { oneIdString ->
            checkForDuplicateDefinition( oneIdString )
        }

    }


    private void checkForDuplicateDefinition(String idString) {
        checkingResults.incNrOfChecks()

        int nrOfOccurrences = idStringsList.findAll{ it == idString}.size()

        // duplicate, IFF idString appears more than once in idStringsList
        if (nrOfOccurrences > 1) {

            checkingResults.newFinding(  "id \"$idString\" has $nrOfOccurrences definitions." )
        }
    }


    /**
     * find all tags with specific id value
     * @param id
     * @param allTags List of tags containing id-attribute
     */
    public static List<HtmlElement> getAllTagsWithSpecificId( String idString,
                                                              List<HtmlElement> allTags ) {
         return allTags.findAll { htmlElement ->
            htmlElement.idAttribute  == idString
        }
    }
}


/*=====================================================================
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
 =====================================================================*/

