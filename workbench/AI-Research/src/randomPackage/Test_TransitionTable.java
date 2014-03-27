package randomPackage;

import static org.junit.Assert.*;

import org.junit.Test;

public class Test_TransitionTable {
	
	/**
	 * testTransitionTable()
	 *
	 * Test the functionality of the constructor (TransitionTable())
	 *
	 * Cases Tested:
	 * 				- When a new table is created, check for a null table 
	 * 				- Make sure that the size of the table is one (null row inserted properly)
	 */
	@Test
	public void testTransitionTable() {
		TransitionTable transitionTable = new TransitionTable();
		assertNotNull("Make sure the Table is not Null", transitionTable);
		assertTrue("Make sure that there is one null row at index 0.", transitionTable.table.get(0) == null && transitionTable.table.size() == 1);
	}
	
	/**
	 * testAddNullRow()
	 *
	 * Test the functionality of the addNullRow method
	 *
	 * Cases Tested:
	 * 				- Ensure the size of the table increases by one when a null row is added
	 * 
	 * @Test
	 */
	public void testAddNullRow(){
		TransitionTable transitionTable = new TransitionTable();
		int sizeBeforeAdd = transitionTable.table.size();
		transitionTable.addNullRow();
		int sizeAfterAdd = transitionTable.table.size();
		assertTrue("There is one more row in the table after a null row is added", (sizeAfterAdd - sizeBeforeAdd) == 1);
	}
	
	/**
	 * testAddEmptyRow()
	 *
	 * Test the functionality of the addEmptyRow method
	 *
	 * Cases Tested: 
	 * 				- Ensure the size of the table increases by one when an empty row is added
	 *				- Ensure that all values in the new row are '-1' 
	 * 				- Ensure that the length of the row is the same as the length of the alphabet
	 *
	 * @Test
	 */
	public void testAddEmptyRow(){
		TransitionTable transitionTable = new TransitionTable();
		
		transitionTable.addEmptyRow();
		
		StateID[] correctRow = new StateID[transitionTable.alphabet.length];
		for(int i=0;i<correctRow.length;i++) {
			correctRow[i] = new StateID(-1);
		}
		
		assertNotNull("Make sure that row 1 is not null.", transitionTable.table.get(1));
		assertTrue("Make sure that all values in the row are -1", checkRowValues(transitionTable.table.get(1), correctRow));
		assertTrue("Make sure that the row length = alphabet length", isCorrectLength(transitionTable.table.get(1), transitionTable.alphabet.length));
	}
	
	/**
	 * testAddRow()
	 *
	 * Test the functionality of adding a given row to the table 
	 *
	 * @Test
	 */
	public void testAddRow() {
		TransitionTable transitionTable = new TransitionTable();
		
		int numRowsBeforeAdd = transitionTable.size();
		
		transitionTable.addRow(createRow(9000,transitionTable.alphabet.length));
		
		int numRowsAfterAdd = transitionTable.size();
		
		boolean numRowsCorrect = numRowsAfterAdd - 1 == numRowsBeforeAdd;
		
		int maxIndexOfTable = numRowsAfterAdd - 1;
		
		assertTrue("Make sure the size of the table increases by one when a row is added",numRowsCorrect);
		assertTrue("Make sure that the row added is at the end of the table",checkRowPosition(transitionTable,newRow,maxIndexOfTable));
		assertTrue("Make sure that the values in the row are as expected",checkRowValues(transitionTable.table.get(maxIndexOfTable),newRow));
	}
	
