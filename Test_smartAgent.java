package randomPackage;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Test;

public class Test_smartAgent {

	/**
	 * testSmartAgent()
	 * 
	 * Test the smart agent constructors:
	 *     - The default constructor that takes no params
	 *     
	 *     These tests should hold true for the alternative ctor as well, and tests will be written for
	 *     that constructor if there is a new reason to do so.
	 *     
	 * 
	 */
	@Test
	public void testSmartAgent() {
		// default ctor. tests

		smartAgent sA = new smartAgent();

		char[] testAlphabet = sA.alphabet;

		//make sure the object is not null
		assertNotNull("Agent is not null", sA);

		//make sure the current state is properly set to 0
		assertTrue("Current State is Zero", sA.currentState==0);

		//make sure the smart agent's episodic memory is not null
		assertNotNull("Episodic memory is not null", sA.episodicMemory);

		//make sure that there is one episode in memory (Unknown,No sensors, stateID=1)
		assertTrue("Episodic Memory has 1 entry",sA.episodicMemory.size() == 1);

		//make sure that the first episode has the desired properties
		Episode testEpisode = new Episode(' ',0,1);

		boolean equalEpisodes = testEpisode.equals(sA.episodicMemory.get(0)) && testEpisode.stateID.get() == sA.episodicMemory.get(0).stateID.get();

		assertTrue("Episodic Memory contains the initial episode",equalEpisodes);


		//make sure the smartAgent's alphabet is equal to it's tabls's alphabet
		boolean sameAlphabets = true;

		for(int i=0;i<testAlphabet.length;i++) {
			if(testAlphabet[i] != sA.transitionTable.alphabet[i]) {
				sameAlphabets = false;
			}
		}

		assertTrue("Smart Agent and it's table have the same alphabet", sameAlphabets);

	}

	/**
	 * testFindNextOpenState()
	 * 
	 * Test the functionality of the findNextOpenState method
	 * 
	 * This function is meant to find the next state with unknown transitions in a transition table
	 * and return it's corresponding stateID in integer form
	 * 
	 * 
	 */
	@Test
	public void testFindNextOpenState() {
		smartAgent sA = new smartAgent();
		char[] alphabet = createAlphabet(5); // grab helper functions from testTransitionTable
		TransitionTable transitionTable = new TransitionTable(alphabet);
		sA.alphabet=alphabet;
		sA.transitionTable=transitionTable;

		// Create an initial state
		StateID[] starterRow = createRow(2, transitionTable.alphabet.length);
		transitionTable.addRow(starterRow);

		//NO UNKNOWN TRANSITIONS AT THIS POINT
		int nextOpenState = sA.findNextOpenState();
		assertTrue("No Unknown Transitions",nextOpenState==-1);

		// create an array with 5 episodes
		ArrayList<Episode> eps = new ArrayList<Episode>();
		for (int i = 0; i < 5; ++i) {
			eps.add(new Episode(alphabet[i], -1, i + 2));
		}

		// Use the addPath method with the list
		transitionTable.addPath(eps);

		//Must now find the next open state
		nextOpenState = sA.findNextOpenState();
		assertTrue("Next open state is correct after added rows",nextOpenState==2);

	}

	/**
	 * testInitTransTable()
	 * 
	 * Test the functionality of the method that adds a path to an agent's table 
	 * along with the goal row at the end of the table
	 * 
	 * 
	 */
	@Test
	public void testInitTransTable() {
		smartAgent sA = new smartAgent();
		char[] alphabet = createAlphabet(5); // grab helper functions from testTransitionTable
		TransitionTable transitionTable = new TransitionTable(alphabet);
		sA.alphabet=alphabet;
		sA.transitionTable=transitionTable;

		// Create an initial state
		StateID[] starterRow = createRow(2, transitionTable.alphabet.length);
		transitionTable.addRow(starterRow);

		// create an array with 5 episodes
		ArrayList<Episode> eps = new ArrayList<Episode>();
		for (int i = 0; i < 5; ++i) {
			eps.add(new Episode(alphabet[i], -1, i + 2));
		}

		//add the path and the goal state to the trans table
		sA.initTransTable(eps);
		
		//the goal row should be the last in the table which should also be state 6
		StateID[] goalRow1 = sA.transitionTable.get(5);
		StateID[] goalRow2 = sA.transitionTable.get(sA.transitionTable.size() -1);
		
		boolean sameRows = checkRowValues(goalRow1,goalRow2);
		
		assertTrue("Goal Row inserted in correct position in table",sameRows);
		
		StateID[] controlRow = new StateID[sA.alphabet.length];
		for(int i=0;i<controlRow.length;i++) {
			controlRow[i] = new StateID(-5);
		}
		
		//compare the control row to the found goal row
		sameRows = checkRowValues(goalRow1,controlRow);
		
		assertTrue("The goal row contains the appropriate values", sameRows);
		
	}
	
