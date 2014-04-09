package randomPackage;

import java.util.ArrayList;

public class temp {
    
    public ArrayList<Episode> episodicMemory = new ArrayList<Episode>(); //only to reduce red marks
    
    /**
     * 
     * @param currentPath
     * @return
     */
    public ListAndBool analyzeMove(ArrayList<Episode> currentPath){
     
        ArrayList<Integer> indexList = new ArrayList<Integer>();
        ArrayList<Episode> conjecturePath = new ArrayList<Episode>();
        Episode stateToMatch = currentPath.get(currentPath.size()-1);
        Episode currentState = currentPath.get(currentPath.size()-1);
        
        int currentMatchedPathLength = 0;
        
        //Find all indices in episodic memory of matching episodes
        indexList = checkIfEpisodeOccured(stateToMatch);
        
        //check to see if any episodes were matched
        if(indexList.size()>0){
            currentMatchedPathLength++; //add 1 to the current matched path length. 
            
            //now we need to move the state that we need to match, back 1.
            if (currentMatchedPathLength < currentPath.size()){
                stateToMatch = currentPath.get(currentPath.size()-(currentMatchedPathLength + 1)); //add 1 to not 
                                                                                                   //get last element
            }//if
            else {
                stateToMatch = currentPath.get(currentPath.size()-(currentMatchedPathLength)); //Don't add 1 because
                                                                                              //matchedlength = length
            }//else
        }//if
        else {  //if we get here, we have not found a match for the state we are looking for.
            ListAndBool NoMatch = new ListAndBool(null,false);
        }
        
        //could be in with the loop in another method? 
        ArrayList<Integer> indexListTemp = new ArrayList<Integer>();
        //? boolean breakFlag = false;
        
        // * narrowMatches() * ==> to match the next spot.
        
        
        //should this be it's own method? 
        while(indexList.size() > 1){
            indexListTemp = indexList; //This is a safe guard in case no matches are found. 
            indexList = narrowMatches(episodicMemory, indexList);
            if(indexList.size() <= 0){
                indexList = indexListTemp;
                break;
            }
        }//while
    }//analyzeMove();
    
    
    /**
     * 
     * @param listOfEpisodes
     * @param indexList
     * @return
     */
    public ArrayList<Integer> narrowMatches(ArrayList<Episode>listOfEpisodes, ArrayList<Integer> indexList){
        if(indexList.size() == 1){
            return indexList;
        }
        
        
        
        return indexList; //Place Holder
    }//NarrowMatches();
    
    
}
