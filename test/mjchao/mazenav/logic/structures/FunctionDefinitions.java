package mjchao.mazenav.logic.structures;

public class FunctionDefinitions {
	
	public ObjectFOL changingInt = ObjectFOL.fromInt( 5 );
	
	public FunctionDefinitions() {
		
	}

	public ObjectFOL SumInt( ObjectFOL arg1 , ObjectFOL arg2 ) {
		int augend = ((Integer) arg1.getValue()).intValue();
		int addend = ((Integer) arg2.getValue()).intValue();
		return ObjectFOL.fromInt( augend + addend );
	}
	
	public ObjectFOL DiffInt( ObjectFOL arg1 , ObjectFOL arg2 ) {
		int augend = ((Integer) arg1.getValue()).intValue();
		int addend = ((Integer) arg2.getValue()).intValue();
		return ObjectFOL.fromInt( augend - addend );
	}
	
	public ObjectFOL SumIntEnvir( ObjectFOL arg2 ) {
		return SumInt( this.changingInt , arg2 );
	}
}
