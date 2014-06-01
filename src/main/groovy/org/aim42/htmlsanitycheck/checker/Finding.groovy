// see end-of-file for license information

package org.aim42.htmlsanitycheck.checker

/**
 * A single "finding" from any check, i.e.:
 * - a missing image file
 * - a missing label/id/bookmark (== broken link)
 * - a duplicate label/id/bookmark
 */
class Finding {

    // TODO handle suggestions


    String item // i.e. which image is missing, which link/anchor is undefined

    ArrayList<String> suggestions

    /**
     * no finding should exist without explanation
     * @param item
     */
    public Finding( String item ) {
        this.item = item
        suggestions = new ArrayList<String>()
    }


    @Override
    public String toString() {
        return item
    }
}

/*======================================================================
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
 ======================================================================*/

