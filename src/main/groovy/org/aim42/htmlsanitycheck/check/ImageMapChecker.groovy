package org.aim42.htmlsanitycheck.check

import org.aim42.htmlsanitycheck.collect.Finding
import org.aim42.htmlsanitycheck.collect.SingleCheckResults
import org.aim42.htmlsanitycheck.html.HtmlElement
import org.aim42.htmlsanitycheck.html.URLUtil

/**
 * principal checks on imageMap usage:
 *
 1.) for every usemap-reference there is one map
 2.) every map is referenced by at least one image
 3.) every every map name is unique
 4.) every area-tag has one non-empty href attribute
 5.) every href points to valid target (broken-links check)
 *
 * see also: http://www.w3schools.com/tags/tag_map.asp
 **/
class ImageMapChecker extends Checker {

    private ArrayList<HtmlElement> maps
    private ArrayList<String> mapNames    // y with <map name="y">

    private ArrayList<HtmlElement> imagesWithUsemapRefs
    private ArrayList<String> usemapRefs // x with referenced by <img... usemap="x">

    private ArrayList<String> listOfIds

    private String findingText


    @Override
    protected void initCheckingResultsDescription() {
        checkingResults.whatIsChecked = "Consistency of ImageMaps"
        checkingResults.sourceItemName = "imageMap"
        checkingResults.targetItemName = "map/area and usemap-references"
    }

    @Override
    protected SingleCheckResults check() {

        readImageMapAttributesFromHtml()

        checkBrokenImageMapReferences()

        checkDuplicateMapNames()

        checkDanglingMaps()

        checkEmptyMaps()

        checkForBrokenHrefLinks() // the major check

        return checkingResults
    }


    /*
     search for maps that are NOT referenced by any image-tag
     */
    private void checkDanglingMaps() {

        mapNames.each { mapName ->
            checkingResults.incNrOfChecks()

            // check if mapName is contained in collection of usemap-references
            if (!usemapRefs.contains(mapName)) {
                findingText = """ImageMap "${mapName}" not referenced by any image."""
                checkingResults.addFinding(new Finding(findingText))
            }
        }
    }




    /*
     search for maps that are NOT referenced by any image-tag
     */
    private void checkEmptyMaps() {
        ArrayList<HtmlElement> areas = new ArrayList<HtmlElement>()

        mapNames.each { mapName ->
            areas = pageToCheck.getAllAreasForMapName(mapName)

            checkingResults.incNrOfChecks()

            // empty map?
            if (areas.size() == 0) {
                findingText = """ImageMap "${mapName}" has no area tags."""
                checkingResults.addFinding(new Finding(findingText))
            }
        }
    }

    /*
    check for duplicate map names
     */
    private void checkDuplicateMapNames() {
        int mapNameCount

        Set<String> mapNameSet = mapNames.toSet()

        mapNameSet.each { mapName ->
            mapNameCount = mapNames.count( mapName )

            checkingResults.incNrOfChecks()

            if (mapNameCount > 1) {
                // more than one map with this name
                findingText = """${mapNameCount} imagemaps with identical name "${mapName}" exist."""
                checkingResults.addFinding(new Finding(findingText))
            }
        }
    }

    /*
    * <img src="x" usemap="y">...
    * a.) if there is no map named "y" -> problem
    * b.) if there are more maps named "y" -> problem
     */
    private void checkBrokenImageMapReferences() {
        String usemapRef
        String imageName
        int mapCount

        imagesWithUsemapRefs.each { imageTag ->
            usemapRef = imageTag.getUsemapRef()
            mapCount = mapNames.findAll{ it == usemapRef }?.size()

            checkingResults.incNrOfChecks()

            if (mapCount == 0) {
                // no map found, despite img-tag usemap-reference
                imageName = imageTag.getImageSrcAttribute()
               findingText = """ImageMap "${usemapRef}" (referenced by image "${imageName}") missing."""
               checkingResults.addFinding( new Finding( findingText ))
            }
        }
    }

    /*
    check for broken href links.
    TODO: currently this checks only for INTERNAL links, enhance to arbitrary links
     */
    private void  checkForBrokenHrefLinks() {

        mapNames.each { mapName ->
            checkAreaHrefsForMapName(mapName)
        }
    }

    /*
    for a specific mapName, check all its contained  areaHrefs
     */
    private void checkAreaHrefsForMapName( String mapName) {
        ArrayList<String> areaHrefs = pageToCheck.getAllHrefsForMapName(mapName)

        // if this List is empty -> the map is empty
        // TODO replace checkEmptyMaps with additional check here
        areaHrefs.each { href ->
            checkingResults.incNrOfChecks()

            // do the actual checking
            if (URLUtil.isCrossReference( href )) {
                checkLocalHref(href, mapName, areaHrefs)
            }

        }
    }

    /*
    check if href has valid local target
    TODO: currently restricted to LOCAL references
    TODO: remove duplication to BrokenCrossReferencesChecker
    */
    private void checkLocalHref( String href, String mapName,ArrayList<String> areaHrefs ) {
        // strip href of its leading "#"
        String linkTarget = (href.startsWith("#")) ? href[1..-1] : href


        if (!listOfIds.contains( linkTarget )) {

            // we found a broken link!
             findingText = """ImageMap "${mapName}" refers to missing link \"$linkTarget\""""

            // now count occurrences - how often is it referenced
            int nrOfReferences = areaHrefs.findAll{  it == href }.size()
            if (nrOfReferences > 1) {
                findingText += ", reference count: $nrOfReferences."
            } else findingText += "."

            checkingResults.newFinding(findingText, nrOfReferences)
        }

    }


    /*
     set all the interesting attributes
     */
    private void readImageMapAttributesFromHtml() {
        // get all <img src="x" usemap="y">
        imagesWithUsemapRefs = pageToCheck.getImagesWithUsemapDeclaration()

        // get all <map name="z">...
        maps = pageToCheck.getAllImageMaps()

        // get the names of all maps
        mapNames = pageToCheck.getAllMapNames()

        // get all referenced maps from image tags with usemap-attribute
        usemapRefs = pageToCheck.getAllUsemapRefs()

        // list of all id="XYZ"
        listOfIds    = pageToCheck.getAllIdStrings()

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
