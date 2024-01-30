// see end-of-file for license information

package org.aim42.htmlsanitycheck.collect;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * collects results for a specific type of @see Checker
 * (i.e. missing images, broken cross-references).
 *
 * @author Gernot Starke <gs@gernotstarke.de>
 */

public class SingleCheckResults implements CheckResults {

    // the actual findings
    public List<Finding> findings;
    @Setter
    @Getter
    String whatIsChecked;    // i.e. "Missing Local Images Check"
    // source-whatIsTheProblem is checked against target-whatIsTheProblem
    @Setter
    @Getter
    String sourceItemName;    // i.e. image-src-attribute, anchor/link
    @Setter
    @Getter
    private String targetItemName;    // i.e. local-image-file, id/bookmark
    @Setter
    @Getter
    String generalRemark;     // i.e. "Internet not available"
    @Getter
    int nrOfItemsChecked;
    // nrOfIssues can be larger than findings.size(),
    // if some findings occur more than once
    private int nrOfIssues;

    /**
     * Initialize some members.
     * <p>
     * Other members are set by the Checker-instance
     * owning this SingleCheckResults.
     */
    public SingleCheckResults() {

        this.nrOfItemsChecked = 0;
        this.nrOfIssues = 0;
        this.findings = new ArrayList<>();
        this.generalRemark = "";
    }


    /**
     * add a single finding to the collection,
     *
     * @param message: what kind of finding is it?
     */
    public void newFinding(String message) {
        addFinding(new Finding(message), 1);
    }

    /**
     * add a single finding to the collection,
     *
     * @param message:         what kind of finding is it?
     * @param nrOfOccurrences: how often does this occur?
     */
    public void newFinding(String message, int nrOfOccurrences) {
        addFinding(new Finding(message), nrOfOccurrences);
    }


    /**
     * Add a single finding to the collection of Finding instances
     *
     * @param singleFinding the finding to be added
     */
    public void addFinding(Finding singleFinding) {
        if (null != singleFinding) {
            findings.add(singleFinding);
            incNrOfIssues();
        }
    }

    /**
     * add single Finding with multiple occurrences
     */
    public void addFinding(Finding singleFinding, int nrOfOccurrences) {
        findings.add(singleFinding);
        addNrOfIssues(nrOfOccurrences);
    }

    /**
     * bookkeeping on the number of checks
     */
    public void incNrOfChecks() {
        nrOfItemsChecked += 1;
    }

    public void setNrOfChecks(int nrOfChecks) {
        nrOfItemsChecked = nrOfChecks;
    }

    /**
     * bookkeeping on the number of issues
     */
    public void incNrOfIssues() {
        nrOfIssues += 1;
    }

    public void addNrOfIssues(int nrOfIssuesToAdd) {
        nrOfIssues += nrOfIssuesToAdd;
    }


    /**
     * @return a description of what is checked
     */
    @Override
    public String description() {
        return whatIsChecked;
    }


    @Override
    public List<Finding> getFindings() {
        return findings;
    }

    /**
     * return a collection of finding-messages
     * (used to simplify testing)
     */
    public List<String> getFindingMessages() {
        return findings.stream().map(Finding::getWhatIsTheProblem).collect(Collectors.toList());
    }


    /**
     * @return (int) the nr of issues/findings found for this checkingResults.
     */
    public int nrOfProblems() {
        return nrOfIssues;
    }


    @Override
    public String toString() {
        return String.format ("Checking results for %s\n  %d '%s' items checked,\n  %d finding(s):\n    %s",
                whatIsChecked, nrOfItemsChecked, sourceItemName, nrOfIssues,
                findings.stream().map(Finding::toString).collect(Collectors.joining("\n    ")));
    }


}

/*=====================================================================
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
 =====================================================================*/