	/**
	 * testMakeExploratoryMove()
	 * 
	 * Test the functionality of the makeExploratoryMove() function.
	 * 
	 * This method is designed to choose a random move from a given state
	 * given the random move is a previously unknown transition
	 */
	@Test
	public void testMakeExploratoryMove() {
		smartAgent sA = new smartAgent();
		char[] alphabet = createAlphabet(5); // grab helper functions from testTransitionTable
		TransitionTable transitionTable = new TransitionTable(alphabet);
		sA.alphabet=alphabet;
		sA.transitionTable=transitionTable;

		// Create an initial state
		StateID[] starterRow = createRow(2, transitionTable.alphabet.length);
		transitionTable.addRow(starterRow);

		// create an array with 5 episodes
		ArrayList<Episode> eps = new ArrayList<Episode>();
		for (int i = 0; i < 5; ++i) {
			eps.add(new Episode(alphabet[i], -1, i + 2));
		}

		//add the path and the goal state to the trans table
		sA.initTransTable(eps);
		
		int nextState = sA.findNextOpenState();
		
		assertTrue("THe currentPath is empty to begin with",sA.currentPath.isEmpty());
		
		//must now make an exploratory move from the new state
		sA.makeExploratoryMove(nextState);
		
		assertNotNull("After a move is made the path is no longer null",sA.currentPath);
		
		//must make sure that the move made was from a previously unknown transistion
		Episode exploratoryEpisode = sA.currentPath.get(0);
		
		char comparisonCommand = 'b'; //this is the only known transition for the state in question
		
		assertTrue("New move is not from a known transition",exploratoryEpisode.command!=comparisonCommand);
		
		boolean inAlphabetRange = false;
		for(int i=0;i<sA.alphabet.length;i++){
			if(sA.alphabet[i] == exploratoryEpisode.command) {
				inAlphabetRange=true;
			}
		}
		
		//Makes sure the chosen char is in the alphabet's range
		assertTrue("The command chosen is in the alphabet's range",inAlphabetRange);
	}
	
