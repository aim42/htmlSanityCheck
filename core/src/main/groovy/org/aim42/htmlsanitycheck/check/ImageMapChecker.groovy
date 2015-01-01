package org.aim42.htmlsanitycheck.check

import org.aim42.htmlsanitycheck.collect.SingleCheckResults
import org.aim42.htmlsanitycheck.html.HtmlElement

/**
 * checks imageMaps:
 * 1.) Are all map-tags referenced by at least ONE image?
 * 2.) Do all map-tags contain at least one area?
 * 3.) Do all area-Elements contain at least one href?
 * 4.) are all href-Elements in the area valid?
 *
 */
class ImageMapChecker extends Checker {


    @Override
    protected void initCheckingResultsDescription() {
        checkingResults.whatIsChecked = "Consistency of ImageMaps"
        checkingResults.sourceItemName = "imageMap"
        checkingResults.targetItemName = "missing alt attributes"
    }


    @Override
    protected SingleCheckResults check() {
        // the number of checks is calculated by counting
        // ALL image tags:
        //checkingResults.setNrOfChecks( pageToCheck.getAllImageTags().size())

        //pageToCheck.getAllImageTagsWithMissingAltAttribute().each { element ->
        //    reportSingleImageTagWithMissingAlt(element)
        //}

        return checkingResults
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
