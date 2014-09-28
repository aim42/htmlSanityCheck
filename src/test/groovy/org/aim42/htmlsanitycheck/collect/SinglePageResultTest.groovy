package org.aim42.htmlsanitycheck.collect

import org.aim42.htmlsanitycheck.check.Checker
import org.junit.Before
import org.junit.Test

class SinglePageResultTest extends GroovyTestCase {
    final static String HTML_HEAD = '<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN"> <head></head><html>'

    final static String HTML = """$HTML_HEAD<body><title>Faulty Dragon</title></body>
                   <h1 id="aim42">dummy-heading-1</h1>
                   <h2 id="aim42">duplicate id</h2>
                   <a> href="#nonexisting">broken cross reference</a>
                </html>"""


    private SingleCheckResults singleCheckResults
    private SinglePageResults  singlePageResults

    private File tmpFile

    @Before
    public void setUp() {

        // create file
        tmpFile = File.createTempFile("testfile", ".html")
        tmpFile.write( HTML )

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
    public void testSinglePageResultConstruction() {
        assertEquals("exptected temporary filename ", tmpFile.getCanonicalPath().toString(),
                             singlePageResults.pageFileName)


    }

    @Test
    public void testAddFindingToCheckingResult() {
        singleCheckResults.addFinding(new Finding("googlygoob"))

        String expected = "One finding expected"
        assertEquals(expected, 1, singleCheckResults.nrOfProblems())
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


