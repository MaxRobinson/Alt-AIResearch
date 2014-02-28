package randomPackage;

import java.util.*;

public class smartAgent extends Agent{
	
	//Variables
	protected int UNKNOWN_TRANSITION = -1;
	protected int UNKNOWN_STATE = -1;
	
	// An ArrayList of arrays that are length 2 to allow us to store which states we think are equal
	protected ArrayList<StateID[]> equivStates = new ArrayList<StateID[]>();
	
	// An ArrayList of arrays that are length 2 to allow us to store which states we think are not equal
	protected ArrayList<StateID[]> diffStates = new ArrayList<StateID[]>();
	
	// Holds the Agents current transition table (what it thinks the transition table is)
	//***************** THIS NEEDS TO CHANGE TO AN ArrayList<StateID[]> transitionTable ***************************
	protected ArrayList<StateID[]> transitionTable;				// we can know the length of alphabet but don't know how many states there are going to be
	
	// Which state the agent thinks it is in currently
	protected int currentState;
	
	// A list of episodes that the agent thinks it can take to get to the goal state.
	//protected ArrayList<Episode> currentPlan = null; //?????????????????????????????????????????????????????
	
	// Which two states the Agent is conjecturing are equal.
	// private int[] currentHypothesis = new int[2];
	
	// Does the current state we are in have a path that we have not tried yet? 
	// protected boolean hasBestPath = false;z
	
	// Keeps the agents current path that it is taking
	protected ArrayList<Episode> currentPath = new ArrayList<Episode>();
	
	
	public smartAgent(){
		super();
		transitionTable = new ArrayList<StateID[]>();
		currentState = 0;
	}
	
	public smartAgent(StateMachineEnvironment environment){
		super(environment);
		transitionTable = new ArrayList<StateID[]>();
		currentState = 0;
	}
	
	/*
	 * findNextOpenState()
	 * finds the next state in the transition table that has a move
	 * that hasn't been made yet. 
	 */
	public int findNextOpenState(){
		//Look through it to find the next open STATE (not next move from that state)
		for(int i = 0; i < transitionTable.size();  ++i){
			for(int j = 0; j< transitionTable.get(i).length; ++j){
				if(transitionTable.get(i)[j].get() == UNKNOWN_TRANSITION){
					//return transitionTable.get(i);
					return i;
				}
			}
		}
		return UNKNOWN_STATE;
	}
	
	/*
	 * addRow()
	 *
	 */
	public void addRow(/*?????*/){
		
	}
	
	
	/*
	 * initTransTable()
	 * initializes the transition table based on the original random path.
	 */
	public void initTransTable(){
		//int rowCount = 0;
		//Makes transition table out of the random path
		for(int i = 1; i< episodicMemory.size(); ++i){
			Episode next = episodicMemory.get(i);
			char command = next.command;
			int index = findIndex(command);  // finds which index the letter corresponds to.
			//now we have index of the command
			//we also have the state ID of the state.
			int state = next.stateID.get();
			StateID[] myTableEntry = new StateID[alphabet.length];
			
			//Used to init the array to UNKNOWN_TRANSITION to start 
			for(int j = 0; j<myTableEntry.length; ++j){
				myTableEntry[j] = new StateID(UNKNOWN_TRANSITION);
			}
			myTableEntry[index].set(state);
			transitionTable.add(myTableEntry);
		}
	}
	
	
	/*
	 * makeMove(int next)
	 * given a state, makes a move, from that state that has not been tried before
	 */
	public void makeMove(int next){
		char nextMove;
		int index;
		do{
			nextMove = randomChar(alphabet.length);
			index = findIndex(nextMove);
		}while(transitionTable.get(next)[index].get() != UNKNOWN_TRANSITION);
		//At this point we now know what move to make. Thee nextMove
		
		//This is where we actually move
		sensor = this.env.tick(nextMove);	//updates sensor
		int encodedSensorValue = encodeSensors(sensor);
		currentPath.add(new Episode(nextMove, encodedSensorValue, ++numStates));  //This copies the move into currentPath "memory"
	}
	
	
	
