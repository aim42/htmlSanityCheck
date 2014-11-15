package org.aim42.htmlsanitycheck.collect

import org.junit.Before
import org.junit.Test

class SinglePageResultsTest extends GroovyTestCase {
    final static String HTML_HEAD = '<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN"> <head></head><html>'

    final static String HTML = """$HTML_HEAD<body><title>Faulty Dragon</title></body>
                   <h1 id="aim42">dummy-heading-1</h1>
                   <h2 id="aim42">duplicate id</h2>
                   <a> href="#nonexisting">broken cross reference</a>
                </html>"""


    private SingleCheckResults singleCheckResults
    private SinglePageResults singlePageResults

    private File tmpFile

    @Before
    public void setUp() {

        // create file
        tmpFile = File.createTempFile("testfile", ".html")
        tmpFile.write(HTML)

        singlePageResults = new SinglePageResults(
                pageFileName: tmpFile.canonicalPath.toString(),
                pageTitle: "Faulty Dragon",
                pageSize: 233
        )

        singleCheckResults = new SingleCheckResults(
                whatIsChecked: "Broken Internal Links Check",
                sourceItemName: "href",
                targetItemName: "missing id"
        )

    }

    @Test
    public void testSinglePageResultForOnePage() {
        assertEquals("exptected temporary filename ", tmpFile.getCanonicalPath().toString(),
                singlePageResults.pageFileName)

        int nrOfChecks = 10

        for (int findingNr = 0; findingNr < nrOfChecks; findingNr++) {

            singleCheckResults.with {
                addFinding(new Finding("Finding #$findingNr"))
                incNrOfChecks()
            }
        }

        assertEquals("expect $nrOfChecks item checked on SingleCheckResults",
                nrOfChecks, singleCheckResults.nrOfItemsChecked)
        assertEquals("expect $nrOfChecks findings on SingleCheckResults",
                nrOfChecks, singleCheckResults.nrOfProblems())

        singlePageResults.addResultsForSingleCheck(singleCheckResults)

        assertEquals("expected $nrOfChecks checks on SinglePageChecks",
                nrOfChecks, singlePageResults.nrOfItemsCheckedOnPage())
        assertEquals("expected $nrOfChecks findings on SinglePageChecks",
                nrOfChecks, singlePageResults.nrOfFindingsOnPage())

    }

    @Test
    public void testAddFindingToCheckingResult() {

        // we produce some checkers with some findings and many checks...

        int nrOfCheckers = 4
        int nrOfFindings = 10
        int nrOfChecks = 50

        SingleCheckResults scr

        // in outer loop, create SingleCheckResults
        for (int checker = 0; checker < nrOfCheckers; checker++) {

            scr = new SingleCheckResults(
                    whatIsChecked: "Dummy Check $checker",
                    sourceItemName: "source-$checker",
                    targetItemName: "target-$checker"
            )

            // add some findings
            for (int findingNr = 0; findingNr < nrOfFindings; findingNr++) {
                scr.addFinding(new Finding("Finding #$findingNr"))
            }

            // now set the nr of checks
            for (int i = 0; i < nrOfChecks; i++) {
                scr.incNrOfChecks()
            }

            // now add this scr instance to SinglePageResults
            singlePageResults.addResultsForSingleCheck( scr )

        }

        // now we've pretty high expectations concerning the nr of checks and findings...

        // assert nr of checkers

        assertEquals("expected $nrOfCheckers on SinglePageChecks",
                nrOfCheckers, singlePageResults.howManyCheckersHaveRun() )

        int expectedNrOfFindings =nrOfFindings * nrOfCheckers
        assertEquals("expected $expectedNrOfFindings findings on SinglePageChecks",
                expectedNrOfFindings, singlePageResults.nrOfFindingsOnPage())

        int expectedNrOfChecks = nrOfCheckers * nrOfChecks
        assertEquals( "expected $expectedNrOfChecks checks on SinglePageChecks",
                expectedNrOfChecks, singlePageResults.nrOfItemsCheckedOnPage())
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


