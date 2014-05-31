package org.aim42.htmlsanitycheck.checker

import org.aim42.htmlsanitycheck.html.HtmlElement

// see end-of-file for license information


class ImageFileExistChecker extends Checker {

    private List<HtmlElement> images
    private String baseDir



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
            checkSingleImage(image)

        }
    }

    private void checkSingleImage(HtmlElement image) {
        String relativePathToCurrentImage = image.getSrcAttribute()

        // bookkeeping one more check...
        checkingResults.incNrOfChecks()

        // check only "local" image references
        if (!relativePathToCurrentImage.startsWith("http:")) {
            doesFileExist(relativePathToCurrentImage);
        }
    }


    /**
     * check if the file at relativePathToImageFile exists
     *
    * @param relativePathToImageFile == XYZ in <img src="XYZ">
     **/
    private void doesFileExist(String relativePathToImageFile) {
        // problem: if the relativePath is "./images/fileName.jpg",
        // we need to add the appropriate path prefix...

        String absolutePath = baseDir + relativePathToImageFile[1..relativePathToImageFile.length()-1]

        File imageFile = new File(absolutePath);

        if (!imageFile.exists()) {
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