	/*
	 * findIndex(char command)
	 * maps char in alphabet to a location 
	 */
	private int findIndex(char command){
		int index = UNKNOWN_TRANSITION;
		for(int j = 0; j<alphabet.length; ++j){
			if(alphabet[j] == command){
				index = j;
				return j;
			}
		}
		return index;
	}
	
	
	
	/*
	 * isTransitionTableFull()
	 * this is used to see if there are any unknown transitions in the table
	 * used for conjecturing and filling out the table.
	 * argument to the while loop.
	 */
	public boolean isTransitionTableFull(){
		for(StateID[] x : transitionTable){
			for(int i = 0; i<x.length; ++i){
				if(x[i].get() == UNKNOWN_TRANSITION){
					return false;
				}
			}
		}
		return true;
	}
	
	//
	public ListAndBool analyzeMove(){
		ArrayList<Integer> indexList = new ArrayList<Integer>();
		ArrayList<Episode> conjecturePath = new ArrayList<Episode>();
		Episode currentState = currentPath.get(currentPath.size()-1);
		int currentMatchedPathLength = 0;
		
		indexList = checkIfEpisodeOccured(currentState);
		if(indexList.size() > 0){
			currentMatchedPathLength++;
			currentState = currentPath.get(currentPath.size()-(currentMatchedPathLength));
		}
		else{
			
			// This happens if we have never seen this pattern before!!!!!!!!!!!
			// returning a class/ an object that holds a boolean.....................
			//return false;
			//ConjecturePathReturn conjecturePathReturn = new ConjecturePathReturn(null, false);
			//return conjecturePathReturn;
			ListAndBool ListOfNewEpisodes = new ListAndBool(null,false);
			return ListOfNewEpisodes;
		}
		//we have now updated our state and now we can call our second check episode thing
		
		ArrayList<Integer> indexListTemp = new ArrayList<Integer>();
		
		while(indexList.size() > 0){
			System.out.println(indexList.get(0)); ///THIS IS FOR TESTING!!!!! TAKE ME OUT TO THE BALL PARK>>>>.......!!!!
			if(indexList.size() == 1){
				break;
			}
			
			System.out.println(indexList.get(0));  //THIS IS FOR TESTING TAKE ME OUT!!!! Later of course
			
			indexListTemp = indexList; 
			
			indexList = checkIfEpisodeOccured(decrementArrayList(indexList,currentMatchedPathLength), currentState);
			
			
			
			if(indexList.size() > 0){
				currentMatchedPathLength++;
				currentState = currentPath.get(currentPath.size()-(currentMatchedPathLength+1));
			}
		}
		
		//what Happens if it cannot find just one match to the path? 
		//it will use the most recent one. 
		
		
		
		if(!(indexList.size() > 0)){
			indexList = indexListTemp;
		}
		int index = indexList.size()-1;
		//call build Conjecture path on the last index of indexlist (this will always call the last one)
		conjecturePath = buildConjecturePath(indexList.get(index));
		
		//test the conjectured Path
		ListAndBool newEpisodePathList = testConjecture(conjecturePath);
		boolean theyAreTheSame = newEpisodePathList.getReturnValue();
		
		if(theyAreTheSame){
			//add these to the sameStates Table thingy.....yyyyyyyyyyyy
			StateID[] same = new StateID[2];
			same[0] = currentState.stateID;
			same[1] = episodicMemory.get(indexList.get(index)).stateID;
			equivStates.add(same);
		}
		else{
			//add these to the not sateStates table thingy....yyyyyyyyyyy
			StateID[] diff = new StateID[2];
			diff[0] = currentState.stateID;
			diff[1] = episodicMemory.get(indexList.get(index)).stateID;
			diffStates.add(diff);
		}
		
		
		//Now we know if the two states are the same or different.
		//We now need to update the transition table. 
		modifyTransitionTable();
		
		
		//FOR TESTING
		if(indexList.get(indexList.size() -1) > -1 ){
			boolean foundMatch = true;
			ListAndBool ListOfNewEpisodes = new ListAndBool(newEpisodePathList.getConjecturePath(),foundMatch);
			//ConjecturePathReturn conjecturePathReturn = new ConjecturePathReturn(conjecturePath, true);
			//return conjecturePathReturn;
			return ListOfNewEpisodes;
			//return foundMatch;
		}
		
		//update transition table.... probably
		boolean foundMatch = false;
		ListAndBool ListOfNewEpisodes = new ListAndBool(newEpisodePathList.getConjecturePath(),foundMatch);
		//ConjecturePathReturn conjecturePathReturn = new ConjecturePathReturn(null, false);
		//return conjecturePathReturn;
		return ListOfNewEpisodes;
		//return foundMatch;
	}
	
	
	//New 2/13/14
	//This method will update the transition table for as long as there are same states
	/**
	 * modifyTransitionTable()
	 * This sets the states equal to each other or calls adds it to transition table.
	 */
	public void modifyTransitionTable(){
		
		// Sanity check 
		if(equivStates.size() <= 0 && diffStates.size() <=0){
			return;  // If this happens SOMETHING IS BROKEN!!!!!!!!!!
		}
		if(equivStates.size() > 0){
			// Spot 0 in equivStates holds CurrentState
			// Spot 1 in equivStates holds the state it is equal to
			equivStates.get(0)[1] = equivStates.get(0)[0];
			//This is so this number for the "different state" is not used in the trans table.
			//The index is then "thrown out" since the state doesn't exist but the counter of states
			//is not reset or decremented if two states are equal. (continued count)
			transitionTable.add(null); 
		}
		if(diffStates.size() > 0){
			// call addToTransitionTable()
			addToTransitionTable();
		}
		
		//set equivStates back to length 0.
		equivStates.remove(0);
		
	}
	
	
	/**
	 * addToTransitionTable()
	 */
	public void addToTransitionTable(){
		
		// We do not need to know the stateID of the diff state because we are assuming
		// that up to this point we have the correct number of rows either null or not that 
		// corresponds to numstates and thus by adding to the end of the transition Table
		// it corresponds to the StateID of the diff state. 
		StateID[] newState = new StateID[alphabet.length];
		for(int j = 0; j<newState.length; ++j){
			newState[j] = new StateID(UNKNOWN_TRANSITION);
		}
		transitionTable.add(newState);
		
		//sets diffStates back to length 0.
		diffStates.remove(0);
	}
	
	
	
