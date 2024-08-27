package org.aim42.htmlsanitycheck.gradle

import org.aim42.htmlsanitycheck.MisconfigurationException
import org.gradle.testfixtures.ProjectBuilder
import spock.lang.Specification

class HtmlSanityCheckTaskSpec extends Specification {

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

    def "should set source directory and files"() {
        given:
        def project = ProjectBuilder.builder().build()
        def task = project.tasks.register('htmlSanityCheck', HtmlSanityCheckTask.class)
        def sourceDir = new File(project.DEFAULT_BUILD_DIR_NAME, "/resources/test/resources")
        sourceDir.mkdirs()
        def testFile = new File(sourceDir, "file-to-test.html")
        testFile << """<html></html>"""

        when:
        task.sourceDir = sourceDir
        task.httpSuccessCodes = [299]
        task.httpErrorCodes = [599]
        task.httpWarningCodes = [199]
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
}