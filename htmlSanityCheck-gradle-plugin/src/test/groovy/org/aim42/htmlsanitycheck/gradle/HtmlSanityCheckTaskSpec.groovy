package org.aim42.htmlsanitycheck.gradle

import org.aim42.htmlsanitycheck.MisconfigurationException
import org.gradle.api.GradleException
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import spock.lang.Specification

class HtmlSanityCheckTaskSpec extends HtmlSanityCheckBaseSpec {
    Project project
    HtmlSanityCheckTask task

    def setup () {
        project = ProjectBuilder.builder().withProjectDir(testProjectDir.root).build()
        task = project.tasks.register(HtmlSanityCheckPlugin.HTML_SANITY_CHECK, HtmlSanityCheckTask).get()
    }

    def "should initialize task with defaults"() {
        given:
        def project = ProjectBuilder.builder().build()
        def task = project.tasks.register('htmlSanityCheck', HtmlSanityCheckTask)

        expect:
        task.failOnErrors == false
        task.httpConnectionTimeout == 5000
        task.ignoreLocalHost == false
        task.ignoreIPAddresses == false
        task.checkingResultsDir == new File(project.DEFAULT_BUILD_DIR_NAME, '/reports/htmlSanityCheck/')
        task.junitResultsDir == new File(project.DEFAULT_BUILD_DIR_NAME, '/test-results/htmlSanityCheck/')
    }

    def "should work with simple file"() {
        given:
        htmlFile << VALID_HTML
        task.setSourceDir(testProjectDir.root)
        task.httpSuccessCodes = [299]
        task.httpErrorCodes = [599]
        task.httpWarningCodes = [199]

        when:
        task.sanityCheckHtml()

        then:
        task.sourceDocuments != null
    }

    def "should throw exception if configuration is invalid"() {
        given:
        def project = ProjectBuilder.builder().build()
        def task = project.tasks.register('htmlSanityCheck', HtmlSanityCheckTask.class)
        task.failOnErrors = true

        when:
        task.sanityCheckHtml()

        then:
        def e = thrown(MisconfigurationException)
        e.message.contains("source directory must not be null")
    }

    def "should fail on invalid HTML with failOnErrors"() {
        given:
        htmlFile << INVALID_HTML
        task.failOnErrors = true
        task.setSourceDir(testProjectDir.root)

        when:
        task.sanityCheckHtml()

        then:
        def e = thrown(GradleException)
        e.message.contains("Your build configuration included 'failOnErrors=true', and 1 error(s) were found on all checked pages.")
    }

}