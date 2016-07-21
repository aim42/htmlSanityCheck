package org.aim42.htmlsanitycheck.suggest;

import net.ricecode.similarity.JaroWinklerStrategy;
import net.ricecode.similarity.SimilarityStrategy;
import net.ricecode.similarity.StringSimilarityService;
import net.ricecode.similarity.StringSimilarityServiceImpl;

import java.util.ArrayList;
import java.util.List;

/**
 * Finds suggestions for a target within a given list of options by applying string-similarity search
 * Example: target == "McDon", options == ["McAbb", "McDown", "Donna"],
 * a suggestions would be "McDown", as it is most similar to "McDon"
 */
public class Suggester {

    private static StringSimilarityService service;


    /**
     * finds a single suggestion for a "target" within a list of options.
     *
     * @param target  string not contained in options similarity-compared to every entry in options
     * @param options list of available options where the suggestion is taken from
     * @return a suggested alternative for target from the options
     */
     static String determineSingleSuggestion(String target, ArrayList<String> options) {

        service = new StringSimilarityServiceImpl( new JaroWinklerStrategy());

        return service.findTop(options, target).getKey();
    }


    /**
     * determines at most n suggestions.
     *
     * @param target  string that is not contained in options, similarity-compared to every entry in options
     * @param options list of available options where suggestions are taken from
     * @param n       number of suggestions to return. Should better be lower than options.size()
     * @return ArrayList of suggestions
     */
    public static List<String> determineNSuggestions(String target, ArrayList<String> options, int n) {
        service = new StringSimilarityServiceImpl( new JaroWinklerStrategy());

        // the "*." operator is the coolest thing in groovy:
        // applies the method to all elements of the collection (usually known as "map")
        return service.findBestN( options, target, n)*.getKey()
    }
}

/************************************************************************
 * This is free software - without ANY guarantee!
 * <p/>
 * <p/>
 * Copyright Dr. Gernot Starke, arc42.org
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * <p/>
 * **********************************************************************
 */

