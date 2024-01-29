package org.aim42.inet

import org.aim42.htmlsanitycheck.tools.Web
import spock.lang.Specification
import spock.lang.Unroll


// see end-of-file for license information


class NetUtilSpec extends Specification {

    @Unroll
    def "success return codes contain #successCode"() {
        expect:
        successCode in Web.HTTP_SUCCESS_CODES

        where:
        successCode << [200,201,202]
    }

    @Unroll
    def "error codes contain #errorCode"() {
        expect:
        errorCode in Web.HTTP_ERROR_CODES

        where:
        errorCode << [400,401,402,403,404,405,406,407,408,409,410,500,501,502,503,504,505]
    }

    def "is internet connection available"() {

        // Stubbing in the following manner DOES NOT WORK!!!
        //given: "we're disconnected from the internet..."
        //Stub(NetUtil)
        //NetUtil.isInternetConnectionAvailable(_) >> false


        expect:
        Web.isInternetConnectionAvailable() == true
    }
}