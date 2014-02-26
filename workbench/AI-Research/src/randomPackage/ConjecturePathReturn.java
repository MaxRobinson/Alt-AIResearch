package randomPackage;

import java.util.ArrayList;

public class ConjecturePathReturn {
	private ArrayList<Episode> conjecturePath;
	private boolean returnValue;
	
	
	//constructor
	ConjecturePathReturn(ArrayList<Episode> conjecturePath, boolean returnValue ){
		this.conjecturePath = new ArrayList<Episode>(conjecturePath); 
		this.returnValue = returnValue;
	}
	
	public ArrayList<Episode> getConjecturePath(){
		return this.conjecturePath;
	}
	
	public boolean getReturnValue(){
		return this.returnValue;
	}
	
}
