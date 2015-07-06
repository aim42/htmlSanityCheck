package org.aim42.filesystem

import spock.lang.Specification
import spock.lang.Unroll

// see end-of-file for license information
class ImageFileCollectorSpec extends Specification {
    File tempDir

    def setup() {
        tempDir = File.createTempDir()
    }

    // we can identify image files
    def "Is Image File"(String fileName, Boolean isImage) {

        expect:
        FileCollector.isImageFileName(fileName) == isImage

        where:
        fileName      | isImage
        "test.jpg"   | true
        "test.JPG"    | true
        "test.JPEG"   | true
        "TEST.jpeg"    | true
        "test.png"    | true
        "html.PNG"    | true
        "/test.gif"   | true
        "/test.GIF"   | true
        "/a/b/c.pdf" | false
        ".bmp"        | false
        ".svg"       | false
        "a.SVG"      | true
        "a.html"     | false

    }


    def "get Images From Directory"() {
        Set<File> collectedFiles
        File file

        String f1name = "f1.jpg"
        String f2name = "f2.png"

        when:
        file = new File(tempDir, f1name)   << "non-binary-content"
        file = new File(tempDir, f2name)   << "again, some content"

        collectedFiles = FileCollector.getAllImageFilesFromDirectory( tempDir )

        Set<String> collectedFileNames = FileCollector.collectFileNamesFromFiles( collectedFiles )

        then:
            // two files found
            collectedFiles.size() == 2

            collectedFileNames.contains( f1name )

            collectedFileNames.contains( f2name )

    }



    def "Collect image files from given directory"(Set<String> files, int imageFileCount) {
        Set<File> collectedFiles
        File f

        when:
        // create defined files under tempDir
        files.each { fileName ->
            f = new File(tempDir, fileName) << "some content"
        }

        collectedFiles = FileCollector.getAllImageFilesFromDirectory( tempDir )

        then:  // find the file we just created
        collectedFiles.size() == imageFileCount


        where:

        files                                 | imageFileCount
        ["a.jpg"]                             | 1
        ["a.jpg", "b.png"]                    | 2
        ["a.jpg", "b.txt"]                    | 1
        ["a.jpg", "b.jpg", "c.txt", "d.bmp"]  | 3
    }




    def "if we configure just a directory all contained image files are taken"(
            Set dirsAndFiles,
            int imageFileCount) {

        Set<String> emptyFileCollection

        setup:
        emptyFileCollection = new HashSet<String>()


        when:
        DirAndFileCreator.createDirsAndFiles(dirsAndFiles, tempDir)

        // "mock" a configuration
        Set<File> collectedFiles =
                FileCollector.getConfiguredImageFiles(tempDir, emptyFileCollection)

        then:
        collectedFiles.size() == imageFileCount //find the file we just created


        where:
        dirsAndFiles                   | imageFileCount
        [["/a", ["a.jpg"]]]            | 1
        [["/a", ["a1.jpg", "a2.png"]]] | 2

        // nested directories, some files each
        [["/a", ["t1.jpg", "t2.txt", "html.pdf", "h2o.doc"]],
         ["/a/b", ["ab2.jpg", "t3.txt"]],
         ["/a/b/c", ["abc1.png", "t4.txt", "abc2.PNG", "abc3.PNG"]],
         ["/a/b/d", ["abcd1.jpg", "t5.adoc"]],
         ["/a/b/c/d/e", ["abcde1.jpg", "abcde2.jpeg", "abcde3.JPG"]],
         ["/a/b/c/d/f", ["nested.jpg"]]] | 10

    }



}

/*========================================================================
 Copyright 2015 Gernot Starke and aim42 contributors

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


