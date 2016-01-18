package mjchao.mazenav.logic.structures;

import java.lang.reflect.InvocationTargetException;

/**
 * Represents a relation in first-order logic (FOL). A relation is a function
 * that returns True or False.
 * 
 * @author mjchao
 *
 */
public class Relation extends Function {

	public Relation( String name , Object definitionClassInstance , String... argTypes ) {
		super( name , definitionClassInstance , argTypes );
	}
	
	@Override
	public BooleanFOL operate( ObjectFOL... args ) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		ObjectFOL functionResult = super.operate( args );
		if ( functionResult instanceof BooleanFOL ) {
			return (BooleanFOL) functionResult;
		}
		else {
			throw new IllegalStateException( "Relation " + name + " returned non-boolean result:\n" + functionResult.toString() );
		}
	}
	
}
