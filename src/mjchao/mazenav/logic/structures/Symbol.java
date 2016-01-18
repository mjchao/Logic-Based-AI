package mjchao.mazenav.logic.structures;

import mjchao.mazenav.util.Utils;

/**
 * A symbol to be used in a logic expression.
 * 
 * @author mjchao
 *
 */
public class Symbol {
	
	public static final Symbol LEFT_PAREN = new Symbol( "(" );
	public static final Symbol RIGHT_PAREN = new Symbol( ")" );
	public static final Symbol COMMA = new Symbol( "," );
	public static final Symbol[] SYMBOL_LIST = new Symbol[] { LEFT_PAREN , RIGHT_PAREN , COMMA };
	public static Symbol[] RESERVED = Utils.join( Symbol.class , Symbol.SYMBOL_LIST , Operator.OPERATOR_LIST );

	
	protected String name;
	
	public Symbol( String name ) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	@Override
	public String toString() {
		return this.name;
	}
}
