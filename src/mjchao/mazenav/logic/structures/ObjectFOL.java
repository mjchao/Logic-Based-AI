package mjchao.mazenav.logic.structures;


/**
 * Represents in object in first-order logic (FOL)
 * 
 * @author mjchao
 *
 */
public class ObjectFOL extends Symbol {
	
	/**
	 * Creates a FOL object.
	 *  
	 * @param name			the name of the object, as referred to in logic statements
	 * @param value			an actual object that represents the value of this FOL object
	 * @param types			the types under which this object may be classified, e.g. this
	 * 						object is an Integer and a Real number
	 */
	public ObjectFOL( String name ) {
		super( name );
	}
}
