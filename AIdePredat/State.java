package search;

import java.util.Set;

public interface State {
	Set<? extends Action> getApplicableActions();
	State getActionResult(Action action);
	
	/* to detect equality */
	public boolean equals(Object that);

	/* So that it can be used in data structures */
	public int hashCode();
	
}
