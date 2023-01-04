package org.aim42.htmlsanitycheck.check

import org.aim42.htmlsanitycheck.Configuration
import org.aim42.htmlsanitycheck.html.HtmlPage
import spock.lang.Specification

/**
 * specifiy behavior of CheckerCreator factory
 */
class CheckerCreatorSpec extends Specification {


    private Class[] params

    String pMethod

    Configuration myConfig = new Configuration()

    def setup() {
        // ... takes HtmlPage as parameter
        params = [HtmlPage.class]
    }


    def "can create brokenCrossRefChecker instance"() {
        Checker oneChecker

        when:
        oneChecker = CheckerCreator.createSingleChecker(BrokenCrossReferencesChecker.class, myConfig)

        // performCheck method returns SingleCheckResults
        def fullDeclaration = "public final org.aim42.htmlsanitycheck.collect.SingleCheckResults org.aim42.htmlsanitycheck.check.SuggestingChecker.performCheck(org.aim42.htmlsanitycheck.html.HtmlPage)"

        pMethod = oneChecker.class.getMethod("performCheck", params)

        then:
        notThrown(NoSuchMethodException)
        pMethod == fullDeclaration
    }


    def "can create multiple checker instances"() {
        List<Checker> checkers
        Checker oneChecker

        setup:
        ArrayList<Class> checkerClazzes = [BrokenCrossReferencesChecker.class,
                                           DuplicateIdChecker.class]


        when:
        checkers = CheckerCreator.createCheckerClassesFrom( checkerClazzes, myConfig )
        then:
        checkers.size() == 2


        when:
        oneChecker = checkers.first()
        then:
        oneChecker instanceof BrokenCrossReferencesChecker


        when:
        pMethod = oneChecker.class.getMethod("performCheck", params)
        then:
        notThrown(NoSuchMethodException)


        when:
        oneChecker = checkers.last()
        then:
        oneChecker instanceof DuplicateIdChecker


        when:
        pMethod = oneChecker.class.getMethod("performCheck", params)
        then:
        notThrown(NoSuchMethodException)

    }


    def "can create checker with correct supertype"(Class checkerClazz, Class superClazz) {
        Checker oneChecker

        when:
        oneChecker = CheckerCreator.createSingleChecker(checkerClazz, myConfig)

        then:
        notThrown(NoSuchMethodException)
        oneChecker.class.getSuperclass() == superClazz

        where:

        checkerClazz                                                 | superClazz
        org.aim42.htmlsanitycheck.check.BrokenCrossReferencesChecker | org.aim42.htmlsanitycheck.check.SuggestingChecker

        // MIFChecker shall be SuggestingChecker - when issue #113 is fixed!
        org.aim42.htmlsanitycheck.check.MissingImageFilesChecker     | org.aim42.htmlsanitycheck.check.Checker

        org.aim42.htmlsanitycheck.check.DuplicateIdChecker           | org.aim42.htmlsanitycheck.check.Checker
        org.aim42.htmlsanitycheck.check.MissingAltInImageTagsChecker | org.aim42.htmlsanitycheck.check.Checker
        org.aim42.htmlsanitycheck.check.ImageMapChecker              | org.aim42.htmlsanitycheck.check.Checker


    }


    def "creating unknown checkers throws exception"() {
        when:
        Checker checker = CheckerCreator.createSingleChecker(java.lang.String.class, myConfig)

        then:
        thrown(UnknownCheckerException)
    }
	
	def "can create MissingImageFilesChecker instance"() {
        MissingImageFilesChecker oneChecker

        when:
        myConfig.addConfigurationItem( Configuration.ITEM_NAME_sourceDir, new File('.'))
        oneChecker = CheckerCreator.createSingleChecker(MissingImageFilesChecker.class, myConfig)

        // performCheck method returns SingleCheckResults
        def fullDeclaration = "public org.aim42.htmlsanitycheck.collect.SingleCheckResults org.aim42.htmlsanitycheck.check.Checker.performCheck(org.aim42.htmlsanitycheck.html.HtmlPage)"

        pMethod = oneChecker.class.getMethod("performCheck", params)

        then:'performCheck method is present'
        notThrown(NoSuchMethodException)
        pMethod == fullDeclaration
		and:'baseDir is present'
		oneChecker.baseDir
    }

	def "can create MissingLocalResourcesChecker instance"() {
        MissingLocalResourcesChecker oneChecker

        when:
        myConfig.addConfigurationItem( Configuration.ITEM_NAME_sourceDir, new File('.'))
        oneChecker = CheckerCreator.createSingleChecker(MissingLocalResourcesChecker.class, myConfig)

        // performCheck method returns SingleCheckResults
        def fullDeclaration = "public org.aim42.htmlsanitycheck.collect.SingleCheckResults org.aim42.htmlsanitycheck.check.Checker.performCheck(org.aim42.htmlsanitycheck.html.HtmlPage)"

        pMethod = oneChecker.class.getMethod("performCheck", params)

        then:'performCheck method is present'
        notThrown(NoSuchMethodException)
        pMethod == fullDeclaration
        and:'baseDir is present'
        oneChecker.baseDir
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