	/**
	 * 
	 * @param list
	 * @param decrementAmount
	 * @return
	 */
	public ArrayList<Integer> decrementArrayList(ArrayList<Integer> list, int decrementAmount){
		ArrayList<Integer> temp = new ArrayList<Integer>();
		temp = list;
		for(int x : temp){
			x-=decrementAmount;
		}
		return temp;
	}
	
	
	
	//
	public ArrayList<Integer> checkIfEpisodeOccured(ArrayList<Integer> indexList, Episode episode){
		ArrayList<Integer> tempList = new ArrayList<Integer>();
		for(int i = 0; i < indexList.size(); ++i){
			if(episode.equals(episodicMemory.get(indexList.get(i)))){
				tempList.add(indexList.get(i));									// this adds the index in episodic memory held in indexList
			}
		}
		return tempList;
	}
	
	
	/*
	 * this is the initial check to see if this state has ever been seen before
	 * in episodicMemory
	 */
	public ArrayList<Integer> checkIfEpisodeOccured(Episode episode){
		ArrayList<Integer> indexList = new ArrayList<Integer>();
		for(int i = 0; i < episodicMemory.size(); ++i){
			if(episodicMemory.get(i).equals(episode)){
				indexList.add(i);
			}
		}
		return indexList;
	}
	
