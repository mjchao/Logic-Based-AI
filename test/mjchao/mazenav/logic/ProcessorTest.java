package mjchao.mazenav.logic;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import mjchao.mazenav.logic.structures.NumbersFOL;
import mjchao.mazenav.logic.structures.Operator;
import mjchao.mazenav.logic.structures.Quantifier;
import mjchao.mazenav.logic.structures.Symbol;
import mjchao.mazenav.logic.structures.SymbolTracker;

public class ProcessorTest {

	/**
	 * Accessor to the private tokens field of the Processor
	 * class.
	 * 
	 * @param p
	 * @return
	 */
	public ArrayList< Symbol > getTokens( Processor p ) {
		Class<?> c = Processor.class;
		try {
			Field f = c.getDeclaredField( "tokens" );
			f.setAccessible( true );
			return (ArrayList<Symbol>) f.get( p );
		} catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
			e.printStackTrace();
			throw new RuntimeException( "Could not apply getTokens() method to Processor object." );
		}
	}
	
	@Test
	public void tokenizeBySymbols() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		Method preprocess = Processor.class.getDeclaredMethod( "tokenizeByReservedSymbols" , String.class );
		preprocess.setAccessible( true );
		
		//distinguishing between ! and !=
		String input = "(!x OR y) && (x != y)";
		String[] expected = new String[] { "(" , "!" , "x" , "OR" , "y" , ")" , "&&" , "(" , "x" , "!=" , "y" , ")" };
		String[] found = (String[])preprocess.invoke( null , input );
		Assert.assertArrayEquals( expected , found );
		
		//distinguishing between EQUALS and NEQUALS
		input = "FORALL(x, y), !(x EQUALS y) <=> x NEQUALS y";
		expected = new String[] { "FORALL" , "(" , "x" , "," , "y" , ")" , "," , 
				"!" , "(" , "x" , "EQUALS" , "y" , ")" , "<=>" , "x" , 
				"NEQUALS" , "y" };
		found = (String[])preprocess.invoke( null , input );
		Assert.assertArrayEquals( expected , found );
		
		//distinguishing between => and <=> and lack of spaces
		input = "x==y||x=>y||x<=>y";
		expected = new String[] { "x" , "==" , "y" , "||" , "x" , "=>" , "y" , "||" , "x" , "<=>" , "y" };
		found = (String[]) preprocess.invoke( null , input );
		Assert.assertArrayEquals( expected , found );
		
		//dealing with too many spaces
		input = "FORALL     x        !       x     <=>      EXISTS y";
		expected = new String[] { "FORALL" , "x" , "!" , "x" , "<=>" , "EXISTS" , "y" };
		found = (String[]) preprocess.invoke( null , input );
		Assert.assertArrayEquals( expected , found );	
	}
		
	@Test
	public void tokenizeBySymbolsReservedKeywordsInVariableNames() throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		Method preprocess = Processor.class.getDeclaredMethod( "tokenizeByReservedSymbols" , String.class );
		preprocess.setAccessible( true );
		
		String input;
		String[] expected;
		String[] found;
		
		//realizing that OR might be part of a variable name
		input = "!xORy ORy yOR";
		expected = new String[] { "!" , "xORy" , "ORy" , "yOR" };
		found = (String[]) preprocess.invoke( null , input );
		Assert.assertArrayEquals( expected , found );
		
		//edge cases with reserved keywords in variable names
		input = "OR";
		expected = new String[] {"OR"};
		found = (String[]) preprocess.invoke( null , input );
		Assert.assertArrayEquals( expected , found );
		
		input = "ORx";
		expected = new String[] {"ORx"};
		found = (String[]) preprocess.invoke( null , input );
		Assert.assertArrayEquals( expected , found );
	}
	
	@Test
	public void tokenizeBySymbolsEdgeCases() throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		Method preprocess = Processor.class.getDeclaredMethod( "tokenizeByReservedSymbols" , String.class );
		preprocess.setAccessible( true );
		
		String input;
		String[] expected;
		String[] found;
		
		//other edge cases
		input = "or";
		expected = new String[] {"or"};
		found = (String[]) preprocess.invoke( null , input );
		Assert.assertArrayEquals( expected , found );
		
		input = "";
		expected = new String[] {};
		found = (String[]) preprocess.invoke( null , input );
		Assert.assertArrayEquals( expected , found );
		
		input = "          ";
		expected = new String[] {};
		found = (String[]) preprocess.invoke( null , input );
		Assert.assertArrayEquals( expected , found );
		
		input = ">";
		expected = new String[] { ">" };
		found = (String[]) preprocess.invoke( null , input );
		Assert.assertArrayEquals( expected , found );
	}
	
	@Test
	public void testTokenizeWithoutStructures() {
		//test without having to preload a SymbolTracker from
		//some file
		
		Processor test;
		String logicStatement;
		SymbolTracker tracker = new SymbolTracker();
		List<Symbol> tokens;
		List<Symbol> expected;
		
		//basic acceptance test:
		logicStatement = "FORALL x, x==1";
		tracker = new SymbolTracker();
		test = new Processor( logicStatement , tracker );
		test.tokenize();
		tokens = getTokens( test );
		expected = Arrays.asList( Quantifier.FORALL , tracker.getVariableByName( "x" ) , 
				Symbol.COMMA , tracker.getVariableByName( "x" ) , 
				Operator.EQUALS , NumbersFOL.fromInt( 1 ) );
		Assert.assertTrue( tokens.equals( expected ) );
		
		//longer basic acceptance test:
		logicStatement = "FORALL(x, y), EXISTS z S.T. z == x AND EXISTS u S.T. u == y";
		tracker = new SymbolTracker();
		test = new Processor( logicStatement , tracker );
		test.tokenize();
		tokens = getTokens( test );
		expected = Arrays.asList( Quantifier.FORALL , Symbol.LEFT_PAREN , tracker.getVariableByName( "x" ) , 
				Symbol.COMMA , tracker.getVariableByName( "y" ) , Symbol.RIGHT_PAREN , 
				Symbol.COMMA , Quantifier.EXISTS , tracker.getVariableByName( "z" ) ,
				Symbol.SUCH_THAT , tracker.getVariableByName( "z" ) , Operator.EQUALS ,
				tracker.getVariableByName( "x" ) , Operator.AND , Quantifier.EXISTS , 
				tracker.getVariableByName( "u" ) , Symbol.SUCH_THAT , tracker.getVariableByName( "u" ) ,
				Operator.EQUALS , tracker.getVariableByName( "y" ) );
		Assert.assertTrue( tokens.equals( expected ) );
		
		//test that != does not get mixed up with !
		logicStatement = "(!x OR y) && (x != y)";
		tracker = new SymbolTracker();
		test = new Processor( logicStatement , tracker );
		test.tokenize();
		tokens = getTokens( test );
		expected = Arrays.asList( Symbol.LEFT_PAREN , Operator.NOT , tracker.getVariableByName( "x" ) , 
							Operator.OR , tracker.getVariableByName( "y" ) , Symbol.RIGHT_PAREN ,
							Operator.AND , Symbol.LEFT_PAREN , tracker.getVariableByName( "x" ) , 
							Operator.NOT_EQUALS , tracker.getVariableByName( "y" ) , Symbol.RIGHT_PAREN );
		Assert.assertTrue( tokens.equals( expected ) );
		
		//test various operators
		logicStatement = "   && || ! != IMPLICATION BICONDITIONAL , AND OR NEQUALS EQUALS";
		tracker = new SymbolTracker();
		test = new Processor( logicStatement , tracker );
		test.tokenize();
		tokens = getTokens( test );
		expected = Arrays.asList( Operator.AND , Operator.OR , Operator.NOT , 
				Operator.NOT_EQUALS , Operator.IMPLICATION , Operator.BICONDITIONAL , 
				Symbol.COMMA , Operator.AND , Operator.OR , Operator.NOT_EQUALS , Operator.EQUALS );
		Assert.assertTrue( tokens.equals( expected ) );
		
		//test various symbols
		logicStatement = "   )   (   , S.T. ";
		tracker = new SymbolTracker();
		test = new Processor( logicStatement , tracker );
		test.tokenize();
		tokens = getTokens( test );
		expected = Arrays.asList( Symbol.RIGHT_PAREN , Symbol.LEFT_PAREN , Symbol.COMMA , Symbol.SUCH_THAT );
		Assert.assertTrue( tokens.equals( expected ) );
		
		//test various quantifiers
		logicStatement = "FORALL FORALL EXISTS    ";
		tracker = new SymbolTracker();
		test = new Processor( logicStatement , tracker );
		test.tokenize();
		tokens = getTokens( test );
		expected = Arrays.asList( Quantifier.FORALL , Quantifier.FORALL , Quantifier.EXISTS );
		Assert.assertTrue( tokens.equals( expected ) );
	}
}
