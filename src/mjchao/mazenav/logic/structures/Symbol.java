package mjchao.mazenav.logic.structures;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

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
	public static final Symbol SUCH_THAT = new Symbol( "S.T." );
	public static final Symbol[] SYMBOL_LIST = new Symbol[] { LEFT_PAREN , RIGHT_PAREN , COMMA , SUCH_THAT };

	/**
	 * Stores a list of reserved keywords in order of decreasing
	 * length so that we can appropriate parse statements. Note that
	 * symbols like != and ! overlap, and we need to be sure not to
	 * parse != as ! and =. 
	 */
	private static List<String> RESERVED_STRINGS = null;
	
	/**
	 * Gets a list of reserved keywords in order of decreasing
	 * length so that we can appropriate parse statements. Note that
	 * symbols like != and ! overlap, and we need to be sure not to
	 * parse != as ! and =. Thus, we process reserved keywords by
	 * decreasing length to avoid this mixup.
	 *  
	 * @return
	 */
	public static final List<String> GET_RESERVED_SYMBOLS() {
		if ( RESERVED_STRINGS == null ) {
			RESERVED_STRINGS = new ArrayList< String >();
			Symbol[] completeSymbolList = Utils.join( Symbol.class , SYMBOL_LIST , Operator.OPERATOR_LIST , Quantifier.QUANTIFIER_LIST );
			for ( Symbol s : completeSymbolList ) {
				RESERVED_STRINGS.add( s.getSymbolName() );
				if ( !s.getSymbolName().equals( s.getShorthand() ) ) {
					RESERVED_STRINGS.add( s.getShorthand() );
				}
			}
			Collections.sort( RESERVED_STRINGS , new Comparator<String>() {

				@Override
				public int compare(String s1, String s2) {
					if ( s1.length() > s2.length() ) {
						
						//put longer reserved strings first
						//so that we can properly deal with
						//overlaps
						return -1;
					}
					else if ( s2.length() > s1.length() ) {
						return 1;
					}
					else {
						return 0;
					}
				}
				
			});
		}
		return RESERVED_STRINGS;
	}
	
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
	
	/**
	 * @return		the full name of this symbol. this should be
	 * 				unambiguous and complete, as opposed to
	 * 				the <code>getShorthand()</code> method.
	 */
	public String getSymbolName() {
		return name;
	}
	
	/**
	 * @return		a shorthand representation of this symbol
	 * 				that can be used for convenient printing
	 * 				purposes.
	 */
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
	public int hashCode() {
		return this.name.hashCode();
	}
	
	@Override
	public String toString() {
		return getShorthand();
	}
}
