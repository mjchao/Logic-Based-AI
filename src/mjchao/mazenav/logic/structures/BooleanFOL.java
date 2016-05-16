package mjchao.mazenav.logic.structures;

/**
 * Represents a boolean (True or False) in first-order logic (FOL).
 * 
 * @author mjchao
 *
 */
public class BooleanFOL extends ObjectFOL {

	public static BooleanFOL fromBoolean( boolean b ) {
		return b ? True() : False();
	}
	public static BooleanFOL True() {
		return new BooleanFOL( "True" );
	}
	
	public static BooleanFOL False() {
		return new BooleanFOL( "False" );
	}
	
	private BooleanFOL( String name ) {
		super( name );
	}
	
	@Override
	public boolean equals( Object other ) {
		if ( other instanceof BooleanFOL ) {
			return this.name.equals( ((BooleanFOL) other).name );
		}
		return false;
	}

}
