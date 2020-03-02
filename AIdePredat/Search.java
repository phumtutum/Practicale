package search;

public interface Search {
	public Node findSolution(State root, GoalTest goalTest);
	
	public int getGeneratedNodes();
}
