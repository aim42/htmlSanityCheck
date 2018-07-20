package org.aim42.filesystem

import java.nio.file.Path

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

class FileUtil {


    static File commonPath(Collection<File> files) {
        if (!files) {
            return null
        }
        if (files.size() == 1) {
            return files.first().parentFile
        }
        Path initial = files.first().toPath().parent
        Path common = files.collect { it.toPath() }.inject(initial) {
            Path acc, Path val ->
            if (!acc || !val) {
                return null
            }

            int idx = 0
            Path p1 = acc, p2 = val.parent
            def iter1 = p1.iterator(), iter2 = p2.iterator()
            while (iter1.hasNext() && iter2.hasNext() && iter1.next() == iter2.next()) {
                idx++
            }
            if (idx == 0) {
                return null
            }
            p1.subpath(0, idx)
        }
        common ? (initial?.root ? initial.root.resolve(common).toFile() : common.toFile()) : null
    }
}
