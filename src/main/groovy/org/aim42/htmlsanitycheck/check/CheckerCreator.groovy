package org.aim42.htmlsanitycheck.check

import org.aim42.htmlsanitycheck.check.UnknownCheckerException
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/** abstract factory to create Checker instances
 *
 */

class CheckerCreator {

    private final static Logger logger = LoggerFactory.getLogger(CheckerCreator.class);


    public static ArrayList<Checker> createCheckerClassesFrom(
            final Collection<Class> checkerClasses,
			final Map<String, Object> params = [:]) {

        ArrayList<Checker> checkers = new LinkedHashSet<Checker>( checkerClasses.size() )

        checkerClasses.each { checkerClass ->
            checkers.add( CheckerCreator.createSingleChecker( checkerClass, params ))

        }

        return checkers

    }


    // TODO: needs some params,

    public static Checker createSingleChecker( final Class checkerClass, final Map<String, Object> params = [:] ) {
        Checker checker

        // switch over all possible Checker classes
        // in case of new Checkers, this has to be adapted,
        // as Checker constructors will differ in minor details!

        // clearly violates the open-close principle

        switch ( checkerClass ) {
            case BrokenCrossReferencesChecker.class:
                checker = new BrokenCrossReferencesChecker(); break

            case BrokenHttpLinksChecker.class:
                checker = new BrokenHttpLinksChecker(); break

            case DuplicateIdChecker.class:
                checker = new DuplicateIdChecker(); break

            case ImageMapChecker.class:
                checker = new ImageMapChecker(); break

            case MissingAltInImageTagsChecker.class:
                checker = new MissingAltInImageTagsChecker(); break

            case MissingImageFilesChecker.class:
                checker = new MissingImageFilesChecker(); break

            case MissingLocalResourcesChecker.class:
                checker = new MissingLocalResourcesChecker();break


            default:
                logger.warn( "unknown Checker ${checkerClass.toString()}")
                throw new UnknownCheckerException( checkerClass.toString() )

        }
		
		params.each { key, value ->
			if (checker.hasProperty(key)) {
				checker[key] = value
			}
		}

        return checker

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

