package mjchao.mazenav.logic.structures;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.junit.Assert;
import org.junit.Test;

public class SymbolTrackerTest {

	@Test
	public void tokenize() throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		String input;
		String[] expected;
		Method tokenize = SymbolTracker.class.getDeclaredMethod( "tokenize" , String.class );
		tokenize.setAccessible( true );
		
		//Basic acceptance test
		input = "FUNCTION: SumInt( Integer , Integer )";
		expected = new String[]{ "FUNCTION" , "SumInt" , "Integer" , "Integer" };
		Assert.assertArrayEquals( (String[])tokenize.invoke( null ,  input ) , expected );
		
		//test extraneous spaces
		input = "      FUNCTION:    SumInt(   Integer ,    Integer )      ";
		expected = new String[]{ "FUNCTION" , "SumInt" , "Integer" , "Integer" };
		Assert.assertArrayEquals( (String[])tokenize.invoke( null , input ) , expected );
		
		//test extra commas
		input = ":::FUNCTION:		SumInt( Integer ,, Integer )    ";
		expected = new String[]{ "FUNCTION" , "SumInt" , "Integer" , "" , "Integer" };
		Assert.assertArrayEquals( (String[])tokenize.invoke( null , input ) , expected );
	}
	
	@Test 
	public void fromFile() throws IOException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		FunctionDefinitions def = new FunctionDefinitions();
		SymbolTracker test = SymbolTracker.fromDataFile( "test/mjchao/mazenav/logic/structures/integerworld.txt" , def );
		
		ObjectFOL five = ObjectFOL.fromInt( 5 );
		ObjectFOL six = ObjectFOL.fromInt( 6 );
		
		Function SumInt = test.getFunction( "SumInt" );
		ObjectFOL eleven = SumInt.operate( five , six );
		Assert.assertTrue( ( eleven.toString().equals( "11" ) ) );
		
		Relation GreaterThan = test.getRelation( "GreaterThan" );
		BooleanFOL True = GreaterThan.operate( six , five );
		Assert.assertTrue( True.getValue().booleanValue() == true );
	}
}
