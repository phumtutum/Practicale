package search;
import java.util.*;

public class GraphSearch implements Search{
	
	Frontier frontier;
	Set<State> seen = new HashSet<State>();
	int allNodes = 0;
	
	public Node findSolution(State root, GoalTest goalTest)
	{
		seen = new HashSet<State>();
		frontier.addNode(new Node(null, null, root));
		allNodes=1;
		while(!frontier.isEmpty()) {
			Node currNode = frontier.removeNode();
			seen.add(currNode.state);
			allNodes++;
			if(goalTest.isGoal(currNode.state))
			{
				frontier.clearContents();
				return currNode;
			}
			else
			{
				for(Action action : currNode.state.getApplicableActions())
				{
					State nextState = currNode.state.getActionResult(action);
					if(!seen.contains(nextState)){
						//one can add this
						
						frontier.addNode(new Node(currNode, action, nextState));
					}
				}
			}
		}
		
		return null;
	}
	
	public GraphSearch(Frontier fr){
		frontier = fr;
	}
	
	public int getGeneratedNodes() {
		return allNodes;
	}
}
