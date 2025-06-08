package com.perinuzzi;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DecisionTreeTest {

    // Instantiate DecisionTree once for all tests in this class
    private DecisionTree decisionTree = new DecisionTree();

    @Test
    @DisplayName("Feature Selection: Selected indices should be unique and array size correct")
    public void featureSelectIndexShouldBeUnique() {
        int size = 3;
        int[] randColsIndx = decisionTree.featureSelect(size); 

        for (int i = 0; i < randColsIndx.length - 1; i++) {
            for (int j = i + 1; j < randColsIndx.length; j++) {
                assertNotEquals(randColsIndx[i], randColsIndx[j], "Indices should be unique");
            }
        }
        assertEquals(size, randColsIndx.length, "The array should have the expected size");
    }

    @Test
    @DisplayName("Gini Impurity: Should return 0.0 for pure sets (all one label)")
    void calculateGiniImpurity_PureSet_ReturnsZero() {
        // Test case 1: All Label 0
        int count0_1 = 10;
        int count1_1 = 0;
        double expectedGini1 = 0.0;
        double actualGini1 = decisionTree.calculateGiniImpurity(count0_1, count1_1);
        assertEquals(expectedGini1, actualGini1, 0.0001, "Gini impurity for all Label 0 should be 0.0");

        // Test case 2: All Label 1
        int count0_2 = 0;
        int count1_2 = 5;
        double expectedGini2 = 0.0;
        double actualGini2 = decisionTree.calculateGiniImpurity(count0_2, count1_2);
        assertEquals(expectedGini2, actualGini2, 0.0001, "Gini impurity for all Label 1 should be 0.0");
    }

    @Test
    @DisplayName("Gini Impurity: Should return 0.5 for a perfectly split set (50/50)")
    void calculateGiniImpurity_FiftyFiftySplit_ReturnsHalf() {
        int count0 = 5;
        int count1 = 5;
        double expectedGini = 0.5; 
        double actualGini = decisionTree.calculateGiniImpurity(count0, count1);
        assertEquals(expectedGini, actualGini, 0.0001, "Gini impurity for 50/50 split should be 0.5");
    }

    @ParameterizedTest
    @CsvSource({
        "1, 1, 0.5",     // 50/50 split
        "2, 2, 0.5",     // 50/50 split
        "3, 0, 0.0",     // Pure set (all 0)
        "0, 4, 0.0",     // Pure set (all 1)
        "3, 1, 0.375",   // 3/4, 1/4
        "1, 9, 0.18",    // 1/10, 9/10
        "0, 0, 0.0"      // Edge case: no samples
    })
    @DisplayName("Gini Impurity: Parameterized tests for various counts")
    void calculateGiniImpurity_VariousCounts_ReturnsCorrectValue(int count0, int count1, double expectedGini) {
        double actualGini = decisionTree.calculateGiniImpurity(count0, count1);
        // Delta of 0.0001 for double comparison due to potential floating point inaccuracies
        assertEquals(expectedGini, actualGini, 0.0001,
            String.format("Gini impurity for counts (%d, %d) should be %f", count0, count1, expectedGini));
    }


    @Test
    @DisplayName("Gini Impurity: Should throw IllegalArgumentException for negative counts")
    void calculateGiniImpurity_NegativeCounts_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> {
            decisionTree.calculateGiniImpurity(-1, 5);
        }, "Should throw IllegalArgumentException for negative count0");

        assertThrows(IllegalArgumentException.class, () -> {
            decisionTree.calculateGiniImpurity(5, -1);
        }, "Should throw IllegalArgumentException for negative count1");

        assertThrows(IllegalArgumentException.class, () -> {
            decisionTree.calculateGiniImpurity(-1, -1);
        }, "Should throw IllegalArgumentException for both negative counts");
    }

}