package org.aim42.htmlsanitycheck.gradle


import org.gradle.api.Project
import org.gradle.language.base.plugins.LifecycleBasePlugin
import org.gradle.testfixtures.ProjectBuilder
import spock.lang.Specification

class HtmlSanityCheckPluginSpec extends Specification {

    def "plugin adds an htmlSanityCheck task to the project"() {
        given: "a project"
        Project project = ProjectBuilder.builder().build()

        when: "the plugin is applied"
        project.plugins.apply HtmlSanityCheckPlugin

        then: "the htmlSanityCheck task is added"
        project.tasks.named(HtmlSanityCheckPlugin.HTML_SANITY_CHECK) != null
    }

    def "htmlSanityCheck task has correct type and properties"() {
        given: "a project with the plugin applied"
        Project project = ProjectBuilder.builder().build()
        project.plugins.apply(HtmlSanityCheckPlugin)

        when: "retrieving the htmlSanityCheck task"
        def task = project.tasks.named(HtmlSanityCheckPlugin.HTML_SANITY_CHECK).get()

        then: "the task is of type HtmlSanityCheckTask"
        task instanceof HtmlSanityCheckTask

        and: "the task has the correct description"
        task.description == 'performs semantic checks on html files'

        and: "the task is in the verification group"
        task.group == LifecycleBasePlugin.VERIFICATION_GROUP
    }
}