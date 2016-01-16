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
		return envir.hasStench( row , col );
	}
	
	public boolean shouldRenderBreeze( int row , int col ) {
		return envir.hasBreeze( row , col );
	}
	
	public boolean shouldRenderAgent( int row , int col ) {
		return envir.hasAgent( row , col );
	}
}
