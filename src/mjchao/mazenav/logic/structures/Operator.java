package mjchao.mazenav.logic.structures;

/**
 * Represents an operator in first-order logic (FOL). The currently-supported
 * operators are as follows (in order of decreasing precedence):
 * <ul>
 * 	<li> ! (NOT)
 *	<li> == (EQUALS)
 *	<li> && (AND)
 *	<li> || (OR)
 *	<li> --> (IMPLICATION)
 *	<li> <-> (BICONDITIONAL)
 * </ul>
 * @author mjchao
 *
 */
public class Operator extends Symbol {

	public static final String NOT_SHORTHAND = "!";
	public static final Operator NOT = new Operator( "NOT" );
	
	public static final String EQUALS_SHORTHAND = "==";
	public static final Operator EQUALS = new Operator( "EQUALS" );
	
	public static final String AND_SHORTHAND = "&&";
	public static final Operator AND = new Operator( "AND" );
	
	public static final String OR_SHORTHAND = "||";
	public static final Operator OR = new Operator( "OR" );
	
	public static final String IMPLICATION_SHORTHAND = "-->";
	public static final Operator IMPLICATION = new Operator( "IMPLICATION" );
	
	public static final String BICONDITIONAL_SHORTHAND = "<->";
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
			if ( op.getSymbolName().equals( str ) || op.toString().equals( str ) ) {
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
			return str.equals( NOT_SHORTHAND );
		}
		else if ( name.equals( "EQUALS" ) ) {
			return str.equals( EQUALS_SHORTHAND );
		}
		else if ( name.equals( "AND" ) ) {
			return str.equals( AND_SHORTHAND );
		}
		else if ( name.equals( "OR" ) ) {
			return str.equals( OR_SHORTHAND );
		}
		else if ( name.equals( "IMPLICATION" ) ) {
			return str.equals( IMPLICATION_SHORTHAND );
		}
		else if ( name.equals( "BICONDITIONAL" ) ) {
			return str.equals( BICONDITIONAL_SHORTHAND );
		}
		return false;
	}
	
	@Override
	public String getShorthand() {
		if ( name.equals( "NOT" ) ) {
			return NOT_SHORTHAND;
		}
		else if ( name.equals( "EQUALS" ) ) {
			return EQUALS_SHORTHAND;
		}
		else if ( name.equals( "AND" ) ) {
			return AND_SHORTHAND;
		}
		else if ( name.equals( "OR" ) ) {
			return OR_SHORTHAND;
		}
		else if ( name.equals( "IMPLICATION" ) ) {
			return IMPLICATION_SHORTHAND;
		}
		else if ( name.equals( "BICONDITIONAL" ) ) {
			return BICONDITIONAL_SHORTHAND;
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
