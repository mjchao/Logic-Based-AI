package mjchao.mazenav.logic.structures;

import java.lang.reflect.InvocationTargetException;

import org.junit.Assert;
import org.junit.Test;

public class FunctionTest {

	@Test
	public void basicAcceptance() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		IntegerWorld definitionClassInstance = new IntegerWorld();
		Function addFunction = new Function( "SumInt" , definitionClassInstance , "Integer" , "Integer" );
		Assert.assertTrue( addFunction.operate( NumbersFOL.fromInt( 2 ) , NumbersFOL.fromInt( 3 ) ).toString().equals("5") );
		
		Function diffFunction = new Function( "DiffInt" , definitionClassInstance , "Integer" , "Integer" );
		Assert.assertTrue( diffFunction.operate( NumbersFOL.fromInt( 10 ) , NumbersFOL.fromInt( 3 ) ).toString().equals( "7" ) );
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void nonexistentFunction() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		IntegerWorld definitionClassInstance = new IntegerWorld();
		Function addFunction = new Function( "NonexistentFunction" , definitionClassInstance , "Integer" , "Integer" );
		addFunction.operate( NumbersFOL.fromInt( 2 ) , NumbersFOL.fromInt( 3 ) );
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void tooFewFunctionArguments() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		IntegerWorld definitionClassInstance = new IntegerWorld();
		Function addFunction = new Function( "SumInt" , definitionClassInstance , "Integer" , "Integer" );
		addFunction.operate( NumbersFOL.fromInt( 2 ) , NumbersFOL.fromInt( 3 ) , NumbersFOL.fromInt( 4 ) );
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void tooManyFunctionArguments() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		IntegerWorld definitionClassInstance = new IntegerWorld();
		Function addFunction = new Function( "SumInt" , definitionClassInstance , "Integer" , "Integer" );
		addFunction.operate( NumbersFOL.fromInt( 2 ) );
	}
	
	@Test
	public void changingEnvironment() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		IntegerWorld definitionClassInstance = new IntegerWorld();
		definitionClassInstance.changingInt = NumbersFOL.fromInt( 0 );
		
		Function addIntEnvir = new Function( "SumIntEnvir" , definitionClassInstance , "Integer" );
		Assert.assertTrue( addIntEnvir.operate( NumbersFOL.fromInt( 100 ) ).toString().equals( "100" ) );
		
		definitionClassInstance.changingInt = NumbersFOL.fromInt( -20 );
		Assert.assertTrue( addIntEnvir.operate( NumbersFOL.fromInt( 100 ) ).toString().equals( "80" ) );	
	}
	
	@Test
	public void testRelations() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		IntegerWorld definitionClassInstance = new IntegerWorld();
		Relation ge = new Relation( "GreaterThan" , definitionClassInstance , "Integer" , "Integer" );
		Assert.assertTrue( ge.operate( NumbersFOL.fromInt( 50 ) , NumbersFOL.fromInt( 100 ) ).toString().equals( "False" ) );
		Assert.assertTrue( ge.operate( NumbersFOL.fromInt( 100 ) , NumbersFOL.fromInt( 50 ) ).toString().equals( "True" ) );
	}
	
	@Test(expected=IllegalStateException.class)
	public void testNotARelation() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		IntegerWorld definitionClassInstance = new IntegerWorld();
		Relation notARelation = new Relation( "NotARelation" , definitionClassInstance , "Integer" );
		notARelation.operate( NumbersFOL.fromInt( 5 ) );
	}
}
