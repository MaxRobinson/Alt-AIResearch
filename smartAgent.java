package randomPackage;

import java.util.*;

public class smartAgent extends Agent {

	// Constants
	protected int UNKNOWN_TRANSITION = -1;
	protected int GOAL_STATE_TRANSITION = -5;
	protected int UNKNOWN_STATE = -1;

	// Instance Varibales

	// An ArrayList of arrays that are length 2 to allow us to store which
	// states we think are equal
	protected ArrayList<StateID[]> equivStates = new ArrayList<StateID[]>();

	// An ArrayList of arrays that are length 2 to allow us to store which
	// states we think are not equal
	protected ArrayList<StateID[]> diffStates = new ArrayList<StateID[]>();

	// Holds the Agents current transition table (what it thinks the transition
	// table is)
	protected TransitionTable transitionTable;

	// Which state the agent thinks it is in currently
	protected int currentState;

	// Keeps the agents current path that it is taking
	protected ArrayList<Episode> currentPath = new ArrayList<Episode>();
	
	protected StateID goalState;

	/**
	 * Constructor
	 * 
	 * Calls the super-class constructor and initializes the current state and
	 * the transition table
	 */
	public smartAgent() {
		super();
		transitionTable = new TransitionTable(this.alphabet);
		currentState = 0;
	}

	/**
	 * Constructor
	 * 
	 * Overloaded constructor used for testing.
	 * 
	 * @param environment
	 *            - A fixed SME
	 */
	public smartAgent(StateMachineEnvironment environment) {
		super(environment);
		transitionTable = new TransitionTable(this.alphabet);
		currentState = 0;
	}

	/**
	 * findNextOpenState()
	 * 
	 * Finds the next state in the transition table that has an unknown
	 * transition.
	 * 
	 * @param void
	 * @return int - The state that has an unknown transition --OR-- '-1' if
	 *         there are no states with unknown transitions
	 * 
	 */
	public int findNextOpenState() {
		for (int i = 1; i < transitionTable.size(); ++i) {
			for (int j = 0; j < transitionTable.get(i).length; ++j) {
				if (transitionTable.get(i)[j].get() == UNKNOWN_TRANSITION) {
					return i;
				}
			}
		}
		return UNKNOWN_STATE;
	}// findNextOpenState

	/**
	 * initTransTable()
	 * 
	 * Initializes the transition table.
	 * 
	 * @param void
	 * @return void
	 */
	public void initTransTable(ArrayList<Episode> path) {
		transitionTable.addPath(path);

		transitionTable.addEmptyRow();
		
		// The last state in the path is the goal state, so mark it as such
		StateID[] goalStateRow = transitionTable
				.get(transitionTable.size() - 1);

		// Initialize the goal State Row.
		for (int j = 0; j < goalStateRow.length; ++j) {
			goalStateRow[j] = new StateID(GOAL_STATE_TRANSITION);
		}
	}// initTransTable()

	/**
	 * makeExploratoryMove()
	 * 
	 * Selects a move from the current state that the agent has never made
	 * before (unknown transition) and makes that move.
	 * 
	 * CAVEAT: The given row must have at least one unknown transition
	 * 
	 * @param next
	 *            - The State from which to make a move
	 * @return void
	 * 
	 */
	public void makeExploratoryMove(int next) {
		char nextMove;
		int index;
		do {
			nextMove = randomChar(alphabet.length);
			index = findIndex(nextMove);
		} while (transitionTable.get(next)[index].get() != UNKNOWN_TRANSITION);

		// make move
		sensor = this.env.tick(nextMove);
		int encodedSensorValue = encodeSensors(sensor);

		
		//TODO:If the encoded sensor value indicates the new state is the goal
		//update the stateID to the goalStateID which is a global variable
		int nextState = numStates;
		if(encodedSensorValue == MYSTERY_AND_GOAL_ON) {
			//set the nextState to the Goal State ID
			nextState = goalState.get();
		}
		else {
			//not at the goal so there is one more state, update accordingly
			++numStates;
			nextState = numStates;
		}
		
		// add the move to the current path
		currentPath.add(new Episode(nextMove, encodedSensorValue, nextState));
	}// makeExploratoryMove

