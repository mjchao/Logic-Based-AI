package mjchao.mazenav.logic.structures;

import java.util.ArrayList;

/**
 * Represents in object in first order logic (FOL)
 * 
 * @author mjchao
 *
 */
public class ObjectFOL extends Symbol {
	
	public static ObjectFOL fromInt( int val ) {
		return new ObjectFOL( String.valueOf( val ) , Integer.valueOf( val ) , "Integer" , "Number" );
	}
	
	public static ObjectFOL fromDouble( double val ) {
		return new ObjectFOL( String.valueOf( val ) , Double.valueOf( val ) , "Number" );
	}
	
	public static ObjectFOL fromFloat( float val ) {
		//we only use integer types and double precision
		//there is no need for optimization yet
		return fromDouble( val );
	}
	
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
	
	public Object getValue() {
		return value;
	}
	
	public boolean isOfType( String type ) {
		return types.contains( type );
	}
}
