package mjchao.mazenav.logic;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import mjchao.mazenav.logic.structures.IntegerWorld;
import mjchao.mazenav.logic.structures.Operator;
import mjchao.mazenav.logic.structures.Quantifier;
import mjchao.mazenav.logic.structures.Symbol;
import mjchao.mazenav.logic.structures.SymbolTracker;
import mjchao.mazenav.logic.structures.Variable;

public class ExpressionTreeTest {

	public ArrayList< Symbol > convertToPostfix( List< Symbol > input ) {
		Class<?> c = ExpressionTree.class;
		try {
			Method f = c.getDeclaredMethod( "convertToPostfix" , List.class );
			f.setAccessible( true );
			return (ArrayList< Symbol >) f.invoke( null , input );
		} catch (IllegalArgumentException | IllegalAccessException | SecurityException | NoSuchMethodException | InvocationTargetException e) {
			e.printStackTrace();
			throw new RuntimeException( "Could not apply eliminateArrows() method to Processor object." );
		}
	}
	
	public static ExpressionTree.QuantifierList newQuantifierList( Quantifier quantifier , Variable... vars ) {
		return new ExpressionTree.QuantifierList( quantifier , vars );
	}
	
	@Test
	public void testConvertToPostfix() throws IOException {
		SymbolTracker tracker;
		List< Symbol > input;
		List< Symbol > expected;
		List< Symbol > found;
		
		//-----Basic acceptance tests------//
		
		//test on empty input
		tracker = new SymbolTracker();
		input = Arrays.asList();
		expected = Arrays.asList();
		found = convertToPostfix( input );
		Assert.assertTrue( expected.equals( found ) );
		
		//test just one variable "x"
		tracker = new SymbolTracker();
		input = Arrays.asList( 
				tracker.getNewVariable( "x" ) 
			);
		expected = Arrays.asList(
				tracker.getVariableByName( "x" ) 
			);
		found = convertToPostfix( input );
		Assert.assertTrue( expected.equals( found ) );
		
		//test expression with one operator "x OR y"
		tracker = new SymbolTracker();
		input = Arrays.asList( 
				tracker.getNewVariable( "x" ) , Operator.OR ,
				tracker.getNewVariable( "y" )
			);
		expected = Arrays.asList(
				tracker.getVariableByName( "x" ) , tracker.getVariableByName( "y" ) ,
				Operator.OR
			);
		found = convertToPostfix( input );
		Assert.assertTrue( expected.equals( found ) );
		
		
		//test expression with different operators
		//"x OR y AND z"
		tracker = new SymbolTracker();
		input = Arrays.asList( 
				tracker.getNewVariable( "x" ) , Operator.OR ,
				tracker.getNewVariable( "y" ) , Operator.AND ,
				tracker.getNewVariable( "z" )
			);
		expected = Arrays.asList(
				tracker.getVariableByName( "x" ) , tracker.getVariableByName( "y" ) ,
				tracker.getVariableByName( "z" ) , Operator.AND , Operator.OR
			);
		found = convertToPostfix( input );
		Assert.assertTrue( expected.equals( found ) );
		
		//test expression with parentheses
		//"(x OR y) AND z"
		tracker = new SymbolTracker();
		input = Arrays.asList( 
				Symbol.LEFT_PAREN ,
				tracker.getNewVariable( "x" ) , Operator.OR ,
				tracker.getNewVariable( "y" ) , 
				Symbol.RIGHT_PAREN ,
				Operator.AND , tracker.getNewVariable( "z" )
			);
		expected = Arrays.asList(
				tracker.getVariableByName( "x" ) , tracker.getVariableByName( "y" ) , 
				Operator.OR , tracker.getVariableByName( "z" ) , Operator.AND
			);
		found = convertToPostfix( input );
		Assert.assertTrue( expected.equals( found ) );
		
		//test expression with functions
		// "GreaterThan(SumInt( x , y ), DiffInt( x , y )) 
		// <=>  "x y SumInt x y DiffInt GreaterThan"
		tracker = SymbolTracker.fromDataFile( "test/mjchao/mazenav/logic/structures/integerworld.txt" , new IntegerWorld() );
		input = Arrays.asList( 
				tracker.getRelation( "GreaterThan" ) , Symbol.LEFT_PAREN ,
				tracker.getFunction( "SumInt" ) , Symbol.LEFT_PAREN , 
				tracker.getNewVariable( "x" ) , Symbol.COMMA , tracker.getNewVariable( "y" ) ,
				Symbol.RIGHT_PAREN , Symbol.COMMA , tracker.getFunction( "DiffInt" ) ,
				Symbol.LEFT_PAREN , tracker.getVariableByName( "x" ) , Symbol.COMMA ,
				tracker.getVariableByName( "y" ) , Symbol.RIGHT_PAREN , Symbol.RIGHT_PAREN
			);
		expected = Arrays.asList(
				tracker.getVariableByName( "x" ) , tracker.getVariableByName( "y" ) ,
				tracker.getFunction( "SumInt" ) , 
				tracker.getVariableByName( "x" ) , tracker.getVariableByName( "y" ) ,
				tracker.getFunction( "DiffInt" ) , tracker.getRelation( "GreaterThan" )
			);
		found = convertToPostfix( input );
		Assert.assertTrue( expected.equals( found ) );
		
		
		//test expression with quantifiers
		// "FORALL x, y"	<=> "y FORALL(x)"
		tracker = new SymbolTracker();
		input = Arrays.asList( 
				Quantifier.FORALL , tracker.getNewVariable( "x" ) , Symbol.COMMA ,
				tracker.getNewVariable( "y" )
			);
		expected = Arrays.asList(
				tracker.getNewVariable( "y" ) , newQuantifierList( Quantifier.FORALL , tracker.getVariableByName( "x" ) )
			);
		found = convertToPostfix( input );
		Assert.assertTrue( expected.equals( found ) );	
		
		// "FORALL x, x AND y => y AND x"
		// <=> "x y AND y x AND => FORALL(x)"
		tracker = new SymbolTracker();
		input = Arrays.asList( 
				Quantifier.FORALL , tracker.getNewVariable( "x" ) , Symbol.COMMA ,
				tracker.getVariableByName( "x" ) , Operator.AND , tracker.getNewVariable( "y" ) ,
				Operator.IMPLICATION , tracker.getVariableByName( "y" ) , Operator.AND ,
				tracker.getVariableByName( "x" )
			);
		expected = Arrays.asList(
				tracker.getVariableByName( "x" ) , tracker.getVariableByName( "y" ) ,
				Operator.AND , tracker.getVariableByName( "y" ) , tracker.getVariableByName( "x" ) ,
				Operator.AND , Operator.IMPLICATION , newQuantifierList( Quantifier.FORALL , tracker.getVariableByName( "x" ) )
			);
		found = convertToPostfix( input );
		Assert.assertTrue( expected.equals( found ) );
		
		// "FORALL (x,y) x AND y"
		// <=> "x y AND FORALL(x,y)"
		tracker = new SymbolTracker();
		input = Arrays.asList( 
				Quantifier.FORALL , Symbol.LEFT_PAREN , tracker.getNewVariable( "x" ) ,
				Symbol.COMMA , tracker.getNewVariable( "y" ) , Symbol.RIGHT_PAREN ,
				tracker.getVariableByName( "x" ) , Operator.AND , tracker.getVariableByName( "y" )
			);
		expected = Arrays.asList(
				tracker.getVariableByName( "x" ) , tracker.getVariableByName( "y" ) ,
				Operator.AND ,
				newQuantifierList( Quantifier.FORALL , tracker.getVariableByName( "x" ) , tracker.getVariableByName( "y" ) )
			);
		found = convertToPostfix( input );
		System.out.println( found );
		Assert.assertTrue( expected.equals( found ) );		
		
		// "FORALL(x, y) x AND y => y AND x"
		// <=> "x y AND y x AND => FORALL(x,y)"
		
	}
}
