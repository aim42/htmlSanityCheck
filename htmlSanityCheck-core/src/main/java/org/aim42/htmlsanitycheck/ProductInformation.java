package org.aim42.htmlsanitycheck;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.util.Properties;

/**
 * Provides the current product version,
 * as configured in src/main/resources/product-version.properties.
 * Code proposed by René Gröschke of Gradleware in
 * <a href="https://discuss.gradle.org/t/access-constant-from-groovy-class-in-gradle-buildfile/10571/5">...</a>
 */
public class ProductInformation {
    private ProductInformation() {

    }

    private static final Logger logger = LoggerFactory.getLogger(ProductInformation.class);

    public static final Properties PRODUCT_PROPERTIES = getProperties("product-version.properties"); //NOSONAR(S2386)
    public static final String VERSION = PRODUCT_PROPERTIES.getProperty("version") == null
            ? "[unknown]"
            : PRODUCT_PROPERTIES.getProperty("version");
    public static final Properties GIT_PROPERTIES = getProperties("git.properties"); //NOSONAR(S2386)

    public static String getGitProperty (final String propertyKey) {
        return GIT_PROPERTIES.getProperty(propertyKey);
    }

    private static Properties getProperties(final String propertyFilename) {
        Properties props = new Properties();
        try {
            final URL resource = ProductInformation.class.getClassLoader().getResource(propertyFilename);
            if (resource != null) {
                props.load(resource.openConnection().getInputStream());
            } else {
                logger.warn("Couldn't obtain URL resource for '{}'", propertyFilename);
            }
        } catch (IOException E) {
            logger.warn("'{}' cannot be obtained due to IOException.", propertyFilename);
        }
        return props;
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

