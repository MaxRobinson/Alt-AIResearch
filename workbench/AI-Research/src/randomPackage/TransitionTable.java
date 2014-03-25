package randomPackage;

import java.util.ArrayList;

public class TransitionTable {
	
	protected ArrayList<StateID[]> table;
	protected char[] alphabet;
	
	public TransitionTable(){
		table = new ArrayList<StateID[]>();
		table.add(null);
		alphabet = null;
	}
	
	//This one is for taking an array of Chars
	public TransitionTable(char[] alphabet){
		table = new ArrayList<StateID[]>();
		table.add(null);
		this.alphabet = alphabet;
	}
	
	public void addNullRow(){
		table.add(null);
	}
	
	public void addEmptyRow(){
		
	}
	
}
