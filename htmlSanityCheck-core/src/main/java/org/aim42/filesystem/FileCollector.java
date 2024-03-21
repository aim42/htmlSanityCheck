package org.aim42.filesystem;

// see end-of-file for license information

// TODO: add exclude-patterns

import java.io.File;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

public class FileCollector {

    private static final Pattern IMAGE_FILE_EXTENSION_PATTERN = Pattern.compile("(?i).+\\.(jpg|jpeg|png|gif|bmp|svg)?$");

    public static boolean isImageFileName(String fileName) {
        return IMAGE_FILE_EXTENSION_PATTERN.matcher(fileName).matches();
    }

    /**
     * @return all configured image files as Set<File>
     */
    public static Set<File> getConfiguredImageFiles(File srcDir, Set<String> sourceDocs) {
        // first case: no document names given -> return all html files
        // in a directory tree
        if (sourceDocs == null || sourceDocs.isEmpty()) {
            return getAllImageFilesFromDirectory(srcDir);
        } else {
            return getAllConfiguredImageFiles(srcDir, sourceDocs);
        }
    }

    /**
     * Returns all image files in a given directory.
     * (recursively looks in subdirectories)
     *
     * @param dir where to look for matching files
     * @return all files with the appropriate extension
     */
    public static Set<File> getAllImageFilesFromDirectory(File dir) {
        Set<File> files = new HashSet<>();
        File[] foundFiles = dir.listFiles();

        if (foundFiles != null) {
            for (File file : foundFiles) {
                if (file.isFile()) {
                    if (isImageFileName(file.getName())) {
                        files.add(file);
                    }
                } else if (file.isDirectory()) {
                    files.addAll(getAllImageFilesFromDirectory(file));
                }
            }
        }

        return files;
    }

    /**
     * Returns all configured image files from @param srcDocs
     * which really exist below @param srcDocs
     */
    public static Set<File> getAllConfiguredImageFiles(File srcDir, Set<String> srcDocs) {
        Set<File> files = new HashSet<>();
        for (String srcFileName : srcDocs) {
            File file = new File(srcDir, srcFileName);
            if (file.exists() && isImageFileName(file.getName())) {
                files.add(file);
            }
        }
        return files;
    }

    /**
     * Convert Set of Files to Set of file-names without a path prefix!
     */
    public static Set<String> collectFileNamesFromFiles(Set<File> files) {
        Set<String> fileNames = new HashSet<>();
        for (File file : files) {
            fileNames.add(file.getName());
        }
        return fileNames;
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
 "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 ========================================================================*/

