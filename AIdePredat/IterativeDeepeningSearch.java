package search;

public class IterativeDeepeningTreeSearch implements Search{

	int bestDepth;
	Frontier frontier = new DepthFirstFrontier();
	int maxGeneratedNodes = 0;
	int allNodes = 0;
	Node depthLimitedSearch(State root, GoalTest goalTest, int depthLimit) {
		
		frontier.addNode(new Node(null, null, root));
		allNodes++;
		while(!frontier.isEmpty())
		{
			Node currNode = frontier.removeNode();
			if(bestDepth < currNode.depth)
				bestDepth = currNode.depth;
			if(goalTest.isGoal(currNode.state))
			{
				//problem solved
				frontier.clearContents();
				return currNode;
			}
			else
			{
				if(currNode.depth + 1 <= depthLimit)
				{
					for(Action action : currNode.state.getApplicableActions())
					{
						State nextState = currNode.state.getActionResult(action);
						allNodes++;
						frontier.addNode(new Node(currNode, action, nextState, currNode.depth + 1));
					}
				}
			}
		}
		
		return null;
	}
	
	public Node findSolution(State root, GoalTest goalTest) {
		bestDepth = 0;
		boolean ok = true;
		for(int maxDepth = 1 ; ok ; maxDepth++)
		{
			Node result = depthLimitedSearch(root, goalTest, maxDepth);
			if(bestDepth < maxDepth)
				ok = false;
			if(result != null)
				return result;
		}
		
		return null;
	}
	
	public int getMaxNodes() {
		return frontier.getMaxNodes();
	}
	
	public int getGeneratedNodes() {
		return allNodes;
	}
	
	
	
}
