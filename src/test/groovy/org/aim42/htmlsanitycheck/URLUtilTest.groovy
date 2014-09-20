package org.aim42.htmlsanitycheck


import org.junit.Test

// see end-of-file for license information


class URLUtilTest extends GroovyTestCase {

    private static final AIM = "aim42.org"
    private static final IMG = "image.jpg"

    @Test
    public void testFileURL() {
        String fileURL = "file://$IMG"

        Boolean actual = URLUtil.isRemoteURL(fileURL)

        assertFalse ("$fileURL shall be recognized as file but wasn't",
                actual)

        fileURL = "$IMG"
        actual = URLUtil.isRemoteURL(fileURL)

        assertFalse ("$fileURL shall be recognized as file but wasn't",
                actual)


    }

    @Test
    public void testHTTPUrl() {
        String httpURL = "http://$AIM/$IMG"
        Boolean actual = URLUtil.isRemoteURL(httpURL)
        assertTrue ("$httpURL shall be recognized as remote url but wasn't", actual)

    }


    @Test
    public void testRemoteURLs() {
        def prefixes = ["http", "HTTP", "https", "HTTPS", "hTtPs",
                        "ftp", "FTP", "fTP", "telnet", "TELNET"]

        prefixes.each { prefix ->
            String url = prefix + "://$AIM/$IMG"
            assertTrue( "$prefix is remote URL but wasnt recognized",
                    URLUtil.isRemoteURL(url))
        }
    }

    @Test
    public void testRelativeFilePath() {
        def paths = ["./$IMG", "../$IMG", "$IMG",
                     "../../$IMG", "$IMG",
                     "images/aim42-logo.png"
                    ]

        paths.each { url ->
            assertFalse( "$url is local but was recognized as remote", URLUtil.isRemoteURL(url))
        }
    }
}
/************************************************************************
 * This is free software - without ANY guarantee!
 *
 *
 * Copyright 2013, Dr. Gernot Starke, arc42.org
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

