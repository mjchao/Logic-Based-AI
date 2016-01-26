package mjchao.mazenav.logic.structures;

public class IntegerWorldErrorUndefinedConstant {
	
	public ObjectFOL changingInt = NumbersFOL.fromInt( 5 );
	
	public IntegerWorldErrorUndefinedConstant() {
		
	}

	public ObjectFOL SumInt( ObjectFOL arg1 , ObjectFOL arg2 ) {
		int augend = ((Integer) arg1.getValue()).intValue();
		int addend = ((Integer) arg2.getValue()).intValue();
		return NumbersFOL.fromInt( augend + addend );
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
