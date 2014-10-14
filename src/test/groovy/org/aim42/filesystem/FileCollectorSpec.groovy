package org.aim42.filesystem

// see end-of-file for license information
import spock.lang.Specification
import spock.lang.Unroll

class FileCollectorSpec extends Specification {


    // we can identify html files
    @Unroll
    def "IsHtmlFile"( String fileName, Boolean isHtml ) {

        expect:
        FileCollector.isHtmlFile( fileName ) == isHtml

        where:
        fileName     | isHtml
        "test.html"  | true
        "test.htm"   | true
        "test.HTML"  | true
        "TEST.HTM"   | true
        "test.txt"   | false
        "html.txt"   | false
        "/test.htm"  | true
        "/test.txt"  | false
        "/a/b/c.html"| true
        ".htm"       | false
        ".html"      | false
        "a.html"     | true

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


