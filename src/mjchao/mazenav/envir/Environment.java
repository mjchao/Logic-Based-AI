package mjchao.mazenav.envir;

import java.util.ArrayList;

/**
 * Represents the maze environments
 * 
 * @author mjchao
 *
 */
class Environment {
	
	private final static int[] dRow = { 1 , 0 , -1 , 0 };
	private final static int[] dCol = { 0 , 1 , 0 , -1 };
	
	public static final double PIT_PROBABILITY = 0.1;

	public static final Environment createRandom( int rows , int cols ) {
		Environment rtn = new Environment( rows , cols );
		
		int wumpusRow = 0 , wumpusCol = 0;
		
		//wumpus cannot be on starting square or squares
		//adjacent to starting square
		while ( (wumpusRow == 0 && wumpusCol == 0) ||
				(wumpusRow == 1 && wumpusCol == 0) ||
				(wumpusRow == 0 && wumpusCol == 1) ) {
			wumpusRow = (int)(Math.random()*rows);
			wumpusCol = (int)(Math.random()*cols);
		}

		//make the problem a bit challenging
		//by putting the gold away from the starting
		//square
		int goldRow = (int)(Math.random()*rows/2) + rows/2;
		int goldCol = (int)(Math.random()*cols/2) + rows/2;
		
		for ( int r=0 ; r<rows ; ++r ) {
			for ( int c=0 ; c<cols ; ++c ) {
				boolean hasWumpus = ( r == wumpusRow && c == wumpusCol );
				boolean hasGold = ( r == goldRow && c == goldCol );
				
				//10% chance that a given square has a pit
				//given that the square does not have gold or the wumpus
				boolean hasPit = (!hasWumpus && !hasGold && Math.random() <= PIT_PROBABILITY );
				
				//cannot have starting square or squares
				//adjacent to starting square be a pit
				if ( (r == 0 && c == 0) ||
						(r == 1 && c == 0 ) ||
						(r == 0 && c == 1 ) ) {
					hasPit = false;
				}
				rtn.tiles[ r ][ c ] = new Tile( hasWumpus , hasPit , hasGold );
			}
		}
		
		return rtn;
	}
	
	private final Tile[][] tiles;
	private Agent agent;
	private boolean alive = true;
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
				alive = false;
			}
			if ( targetTile.hasPit() ) {
				rtn.add( Percept.Die );
				alive = false;
			}
		}
		else {
			rtn.add( Percept.Bump );
		}
		return rtn;
	}
	
	public ArrayList< Percept > forward() {
		int targetRow = agent.row + dRow[ agent.dir ];
		int targetCol = agent.col + dCol[ agent.dir ];
		System.out.println( targetRow + " " + targetCol );
		if ( 0 <= targetRow && targetRow < numRows &&
				0 <= targetCol && targetCol < numCols ) {
			agent.row = targetRow;
			agent.col = targetCol;
		}
		return getMoveToPercepts( targetRow , targetCol );
	}
	
	public ArrayList< Percept > turnLeft() {
		agent.dir = (agent.dir + 1) % 4;
		return new ArrayList< Percept >();
	}
	
	public ArrayList< Percept > turnRight() {
		agent.dir = (agent.dir - 1) % 4;
		return new ArrayList< Percept >();
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
			checkRow += dRow[ agent.dir ];
			checkCol += dCol[ agent.dir ];
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
	
	public ArrayList< Percept > grab() {
		if ( tiles[ agent.row ][ agent.col ].hasGold() ) {
			
			//if the agent is on a tile with gold, the
			//tile cannot have a pit or wumpus in it
			//and once the agent grabs the gold, the gol
			//is gone
			tiles[ agent.row ][ agent.col ] = new Tile( false , false , false );
			agent.hasGold = true;
		}
		return new ArrayList< Percept >();
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
		int breezes = countBreezes( agent.row , agent.col );
		for ( int i=0 ; i<breezes ; ++i ) {
			rtn.add( Percept.Breeze );
		}
		return rtn;
	}
	
	public boolean hasStench( int row , int col ) {
		for ( int i=0 ; i<4 ; ++i ) {
			int neighborRow = row+dRow[ i ];
			int neighborCol = col+dCol[ i ];
			if ( 0 <= neighborRow && neighborRow < numRows &&
					0 <= neighborCol && neighborCol < numCols ) {
				if ( tiles[ neighborRow ][ neighborCol ].hasWumpus() )  {
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * Returns the number of breezes the agent should feel,
	 * corresponding to the number of pits in adjacent
	 * squares
	 * 
	 * @param row
	 * @param col
	 * @return
	 */
	public int countBreezes( int row , int col ) {
		int count = 0;
		for ( int i=0 ; i<4 ; ++i ) {
			int neighborRow = row+dRow[ i ];
			int neighborCol = col+dCol[ i ];
			if ( 0 <= neighborRow && neighborRow < numRows &&
					0 <= neighborCol && neighborCol < numCols ) {
				if ( tiles[ neighborRow ][ neighborCol ].hasPit() )  {
					++count;
					
					//Return here if we choose to go back to the
					//single-breeze model where there is no indication
					//of how many adjacent pits there are
					//return count;
				}
			}
		}
		return count;
	}
	
	public boolean hasBreeze( int row , int col ) {
		return countBreezes( row , col ) > 0;
	}
	
	public boolean hasAgent( int row , int col ) {
		return agent.row == row && agent.col == col;
	}
	
	public boolean alive() {
		return alive;
	}
	
	/**
	 * 
	 * @return a copy of the current Agent data
	 */
	public Agent getAgentData() {
		return agent.clone();
	}
}
