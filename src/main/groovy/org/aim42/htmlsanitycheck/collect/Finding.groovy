// see end-of-file for license information

package org.aim42.htmlsanitycheck.collect

/**
 * A single "finding" from any check, i.e.:
 * - a missing image file
 * - a missing label/id/bookmark (== broken link)
 * - a duplicate label/id/bookmark
 */
class Finding {

    // TODO handle suggestions


    String item // i.e. which image is missing, which link/anchor is undefined

    int nrOfOccurrences // how often does this specific finding occur in the checked-page

    ArrayList<String> suggestions


    /**
     * no finding should exist without giving an explanation ("item")
     * about what went wrong.
     * @param item An explanation of what went wrong.
     */
    public Finding( String item ) {
        this.item = item
        suggestions = new ArrayList<String>()
    }

    /**
     * create Finding with explanation and suggestions
     * @param item explanation what went wrong
     * @param suggestions what could have been meant
     */
    public Finding( String item, ArrayList<String> suggestions ) {
        this.item = item
        this.suggestions = suggestions
    }

    /**
     * add a single suggestion to the list of suggestions
     * @param suggestion
     */
    public void addSuggestion( String suggestion) {
        suggestions.add( suggestion )
    }



    @Override
    public String toString() {
        return item
    }
}

/*======================================================================
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
 ======================================================================*/

