package org.aim42.htmlsanitycheck.cli


import picocli.CommandLine
import spock.lang.Specification
import spock.lang.Unroll

class MainCliSpec extends Specification {

    Main.MainRunner myAppRunner = Mock(Main.MainRunner)

    @Unroll
    def "test hsc with #args"() {
        given:
        def cmdLine = new CommandLine(new Main(myAppRunner))

        when:
        def exitCode = cmdLine.execute(args.split())

        then:
        exitCode == expectedExitCode
        (runnerWasCalled ? 1 : 0) * myAppRunner.run()

        where:
        args                         | expectedExitCode | runnerWasCalled
        "-h"                         | 0                | false
        "--help"                     | 0                | false
        "-V"                         | 0                | false
        "--version"                  | 0                | false
        ""                           | 0                | true
        "."                          | 0                | true
        "-r /tmp/results"            | 0                | true
        "--resultsDir /tmp/results"  | 0                | true
    }
}
