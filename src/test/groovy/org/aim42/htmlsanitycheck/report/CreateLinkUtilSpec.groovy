package org.aim42.htmlsanitycheck.report

import spock.lang.Specification

// see end-of-file for license information


class CreateLinkUtilSpec extends Specification {

    def "inappropriate strings are converted to appropriate link-aware strings"(
            String bad, String good) {

        expect:
            // standard form of convertToLink method replaces whitespace by "X"
            CreateLinkUtil.convertToLink( bad ) == good

        where:
        bad                   | good
        "a.b"                 | "aXb"
        "onefile.html"        | "onefileXhtml"
        "/dir/file.htm"       | "XdirXfileXhtm"
        "file://test.htm"     | "fileXXXtestXhtm"
        "file:///a/b/test.txt"| "fileXXXXaXbXtestXtxt"
        "/a/b@§!.%&"          | "XaXbXXXXXX"

    }
}
