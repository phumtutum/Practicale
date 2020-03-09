package npuzzle;

import search.Node;
import search.State;
import search.NodeFunction;


public class MisplacedTilesHeuristic implements NodeFunction {
	
	public int ev(Node n) {
		Tiles tiles = (Tiles) n.state;
		int cnt = 0;
		for(int i = 0 ; i < tiles.width*tiles.width - 1 ; ++i) {
			if(i + 1 != tiles.tiles[i]) {
				cnt++;
			}
		}
		
		return cnt;
	}
	
}