	/**
	 * 
	 * @param index
	 * @return
	 */
	// Builds Path from conjectured same state to goal.
	public ArrayList<Episode> buildConjecturePath(int index){
		
		ArrayList<Episode> conjecturePath = new ArrayList<Episode>();
		while(episodicMemory.get(index).sensorValue != MYSTERY_AND_GOAL_ON){
			conjecturePath.add(new Episode(episodicMemory.get(index)));
			++index;
		}
		//workaround for now add the final state to the conjecture path
		conjecturePath.add(new Episode(episodicMemory.get(index)));
				
		return conjecturePath;
	}
	
	
	
	/**
	 * testConjecture(ArrayList<Episode> conjecturePath)
	 * 		This tests from the conjectured state that we are CURRENTLY IN (call it i), and makes moves
	 * 		from the State that it is conjectured to be, to the end, starting with the Episode after the 
	 * 		state that it is conjectured to be (call it i+1). 
	 * 		We move from i to i+1 using episode i+1 's Command, since that is the command that was used to get
	 * 		to that State. 
	 * 		If we do not end up with the same sensor values at any point that match up with the command,
	 * 		we know that this Conjectured state is Different from the state it is conjectured to be. 
	 * 		This set of moves that it has done. (ticked) is recorded in a separate temp path, which will be added to 
	 * 		the current path if the sates are different, and if they are the same, Conjecture Path is added to currentPath. (sateID problem) 
	 * 			IF the states are the same the tempPath should = the ConjecturePath (thus add conjecturePath to currentPath).
	 * 			ELSE if the states are different, the temppath will not be the same as conjecturePath, and will be added to CurrentPath. 
	 * @param conjecturePath
	 * @return
	 */
	public ListAndBool testConjecture(ArrayList<Episode> conjecturePath){
		int tempStateNum = numStates;
		ArrayList<Episode> tempPath = new ArrayList<Episode>();
		boolean returnValue = true; //Assumption is that the states are the same by default. (randomly picked a value)
		boolean[] tempSensor = new boolean[2];
		
		for(Episode episode: conjecturePath) {
			tempSensor = this.env.tick(episode.command);
			int encodedSensorValue = this.encodeSensors(tempSensor);
			tempPath.add(new Episode(episode.command, encodedSensorValue, ++tempStateNum));
			if( !(encodedSensorValue == episode.sensorValue)){
				returnValue = false;
				break; 	//if at any point we make a move and something no match break(run away). 
			}
		}
		
		//If they are the same state. set tempPath = to ConjecturePath
		if(returnValue){
			tempPath = conjecturePath;
		}
		else{
			numStates = tempStateNum;
		}
		
		
		// Add all the stuff in tempPath to the currentPath. 
		// (NOTE!!!!! :::: These moves have been made!!! ACTUALLY!!! IN THE WORLD TYPE MADE!!!!!!) 
		// (like don't fuck with this)
		for(Episode episode : tempPath){
			currentPath.add(episode);
		}
		
		ListAndBool NewEpisodePathList = new ListAndBool(tempPath,returnValue);
		
		
		return NewEpisodePathList; 
	}
	
	
	/**
	 * printTransTable()
	 * This method prints out the smartAgents version of the Transition Table
	 */
	public void printTransTable(){
		for(int i = 0;  i < transitionTable.size();  ++i){
			for(int j = 0; j < alphabet.length; j++){
				System.out.print(transitionTable.get(i)[j].get()+ " ");
			}
			System.out.println("");
		}
		
	}
	
	/**
	 * 
	 * @param conjecturePathReturn
	 */
	public void moveToEnd(){
		
		//this will update the one spot in the trans table that needs to have a path added to it.
		int indexOfRow = currentPath.get(currentPath.size()-2).stateID.get();
		int indexOfChar = findIndex(currentPath.get(currentPath.size()-1).command);
		transitionTable.get(indexOfRow)[indexOfChar] = currentPath.get(currentPath.size()-1).stateID;
		
		// At this point we have added the path to the transition table from the findNextOpenState() to the state that we
		// just conjectured was the same as another. 
		
		
	}
	
