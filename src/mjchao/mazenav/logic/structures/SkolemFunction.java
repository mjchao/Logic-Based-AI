package mjchao.mazenav.logic.structures;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Represents a Skolem Function to be used when
 * converting to conjunctive normal form
 * 
 * @author mjchao
 *
 */
public class SkolemFunction extends Symbol {

	/**
	 * Stores all the values with which this
	 * skolem function has been unified. Each different
	 * setting of this function's parameters allows for
	 * a different unification.  
	 */
	private HashMap< List<ObjectFOL> , ObjectFOL > unifications = new HashMap< List<ObjectFOL> , ObjectFOL >();
	
	/**
	 * The arguments to this skolem function
	 */
	private Variable[] args;
	
	/**
	 * Creates a SkolemFunction with the given id
	 * and arguments.
	 * 
	 * @param id		a unique ID to be assigned to this skolem
	 * 					function (for printing purposes)
	 * @param args		arguments to this SkolemFunction
	 */
	public SkolemFunction( int id , Variable[] args ) {
		super( "$" + id );
		this.args = args;
	}
	
	/**
	 * Creates a SkolemFunction with the given 
	 * arguments. The ID is set to 0. Use this
	 * constructor if you do not care about
	 * printing this function for debugging.
	 * 
	 * @param args		arguments to this SkolemFunction
	 */
	public SkolemFunction( Variable[] args ) {
		this( 0 , args );
	}
	
	/**
	 * Unifies this SkolemFunction with the given
	 * value. This assumes the variable parameters
	 * have already been unified appropriately
	 * 
	 * @param val
	 */
	public void unify( ObjectFOL val ) {
		ArrayList< ObjectFOL > parameterSettings = new ArrayList< ObjectFOL >();
		for ( Variable v : args ) {
			if ( v.getValue() == null ) {
				throw new IllegalStateException( "Cannot unify Skolem Function before unifying parameter." );
			}
			parameterSettings.add( v.getValue() );
		}
		this.unifications.put( parameterSettings , val );
	}
}