	/**
	 * findIndex()
	 * 
	 * Maps char in alphabet to an index in the alphabet array
	 * 
	 * @param command
	 *            - Letter from the alphabet
	 * @return int - Index of the letter given --OR-- '-1' if the letter is not
	 *         in the alphabet
	 * 
	 */
	private int findIndex(char command) {
		return transitionTable.findIndexOfChar(command);
	}// findIndex

	/**
	 * isTransitionTableFull()
	 * 
	 * This is used to see if there are any unknown transitions in the table
	 * used for conjecturing and filling out the table. argument to the while
	 * loop.
	 * 
	 * @param void
	 * @return boolean - true if the table is full --OR-- false if the table is
	 *         not
	 */
	public boolean isTransitionTableFull() {
		return !transitionTable.containsUnknownTransitions(goalState);
	}// isTransitionTableFull

	/**
	 * Done
	 * 
	 * @param currentPath
	 * @return
	 */
	public ListAndBool analyzeMove(ArrayList<Episode> currentPath) {

		ArrayList<Integer> indexList = new ArrayList<Integer>();
		ArrayList<Episode> conjecturePath = new ArrayList<Episode>();
		Episode stateToMatch = currentPath.get(currentPath.size() - 1);
		Episode currentState = currentPath.get(currentPath.size() - 1);

		int currentMatchedPathLength = 0;

		// Find all indices in episodic memory of matching episodes
		indexList = checkIfEpisodeOccurred(stateToMatch);

		// check to see if any episodes were matched
		if (indexList.size() == 0) {
			ListAndBool noMatch = new ListAndBool(null, false);
			return noMatch;
		}

		currentMatchedPathLength++; // add 1 to the current matched path length.

		// now we need to move the state that we need to match, back 1.
		if (currentMatchedPathLength < currentPath.size()) {
			stateToMatch = currentPath.get(currentPath.size() - (currentMatchedPathLength + 1)); // add 1 to not
													   											 // get last element
		} else {
			stateToMatch = currentPath.get(currentPath.size() - (currentMatchedPathLength)); // Don't add 1 because
																							 // matchedlength = length
		}

		// could be in with the loop in another method?
		ArrayList<Integer> indexListTemp = new ArrayList<Integer>();
		// * narrowMatches() * ==> to match the next spot.
		// should this be it's own method?
		while (indexList.size() > 1) {
			if(currentPath.size() == currentMatchedPathLength){
				break; //weeze done. matched entire currentPath.
			}
			
			indexListTemp = indexList; // This is a safe guard in case no
										// matches are found.
			indexList = narrowMatches(episodicMemory, indexList, stateToMatch); // then pass in here<<stateToMatch
			
			// Makes sure that indexList length is not 0, it it is,
			// go back to the last list that had something in it and break
			if (indexList.size() <= 0) {
				indexList = indexListTemp;
				break;
			}
			currentMatchedPathLength++; // we can do this because we know the
										// indexList is not 0;
			
			// The State we are matching needs to be changing and it is not...
			// need to do something like stateToMatch =
			// currentPath.get(curentPath.size() - currentMatchedPathLength);
			stateToMatch = currentPath.get(currentPath.size() - currentMatchedPathLength);
			
			
		}// while

		// HERE We have a list of Indices that go as far back as the
		// currentMatchedPathLength from the

		// original state we wanted to match.
		int index = indexList.get(indexList.size() - 1) + currentMatchedPathLength; // gets back to the episode we want

		// build Conjecture Path
		conjecturePath = buildConjecturePath(index);

		// test conjectured Path
		ListAndBool newPath = testConjecture(conjecturePath);
		boolean areSame = newPath.getReturnValue(); // checks if the episodes were found to be the same
													// may not matter
		if (newPath.getConjecturePath() != null) {
			transitionTable.addPath(newPath.getConjecturePath()); // addPath to transTable ** May not want to do here **
		}

		return newPath;

	}// analyzeMove();

	/**
	 * narrowMatches()
	 * 
	 * @param listOfEpisodes
	 * @param indexList
	 * @return
	 */
	public ArrayList<Integer> narrowMatches(ArrayList<Episode> listOfEpisodes,
			ArrayList<Integer> indexList, Episode stateToMatch) {
		if (indexList.size() == 1) {
			return indexList;
		}
		// decrement all of the indices in the index list by 1
		ArrayList<Integer> newIndexList = decrementArrayList(indexList, 1);
		// check if episode has occurred.
		ArrayList<Integer> resultIndexList = checkIfEpisodeOccurred(
				newIndexList, stateToMatch);

		return resultIndexList; // Place Holder
	}// NarrowMatches();

