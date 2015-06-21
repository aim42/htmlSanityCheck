package org.aim42.htmlsanitycheck.suggest;

import net.ricecode.similarity.SimilarityStrategy;
import net.ricecode.similarity.StringSimilarityService;

import java.util.ArrayList;

/**
 * Finds suggestions for a target within a given list of options by applying string-similarity search
 * Example: target == "McDon", options == ["McAbb", "McDown", "Donna"],
 * a suggestions would be "McDown", as it is most similar to "McDon"
 */
public class Suggester {

    private static SimilarityStrategy strategy;
    private static StringSimilarityService service;

    public static String determineSingleSuggestion( String target, ArrayList<String> options ) {
        return "";
    }


    /**
     * determines at most n suggestions.
     * @param target string that is not contained in options, similarity-compared to every entry in options
     * @param options list of available options where suggestions are taken from
     * @param n number of suggestions to return. Should better be lower than options.size()
     * @return
     */
    public static String determineNSuggestions( String target, ArrayList<String> options, int n) {
      return "";
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

