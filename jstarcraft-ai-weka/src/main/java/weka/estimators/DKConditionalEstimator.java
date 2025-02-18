/*
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

/*
 *    DKConditionalEstimator.java
 *    Copyright (C) 1999-2012 University of Waikato, Hamilton, New Zealand
 *
 */

package weka.estimators;

import weka.core.RevisionUtils;

/**
 * Conditional probability estimator for a discrete domain conditional upon a
 * numeric domain.
 *
 * @author Len Trigg (trigg@cs.waikato.ac.nz)
 * @version $Revision$
 */
public class DKConditionalEstimator implements ConditionalEstimator {

    /** Hold the sub-estimators */
    private KernelEstimator[] m_Estimators;

    /** Hold the weights for each of the sub-estimators */
    private DiscreteEstimator m_Weights;

    /**
     * Constructor
     *
     * @param numSymbols the number of symbols
     * @param precision  the precision to which numeric values are given. For
     *                   example, if the precision is stated to be 0.1, the values
     *                   in the interval (0.25,0.35] are all treated as 0.3.
     */
    public DKConditionalEstimator(int numSymbols, double precision) {

        m_Estimators = new KernelEstimator[numSymbols];
        for (int i = 0; i < numSymbols; i++) {
            m_Estimators[i] = new KernelEstimator(precision);
        }
        m_Weights = new DiscreteEstimator(numSymbols, true);
    }

    /**
     * Add a new data value to the current estimator.
     *
     * @param data   the new data value
     * @param given  the new value that data is conditional upon
     * @param weight the weight assigned to the data value
     */
    public void addValue(double data, double given, double weight) {

        m_Estimators[(int) data].addValue(given, weight);
        m_Weights.addValue((int) data, weight);
    }

    /**
     * Get a probability estimator for a value
     *
     * @param given the new value that data is conditional upon
     * @return the estimator for the supplied value given the condition
     */
    public Estimator getEstimator(double given) {

        Estimator result = new DiscreteEstimator(m_Estimators.length, false);
        for (int i = 0; i < m_Estimators.length; i++) {
            // System.out.println("Val " + i
            // + " Weight:" + m_Weights.getProbability(i)
            // +" EstProb(" + given + ")="
            // + m_Estimators[i].getProbability(given));
            result.addValue(i, m_Weights.getProbability(i) * m_Estimators[i].getProbability(given));
        }
        return result;
    }

    /**
     * Get a probability estimate for a value
     *
     * @param data  the value to estimate the probability of
     * @param given the new value that data is conditional upon
     * @return the estimated probability of the supplied value
     */
    public double getProbability(double data, double given) {

        return getEstimator(given).getProbability(data);
    }

    /**
     * Display a representation of this estimator
     */
    public String toString() {

        String result = "DK Conditional Estimator. " + m_Estimators.length + " sub-estimators:\n";
        for (int i = 0; i < m_Estimators.length; i++) {
            result += "Sub-estimator " + i + ": " + m_Estimators[i];
        }
        result += "Weights of each estimator given by " + m_Weights;
        return result;
    }

    /**
     * Returns the revision string.
     * 
     * @return the revision
     */
    public String getRevision() {
        return RevisionUtils.extract("$Revision$");
    }

    /**
     * Main method for testing this class.
     *
     * @param argv should contain a sequence of pairs of integers which will be
     *             treated as pairs of symbolic, numeric.
     */
    public static void main(String[] argv) {

        try {
            if (argv.length == 0) {
                System.out.println("Please specify a set of instances.");
                return;
            }
            int currentA = Integer.parseInt(argv[0]);
            int maxA = currentA;
            int currentB = Integer.parseInt(argv[1]);
            int maxB = currentB;
            for (int i = 2; i < argv.length - 1; i += 2) {
                currentA = Integer.parseInt(argv[i]);
                currentB = Integer.parseInt(argv[i + 1]);
                if (currentA > maxA) {
                    maxA = currentA;
                }
                if (currentB > maxB) {
                    maxB = currentB;
                }
            }
            DKConditionalEstimator newEst = new DKConditionalEstimator(maxA + 1, 1);
            for (int i = 0; i < argv.length - 1; i += 2) {
                currentA = Integer.parseInt(argv[i]);
                currentB = Integer.parseInt(argv[i + 1]);
                System.out.println(newEst);
                System.out.println("Prediction for " + currentA + '|' + currentB + " = " + newEst.getProbability(currentA, currentB));
                newEst.addValue(currentA, currentB, 1);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
