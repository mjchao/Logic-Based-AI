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

	private ObjectFOL unification = null;
	
	private int id;
	
	/**
	 * Creates a new variable. The unique id
	 * allows us to distinguish between
	 * different variables.
	 * 
	 * @param id
	 */
	public Variable( int id ) {
		super( "Variable" + id );
		this.id = id;
	}
	
	public Variable( String name , int id ) {
		super( name );
		this.id = id;
	}
	
	public void unify( ObjectFOL obj ) {
		this.unification = obj;
	}
	
	public ObjectFOL getValue() {
		return this.unification;
	}
	
	public int getId() {
		return this.id;
	}
}
