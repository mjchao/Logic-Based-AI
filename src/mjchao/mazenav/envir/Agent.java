package mjchao.mazenav.envir;

/**
 * Stores data about the agent in the Wumpus World
 * 
 * @author mjchao
 *
 */
public class Agent implements Cloneable {

	public int row = 0;
	public int col = 0;
	public int dir = 0;
	public int numArrows = 1;
	public boolean hasGold = false;
	
	public Agent() {
		
	}
	
	@Override
	public Agent clone() {
		Agent rtn = new Agent();
		rtn.row = row;
		rtn.col = col;
		rtn.dir = dir;
		rtn.numArrows = numArrows;
		rtn.hasGold = hasGold;
		return rtn;
	}
}
