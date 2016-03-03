package mjchao.mazenav.logic;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import mjchao.mazenav.logic.structures.Operator;
import mjchao.mazenav.logic.structures.Quantifier;
import mjchao.mazenav.logic.structures.Symbol;
import mjchao.mazenav.logic.structures.SymbolTracker;

public class ProcessorConvertToNNFTest {
	
	public List< Symbol > negate( Processor p , List< Symbol > input ) {
		Class<?> c = Processor.class;
		try {
			Method f = c.getDeclaredMethod( "negate" , List.class );
			f.setAccessible( true );
			return (List<Symbol>) f.invoke( p , input );
		} catch (IllegalArgumentException | IllegalAccessException | SecurityException | NoSuchMethodException | InvocationTargetException e) {
			e.printStackTrace();
			throw new RuntimeException( "Could not apply negate() method to Processor object." );
		}		
	}

	public List< Symbol > distributeNots( Processor p , List< Symbol > input ) {
		Class<?> c = Processor.class;
		try {
			Method f = c.getDeclaredMethod( "distributeNots" , List.class );
			f.setAccessible( true );
			return (List<Symbol>) f.invoke( p , input );
		} catch (IllegalArgumentException | IllegalAccessException | SecurityException | NoSuchMethodException | InvocationTargetException e) {
			e.printStackTrace();
			throw new RuntimeException( "Could not apply distributeNots() method to Processor object." );
		}		
	}
	
	public List< Symbol > eliminateArrows( Processor p , List< Symbol > input ) {
		Class<?> c = Processor.class;
		try {
			Method f = c.getDeclaredMethod( "eliminateArrows" , List.class );
			f.setAccessible( true );
			return (List<Symbol>) f.invoke( p , input );
		} catch (IllegalArgumentException | IllegalAccessException | SecurityException | NoSuchMethodException | InvocationTargetException e) {
			e.printStackTrace();
			throw new RuntimeException( "Could not apply eliminateArrows() method to Processor object." );
		}			
	}
	