	/**
	 * analyzeMoveOld()
	 * 
	 * TODO: Add Method Header and cleanup/seperate into methods
	 * 
	 * @param void
	 * @return ListAndBool - An object that holds a boolean value and a list of
	 *         episodes
	 * 
	 */
	public ListAndBool analyzeMoveOld() {
		ArrayList<Integer> indexList = new ArrayList<Integer>();
		ArrayList<Episode> conjecturePath = new ArrayList<Episode>();
		Episode stateToBeMatched = currentPath.get(currentPath.size() - 1);
		Episode currentState = currentPath.get(currentPath.size() - 1); // legit
		// current
		// State
		int currentMatchedPathLength = 0;

		// Find all indices in episodic memory of matching episodes
		indexList = checkIfEpisodeOccurred(stateToBeMatched); // Should take
																// list as param
																// as well

		if (indexList.size() > 0) {
			currentMatchedPathLength++;
			// add one to currentMatchedPathLength because we need to go
			// currentMatchPathLength back, from the end of the array,
			// which is size()-1.
			if (currentMatchedPathLength < currentPath.size()) {
				stateToBeMatched = currentPath.get(currentPath.size()
						- (currentMatchedPathLength + 1));
			} else {
				stateToBeMatched = currentPath.get(currentPath.size()
						- (currentMatchedPathLength));
			}
		} else {
			ListAndBool ListOfNewEpisodes = new ListAndBool(null, false);
			return ListOfNewEpisodes;
		}
		// We have now updated our current state, Now we can expand the search
		// to find the most likely matching state

		ArrayList<Integer> indexListTemp = new ArrayList<Integer>();
		boolean breakFlag = false;

		// TO DO: I don't think this is right.
		while (indexList.size() > 0) {
			System.out.println(indexList.get(0)); // /THIS IS FOR TESTING!!!!!
			// TAKE ME OUT TO THE BALL
			// PARK>>>>.......!!!!
			if (indexList.size() == 1) {
				break;
			}
			System.out.println(indexList.get(0)); // THIS IS FOR TESTING TAKE ME
			// OUT!!!! Later of course
			indexListTemp = indexList;

			indexList = checkIfEpisodeOccurred(
					decrementArrayList(indexList, currentMatchedPathLength),
					stateToBeMatched);
			if (indexList.size() > 0) {
				currentMatchedPathLength++;
				if (currentMatchedPathLength < currentPath.size()) {
					stateToBeMatched = currentPath.get(currentPath.size()
							- (currentMatchedPathLength + 1));
				} else {
					breakFlag = true;
				}
			}
			if (breakFlag) {
				break; // breaking because we have matched our entire current
				// path
			}
		}
		// Failsafe, if there were no 'longer' matches in memory
		if (!(indexList.size() > 0)) {
			indexList = indexListTemp;
		}

		// get the last index from the list
		int index = indexList.size() - 1; // //// THIS IS SO INCREDIBLY
											// WRONG!!!!!!!!!!

		// build a path from the state found in memory to the goal
		conjecturePath = buildConjecturePath(indexList.get(index));

		// test the conjectured Path
		ListAndBool newEpisodePathList = testConjecture(conjecturePath);
		boolean theyAreTheSame = newEpisodePathList.getReturnValue();

		if (theyAreTheSame) {
			// add to the equivStates table
			StateID[] same = new StateID[2];
			same[0] = currentState.stateID; // legit current state
			same[1] = episodicMemory.get(indexList.get(index)).stateID;
			equivStates.add(same);
		} else {
			// add to the diffStates table
			StateID[] diff = new StateID[2];
			diff[0] = currentState.stateID;
			diff[1] = episodicMemory.get(indexList.get(index)).stateID;
			diffStates.add(diff);
		}

		// given same or different states, update the transistion table
		modifyTransitionTable();

		// Not necessarily for testing anymore don't need if any more?? probs
		// not.
		if (indexList.get(indexList.size() - 1) > -1) {
			boolean foundMatch = true;
			ListAndBool ListOfNewEpisodes = new ListAndBool(
					newEpisodePathList.getConjecturePath(), foundMatch);
			return ListOfNewEpisodes;
		} else {
			boolean foundMatch = false;
			ListAndBool ListOfNewEpisodes = new ListAndBool(
					newEpisodePathList.getConjecturePath(), foundMatch);
			return ListOfNewEpisodes;
		}
	}

