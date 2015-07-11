package org.aim42.htmlsanitycheck.check

import org.aim42.htmlsanitycheck.html.HtmlPage
import org.aim42.htmlsanitycheck.html.URLUtil
import org.aim42.htmlsanitycheck.collect.SingleCheckResults
import org.aim42.htmlsanitycheck.html.HtmlElement
import org.slf4j.Logger
import org.slf4j.LoggerFactory

// see end-of-file for license information


class MissingImageFilesChecker extends Checker {

    // members are initialized in implicit constructor
    private List<HtmlElement> images
    private String baseDirPath

    // logging stuff
    private static Logger logger = LoggerFactory.getLogger(MissingImageFilesChecker.class);


    @Override
    protected void initCheckingResultsDescription() {

        checkingResults.whatIsChecked  = "Missing Local Images Check"
        checkingResults.sourceItemName = "img src attributes"
        checkingResults.targetItemName = "missing image files"
    }


    @Override
    protected SingleCheckResults check(final HtmlPage pageToCheck) {

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
        String relativePathToCurrentImage = image.getImageSrcAttribute()

        // check only "local" image references
        // (that is, NO remote URL)
        if (!URLUtil.isRemoteURL(relativePathToCurrentImage)) {

            // bookkeeping:
            checkingResults.incNrOfChecks()

            doesImageFileExist( relativePathToCurrentImage );
        }
    }



    /**
     * check if the file at relativePathToImageFile exists
     *
    * @param relativePathToImageFile == XYZ in <img src="XYZ">
     **/
    private void doesImageFileExist(String relativePathToImageFile) {
        // problem: if the relativePath is "./images/fileName.jpg",
        // we need to add the appropriate path prefix...

        String absolutePath = baseDirPath + "/" + relativePathToImageFile //[1..-1]

        //logger.info( "doesFileExist: absolutePath of image: $absolutePath")

        File imageFile = new File(absolutePath);

        if (!imageFile.exists() || imageFile.isDirectory()) {
            String findingText = "image \"$relativePathToImageFile\" missing"
            checkingResults.newFinding(findingText)
        }

    }



}

/*========================================================================
 Copyright 2014 Gernot Starke and aim42 contributors

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