	@Test
	public void testDistributeNotsAlreadyInNNF() {
		SymbolTracker tracker;
		Processor p;
		List< Symbol > input;
		List< Symbol > expected;
		List< Symbol > found;
		
		//----basic acceptance tests------//
		
		//test distributing no NOTs with input "x"
		tracker = new SymbolTracker();
		p = new Processor( "" , tracker );
		input = new ArrayList< Symbol >( Arrays.asList( 
				tracker.getNewVariable( "x" ) ) );
		expected = Arrays.asList( 
				tracker.getVariableByName( "x" ) );
		found = distributeNots( p , input );
		Assert.assertTrue( expected.equals( found ) );
		
		//test distributing no NOTs with input "(x)"
		tracker = new SymbolTracker();
		p = new Processor( "" , tracker );
		input = new ArrayList< Symbol >( Arrays.asList( 
				Symbol.LEFT_PAREN , tracker.getNewVariable( "x" ) , Symbol.RIGHT_PAREN ) );
		expected = Arrays.asList( 
				tracker.getVariableByName( "x" ) );
		found = distributeNots( p , input );
		Assert.assertTrue( expected.equals( found ) );
		
		//test distributing no NOTs over a single variable
		//with input "!x"
		tracker = new SymbolTracker();
		p = new Processor( "" , tracker );
		input = new ArrayList< Symbol >( Arrays.asList( 
				Operator.NOT , tracker.getNewVariable( "x" ) ) );
		expected = Arrays.asList( 
				Operator.NOT , tracker.getVariableByName( "x" ) );
		found = distributeNots( p , input );
		Assert.assertTrue( expected.equals( found ) );
		
		//test distributing no NOTs over a quantifier with
		//input "! EXISTS x"
		tracker = new SymbolTracker();
		p = new Processor( "" , tracker );
		input = new ArrayList< Symbol >( Arrays.asList( 
				Operator.NOT , Quantifier.EXISTS , tracker.getNewVariable( "x" ) ) );
		expected = Arrays.asList( 
				Operator.NOT , Quantifier.EXISTS , tracker.getVariableByName( "x" ) );
		found = distributeNots( p , input );
		Assert.assertTrue( expected.equals( found ) );
		
		//test distributing a single NOT over input "!(x)"
		tracker = new SymbolTracker();
		p = new Processor( "" , tracker );
		input = new ArrayList< Symbol >( Arrays.asList( 
				Operator.NOT , Symbol.LEFT_PAREN , tracker.getNewVariable( "x" ) , Symbol.RIGHT_PAREN ) );
		expected = Arrays.asList( 
				Operator.NOT , tracker.getVariableByName( "x" ) );
		found = distributeNots( p , input );
		Assert.assertTrue( expected.equals( found ) );
		
		//test distributing NOT over OR
		tracker = new SymbolTracker();
		p = new Processor( "" , tracker );
		input = new ArrayList< Symbol >( Arrays.asList( 
				Operator.NOT , Symbol.LEFT_PAREN , tracker.getNewVariable( "x" ) , 
				Operator.OR , tracker.getNewVariable( "y" ) , Symbol.RIGHT_PAREN ) );
		expected = Arrays.asList( 
				Operator.NOT , tracker.getVariableByName( "x" ) , Operator.AND , 
				Operator.NOT , tracker.getVariableByName( "y" ) );
		found = distributeNots( p , input );
		Assert.assertTrue( expected.equals( found ) );
		
		//test distributing NOT over AND
		tracker = new SymbolTracker();
		p = new Processor( "" , tracker );
		input = new ArrayList< Symbol >( Arrays.asList( 
				Operator.NOT , Symbol.LEFT_PAREN , tracker.getNewVariable( "x" ) , 
				Operator.AND , tracker.getNewVariable( "y" ) , Symbol.RIGHT_PAREN ) );
		expected = Arrays.asList( 
				Operator.NOT , tracker.getVariableByName( "x" ) , Operator.OR , 
				Operator.NOT , tracker.getVariableByName( "y" ) );
		found = distributeNots( p , input );
		Assert.assertTrue( expected.equals( found ) );
		
		//------more complicated tests-----//
		//test distributing over two parenthetical expressions
		//!(!(x AND y) OR !(x OR y))	<=>		x AND y AND (x OR y) 
		tracker = new SymbolTracker();
		p = new Processor( "" , tracker );
		input = new ArrayList< Symbol >( Arrays.asList( 
				Operator.NOT , Symbol.LEFT_PAREN , Operator.NOT , Symbol.LEFT_PAREN ,
				tracker.getNewVariable( "x" ) , Operator.AND , tracker.getNewVariable( "y" ) ,
				Symbol.RIGHT_PAREN , Operator.OR , Operator.NOT , Symbol.LEFT_PAREN ,
				tracker.getVariableByName( "x" ) , Operator.OR , tracker.getVariableByName( "y" ) ,
				Symbol.RIGHT_PAREN , Symbol.RIGHT_PAREN ) );
		expected = Arrays.asList( 
					tracker.getVariableByName( "x" ) , Operator.AND , 
					tracker.getVariableByName( "y" ) , Operator.AND ,
					Symbol.LEFT_PAREN , tracker.getVariableByName( "x" ) , Operator.OR , 
					tracker.getVariableByName( "y" ) , Symbol.RIGHT_PAREN
				);
		found = distributeNots( p , input );
		Assert.assertTrue( expected.equals( found ) );
		
		//test distributing over two parenthetical expressions
		//with redundant parentheses
		//!(!((x AND y)) OR !(((x OR y))))    <=> 	x AND y AND (x OR y)
		tracker = new SymbolTracker();
		p = new Processor( "" , tracker );
		input = new ArrayList< Symbol >( Arrays.asList( 
				Operator.NOT , Symbol.LEFT_PAREN , Operator.NOT , Symbol.LEFT_PAREN , Symbol.LEFT_PAREN ,
				tracker.getNewVariable( "x" ) , Operator.AND , tracker.getNewVariable( "y" ) ,
				Symbol.RIGHT_PAREN ,  Symbol.RIGHT_PAREN , Operator.OR , Operator.NOT , 
				Symbol.LEFT_PAREN , Symbol.LEFT_PAREN , Symbol.LEFT_PAREN ,
				tracker.getVariableByName( "x" ) , Operator.OR , tracker.getVariableByName( "y" ) ,
				Symbol.RIGHT_PAREN , Symbol.RIGHT_PAREN , Symbol.RIGHT_PAREN , Symbol.RIGHT_PAREN ) );
		expected = Arrays.asList( 
					tracker.getVariableByName( "x" ) , Operator.AND , 
					tracker.getVariableByName( "y" ) , Operator.AND ,
					Symbol.LEFT_PAREN , tracker.getVariableByName( "x" ) , Operator.OR , 
					tracker.getVariableByName( "y" ) , Symbol.RIGHT_PAREN
				);
		found = distributeNots( p , input );
		Assert.assertTrue( expected.equals( found ) );
		
		//test distributing over multiple parenthetical expression with redundant
		//parentheses
		//!(!((x AND y)) OR !((x AND z)) OR !((y AND z)))   <=>
		//x AND y AND x AND z AND y AND z
		//(note, no simplification occurs in this process yet)
		tracker = new SymbolTracker();
		p = new Processor( "" , tracker );
		input = new ArrayList< Symbol >( Arrays.asList( 
				Operator.NOT , Symbol.LEFT_PAREN , 
				
				Operator.NOT , Symbol.LEFT_PAREN ,
				Symbol.LEFT_PAREN , tracker.getNewVariable( "x" ) , Operator.AND ,
				tracker.getNewVariable( "y" ) , Symbol.RIGHT_PAREN , Symbol.RIGHT_PAREN ,
							Operator.OR ,		
				Operator.NOT , Symbol.LEFT_PAREN , Symbol.LEFT_PAREN , tracker.getVariableByName( "x" ) ,
				Operator.AND , tracker.getNewVariable( "z" ) , Symbol.RIGHT_PAREN , Symbol.RIGHT_PAREN ,
							Operator.OR ,
				Operator.NOT , Symbol.LEFT_PAREN , Symbol.LEFT_PAREN , tracker.getVariableByName( "y" ) ,
				Operator.AND , tracker.getVariableByName( "z" ) , Symbol.RIGHT_PAREN , Symbol.RIGHT_PAREN ,
				
				Symbol.RIGHT_PAREN 
			) );
		expected = Arrays.asList( 
					tracker.getVariableByName( "x" ) , Operator.AND , tracker.getVariableByName( "y" ) ,
					Operator.AND , tracker.getVariableByName( "x" ) , Operator.AND , tracker.getVariableByName( "z" ) ,
					Operator.AND , tracker.getVariableByName( "y" ) , Operator.AND , tracker.getVariableByName( "z" )
				);
		found = distributeNots( p , input );
		Assert.assertTrue( expected.equals( found ) );
	}
	
