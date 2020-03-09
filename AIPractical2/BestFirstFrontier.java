package search;

import java.util.*;

public class BreadthFirstFrontier implements Frontier{

	Queue<Node> q = new LinkedList<Node>();
	int cntNodes = 0;
	int maxNodes = 0;
	
	public void addNode(Node element) {
		q.add(element);
		cntNodes++;
		maxNodes = Math.max(maxNodes, cntNodes);
	}

	public void clearContents() {
		q.clear();
		cntNodes = 0;
	}

	public boolean isEmpty() {
		return q.isEmpty();
	}

	public Node removeNode() {
		assert(!q.isEmpty());
		cntNodes--;
		return q.remove();
	}
	
	public int getCntNodes() {
		return cntNodes;
	}
	
	public int getMaxNodes() {
		return maxNodes;
	}

}
