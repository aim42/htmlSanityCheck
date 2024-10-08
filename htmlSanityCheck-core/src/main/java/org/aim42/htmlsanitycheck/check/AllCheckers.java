package org.aim42.htmlsanitycheck.check;

import java.util.Arrays;
import java.util.List;

/* ***********************************************************************
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
    public static final List<Class<? extends Checker>> CHECKER_CLASSES =
            Arrays.asList(
                    // tag::checker-classes[]
                    // Keep the list ordering to ensure
                    // report ordering comparability
                    // with HSC 1.x versions
                    MissingAltInImageTagsChecker.class,
                    MissingImageFilesChecker.class,
                    DuplicateIdChecker.class,
                    BrokenHttpLinksChecker.class,
                    ImageMapChecker.class,
                    BrokenCrossReferencesChecker.class,
                    MissingLocalResourcesChecker.class
                    // end::checker-classes[]
            );
}