	@Test
	public void testNegateAlreadyInNNF() {
		SymbolTracker tracker;
		Processor p;
		List< Symbol > input;
		List< Symbol > expected;
		List< Symbol > found;
		
		//------basic acceptance tests------//
		
		//test negating the statement "x"
		tracker = new SymbolTracker();
		p = new Processor( "" , tracker );
		input = new ArrayList< Symbol >( Arrays.asList( tracker.getNewVariable( "x" ) ) );
		expected = Arrays.asList( Operator.NOT , tracker.getVariableByName( "x" ) );
		found = negate( p , input );
		Assert.assertTrue( expected.equals( found ) );
		
		//test negating the statmeent "(x)"
		tracker = new SymbolTracker();
		p = new Processor( "" , tracker );
		input = new ArrayList< Symbol >( Arrays.asList( 
				Symbol.LEFT_PAREN , tracker.getNewVariable( "x" ) , Symbol.RIGHT_PAREN ) );
		expected = Arrays.asList( 
				Operator.NOT , 
				tracker.getVariableByName( "x" ) );
		found = negate( p , input );
		Assert.assertTrue( expected.equals( found ) );
		
		//test negating the statement "x AND y"
		tracker = new SymbolTracker();
		p = new Processor( "" , tracker );
		input = new ArrayList< Symbol >( Arrays.asList( 
				tracker.getNewVariable( "x" ) , Operator.AND , tracker.getNewVariable( "y" ) ) );
		expected = Arrays.asList( 
				Operator.NOT , tracker.getVariableByName( "x" ) , Operator.OR , 
				Operator.NOT , tracker.getVariableByName( "y" ) );
		found = negate( p , input );
		Assert.assertTrue( expected.equals( found ) );
		
		//test negating the statement "!x AND !y"
		tracker = new SymbolTracker();
		p = new Processor( "" , tracker );
		input = new ArrayList< Symbol >( Arrays.asList( 
				Operator.NOT , tracker.getNewVariable( "x" ) , Operator.AND , 
				Operator.NOT , tracker.getNewVariable( "y" ) ) );
		expected = Arrays.asList( 
				tracker.getVariableByName( "x" ) , Operator.OR , 
				tracker.getVariableByName( "y" ) );
		found = negate( p , input );
		Assert.assertTrue( expected.equals( found ) );
		
		//test negating compound statement !x AND !y AND !z
		tracker = new SymbolTracker();
		p = new Processor( "" , tracker );
		input = new ArrayList< Symbol >( Arrays.asList( 
				Operator.NOT , tracker.getNewVariable( "x" ) , Operator.AND , 
				Operator.NOT , tracker.getNewVariable( "y" ) , Operator.AND , 
				Operator.NOT , tracker.getNewVariable( "z" ) ) );
		expected = Arrays.asList( 
				tracker.getVariableByName( "x" ) , Operator.OR , 
				tracker.getVariableByName( "y" ) , Operator.OR ,
				tracker.getVariableByName( "z" ) );
		found = negate( p , input );
		Assert.assertTrue( expected.equals( found ) );		
		
		//------Corner Cases-------//
		
		//test negating the statement "!!x AND !!y"
		tracker = new SymbolTracker();
		p = new Processor( "" , tracker );
		input = new ArrayList< Symbol >( Arrays.asList( 
				Operator.NOT , Operator.NOT , tracker.getNewVariable( "x" ) , Operator.AND , 
				Operator.NOT , Operator.NOT , tracker.getNewVariable( "y" ) ) );
		expected = Arrays.asList( 
				Operator.NOT , tracker.getVariableByName( "x" ) , Operator.OR , 
				Operator.NOT , tracker.getVariableByName( "y" ) );
		found = negate( p , input );
		Assert.assertTrue( expected.equals( found ) );
		
		//test negating the statement !!!x AND !!!y"
		tracker = new SymbolTracker();
		p = new Processor( "" , tracker );
		input = new ArrayList< Symbol >( Arrays.asList( 
				Operator.NOT , Operator.NOT , Operator.NOT , tracker.getNewVariable( "x" ) , Operator.AND , 
				Operator.NOT , Operator.NOT , Operator.NOT , tracker.getNewVariable( "y" ) ) );
		expected = Arrays.asList( 
				tracker.getVariableByName( "x" ) , Operator.OR , 
				tracker.getVariableByName( "y" ) );
		found = negate( p , input );
		Assert.assertTrue( expected.equals( found ) );
		
		//-----Complex Test Cases------//
		
		//test negating with parenthetical expressions
		
		//test negating the statement !(!(!(!(x))))
		tracker = new SymbolTracker();
		p = new Processor( "" , tracker );
		input = new ArrayList< Symbol >( Arrays.asList( 
				Operator.NOT , Symbol.LEFT_PAREN , Operator.NOT , Symbol.LEFT_PAREN , 
				Operator.NOT , Symbol.LEFT_PAREN , Operator.NOT , Symbol.LEFT_PAREN , 
				tracker.getNewVariable( "x" ) , 
				Symbol.RIGHT_PAREN , Symbol.RIGHT_PAREN , Symbol.RIGHT_PAREN , 
				Symbol.RIGHT_PAREN) );
		expected = Arrays.asList(
				Operator.NOT , tracker.getVariableByName( "x" )
				);
		found = distributeNots( p , input );
		found = negate( p , found );
		Assert.assertTrue( expected.equals( found ) );
	}
	
