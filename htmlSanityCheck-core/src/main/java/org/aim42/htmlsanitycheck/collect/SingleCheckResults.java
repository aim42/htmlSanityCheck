// see end-of-file for license information

package org.aim42.htmlsanitycheck.collect;

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
    public ArrayList<Finding> findings;
    String whatIsChecked;    // i.e. "Missing Local Images Check"
    // source-whatIsTheProblem is checked against target-whatIsTheProblem
    String sourceItemName;    // i.e. image-src-attribute, anchor/link
    String targetItemName;    // i.e. local-image-file, id/bookmark
    String generalRemark;     // i.e. "Internet not available"
    int nrOfItemsChecked;
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
        this.findings = new ArrayList<Finding>();
        this.generalRemark = "";
    }

    public String getWhatIsChecked() {
        return whatIsChecked;
    }

    public void setWhatIsChecked(String whatIsChecked) {
        this.whatIsChecked = whatIsChecked;
    }

    public String getSourceItemName() {
        return sourceItemName;
    }

    public void setSourceItemName(String sourceItemName) {
        this.sourceItemName = sourceItemName;
    }

    public String getTargetItemName() {
        return targetItemName;
    }

    public void setTargetItemName(String targetItemName) {
        this.targetItemName = targetItemName;
    }

    public String getGeneralRemark() {
        return generalRemark;
    }
    // nrOfIssues can be larger than findings.size(),
    // if some findings occur more than once

    public void setGeneralRemark(String generalRemark) {
        this.generalRemark = generalRemark;
    }

    public int getNrOfItemsChecked() {
        return nrOfItemsChecked;
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
     * add a single finding to the collection of Finding instances
     *
     * @param singleFinding
     */
    public void addFinding(Finding singleFinding) {
        findings.add(singleFinding);
        incNrOfIssues();
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

    public void addNrOfChecks(int nrOfChecksToAdd) {
        nrOfItemsChecked += nrOfChecksToAdd;
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
    public ArrayList<Finding> getFindings() {
        return findings;
    }

    /**
     * return a collection of finding-messages
     * (used to simplify testing)
     */
    public List<String> getFindingMessages() {
        return findings.stream().map(finding -> finding.getWhatIsTheProblem()).collect(Collectors.toList());

    }


    /**
     * @return (int) the nr of issues/findings found for this checkingResults.
     */
    public int nrOfProblems() {
        return nrOfIssues;
    }


    @Override
    public String toString() {
        int nrOfProblems = nrOfProblems();
        return "Checking results for $whatIsChecked" + '\n' +
                " $nrOfItemsChecked $sourceItemName checked," + '\n' +
                " $nrOfProblems finding(s)" + '\n' +
                findings.stream().map(it -> it.toString()).collect(Collectors.joining("\n"));


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

