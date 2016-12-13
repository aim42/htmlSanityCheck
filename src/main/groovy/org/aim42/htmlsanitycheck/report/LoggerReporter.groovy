package org.aim42.htmlsanitycheck.report

import groovy.transform.InheritConstructors
import org.aim42.htmlsanitycheck.ProductVersion
import org.aim42.htmlsanitycheck.collect.PerRunResults
import org.aim42.htmlsanitycheck.collect.SingleCheckResults
import org.aim42.htmlsanitycheck.collect.SinglePageResults
import org.slf4j.Logger

/************************************************************************
 * This is free software - without ANY guarantee!
 *
 *
 * Copyright 2016, Patrick Double, https://github.com/double16
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

@InheritConstructors
class LoggerReporter extends ConsoleReporter {

    public LoggerReporter( PerRunResults runResults, Logger logger ) {
        super( runResults )
		printer = { line -> logger.info(line) }
    }

}

