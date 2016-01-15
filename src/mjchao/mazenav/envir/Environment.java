package mjchao.mazenav.envir;

/**
 * Represents the maze environments
 * 
 * @author mjchao
 *
 */
class Environment {

	public static final Environment createRandom( int rows , int cols ) {
		Environment rtn = new Environment( rows , cols );
		
		int wumpusRow = (int)(Math.random()*rows);
		int wumpusCol = (int)(Math.random()*cols);
		
		int goldRow = (int)(Math.random()*rows);
		int goldCol = (int)(Math.random()*cols);
		
		for ( int r=0 ; r<rows ; ++r ) {
			for ( int c=0 ; c<cols ; ++c ) {
				boolean hasWumpus = ( r == wumpusRow && c == wumpusCol );
				boolean hasGold = ( r == goldRow && c == goldCol );
				
				//10% chance that a given square has a pit
				boolean hasPit = (Math.random() <= 0.1 );
				rtn.tiles[ r ][ c ] = new Tile( hasWumpus , hasGold , hasPit );
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
