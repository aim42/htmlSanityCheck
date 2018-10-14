package org.aim42.filesystem

import org.aim42.htmlsanitycheck.Configuration
import spock.lang.Shared
import spock.lang.Specification

// see end-of-file for license information
class FileCollectorSpec extends Specification {
    @Shared
    File tempDir

    private Configuration myConfig

    def setup() {
        tempDir = File.createTempDir()
        myConfig = new Configuration()
    }


    def "we can take srcDir and srcDocs from configuration instance"() {
        given: "a file, directory and Configuration instance"
        // create file with proper html content
        File tmpFile = File.createTempFile("testfile", ".html") << """<body><title>hsc</title></body></html>"""


        myConfig.addConfigurationItem( Configuration.ITEM_NAME_sourceDocuments, tmpFile.name)
        myConfig.addConfigurationItem( Configuration.ITEM_NAME_sourceDir, tmpFile?.getAbsoluteFile().getParent())

        when: "we call the collector with that configuration"
        HashSet<File> allFilesToCheck

        def tmpDir = myConfig.getConfigItemByName(Configuration.ITEM_NAME_sourceDir)
        def tmpDocs = myConfig.getConfigItemByName(Configuration.ITEM_NAME_sourceDocuments)

        allFilesToCheck = FileCollector.getHtmlFilesToCheck(
                tmpDir,
                tmpDocs
        )

        then: "we find that one file!"
        allFilesToCheck.size() == 1
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
        //"test.html#anchor" | true

    }


    def "collect html files from given directory"(Set<String> files, int htmlFileCount) {
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
            String subDirs,
            Set<String> files,
            int htmlFileCount) {

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
        subDirs  | files              | htmlFileCount
        "/a"     | ["a.htm"]          | 1
        "/a"     | ["a.htm", "b.htm"] | 2
        "/a"     | ["a.htm", "b.txt"] | 1
        "/a/b"   | ["a.htm"]          | 1
        "/a/b/c" | ["a.htm", "b.htm"] | 2
    }


    def "we can collect all files from nested directory trees"(
            Set dirsAndFiles,
            int htmlFileCount) {


        when:
        DirAndFileCreator.createDirsAndFiles(dirsAndFiles, tempDir)
        Set<File> collectedFiles = FileCollector.getAllHtmlFilesFromDirectory(tempDir)

        then:
        collectedFiles.size() == htmlFileCount //find the file we just created


        where:
        dirsAndFiles                   | htmlFileCount
        [["/a", ["a.htm"]]]            | 1
        [["/a", ["a1.htm", "a2.htm"]]] | 2

        // two directories, same level
        [["/a", ["a1.htm", "a2.htm"]],
         ["/b", ["b1.htm", "b2.htm"]]] | 4

        // nested directories, two files each
        [["/a", ["a1.htm", "a2.htm"]],
         ["/a/b", ["ab1.htm", "ab2.htm"]]] | 4

        // nested directories, some files each
        [["/a", ["a1.htm", "a2.htm"]],
         ["/a/b", ["ab1.htm", "ab2.txt"]],
         ["/a/b/c", ["abc1.htm", "ab2c.htm", "tt.txt"]]] | 5

        // nested directories, some files each
        [["/a", ["t1.htm"]],
         ["/a/b", ["ab2.htm"]],
         ["/a/b/c", ["abc1.html", "abc2.HTM", "abc3.HTM"]],
         ["/a/b/d", ["abcd1.html"]],
         ["/a/b/c/d/e", ["abcde1.htm", "abcde2.html", "abcde3.HTM"]],
         ["/a/b/c/d/f", ["nested.htm"]]] | 10

    }

    def "if we configure just a directory all contained html files are taken"(
            Set dirsAndFiles,
            int htmlFileCount) {

        Set<String> emptyFileCollection

        setup:
        emptyFileCollection = new HashSet<String>()


        when:
        DirAndFileCreator.createDirsAndFiles(dirsAndFiles, tempDir)

        // "mock" a configuration
        Set<File> collectedFiles =
                FileCollector.getHtmlFilesToCheck(tempDir, emptyFileCollection)

        then:
        collectedFiles.size() == htmlFileCount //find the file we just created


        where:
        dirsAndFiles                   | htmlFileCount
        [["/a", ["a.htm"]]]            | 1
        [["/a", ["a1.htm", "a2.htm"]]] | 2

        // nested directories, some files each
        [["/a", ["t1.htm", "t2.txt", "html.pdf", "h2o.doc"]],
         ["/a/b", ["ab2.htm", "t3.txt"]],
         ["/a/b/c", ["abc1.html", "t4.txt", "abc2.HTM", "abc3.HTM"]],
         ["/a/b/d", ["abcd1.html", "t5.adoc"]],
         ["/a/b/c/d/e", ["abcde1.htm", "abcde2.html", "abcde3.HTM"]],
         ["/a/b/c/d/f", ["nested.htm"]]] | 10

    }


    def "we can configure selected files"(
            Set otherDirsAndFiles,
            Set<String> configuredFiles ) {


        when:
        DirAndFileCreator.createDirsAndFiles( otherDirsAndFiles, tempDir )


        DirAndFileCreator.createDirsAndFiles( new HashSet([["", configuredFiles]]), tempDir)


        // "mock" a configuration
        Set<File> collectedFiles =
                FileCollector.getHtmlFilesToCheck(tempDir, configuredFiles )

        then:
        collectedFiles.size() == 1

        where:
        otherDirsAndFiles              | configuredFiles

        // two unused files o1 and o2, one real oneFile.html
        [["", ["o1.htm", "o2.htm"]]]   | new HashSet(["oneFile.html"])

    }


}

/*========================================================================
 Copyright Gernot Starke and aim42 contributors

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


