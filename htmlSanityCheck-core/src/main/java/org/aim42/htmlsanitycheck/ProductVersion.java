package org.aim42.htmlsanitycheck;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.util.Properties;

/**
 * provides the current product version,
 * as configured in src/main/resources/product-version.properties.
 * Code proposed by René Gröschke of Gradleware in
 * https://discuss.gradle.org/t/access-constant-from-groovy-class-in-gradle-buildfile/10571/5
 */
public class ProductVersion {

    private static final Logger logger = LoggerFactory.getLogger(ProductVersion.class);

    public static String getVersion() {
        try {
            final URL RESOURCE = ProductVersion.class.getClassLoader().getResource("product-version.properties");
            Properties props = new Properties();
            props.load(RESOURCE.openConnection().getInputStream());
            return props.getProperty("version");
        } catch (IOException E) {
            logger.debug("ProductVersion cannot be obtained due to IOException.");
        }
        return "[unknown]";
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

