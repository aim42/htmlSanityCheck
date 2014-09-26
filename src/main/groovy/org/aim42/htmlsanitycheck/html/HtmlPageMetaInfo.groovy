package org.aim42.htmlsanitycheck.html

/**
 * Container for simple meta information about a html page.
 * This class will be used by @see FindingsReporter
 *
 */
class HtmlPageMetaInfo {

    public final String documentFileName
    public final String documentTitle
    public final int    documentSize


    public HtmlPageMetaInfo( HtmlPage page) {

        if (page != null ) {
            this.documentFileName = page.getDocumentURL()
            this.documentTitle = page.getDocumentTitle()
            this.documentSize = page.getDocumentSize()
        } else {
            this.documentTitle = ""
            this.documentSize  = 0
            this.documentFileName = ""

        }

    }


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
