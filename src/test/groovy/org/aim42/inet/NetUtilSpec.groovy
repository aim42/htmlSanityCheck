package org.aim42.inet

import spock.lang.Specification
import spock.lang.Unroll


// see end-of-file for license information


class NetUtilSpec extends Specification {

    @Unroll
    def "success return codes contain #successCode"() {
        expect:
        successCode in NetUtil.HTTP_SUCCESS_CODES

        where:
        successCode << [200,201,202]
    }



    def "is internet connection available"() {

        // Stubbing in the following manner DOES NOT WORK!!!
        //given: "we're disconnected from the internet..."
        //Stub(NetUtil)
        //NetUtil.isInternetConnectionAvailable(_) >> false


        expect:
        NetUtil.isInternetConnectionAvailable() == true
    }
}