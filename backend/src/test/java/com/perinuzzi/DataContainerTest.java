package com.perinuzzi;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


public class DataContainerTest {

    // Tests for isPure()

    @Test
    @DisplayName("isPure: Should return true when all labels are '0'")
    void isPure_AllZeros_ReturnsTrue() {
        DataContainer dataContainer = new DataContainer();
        dataContainer.setCountLabel0Test(5);
        dataContainer.setCountLabel1Test(0);
        assertTrue(dataContainer.isPure(), "Data container with only '0' labels should be pure.");
    }

    @Test
    @DisplayName("isPure: Should return true when all labels are '1'")
    void isPure_AllOnes_ReturnsTrue() {
        DataContainer dataContainer = new DataContainer();
        dataContainer.setCountLabel0Test(0);
        dataContainer.setCountLabel1Test(7);
        assertTrue(dataContainer.isPure(), "Data container with only '1' labels should be pure.");
    }

    @Test
    @DisplayName("isPure: Should return true when data is empty (both counts are 0)")
    void isPure_EmptyData_ReturnsTrue() {
        DataContainer dataContainer = new DataContainer();
        dataContainer.setCountLabel0Test(0);
        dataContainer.setCountLabel1Test(0);
        assertTrue(dataContainer.isPure(), "Empty data container (0,0 counts) should be pure.");
    }

    @Test
    @DisplayName("isPure: Should return false when labels are mixed")
    void isPure_MixedLabels_ReturnsFalse() {
        DataContainer dataContainer = new DataContainer();
        dataContainer.setCountLabel0Test(3);
        dataContainer.setCountLabel1Test(2);
        assertFalse(dataContainer.isPure(), "Data container with mixed labels should not be pure.");
    }

    // Tests for isEmpty() 

    @Test
    @DisplayName("isEmpty: Should return true for an empty data container (0 rows, 0 columns)")
    void isEmpty_EmptyData_ReturnsTrue() {
        DataContainer dataContainer = new DataContainer();
        dataContainer.setRows(0);
        dataContainer.setColumns(0);
        assertTrue(dataContainer.isEmpty(), "Data container with 0 rows and 0 columns should be empty.");
    }

    @Test
    @DisplayName("isEmpty: Should return false for a non-empty data container")
    void isEmpty_NonEmptyData_ReturnsFalse() {
        DataContainer dataContainer = new DataContainer();
        dataContainer.setRows(2);
        dataContainer.setColumns(3);
        assertFalse(dataContainer.isEmpty(), "Data container with rows and columns should not be empty.");
    }

    //  Tests for readUserInput() 

    @Test
    @DisplayName("readUserInput: Should parse comma-separated input correctly without missing values")
    void readUserInput_ValidInput_ParsesCorrectly() {
        String userInput = "1,2,3,4,5,6,7,8,9,10,11,12"; 
        DataContainer data = new DataContainer(userInput);

        assertEquals(1, data.getRows(), "User input should result in 1 row.");
        assertEquals(12, data.getColumns(), "User input should result in 12 columns.");

        String[] expectedRow = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12"};
        String[] actualRow = data.getRow(0);

        assertArrayEquals(expectedRow, actualRow, "User input row should be parsed correctly.");
    }

    @Test
    @DisplayName("readUserInput: Should handle missing values at the end (trailing comma) correctly")
    void readUserInput_TrailingComma_HandlesMissingValue() {
        String userInput = "1,2,3,4,5,6,7,8,9,10,11,";
        DataContainer data = new DataContainer(userInput);

        String[] actualRow = data.getRow(0);
        assertEquals(12, actualRow.length, "Row should still have 12 columns after imputation.");
        assertNotNull(actualRow[11], "The last column should not be null after imputation.");
        assertFalse(actualRow[11].isEmpty(), "The last column should not be empty after imputation.");
    }

    @Test
    @DisplayName("readUserInput: Should handle missing values in the middle correctly")
    void readUserInput_MiddleMissingValue_HandlesMissingValue() {
        String userInput = "1,2,,4,5,6,7,8,9,10,11,12"; // Missing value at index 2 
        DataContainer data = new DataContainer(userInput);

        String[] actualRow = data.getRow(0);
        assertEquals(12, actualRow.length, "Row should still have 12 columns after imputation.");
        assertNotNull(actualRow[2], "The missing column in the middle should not be null after imputation.");
        assertFalse(actualRow[2].isEmpty(), "The missing column in the middle should not be empty after imputation.");
    }
}