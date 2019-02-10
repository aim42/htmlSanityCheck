package org.aim42.filesystem

import groovy.io.FileType

import java.util.regex.Pattern

// see end-of-file for license information

// TODO: add exclude-patterns

class FileCollector {

    public final static String IMAGE_FILE_EXTENSION_PATTERN = ~/(?i).+\.(jpg|jpeg|png|gif|bmp|svg)?$/

    /**
     * checks if @param fileName represents an image file name,
     * ignoring the files' content.
     * @param fileName
     */
    public static Boolean isImageFileName(String fileName) {
        return (fileName ==~ IMAGE_FILE_EXTENSION_PATTERN)
    }

    /**
     * returns all configured image files as Set<File>
     *
     */
    public static Set<File> getConfiguredImageFiles(
            File srcDir,
            Set<String> sourceDocs) {
        // first case: no document names given -> return all html files
        // in directory tree
        if ((sourceDocs == null) || (sourceDocs?.empty)) {
            return getAllImageFilesFromDirectory(srcDir)
        } else {
            return getAllConfiguredImageFiles(srcDir, sourceDocs)
        }
    }

    /**
     * returns all image files in a given directory.
     * (recursively looks in subdirectories)
     * @param dir where to look for matching files
     * @return all files with appropriate extension
     */
    public static Set<File> getAllImageFilesFromDirectory(File dir) {
        Set<File> files = new HashSet<File>()

        // scan only files, not directories
        dir.eachFileRecurse(FileType.FILES) { file ->
            if (FileCollector.isImageFileName(file.getName())) {
                files.add(file)
            }
        }

        return files
    }

    /**
     * returns all configured image files from @param srcDocs
     * which really exist below @param srcDocs
     */
    public static Set<File> getAllConfiguredImageFiles(File srcDir, Set<String> srcDocs) {
        Set<File> files = new HashSet<File>()

        srcDocs.each { srcFileName ->
            // add only existing image files
            File file = new File(srcDir, srcFileName)
            if (file.exists() && isImageFileName(file.getName())) {
                files.add(file)
            }
        }

        return files

    }

    /**
     * convert Set of Files to Set of file-names without path prefix!
     */
    public static Set<String> collectFileNamesFromFiles(Set<File> files) {
        // remark: could also achieve that with java.nio.file.Paths - but getName() seems simpler
        return files*.getName()


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

