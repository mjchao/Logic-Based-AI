package mjchao.mazenav.logic.structures;

import java.lang.reflect.InvocationTargetException;

import org.junit.Assert;
import org.junit.Test;

public class FunctionTest {

	@Test
	public void basicAcceptance() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		FunctionDefinitions definitionClassInstance = new FunctionDefinitions();
		Function addFunction = new Function( "SumInt" , definitionClassInstance , "Integer" , "Integer" );
		Assert.assertTrue( addFunction.operate( ObjectFOL.fromInt( 2 ) , ObjectFOL.fromInt( 3 ) ).toString().equals("5") );
		
		Function diffFunction = new Function( "DiffInt" , definitionClassInstance , "Integer" , "Integer" );
		Assert.assertTrue( diffFunction.operate( ObjectFOL.fromInt( 10 ) , ObjectFOL.fromInt( 3 ) ).toString().equals( "7" ) );
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void nonexistentFunction() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		FunctionDefinitions definitionClassInstance = new FunctionDefinitions();
		Function addFunction = new Function( "NonexistentFunction" , definitionClassInstance , "Integer" , "Integer" );
		addFunction.operate( ObjectFOL.fromInt( 2 ) , ObjectFOL.fromInt( 3 ) );
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void tooFewFunctionArguments() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		FunctionDefinitions definitionClassInstance = new FunctionDefinitions();
		Function addFunction = new Function( "SumInt" , definitionClassInstance , "Integer" , "Integer" );
		addFunction.operate( ObjectFOL.fromInt( 2 ) , ObjectFOL.fromInt( 3 ) , ObjectFOL.fromInt( 4 ) );
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void tooManyFunctionArguments() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		FunctionDefinitions definitionClassInstance = new FunctionDefinitions();
		Function addFunction = new Function( "SumInt" , definitionClassInstance , "Integer" , "Integer" );
		addFunction.operate( ObjectFOL.fromInt( 2 ) );
	}
	
	@Test
	public void changingEnvironment() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		FunctionDefinitions definitionClassInstance = new FunctionDefinitions();
		definitionClassInstance.changingInt = ObjectFOL.fromInt( 0 );
		
		Function addIntEnvir = new Function( "SumIntEnvir" , definitionClassInstance , "Integer" );
		Assert.assertTrue( addIntEnvir.operate( ObjectFOL.fromInt( 100 ) ).toString().equals( "100" ) );
		
		definitionClassInstance.changingInt = ObjectFOL.fromInt( -20 );
		Assert.assertTrue( addIntEnvir.operate( ObjectFOL.fromInt( 100 ) ).toString().equals( "80" ) );	
	}
	
	@Test
	public void testRelations() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		FunctionDefinitions definitionClassInstance = new FunctionDefinitions();
		Relation ge = new Relation( "GreaterThan" , definitionClassInstance , "Integer" , "Integer" );
		Assert.assertTrue( ge.operate( ObjectFOL.fromInt( 50 ) , ObjectFOL.fromInt( 100 ) ).toString().equals( "False" ) );
		Assert.assertTrue( ge.operate( ObjectFOL.fromInt( 100 ) , ObjectFOL.fromInt( 50 ) ).toString().equals( "True" ) );
	}
	
	@Test(expected=IllegalStateException.class)
	public void testNotARelation() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		FunctionDefinitions definitionClassInstance = new FunctionDefinitions();
		Relation notARelation = new Relation( "NotARelation" , definitionClassInstance , "Integer" );
		notARelation.operate( ObjectFOL.fromInt( 5 ) );
	}
}
