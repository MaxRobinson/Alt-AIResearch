package randomPackage;

import static org.junit.Assert.*;

import org.junit.Test;
import java.util.ArrayList;

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
		boolean nullRowAtIndexZero = transitionTable.table.get(0) == null && transitionTable.table.size() == 1;
		assertTrue("Make sure that there is one null row at index 0.", nullRowAtIndexZero);
		
		//Test non-default constructor
		char[] alphabet = createAlphabet(5);
		
		transitionTable = new TransitionTable(alphabet);
		assertNotNull("Make sure the Table is not Null", transitionTable);
		nullRowAtIndexZero = transitionTable.table.get(0) == null && transitionTable.table.size() == 1;
		assertTrue("Make sure that there is one null row at index 0.", nullRowAtIndexZero);
		transitionTable.table.add(new StateID[alphabet.length]);
		int maxIndex = transitionTable.table.size() -1;
		boolean alphabetLengthCorrect = isCorrectLength(transitionTable.table.get(maxIndex),alphabet.length);
		assertTrue("Make sure that the table's rows are the same length as the alphabet provided",alphabetLengthCorrect);
	}

	/**
	 * testAddNullRow()
	 *
	 * Test the functionality of the addNullRow method
	 *
	 * Cases Tested:
	 * 				- Ensure the size of the table increases by one when a null row is added
	 * 
	 */
	@Test
	public void testAddNullRow(){
		char[] alphabet = createAlphabet(5);
		
		TransitionTable transitionTable = new TransitionTable(alphabet);
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
	 */
	@Test
	public void testAddEmptyRow(){
		char[] alphabet = createAlphabet(5);
		
		TransitionTable transitionTable = new TransitionTable(alphabet);
		
		transitionTable.addEmptyRow();

        //This is what the row that was added *should* be
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
	 */
	@Test
	public void testAddRow() {
		char[] alphabet = createAlphabet(5);
		
		TransitionTable transitionTable = new TransitionTable(alphabet);
		
		int numRowsBeforeAdd = transitionTable.table.size();
		
		StateID[] newRow = createRow(9000,transitionTable.alphabet.length);
		
		transitionTable.addRow(newRow);
		
		int numRowsAfterAdd = transitionTable.table.size();
		
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
	 */
	@Test
	public void testIsTransitionTableFull() {
		char[] alphabet = createAlphabet(5);
		
		TransitionTable transitionTable = new TransitionTable(alphabet);
		
		assertTrue("With no rows added the table should be full as there are no unknown transitions",transitionTable.table.size() == 1);
		
		transitionTable.addNullRow();
		
		assertTrue("With a null row added, the table is still full",!doesTableContainValues(transitionTable));
		
		transitionTable.addRow(createRow(9000,transitionTable.alphabet.length));
		
		assertTrue("With a full row added with no unknown transitions, the table is still full",!containsUnknownTransitions(transitionTable));
		
		transitionTable.addEmptyRow();
		
		assertTrue("With an empty row added to the table, the table is not full",containsUnknownTransitions(transitionTable));
	} 
	/**
	 * testUpdateSingleTransition()
	 * 
	 * Test the functionality of the UpdateSingleTransition method 
	 * 
	 */
	@Test
	public void testUpdateSingleTransition() {
		char[] alphabet = createAlphabet(5);
		TransitionTable transitionTable = new TransitionTable(alphabet);
		
		StateID[] newRow = createRow(2,transitionTable.alphabet.length);
		StateID[] newRow2 = createRow(3,transitionTable.alphabet.length);
		
		transitionTable.addRow(newRow);
		transitionTable.addRow(newRow2);
		
		StateID beforeUpdate = transitionTable.table.get(1)[2];
		
		Episode source = new Episode(' ',-1,1);
		Episode target = new Episode('c',-1,9000);
		
		transitionTable.updateSingleTransition(source,target);
		
		StateID afterUpdate = transitionTable.table.get(1)[2];
		
		assertTrue("Ensure that a value in the table is changed",beforeUpdate.get() != afterUpdate.get());
		assertTrue("The value was actually updated to the appropriate value",afterUpdate.get() == 9000);
		
	}
	
	/**
	 * testAddPath()
	 * 
	 * Test the functionality of the addPath method 
	 * 
	 */
	@Test
	public void testAddPath() {
		char[] alphabet = createAlphabet(5);
		TransitionTable transitionTable = new TransitionTable(alphabet);

        //Create an initial state
		StateID[] starterRow = createRow(2,transitionTable.alphabet.length);
        transitionTable.addRow(starterRow);

        //create an array with 5 episodes
        ArrayList<Episode> eps = new ArrayList<Episode>();
        for(int i = 0; i < 5; ++i) {
            eps.add(new Episode(alphabet[i],-1,i+2));
        }

        //Use the addPath method with the list
        transitionTable.addPath(eps);

        //Verify they were added properly
        for(int i = 1; i < 5; ++i) {
            assertTrue("Testing for correct transition from episodes",
                       transitionTable.table.get(i+1)[i].get() == i+2);
        }        
		
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
		
		if(rowToValidate == null || correctRow == null) return false;
		if(rowToValidate.length != correctRow.length) return false;
		
		for(int i = 0; i<rowToValidate.length; ++i ){
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
		return checkRowValues(table.table.get(rowIndex), row);
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
		for(StateID[] row : table.table) {
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
		for(int i=0;i<row.length;i++) {
			row[i] = new StateID(value);
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
		for(StateID[] row : table.table) {
			for(int i=0;row!=null && i<row.length;++i) {
				if(row[i].get() == -1) {
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * createAlphabet()
	 * 
	 * Given a number of characters, create an array of that many characters
	 * 
	 * @param numchars - Number of characters to be placed in the alphabet
	 * @return char[] - The alphabet created
	 */
	private char[] createAlphabet(int numChars) {
		char[] wholeAlphabet = {'a','b','c','d','e','f','g','h','i','j','k','l','m',
								'n','o','p','q','r','s','t','u','v','w','x','y','z'};
		
		char[] returnAlphabet = new char[numChars];
		
		for(int i=0;i<numChars;++i) {
			returnAlphabet[i] = wholeAlphabet[i];
		}
		
		return returnAlphabet;
	}
}
