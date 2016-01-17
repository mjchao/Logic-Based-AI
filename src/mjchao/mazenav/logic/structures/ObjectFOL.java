package mjchao.mazenav.logic.structures;

import java.util.ArrayList;

/**
 * Represents in object in first order logic (FOL)
 * 
 * @author mjchao
 *
 */
public class ObjectFOL extends Symbol {
	
	private ArrayList< String > types = new ArrayList< String >();
	private Object value;
	
	/**
	 * Creates a FOL object.
	 *  
	 * @param name			the name of the object, as referred to in logic statements
	 * @param value			an actual object that represents the value of this FOL object
	 * @param types			the types under which this object may be classified, e.g. this
	 * 						object is an Integer and a Real number
	 */
	public ObjectFOL( String name , Object value , String... types ) {
		super( name );
		this.value = value;
		
		for ( String type : types ) {
			this.types.add( type );
		}
	}
	
	public boolean isOfType( String type ) {
		return types.contains( type );
	}
}
