package org.aim42.htmlsanitycheck.suggest

import org.aim42.testutil.RandomStringGenerator
import spock.lang.Specification
import spock.lang.Timeout

class SuggesterSpec extends Specification {

    private ArrayList<String> options
    private String target

    private List<String> suggestions


    void setup() {
        options = Arrays.asList(
                "Abomasnow", "Abra", "Absol", "Accelgor", "Aerodactyl", "Aggron",
                "Aipom", "Alakazam", "Alomomola", "Altaria", "Ambipom", "Amoonguss",
                "Ampharos", "Anorith", "Arbok", "Arcanine", "Arceus", "Archen", "Archeops",
                "Ariados", "Armaldo", "Aron", "Articuno", "Audino", "Axew", "Azelf",
                "Azumarill", "Azurill", "Bagon", "Baltoy", "Banette", "Barboach",
                "Basculin", "Bastiodon", "Bayleef", "Beartic", "Beautifly", "Beedrill",
                "Beheeyem", "Beldum", "Bellossom", "Bellsprout", "Bibarel", "Bidoof",
                "Bisharp", "Blastoise", "Blaziken", "Blissey", "Blitzle", "Boldore",
                "Bonsly", "Bouffalant", "Braviary", "Breloom", "Bronzong")

    }

    def "Determine One Plausible Suggestion"() {
        given:
        target = "Bastodon"

        when:
        String singleSuggestion = Suggester.determineSingleSuggestion(target, options)

        then:
        // get ONE result
        singleSuggestion != null

        // "Bastiodon" is plausible suggestion
        singleSuggestion == "Bastiodon"
    }


    def "Determine Multiple Suggestions"() {
        given:
        target = "Bastodon"

        when:
        List<String> multipleSuggestion = Suggester.determineNSuggestions(target, options, 2)

        then:
        // get two results
        multipleSuggestion.size() == 2

        // "Bastiodon" is plausible suggestion
        multipleSuggestion.get(0) == "Bastiodon"
        multipleSuggestion.get(1) == "Baltoy"
    }


    def "Find No Suggestion In Empty Option List"() {
        given:
        target = "Bastodon"
        def emptyOptions = new ArrayList<String>()

        when:
        List<String> multipleSuggestion = Suggester.determineNSuggestions(target, emptyOptions, 2)

        then:
        // get no results
        multipleSuggestion.size() == 0
    }

    @Timeout(1)
    // wait max one second
    def "Find Suggestion in Long Option List"() {
        final int HALFLENGTH = 1001
        ArrayList<String> longOptionList = new ArrayList<String>(2 * HALFLENGTH)

        given:
        target = "HtmlSanitY"
        String bestSuggestion = "HtmkSanity"   // two differences
        String secondSuggestion = "htmlSaqqqy" // three differences

        longOptionList.add(secondSuggestion)

        (1..HALFLENGTH).each() {
            longOptionList.add(RandomStringGenerator.randomString())
        }

        longOptionList.add(bestSuggestion)

        (1..HALFLENGTH).each() {
            longOptionList.add(RandomStringGenerator.randomString())
        }

        when:
        List<String> multipleSuggestion = Suggester.determineNSuggestions(target, longOptionList, 3)

        then:
        // get three results
        multipleSuggestion.size() == 3

        // longOption list is really long :-)
        longOptionList.size() == (2 * HALFLENGTH + 2)

        // does method find right suggestions?
        multipleSuggestion.get(0) == bestSuggestion
        multipleSuggestion.get(1) == secondSuggestion
    }


    // test behavior of suggesting image file names
    // with strings of different length
    def "image filename suggester test"() {
        final String target = "hsc-logo.png"
        final String bestOption = "hsc-logo.jpg"
        final String secondOption = "hsclogo.jpg"

        ArrayList<String> optionList = ["arc42-logo.jpg",
                                        "hsc-logo-shaded.png",
                                        bestOption,
                                        secondOption,
                                        "outside.jpg"]

        when:
        // suggest the two best matches from the optionList above
        List<String> multipleSuggestion = Suggester.determineNSuggestions(target, optionList, 2)

        then:
        multipleSuggestion.size() == 2
        multipleSuggestion.get(0) == bestOption
        multipleSuggestion.get(1) == secondOption

    }
}

/************************************************************************
 * This is free software - without ANY guarantee!
 *
 *
 * Copyright Dr. Gernot Starke, arc42.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 *********************************************************************** */