	/**
	 * modifyTransitionTable()
	 * 
	 * Merges two equiv states --OR-- adds a new diff state to the table
	 * 
	 * @param void
	 * @return void
	 */
	public void modifyTransitionTable() {
		if (equivStates.size() > 0) {
			// equivStates holds CurrentState([0]) and state it is equal to([1])
			equivStates.get(0)[1] = equivStates.get(0)[0];

			// add a null row to the table to account for a state that no longer
			// exists

			transitionTable.addNullRow();

			// set equivStates back to length 0.
			equivStates.clear();
		}
		if (diffStates.size() > 0) {
			transitionTable.addEmptyRow();
			diffStates.clear();
		}
	}// modifyTransitionTable

	/**
	 * decrementArrayList()
	 * 
	 * Given an arrayList of integers, decrements all values by a given amount
	 * 
	 * @param list
	 *            - arrayList to decrement
	 * @param decrementAmount
	 *            - amount to decrement the values in the list
	 * 
	 * @return ArrayList<Integer> - The decremented arrayList
	 * 
	 */
	public ArrayList<Integer> decrementArrayList(ArrayList<Integer> list,
			int decrementAmount) {
		ArrayList<Integer> temp = new ArrayList<Integer>(list);
		for (int i = 0; i < temp.size(); i++) {
			Integer x = temp.get(i) - decrementAmount;
			temp.set(i, x);
		}
		return temp;
	}// decrementArrayList

	/**
	 * checkIfEpisodeOccurred()
	 * 
	 * Given a list of indexes and an episode to check for, runs through
	 * episodic memory searching for the episode at the indexes given
	 * 
	 * @param indexList
	 *            - List of indexes to search
	 * @param episode
	 *            - Episode to search for
	 * @return ArrayList<Integer> - List of all indeces where the episode was
	 *         found
	 * 
	 */
	public ArrayList<Integer> checkIfEpisodeOccurred(
			ArrayList<Integer> indexList, Episode episode) {
		ArrayList<Integer> tempList = new ArrayList<Integer>();
		for (int i = 0; i < indexList.size(); ++i) {
			if (episode.equals(episodicMemory.get(indexList.get(i)))) {
				tempList.add(indexList.get(i));
			}
		}
		return tempList;
	}// checkIfEpisodeOccurred

	/**
	 * checkIfEpisodeOccurred()
	 * 
	 * Overloaded: Given an episode, searches episodicMemory for matching
	 * episodes
	 * 
	 * @param episode
	 *            - Episode to search for in memory
	 * @return ArrayList<Integer> - List of indeces in episodic memory where a
	 *         matching episode was found
	 */
	public ArrayList<Integer> checkIfEpisodeOccurred(Episode episode) {
		ArrayList<Integer> indexList = new ArrayList<Integer>();
		for (int i = 0; i < episodicMemory.size(); ++i) {
			if (episodicMemory.get(i).equals(episode)) {
				indexList.add(i);
			}
		}
		return indexList;
	}// checkIfEpisodeOccurred

	/**
	 * buildConjecturePath()
	 * 
	 * Builds a path from the episode at the given index in memory to the goal
	 * 
	 * @param index
	 *            - Index in memory from which to start the path
	 * @return ArrayList<Episode> - the path that was found
	 * 
	 */
	public ArrayList<Episode> buildConjecturePath(int index) {
		ArrayList<Episode> conjecturePath = new ArrayList<Episode>();
		int tempIndex = 0;
		while (episodicMemory.size() > index + tempIndex
				&& episodicMemory.get(index).sensorValue != MYSTERY_AND_GOAL_ON) {
			conjecturePath.add(new Episode(episodicMemory
					.get(index + tempIndex)));
			tempIndex++;
		}
		// subtract 1 as loop will fail after the index has been updated one too
		// many times
		int finalIndex = index + tempIndex - 1;
		// add final state that takes the agent to the goal
		conjecturePath.add(new Episode(episodicMemory.get(finalIndex)));
		return conjecturePath;
	}// buildConjecturePath

