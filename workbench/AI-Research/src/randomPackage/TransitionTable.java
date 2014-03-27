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
	 * constructor.
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
	 * constructor.
	 * 
	 * @param alphabet - A given set of chars to use as a transition table's alphabet
	 * 
	 * 
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
	 * 
	 * @return void
	 */
	public void addRow(StateID[] row){
		//ensure the row is not null, there is a separate method for that
		if(row != null) {
			table.add(row);
		}
	}
	
	/**
	 * updateSingleTransition()
	 * 
	 * Given a source episode and a target episode this method will 
	 * go to the row of the source stateID value and update, at the command
	 * that was taken to get to the target, the value with the stateID of Target
	 * 
	 * @param source 
	 * @param target
	 * 
	 * @return void
	 */
	public boolean updateSingleTransition(Episode source, Episode target){
		int rowIndex = source.stateID.get(); //index of row in transitionTable to update;
		char command = target.command;
		int indexOfCommand = findIndexOfChar(command);
		StateID targetID = target.stateID;
		
		//check if valid row
		if(rowIndex >= table.size()){
			return false;
		}
		
		//modify the transition table at the place
		table.get(rowIndex)[indexOfCommand] = targetID;
		
		return true;
	}

	//---------------------------------HELPER FUNCTIONS-------------------------------------//

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
		for(int i=0;i<row.length;i++) {
			row[i] = new StateID(value);
		}
		return row;
	}
	
	/**
	 * size()
	 * 
	 * gets the numbers of rows in the table.
	 * 
	 * @return int - size of the table
	 */
	public int size(){
		return table.size();
	}
	
	/**
	 * findIndexOfChar()
	 * 
	 * Maps char in alphabet to an index
	 *
	 * @param command - Letter from the alphabet
	 * @return int - Index of the letter given --OR-- '-1' if the letter is 
	 *				 not in the alphabet
	 * 
	 */
	private int findIndexOfChar(char command){
		int index = UNKNOWN_TRANSITION;
		for(int j = 0; j<alphabet.length; ++j){
			if(alphabet[j] == command){
				index = j;
				return j;
			}
		}
		return index;
	}
	
}
