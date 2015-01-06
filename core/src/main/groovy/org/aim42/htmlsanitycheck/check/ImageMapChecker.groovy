package org.aim42.htmlsanitycheck.check

import org.aim42.htmlsanitycheck.collect.Finding
import org.aim42.htmlsanitycheck.collect.SingleCheckResults
import org.aim42.htmlsanitycheck.html.HtmlElement

/**
 * principal checks on imageMap usage:
 *
 1.) for every usemap-reference there is one map
 2.) every map is referenced by at least one image
 3.) every every map name is unique
 4.) every area-tag has one non-empty href attribute
 5.) every href points to valid target (broken-links check)
 *
 * @see www.w3schools.com/tags/tag_map.asp
 */
class ImageMapChecker extends Checker {

    ArrayList<HtmlElement> maps
    ArrayList<String> mapNames    // y with <map name="y">

    ArrayList<HtmlElement> imagesWithUsemapRefs
    ArrayList<String> usemapNames // x with referenced by <img... usemap="x">

    ArrayList<HtmlElement> areas

    @Override
    protected void initCheckingResultsDescription() {
        checkingResults.whatIsChecked = "Consistency of ImageMaps"
        checkingResults.sourceItemName = "imageMap"
        checkingResults.targetItemName = "map/area and usemap-references"
    }

    @Override
    protected SingleCheckResults check() {

        readImageMapAttributesFromHtml()

        isThereOneMapForEveryUsemapReference()

        //isEveryMapReferencedByImage()

        //isEveryMapNameUnique() // check for duplicate map names

        //everyAreaTagHasHref()

        //checkForBrokenHrefLinks() // the major check

        return checkingResults
    }


    /*
    * <img src="x" usemap="y">...
    * a.) if there is no map named "y" -> problem
    * b.) if there are more maps named "y" -> problem
     */
    private void isThereOneMapForEveryUsemapReference() {
        String usemapRef
        String imageName
        int mapCount
        String findingText

        imagesWithUsemapRefs.each { imageTag ->
            usemapRef = imageTag.getUsemapRef()
            mapCount = mapNames.findAll{ it == usemapRef }?.size()

            checkingResults.incNrOfChecks()

            if (mapCount == 0) {
                // no map found, despite img-tag usemap-reference
                imageName = imageTag.getImageSrcAttribute()
               findingText = "ImageMap ${usemapRef} (referenced by image ${imageName}) missing."
               checkingResults.addFinding( new Finding( findingText ))
            } else if (mapCount > 1 ) {
                // more than one map for this image
                findingText = "Too many (${mapCount}) ImageMaps (${mapCount}) named ${usemapRef} exist."
                checkingResults.addFinding( new Finding( findingText ))
            }
        }
    }

    /*
    set all the interesting variables
     */
    private void readImageMapAttributesFromHtml() {
        // get all <img src="x" usemap="y">
        imagesWithUsemapRefs = pageToCheck.getImagesWithUsemapDeclaration()

        // get all <map name="z">...
        maps = pageToCheck.getAllImageMaps()

        // get the names of all maps
        mapNames = pageToCheck.getAllMapNames()

    }


}


/************************************************************************
 * This is free software - without ANY guarantee!
 *
 *
 * Copyright 2015, Dr. Gernot Starke, arc42.org
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