	/**
	 * testIsTransitionTableFull()
	 *
	 * Test the functionality of the method that checks whether or not a table is full
	 * 
	 * @Test
	 */
	public void testIsTransitionTableFull() {
		TransitionTable transitionTable = new TransitionTable();
		
		assertTrue("With no rows added the table should be full as there are no unknown transitions",transitionTable.table.size() == 1);
		
		transitionTable.addNullRow();
		
		assertTrue("With a null row added, the table is still full",!doesTableContainValues(transitionTable));
		
		transitionTable.addRow(createRow(9000,transitionTable.alphabet.length));
		
		assertTrue("With a full row added with no unknown transitions, the table is still full",!containsUnknownTransitions(transitionTable));
		
		transitionTable.addEmptyRow();
		
		assertTrue("With an empty row added to the table, the table is not full",containsUnknownTransitions(transitionTable));
	} 
	
	
	//---------------------------------HELPER FUNCTIONS-------------------------------------//
	
	
	/**
	 * checkRowValues()
	 *
	 * Checks two rows for equivalence
	 *
	 * @param rowToValidate - The row that needs to be validated
	 * @param correctRow    - The control row used for comparison
	 *
	 * @return boolean - True if the two rows are equal --OR-- False if the two rows are different
	 *
	 */
	private boolean checkRowValues(StateID[] rowToValidate, StateID[] correctRow){
		if(rowToValidate.length != correctRow.length) return false;
		
		for(int i = 0; i<row.length; ++i ){
			if(rowToValidate[i].get() != correctRow[i].get()){
				return false;
			}
		}
		
		return true;
	}
	
	/**
	 * isCorrectLength()
	 *
	 * Checks a row for correct length
	 *
	 * @param row    - The row that needs to be validated
	 * @param length - The length the row is assumed to be
	 *
	 * @return boolean - True if the length of the row matches the given length
	 *                   --OR-- False if the length of the row differs from the given length
	 *
	 */
	private boolean isCorrectLength(StateID[] row, int length){
		return row.length == length;
	}
	
	/**
	 * checkRowPosition()
	 * 
	 * Ensures that the row given is at the specified location. Given a table, a row index 
	 * of that table and an array of StateID's, checks to make sure 
	 * that the row at the given index is equivalent to the given row.
	 *
	 * @param table    - The table in question
	 * @param row      - The row of StateID's in question
	 * @param rowIndex - The suspected index of the row
	 *
	 * @return boolean - True if the row at the given index matches the given row --OR--
	 *					 False if the row does not match the row at the given index
	 */
	public boolean checkRowPosition(TransitionTable table, StateID[] row, int rowIndex) {
		return checkRowValues(table.get(rowIndex), row);
	}
	
	/**
	 * doesTableContainValues()
	 *
	 * Given a TransitionTable, checks to see if the table contains any values or if
	 * all rows are null
	 * 
	 *
	 * @param table - The transition table in question
	 * 
	 * @return boolean - True if the table contains values --OR-- False if the table has only null entries
	 */
	public boolean doesTableContainValues(TransitionTable table) {
		for(StateID[] row : table) {
			if(row != null) return true;
		}
		return false;
	}
	
	/**
	 * createRow()
	 *
	 * create a non empty array of StateID's, all values must not be equal to '-1'
	 *
	 * @param value - The Value to be placed in each slot of the array
	 * 				  This value should most likely be 9000 in reference to the 
	 *                jackhawk9000 which is of course available at walmart
	 * 
	 * @param length - The length of the array to be created
	 * 
	 * @return StateID[] - An array of StateID's with specified values 
	 */
	public StateID[] createRow(int value, int length) {
		StateID[] row = new StateID[length];
		for(StateID stateId : row) {
			stateID = new StateID(value);
		}
		return row;
	}
	
	/**
	 * containsUnknownTransitions()
	 *
	 * Given a TransitionTable, checks to see if the table contains any values equal to '-1'
	 * 
	 * @param table - The transition table in question
	 * 
	 * @return boolean - True if the table contains '-1' --OR-- False if the table has only 'other' entries
	 */
	public boolean containsUnknownTransitions(TransitionTable table) {
		for(StateID[] row : table) {
			for(int i=0;row!=null && i<row.length;++i) {
				if(row[i].get() == -1) {
					return true;
				}
			}
		}
		return false;
	}
}
