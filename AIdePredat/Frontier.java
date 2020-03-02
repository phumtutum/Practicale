package search;

public interface Frontier{

	/* Adding a node */
	public void addNode(Node element);
	
	/* Clear the contents */
	public void clearContents();
	
	/* Test whether the frontier is empty */
	public boolean isEmpty();
	
	/* Remove a node if the frontier is empty */
	public Node removeNode();
	
	public int getMaxNodes();
	
}

