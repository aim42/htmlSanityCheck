package org.aim42.htmlsanitycheck

import org.gradle.testkit.runner.GradleRunner
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification
import spock.lang.Unroll

import static org.gradle.testkit.runner.TaskOutcome.FAILED
import static org.gradle.testkit.runner.TaskOutcome.SUCCESS

class HtmlSanityCheckTaskFunctionalTest extends Specification {
    private final static VALID_HTML = """<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN"><html><head></head><body></body><html>"""
    private final static INVALID_HTML = """<body><span id="id"/><span id="id"/></body> """
    private final static GRADLE_VERSIONS = [ // 6.x or older does not work!
                                            '7.6.3', // latest 7.x
                                            '8.0.2', '8.1.1', '8.2.1', '8.3', '8.4', '8.5', '8.6' // all 8.x (latest patches)
                                           ]

    @Rule
    TemporaryFolder testProjectDir = new TemporaryFolder()
    File buildDir
    File buildFile
    File htmlFile

    def setup() {
        buildDir = testProjectDir.newFolder("build")
        htmlFile = testProjectDir.newFile("test.html")
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
                sourceDir = file( "${htmlFile.parentFile.toURI().path}" )
                checkingResultsDir = file( "${buildDir.toURI().path}" )
            }
        """
    }

    @Unroll
    def "can execute htmlSanityCheck task with Gradle version #gradleVersion"() {
        given:
        htmlFile << VALID_HTML

        when:

        def result = runnerForHtmlSanityCheckTask(gradleVersion).build()

        then:
        result.task(":htmlSanityCheck").outcome == SUCCESS

        where:
        gradleVersion << GRADLE_VERSIONS
    }

    @Unroll
    def "invalid HTML fails build with failOnErrors=true and Gradle version #gradleVersion"() {
        given:
        htmlFile << INVALID_HTML
        buildFile << """
            htmlSanityCheck {
                failOnErrors = true
            }
        """

        when:

        def result = runnerForHtmlSanityCheckTask(gradleVersion).buildAndFail()

        then:
        result.task(":htmlSanityCheck").outcome == FAILED
        result.output.contains("1 error(s) were found on all checked pages")

        where:
        gradleVersion << GRADLE_VERSIONS
    }

    @Unroll
    def "can specify a subset of files in source directory to be checked with Gradle version #gradleVersion"() {
        given:
        htmlFile << VALID_HTML
        testProjectDir.newFile("test-invalid.html") << INVALID_HTML
        buildFile << """
            htmlSanityCheck {
                sourceDocuments = fileTree(sourceDir) {
                    include '**/$htmlFile.name'
                } 
                failOnErrors = true
            }
        """

        when:
        def result = runnerForHtmlSanityCheckTask(gradleVersion).build()

        then:
        result.task(":htmlSanityCheck").outcome == SUCCESS

        where:
        gradleVersion << GRADLE_VERSIONS
    }

    @Unroll
    def "can select a subset of all checks to be performed with Gradle version #gradleVersion"() {
        given:
        htmlFile << VALID_HTML
        buildFile << """
            import org.aim42.htmlsanitycheck.check.AllCheckers

            htmlSanityCheck {
                checkerClasses = [AllCheckers.CHECKER_CLASSES.first()]
            }
        """

        when:
        runnerForHtmlSanityCheckTask(gradleVersion).build()
        def htmlReportFile = new File(testProjectDir.root, "build/index.html")

        then:
        new HtmlReport(htmlReportFile).fileResults*.checkCount == [1]

        where:
        gradleVersion << GRADLE_VERSIONS
    }

    private GradleRunner runnerForHtmlSanityCheckTask(String gradleVersion) {
        GradleRunner.create()
            .withGradleVersion(gradleVersion)
            .withProjectDir(testProjectDir.root)
            .withPluginClasspath()
            .withArguments('htmlSanityCheck')
    }

    private static class HtmlReport {
        final List<HtmlReportFileResults> fileResults

        HtmlReport(File file) {
            def document = Jsoup.parse(file.text)
            fileResults = document.select("h1[id]").collect { new HtmlReportFileResults(it) }
        }
    }

    private static class HtmlReportFileResults {
        final int checkCount

        HtmlReportFileResults(Element element) {
            def fileElements = nextSiblingsUntil(element, "h1[id]")
            checkCount = fileElements.findAll { it.is("div.success") || it.is("div.failures") }.size()
        }

        private static List<Element> nextSiblingsUntil(Element element, String cssSelector) {
            def elements = []
            def next = element.nextElementSibling()
            def matchFound = next.is(cssSelector)
            while (next && !matchFound) {
                elements << next
                next = next.nextElementSibling()
                matchFound = next.is(cssSelector)
            }
            elements
        }
    }
}