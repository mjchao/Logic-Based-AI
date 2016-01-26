package mjchao.mazenav.logic.structures;

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

	public static Symbol parseSymbol( String str ) {
		for ( Symbol s : SYMBOL_LIST ) {
			if ( s.getSymbolName().equals( str ) ) {
				return s;
			}
		}
		
		//not a defined symbol
		return null;
	}
	
	protected String name;
	
	public Symbol( String name ) {
		this.name = name;
	}
	
	public String getSymbolName() {
		return name;
	}
	
	public String getShorthand() {
		
		//by default, the shorthand will be the same as the name.
		//some symbols may have a shorthand representation
		//e.g. for AND it is &&
		return name;
	}
	
	@Override
	public boolean equals( Object o ) {
		if ( o instanceof Symbol ) {
			return this.name.equals( ((Symbol) o).name );
		}
		return false;
	}
	
	@Override
	public String toString() {
		return this.name;
	}
}
