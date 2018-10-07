package org.aim42.filesystem

import groovy.io.FileType

import java.util.regex.Pattern

// see end-of-file for license information

// TODO: add exclude-patterns

class FileCollector {

    // (?i): ignore-case
    // htm(l): either htm or html
    // at least one character left of the dot
    public final static Pattern HTML_FILE_EXTENSION_PATTERN = ~/(?i).+\.htm(l)?$/

    public final static Closure<Boolean> INCLUDE_HTML_FILES = { File file ->
        // allow only html or htm files
        (file.name ==~ HTML_FILE_EXTENSION_PATTERN)
    }

    public final static String IMAGE_FILE_EXTENSION_PATTERN = ~/(?i).+\.(jpg|jpeg|png|gif|bmp|svg)?$/


    public static Boolean isHtmlFile(File file) {
        return (isHtmlFile(file.absolutePath)
                && file.isFile())
    }

    /**
     * checks if @param fileName represents a valid html file,
     * ignoring the files' content!
     * @param fileName
     * @return
     */
    public static Boolean isHtmlFile(String fileName) {
        return (fileName ==~ HTML_FILE_EXTENSION_PATTERN)
    }

    /**
     * checks if @param fileName represents an image file name,
     * ignoring the files' content.
     * @param fileName
     */
    public static Boolean isImageFileName(String fileName) {
        return (fileName ==~ IMAGE_FILE_EXTENSION_PATTERN)
    }

    /**
     * The main use-case for this class: returns all configured html files as Set<File>.
     * @param srcDir (mandatory) Directory where the html files are located. Type: File. Default: build/docs
     *            can be given as File or String
     * @param sourceDocs (optional) Defaults to all files in ${srcDir}.
     */
    public static Set<File> getHtmlFilesToCheck(File srcDir, Collection<String> sourceDocs) {
        // first case: no document names given -> return all html files
        // in directory
        if ((sourceDocs == null) || (sourceDocs?.empty)) {
            return getAllHtmlFilesFromDirectory(srcDir)
        } else {
            return getAllConfiguredHtmlFiles(srcDir, sourceDocs)
        }
    }

    // if for other types of input parameters, convert and delegate

    public static Set<File> getHtmlFilesToCheck(File srcDir, String sourceDoc) {
        return getHtmlFilesToCheck(srcDir, [sourceDoc])
    }

    public static Set<File> getHtmlFilesToCheck(String srcDirName, ArrayList<String> sourceDocs) {
        return getHtmlFilesToCheck(new File(srcDirName), sourceDocs)
    }

    public static Set<File> getHtmlFilesToCheck(String srcDirName, String sourceDocName) {
        return getHtmlFilesToCheck(new File(srcDirName), [sourceDocName])
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
     * returns all html files in a given directory.
     * (recursively looks in subdirectories)
     * @param dir where to look for matching files
     * @return all files with appropriate extension
     */
    public static Set<File> getAllHtmlFilesFromDirectory(File dir) {
        Set<File> files = new HashSet<File>()

        // scan only files, not directories
        dir.eachFileRecurse(FileType.FILES) { file ->
            if (isHtmlFile(file)) {
                files.add(file)
            }
        }

        return files
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
     * returns all configured html files from @param srcDocs
     * which really exist below @param srcDocs
     */
    public static Set<File> getAllConfiguredHtmlFiles(File srcDir, Collection<String> srcDocs) {
        Set<File> files = new HashSet<File>()

        srcDocs.each { configuredFileName ->
            File file = new File(srcDir, configuredFileName)
            if (file.exists()) {
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

