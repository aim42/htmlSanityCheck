package org.aim42.htmlsanitycheck.gradle

import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification

class HtmlSanityCheckBaseSpec extends Specification {
    final static VALID_HTML = """<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN"><html><head></head><body></body><html>"""
    final static INVALID_HTML = """<body><span id="id"/><span id="id"/></body> """
    final static VALID_HTML_WITH_EXCLUDED_URL = """
        <html>
        <body>
        <a href="http://example.com/excluded">Excluded URL</a>
        <a href="http://example.com/included">Included URL</a>
        </body>
        </html>
    """
    final static VALID_HTML_WITH_EXCLUDED_HOST = """
        <html>
        <body>
        <a href="http://excluded.com/page">Excluded Host</a>
        <a href="http://included.com/page">Included Host</a>
        </body>
        </html>
    """

    @Rule
    TemporaryFolder testProjectDir = new TemporaryFolder()
    File sourceDir
    File buildDir
    File buildFile
    File htmlFile

    def setup() {
        buildDir = testProjectDir.newFolder("build")
        sourceDir = testProjectDir.newFolder("src")
        sourceDir.mkdirs()
        htmlFile = new File (sourceDir, "test.html")
    }

    protected void createBuildFile(String extendedTaskConfig = "") {
        // a note on writing paths to the build script on windows:
        // - the default file separator is a backslash
        // - as the path is written into a quoted string, backslashes should be quoted
        // - to avoid string manipulation or similar, we use URIs to avoid the problem
        //   (URIs consist of / instead of backslashes)
        buildFile = testProjectDir.newFile('build.gradle') << """
            plugins {
                id 'org.aim42.htmlSanityCheck'
            }

            htmlSanityCheck {
                sourceDir = file ("src")
                checkingResultsDir = file ("build")

                ${extendedTaskConfig}
            }
        """.stripIndent()
    }
}