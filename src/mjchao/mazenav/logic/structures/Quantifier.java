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

	public static final Quantifier FORALL = new Quantifier( "FORALL" );
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
			return str.equals( "\u2200" );
		}
		else if ( name.equals( "EXISTS" ) ) {
			return str.equals( "\u2203" );
		}
		return false;
	}
	
	@Override
	public String toString() {
		if ( name.equals( "FORALL" ) ) {
			return "\u2200";
		}
		else if ( name.equals( "EXISTS" ) ) {
			return "\u2203";
		}
		else {
			return "?";
		}
	}

}
