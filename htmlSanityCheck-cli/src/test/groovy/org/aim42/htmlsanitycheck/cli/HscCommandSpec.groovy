package org.aim42.htmlsanitycheck.cli

import org.junit.Rule
import org.junit.rules.TemporaryFolder
import picocli.CommandLine
import spock.lang.Specification
import spock.lang.Unroll

class HscCommandSpec extends Specification {

    private final static VALID_HTML = """<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN">
        <html>
            <head></head>
            <body>
                This <a href="https://tld.invalid/">Invalid TLD</a> should not make a problem!
            </body>
        <html>"""
    private final static INVALID_HTML = """<body><span id="id"/><span id="id"/></body> """

    @Rule
    TemporaryFolder testProjectDir = new TemporaryFolder()
    @Rule
    TemporaryFolder testResultsDir = new TemporaryFolder()

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
        HscCommand.HscRunner myAppRunner = Mock(HscCommand.HscRunner)
        def cmdLine = new CommandLine(new HscCommand(myAppRunner))

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
        HscCommand.main(args)

        then:
        errContent.toString().contains("Usage: hsc")
        errContent.toString().contains("Check HTML files for Sanity")
    }

    @Unroll
    // For misc. log levels
    def "test with empty source directory and verbosity #args"() {
        given:
        TemporaryFolder testProjectDir = new TemporaryFolder()
        testProjectDir.create()
        SecurityManager originalSecurityManager = System.getSecurityManager()
        SecurityManager mockSecurityManager = new NoExitSecurityMock(originalSecurityManager)
        System.setSecurityManager(mockSecurityManager)
        List<String> mainArgs = new ArrayList<>()
        if (args) {
            mainArgs.add(args)
        }
        mainArgs.add(testProjectDir.root as String)

        when:
        HscCommand.main(mainArgs as String[])

        then:
        mockSecurityManager.exitCalled == 1
        errContent.toString().contains("Please specify at least one src document")
        outContent.toString().contains("Usage: hsc")

        cleanup:
        testProjectDir.delete()

        where:
        args << [
                "",
                "-v",
                "-vv",
                "-vvv",
                "-vvvv"
        ]
    }

    def "test with valid HTML file"() {
        given:
        htmlFile << VALID_HTML
        String[] args = ["-e", "^.*\\.invalid.*", "-r", testResultsDir.root, testProjectDir.root]

        when:
        HscCommand.main(args)

        then:
        File resultFile = new File(testResultsDir.root, 'index.html')
        resultFile.exists()
        String result = resultFile.text
        result.toString().contains("<div class=\"infoBox success\" id=\"successRate\"><div class=\"percent\">100%</div>successful</div>")
    }

    def "test with invalid HTML file"() {
        given:
        htmlFile << INVALID_HTML
        String[] args = ["-r", testResultsDir.root, testProjectDir.root]

        when:
        HscCommand.main(args)

        then:
        File resultFile = new File(testResultsDir.root, 'index.html')
        resultFile.exists()
        String result = resultFile.text
        result.toString().contains("<div class=\"infoBox failures\" id=\"successRate\"><div class=\"percent\">0%</div>successful</div>\n")
    }

}