	/**
	 * TODO: FIX METHOD HEADER testConjecture(ArrayList<Episode> conjecturePath)
	 * This tests from the conjectured state that we are CURRENTLY IN (call it
	 * i), and makes moves from the State that it is conjectured to be, to the
	 * end, starting with the Episode after the state that it is conjectured to
	 * be (call it i+1). We move from i to i+1 using episode i+1 's Command,
	 * since that is the command that was used to get to that State. If we do
	 * not end up with the same sensor values at any point that match up with
	 * the command, we know that this Conjectured state is Different from the
	 * state it is conjectured to be. This set of moves that it has done.
	 * (ticked) is recorded in a separate temp path, which will be added to the
	 * current path if the sates are different, and if they are the same,
	 * Conjecture Path is added to currentPath. (sateID problem) IF the states
	 * are the same the tempPath should = the ConjecturePath (thus add
	 * conjecturePath to currentPath). ELSE if the states are different, the
	 * temppath will not be the same as conjecturePath, and will be added to
	 * CurrentPath.
	 * 
	 * @param conjecturePath
	 * @return
	 */
	public ListAndBool testConjecture(ArrayList<Episode> conjecturePath) {
		int tempStateNum = numStates;
		ArrayList<Episode> tempPath = new ArrayList<Episode>();
		boolean returnValue = true;
		boolean[] tempSensor = new boolean[2];

		for (Episode episode : conjecturePath) {
			tempSensor = this.env.tick(episode.command); // A move is made!!!!
			int encodedSensorValue = this.encodeSensors(tempSensor);
			tempPath.add(new Episode(episode.command, encodedSensorValue,
					++tempStateNum));
			if (!(encodedSensorValue == episode.sensorValue)) {
				returnValue = false;
				break;
			}
		}

		// If they are the same state, set tempPath equal to ConjecturePath
		if (returnValue) {
			tempPath = conjecturePath;
		} else {
			numStates = tempStateNum;
		}

		// Add all the stuff in tempPath to the currentPath.
		for (Episode episode : tempPath) {
			// TODO: MAY NOT HAVE THE LAST EPISODE IN THE PATH
			currentPath.add(episode);
		}

		ListAndBool NewEpisodePathList = new ListAndBool(tempPath, returnValue);

		return NewEpisodePathList;
	}

	/**
	 * printTransTable()
	 * 
	 * This method prints out the smartAgent's version of the Transition Table
	 * 
	 * @param void
	 * @return void
	 * 
	 */
	public void printTransTable() {
		transitionTable.print();
	}// printTransitionTable

	// Has Beem Replaced by UpdateSingleTransition
	// replace this call with a call to UpdateSingleTransition with
	// source = currentPath.size() - 2 --> second to last episode in current
	// Path
	// target = currentPath.size() - 1 --> last Episode in current Path
	/**
	 * 
	 * @param conjecturePathReturn
	 */
	public void moveToEnd() {

		// this will update the one spot in the trans table that needs to have a
		// path added to it.
		// btw we subtract 2 because we need to get the value at 1 less than the
		// size of the array list, and then we need the
		// item which is 1 previous to the end.
		int indexOfRow = currentPath.get(currentPath.size() - 2).stateID.get();
		int indexOfChar = findIndex(currentPath.get(currentPath.size() - 1).command);

		// DEBUGGING
		boolean test1 = indexOfRow < transitionTable.size();

		if (test1) {
			test1 = transitionTable.get(indexOfRow) != null;
		} else {
			return;
		}
		// END DEBUGGING ADDITION
		
		// This actually not doing wat it is supposed to be doing. 
		// it is updating the goal state with the new state ID not the state we conjectured 
		// is the same as another. 
		if (test1) {
			transitionTable.get(indexOfRow)[indexOfChar] = currentPath
					.get(currentPath.size() - 1).stateID;
		} else {
			// This makes a new row with values set to default unknown.
			StateID[] myTableEntry = new StateID[alphabet.length];
			for (int j = 0; j < myTableEntry.length; ++j) {
				myTableEntry[j] = new StateID(UNKNOWN_TRANSITION);
			}
			// Sets the one spot in the row to the transition that needs to be
			// updated.
			myTableEntry[indexOfChar] = currentPath.get(currentPath.size() - 1).stateID;

			// THIS WAS HERE ORIGINALLY
			// sets the row in the transition table to a newly intiated row
			// transitionTable.set(indexOfRow, myTableEntry);

			// TODO: Check this change
			transitionTable.setRow(myTableEntry, indexOfRow);
		}
		// At this point we have added the path to the transition table from the
		// findNextOpenState() to the state that we
		// just conjectured was the same as another.

	}

