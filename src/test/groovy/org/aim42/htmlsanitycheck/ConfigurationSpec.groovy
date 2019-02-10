package org.aim42.htmlsanitycheck

import spock.lang.Specification
import spock.lang.Unroll

// see end-of-file for license information


class ConfigurationSpec extends Specification {

    final def CI_FileCheck_Name = "fileToCheck"
    final def CI_FileCheck_Value = "index.html"

    private final static HTML_HEADER = """<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN"> <head></head><html>"""

    private File tempDir
    private File htmlFile
    private File nonHtmlFile

    private Configuration myConfig

    void setup() {
        myConfig = new Configuration()
    }

    /**
     * The very basic SmokeTest for configuration items
     * @return
     */
    def "can add and retrieve single config item"() {

        when: "we add a single configuration item"
        myConfig.addConfigurationItem(CI_FileCheck_Name, CI_FileCheck_Value)

        then: "we can retrieve this item"
        myConfig.getConfigItemByName(CI_FileCheck_Name) == CI_FileCheck_Value

    }

    def "checks if item is already present in configuration"() {
        when: "we configure a single item"
        myConfig.addConfigurationItem(CI_FileCheck_Name, "test")

        then: "a check finds this item"
        myConfig.checkIfItemPresent(CI_FileCheck_Name) == true
    }


    def "configuration item can be overwritten"() {
        String oneITEM = "oneITEM"

        given: "an (int) item is configured"
        myConfig.addConfigurationItem(oneITEM, 10)

        when: "this item is configured again!"
        myConfig.addConfigurationItem(oneITEM, 42)

        then: "only the last value is contained in Configuration"
        myConfig.getConfigItemByName(oneITEM) == 42

    }


    def "not configured item yields null result"() {
        given: " a single entry configuration"
        myConfig.addConfigurationItem(CI_FileCheck_Name, CI_FileCheck_Value)

        expect: "when a different config item is requested, null is returned"
        myConfig.getConfigItemByName("NonExistingItem") == null
    }

    @Unroll
    def "can add and retrieve item #itemName of type #itemValue.getClass()"() {
        when: "we add #itemName"
        myConfig.addConfigurationItem(itemName, itemValue)

        and: "we retrieve that value from our configuration"
        def value = myConfig.getConfigItemByName(itemName)


        then: "we retrieve the correct value"
        value == itemValue

        and: "that value has the correct type"
        value.getClass() == itemValue.getClass()

        where:

        itemName           | itemValue
        "CI_FileName"      | "index.html"
        "CI_NumberSmall"   | 42
        "CI_Number2Bigger" | 80.000
        "CI_Dir"           | "/report/htmlchecks/"
        "CI_Bool"          | false
        "CI_Map"           | [Warning: [300, 301, 305], Error: [400, 404]]
        "CI_List"          | ["https://arc42.org", "https://aim42.org"]
    }


    def "can overwrite http success codes"() {

        given: "configuration where 503 is success instead of error"
        int newSuccessCode = 503
        ArrayList<Integer> httpSuccessCodes = [newSuccessCode]

        when: "we overwrite the standard Configuration with this value"
        myConfig.overwriteHttpSuccessCodes( httpSuccessCodes )

        then: "503 IS now contained in successCodes"
        myConfig.getConfigItemByName(Configuration.ITEM_NAME_httpSuccessCodes).contains(newSuccessCode)

        and: "503 is NOT contained in errorCodes"
        !myConfig.getConfigItemByName(Configuration.ITEM_NAME_httpErrorCodes).contains(newSuccessCode)

        and: "503 is NOT contained in warningCodes"
        !myConfig.getConfigItemByName(Configuration.ITEM_NAME_httpWarningCodes).contains(newSuccessCode)

    }

    def "ignore null values in configuration"() {
        final String itemName = "NonExistingItem"

        when: "we try to add a null value to a config item"
        myConfig.addConfigurationItem(itemName, null)

        then: "this value is NOT added to configuration"
        myConfig.checkIfItemPresent( itemName ) == false
    }

    def "avoid overriding config values with null"() {
        final String itemName = "SomeItem"

        given: "someItem with value 42"
        myConfig.addConfigurationItem(itemName, 42)

        when: "we try to overwrite itemName with null value"
        myConfig.addConfigurationItem(itemName, null)

        then: "the result is still 42"
        myConfig.getConfigItemByName(itemName) == 42
    }

    def "configuring a single html file is ok"() {

        given:
        tempDir = File.createTempDir()
        htmlFile = new File(tempDir, "a.html") << HTML_HEADER

        when: "we create a configuration with this single file"
        myConfig.addSourceFileConfiguration(tempDir, [htmlFile])

        myConfig.isValid()

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
        myConfig.addSourceFileConfiguration(srcDir, srcDocs)

        when: "configuration is validated..."
        def tmpResult = myConfig.isValid()

        then: "an exception is thrown"
        thrown MisconfigurationException


        where:

        srcDir                        | srcDocs
        null                          | null
        new File("/_non/exis_/d_ir/") | null
        new File("/_non/exis_/d_ir/") | []

        // existing but empty directory is absurd too...
        File.createTempDir()          | []

    }

    // this spec is a syntactic variation of the data (table-)driven test
    def "empty configuration makes no sense"() {

        when:
        myConfig.isValid()

        then:
        thrown MisconfigurationException
    }

    // prefixOnlyHrefExtensions can be reconfigured
    def "prefixOnlyHrefExtensions can be overwritten"() {
        def newExtensions = ["html", "htm" ]

        when: "prefixOnlyHrefExtensions are overwritten and read"
            myConfig.overwritePrefixOnlyHrefExtensions( newExtensions )

            def prefixOnlyHrefExtensions = myConfig.getConfigItemByName( Configuration.ITEM_NAME_prefixOnlyHrefExtensions)

        then: "prefixOnlyExtensions contain only new elements"

            prefixOnlyHrefExtensions.size() == newExtensions.size()

            prefixOnlyHrefExtensions.contains( newExtensions.get(0))
    }

    @Unroll("checks to execute cannot be #checksToExecute")
    def "checks to execute have to be a non empty collection"() {
        given:
        tempDir = File.createTempDir()
        htmlFile = new File(tempDir, "a.html") << HTML_HEADER
        myConfig.addConfigurationItem(Configuration.ITEM_NAME_sourceDir, tempDir)
        myConfig.addConfigurationItem(Configuration.ITEM_NAME_sourceDocuments, [htmlFile])

        and:
        myConfig.addConfigurationItem(Configuration.ITEM_NAME_checksToExecute, checksToExecute)

        when:
        myConfig.valid

        then:
        def e = thrown(MisconfigurationException)
        e.message == "checks to execute have to be a non empty collection"

        where:
        checksToExecute << [[], "not a collection"]
    }

}
