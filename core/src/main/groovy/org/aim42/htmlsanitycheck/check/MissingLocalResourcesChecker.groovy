package org.aim42.htmlsanitycheck.check

import org.aim42.htmlsanitycheck.html.URLUtil
import org.aim42.htmlsanitycheck.collect.SingleCheckResults
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class MissingLocalResourcesChecker extends Checker {

    public static final String MLRC_MESSAGE_PREFIX  = "local resource"
    public static final String MLRC_MESSAGE_MISSING = "missing"
    public static final String MLRC_REFCOUNT        = ", reference count:"

    // List of the local resources referenced in anchor tags
    private List<String> localResourcesList

    // unique local references - every one is unique
    // created from the List of all by toSet() method
    private Set<String> localResourcesSet

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
    protected SingleCheckResults check() {
        //get list of all anchor-tags containing href="xyz" in html file
        List<String> allHrefs = pageToCheck.getAllHrefStrings()

        // now filter out all local resources
        localResourcesList = allHrefs.findAll {
            URLUtil.isLocalResource( it )
        }

        // filter duplicates by reducing to set
        localResourcesSet = localResourcesList.toSet()

        logger.debug """local resources set: ${localResourcesSet}"""

        // make sure we have a non-null baseDir
        // (for html pages given as "string", this should be "")
        if (baseDirPath == null) {
            baseDirPath = ""
        }

        // perform the actual checks
        checkAllLocalResources()

        return checkingResults

    }

    private void checkAllLocalResources() {
        localResourcesList.each { localResource ->
            checkSingleLocalResource( localResource )
        }
    }


    /*
    @param localResource can be either:
    - file.ext
    - dir/file.ext
    - file:/dir/file.ext
    - file.ext#anchor
     */
    private void checkSingleLocalResource( String localResource )  {
        // the localResource is either path+filename  or filename or directory

        logger.debug( "single resource to be checked: + $localResource" )

        // bookkeeping:
        checkingResults.incNrOfChecks()

        // we need to strip the localResource of #anchor-parts
        String localResourcePath = new URI( localResource ).getPath()

        // we need the baseDir for robust checking of local resources...
        File localFile = new File( baseDirPath, localResourcePath );

        if (!localFile.exists() ) {
            String findingText = """$MLRC_MESSAGE_PREFIX \"${localResource}\" $MLRC_MESSAGE_MISSING"""

            // how often is localResource referenced?
            int nrOfOccurrences = localResourcesList.count( localResource )

            if (nrOfOccurrences > 1)
                findingText += MLRC_REFCOUNT + nrOfOccurrences

            checkingResults.newFinding(findingText)
        }
    }

    // helper to count occurrences of a localResource
    private void
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
