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
		return new BooleanFOL( "True" , Boolean.valueOf( true ) );
	}
	
	public static BooleanFOL False() {
		return new BooleanFOL( "False" , Boolean.valueOf( false ) );
	}
	
	private BooleanFOL( String name , Object value ) {
		super( name , value , new String[]{ "Boolean" } );
	}
	
	@Override
	public boolean equals( Object other ) {
		if ( other instanceof BooleanFOL ) {
			return this.getValue().equals( ((BooleanFOL)other).getValue() );
		}
		return false;
	}
	
	@Override
	public Boolean getValue() {
		return (Boolean) super.getValue();
	}

}
