package org.aim42.htmlsanitycheck.check;

import java.util.Arrays;
import java.util.LinkedHashSet;

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
public class AllCheckers {
    public static final LinkedHashSet<Class<? extends Checker>> checkerClazzes =
            new LinkedHashSet<Class<? extends Checker>>(
                    Arrays.asList(
                            BrokenCrossReferencesChecker.class,
                            BrokenHttpLinksChecker.class,
                            DuplicateIdChecker.class,
                            ImageMapChecker.class,
                            MissingAltInImageTagsChecker.class,
                            MissingImageFilesChecker.class,
                            MissingLocalResourcesChecker.class));
}
