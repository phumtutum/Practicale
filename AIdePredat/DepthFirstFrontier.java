package search;

import java.util.*;

public class DepthFirstFrontier implements Frontier{
	
	Stack<Node> stack = new Stack<Node>();
	public int maxNodes = 0;
	public int cntNodes = 0;
	
	public void addNode(Node element) {
		stack.push(element);
		maxNodes = Math.max(stack.size(), maxNodes);
		cntNodes++;
	}

	public void clearContents() {
		stack.clear();
		cntNodes = 0;
	}


	public boolean isEmpty() {
		return stack.isEmpty();
	}

	public Node removeNode() {
		assert(!stack.isEmpty());
		cntNodes--;
		return stack.pop();
	}
	
	public int getMaxNodes(){
		return maxNodes;
	}

	
}