	/**
	 * addCurrentPathToEpisodic()
	 * 
	 * Adds the current path to the episodic memory
	 * 
	 * @param void
	 * 
	 */
	public void addCurrentPathToEpisodic() {
		for (Episode episode : currentPath) {
			episodicMemory.add(episode);
		}
	}// addCurrentPathToEpisodic

	/**
	 * addPathToTransTable()
	 * 
	 * Adds a path (list of episodes) to the transition table, updating multiple
	 * transitions
	 * 
	 * @param ListOfNewEpisodes
	 */
	public void addPathToTransTable(ListAndBool listOfNewEpisodes) {
		ArrayList<Episode> newEpisodes = listOfNewEpisodes.getConjecturePath();
		transitionTable.addPath(newEpisodes);
	}// addPathToTransitionTable

	
	/**
	 * findRandomPath()
	 * 
	 * 
	 */
	public void findRandomPath() {
		super.findRandomPath();
		//numStates should now be equal to the goal state ID, init goalState
        goalState = new StateID(numStates);
	}
	
	/**
	 * run()
	 * 
	 * The main entrypoint of the smartAgent. Makes all appropriate method calls
	 * and runs until the transition table is full
	 * 
	 * @param void
	 * 
	 */
	public void run() {
		// Find a path to the goal
		this.findRandomPath();

		// Given the inital path to the goal, initialize the transitionTable
		this.initTransTable(this.episodicMemory);

		while (!isTransitionTableFull()) {

			// Find the next state with unknown transitions
			int next = findNextOpenState();

			if (next == -1) {
				// no unknown transitions
				System.out.println("No more unknown transitions");
				return;
			}
			
			// ********* call buildPath, with the target and source states *************
			// ********* then add the list of states to the current path. ************
			
			
			// Make a move from the chosen state
			this.makeExploratoryMove(next);

			// Analyze the move made
			ListAndBool ListOfNewEpisodes = this.analyzeMove(currentPath);

			// Find out if there was a matched state in history
			boolean foundMatch = ListOfNewEpisodes.getReturnValue();

			if (foundMatch) {
				// add the new transition to the table
				this.moveToEnd();

				// add the current path to the episodic memory
				this.addCurrentPathToEpisodic();

				// clear the current path to get ready for the next moves
				this.currentPath.clear();
			} else {
				// No match found
				while (!foundMatch) {
					if (ListOfNewEpisodes.getConjecturePath() != null) {
						// add the path found to the transition table
						this.addPathToTransTable(ListOfNewEpisodes);
					}// if

					// add the current path to the episodic memory
					//this.addCurrentPathToEpisodic();
					
					this.transitionTable.addEmptyRow();
					// need to update the transition Table so that we know that there is 
						// a new state that has been added to episodic memory with unknown trans
					// TODO:WHAT MOVE ARE WE ANALYZING NOW 
						//Probs should add call to make exploratory move
						// we can call MakeExploritoryMove with the StateID of the most recent thing
							// in episodic memory, The thing we just added to the trans table, that way
								// try to get to the end. 
					// ******* we cannot call findNextOpenState because it will give us the same state 
					// ****** that got us here, as well as we have to now move and look from the place 
					// ****** we are already in the environment. ******
					
					int nextMove = currentPath.get(currentPath.size()-1).stateID.get();
					this.makeExploratoryMove(nextMove);
					
					//maybe clear current Path? maybe not? unsure, lets see how runs.
					ListOfNewEpisodes = this.analyzeMove(currentPath);

					foundMatch = ListOfNewEpisodes.getReturnValue();

				}// while

				this.moveToEnd();
				this.addCurrentPathToEpisodic();

				// clear the current path to get ready for next move
				this.currentPath.clear();

			}// else
		}// while
		transitionTable.print();
	}
}
