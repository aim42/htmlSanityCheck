package org.aim42.htmlsanitycheck.tools

import spock.lang.Specification
import spock.lang.Unroll

// see end-of-file for license information


class WebSpec extends Specification {

    @Unroll
    def "success return codes contain #successCode"() {
        expect:
        Web.isSuccessCode(successCode)

        where:
        successCode << [200, 201, 202]
    }

    @Unroll
    def "warning return codes contain #warningCode"() {
        expect:
        Web.isWarningCode(warningCode)

        where:
        warningCode << [100, 101, 102]
    }

    @Unroll
    def "error codes contain #errorCode"() {
        expect:
        Web.isErrorCode(errorCode)

        where:
        errorCode << [400, 401, 402, 403, 404, 405, 406, 407, 408, 409, 410, 500, 501, 502, 503, 504, 505]
    }

    def "is internet connection available"() {

        // Stubbing in the following manner DOES NOT WORK!!!
        //given: "we're disconnected from the internet..."
        //Stub(NetUtil)
        //NetUtil.isInternetConnectionAvailable(_) >> false


        expect:
        Web.isInternetConnectionAvailable()
    }

    //@Unroll
    def "invalid chars in link"(boolean containsInvalidChars, String link) {
        expect:
        Web.containsInvalidChars(link) == containsInvalidChars

        where:

        containsInvalidChars | link
        false                | "#Context-Analysis"
        false                | "#Context_Analysis"
        false                | "#Context--Analysis"
        false                | "/forum/#!forum/randoop-discuss" // correct, as reported in #271

        true                 | "#Context Analysis" // regression test, contains blank
        true                 | "*Context-Analysis" // * is not allowed

    }


    @Unroll
    def "identify invalid links"(boolean isValid, String link) {
        expect:
        Web.isValidURL(link) == isValid

        where:

        isValid | link
        true    | "http://arc42.de/index.html"
        true    | "#localRef"
        true    | "file://images/image.jpg"

        false   | "#Context Analysis"
        false   | "//10.0.0.1/index.html"

    }

    @Unroll
    def "identify local resource links"(boolean isLocal, String link) {

        expect:
        Web.isLocalResource(link) == isLocal

        where:

        isLocal | link
        true    | "test.html"
        true    | "test.htm"
        true    | "TEST.HTM"
        true    | "test.docx"
        true    | "test.pdf"
        true    | "test.csv"
        true    | "jquery.js"
        true    | "./test.html"
        true    | "../test.html"
        true    | "file://test.html"
        true    | "file://test.html#anchor"
        true    | "dira/file.html"
        true    | "dira/dirb/file.html"
        true    | "dira/dirb/file.html#anchor"

        true    | "//index.html"   // browser assumes "file:" here
        true    | "//10.0.0.1/index.html" // invalid syntax, scheme missing, but browser defaults to "file"

        false   | "#Context Analysis" // regression test for  issue #70, contains blank-character

        false   | "http://index.html"
        false   | "mailto:alan.turing@acm.org"
        false   | ""
        false   | null
        false   | "ftp://acm.org"
        false   | "http://10.0.0.1/index.html"

        false   | "10.0.0.1/index.html"  // this is a valid REMOTE address, defaults to http or https

    }


    @Unroll
    def "check for valid ip address"(boolean isValidIP, String ipa) {
        expect:
        Web.isIP(ipa) == isValidIP

        where:

        isValidIP | ipa
        true      | "0.0.0.0"
        true      | "255.255.255.255"
        true      | "127.0.0.1"

        false     | "a.b.c.d"
        false     | "192.102.100"
        false     | "1.2.3.400"
        false     | "1.2.3"
        false     | "1.2"
        false     | "110"


    }
}