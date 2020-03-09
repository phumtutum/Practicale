package npuzzle;

import search.Printing;
import npuzzle.NPuzzlePrinting;
import npuzzle.Tiles;
import npuzzle.TilesGoalTest;
import search.*;
import tour.*;

public class Main {

	Node globalNode;
	
	
	public static void runAll(String s, State init, GoalTest goal) {
        System.out.println(s + ":");
        System.out.println();
        Frontier TreeBFFrontier = new BreadthFirstFrontier();
        Search TreeSearchBF = new TreeSearch(TreeBFFrontier);
        Node solution1 = TreeSearchBF.findSolution(init, goal);
        System.out.println("Tree BFS: " + TreeBFFrontier.getMaxNodes() + " " + TreeSearchBF.getGeneratedNodes());
        
        
        Frontier GraphBFFrontier = new BreadthFirstFrontier();
        Search GraphSearchBF = new GraphSearch(GraphBFFrontier);
        Node solution2 = GraphSearchBF.findSolution(init, goal);
        System.out.println("Graph BFS: " + GraphBFFrontier.getMaxNodes() + " " + GraphSearchBF.getGeneratedNodes());
        
        
        Frontier GraphDFFrontier = new DepthFirstFrontier();
        Search GraphSearchDF = new GraphSearch(GraphDFFrontier);
        Node solution4 = GraphSearchDF.findSolution(init, goal);
        System.out.println("Graph DFS: " + GraphDFFrontier.getMaxNodes() + " " + GraphSearchDF.getGeneratedNodes());
       
        Frontier TreeDFFrontier = new DepthFirstFrontier();
        Search TreeSearchDF = new TreeSearch(TreeDFFrontier);
        IterativeDeepeningTreeSearch IterativeDeepening = new IterativeDeepeningTreeSearch();
        // Node solution3 = TreeSearchDF.findSolution(init, goal);
        Node solution5 = IterativeDeepening.findSolution(init, goal);
        
        //System.out.println("Tree DFS is failing" + TreeDFFrontier.getMaxNodes() + " " + TreeSearchDF.getGeneratedNodes());
        
        System.out.println("Iterative deepening: " + IterativeDeepening.getMaxNodes() + " " + IterativeDeepening.getGeneratedNodes());
        System.out.println();
        if(solution1.state instanceof Tiles) {
        	Printing printer = new NPuzzlePrinting();
            System.out.println("Tree BFS solution:");
            printer.printSolution(solution1);
        }
        else {
        	Printing printer = new TourPrinting();
            System.out.println("Tree BFS solution:");
            printer.printSolution(solution1);
        }
        
    }
	
	public static void secondPractical(State init, GoalTest goalTest) {
		
		// A* tree search Tiles
		AStarFunction f = new AStarFunction(new MisplacedTilesHeuristic());
		Frontier TilesFrontier = new BestFirstFrontier(f);
		Search TilesAStarTreeSearch = new TreeSearch(TilesFrontier);
		Node solution1 = TilesAStarTreeSearch.findSolution(init, goalTest);
		System.out.println("Tiles A* Tree Search: " + TilesFrontier.getMaxNodes() + " " + TilesAStarTreeSearch.getGeneratedNodes());
		Printing printer1 = new NPuzzlePrinting();
		System.out.println("A* tree search solution: ");
		printer1.printSolution(solution1);
		
		
		//A* graph search Tiles
		Frontier TilesFrontier2 = new BestFirstFrontier(f);
		Search TilesAStarGraphSearch = new GraphSearch(TilesFrontier2);
		Node solution2 = TilesAStarGraphSearch.findSolution(init, goalTest);
		System.out.println("Tiles A* Graph Search: " + TilesFrontier2.getMaxNodes() + " " + TilesAStarGraphSearch.getGeneratedNodes());
		Printing printer = new NPuzzlePrinting();
		System.out.println("A* graph search solution: ");
		printer.printSolution(solution2);
	}
	
	public static void secondPracticalRomaniaTour(State init, GoalTest goalTest,Cities romania, City goalCity) {
		// A* tree search Tiles
		AStarFunction f = new AStarFunction(new RomaniaHeuristic(romania.getAllCities(), goalCity));
		Frontier TourFrontier = new BestFirstFrontier(f);
		Search TourAStarTreeSearch = new TreeSearch(TourFrontier);
		Node solution1 = TourAStarTreeSearch.findSolution(init, goalTest);
		System.out.println("Romania Tour A* Tree Search: " + TourFrontier.getMaxNodes() + " " + TourAStarTreeSearch.getGeneratedNodes());
		TourPrinting printer1 = new TourPrinting();
		System.out.println("A* tree search solution to Romania Tour: ");
		printer1.printSolution(solution1);
		printer1.printCost(solution1);
	}

    public static void main(String[] args) {
        Tiles initialConfiguration = new Tiles(new int[][] {
                { 7, 4, 2 },
                { 8, 1, 3 },
                { 5, 0, 6 }
        });

        GoalTest goalTest1 = new TilesGoalTest();
        /*runAll("8-puzzle", initialConfiguration, goalTest1);*/

        Cities romania = SetUpRomania.getRomaniaMapSmall();
        City startCity = romania.getState("Bucharest");
        GoalTest goalTest2 = new TourGoalTest(romania.getAllCities(), startCity);
        //runAll("Map of romania", new TourState(startCity), goalTest2);
        
        secondPractical(initialConfiguration, goalTest1);
        secondPracticalRomaniaTour(new TourState(startCity), goalTest2, romania, startCity);
    }
}
