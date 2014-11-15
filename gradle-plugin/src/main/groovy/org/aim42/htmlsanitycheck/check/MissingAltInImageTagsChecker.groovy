package org.aim42.htmlsanitycheck.check

import org.aim42.htmlsanitycheck.collect.SingleCheckResults
import org.aim42.htmlsanitycheck.html.HtmlElement

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

/**
 * checks for missing or empty alt-attributes in image tags.
 *
 */
class MissingAltInImageTagsChecker extends Checker {


    @Override
    protected void initCheckingResultsDescription() {
        checkingResults.whatIsChecked = "Missing alt-attribute declaration in image tags"
        checkingResults.sourceItemName = "image tags"
        checkingResults.targetItemName = "missing alt attributes"
    }


    @Override
    protected SingleCheckResults check() {
        // the number of checks is calculated by counting
        // ALL image tags:
        checkingResults.setNrOfChecks( pageToCheck.getAllImageTags().size())

        // see HtmlPageSpec for behavior: missing or empty alt-attributes are included...
        pageToCheck.getAllImageTagsWithMissingAltAttribute().each { element ->
            reportSingleImageTagWithMissingAlt(element)
        }

        return checkingResults
    }

    /*

     */

    private void reportSingleImageTagWithMissingAlt(HtmlElement element) {

        String imageName = element.imageSrcAttribute

        String findingText = """image \"$imageName\" is missing alt-attribute"""

        checkingResults.newFinding(findingText)

    }

}
