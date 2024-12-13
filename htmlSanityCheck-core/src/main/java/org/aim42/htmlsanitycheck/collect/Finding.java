// see end-of-file for license information

package org.aim42.htmlsanitycheck.collect;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * A single "finding" from any check, i.e.,
 * - a missing image file
 * - a missing label/id/bookmark (== broken link)
 * - a duplicate label/id/bookmark
 */
@Getter
public class Finding {

    String whatIsTheProblem; // i.e., which image is missing, which link/anchor is undefined
    int nrOfOccurrences;// how often does this specific finding occur in the checked-page
    // suggestions are ordered: getAt(0) yields the best, getAt(1) the second and so forth
    @Setter
    List<String> suggestions;

    public Finding() {
        this("");
    }

    /**
     * No finding should exist without giving an explanation ("whatIsTheProblem")
     * about what went wrong.
     *
     * @param whatIsTheProblem An explanation of what went wrong (i.e., name of the missing file)
     */
    public Finding(String whatIsTheProblem) {
        this(whatIsTheProblem, 1, new ArrayList<>(3));
    }

    /**
     * Finding with explanation and several occurrences
     */
    public Finding(String whatIsTheProblem, int nrOfOccurrences) {
        this(whatIsTheProblem, nrOfOccurrences, new ArrayList<>(3));
    }


    /**
     * Most general constructor:
     * create Finding with explanation and nrOfOccurrences
     *
     * @param whatIsTheProblem An explanation of what went wrong (i.e. name of missing file)
     */
    public Finding(String whatIsTheProblem, int nrOfOccurrences, List<String> suggestions) {
        this.whatIsTheProblem = whatIsTheProblem;
        this.nrOfOccurrences = nrOfOccurrences;
        this.suggestions = suggestions;

    }

    /**
     * Create Finding with explanation and suggestions
     *
     * @param whatIsTheProblem explanation what went wrong
     * @param suggestions      what could have been meant
     */
    public Finding(String whatIsTheProblem, List<String> suggestions) {
        this(whatIsTheProblem, 1, suggestions);
    }

    @Override
    public String toString() {
        String refCount = (nrOfOccurrences > 1) ? String.format(" (reference count: %d)", nrOfOccurrences) : "";
        String suggestionStr = (!suggestions.isEmpty()) ? "\n (Suggestions: " + String.join(",", suggestions) + ")" : "";

        return whatIsTheProblem + refCount + (suggestionStr.isEmpty() ? "" : suggestionStr);
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

