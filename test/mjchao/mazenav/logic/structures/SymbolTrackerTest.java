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
		SymbolTracker test = SymbolTracker.fromDataFile( "test/mjchao/mazenav/logic/structures/integerworld.txt" );
		Assert.assertTrue( test.parseFunction( "SumInt" ) != null );
		Assert.assertTrue( test.parseFunction( "DiffInt" ) != null );
		Assert.assertTrue( test.parseFunction( "GreaterThan" ) != null );
		Assert.assertTrue( test.parseConstant( "One" ) != null );
		Assert.assertTrue( test.parseConstant( "Two" ) != null );
		Assert.assertTrue( test.parseConstant( "Three" ) != null );
		Assert.assertTrue( test.parseConstant( "Four" ) != null );
		Assert.assertTrue( test.parseConstant( "Five" ) != null );
		
		//some non-defined constants and functions
		Assert.assertTrue( test.parseConstant( "Six" ) == null );
		Assert.assertTrue( test.parseConstant( "Seven" ) == null );
		Assert.assertTrue( test.parseConstant( "Eight" ) == null );
		Assert.assertTrue( test.parseFunction( "MultInt" ) == null );
		Assert.assertTrue( test.parseFunction( "DivInt" ) == null );
	}
	
	@Test
	public void fromGeometryWorldFile() throws IOException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		SymbolTracker test = SymbolTracker.fromDataFile( "test/mjchao/mazenav/logic/structures/geometryworld.txt" );
		Assert.assertTrue( test.parseFunction( "Point" ) != null );
		Assert.assertTrue( test.parseFunction( "LineSegment" ) != null );
		Assert.assertTrue( test.parseFunction( "Line" ) != null );
		Assert.assertTrue( test.parseFunction( "Circle" ) != null );
		Assert.assertTrue( test.parseFunction( "Angle" ) != null );
		Assert.assertTrue( test.parseFunction( "AngleEquals" ) != null );
		Assert.assertTrue( test.parseConstant( "RightAngle" ) != null );
		Assert.assertTrue( test.parseConstant( "AcuteAngle" ) == null );
		Assert.assertTrue( test.parseConstant( "ObtuseAngle" ) == null );
	}
}
