package org.aim42.filesystem

/** helper class to facilitate testing of file system tests **/

class DirAndFileCreator {
    /*
      helper method to create temporary directories and files within those
      Expects @param dirsAndFiles to look like
      [["/a", ["a.htm"]]]
       */

    public static void createDirsAndFiles(Set dirsAndFiles, File whereToCreate) {
        File dir, file
        dirsAndFiles.each { aDir, fileSet ->
            dir = new File(whereToCreate, (String) aDir)
            dir.mkdirs()
            fileSet.each { fileName ->
                file = new File((File) dir, (String) fileName) << "some content"
            }

        }
    }
}

/************************************************************************
 * This is free software - without ANY guarantee!
 *
 *
 * Copyright Dr. Gernot Starke, arc42.org
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

