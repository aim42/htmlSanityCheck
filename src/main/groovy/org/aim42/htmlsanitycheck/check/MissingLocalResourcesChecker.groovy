package org.aim42.htmlsanitycheck.check

import org.aim42.htmlsanitycheck.Configuration
import org.aim42.htmlsanitycheck.collect.SingleCheckResults
import org.aim42.htmlsanitycheck.html.HtmlPage
import org.aim42.htmlsanitycheck.html.URLUtil
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class MissingLocalResourcesChecker extends Checker {

    public static final String MLRC_MESSAGE_PREFIX  = "local resource"
    public static final String MLRC_MESSAGE_MISSING = "missing"
    public static final String MLRC_REFCOUNT        = ", reference count: "

    // List of the local resources referenced in anchor tags
    private List<String> localResourcesList

    // unique local references - every one is unique
    // created from the List of all by toSet() method
    private Set<String> localResourcesSet

    /**
     * The base directory to resolve absolute paths.
     */
    private File baseDir

    /**
     * The current directory, obtained from the HtmlPage, to resolve
     * relative paths.
     */
    private File currentDir

    /**
     * True to require files to be referenced and not directories. Useful if the web server doesn't
     * support a default directory, such as Amazon S3.
     */
    private boolean requireFiles = false

    // logging stuff
    private final static Logger logger = LoggerFactory.getLogger(MissingLocalResourcesChecker.class);

    public MissingLocalResourcesChecker( Configuration pConfig ) {
        super( pConfig )
        baseDir = pConfig.getConfigItemByName( Configuration.ITEM_NAME_sourceDir )
    }

    @Override
    protected void initCheckingResultsDescription() {
        checkingResults.whatIsChecked  = "Missing Local Resources Check"
        checkingResults.sourceItemName = "anchor tag href attribute"
        checkingResults.targetItemName = "missing local resources"
    }

    @Override
    protected SingleCheckResults check(final HtmlPage pageToCheck) {
        //get list of all anchor-tags containing href="xyz" in html file
        List<String> allHrefs = pageToCheck.getAllHrefStrings()

        // now filter out all local resources
        localResourcesList = allHrefs.findAll {
            URLUtil.isLocalResource( it )
        }

        // filter duplicates by reducing to set
        localResourcesSet = localResourcesList.toSet()

        logger.debug """local resources set: ${localResourcesSet}"""

        currentDir = pageToCheck.file?.parentFile ?: baseDir

        // perform the actual checks
        checkAllLocalResources( localResourcesSet )

        return checkingResults

    }

    /*
    * iterate over the SET of all local resources
     */
    private void checkAllLocalResources( Set<String> localResources ) {
        localResources.each { localResource ->
            checkSingleLocalResource( localResource )
        }
    }


    /*
    check a single resource:

    @param localResource can be either:
    - file.ext
    - dir/file.ext
    - file:/dir/file.ext
    - file.ext#anchor

    - see #252 (false positives), localResource can be a /example string referencing a file "/example.html"
      This special case is called "prefixOnlyHref"
     */

    private void checkSingleLocalResource( String localResource )  {
        // the localResource is either path+filename  or filename or directory

        logger.debug( "single resource to be checked: + $localResource" )

        // bookkeeping:
        checkingResults.incNrOfChecks()

        // we need to strip the localResource of #anchor-parts
        String localResourcePath = new URI( localResource ).getPath()

        if (localResourcePath == null) {
            // For example, javascript:;
            return
        }

        File parentDir = localResourcePath?.startsWith("/") ? baseDir : currentDir;

        // we need the baseDir for robust checking of local resources...
        File localFile = new File( parentDir, localResourcePath );

        // action required if resource does not exist
        if (!localFile.exists() || !localFile.isFile()) {
            handleNonexistingLocalResource( localResource )
        }
    }

    /*
     create error message and reference count
    */
    private handleNonexistingLocalResource(String nonExistingLocalResource) {
        String findingText = """$MLRC_MESSAGE_PREFIX \"${nonExistingLocalResource}\" $MLRC_MESSAGE_MISSING"""

        // how often is localResource referenced?
        int nrOfOccurrences = localResourcesList.count(nonExistingLocalResource)

        if (nrOfOccurrences > 1)
            findingText += MLRC_REFCOUNT + nrOfOccurrences

        // add Finding to our current checking results, increment nrOfFindings by nrOfOccurrences
        checkingResults.newFinding(findingText, nrOfOccurrences)
    }


}


/************************************************************************
 * This is free software - without ANY guarantee!
 *
 *
 * Copyright Dr. Gernot Starke and aim42 contributors
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
