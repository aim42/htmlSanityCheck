package org.aim42.htmlsanitycheck

import spock.lang.Specification
import spock.lang.Unroll

// see end-of-file for license information


class ConfigurationSpec extends Specification {

    final def CI_FileCheck_Name = "fileToCheck"
    final def CI_FileCheck_Value = "index.html"

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
}
