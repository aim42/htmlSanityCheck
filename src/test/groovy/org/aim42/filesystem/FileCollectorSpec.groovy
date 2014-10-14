package org.aim42.filesystem

import org.gradle.api.file.FileCollection

// see end-of-file for license information
import spock.lang.Specification
import spock.lang.Unroll

class FileCollectorSpec extends Specification {
    File tempDir

    def setup() {
        tempDir = File.createTempDir()
    }

    // we can identify html files
    def "IsHtmlFile"(String fileName, Boolean isHtml) {

        expect:
        FileCollector.isHtmlFile(fileName) == isHtml

        where:
        fileName      | isHtml
        "test.html"   | true
        "test.htm"    | true
        "test.HTML"   | true
        "TEST.HTM"    | true
        "test.txt"    | false
        "html.txt"    | false
        "/test.htm"   | true
        "/test.txt"   | false
        "/a/b/c.html" | true
        ".htm"        | false
        ".html"       | false
        "a.html"      | true

    }


    def "we can collect html files from a given directory"(Set<String> files, int htmlFileCount) {
        Set<File> collectedFiles
        File f

        when:
        // create defined files under tempDir
        files.each { fileName ->
            f = new File(tempDir, fileName) << "some content"
        }

        collectedFiles = FileCollector.getAllHtmlFilesFromDirectory(tempDir)

        then:  //find the file we just created
        collectedFiles.size() == htmlFileCount


        where:

        files                                 | htmlFileCount
        ["a.html"]                            | 1
        ["a.html", "b.html"]                  | 2
        ["a.htm", "b.txt"]                    | 1
        ["a.htm", "b.htm", "c.txt", "d.html"] | 3
    }

    def "we can collect files recursively from subdirectories"(
            String      subDirs,
            Set<String> files,
            int         htmlFileCount) {

        File dir
        File file

        when:
        // create defined directories
        dir = new File(tempDir, subDirs)
        dir.mkdirs()
        // in the newly created directory, create some files
        files.each { fileName ->
            file = new File(dir, fileName) << "some content"
        }

        // get all the html files from this directory tree
        Set<File> collectedFiles = FileCollector.getAllHtmlFilesFromDirectory(tempDir)

        then:
        collectedFiles.size() == htmlFileCount //find the file we just created

        where:
        subDirs     |    files              | htmlFileCount
        "/a"        | ["a.htm"]             | 1
        "/a"        | ["a.htm", "b.htm"]    | 2
        "/a"        | ["a.htm", "b.txt"]    | 1
        "/a/b"      | ["a.htm"]             | 1
        "/a/b/c"    | ["a.htm", "b.htm"]    | 2
    }


    def 

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