	/**
	 * testDecrementArrayList()
	 * 
	 * Tests the functionality of the decrementArrayList method
	 * 
	 * This method is meant to take an arrayList of Integer and decrement 
	 * every element by a given decrement amount 
	 */
	@Test
	public void testDecrementArrayList() {
		smartAgent sA = new smartAgent();
		ArrayList<Integer> arrayList = new ArrayList<Integer>();
		ArrayList<Integer> decArrayList = new ArrayList<Integer>();
		for(int i=0;i<100;i++) {
			arrayList.add(i+1);
			decArrayList.add(i);
		}
		
		//ensure that the two array lists differ by 1
		for(int i=0;i<arrayList.size();i++){
			assertTrue("Values differ by 1",arrayList.get(i) -1 == decArrayList.get(i));
		}
		
		//ensure that the decrementArrayList function follows the same protocol
		ArrayList<Integer> decrementedList = sA.decrementArrayList(arrayList, 1);
		
		for(int i=0;i<decArrayList.size();i++) {
			assertTrue("All values are reasonable",decArrayList.get(i) == decrementedList.get(i));
		}
	}
	
	
	/**
	 * testCheckIfEpisodeOccurred
	 * 
	 * Tests the functionality of the checkIfEpisodeOccurred methods.
	 * 
	 * Both methods are meant to search for an Episode in a list of episodes
	 * and return a list of indexes of all matching episodes.
	 * 
	 */
	@Test 
	public void testCheckIfEpisodeOccurred() {
		//Need a list of episodes to search through
		smartAgent sA = new smartAgent();
		ArrayList<Episode> episodes = new ArrayList<Episode>();
		episodes.add(new Episode(' ', 0, 1)); 
		episodes.add(new Episode('b', 0, 2)); 
		episodes.add(new Episode('c', 0, 3)); 
		episodes.add(new Episode('b', 0, 4)); 
		episodes.add(new Episode('c', 0, 5));
		episodes.add(new Episode('d', 2, 6));
		
		sA.episodicMemory=episodes;
		//Check to see if an episode occurred that is not in the list
		Episode epToCheck = new Episode('e', 1,12);
		ArrayList<Integer> returnList = sA.checkIfEpisodeOccurred(epToCheck);
		
		assertTrue("No index found for non-existent episode",returnList.isEmpty());
		
		//Check to make sure episode that is in list is returned with proper index
		epToCheck = new Episode('d',2,9000);
		returnList = sA.checkIfEpisodeOccurred(epToCheck);
		
		//make sure there is only one thing in the list
		
		assertTrue("Only one thing in the list",returnList.size()==1);
		
		//make sure the index is the proper one
		int realIndex = 5;
		assertTrue("Index returned is correct",returnList.get(0) == realIndex);
		
		//make sure that more than one index can be returned
		epToCheck = new Episode('b',0,123);
		returnList = sA.checkIfEpisodeOccurred(epToCheck);
		
		//ensure size of list is 2
		assertTrue("List has correct number of entries",returnList.size() == 2);
		
		//ensure that both indexes are correct
		boolean correctIndexes = true;
		for(Integer index : returnList) {
			if(index !=1 && index !=3) {
				correctIndexes = false;
			}
		}
		
		//all indexes returned are as they should be
		assertTrue("Indexes returned are correct",correctIndexes);
		
		
		//-------------------NOW CHECK THE OVERLOADED METHOD---------------------------//
		
		ArrayList<Integer> indexList = new ArrayList<Integer>();
		indexList = sA.checkIfEpisodeOccurred(epToCheck);  //List should now have 1 & 3
		
		//given the set index list the check if episode occurred method should simply return the same list
		returnList = sA.checkIfEpisodeOccurred(indexList, epToCheck);
		
		//returnList should equal indexList
		boolean sameLists= true;
		for(int i=0;i<indexList.size() && i< returnList.size();i++) {
			if(indexList.get(i) != returnList.get(i)) {
				sameLists = false;
			}
		}
		
		assertTrue("Indexes are properly returned in overloaded method",sameLists);
		
		//ensure the returnList is empty given a list of indexes that correspond to an episode not being searched for
		epToCheck = new Episode('e',2,21);
		returnList = sA.checkIfEpisodeOccurred(indexList, epToCheck);
		
		assertTrue("Return List is empty",returnList.isEmpty());
		
		ArrayList<Integer> newIndexes = new ArrayList<Integer>(indexList);
		//check to make sure only some of the indexes are returned
		indexList.clear();
		for(int i=0;i<sA.episodicMemory.size();i++) {
			indexList.add(i);
		}
		
		epToCheck = new Episode('b',0,123);
		returnList = sA.checkIfEpisodeOccurred(indexList, epToCheck);
		
		sameLists = true;
		for(int i=0;i<returnList.size() && i<newIndexes.size();i++) {
			if(returnList.get(i) != newIndexes.get(i)) {
				sameLists = false;
			}
		}
		
		assertTrue("List contains appropriate values",sameLists);
		
	}
	
	
	//Still to test
	/*
	 * 
	 * buildConjecturePath
	 * testConjecture
	 * moveToEnd (DEPRECATED...SHOULD NOT BE USED)
	 * modifyTransitionTable
	 */
	
	
	
	
	
	
	
	
	//HELPER METHODS

	/**
	 * createAlphabet()
	 * 
	 * Given a number of characters, create an array of that many characters
	 * 
	 * @param numchars
	 *            - Number of characters to be placed in the alphabet
	 * @return char[] - The alphabet created
	 */
	private char[] createAlphabet(int numChars) {
		char[] wholeAlphabet = { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i',
				'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u',
				'v', 'w', 'x', 'y', 'z' };

		char[] returnAlphabet = new char[numChars];

		for (int i = 0; i < numChars; ++i) {
			returnAlphabet[i] = wholeAlphabet[i];
		}

		return returnAlphabet;
	}

	/**
	 * createRow()
	 * 
	 * create a non empty array of StateID's, all values must not be equal to
	 * '-1'
	 * 
	 * @param value
	 *            - The Value to be placed in each slot of the array This value
	 *            should most likely be 9000 in reference to the jackhawk9000
	 *            which is of course available at walmart
	 * 
	 * @param length
	 *            - The length of the array to be created
	 * 
	 * @return StateID[] - An array of StateID's with specified values
	 */
	public StateID[] createRow(int value, int length) {
		StateID[] row = new StateID[length];
		for (int i = 0; i < row.length; i++) {
			row[i] = new StateID(value);
		}
		return row;
	}
	
	/**
     * checkRowValues()
     * 
     * Checks two rows for equivalence
     * 
     * @param rowToValidate
     *            - The row that needs to be validated
     * @param correctRow
     *            - The control row used for comparison
     * 
     * @return boolean - True if the two rows are equal --OR-- False if the two
     *         rows are different
     * 
     */
    private boolean checkRowValues(StateID[] rowToValidate, StateID[] correctRow) {

        if (rowToValidate == null || correctRow == null)
            return false;
        if (rowToValidate.length != correctRow.length)
            return false;

        for (int i = 0; i < rowToValidate.length; ++i) {
            if (rowToValidate[i].get() != correctRow[i].get()) {
                return false;
            }
        }
        return true;
    }
}
