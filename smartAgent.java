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
     * @return int - The state that has an unknown transition
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
    }//findNextOpenState

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

        // The last state in the path is the goal state, so mark it as such
        StateID[] goalStateRow = transitionTable.get(transitionTable.size() - 1);
        
        // Initialize the goal State Row.
        for (int j = 0; j < goalStateRow.length; ++j) {
            goalStateRow[j] = new StateID(GOAL_STATE_TRANSITION);
        }
    }

    /**
     * makeExploratoryMove()
     * 
     * Selects a move from the current state that the agent has never made
     * before (unknown transition) and makes that move.
     *
     * CAVEAT:  The given row must have at least one unknown transition
     * 
     * @param next
     *            - The State from which to make a move
     * @return void
     * 
     */
    public void makeExploratoryMove(int next) {
        char nextMove;
        int index;
        // find move to make
        do {
            nextMove = randomChar(alphabet.length);
            index = findIndex(nextMove);
        } while (transitionTable.get(next)[index].get() != UNKNOWN_TRANSITION);

        // make move
        sensor = this.env.tick(nextMove);
        int encodedSensorValue = encodeSensors(sensor);

        // add the move to the current path
        currentPath.add(new Episode(nextMove, encodedSensorValue, ++numStates));
    }//makeExploratoryMove

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
        int index = UNKNOWN_TRANSITION;
        for (int j = 0; j < alphabet.length; ++j) {
            if (alphabet[j] == command) {
                index = j;
                return j;
            }
        }
        return index;
    }

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
        for (int i = 0; i < transitionTable.size(); ++i) {
            StateID[] row = transitionTable.get(i);
            if (row != null) {
                for (int j = 0; j < row.length; ++j) {
                    if (row[j].get() == UNKNOWN_TRANSITION) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /**
     * analyzeMove()
     * 
     * TODO: Add Method Header and cleanup/seperate into methods
     * 
     * @param void
     * @return ListAndBool - An object that holds a boolean value and a list of
     *         episodes
     * 
     */
    public ListAndBool analyzeMove() {
        ArrayList<Integer> indexList = new ArrayList<Integer>();
        ArrayList<Episode> conjecturePath = new ArrayList<Episode>();
        Episode stateToBeMatched = currentPath.get(currentPath.size() - 1);
        Episode currentState = currentPath.get(currentPath.size() - 1); // legit current State
        int currentMatchedPathLength = 0;

        // Find all indeces in episodic memory of matching episodes
        indexList = checkIfEpisodeOccured(stateToBeMatched);

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

            indexList = checkIfEpisodeOccured(
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
        int index = indexList.size() - 1;

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

        // Sanity check
        if (equivStates.size() <= 0 && diffStates.size() <= 0) {
            return; // Should not reach this statement.
        }

        if (equivStates.size() > 0) {
            // equivStates holds CurrentState([0]) and state it is equal to([1])
            equivStates.get(0)[1] = equivStates.get(0)[0];

            // add a null row to the table to account for a state that no longer
            // exists
            
            
            //THIS WAS HERE ORIGINALLY
            //transitionTable.add(null);
            
            //TODO:Check this change
            transitionTable.addNullRow();
            
            
            // set equivStates back to length 0.
            equivStates.remove(0);
        }

        if (diffStates.size() > 0) {
            addToTransitionTable();
            diffStates.remove(0);
        }

    }

    /**
     * addToTransitionTable()
     * 
     * Adds a row of unknown transitions to the transition table to hold the
     * place of a newly discovered diff state
     * 
     * @param void
     * @return void
     * 
     */
    public void addToTransitionTable() {
        StateID[] newState = new StateID[alphabet.length];
        for (int j = 0; j < newState.length; ++j) {
            newState[j] = new StateID(UNKNOWN_TRANSITION);
        }
        
        //THIS WAS HERE ORIGINALLY
        //transitionTable.add(newState);
        
        //TODO:Check this change
        transitionTable.addRow(newState);
    }

    /**
     * decrementArrayList()
     * 
     * Given an arrayList of integers, decrements all values by a given ammount
     * 
     * @param list
     *            - arrayList to decrement
     * @param decrementAmount
     *            - ammount to decrement the values in the list
     * @return ArrayList<Integer> - The decremented arrayList
     * 
     * 
     */
    public ArrayList<Integer> decrementArrayList(ArrayList<Integer> list,
            int decrementAmount) {
        ArrayList<Integer> temp = new ArrayList<Integer>();
        temp = list;
        for (int x : temp) {
            x -= decrementAmount;
        }
        return temp;
    }

    /**
     * checkIfEpisodeOccured()
     * 
     * Given a list of episodes and an episode to check for, runs through the
     * list searching for the episode
     * 
     * @param indexList
     *            - List of episodes to search
     * @param episode
     *            - Episode to search for
     * @return ArrayList<Integer> - List of all indeces where the episode was
     *         found
     * 
     */
    public ArrayList<Integer> checkIfEpisodeOccured(
            ArrayList<Integer> indexList, Episode episode) {
        ArrayList<Integer> tempList = new ArrayList<Integer>();
        for (int i = 0; i < indexList.size(); ++i) {
            if (episode.equals(episodicMemory.get(indexList.get(i)))) {
                tempList.add(indexList.get(i));
            }
        }
        return tempList;
    }

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
    public ArrayList<Integer> checkIfEpisodeOccured(Episode episode) {
        ArrayList<Integer> indexList = new ArrayList<Integer>();
        for (int i = 0; i < episodicMemory.size(); ++i) {
            if (episodicMemory.get(i).equals(episode)) {
                indexList.add(i);
            }
        }
        return indexList;
    }

    /**
     * buildConjecture Path()
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

        // We subtract one because of the way the loop will fail when index is
        // too large
        int finalIndex = index + tempIndex - 1;

        // add final state that takes the agent to the goal
        conjecturePath.add(new Episode(episodicMemory.get(finalIndex - 1)));

        return conjecturePath;
    }

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
                break; // if at any point a move is made and something doesn't
                       // match, break.
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
        for (int i = 0; i < transitionTable.size(); ++i) {
            for (int j = 0; j < alphabet.length; j++) {
                if (transitionTable.get(i) != null) {
                    System.out.print(transitionTable.get(i)[j].get() + " ");
                }
            }
            System.out.println("");
        }
    }

    
    
    // Has Beem Replaced by UpdateSingleTransition
    // replace this call with a call to UpdateSingleTransition with
    // source = currentPath.size() - 2 --> second to last episode in current Path 
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

            
            //THIS WAS HERE ORIGINALLY
            // sets the row in the transition table to a newly intiated row
            //transitionTable.set(indexOfRow, myTableEntry);
            
            //TODO: Check this change
            transitionTable.setRow(myTableEntry, indexOfRow);
        }
        // At this point we have added the path to the transition table from the
        // findNextOpenState() to the state that we
        // just conjectured was the same as another.

    }

    /**
     * 
     */
    public void addCurrentPathToEpisodic() {
        for (Episode episode : currentPath) {
            episodicMemory.add(episode);
        }
    }

    /**
     * addPathToTransTable(ListAndBool ListOfNewEpisodes) This will add a new
     * path of unknown Episodes to the transitionTable using the list of moves
     * made from the conjectured state that is different till the move that was
     * different from the rest.
     * 
     * @param ListOfNewEpisodes
     */
    public void addPathToTransTable(ListAndBool listOfNewEpisodes) {
        // if the path that we are adding to the trans table is known
        // just update in one spot.

        // we are under the assumption that the number of rows in the table
        // still corresponds to the StateID of the states being added.

        // adds the rows needed for the transition Table

        if (listOfNewEpisodes == null) {
            System.out.println("listOfNewEpisodes is NULL, line 512.");
        }

        if (listOfNewEpisodes.getConjecturePath() == null) {
            System.out.println("listOfNewEpisodes is NULL, line 516.");
        }

        for (int i = 0; i < listOfNewEpisodes.getConjecturePath().size(); ++i) {
            StateID[] newState = new StateID[alphabet.length];
            for (int j = 0; j < newState.length; ++j) {
                newState[j] = new StateID(UNKNOWN_TRANSITION);
            }
            
            //THIS WAS HERE ORIGINALLY
            //transitionTable.add(newState);
            
            transitionTable.addRow(newState);
        }

        ArrayList<Episode> newEpisodes = listOfNewEpisodes.getConjecturePath();

        // as we add these new states to the transition Table, we also have to
        // add what move they make from one episode to the next one.
        // Look at MoveToEnd to see how we have done it before
        int indexOfRow = this.currentState; // Should double check to make sure
                                            // that this is actually the state
                                            // that is conjectured to be the
                                            // same or diff.
        int indexOfChar = findIndex(newEpisodes.get(0).command); // this will
                                                                 // get the
                                                                 // first move
                                                                 // from
                                                                 // conjectured
                                                                 // state to the
                                                                 // first
                                                                 // episode in
                                                                 // listOfNewEpisodes
        transitionTable.get(indexOfRow)[indexOfChar] = newEpisodes.get(0).stateID;

        for (int i = 1; i < newEpisodes.size(); ++i) {

            indexOfRow = newEpisodes.get(i).stateID.get();
            indexOfChar = findIndex(newEpisodes.get(i).command);
            transitionTable.get(indexOfRow)[indexOfChar] = newEpisodes.get(i).stateID;

        }

    }

    /*
     * findBestPath() This method finds the best path for the agent from the
     * start to finish and fills out the entire state table for the graph to the
     * best of it's ability
     */
    public void run() {
        // step1
        this.findRandomPath();

        // step2
        //TODO: Check this change
        this.initTransTable(this.episodicMemory); // open meaning has unfilled transition table
                               // entries.

        while (!isTransitionTableFull()) {

            // step 2b
            int next = findNextOpenState();

            // step 3
            this.makeExploratoryMove(next); // makes the move from the found open state

            // step 4
            // ConjecturePathReturn conjecturePathReturn =
            // this.analyzeMove(/*Step 5*/); // Checks to see if the state we
            // land in is one we recognize
            // boolean foundMatch = this.analyzeMove();
            ListAndBool ListOfNewEpisodes = this.analyzeMove();
            boolean foundMatch = ListOfNewEpisodes.getReturnValue(); // found
                                                                     // two
                                                                     // states
                                                                     // that are
                                                                     // equal

            // this is in analyzeMove in calls addToTransitionTable();
            // Hey if not seen this pattern before, I need to be added to the
            // transition table.
            // Uses the last State in the currentPath();
            // this.addToTransitionTable();

            // step 6
            // if(conjecturePathReturn.getReturnValue()){
            if (foundMatch) {
                // Move by conjecture Path
                // this.moveToEnd(conjecturePathReturn);
                this.moveToEnd(); // needs to change name to
                                  // UpdateTransitionTable
                this.addCurrentPathToEpisodic();
            } else {

                while (!foundMatch) {
                    if (ListOfNewEpisodes.getConjecturePath() != null) {
                        this.addPathToTransTable(ListOfNewEpisodes);
                    }
                    // Need to update the transition table by saying where the
                    // move you made got you.
                    this.addCurrentPathToEpisodic();
                    ListOfNewEpisodes = this.analyzeMove();
                    foundMatch = ListOfNewEpisodes.getReturnValue();

                }
                this.moveToEnd();
                this.addCurrentPathToEpisodic();

                currentPath.clear();

            }
            // need to update the transition table by saying where the move you
            // made got you.
            // this.addPathToTransTable();

        }
        // step 8 b loop
        // step 9 beq

        // use equiv states to replace transition table values.
        // this.optimizeTransTable();

    }
}