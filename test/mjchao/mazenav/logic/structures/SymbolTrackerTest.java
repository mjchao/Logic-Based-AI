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
		IntegerWorld def = new IntegerWorld();
		SymbolTracker test = SymbolTracker.fromDataFile( "test/mjchao/mazenav/logic/structures/integerworld.txt" , def );
		
		ObjectFOL five = NumbersFOL.fromInt( 5 );
		ObjectFOL six = NumbersFOL.fromInt( 6 );
		
		Function SumInt = test.getFunction( "SumInt" );
		ObjectFOL eleven = SumInt.operate( five , six );
		Assert.assertTrue( ( eleven.toString().equals( "11" ) ) );
		
		Relation GreaterThan = test.getRelation( "GreaterThan" );
		BooleanFOL True = GreaterThan.operate( six , five );
		Assert.assertTrue( True.getValue().booleanValue() == true );
	}
	
	@Test(expected=IllegalStateException.class)
	public void redefinition() throws IOException {
		//this test case defines two functions to have the same name
		IntegerWorldErrorRedefinition def = new IntegerWorldErrorRedefinition();
		SymbolTracker test = SymbolTracker.fromDataFile( "test/mjchao/mazenav/logic/structures/integerworld_error_redefinition.txt" , def );
	}
	
	@Test(expected=IllegalStateException.class)
	public void redefinition2() throws IOException {
		//this test case defines a function and a constant to have the same name
		IntegerWorldErrorRedefinition2 def = new IntegerWorldErrorRedefinition2();
		SymbolTracker test = SymbolTracker.fromDataFile( "test/mjchao/mazenav/logic/structures/integerworld_error_redefinition2.txt" , def );
	}
	
	@Test
	public void fromGeometryWorldFile() throws IOException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		GeometryWorld def = new GeometryWorld();
		SymbolTracker test = SymbolTracker.fromDataFile( "test/mjchao/mazenav/logic/structures/geometryworld.txt", def );
		
		Function Angle = test.getFunction( "Angle" );
		ObjectFOL angle1 = Angle.operate( NumbersFOL.fromDouble( 25.7) );
		ObjectFOL angle2 = Angle.operate( NumbersFOL.fromDouble( 25.7 ) );
		ObjectFOL angle3 = Angle.operate( NumbersFOL.fromDouble( 50.5 ) );
		ObjectFOL rightAngle = Angle.operate( NumbersFOL.fromInt( 90 ) );
		
		Function AngleEquals = test.getRelation( "AngleEquals" );
		Assert.assertTrue( AngleEquals.operate( angle1 , angle2 ).equals( BooleanFOL.True() ) );
		Assert.assertTrue( AngleEquals.operate( angle1 , angle3 ).equals( BooleanFOL.False() ) );
		
		ObjectFOL rightAngleConstant = test.getConstant( "RightAngle" );
		Assert.assertTrue( AngleEquals.operate( rightAngle , rightAngleConstant ).equals( BooleanFOL.True() ) );
		
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void undefinedConstant() throws IOException {
		IntegerWorldErrorUndefinedConstant def = new IntegerWorldErrorUndefinedConstant();
		SymbolTracker test = SymbolTracker.fromDataFile( "test/mjchao/mazenav/logic/structures/integerworld_error_undefined_constant.txt" , def );
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void constantMissingType() throws IOException {
		IntegerWorldErrorConstantMissingType def = new IntegerWorldErrorConstantMissingType();
		SymbolTracker test = SymbolTracker.fromDataFile( "test/mjchao/mazenav/logic/structures/integerworld_error_constant_missing_type.txt" , def );		
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void constantDefineWithArguments() throws IOException {
		IntegerWorldErrorConstantDefinedWithArguments def = new IntegerWorldErrorConstantDefinedWithArguments();
		SymbolTracker test = SymbolTracker.fromDataFile( "test/mjchao/mazenav/logic/structures/integerworld_error_constant_defined_with_arguments.txt" , def );		
	}
}
