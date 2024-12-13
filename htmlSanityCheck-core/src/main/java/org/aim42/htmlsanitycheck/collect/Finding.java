// see end-of-file for license information

package org.aim42.htmlsanitycheck.collect;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Arrays;
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
    int nrOfOccurrences; // how often does this specific finding occur in the checked-page
    // suggestions are ordered: getAt(0) yields the best, getAt(1) the second and so forth
    @Setter
    List<String> suggestions;
    Throwable throwable;

    /**
     * No finding should exist without giving an explanation ("whatIsTheProblem")
     * about what went wrong.
     *
     * @param whatIsTheProblem An explanation of what went wrong (i.e., name of the missing file)
     */
    public Finding(String whatIsTheProblem) {
        this(whatIsTheProblem, 1, new ArrayList<>(3), null);
    }

    /**
     * Finding with explanation and several occurrences.
     *
     * @param whatIsTheProblem An explanation of what went wrong (i.e., name of the missing file).
     * @param throwable        The throwable instance representing an exception or error related to this occurrence.
     */
    public Finding(final String whatIsTheProblem, final Throwable throwable) {
        this(whatIsTheProblem, 1, new ArrayList<>(1), throwable);
    }

    /**
     * Finding with explanation and several occurrences.
     *
     * @param whatIsTheProblem An explanation of what went wrong (i.e., name of the missing file).
     * @param nrOfOccurrences  The number of occurrences of a specific issue or event.
     */
    public Finding(String whatIsTheProblem, int nrOfOccurrences) {
        this(whatIsTheProblem, nrOfOccurrences, new ArrayList<>(3), null);
    }

    /**
     * Finding with explanation and several occurrences, as well as suggestions.
     *
     * @param whatIsTheProblem An explanation of what went wrong (i.e., name of the missing file).
     * @param nrOfOccurrences  The number of occurrences of a specific issue or event.
     * @param suggestions      A list of suggestions related to resolving or addressing the issue.
     */
    public Finding(String whatIsTheProblem, int nrOfOccurrences, List<String> suggestions) {
        this(whatIsTheProblem, nrOfOccurrences, suggestions, null);
    }

    /**
     * Most general constructor: Create Finding with all attributes.
     *
     * @param whatIsTheProblem An explanation of what went wrong (i.e., name of the missing file).
     * @param nrOfOccurrences  The number of occurrences of a specific issue or event.
     * @param suggestions      A list of suggestions related to resolving or addressing the issue.
     * @param throwable        The throwable instance representing an exception or error related to this occurrence.
     */
    public Finding(String whatIsTheProblem, int nrOfOccurrences,
                   List<String> suggestions, Throwable throwable) {
        this.whatIsTheProblem = whatIsTheProblem;
        this.nrOfOccurrences = nrOfOccurrences;
        this.suggestions = suggestions;
        this.throwable = throwable;
    }


    @Override
    public String toString() {
        String refCount = (nrOfOccurrences > 1) ? String.format(" (reference count: %d)", nrOfOccurrences) : "";
        String suggestionStr = (!suggestions.isEmpty()) ? "\n (Suggestions: " + String.join(",", suggestions) + ")" : "";
        String stackTrace = (null != throwable)
                ? "\nStackTrace:\n\t" + String.join("\n\t", Arrays.stream(throwable.getStackTrace())
                .map(StackTraceElement::toString)
                .toArray(String[]::new))
                : "";

        return whatIsTheProblem + refCount + suggestionStr + stackTrace;
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

