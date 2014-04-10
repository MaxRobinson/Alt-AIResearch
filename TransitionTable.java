package randomPackage;

import java.util.ArrayList;

/**
 * class TransitionTable
 * 
 * contains a transition table representing a FSM as created by an episodic
 * memory agent
 */

public class TransitionTable {

    // CONSTANTS
    public static final int UNKNOWN_TRANSITION = -1;

    /*
     * The table itself is an ArrayList of array since each row in the table
     * will always ways be the same length as the alphabet.
     */
    protected ArrayList<StateID[]> table;

    /* the alphabet of allowed commands used by the FSM */
    protected char[] alphabet;

    /**
     * TransitionTable()
     * 
     * constructor.
     * 
     * @param void - Default Constructor
     * 
     */
    public TransitionTable() {
        table = new ArrayList<StateID[]>();
        table.add(null);
        alphabet = null;
    }

    /**
     * TransitionTable()
     * 
     * constructor.
     * 
     * @param alphabet
     *            - A given set of chars to use as a transition table's alphabet
     * 
     * 
     */
    public TransitionTable(char[] alphabet) {
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
    public void addNullRow() {
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
    public void addEmptyRow() {
        StateID[] emptyRow = createRow(UNKNOWN_TRANSITION, alphabet.length);
        table.add(emptyRow);
    }

    /**
     * addRow()
     * 
     * Given an array of StateID's, add the row to the table
     * 
     * @param row
     *            - row to add
     * 
     * @return void
     */
    public void addRow(StateID[] row) {
        // ensure the row is not null, there is a separate method for that
        if (row != null) {
            table.add(row);
        }
    }

    /**
     * setRow()
     * 
     * Given an array of StateID's, and an index, add the row to the table at the index
     * 
     * @param row - row to add
     * @param idx - where to insert the row
     * 
     * IMPORTANT: ROW MUST EXIST PRIOR TO THIS METHOD BEING CALLED!!
     * 
     * TODO: Must create test for this method
     * 
     * @return void
     */
    public void setRow(StateID[] row, int idx) {
        table.set(idx, row);
    }
    
    /**
     * updateSingleTransition()
     * 
     * Given a source episode and a target episode this method will go to the
     * row of the source stateID value and update, at the command that was taken
     * to get to the target, the value with the stateID of Target
     * 
     * @param source
     * @param target
     * 
     * @return void
     */
    public void updateSingleTransition(Episode source, Episode target) {
        int rowIndex = source.stateID.get(); // index of row in transitionTable
                                             // to update;
        System.out.println("" + rowIndex);
        char command = target.command;
        int indexOfCommand = findIndexOfChar(command);
        StateID targetID = target.stateID;

        // check if valid row
        if (rowIndex >= table.size()) {
            System.err
                    .println("ERROR:  corrupted episode passed to updateSingleTransition");
            System.exit(-1);
        }

        // modify the transition table at the place
        table.get(rowIndex)[indexOfCommand] = targetID;
    }

    /**
     * addPath
     * 
     * given a list of Episode objects, this method adds associated
     * transitions.  If an episode contains a state Id for which there is no row
     * in the table, rows are added to accommodate it.
     * 
     * @param eps
     *            the list of episodes
     */
    public void addPath(ArrayList<Episode> eps) {
        Episode prev = eps.get(0);
        for (int i = 1; i < eps.size(); ++i) {  //start at '1' because we're
                                                //processing pairs of Episodes
            Episode curr = eps.get(i);

            // Make sure there is an entry in the table for this episode
            while(this.size() <= prev.stateID.get())
            {
                this.addEmptyRow();
            }

            updateSingleTransition(prev, curr);
            prev = curr;
        }//for
    }//addPath

    /**
     * print()
     * 
     * This method prints an ASCII representation of the table to stdout
     * 
     */
    public void print() {
        System.out.print("     ");
        for (int i = 0; i < alphabet.length; ++i) {
            System.out.printf("%3c", alphabet[i]);
        }
        System.out.println();

        for (int i = 0; i < table.size(); i++) {
            System.out.printf("%3d: ", i);

            if (table.get(i) != null)
            {
                for (int j = 0; j < alphabet.length; j++) {
                    System.out.printf("%3d", table.get(i)[j].get());
                }
                System.out.println();
            }
            else
            {
                System.out.println("  null");
            }
        }//for

        System.out.print("     ");
        for (int i = 0; i < alphabet.length; ++i) {
            System.out.printf("%3c", alphabet[i]);
        }
        System.out.println();

    }//print
    
    /**
     * containsUnknownTransitions()
     * 
     * Given a TransitionTable, checks to see if the table contains any values
     * equal to '-1'
     * 
     * @param table
     *            - The transition table in question
     * 
     * @return boolean - True if the table contains '-1' --OR-- False if the
     *         table has only 'other' entries
     */
    public boolean containsUnknownTransitions() {
        for (StateID[] row : table) {
            for (int i = 0; row != null && i < row.length; ++i) {
                if (row[i].get() == -1) {
                    return true;
                }
            }
        }
        return false;
    }
    
    // ---------------------------------HELPER FUNCTIONS-------------------------------------//

    /**
     * createRow()
     * 
     * create a non empty array of StateID's
     * 
     * @param value
     *            - The Value to be placed in each slot of the array
     * 
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
     * size()
     * 
     * gets the numbers of rows in the table.
     * 
     * @return int - size of the table
     */
    public int size() {
        return table.size();
    }

    /**
     * findIndexOfChar()
     * 
     * Maps char in alphabet to an index
     * 
     * @param command
     *            - Letter from the alphabet
     * @return int - Index of the letter given --OR-- '-1' if the letter is not
     *         in the alphabet
     * 
     */
    public int findIndexOfChar(char command) {
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
     * get
     *
     * retrieves a single row from the table
     */
    public StateID[] get(int i)
    {
        return table.get(i);
    }

}
