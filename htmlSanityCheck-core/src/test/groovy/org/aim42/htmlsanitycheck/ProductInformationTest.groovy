package org.aim42.htmlsanitycheck

import spock.lang.Specification
import spock.lang.Unroll

class ProductInformationTest extends Specification {

    final static String BAD_VERSION = "--.--.--"

    def "can get Version"() {
        String version

        when:
        version = ProductInformation.VERSION

        then:
        version != BAD_VERSION
        version != "version"
        version != "@version@"
        version != null
    }

    @Unroll
    def "can get git property #propertyKey"() {
        setup:
        String propertyValue = ProductInformation.getGitProperty(propertyKey)

        expect:
        propertyValue != null

        where:
        propertyKey << [
                'git.branch',
                'git.build.host',
                'git.build.user.email',
                'git.build.user.name',
                'git.build.version',
                'git.closest.tag.commit.count',
                'git.closest.tag.name',
                'git.commit.id',
                'git.commit.id.abbrev',
                'git.commit.id.describe',
                'git.commit.message.full',
                'git.commit.message.short',
                'git.commit.time',
                'git.commit.user.email',
                'git.commit.user.name',
                'git.dirty',
                'git.remote.origin.url',
                'git.tags',
                'git.total.commit.count'
        ]
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

