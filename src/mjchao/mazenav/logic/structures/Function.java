package mjchao.mazenav.logic.structures;

/**
 * Represents a Function in first-order logic (FOL).
 * 
 * @author mjchao
 *
 */
public class Function extends Symbol {
	
	int numArgs = 0;
	
	/**
	 * Creates a function with the given name
	 * 
	 * @param name						the name of this function as it is to
	 * 									appear in FOL expressions
	 */
	public Function( String name ) {
		super( name );
	}
	
	public void setNumArgs( int numArgs ) {
		this.numArgs = numArgs;
	}
	
	public int getNumArgs() {
		return this.numArgs;
	}
}
