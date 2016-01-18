package mjchao.mazenav.logic.structures;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.junit.Assert;
import org.junit.Test;

public class SymbolTrackerTest {

	@Test
	public void testTokenize() throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
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
}
