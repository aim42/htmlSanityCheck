package org.aim42.htmlsanitycheck.cli

import org.junit.Rule
import org.junit.rules.TemporaryFolder
import picocli.CommandLine
import spock.lang.Specification
import spock.lang.Unroll

import java.nio.file.Files

class MainCliSpec extends Specification {

    private final static VALID_HTML = """<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN"><html><head></head><body></body><html>"""
    private final static INVALID_HTML = """<body><span id="id"/><span id="id"/></body> """

    @Rule
    TemporaryFolder testProjectDir = new TemporaryFolder()
    File htmlFile
    ByteArrayOutputStream outContent
    ByteArrayOutputStream errContent

    def setup() {
        outContent = new ByteArrayOutputStream()
        errContent = new ByteArrayOutputStream()
        System.setOut(new PrintStream(outContent))
        System.setErr(new PrintStream(errContent))

        htmlFile = testProjectDir.newFile("test.html")
    }

    def cleanup() {
        System.setOut(System.out)
        System.setErr(System.err)
        System.setSecurityManager(null)
        testProjectDir.delete()
    }

    @Unroll
    def "test hsc with #args"() {
        given:
        Main.MainRunner myAppRunner = Mock(Main.MainRunner)
        def cmdLine = new CommandLine(new Main(myAppRunner))

        when:
        def exitCode = cmdLine.execute(args.split())

        then:
        exitCode == expectedExitCode
        (runnerWasCalled ? 1 : 0) * myAppRunner.run()

        where:
        args                        | expectedExitCode | runnerWasCalled
        "-h"                        | 2                | false
        "--help"                    | 2                | false
        "-V"                        | 0                | true
        "--version"                 | 0                | true
        ""                          | 0                | true
        "."                         | 0                | true
        "-r /tmp/results"           | 0                | true
        "--resultsDir /tmp/results" | 0                | true
    }

    def "test main method with -h argument"() {
        given:
        String[] args = ["-h"]

        when:
        Main.main(args)

        then:
        errContent.toString().contains("Usage: hsc")
        errContent.toString().contains("Check HTML files for Sanity")
    }

    def "test with empty source directory"() {
        given:
        TemporaryFolder testProjectDir = new TemporaryFolder()
        testProjectDir.create()
        SecurityManager originalSecurityManager = System.getSecurityManager()
        SecurityManager mockSecurityManager = new NoExitSecurityMock(originalSecurityManager)
        System.setSecurityManager(mockSecurityManager)
        String[] args = [testProjectDir.getRoot()]

        when:
        Main.main(args)

        then:
        mockSecurityManager.exitCalled == 1
        errContent.toString().contains("Please specify at least one src document")
        outContent.toString().contains("Usage: hsc")

        cleanup:
        testProjectDir.delete()
    }

    def "test with valid HTML file"() {
        given:
        htmlFile << VALID_HTML
        String[] args = [testProjectDir.getRoot()]

        when:
        Main.main(args)

        then:
        outContent.toString().contains("found 0 issue, 100% successful.")
    }


}
