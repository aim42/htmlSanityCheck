package org.aim42.htmlsanitycheck.check

import org.aim42.htmlsanitycheck.html.HtmlPage
import spock.lang.Specification

/**
 * specifiy behavior of CheckerCreator factory
 */
class CheckerCreatorSpec extends Specification {

    private Collection<Checker> checkers
    private Checker oneChecker

    private Class[] params

    String  pMethod
    def setup() {
        // ... takes HtmlPage as parameter
      params = [HtmlPage.class]

    }



    def "can create brokenCrossRefChecker instance"() {

        when:
        oneChecker = CheckerCreator.createSingleChecker( BrokenCrossReferencesChecker.class )

        // performCheck method returns SingleCheckResults
        def fullDeclaration = "public final org.aim42.htmlsanitycheck.collect.SingleCheckResults org.aim42.htmlsanitycheck.check.SuggestingChecker.performCheck(org.aim42.htmlsanitycheck.html.HtmlPage)"

        pMethod = oneChecker.class.getMethod("performCheck", params)

        then:
        notThrown( NoSuchMethodException )
        pMethod == fullDeclaration
    }


    def "can create multiple checker instances"() {

        when:
        checkers = CheckerCreator.createCheckerClassesFrom(
                [BrokenCrossReferencesChecker.class, DuplicateIdChecker.class].toSet()
        )

        oneChecker = checkers.first()
        //pMethod = checker.class.getMethod("performCheck", params)

        then:
        checkers.size() == 2

/*
        then:
        checker instanceof BrokenCrossReferencesChecker
        notThrown( NoSuchMethodException )

        when:
          pMethod = checkers.last().class.getMethod("performCheck", params)

        then:
        notThrown( NoSuchMethodException)
*/
    }


    def "can create single checker"( Class checkerClazz, Class superClazz ) {

    }

    def "creating unknown checkers throws exception"() {
        when:
        Checker checker = CheckerCreator.createSingleChecker( java.lang.String.class )

        then:
        thrown( UnknownCheckerException )
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

