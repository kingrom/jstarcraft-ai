package com.jstarcraft.ai.math.algorithm.correlation.similarity;

import com.jstarcraft.ai.math.algorithm.correlation.AbstractSimilarityTestCase;
import com.jstarcraft.ai.math.algorithm.correlation.Correlation;
import com.jstarcraft.ai.math.algorithm.correlation.similarity.DiceCoefficientSimilarity;

public class DiceCoefficientSimilarityTestCase extends AbstractSimilarityTestCase {

    @Override
    protected Correlation getCorrelation() {
        return new DiceCoefficientSimilarity();
    }

}
