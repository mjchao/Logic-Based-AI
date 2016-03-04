package mjchao.mazenav.logic.structures;

/**
 * Represents an operator in first-order logic (FOL). The currently-supported
 * operators are as follows (in order of decreasing precedence):
 * <ul>
 * 	<li> ! (NOT)
 *	<li> == (EQUALS)
 *  <li> != (NOT_EQUALS)
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
	public static final int NOT_PRECEDENCE = 5;
	public static final Operator NOT = new Operator( "NOT" , NOT_PRECEDENCE );
	
	public static final String EQUALS_SHORTHAND = "==";
	public static final int EQUALS_PRECEDENCE = 4;
	public static final Operator EQUALS = new Operator( "EQUALS" , EQUALS_PRECEDENCE );
	
	public static final String NOT_EQUALS_SHORTHAND = "!=";
	public static final int NOT_EQUALS_PRECEDENCE = 4;
	public static final Operator NOT_EQUALS = new Operator( "NEQUALS" , NOT_EQUALS_PRECEDENCE );
	
	public static final String AND_SHORTHAND = "&&";
	public static final int AND_PRECEDENCE = 3;
	public static final Operator AND = new Operator( "AND" , AND_PRECEDENCE );
	
	public static final String OR_SHORTHAND = "||";
	public static final int OR_PRECEDENCE = 2;
	public static final Operator OR = new Operator( "OR" , OR_PRECEDENCE );
	
	public static final String IMPLICATION_SHORTHAND = "=>";
	public static final int IMPLICATION_PRECEDENCE = 1;
	public static final Operator IMPLICATION = new Operator( "IMPLICATION" , IMPLICATION_PRECEDENCE );
	
	public static final String BICONDITIONAL_SHORTHAND = "<=>";
	public static final int BICONDITIONAL_PRECEDENCE = 0;
	public static final Operator BICONDITIONAL = new Operator( "BICONDITIONAL" , BICONDITIONAL_PRECEDENCE );
	
	public static final Operator[] OPERATOR_LIST = new Operator[] { 
			BICONDITIONAL , IMPLICATION , OR , AND , NOT_EQUALS , EQUALS , NOT 
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
			if ( op.getSymbolName().equals( str ) || op.getShorthand().equals( str ) ) {
				return op;
			}
		}
		
		//not an operator
		return null;
	}
	
	private final int precedence;
	
	private Operator( String name , int precedence ) {
		super( name );
		this.precedence = precedence;
	}
	
	public int getPrecedence() {
		return this.precedence;
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
		else if ( name.equals( "NEQUALS" ) ) {
			return str.equals( NOT_EQUALS_SHORTHAND );
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
		else if ( name.equals( "NEQUALS" ) ) {
			return NOT_EQUALS_SHORTHAND;
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
