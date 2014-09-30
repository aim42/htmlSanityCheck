package org.aim42.htmlsanitycheck.report


import org.junit.Before
import org.junit.Test


class StatisticsTest extends GroovyTestCase {

    private Statistics statistics

    @Before
    public void setUp() {

    }

    @Test
    public void testEmptyStatistic() {
        statistics = new Statistics(0, 0)

        assertEquals("0 checks shall be 100% success", 100,
                statistics.percentSuccessful)
    }

    @Test
    public void testAllFailures() {
        statistics = new Statistics(1, 1)

        assertEquals("1 check, no fail shall be 0% success", 0,
                statistics.percentSuccessful)
    }

    @Test
    public void testHalfFailures() {
        statistics = new Statistics( 2, 1 )

        assertEquals( "2 checks, 1 fail shall be 50% success", 50,
                statistics.percentSuccessful)
        }


    @Test
    public void testQuarterFailures() {
        statistics = new Statistics( 4, 1 )

        assertEquals( "4 checks, 1 fail shall be 75% success", 75,
                statistics.percentSuccessful)
    }


    @Test
    public void testThirdFailures() {
        statistics = new Statistics( 3, 1 )

        assertEquals( "3 checks, 1 fail shall be 66% success", 66,
                statistics.percentSuccessful)
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
