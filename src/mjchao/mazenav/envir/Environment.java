package mjchao.mazenav.envir;

/**
 * Represents the maze environments
 * 
 * @author mjchao
 *
 */
class Environment {
	public static final double PIT_PROBABILITY = 0.5;

	public static final Environment createRandom( int rows , int cols ) {
		Environment rtn = new Environment( rows , cols );
		
		int wumpusRow = 0 , wumpusCol = 0;
		while ( wumpusRow == 0 && wumpusCol == 0 ) {
			wumpusRow = (int)(Math.random()*rows);
			wumpusCol = (int)(Math.random()*cols);
		}

		int goldRow = (int)(Math.random()*rows);
		int goldCol = (int)(Math.random()*cols);
		
		for ( int r=0 ; r<rows ; ++r ) {
			for ( int c=0 ; c<cols ; ++c ) {
				boolean hasWumpus = ( r == wumpusRow && c == wumpusCol );
				boolean hasGold = ( r == goldRow && c == goldCol );
				
				//10% chance that a given square has a pit
				//given that the square does not have gold or the wumpus
				boolean hasPit = (!hasWumpus && !hasGold && Math.random() <= PIT_PROBABILITY );
				
				//cannot have starting square be a pit
				if ( r == 0 && c == 0 ) {
					hasPit = false;
				}
				rtn.tiles[ r ][ c ] = new Tile( hasWumpus , hasPit , hasGold );
			}
		}
		
		return rtn;
	}
	
	private final Tile[][] tiles;
	private final int numRows;
	private final int numCols;
	
	public Environment( int rows , int cols ) {
		this.numRows = rows;
		this.numCols = cols;
		tiles = new Tile[ rows ][ cols ];
		for ( int r=0 ; r<rows ; ++r ) {
			for ( int c=0 ; c<cols ; ++c ) {
				tiles[ r ][ c ] = new Tile();
			}
		}
	}
	
	public Tile getTile( int row , int col ) {
		return this.tiles[ row ][ col ];
	}
	
	public int getNumRows() {
		return this.numRows;
	}
	
	public int getNumCols() {
		return this.numCols;
	}
}
