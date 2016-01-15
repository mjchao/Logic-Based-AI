package mjchao.mazenav.envir;

/**
 * Represents a tile in the maze.
 * 
 * @author mjchao
 *
 */
class Tile {

	private final boolean hasWumpus;
	private final boolean hasPit;
	private final boolean hasGold;
	
	/**
	 * Creates an empty tile
	 */
	public Tile() {
		this( false , false , false );
	}
	
	/**
	 * Creates a tile with the given properties
	 * 
	 * @param hasWumpus
	 * @param hasPit
	 * @param hasGold
	 */
	public Tile( boolean hasWumpus , boolean hasPit , boolean hasGold ) {
		this.hasWumpus = hasWumpus;
		this.hasPit = hasPit;
		this.hasGold = hasGold;
	}
	
	public boolean empty() {
		return !hasWumpus && !hasPit && !hasGold;
	}
	
	public boolean hasWumpus() {
		return hasWumpus;
	}
	
	public boolean hasPit() {
		return hasPit;
	}
	
	public boolean hasGold() {
		return hasGold;
	}
}
