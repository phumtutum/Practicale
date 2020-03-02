package search;

public class TreeSearch implements Search {

	Frontier frontier;
	
	int allNodes = 0;

	public Node findSolution(State root, GoalTest goalTest) {
		
		frontier.addNode(new Node(null, null, root));
		allNodes++;
		while(!frontier.isEmpty())
		{
			Node currNode = frontier.removeNode();
			if(goalTest.isGoal(currNode.state))
			{
				//problem solved
				frontier.clearContents();
				return currNode;
			}
			else
			{
				for(Action action : currNode.state.getApplicableActions())
				{
					State nextState = currNode.state.getActionResult(action);
					allNodes++;
					frontier.addNode(new Node(currNode, action, nextState));
				}
			}
		}
		
		return null;
	}
	
	public TreeSearch(Frontier fr){
		frontier = fr;
	}
	
	public int getGeneratedNodes() {
		return allNodes;
	}

}
