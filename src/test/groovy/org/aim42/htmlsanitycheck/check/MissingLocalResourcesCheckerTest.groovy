package org.aim42.htmlsanitycheck.check

import org.aim42.htmlsanitycheck.collect.SingleCheckResults
import org.aim42.htmlsanitycheck.html.HtmlConst
import org.aim42.htmlsanitycheck.html.HtmlPage
import org.junit.Before
import org.junit.Test

class MissingLocalResourcesCheckerTest extends GroovyTestCase {


    Checker missingLocalResourcesChecker
    HtmlPage htmlPage
    SingleCheckResults collector

    @Before
    public void setUp() {
        collector = new SingleCheckResults()
    }


    /**
     * test that we can find a referenced local file in subdirectory
     */
    @Test
    public void testExistingLocalResourceIsFound() {

        def (File index, String fname, File d1) = createNestedTempDirWithFile()

        index << """<a href="d2/$fname">link to local resource"</a></body></html>"""

        assertTrue( "newly created html file exists", index.exists())


        // 4.) check
        htmlPage = new HtmlPage( index )

        missingLocalResourcesChecker = new MissingLocalResourcesChecker( baseDirPath: d1.canonicalPath )
        collector = missingLocalResourcesChecker.performCheck( htmlPage )

        // assert that no issue is found (== the local resource d2/fname.html is found)
        assertEquals( "expected zero finding", 0, collector.nrOfProblems())
        assertEquals( "expected one check", 1, collector.nrOfItemsChecked)
    }


    @Test
    /**
     * a "complex" local reference is an anchor of the form <a href="dir/file.html#anchor>...
     *
     */
    public void testExistingComplexLocalReferenceIsFound() {

       def (File index, String fname, File d1) = createNestedTempDirWithFile()

        index << """<a href="d2/$fname#anchor">link to local resource"</a></body></html>"""

        assertTrue( "newly created html file exists", index.exists())

        htmlPage = new HtmlPage( index )

        // pageToCheck shall contain ONE local resource / local-reference
        int nrOfLocalReferences = htmlPage.getAllHrefStrings().size()
        assertEquals( "expected one reference", 1, nrOfLocalReferences)

        // reference contained in pageToCheck shall be "d2/$fname#anchor"
        String localReference = htmlPage.getAllHrefStrings().first()
        assertEquals( "expected d2/fname#anchor", "d2/$fname#anchor", localReference)


        missingLocalResourcesChecker = new MissingLocalResourcesChecker( baseDirPath: d1.canonicalPath )
        collector = missingLocalResourcesChecker.performCheck( htmlPage )

        // assert that no issue is found
        // (== the existinglocal resource d2/fname.html is found)
        assertEquals( "expected zero finding", 0, collector.nrOfProblems())
        assertEquals( "expected one check", 1, collector.nrOfItemsChecked)

    }


    @Test
    public void testCrossReferenceIsNotChecked() {
        String HTML = """${HtmlConst.HTML_HEAD}
            <h1>dummy-heading-1</h1>
            <a href="#aim42">aim42</a>
           ${HtmlConst.HTML_END}"""

        htmlPage = new HtmlPage( HTML )

        missingLocalResourcesChecker = new MissingLocalResourcesChecker()
        collector = missingLocalResourcesChecker.performCheck( htmlPage )


        assertEquals( "expected zero finding", 0, collector.nrOfProblems())
        assertEquals( "expected zero checks", 0, collector.nrOfItemsChecked)

    }



    @Test
    public void testReferenceToLocalFileIsChecked() {
        String HTML = """${HtmlConst.HTML_HEAD}
            <h1>dummy-heading-1</h1>
            <a href="a_nonexisting_doc_uh7mP0R3YfR2__.pdf">nonexisting-doc</a>
            <a href="no/nex/ist/ing/dire/tory/test.pdf">another nonexisting download</a>
           ${HtmlConst.HTML_END}"""

        htmlPage = new HtmlPage( HTML )

        missingLocalResourcesChecker = new MissingLocalResourcesChecker()
        collector = missingLocalResourcesChecker.performCheck( htmlPage )

        int expected = 2

        assertEquals( "expected $expected findings", expected, collector.nrOfProblems())
        assertEquals( "expected $expected checks", expected, collector.nrOfItemsChecked)

    }


    /*
    helper to created nested directory structure
     */

    private List createNestedTempDirWithFile() {
        // 1.) create tmp directory d1 with subdir d2
        File d1 = File.createTempDir()
        File d2 = new File(d1, "/d2")
        d2.mkdirs()

        // 2.) create local resource file f2 in subdir d2
        final String fname = "fname.html"
        File f2 = new File(d2, fname) << HtmlConst.HTML_HEAD

        assertEquals("created an artificial file", "d2/fname.html",
                f2.canonicalPath - d1.canonicalPath - "/")

        assertTrue("newly created artificial file exists", f2.exists())

        // 3.) create tmp html file "index.html" linking to f2 in directory d1
        File index = new File(d1, "index.html") << HtmlConst.HTML_HEAD

        return [index, fname, d1]
    }


    @Test
    public void testCheckAbsoluteResource() {
		File tempFolder = File.createTempDir()

		File filesFolder = new File(tempFolder, "files")
		assertTrue("Cannot create ${filesFolder}", filesFolder.mkdirs())
		new File(filesFolder, "doc.pdf").bytes = new byte[0]
		
		File htmlFile = new File(tempFolder, "index.html")
		htmlFile.text = """<html><head><title>absolute resource test</title></head>
<body><p><a href="/files/doc.pdf" /></p></body>
</html>"""
		
        htmlPage = new HtmlPage( htmlFile )

        int nrOfLocalReferences = htmlPage.getAllHrefStrings().size()
        assertEquals( "expected one reference", 1, nrOfLocalReferences)

        missingLocalResourcesChecker = new MissingLocalResourcesChecker( baseDirPath: tempFolder)

        collector = missingLocalResourcesChecker.performCheck( htmlPage )

        int expected = 1
        int actual = collector.nrOfItemsChecked

        assertEquals("expected $expected resources, found $actual",
                expected, actual)

        expected = 0
        actual = collector.nrOfProblems()

        assertEquals( "extected $expected finding, found $actual",
                expected, actual)
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
