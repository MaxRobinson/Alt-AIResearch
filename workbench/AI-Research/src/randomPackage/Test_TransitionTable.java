package randomPackage;

import static org.junit.Assert.*;

import org.junit.Test;

public class Test_TransitionTable {

	@Test
	public void testTransitionTable() {
		TransitionTable transitionTable = new TransitionTable();
		assertNotNull("Make sure the Table is not Null", transitionTable);
		assertTrue("Make sure that there is one null row at index 0.", transitionTable.table.get(0) == null && transitionTable.table.size() == 1);
	}
	
	@Test
	public void testAddNullRow(){
		TransitionTable transitionTable = new TransitionTable();
		transitionTable.addNullRow();
		assertTrue("Make sure that there is a null row at index 1.", transitionTable.table.size() == 2);
	}
	
	@Test
	public void testAddEmptyRow(){
		TransitionTable transitionTable = new TransitionTable();
		transitionTable.addEmptyRow();
		assertNotNull("Make sure that row 1 is not null.", transitionTable.table.get(1));
		assertTrue("Make sure that all values in the row are -1", checkRowValues(transitionTable.table.get(1)));
		assertTrue("Make sure that the row length = alphabet length", isCorrectLength(transitionTable.table.get(1),transitionTable.alphabet.length));
	}
	
	
	//Helper Method
	private boolean checkRowValues(StateID[] row){
		for(int i = 0; i< row.length; ++i ){
			if(row[i].get() != -1){
				return false;
			}
		}
		return true;
	}
	
	private boolean isCorrectLength(StateID[] row, int length){
		return row.length == length;
	}
	
}
