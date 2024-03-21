package org.aim42.htmlsanitycheck.report

import org.aim42.htmlsanitycheck.collect.PerRunResults
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

import java.nio.file.Files

import static org.junit.Assert.assertTrue
import static org.junit.Assert.fail

class HtmlReporterTest {

    private HtmlReporter htmlReporter
    private PerRunResults runResults

    @Rule
    public TemporaryFolder tempDir = new TemporaryFolder()

    @Before
    void setUp() {
        runResults = new PerRunResults()
        htmlReporter = new HtmlReporter(runResults, tempDir.getRoot().getAbsolutePath())
    }

    @Test
    void testInitReport() {
        htmlReporter.initReport()
        File reportFile = new File(tempDir.getRoot(), "index.html")
        assertTrue("Report file should be created", reportFile.exists())
    }

    @Test
    void testWrite() {
        htmlReporter.initReport()
        htmlReporter.write("Test content")
        htmlReporter.closeReport()
        File reportFile = new File(tempDir.getRoot(), "index.html")
        try {
            String content = new String(Files.readAllBytes(reportFile.toPath()))
            assertTrue("Report file should contain written content", content.contains("Test content"))
        } catch (IOException e) {
            fail("Failed to read report file: ${e}")
        }
    }

    // Add more test methods for other methods in HtmlReporter
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
