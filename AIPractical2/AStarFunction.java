package search;

public class AStarFunction implements NodeFunction {
	
	NodeFunction heuristicFunction;
	
	public int ev(Node n) {
		return heuristicFunction.ev(n) + n.getG();
	}
	
	public AStarFunction(NodeFunction heuristicFunction) {
		this.heuristicFunction = heuristicFunction;
	}
	
}
