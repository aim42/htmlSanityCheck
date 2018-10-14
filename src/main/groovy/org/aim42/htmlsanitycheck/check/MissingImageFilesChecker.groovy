package org.aim42.htmlsanitycheck.check

import org.aim42.htmlsanitycheck.Configuration
import org.aim42.htmlsanitycheck.html.HtmlPage
import org.aim42.htmlsanitycheck.html.URLUtil
import org.aim42.htmlsanitycheck.collect.SingleCheckResults
import org.aim42.htmlsanitycheck.html.HtmlElement
import org.slf4j.Logger
import org.slf4j.LoggerFactory

// see end-of-file for license information


class MissingImageFilesChecker extends Checker {

    private List<HtmlElement> images
    private File baseDir
    private File currentDir

    // logging stuff
    private final static Logger logger = LoggerFactory.getLogger(MissingImageFilesChecker);


    public MissingImageFilesChecker( Configuration pConfig) {
        super( pConfig )
        baseDir = myConfig.getConfigItemByName( Configuration.ITEM_NAME_sourceDir )
    }

    @Override
    protected void initCheckingResultsDescription() {

        checkingResults.whatIsChecked  = "Missing Local Images Check"
        checkingResults.sourceItemName = "img src attributes"
        checkingResults.targetItemName = "missing image files"
    }


    @Override
    protected SingleCheckResults check(final HtmlPage pageToCheck) {
        currentDir = pageToCheck.file?.parentFile ?: baseDir

        //get list of all image-tags "<img..." in html file
        images = pageToCheck.getAllImageTags()

        logger.debug("images to check: ${images}")

        checkAllImages()

        return checkingResults

    }


    private void checkAllImages() {

        images.each { image ->
            checkSingleLocalImage(image)
      }
    }


    private void checkSingleLocalImage(HtmlElement image) {
        String imageSrcAttribute = image.getImageSrcAttribute()

        // check only "local" image references
        // (that is, NO remote URL)
        Boolean isRemoteURL = URLUtil.isRemoteURL(imageSrcAttribute)
        Boolean isDataURI   = URLUtil.isDataURI(imageSrcAttribute)
        if (isRemoteURL) {
            //do nothing. This checks for _local_ images
        } else if (isDataURI) {
            // bookkeeping:
            checkingResults.incNrOfChecks()

            doesDataURIContainData( imageSrcAttribute );

        } else {
            //we have a simple local image

            // bookkeeping:
            checkingResults.incNrOfChecks()

            doesImageFileExist( imageSrcAttribute );
        }
    }



    /**
     * check if a single image file exists
     *
    * @param relativePathToImageFile == XYZ in <img src="XYZ">
     **/
    private void doesImageFileExist(String relativePathToImageFile) {
        File parentDir = relativePathToImageFile?.startsWith("/") ? baseDir : currentDir;


        File imageFile = new File(parentDir, relativePathToImageFile);

        if (!imageFile.exists() || imageFile.isDirectory()) {
            String findingText = "image \"$relativePathToImageFile\" missing"
            checkingResults.newFinding(findingText)
        }

    }

    /**
     * check if the given data-URI contains actual data
     *
     * Good: "data:image/png;base64,iVBORw0KGgoAAAANSU..."
     *
     * Bad: "data:image/jpg;base64,"
     *
     * @param dataURI == XYZ in <img src="XYZ">
     **/
    private void doesDataURIContainData(String dataURI) {
        // let's do a simple regexp

        if (dataURI ==~ "^data:image/[a-z]+;base64,") {
            String findingText = "data-URI image missing"
            checkingResults.newFinding(findingText)
        }
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


