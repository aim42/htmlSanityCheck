package org.aim42.htmlsanitycheck

import org.aim42.htmlsanitycheck.check.Checker
import spock.lang.Specification
import spock.lang.Unroll

// see end-of-file for license information


class ConfigurationSpec extends Specification {

    private final static HTML_HEADER = """<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN"> <head></head><html>"""

    private File tempDir
    private File htmlFile

    private Configuration myConfig

    void setup() {
        myConfig = new Configuration()
    }

    def "can overwrite http success codes"() {

        given: "configuration where 503 is success instead of error"
        int newSuccessCode = 503
        Set<Integer> httpSuccessCodes = [newSuccessCode]

        when: "we overwrite the standard Configuration with this value"
        myConfig.overrideHttpSuccessCodes(httpSuccessCodes)

        then: "503 IS now contained in successCodes"
        myConfig.getHttpSuccessCodes().contains(newSuccessCode)

        and: "503 is NOT contained in errorCodes"
        !myConfig.getHttpErrorCodes().contains(newSuccessCode)

        and: "503 is NOT contained in warningCodes"
        !myConfig.getHttpWarningCodes().contains(newSuccessCode)
    }

    def "can overwrite http warning codes"() {

        given: "configuration where 201 is warning instead of success"
        int newWarningCode = 201
        Set<Integer> httpWarningCodes = [newWarningCode]

        when: "we overwrite the standard Configuration with this value"
        myConfig.overrideHttpWarningCodes(httpWarningCodes)

        then: "201 IS now contained in warningCodes"
        myConfig.getHttpWarningCodes().contains(newWarningCode)

        and: "201 is NOT contained in errorCodes"
        !myConfig.getHttpErrorCodes().contains(newWarningCode)

        and: "201 is NOT contained in successCodes"
        !myConfig.getHttpSuccessCodes().contains(newWarningCode)

    }

    def "can overwrite http error codes"() {

        given: "configuration where 403 is error instead of warning"
        int newErrorCode = 403
        Set<Integer> httpErrorCodes = [newErrorCode]

        when: "we overwrite the standard Configuration with this value"
        myConfig.overrideHttpErrorCodes(httpErrorCodes)

        then: "403 IS now contained in errorCodes"
        myConfig.getHttpErrorCodes().contains(newErrorCode)

        and: "403 is NOT contained in successCodes"
        !myConfig.getHttpSuccessCodes().contains(newErrorCode)

        and: "403 is NOT contained in warningCodes"
        !myConfig.getHttpWarningCodes().contains(newErrorCode)

    }

    def "configuring a single html file is ok"() {

        given:
        tempDir = File.createTempDir()
        htmlFile = new File(tempDir, "a.html") << HTML_HEADER

        when: "we create a configuration with this single file"
        myConfig.setSourceConfiguration(tempDir, [htmlFile] as Set)
        myConfig.validate()

        then:
        htmlFile.exists()
        tempDir.isDirectory()
        tempDir.directorySize() > 0
        notThrown(Exception)

    }

    /*
     * giving no srcFiles and no srcDir is absurd...
     *
     */

    @Unroll
    def "configuring file #srcDocs in directory #srcDocs is absurd"() {

        given: "configuration with #srcDir and #srcDocs"
        myConfig.setSourceConfiguration(srcDir, srcDocs as Set)

        when: "configuration is validated..."
        myConfig.validate()

        then: "an exception is thrown"
        thrown MisconfigurationException

        where:
        srcDir                          | srcDocs
        null                            | null
        new File("/_non/exists_/d_ir/") | null
        new File("/_non/exists_/d_ir/") | []

        // existing but empty directory is absurd too...
        File.createTempDir()            | []
    }

    // this spec is a syntactic variation of the data (table-)driven test
    def "empty configuration makes no sense"() {

        when:
        myConfig.validate()

        then:
        thrown MisconfigurationException
    }

    @Unroll("checks to execute cannot be #checkersToExecute")
    def "checks to execute have to be non empty"() {
        given:
        tempDir = File.createTempDir()
        htmlFile = new File(tempDir, "a.html") << HTML_HEADER
        Configuration myConfig = Configuration.builder()
                .sourceDir(tempDir)
                .sourceDocuments([htmlFile] as Set)
                .checksToExecute(checkersToExecute)
                .build()

        when:
        myConfig.validate()

        then:
        def e = thrown(MisconfigurationException)
        e.message == "checks to execute have to be a non-empty list"

        where:
        checkersToExecute << [[] as List<Class<? extends Checker>>]
    }

    // The following methods only increase code coverage without providing any test at all
    def "cannot overwrite http error codes with null list"() {
        expect: "we overwrite the standard Configurations with null"
        myConfig.overrideHttpSuccessCodes(null)
        myConfig.overrideHttpWarningCodes(null)
        myConfig.overrideHttpErrorCodes(null)
    }

    def "can set and retrieve urlsToExclude"() {
        given: "a set of URLs to exclude"
        Set<String> urlsToExclude = ["(http|https)://example\\.com", "http://test\\.com"]

        when: "we set the exclude in the configuration"
        myConfig.exclude = urlsToExclude

        then: "the configuration should contain these URLs"
        myConfig.getExclude() == urlsToExclude
    }

    def "can set and retrieve hostsToExclude"() {
        given: "a set of hosts to exclude"
        Set<String> hostsToExclude = [".*example\\.com.*", ".*myhost\\.(com|org):2000"]

        when: "we set the exclude in the configuration"
        myConfig.exclude = hostsToExclude

        then: "the configuration should contain these hosts"
        myConfig.getExclude() == hostsToExclude
    }

    def "can set and retrieve urls and hosts to exclude"() {
        given: "a set of URLs and hosts to exclude"
        Set<String> exclusions = [
            "(http|https)://example\\.com",
            "http://test\\.com",
            ".*example2\\.com.*",
            ".*myhost\\.(com|org):23"
        ]

        when: "we set the exclude in the configuration"
        myConfig.exclude = exclusions

        then: "the configuration should contain these URLs and hosts"
        myConfig.getExclude() == exclusions
    }

}