	@Test
	public void testEliminateArrows() {
		SymbolTracker tracker;
		Processor p;
		List< Symbol > input;
		List< Symbol > expected;
		List< Symbol > found;
		
		//-----basic acceptance tests-------//
		
		//test removing arrows from "P => Q"
		//to get "!P OR Q"
		tracker = new SymbolTracker();
		p = new Processor( "" , tracker );
		input = new ArrayList< Symbol >( Arrays.asList( 
				tracker.getNewVariable( "P" ) , Operator.IMPLICATION ,
				tracker.getNewVariable( "Q" )
		) );
		expected = Arrays.asList( 
				Operator.NOT , tracker.getVariableByName( "P" ) , Operator.OR ,
				tracker.getVariableByName( "Q" )
		);
		found = eliminateArrows( p , input );
		Assert.assertTrue( expected.equals( found ) );
		
		//test removing arrows from "!P => !Q"
		//to get "!P OR Q"
		tracker = new SymbolTracker();
		p = new Processor( "" , tracker );
		input = new ArrayList< Symbol >( Arrays.asList( 
				Operator.NOT , tracker.getNewVariable( "P" ) , Operator.IMPLICATION ,
				Operator.NOT , tracker.getNewVariable( "Q" )
		) );
		expected = Arrays.asList( 
				tracker.getVariableByName( "P" ) , Operator.OR , Operator.NOT ,
				tracker.getVariableByName( "Q" )
		);
		found = eliminateArrows( p , input );
		Assert.assertTrue( expected.equals( found ) );

		//test removing arrows from "P <=> Q"
		//to get "(P OR !Q) AND (!P OR Q)"
		tracker = new SymbolTracker();
		p = new Processor( "" , tracker );
		input = new ArrayList< Symbol >( Arrays.asList( 
				tracker.getNewVariable( "P" ) , Operator.BICONDITIONAL ,
				tracker.getNewVariable( "Q" )
		) );
		expected = Arrays.asList( 
				Symbol.LEFT_PAREN , tracker.getVariableByName( "P" ) , Operator.OR ,
				Operator.NOT , tracker.getVariableByName( "Q" ) , Symbol.RIGHT_PAREN ,
				Operator.AND , Symbol.LEFT_PAREN , Operator.NOT , tracker.getVariableByName( "P" ) ,
				Operator.OR , tracker.getVariableByName( "Q" ) , Symbol.RIGHT_PAREN
		);
		found = eliminateArrows( p , input );
		Assert.assertTrue( expected.equals( found ) );
		
		//test removing arrows from "!P <=> !Q"
		//to get "(!P OR Q) AND (P OR !Q)"
		tracker = new SymbolTracker();
		p = new Processor( "" , tracker );
		input = new ArrayList< Symbol >( Arrays.asList( 
				Operator.NOT , tracker.getNewVariable( "P" ) , Operator.BICONDITIONAL ,
				Operator.NOT , tracker.getNewVariable( "Q" )
		) );
		expected = Arrays.asList( 
				Symbol.LEFT_PAREN , Operator.NOT , tracker.getVariableByName( "P" ) ,
				Operator.OR , tracker.getVariableByName( "Q" ) , Symbol.RIGHT_PAREN ,
				Operator.AND , Symbol.LEFT_PAREN , tracker.getVariableByName( "P" ) ,
				Operator.OR , Operator.NOT , tracker.getVariableByName( "Q" ) , Symbol.RIGHT_PAREN
		);
		found = eliminateArrows( p , input );
		Assert.assertTrue( expected.equals( found ) );
		
		//----Test implications with parentheses-----//
		//test removing arrows from "R AND (P => Q)"
		//to get "R AND !P OR Q"
		tracker = new SymbolTracker();
		p = new Processor( "" , tracker );
		input = new ArrayList< Symbol >( Arrays.asList( 
				tracker.getNewVariable( "R" ) , Operator.AND , Symbol.LEFT_PAREN ,
				tracker.getNewVariable( "P" ) , Operator.IMPLICATION , tracker.getNewVariable( "Q" ) ,
				Symbol.RIGHT_PAREN
		) );
		expected = Arrays.asList( 
				tracker.getVariableByName( "R" ) , Operator.AND , 
				Operator.NOT , tracker.getVariableByName( "P" ) ,
				Operator.OR , tracker.getVariableByName( "Q" )
		);
		found = eliminateArrows( p , input );
		Assert.assertTrue( expected.equals( found ) );		
		
		//-----Test removing multiple arrows------//
		
		//test removing arrows from "P => Q => R". Assuming right associativity,
		//this is equivalent to "P => (Q => R)" and we get
		//"!P OR (Q => R)" which is equivalent to
		//"!P OR !Q OR R"
		tracker = new SymbolTracker();
		p = new Processor( "" , tracker );
		input = new ArrayList< Symbol >( Arrays.asList( 
				tracker.getNewVariable( "P" ) , Operator.IMPLICATION , tracker.getNewVariable( "Q" ) ,
				Operator.IMPLICATION , tracker.getNewVariable( "R" )
		) );
		expected = Arrays.asList( 
				Operator.NOT , tracker.getVariableByName( "P" ) , Operator.OR , 
				Operator.NOT , tracker.getVariableByName( "Q" ) , Operator.OR ,
				tracker.getVariableByName( "R" )
		);
		found = eliminateArrows( p , input );
		System.out.println( found.toString() );
		Assert.assertTrue( expected.equals( found ) );		
	}
	
	@Ignore
	public void testNegateConvertIntoCNF() {
		SymbolTracker tracker;
		Processor p;
		List< Symbol > input;
		List< Symbol > expected;
		List< Symbol > found;
		
		//test negating the statement !(x)
		tracker = new SymbolTracker();
		p = new Processor( "" , tracker );
		input = new ArrayList< Symbol >( Arrays.asList( 
				Operator.NOT , Symbol.LEFT_PAREN , tracker.getNewVariable( "x" ) , 
				Symbol.RIGHT_PAREN ) );
		expected = Arrays.asList(
				tracker.getVariableByName( "x" )
				);
		found = distributeNots( p , input );
		System.out.println( found.toString() );
		Assert.assertTrue( expected.equals( found ) );
	}
}
