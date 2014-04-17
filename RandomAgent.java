package randomPackage;

import java.util.*;

public class RandomAgent extends Agent {

    /**
     * RandomAgent Constructor.
     */
    public RandomAgent() {
        super();
    }

    /**
     * RandomAgent Ctor
     */
    public RandomAgent(StateMachineEnvironment environment) {
        super(environment);
    }

    /**
     * main
     * 
     * @param args
     */
    public static void main(String[] args) {

        /*
         * RandomAgent test = new RandomAgent(); test.findRandomPath();
         * test.env.printStateMachine(); test.printPath();
         * 
         * StateMachineEnvironment testEnv = new StateMachineEnvironment(5,2);
         * RandomAgent test2 = new RandomAgent(testEnv); test2.findRandomPath();
         * test2.env.printStateMachine(); test2.printPath();
         */

        /*
         * //TEST 1 smartAgent tester = new smartAgent();
         * 
         * //this is going to be used to test the path matching algorithm
         * ArrayList<Episode> testMem = new ArrayList<Episode>();
         * testMem.add(new Episode('b', 0, 1)); testMem.add(new Episode('a', 1,
         * 2)); testMem.add(new Episode('b', 1, 3)); testMem.add(new
         * Episode('b', 2, 4));
         * 
         * ArrayList<Episode> testCurrent = new ArrayList<Episode>();
         * testCurrent.add(new Episode('a', 1, 9));
         * 
         * tester.episodicMemory = testMem; tester.currentPath = testCurrent;
         * 
         * if(tester.analyzeMove().getReturnValue()){
         * System.out.println("SUCCESS!!!!"); }
         * 
         * 
         * //TEST 2
         * 
         * StateMachineEnvironment testEnv5 = new StateMachineEnvironment(5,4);
         * smartAgent tester2 = new smartAgent(testEnv5);
         * 
         * ArrayList<Episode> testMem2 = new ArrayList<Episode>();
         * testMem2.add(new Episode(' ', 0, 1)); testMem2.add(new Episode('b',
         * 0, 2)); testMem2.add(new Episode('c', 0, 3)); testMem2.add(new
         * Episode('b', 0, 4)); testMem2.add(new Episode('c', 0, 5));
         * testMem2.add(new Episode('d', 2, 6));
         * 
         * ArrayList<Episode> testCurrent2 = new ArrayList<Episode>();
         * testCurrent2.add(new Episode('a', 0, 100)); testCurrent2.add(new
         * Episode('b', 0, 200)); testCurrent2.add(new Episode('c', 0, 300));
         * 
         * tester2.episodicMemory = testMem2; tester2.currentPath =
         * testCurrent2;
         * 
         * if(tester2.analyzeMove().getReturnValue()){
         * System.out.println("SUCCESS!!!!"); }
         * 
         * tester2.initTransTable();
         * 
         * tester2.printTransTable();
         */

        // ********************************************************

    	StateMachineEnvironment easy = new StateMachineEnvironment(2,2);
    	
    	easy.transition[0][0] = 0;
    	easy.transition[0][1] = 1;
    	easy.transition[1][0] = 1;
    	easy.transition[1][1] = 1;
    	easy.findShortestPaths();
    	
    	easy.printStateMachine();
    	
    	smartAgent easyTest = new smartAgent(easy);
    	easyTest.run();
    	
    	
    	
    	//------------------------------------------------------
    	/*
        StateMachineEnvironment testEnv9000 = new StateMachineEnvironment(5, 3);
        // testEnv9000.printStateMachine();

        StateMachineEnvironment testEnvStatic = new StateMachineEnvironment(5,
                3);
        testEnvStatic.transition[0][0] = 0;
        testEnvStatic.transition[0][1] = 2;
        testEnvStatic.transition[0][2] = 0;
        testEnvStatic.transition[1][0] = 1;
        testEnvStatic.transition[1][1] = 0;
        testEnvStatic.transition[1][2] = 1;
        testEnvStatic.transition[2][0] = 4;
        testEnvStatic.transition[2][1] = 2;
        testEnvStatic.transition[2][2] = 2;
        testEnvStatic.transition[3][0] = 3;
        testEnvStatic.transition[3][1] = 4;
        testEnvStatic.transition[3][2] = 3;
        testEnvStatic.transition[4][0] = 2;
        testEnvStatic.transition[4][1] = 4;
        testEnvStatic.transition[4][2] = 4;
        testEnvStatic.findShortestPaths();

        testEnvStatic.printStateMachine();

        smartAgent realTest = new smartAgent(testEnvStatic);
        realTest.run();*/

    }

}