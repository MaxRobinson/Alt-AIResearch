package randomPackage;

import java.util.ArrayList;

public class temp extends smartAgent {
    
    //public ArrayList<Episode> episodicMemory = new ArrayList<Episode>(); //only to reduce red marks
    
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
            ListAndBool noMatch = new ListAndBool(null,false);
            return noMatch;
        }
        
        //could be in with the loop in another method? 
        ArrayList<Integer> indexListTemp = new ArrayList<Integer>();
        // * narrowMatches() * ==> to match the next spot.
        //should this be it's own method? 
        while(indexList.size() > 1){
            
            //The State we are matching needs to be changing and it is not...
            // need to do something like stateToMatch = currentPath.get(currentMatchedPathLength);
            stateToMatch = currentPath.get(currentMatchedPathLength);
            
            indexListTemp = indexList; //This is a safe guard in case no matches are found. 
            indexList = narrowMatches(episodicMemory, indexList,stateToMatch); //then pass in here <<stateToMatch
            //Makes sure that indexList length is not 0, it it is, 
            //go back to the last list that had something in it and break
            if(indexList.size() <= 0){
                indexList = indexListTemp;
                break;
            }
            currentMatchedPathLength++; //we can do this because we know the indexList is not 0;
        }//while
        
        
        // HERE We have a list of Indices that go as far back as the currentMatchedPathLength from the
        // original state we wanted to match.
        int index = indexList.get(indexList.size()-1) + currentMatchedPathLength; //gets back to the episode we want
        
        //build Conjecture Path
        conjecturePath = buildConjecturePath(index);
        
        //test conjectured Path
        ListAndBool newPath = testConjecture(conjecturePath);
        boolean areSame = newPath.getReturnValue(); //checks if the episodes were found to be the same
                                                    //may not matter
        if(newPath.getConjecturePath() != null){
            transitionTable.addPath(newPath.getConjecturePath()); //addPath to transTable May not want to do here.
        }
        
        return newPath;

    }//analyzeMove();
    
    
    /**
     * 
     * @param listOfEpisodes
     * @param indexList
     * @return
     */
    public ArrayList<Integer> narrowMatches(ArrayList<Episode>listOfEpisodes, ArrayList<Integer> indexList, Episode stateToMatch){
        if(indexList.size() == 1){
            return indexList;
        }
        //decriment all of the indices in the index list by 1
        ArrayList<Integer> newIndexList = decrementArrayList(indexList,1);
        //check if episode has occurred.
        ArrayList<Integer> resultIndexList = checkIfEpisodeOccured(newIndexList,stateToMatch);
        
        
        return resultIndexList; //Place Holder
    }//NarrowMatches();
    
    
}