	/**
	 * 
	 */
	public void addCurrentPathToEpisodic(){
		for(Episode episode : currentPath ){
			episodicMemory.add(episode);
		}
	}
	
	
	/**
	 * addPathToTransTable(ListAndBool ListOfNewEpisodes)
	 * This will add a new path of unknown Episodes to the transitionTable using
	 * the list of moves made from the conjectured state that is different till the
	 * move that was different from the rest.
	 * @param ListOfNewEpisodes
	 */
	public void addPathToTransTable(ListAndBool ListOfNewEpisodes){
		// if the path that we are adding to the trans table is known
		// just update in one spot.
		
		// we are under the assumption that the number of rows in the table
		// still corresponds to the StateID of the states being added. 
		
		//adds the rows needed for the transition Table
		/*
		for(Episode episode : ListOfNewEpisodes.getConjecturePath()){
			StateID[] newState = new StateID[alphabet.length];
			for(int j = 0; j<newState.length; ++j){
				newState[j] = new StateID(UNKNOWN_TRANSITION);
			}
			transitionTable.add(newState);
		}
		*/
			
		
		// as we add these new states to the transition Table, we also have to 
		// add what move they make from one episode to the next one. 
		// Look at MoveToEnd to see how we have done it before
		
		/*
		for(Episode episode : ListOfNewEpisodes.getConjecturePath()){
		
		//this is not right but something like this.
		 
		int indexOfRow = currentPath.get(currentPath.size()-2).stateID.get();
		int indexOfChar = findIndex(currentPath.get(currentPath.size()-1).command);
		transitionTable.get(indexOfRow)[indexOfChar] = currentPath.get(currentPath.size()-1).stateID;
		
		}
		*/
	}
	

	/*
	 * findBestPath()
	 * This method finds the best path for the agent from the start to finish
	 * and fills out the entire state table for the graph to the best of it's ability
	 */
	public void run(){
		// step1
		this.findRandomPath();
		
		// step2
		this.initTransTable();			// open meaning has unfilled transition table entries.

		while(!isTransitionTableFull()){
			
			// step 2b
			int next = findNextOpenState();
			
			// step 3
			this.makeMove(next);						// makes the move from the found open state
			
			// step 4
			//ConjecturePathReturn conjecturePathReturn = this.analyzeMove(/*Step 5*/);				// Checks to see if the state we land in is one we recognize
			//boolean foundMatch = this.analyzeMove();
			ListAndBool ListOfNewEpisodes = this.analyzeMove();
			boolean foundMatch = ListOfNewEpisodes.getReturnValue();
			
			//this is in analyzeMove in calls addToTransitionTable();
				// Hey if not seen this pattern before, I need to be added to the transition table.
				// Uses the last State in the currentPath();
				// this.addToTransitionTable();
			
			
			// step 6
			//if(conjecturePathReturn.getReturnValue()){
			if(foundMatch){
				//Move by conjecture Path
				//this.moveToEnd(conjecturePathReturn);
				this.moveToEnd();  //needs to change name to UpdateTransitionTable
				this.addCurrentPathToEpisodic();
			}
			else{

				while(!foundMatch){
					// Need to update the transition table by saying where the move you made got you. 
					this.addPathToTransTable(ListOfNewEpisodes);
					this.addCurrentPathToEpisodic();
					ListOfNewEpisodes = this.analyzeMove();
					foundMatch = ListOfNewEpisodes.getReturnValue();
	
				}
				this.moveToEnd();
				this.addCurrentPathToEpisodic();

			}
			// need to update the transition table by saying where the move you made got you. 
			//this.addPathToTransTable();
			
		}		
		// step 8 b loop
		// step 9 beq 
		
		// use equiv states to replace transition table values.
		//this.optimizeTransTable();
		
		
	}
}

















