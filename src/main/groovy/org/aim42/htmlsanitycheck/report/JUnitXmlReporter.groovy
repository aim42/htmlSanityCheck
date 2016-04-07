package org.aim42.htmlsanitycheck.report

import groovy.transform.InheritConstructors
import groovy.xml.MarkupBuilder
import org.aim42.htmlsanitycheck.collect.PerRunResults
import org.aim42.htmlsanitycheck.collect.SingleCheckResults
import org.aim42.htmlsanitycheck.collect.SinglePageResults

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

/**
 * Write the findings report to JUnit XML. Allows tools processing JUnit to
 * include the findings.
 */
@InheritConstructors
class JUnitXmlReporter extends Reporter {
	File outputPath

	JUnitXmlReporter( PerRunResults runResults, String outputPath) {
        super( runResults )
		this.outputPath = new File(outputPath)
    }

    @Override
    void initReport() {
		if (!outputPath.canWrite() && !outputPath.mkdirs()) {
			throw new IOException("Cannot create or write to ${outputPath}")
		}
    }

    @Override
    void reportOverallSummary() {
    }

    @Override
    void reportPageSummary( SinglePageResults pageResult ) {
		String name = pageResult.pageFilePath ?: pageResult.pageTitle ?: UUID.randomUUID()
		String sanitiziedPath = name.replaceAll(~/[^A-Za-z0-9_-]+/, '_')
		File testOutputFile = new File(outputPath, "TEST-${sanitiziedPath}.xml")
		testOutputFile.withWriter { writer ->
			def builder = new MarkupBuilder(writer)
			builder.testsuite(
				tests: pageResult.nrOfItemsCheckedOnPage(),
				failures: pageResult.nrOfFindingsOnPage(), 
				errors:0,
				time:0,
				name:name) {
				pageResult.singleCheckResults?.each { singleCheckResult ->
					testcase(
						assertions:singleCheckResult.nrOfItemsChecked,
						time:0,
						name:(singleCheckResult.whatIsChecked ?: '')
					) {
						singleCheckResult.findings.each { finding ->
							failure(
								type:"${[singleCheckResult.sourceItemName, singleCheckResult.targetItemName].findAll().join(' -> ')}", 
								message:finding.item, 
								finding.suggestions?.join(', ') ?: '')
						}
					}
				}
			}
		}
    }

    @Override
    protected void reportPageFooter() {
    }

    @Override
    protected void reportSingleCheckSummary( SingleCheckResults checkResults ) {
    }

    @Override
    protected void reportSingleCheckDetails( SingleCheckResults checkResults  ) {
    }

    @Override
    void closeReport() {
    }

}
