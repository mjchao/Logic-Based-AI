package mjchao.mazenav.logic.structures;

/**
 * Provides convenient functions for working with
 * numbers in First-Order Logic
 * @author mjchao
 *
 */
public class NumbersFOL {
	
	public static ObjectFOL fromInt( int val ) {
		return new ObjectFOL( String.valueOf( val ) );
	}
	
	public static ObjectFOL fromDouble( double val ) {
		return new ObjectFOL( String.valueOf( val ) );
	}
	
	public static ObjectFOL fromFloat( float val ) {
		//we only use integer types and double precision
		//there is no need for optimization yet for floats
		return fromDouble( val );
	}
}
