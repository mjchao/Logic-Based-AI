package mjchao.mazenav.envir;

import java.util.ArrayList;

/**
 * Represents the maze environments
 * 
 * @author mjchao
 *
 */
class Environment {
	
	private final static int[] dx = { 0 , 1 , 0 , -1 };
	private final static int[] dy = { 1 , 0 , -1 , 0 };
	
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
	private Agent agent;
	private final int numRows;
	private final int numCols;
	
	public Environment( int rows , int cols ) {
		this.agent = new Agent();
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
	
	public ArrayList< Percept > getMoveToPercepts( int destRow , int destCol ) {
		ArrayList< Percept > rtn = new ArrayList< Percept >();
		if ( 0 <= destRow && destRow < numRows &&
				0 <= destCol && destCol < numCols ) {
			Tile targetTile = tiles[ destRow ][ destCol ];
			if ( targetTile.hasWumpus() ) {
				rtn.add( Percept.Die );
			}
			if ( targetTile.hasPit() ) {
				rtn.add( Percept.Die );
			}
		}
		else {
			rtn.add( Percept.Bump );
		}
		return rtn;
	}
	
	public ArrayList< Percept > forward() {
		int targetRow = agent.row + dx[ agent.dir ];
		int targetCol = agent.col + dy[ agent.dir ];
		if ( targetRow < numRows ) {
			agent.row = targetRow;
			agent.col = targetCol;
		}
		return getMoveToPercepts( targetRow , targetCol );
	}
	
	public void turnLeft() {
		agent.dir = (agent.dir + 1) % 4;
	}
	
	public void turnRight() {
		agent.dir = (agent.dir - 1) % 4;
	}
	
	public ArrayList< Percept > shoot() {
		ArrayList< Percept > rtn = new ArrayList< Percept >();
		
		//make sure the agent has arrows left
		if ( agent.numArrows <= 0 ) {
			return rtn;
		}
		
		//check if the agent shot the wumpus
		int checkRow = agent.row;
		int checkCol = agent.col;
		while( 0 <= checkRow && checkRow < numRows && 
				0 <= checkCol && checkCol < numCols ) {
			Tile toCheck = tiles[ checkRow ][ checkCol ];
			if ( tiles[ checkRow ][ checkCol ].hasWumpus() ) {
				rtn.add( Percept.Scream );
				tiles[ checkRow ][ checkCol ] = new Tile( false , toCheck.hasPit() , toCheck.hasGold() );
			}
			checkRow += dx[ agent.dir ];
			checkCol += dy[ agent.dir ];
		}
		return rtn;
	}
	
	public ArrayList< Percept > climb() {
		ArrayList< Percept > rtn = new ArrayList< Percept >();
		if ( agent.row == 0 && agent.col == 0 ) {
			rtn.add( Percept.Exit );
		}
		return rtn;
	}
	
	public void grab() {
		if ( tiles[ agent.row ][ agent.col ].hasGold() ) {
			
			//if the agent is on a tile with gold, the
			//tile cannot have a pit or wumpus in it
			//and once the agent grabs the gold, the gol
			//is gone
			tiles[ agent.row ][ agent.col ] = new Tile( false , false , false );
			agent.hasGold = true;
		}
	}
	
	public ArrayList< Percept > getTilePercepts() {
		ArrayList< Percept > rtn = new ArrayList< Percept >();
		Tile currTile = this.tiles[ agent.row ][ agent.col ];
		if ( currTile.hasGold() ) {
			rtn.add( Percept.Glitter );
		}
		if ( hasStench( agent.row , agent.col ) ) {
			rtn.add( Percept.Stench );
		}
		if ( hasBreeze( agent.row , agent.col ) ) {
			rtn.add( Percept.Breeze );
		}
		return rtn;
	}
	
	public boolean hasStench( int row , int col ) {
		for ( int i=0 ; i<4 ; ++i ) {
			int neighborRow = row+dy[ i ];
			int neighborCol = col+dx[ i ];
			if ( 0 <= neighborRow && neighborRow < numRows &&
					0 <= neighborCol && neighborCol < numCols ) {
				if ( tiles[ neighborRow ][ neighborCol ].hasWumpus() )  {
					return true;
				}
			}
		}
		return false;
	}
	
	public boolean hasBreeze( int row , int col ) {
		for ( int i=0 ; i<4 ; ++i ) {
			int neighborRow = row+dy[ i ];
			int neighborCol = col+dx[ i ];
			if ( 0 <= neighborRow && neighborRow < numRows &&
					0 <= neighborCol && neighborCol < numCols ) {
				if ( tiles[ neighborRow ][ neighborCol ].hasPit() )  {
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * 
	 * @return a copy of the current Agent data
	 */
	public Agent getAgentData() {
		return agent.clone();
	}
}
