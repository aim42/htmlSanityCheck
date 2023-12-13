package org.aim42.htmlsanitycheck

import org.aim42.testutil.RandomStringGenerator
import spock.lang.Specification


// see end-of-file for license information

class RandomStringSpec extends Specification {

    String randomString

    def "randomString returns appropriate length"() {

        when:
            randomString = RandomStringGenerator.randomStringLength10()

        then:
            randomString.length() == 10

    }

}