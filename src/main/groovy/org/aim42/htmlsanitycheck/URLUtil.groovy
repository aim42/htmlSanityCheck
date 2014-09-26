package org.aim42.htmlsanitycheck

/**
 * functions to identify categories of string-representations of URLs,
 * e.g. isRemote, isCrossReference
 *
 */
class URLUtil {

    /*
     * Checks if this String
     * represents a remote URL (startsWith http, https, ftp, telnet...)
     * @param link
     */

    public static boolean isRemoteURL(String link) {
        // simple regular expression to match http://, https:// and ftp://
        //

        return (link ==~ (/^(?i)(https?|ftp|telnet|ssh|gopher):\/\/.*$/) ||
                // special case for mailto-links
                link ==~ (/^(?i)(mailto):.*$/))

    }

    /**
     * Checks if this String represents a cross-reference,
     * that is an intra-document link
     * @param xref
     */
    public static boolean isCrossReference(String xref) {

        // xref CAN NOT be a cross-reference if one of the following is true:
        // (1) xref contains a path separator ('\' on Mac/Unix, '/' on Windows),
        // (2) xref is a remoteURL (starts with http or similar protocol identifiers)
        // (3) contains ".html" or ".htm" as suffix
        // (4) is a filename... very simple test: contains a "."

        // the simple test is if the xref starts with "#", though :-)

        return xref.startsWith("#")

//        String upped = xref.toUpperCase()
//
//        return ! (xref.contains(File.separatorChar.toString())     // (1)
//                 ||
//                 URLUtil.isRemoteURL( xref )                       // (2)
//                 ||
//                 upped.endsWith("HTML") || upped.endsWith("HTM")   // (3)
//                 ||
//                 xref.contains(".")                                // (4)
//         )
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

