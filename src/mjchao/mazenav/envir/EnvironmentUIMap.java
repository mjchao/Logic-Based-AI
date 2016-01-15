package mjchao.mazenav.envir;

/**
 * Provides an interface for the user interface
 * to render the entire environment
 * 
 * @author mjchao
 *
 */
public class EnvironmentUIMap {

	private final Environment envir;
	private final int[] dx = { 1 , 0 , -1 , 0 };
	private final int[] dy = { 0 , 1 , 0 , -1 };
	
	public EnvironmentUIMap( Environment envir ) {
		this.envir = envir;
	}
	
	public int getNumRows() {
		return this.envir.getNumRows();
	}
	
	public int getNumCols() {
		return this.envir.getNumCols();
	}
	
	public boolean shouldRenderWumpus( int row , int col ) {
		return envir.getTile( row , col ).hasWumpus();
	}
	
	public boolean shouldRenderPit( int row , int col ) {
		return envir.getTile( row , col ).hasPit();
	}
	
	public boolean shouldRenderGold( int row , int col ) {
		return envir.getTile( row , col ).hasGold();
	}
	
	public boolean shouldRenderStench( int row , int col ) {
		for ( int i=0 ; i<4 ; ++i ) {
			int neighborRow = row+dy[ i ];
			int neighborCol = col+dx[ i ];
			if ( 0 <= neighborRow && neighborRow < envir.getNumRows() &&
					0 <= neighborCol && neighborCol < envir.getNumCols() ) {
				if ( envir.getTile( neighborRow , neighborCol ).hasWumpus() )  {
					return true;
				}
			}
		}
		return false;
	}
	
	public boolean shouldRenderBreeze( int row , int col ) {
		for ( int i=0 ; i<4 ; ++i ) {
			int neighborRow = row+dy[ i ];
			int neighborCol = col+dx[ i ];
			if ( 0 <= neighborRow && neighborRow < envir.getNumRows() &&
					0 <= neighborCol && neighborCol < envir.getNumCols() ) {
				if ( envir.getTile( neighborRow , neighborCol ).hasPit() )  {
					return true;
				}
			}
		}
		return false;
	}
}
