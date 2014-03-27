package randomPackage;

import java.util.ArrayList;

public class TransitionTable {
	
	//CONSTANTS
	public static final int UNKNOWN_TRANSITION = -1;

	protected ArrayList<StateID[]> table;
	protected char[] alphabet;

	/**
	 * TransitionTable()
	 * 
	 * ctor.
	 * 
	 * @param void - Default Constructor
	 * 
	 */
	public TransitionTable(){
		table = new ArrayList<StateID[]>();
		table.add(null);
		alphabet = null;
	}

	/**
	 * TransitionTable()
	 * 
	 * ctor.
	 * 
	 * @param alphabet - A given set of chars to use as a transition table's alphabet
	 * 
	 * TODO: Still to be tested
	 */
	public TransitionTable(char[] alphabet){
		table = new ArrayList<StateID[]>();
		table.add(null);
		this.alphabet = alphabet;
	}

	/**
	 * addNullRow()
	 * 
	 * A null row is added to the table
	 * 
	 * @param void
	 * @return void
	 */
	public void addNullRow(){
		table.add(null);
	}

	/**
	 * addEmptyRow()
	 * 
	 * Add a row of unknown transitions to the table
	 * 
	 * @param void
	 * @return void
	 */
	public void addEmptyRow(){
		StateID[] emptyRow = createRow(UNKNOWN_TRANSITION,alphabet.length);
		table.add(emptyRow);
	}

	/**
	 * addRow()
	 * 
	 * Given an array of StateID's, add the row to the table
	 * 
	 * @param row - row to add
	 * @return void
	 */
	public void addRow(StateID[] row){
		//ensure the row is not null, there is a separate method for that
		if(row != null) {
			table.add(row);
		}
	}



	//HELPER METHODS//

	/**
	 * createRow()
	 *
	 * create a non empty array of StateID's
	 *
	 * @param value - The Value to be placed in each slot of the array
	 * 				  
	 * 
	 * @param length - The length of the array to be created
	 * 
	 * @return StateID[] - An array of StateID's with specified values 
	 */
	public StateID[] createRow(int value, int length) {
		StateID[] row = new StateID[length];
		for(StateID stateId : row) {
			stateId = new StateID(value);
		}
		return row;
	}
}
