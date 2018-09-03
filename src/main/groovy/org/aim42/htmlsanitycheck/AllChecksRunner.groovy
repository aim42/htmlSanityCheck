package org.aim42.htmlsanitycheck

import org.aim42.filesystem.FileCollector
import org.aim42.htmlsanitycheck.check.*
import org.aim42.htmlsanitycheck.collect.PerRunResults
import org.aim42.htmlsanitycheck.collect.SinglePageResults
import org.slf4j.Logger
import org.slf4j.LoggerFactory

// see end-of-file for license information
/**
 * Coordinates and runs all available html sanity checks.  Convenience class,
 * delegates (most) work to an @see ChecksRunner
 * <p>
 * <ol>
 *     <li>parse the html file </li>
 *     <li>initialize and run image file checker </li>
 *     <li></li>
 *     <li></li>
 * </ol>
 * <p>
 * Uses @see Checker instances (they implement the
 * <a href="http://en.wikipedia.org/wiki/Template_method_pattern">
 * template pattern</a>)
 **/

class AllChecksRunner {

    private ChecksRunner runner

    private HashSet<File> allFilesToCheck = new HashSet<File>()

    private static final Logger logger = LoggerFactory.getLogger(AllChecksRunner.class);

    private static final String prefixTemporary = "temporary"

    /**
     * runs all available checks on the file
     *
     * @param myCfg (supposed to be valid!)
     */

    public AllChecksRunner(Configuration myCfg) {
        super()

        allFilesToCheck = FileCollector.getHtmlFilesToCheck(
                myCfg.getConfigurationItemByName(Configuration.ITEM_NAME_sourceDir),
                myCfg.getConfigurationItemByName(Configuration.ITEM_NAME_sourceDocuments)
        )

        runner = new ChecksRunner(
                AllCheckers.checkerClazzes,
                allFilesToCheck,
                myCfg.getConfigurationItemByName(Configuration.ITEM_NAME_checkingResultsDir),
                myCfg.getConfigurationItemByName(Configuration.ITEM_NAME_junitResultsDir)
        )

        logger.debug("AllChecksRunner created")
    }



    /**
     * performs all available checks on pageToCheck
     *
     */
    public PerRunResults performAllChecks() {


        logger.debug "entered performAllChecks"
        runner.performChecks()

    }

    /**
     *  performs all known checks on a single HTML file.
     *
     *  Creates a {@link SinglePageResults} instance to keep checking results.
     */
    public SinglePageResults performAllChecksForOneFile(File thisFile) {

        return runner.performChecksForOneFile(thisFile)
    }

    /**
     * runs the checks from the command
     * line with default settings...
     * @param args
     */
    public static void main(String[] args) {
        // TODO: read parameter from command line

        // empty method is currently marked "prio-2" issue by CodeNarc... we'll ignore that
    }


}

/*========================================================================
 Copyright Gernot Starke and aim42 contributors

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an
 "AS IS" BASIS,WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 ========================================================================*/

