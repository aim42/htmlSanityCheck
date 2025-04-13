package org.aim42.htmlsanitycheck.check

import org.aim42.htmlsanitycheck.Configuration
import org.aim42.htmlsanitycheck.collect.SingleCheckResults
import org.aim42.htmlsanitycheck.html.HtmlConst
import org.aim42.htmlsanitycheck.html.HtmlPage
import org.junit.Before
import org.junit.Test

import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertTrue

class MissingLocalResourcesCheckerTest {
    private Configuration myConfig

    @Before
    void setUp() {
        myConfig = new Configuration()
    }

    /**
     * test that we can find a referenced local file in subdirectory
     */
    @Test
    void testExistingLocalResourceIsFound() {
        def (File indexFile, File nestedFile, String fname, File rootDir, File subDir) = createNestedTempDirWithFile()
        indexFile << """<a href="subdir/$fname">link to local resource"</a></body></html>"""
        assertTrue("newly created html file exists", indexFile.exists())

        HtmlPage indexPage = new HtmlPage(indexFile)

        myConfig.setSourceConfiguration(rootDir, [indexFile] as Set)
        checkLocalResources(indexPage)
    }


    @Test
    /**
     * a "complex" local reference is an anchor of the form <a href="dir/file.html#anchor>...
     */
    void testExistingComplexLocalReferenceIsFound() {
        def (File indexFile, nestedFile, String fname, File rootDir, File subDir) = createNestedTempDirWithFile()
        indexFile << """<a href="subdir/$fname#anchor">link to local resource"</a></body></html>"""
        assertTrue("newly created html file exists", indexFile.exists())


        HtmlPage indexPage = new HtmlPage(indexFile)
        // pageToCheck shall contain ONE local resource / local-reference
        int nrOfLocalReferences = indexPage.getAllHrefStrings().size()
        assertEquals("expected one reference", 1, nrOfLocalReferences)

        // reference contained in pageToCheck shall be "subDir/$fname#anchor"
        String localReference = indexPage.getAllHrefStrings().first()
        assertEquals("expected subdir/fname#anchor", "subdir/$fname#anchor".toString(), localReference)

        myConfig.setSourceConfiguration(rootDir, [indexFile] as Set)
        checkLocalResources(indexPage)

        HtmlPage nestedPage = new HtmlPage(nestedFile)
        checkLocalResources(nestedPage)
    }

    private void checkLocalResources(HtmlPage indexPage) {
        MissingLocalResourcesChecker missingLocalResourcesChecker = new MissingLocalResourcesChecker(myConfig)
        SingleCheckResults collector = missingLocalResourcesChecker.performCheck(indexPage)

        // assert that no issue is found
        // (== the existinglocal resource subDir/fname.html is found)
        assertEquals("expected zero findings", 0, collector.nrOfProblems())
        assertEquals("expected one check", 1, collector.nrOfItemsChecked)
    }

    @Test
    void testPureCrossReferenceIsNotChecked() {
        String HTML = """${HtmlConst.HTML_HEAD}
            <h1>dummy-heading-1</h1>
            <a href="#aim42">aim42</a>
           ${HtmlConst.HTML_END}"""

        HtmlPage indexPage = new HtmlPage(HTML)

        MissingLocalResourcesChecker missingLocalResourcesChecker = new MissingLocalResourcesChecker(myConfig)
        SingleCheckResults collector = missingLocalResourcesChecker.performCheck(indexPage)

        assertEquals("expected zero finding", 0, collector.nrOfProblems())
        assertEquals("expected zero checks", 0, collector.nrOfItemsChecked)
    }


    @Test
    void testReferenceToLocalFileIsChecked() {
        String HTML = """${HtmlConst.HTML_HEAD}
            <h1>dummy-heading-1</h1>
            <a href="a_nonexisting_doc_uh7mP0R3YfR2__.pdf">nonexisting-doc</a>
            <a href="no/nex/ist/ing/dire/tory/test.pdf">another nonexisting download</a>
           ${HtmlConst.HTML_END}"""

        HtmlPage indexPage = new HtmlPage(HTML)

        myConfig.setSourceConfiguration(new File("."), [] as Set)
        MissingLocalResourcesChecker missingLocalResourcesChecker = new MissingLocalResourcesChecker(myConfig)
        SingleCheckResults collector = missingLocalResourcesChecker.performCheck(indexPage)

        int expectedChecks = 2
        assertEquals("expected $expectedChecks checks", expectedChecks, collector.nrOfItemsChecked)
        int expectedFindings = 2
        assertEquals("expected $expectedFindings findings", expectedFindings, collector.nrOfProblems())
    }


    /*
    helper to created nested directory structure
     */
    private static List createNestedTempDirWithFile() {
        // 1.) create tmp directory with subdir
        File rootDir = File.createTempDir()
        File subDir = new File(rootDir, "/subdir")
        subDir.mkdirs()

        // 2.) create local resource file f2 in subdir
        final String fname = "fname.html"
        File nestedFile = new File(subDir, fname) << """${HtmlConst.HTML_HEAD}
            <h1>Neste File</h1>
            <a href="../">root</a>
           ${HtmlConst.HTML_END}"""

        assertEquals("created a file in subdir",
                // unix: /subdir/fname.html
                // windows: \subdir\fname.html
                File.separator + "subdir" + File.separator + "fname.html",
                nestedFile.canonicalPath - rootDir.canonicalPath)

        assertTrue("newly created file in subdir exists", nestedFile.exists())

        // 3.) create tmp html file "index.html" linking to file in subdir
        File index = new File(rootDir, "index.html") << HtmlConst.HTML_HEAD << HtmlConst.HTML_END

        return [index, nestedFile, fname, rootDir, subDir]
    }

    @Test
    void testCheckAbsoluteResource() {
        File tempFolder = File.createTempDir()

        File filesFolder = new File(tempFolder, "files")
        assertTrue("Cannot create ${filesFolder}", filesFolder.mkdirs())
        new File(filesFolder, "doc.pdf").bytes = new byte[0]

        File htmlFile = new File(tempFolder, "index.html")
        htmlFile.text = """<html><head><title>absolute resource test</title></head>
<body><p><a href="/files/doc.pdf" /></p></body>
</html>"""

        myConfig.setSourceConfiguration(tempFolder, [htmlFile] as Set)
        HtmlPage indexPage = new HtmlPage(htmlFile)

        int nrOfLocalReferences = indexPage.getAllHrefStrings().size()
        assertEquals("expected one reference", 1, nrOfLocalReferences)

        MissingLocalResourcesChecker missingLocalResourcesChecker = new MissingLocalResourcesChecker(myConfig)

        SingleCheckResults collector = missingLocalResourcesChecker.performCheck(indexPage)

        int expectedResources = 1
        int actualResources = collector.nrOfItemsChecked

        assertEquals("expected $expectedResources resource(s), found $actualResources",
                expectedResources, actualResources)

        int expectedFindings = 0
        int actualFindings = collector.nrOfProblems()

        assertEquals("extected $expectedFindings finding(s), found $actualFindings",
                expectedFindings, actualFindings)
    }
}


/************************************************************************
 * This is free software - without ANY guarantee!
 *
 *
 * Copyright Dr. Gernot Starke and aim42 contributors
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
