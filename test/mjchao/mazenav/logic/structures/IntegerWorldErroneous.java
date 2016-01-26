package mjchao.mazenav.logic.structures;

/**
 * An "Integer World" that has been erroneous constructed.
 * Used for checking error-detection abilities 
 * 
 * @author mjchao
 *
 */
public class IntegerWorldErroneous {
	
	public ObjectFOL changingInt = NumbersFOL.fromInt( 5 );
	
	public IntegerWorldErroneous() {
		
	}

	public ObjectFOL SumInt( ObjectFOL arg1 , ObjectFOL arg2 ) {
		int augend = ((Integer) arg1.getValue()).intValue();
		int addend = ((Integer) arg2.getValue()).intValue();
		return NumbersFOL.fromInt( augend + addend );
	}
	
	/**
	 * Overloaded function used for testing purposes (this function is
	 * never used in test cases intended to pass without throwing
	 * an exception)
	 */
	public ObjectFOL SumInt( ObjectFOL arg1 , ObjectFOL arg2 , ObjectFOL arg3 ) {
		throw new RuntimeException( "This function should not have executed." );
	}
	
	public ObjectFOL DiffInt( ObjectFOL arg1 , ObjectFOL arg2 ) {
		int augend = ((Integer) arg1.getValue()).intValue();
		int addend = ((Integer) arg2.getValue()).intValue();
		return NumbersFOL.fromInt( augend - addend );
	}
	
	public ObjectFOL SumIntEnvir( ObjectFOL arg2 ) {
		return SumInt( this.changingInt , arg2 );
	}
	
	public BooleanFOL GreaterThan( ObjectFOL arg1 , ObjectFOL arg2 ) {
		int cmp1 = ((Integer) arg1.getValue()).intValue();
		int cmp2 = ((Integer) arg2.getValue()).intValue();
		boolean result = (cmp1 > cmp2);
		return BooleanFOL.fromBoolean( result );
	}
	
	public ObjectFOL NotARelation( ObjectFOL arg1 ) {
		return NumbersFOL.fromInt( 5 );
	}
	
	
}
