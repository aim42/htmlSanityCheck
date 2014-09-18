package org.aim42.htmlsanitycheck.checker

import org.aim42.htmlsanitycheck.html.HtmlElement
import org.slf4j.Logger
import org.slf4j.LoggerFactory

// see end-of-file for license information


class ImageFileExistChecker extends Checker {

    private List<HtmlElement> images
    private String baseDirPath

    // logging stuff
    private static Logger logger = LoggerFactory.getLogger(ImageFileExistChecker.class);




    @Override
    public CheckingResultsCollector check() {

        super.initResults()

        //get list of all image-tags "<img..." in html file
        images = pageToCheck.getAllImageTags()

        checkAllImages()

        return checkingResults

    }


    private void checkAllImages() {

        images.each { image ->
            checkSingleLocalImage(image)
      }
    }

    private void checkSingleLocalImage(HtmlElement image) {
        String relativePathToCurrentImage = image.getSrcAttribute()

        logger.info( "image: " + image)
        logger.info( "relPathToCurImage: \n$relativePathToCurrentImage")

        // check only "local" image references
        if (isLocalImage(relativePathToCurrentImage)) {

            // bookkeeping:
            checkingResults.incNrOfChecks()

            doesFileExist( relativePathToCurrentImage );
        }
    }


    /*
     * Checks if this image (given by its relative path + filename)
     * is local
     * @param relativePathToCurrentImage
     * @return
     */
    private boolean isLocalImage(String relativePathToCurrentImage) {
        return !relativePathToCurrentImage.startsWith("http:")
    }


    /**
     * check if the file at relativePathToImageFile exists
     *
    * @param relativePathToImageFile == XYZ in <img src="XYZ">
     **/
    private void doesFileExist(String relativePathToImageFile) {
        // problem: if the relativePath is "./images/fileName.jpg",
        // we need to add the appropriate path prefix...

        // thx to rdmueller for the improvement below:
        // was: relativePathToImageFile[1..relativePathToImageFile.length()-1]
        String absolutePath = baseDirPath + "/" + relativePathToImageFile //[1..-1]

        logger.info( "doesFileExist: absolutePath of image: $absolutePath")

        File imageFile = new File(absolutePath);

        if (!imageFile.exists() || imageFile.isDirectory()) {
            String findingText = "image $relativePathToImageFile missing"
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


