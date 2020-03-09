package search;

import java.util.Comparator;
import java.util.PriorityQueue;


public class BestFirstFrontier implements Frontier {

	Comparator<Node> nodeComparator = new Comparator<Node>() {
		@Override
		public int compare(Node n1, Node n2) {
			return n1.f - n2.f;
		}
	};
	
	PriorityQueue<Node> pq = new PriorityQueue<>(nodeComparator);
	
	int maximum = 0;
	
	NodeFunction fevaluator;
	
	public BestFirstFrontier(NodeFunction fevaluator) {
		this.fevaluator = fevaluator;
	}
	
	public void addNode(Node element) {
		int rez = fevaluator.ev(element);
		element.setF(rez);
		pq.add(element);
		maximum = Math.max(maximum,  pq.size());
	}
	
	public void clearContents() {
		pq.clear();
	}
	
	public boolean isEmpty() {
		return pq.isEmpty();
	}
	
	public Node removeNode() {
		return pq.remove();
	}
	
	public int getMaxNodes() {
		return maximum;
	}
	
}
