package org.aim42.htmlsanitycheck

import spock.lang.Specification

// see end-of-file for license information


class MisconfigurationExceptionSpec extends Specification {

    def "can create Misconfiguration exception with just a message"() {
        when:
        throwMisConfEx("just a test")

        then:
        thrown MisconfigurationException
    }

    def "can create Misconfiguration exception with message and file"() {
        when:
        throwMisConfExWithFile()

        then:
        thrown MisconfigurationException
    }



    private static void throwMisConfExWithFile() {
        throw new MisconfigurationException("dummy message", new File("test.txt"))
    }

    private static void throwMisConfEx(String message) {
        throw new MisconfigurationException(message)
    }
}
