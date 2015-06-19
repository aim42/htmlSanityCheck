package org.aim42.htmlSanityCheck.suggest;

import net.ricecode.similarity.*;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Document some features of the StringSimilarity package from Ralph Allan Rice.
 * Based upon code from Ralph Allan Rice ralph.rice@gmail.com
 **/

public class StringSimilarityTest {

    @Test
    public void testScoreAll() {
        SimilarityStrategy strategy = new JaroWinklerStrategy();
        String target = "McDonalds";

        String c1 = "MacMahons";
        String c2 = "McPherson";
        String c3 = "McDonaldz";


        StringSimilarityService service = new StringSimilarityServiceImpl(strategy);
        List<String> features = new ArrayList<String>();
        features.add(c1);
        features.add(c2);
        features.add(c3);

        List<SimilarityScore> scores = service.scoreAll(features, target);

        assertEquals(3, scores.size());

        assertEquals( "McDonaldz is expected to be the best match", "McDonaldz",
                service.findTop(features, target).getKey());


    }


//
//	@Test
//	public void testFindTop() {
//		SimilarityStrategy strategy = mock(SimilarityStrategy.class);
//		String target = "McDonalds";
//		String c1 = "MacMahons";
//		String c2 = "McPherson";
//		String c3 = "McDonalds";
//
//		SimilarityScore expected = new SimilarityScore(c3, 1.000);
//
//		when(strategy.score(c1, target)).thenReturn(0.90);
//		when(strategy.score(c2, target)).thenReturn(0.74);
//		when(strategy.score(c3, target)).thenReturn(1.000);
//
//		StringSimilarityService service = new StringSimilarityServiceImpl(strategy);
//		List<String> features = new ArrayList<String>();
//		features.add(c1);
//		features.add(c2);
//		features.add(c3);
//
//		SimilarityScore top= service.findTop(features,target);
//		verify(strategy).score(c1, target);
//		verify(strategy).score(c2, target);
//		verify(strategy).score(c3, target);
//		assertEquals(expected, top);
//
//	}
//
//	@Test
//	public void testFindTop_Ascending() {
//		SimilarityStrategy strategy = mock(SimilarityStrategy.class);
//		String target = "McDonalds";
//		String c1 = "MacMahons";
//		String c2 = "McPherson";
//		String c3 = "McDonalds";
//
//		SimilarityScore expected = new SimilarityScore(c2, 0.74);
//
//		when(strategy.score(c1, target)).thenReturn(0.90);
//		when(strategy.score(c2, target)).thenReturn(0.74);
//		when(strategy.score(c3, target)).thenReturn(1.000);
//
//		StringSimilarityService service = new StringSimilarityServiceImpl(strategy);
//		List<String> features = new ArrayList<String>();
//		features.add(c1);
//		features.add(c2);
//		features.add(c3);
//
//		AscendingSimilarityScoreComparator comparator = new AscendingSimilarityScoreComparator();
//		SimilarityScore top= service.findTop(features,target, comparator);
//		verify(strategy).score(c1, target);
//		verify(strategy).score(c2, target);
//		verify(strategy).score(c3, target);
//		assertEquals(expected, top);
//	}
//
//	@Test
//	public void testFindTop_Descending() {
//		SimilarityStrategy strategy = mock(SimilarityStrategy.class);
//		String target = "McDonalds";
//		String c1 = "MacMahons";
//		String c2 = "McPherson";
//		String c3 = "McDonalds";
//
//		SimilarityScore expected = new SimilarityScore(c3, 1.000);
//
//		when(strategy.score(c1, target)).thenReturn(0.90);
//		when(strategy.score(c2, target)).thenReturn(0.74);
//		when(strategy.score(c3, target)).thenReturn(1.000);
//
//		StringSimilarityService service = new StringSimilarityServiceImpl(strategy);
//		List<String> features = new ArrayList<String>();
//		features.add(c1);
//		features.add(c2);
//		features.add(c3);
//
//		DescendingSimilarityScoreComparator comparator = new DescendingSimilarityScoreComparator();
//		SimilarityScore top= service.findTop(features,target, comparator);
//		verify(strategy).score(c1, target);
//		verify(strategy).score(c2, target);
//		verify(strategy).score(c3, target);
//		assertEquals(expected, top);
//
//	}
}



/************************************************************************
 * This is free software - without ANY guarantee!
 * <p/>
 * <p/>
 * Copyright Dr. Gernot Starke, arc42.org
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * <p/>
 * **********************************************************************
 */

