// see end-of-file for license information

package org.aim42.htmlsanitycheck.collect

/**
 * A single "finding" from any check, i.e.:
 * - a missing image file
 * - a missing label/id/bookmark (== broken link)
 * - a duplicate label/id/bookmark
 */
class Finding {

    String whatIsTheProblem // i.e. which image is missing, which link/anchor is undefined

    int nrOfOccurrences // how often does this specific finding occur in the checked-page

    // suggestions are ordered: getAt(0) yields the best, getAt(1) the second and so forth
    ArrayList<String> suggestions


    public Finding() {
      this( "" )
    }

    /**
     * no finding should exist without giving an explanation ("whatIsTheProblem")
     * about what went wrong.
     * @param whatIsTheProblem An explanation of what went wrong (i.e. name of missing file)
     */
    public Finding(String whatIsTheProblem) {
        this( whatIsTheProblem, 1, new ArrayList<String>(3))
    }


    /**
     * finding with explanation and several occurences
     */
    public Finding(String whatIsTheProblem, int nrOfOccurrences ) {
        this( whatIsTheProblem, nrOfOccurrences, new ArrayList<String>(3))
    }


    /**
     * most general constructor:
     * create Finding with explanation and nrOfOccurrences
     * @param whatIsTheProblem  An explanation of what went wrong (i.e. name of missing file)
     * */
    public Finding(String whatIsTheProblem, int nrOfOccurrences, ArrayList<String> suggestions) {
        this.whatIsTheProblem = whatIsTheProblem
        this.nrOfOccurrences = nrOfOccurrences
        this.suggestions = suggestions

    }

    /**
     * create Finding with explanation and suggestions
     * @param whatIsTheProblem explanation what went wrong
     * @param suggestions what could have been meant
     */
    public Finding(String whatIsTheProblem, ArrayList<String> suggestions) {
        this( whatIsTheProblem, 1, suggestions)
    }

    /**
     * add a single suggestion to the list of suggestions
     * @param suggestion
     */
    public void addSingleSuggestion(String suggestion) {
        suggestions.add(suggestion)
    }


    public void setNrOfOccurences( int nrOfOccurences ) {
        this.nrOfOccurrences = nrOfOccurrences
    }

    @Override
    public String toString() {
        String refCount = (nrOfOccurrences > 1) ? " (reference count: $nrOfOccurrences)": ""
        String suggestionStr = (suggestions.size() > 0) ? " (Suggestions: " + suggestions.join(", ") + ")": ""

        return whatIsTheProblem + refCount + (suggestionStr ? "\n" + suggestionStr : "")
    }


}

/*======================================================================
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
 ======================================================================*/

