package mjchao.mazenav.logic.structures;

/**
 * Provides convenient functions for working with
 * numbers in First-Order Logic
 * @author mjchao
 *
 */
public class NumbersFOL {
	
	public static final String INTEGER_TYPE = "Integer";
	public static final String REAL_TYPE = "Real";
	
	public static ObjectFOL fromInt( int val ) {
		return new ObjectFOL( String.valueOf( val ) , Integer.valueOf( val ) , INTEGER_TYPE , REAL_TYPE );
	}
	
	public static ObjectFOL convertInteger( ObjectFOL value ) {
		if ( value.getValue() instanceof Integer ) {
			
			//already done
			return value;
		}
		else {
			
			//otherwise, it's a double
			return fromInt( ((Double) value.getValue()).intValue() );
		}
	}
	
	public static ObjectFOL fromDouble( double val ) {
		return new ObjectFOL( String.valueOf( val ) , Double.valueOf( val ) , REAL_TYPE );
	}
	
	/**
	 * Parses a Real value from a value that may be represented
	 * as an Integer or a Double. If we represent an integer value
	 * with an Integer object, we cannot just cast it to Double,
	 * so we must apply this function.
	 * 
	 * @param value		a Real or Integer value
	 * @return			the value converted to a Real value represented
	 * 					by a Double.
	 */
	public static ObjectFOL convertReal( ObjectFOL value ) {
		if ( value.getValue() instanceof Integer ) {
			return fromDouble( ((Integer) value.getValue()).doubleValue() );
		}
		else {
			
			//if it's not an integer type, then it must be a Double type
			return value;
		}
	}
	
	/**
	 * Parses a double from an FOL Object if possible.
	 * 
	 * @param value
	 * @return
	 */
	public static Double parseDouble( ObjectFOL value ) {
		if ( value.getValue() instanceof Integer ) {
			return Double.valueOf( ((Integer) value.getValue()).intValue() );
		}
		else if ( value.getValue() instanceof Double ) {
			return (Double) value.getValue();
		}
		else {
			throw new IllegalArgumentException( "Argument is not an Integer or Real" );
		}
	}
	
	public static ObjectFOL fromFloat( float val ) {
		//we only use integer types and double precision
		//there is no need for optimization yet for floats
		return fromDouble( val );
	}
}
