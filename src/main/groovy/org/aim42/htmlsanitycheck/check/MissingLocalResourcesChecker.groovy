package org.aim42.htmlsanitycheck.check

import org.aim42.htmlsanitycheck.html.URLUtil
import org.aim42.htmlsanitycheck.collect.SingleCheckResultsCollector
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class MissingLocalResourcesChecker extends Checker {

    // members are initialized in implicit constructor
    private List<String> localResources

    // we need to know the baseDir of the html file, so we can check
    // for local resources either with relative or absolute paths
    private String baseDirPath



    // logging stuff
    private static Logger logger = LoggerFactory.getLogger(MissingLocalResourcesChecker.class);

    @Override
    protected void initCheckingResultsDescription() {
        checkingResults.whatIsChecked  = "Missing Local Resources Check"
        checkingResults.sourceItemName = "anchor tag href attribute"
        checkingResults.targetItemName = "missing local resources"
    }

    @Override
    protected SingleCheckResultsCollector check() {
        //get list of all anchor-tags containing href="xyz" in html file
        List<String> allHrefs = pageToCheck.getAllHrefStrings()

        // now filter out all local resources
        localResources = allHrefs.findAll {
            URLUtil.isLocalResource( it )
        }

        logger.info( "local resources", localResources )

        checkAllLocalResources()

        return checkingResults

    }

    private void checkAllLocalResources() {
        localResources.each { localResource ->
            checkSingleLocalResource( localResource )
        }
    }


    private void checkSingleLocalResource( String localResource )  {
        // the localResource is either path+filename  or filename or directory

        //logger.info( "resource to be checked: ", localResource )

        // bookkeeping:
        checkingResults.incNrOfChecks()

        File localFile = new File( localResource );

        if (!localFile.exists() ) {
            String findingText = "local resource \"$localResource\" missing"
            checkingResults.newFinding(findingText)
        }
    }
}


/************************************************************************
 * This is free software - without ANY guarantee!
 *
 *
 * Copyright 2013, Dr. Gernot Starke, arc42.org
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
