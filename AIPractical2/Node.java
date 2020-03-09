package search;

public class Node {
	public final Node parent;
	public final Action action;
	public final State state;
	public int depth = 0;
	public int f = 0,g = 0;
	
	public Node(Node parent, Action action, State state, int depth, int f) {
		this.parent = parent;
		this.action = action;
		this.state = state;
		this.depth = depth;
		this.f = f;
		if(parent == null)
			this.g = 0;
		else
			this.g = parent.g + action.cost();
	}
	
	public Node(Node parent, Action action, State state, int depth) {
		this.parent = parent;
		this.action = action;
		this.state = state;
		this.depth = depth;
		if(parent == null)
			this.g = 0;
		else
			this.g = parent.g + action.cost();
	}
	
	public Node(Node parent, Action action, State state) {
		this.parent = parent;
		this.action = action;
		this.state = state;
		if(parent == null)
			this.g = this.depth = 0;
		else
		{
			this.g = parent.g + action.cost();
			this.depth = parent.depth + 1;
		}
	}
	
	public void setF(int f) {
		this.f = f;
	}
	
	public int getG() {
		return this.g;
	}
	
	public int getF() {
		return this.f;
	}
}
