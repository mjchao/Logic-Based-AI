package mjchao.mazenav.logic.structures;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Represents a Function in first order logic (FOL). The functions in 
 * this FOL package must be defined externally in another class called
 * a "Definition Class." For every desired FOL function, there must
 * be a definition class that defines a method with the same name as
 * the FOL function. The FOL function then takes an instance of the 
 * definition class and whenever the FOL function is invoked, the FOL
 * function invokes the definition class's method with the same name.
 * This allows the logic package to abstract out function definitions
 * and be reused.
 * 
 * @author mjchao
 *
 */
public class Function extends Symbol {
	
	/**
	 * an instance of the class that contains this function's 
	 * definition. We abstract out the function's definition
	 * into an external class so that the logic package
	 * can be reused.
	 */
	private Object definitionClassInstance;
	
	/**
	 * An array of repeated ObjectFOL.class. All FOL functions
	 * take ObjectFOL as their sole arguments.
	 */
	private Class<?>[] parameterTypes;
	
	/**
	 * The types of the FOL objects that this function takes
	 */
	private ArrayList< String > argTypes = new ArrayList< String >();
	
	public Function( String name , Object definitionClassInstance , String... argTypes ) {
		super( name );
		this.definitionClassInstance = definitionClassInstance;
		
		for ( String type : argTypes ) {
			this.argTypes.add( type );
		}
		
		parameterTypes = new Class<?>[ argTypes.length ];
		Arrays.fill( parameterTypes , ObjectFOL.class );
		
		//check that this method is defined in the given class.
		//this way, we catch the bug as soon as it is created.
		try {
			definitionClassInstance.getClass().getMethod( name , parameterTypes );
		}
		catch ( NoSuchMethodException e ) {
			throw new IllegalArgumentException( "Function definition class has not defined a function \"" + 
											name + "\" that takes " + argTypes.length + 
											" arguments of type ObjectFOL" );
		}
	}
	
	/**
	 * Invokes this function on some FOL objects 
	 * 
	 * @param args							a list of FOL objects, which are the arguments to this function
	 * @return								whatever the FOL function is defined to return
	 * @throws NoSuchMethodException		should not be thrown - the constructor checks that
	 * 										the function definition exists
	 * @throws IllegalAccessException		if this function cannot be accessed due to some language restrictions
	 * @throws IllegalArgumentException		should not be thrown - the constructor checks that the
	 * 										function definition class contains the function definition 
	 * @throws InvocationTargetException	if this function throws an exception while executing
	 */
	public ObjectFOL operate( ObjectFOL... args ) throws NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		
		//check that arguments types are what we expect
		for ( int i=0 ; i<args.length ; ++i ) {
			if ( !args[ i ].isOfType( argTypes.get( i ) ) ) {
				throw new IllegalArgumentException( "Argument type mistmatch: \n" + 
							args[ i ].toString() + "\n" +
							"is not of type " + argTypes.get( i ) );
			}
		}
		
		Method toExecute = definitionClassInstance.getClass().getMethod( name , parameterTypes );
		return (ObjectFOL) toExecute.invoke( definitionClassInstance , (Object[]) args );
	}
	
}
