package org.aim42.htmlsanitycheck

import org.gradle.testkit.runner.GradleRunner
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification
import spock.lang.Unroll

import static org.gradle.testkit.runner.TaskOutcome.FAILED
import static org.gradle.testkit.runner.TaskOutcome.SUCCESS

class HtmlSanityCheckTaskFunctionalTest extends Specification {
    private final static VALID_HTML = """<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN"><html><head></head><body></body><html>"""
    private final static INVALID_HTML = """<body><span id="id"/><span id="id"/></body> """
    private final static GRADLE_VERSIONS = ['3.2', '4.6']

    @Rule
    final TemporaryFolder testProjectDir = new TemporaryFolder()
    File buildDir
    File buildFile
    File htmlFile

    def setup() {
        buildDir = testProjectDir.newFolder("build")
        buildFile = testProjectDir.newFile('build.gradle')
        htmlFile = testProjectDir.newFile("test.html")
    }

    @Unroll
    def "can execute htmlSanityCheck task with Gradle version #gradleVersion"() {
        given:
        htmlFile << VALID_HTML
        buildFile << """
            plugins {
                id 'org.aim42.htmlsanitycheck'
            }

            htmlSanityCheck {
                sourceDir = file( "${htmlFile.parent}" )
                checkingResultsDir = file( "${buildDir.absolutePath}" )
                checkExternalLinks = false
            }
        """

        when:

        def result = GradleRunner.create()
                .withGradleVersion(gradleVersion)
                .withProjectDir(testProjectDir.root)
                .withPluginClasspath()
                .withArguments('htmlSanityCheck')
                .build()

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
            plugins {
                id 'org.aim42.htmlsanitycheck'
            }

            htmlSanityCheck {
                sourceDir = file( "${htmlFile.parent}" )
                checkingResultsDir = file( "${buildDir.absolutePath}" )
                checkExternalLinks = false
                failOnErrors = true
            }
        """

        when:

        def result = GradleRunner.create()
                .withGradleVersion(gradleVersion)
                .withProjectDir(testProjectDir.root)
                .withPluginClasspath()
                .withArguments('htmlSanityCheck')
                .buildAndFail()

        then:
        result.task(":htmlSanityCheck").outcome == FAILED
        result.output.contains("Found 1 error(s) on all checked pages")

        where:
        gradleVersion << GRADLE_VERSIONS
    }
}