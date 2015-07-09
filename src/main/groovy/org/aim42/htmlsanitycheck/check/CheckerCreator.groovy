package org.aim42.htmlsanitycheck.check

import org.slf4j.Logger
import org.slf4j.LoggerFactory

/** abstract factory to create Checker instances
 *
 */

class CheckerCreator {

    private static Logger logger = LoggerFactory.getLogger(CheckerCreator.class);


    public static List<Checker> createCheckerClassesFrom( final Set<Class> checkerCollection ) {
        ArrayList checkers = new ArrayList<Checker>( checkerCollection.size() )
        checkerCollection.each { checkerClass ->
            checkers.add( CheckerCreator.createSingleChecker( checkerClass ))

        }

    }


    // TODO: needs some params, like pageToCheck, baseDir, checkExternalResources...

    private static Checker createSingleChecker( Class checkerClass ) {
        Checker checker

        // switch over all possible Checker classes
        // in case of new Checkers, this has to be adapted,
        // as Checker constructors will differ in minor details!
        switch ( checkerClass ) {
            case BrokenCrossReferencesChecker.class:
                checker = new BrokenCrossReferencesChecker(); break

            case DuplicateIdChecker.class:
                checker = new DuplicateIdChecker(); break

            case ImageMapChecker.class:
                checker = new ImageMapChecker(); break

            default:
                logger.warn( "unknown Checker ${checkerClass.toString()}")

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

