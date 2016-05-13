package mjchao.mazenav.logic.structures;

/**
 * Represents a variable in a logic expression.
 * Variables can be unified and set to point to
 * another variable or a ground literal or constant.
 * 
 * @author mjchao
 *
 */
public class Variable extends Symbol {

	public static boolean isValidVariableName( String name ) {
		if ( name.length() == 0 ) {
			return false;
		}
		
		//name must start with a letter or underscore but not a number
		//(numbers are reserved for system-defined variables to be used
		//in unification)
		if ( Character.isLetter( name.charAt( 0 ) ) || (name.charAt( 0 ) == '_') ) {
			//okay
		}
		else {
			return false;
		}
		
		for ( char c : name.toCharArray() ) {
			//allow alphanumeric, and underscores
			if ( Character.isLetterOrDigit( c ) || c == '_' ) {
				//okay
			}
			else {
				return false;
			}
		}
		return true;
	}
	
	private long id;
	
	/**
	 * determines if the user explicitly applied a universal quantifier
	 * to this variable
	 */
	private boolean universallyQuantified = false;
	
	/**
	 * Creates a new variable. The unique id
	 * allows us to distinguish between
	 * different variables.
	 * 
	 * @param id
	 */
	public Variable( long id ) {
		super( "Variable" + id );
		this.id = id;
	}
	
	public Variable( String name , int id ) {
		super( name );
		this.id = id;
	}
	
	public long getId() {
		return this.id;
	}
	
	public void setUniversallyQuantified( boolean b ) {
		this.universallyQuantified = b;
	}
	
	public boolean universallyQuantified() {
		return this.universallyQuantified;
	}
	
	@Override
	public boolean equals( Object o ) {
		if ( o instanceof Variable ) {
			return this.name.equals( ((Variable) o).name );
		}
		return false;
	}
}
