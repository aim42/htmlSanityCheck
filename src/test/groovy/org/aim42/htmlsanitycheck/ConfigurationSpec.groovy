package org.aim42.htmlsanitycheck

import spock.lang.Specification
import spock.lang.Unroll

// see end-of-file for license information


class ConfigurationSpec extends Specification {

    Configuration myConfiguration

    final def CI_FileCheck_Name = "fileToCheck"
    final def CI_FileCheck_Value = "index.html"

    def setup() {
        myConfiguration = new Configuration()
    }

    /**
     * The very basic SmokeTest for configuration items
     * @return
     */
    def "can add and retrieve single config item"() {

        when: "we add a single configuration item"
        myConfiguration.addConfigurationItem(CI_FileCheck_Name, CI_FileCheck_Value)

        then: "there is a single entry in our configuration"
        myConfiguration.nrOfConfigurationItems() == 1

        and: "we can retrieve this item"
        myConfiguration.getConfigurationItemByName(CI_FileCheck_Name) == CI_FileCheck_Value

    }

    def "unconfigured item yields null result"() {
        given: " a single entry configuration"
        myConfiguration.addConfigurationItem(CI_FileCheck_Name, CI_FileCheck_Value)

        expect: "when a different config item is requested, null is returned"
        myConfiguration.getConfigurationItemByName("NonExistingItem") == null
    }

    @Unroll
    def "can add and retrieve item #itemName of type #itemValue.getClass()"() {
        when: "we add #itemName"
        myConfiguration.addConfigurationItem(itemName, itemValue)

        and: "we retrieve that value from our configuration"
        def value = myConfiguration.getConfigurationItemByName(itemName)


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

}
