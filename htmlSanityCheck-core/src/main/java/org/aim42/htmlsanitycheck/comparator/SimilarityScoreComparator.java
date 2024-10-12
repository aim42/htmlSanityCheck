package org.aim42.htmlsanitycheck.comparator;

import net.ricecode.similarity.SimilarityScore;

import java.math.BigDecimal;
import java.util.Comparator;

public class SimilarityScoreComparator implements Comparator <SimilarityScore> {
    @Override
    public int compare(SimilarityScore o1, SimilarityScore o2) {
        BigDecimal score1 = BigDecimal.valueOf(o1.getScore());
        BigDecimal score2 = BigDecimal.valueOf(o2.getScore());
        return score2.compareTo(score1);
    }
}
