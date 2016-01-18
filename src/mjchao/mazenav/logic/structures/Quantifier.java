package mjchao.mazenav.logic.structures;

/**
 * Represents a quantifier in first-order logic (FOL). The currently-supported
 * quantifiers are as follows:
 * <ul>
 * 	<li> FORALL (∀)
 * 	<li> EXISTS (∃)
 * </ul>
 * @author mjchao
 *
 */
public class Quantifier extends Symbol {

	public static final String FORALL_SHORTHAND = "\u2200";
	public static final Quantifier FORALL = new Quantifier( "FORALL" );
	
	public static final String EXISTS_SHORTHAND = "\u2203";
	public static final Quantifier EXISTS = new Quantifier( "EXISTS" );
	public static final Quantifier[] QUANTIFIER_LIST = new Quantifier[]{ FORALL , EXISTS };
	
	public static Quantifier parseQuantifier( String str ) {
		for ( Quantifier q : QUANTIFIER_LIST ) {
			if ( q.equals( str ) ) {
				return q;
			}
		}
		
		//invalid quantifier
		return null;
	}
	
	private Quantifier( String name ) {
		super( name );
	}
	
	public boolean equals( String str ) {
		if ( name.equals( str ) ) {
			return true;
		}
		if ( name.equals( "FORALL" ) ) {
			return str.equals( FORALL_SHORTHAND );
		}
		else if ( name.equals( "EXISTS" ) ) {
			return str.equals( EXISTS_SHORTHAND );
		}
		return false;
	}
	
	@Override
	public String getShorthand() {
		if ( name.equals( "FORALL" ) ) {
			return FORALL_SHORTHAND;
		}
		else if ( name.equals( "EXISTS" ) ) {
			return EXISTS_SHORTHAND;
		}
		else {
			return "?";
		}
	}
	
	@Override
	public String toString() {
		return getShorthand();
	}

}
