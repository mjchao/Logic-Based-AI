package mjchao.mazenav.logic.structures;

/**
 * Represents an operator in first-order logic (FOL). The currently-supported
 * operators are as follows (in order of decreasing precedence):
 * <ul>
 * 	<li> ! (NOT)
 *	<li> == (EQUALS)
 *	<li> && (AND)
 *	<li> || (OR)
 *	<li> => (IMPLICATION)
 *	<li> <=> (BICONDITIONAL)
 * </ul>
 * @author mjchao
 *
 */
public class Operator extends Symbol {

	public static final Operator NOT = new Operator( "NOT" );
	public static final Operator EQUALS = new Operator( "EQUALS" );
	public static final Operator AND = new Operator( "AND" );
	public static final Operator OR = new Operator( "OR" );
	public static final Operator IMPLICATION = new Operator( "IMPLICATION" );
	public static final Operator BICONDITIONAL = new Operator( "BICONDITIONAL" );
	
	public static final Operator[] OPERATOR_LIST = new Operator[] { 
			NOT , EQUALS , AND , OR , IMPLICATION , BICONDITIONAL 
			};
	
	/**
	 * Attempts to convert the given string to an operator, or
	 * returns null if the attempt failed.
	 * 
	 * @param str 		a string to be converted to an operator
	 * @return			the converted operator, or null if conversion
	 * 					failed
	 */
	public static Operator parseOperator( String str ) {
		for ( Operator op : OPERATOR_LIST ) {
			if ( op.getName().equals( str ) || op.toString().equals( str ) ) {
				return op;
			}
		}
		
		//not an operator
		return null;
	}
	
	private Operator( String name ) {
		super( name );
	}
	
	public boolean equals( String str ) {
		if ( name.equals( str ) ) {
			return true;
		}
		
		if ( name.equals( "NOT" ) ) {
			return str.equals( "!" );
		}
		else if ( name.equals( "EQUALS" ) ) {
			return str.equals( "==" );
		}
		else if ( name.equals( "AND" ) ) {
			return str.equals( "&&" );
		}
		else if ( name.equals( "OR" ) ) {
			return str.equals( "||" );
		}
		else if ( name.equals( "IMPLICATION" ) ) {
			return str.equals( "=>" );
		}
		else if ( name.equals( "BICONDITIONAL" ) ) {
			return str.equals( "<=>" );
		}
		return false;
	}
	
	@Override
	public String toString() {
		if ( name.equals( "NOT" ) ) {
			return "!";
		}
		else if ( name.equals( "EQUALS" ) ) {
			return "==";
		}
		else if ( name.equals( "AND" ) ) {
			return "&&";
		}
		else if ( name.equals( "OR" ) ) {
			return "||";
		}
		else if ( name.equals( "IMPLICATION" ) ) {
			return "=>";
		}
		else if ( name.equals( "BICONDITIONAL" ) ) {
			return "<=>";
		}
		else {
			return "?";
		}
	}
}
