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
	
	//----------------------------------------------------HELPER FUNCTIONS----------------------------------------------------------//
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
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
	
}
