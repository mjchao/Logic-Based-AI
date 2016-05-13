package mjchao.mazenav.logic;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import mjchao.mazenav.logic.ExpressionTree.ExpressionNode;
import mjchao.mazenav.logic.structures.BooleanFOL;
import mjchao.mazenav.logic.structures.Function;
import mjchao.mazenav.logic.structures.IntegerWorld;
import mjchao.mazenav.logic.structures.ObjectFOL;
import mjchao.mazenav.logic.structures.Operator;
import mjchao.mazenav.logic.structures.Quantifier;
import mjchao.mazenav.logic.structures.Relation;
import mjchao.mazenav.logic.structures.SkolemFunction;
import mjchao.mazenav.logic.structures.Symbol;
import mjchao.mazenav.logic.structures.SymbolTracker;
import mjchao.mazenav.logic.structures.Variable;

import org.junit.Assert;
import org.junit.Test;

public class ExpressionTreeTest {

	public ArrayList< Symbol > convertToPostfix( List< Symbol > input ) {
		Class<?> c = ExpressionTree.class;
		try {
			Method f = c.getDeclaredMethod( "convertToPostfix" , List.class );
			f.setAccessible( true );
			return (ArrayList< Symbol >) f.invoke( null , input );
		} catch (IllegalArgumentException | IllegalAccessException | SecurityException | NoSuchMethodException | InvocationTargetException e) {
			e.printStackTrace();
			throw new RuntimeException( "Could not apply convertToPostfix() method to ExpressionTree object." );
		}
	}
	
	public void setPostfix( ExpressionTree tree , List< Symbol > postfix ) {
		Class<?> c = ExpressionTree.class;
		try {
			Field f = c.getDeclaredField( "postfixExpression" );
			f.setAccessible( true );
			f.set( tree , postfix );
		} catch (IllegalArgumentException | IllegalAccessException | SecurityException | NoSuchFieldException e ) {
			e.printStackTrace();
			throw new RuntimeException( "Could not set field postfixExpression of ExpressionTree object." );
		}			
	}
	
	public void buildTree( ExpressionTree tree ) {
		Class<?> c = ExpressionTree.class;
		try {
			Method f = c.getDeclaredMethod( "buildTree" );
			f.setAccessible( true );
			f.invoke( tree );
		} catch (IllegalArgumentException | IllegalAccessException | SecurityException | NoSuchMethodException | InvocationTargetException e) {
			e.printStackTrace();
			throw new RuntimeException( "Could not apply buildTree() method to ExpressionTree object." );
		}		
	}
	
	public void eliminateArrowsAndDistributeNots( ExpressionTree tree ) {
		Class<?> c = ExpressionTree.class;
		try {
			Method f = c.getDeclaredMethod( "eliminateArrowsAndDistributeNots" );
			f.setAccessible( true );
			f.invoke( tree );
		} catch (IllegalArgumentException | IllegalAccessException | SecurityException | NoSuchMethodException | InvocationTargetException e) {
			e.printStackTrace();
			throw new RuntimeException( "Could not apply eliminateArrowsAndDistributeNots() method to ExpressionTree object." );
		}			
	}
	
	public void standardize( ExpressionTree tree , SymbolTracker tracker ) {
		Class<?> c = ExpressionTree.class;
		try {
			Method f = c.getDeclaredMethod( "standardize" , SymbolTracker.class );
			f.setAccessible( true );
			f.invoke( tree , tracker );
		} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
			throw new RuntimeException( "Could not apply standardize() method to ExpressionTree object." );
		}
	}
	
	public void skolemize( ExpressionTree tree , SymbolTracker tracker ) {
		Class<?> c = ExpressionTree.class;
		try {
			Method f = c.getDeclaredMethod( "skolemize" , SymbolTracker.class );
			f.setAccessible( true );
			f.invoke( tree , tracker );
		} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
			throw new RuntimeException( "Could not apply skolemize() method to ExpressionTree object." );
		}		
	}
	
	public void dropQuantifiers( ExpressionTree tree , SymbolTracker tracker ) {
		Class<?> c = ExpressionTree.class;
		try {
			Method f = c.getDeclaredMethod( "dropQuantifiers" , SymbolTracker.class );
			f.setAccessible( true );
			f.invoke( tree , tracker );
		} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
			throw new RuntimeException( "Could not apply dropQuantifiers() method to ExpressionTree object." );
		}		
	}
	
	public void distributeOrOverAnd( ExpressionTree tree ) {
		Class<?> c = ExpressionTree.class;
		try {
			Method f = c.getDeclaredMethod( "distributeOrOverAnd" );
			f.setAccessible( true );
			f.invoke( tree );
		} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
			throw new RuntimeException( "Could not apply distributeOrOverAnd() method to ExpressionTree object." );
		}		
	}
	
	public ExpressionNode getRoot( ExpressionTree tree ) {
		Class<?> c = ExpressionTree.class;
		try {
			Field f = c.getDeclaredField( "root" );
			f.setAccessible( true );
			return (ExpressionNode) f.get( tree );
		} catch (IllegalArgumentException | IllegalAccessException | SecurityException | NoSuchFieldException e ) {
			e.printStackTrace();
			throw new RuntimeException( "Could not access field root of ExpressionTree object." );
		}		
	}
	
	public void buildPostfixFromExpressionTree( ExpressionNode node , List< Symbol > postfix ) {
		ArrayList< ExpressionNode > children = node.getChildren();
		if ( children.size() == 0 ) {
			postfix.add( node.getValue() );
			if ( node.isNegated() ) {
				postfix.add( Operator.NOT );
			}
			return;
		}
		
		for ( ExpressionNode child : children ) {
			buildPostfixFromExpressionTree( child , postfix );
		}
		postfix.add( node.getValue() );
		if ( node.isNegated() ) {
			postfix.add( Operator.NOT );
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
		
		//test multiple NOTs:
		//"!!!x OR !y"
		tracker = new SymbolTracker();
		input = Arrays.asList( 
				Operator.NOT , Operator.NOT , Operator.NOT , tracker.getNewVariable( "x" ) , Operator.OR ,
				Operator.NOT , tracker.getNewVariable( "y" )
			);
		expected = Arrays.asList(
				tracker.getVariableByName( "x" ) , Operator.NOT , Operator.NOT , Operator.NOT ,
				tracker.getVariableByName( "y" ) ,Operator.NOT , Operator.OR
			);
		found = convertToPostfix( input );
		Assert.assertTrue( expected.equals( found ) );
		
		//test expressions with constants (just to make sure it handles
		//objects of type ObjectFOL)
		// "True => True"
		tracker = new SymbolTracker();
		input = Arrays.asList( 
				BooleanFOL.True() , Operator.IMPLICATION , BooleanFOL.True()
			);
		expected = Arrays.asList(
				BooleanFOL.True() , BooleanFOL.True() , Operator.IMPLICATION
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
	}
	
	@Test
	public void testConverToPostfixWithQuantifiers() throws IOException {
		SymbolTracker tracker;
		List< Symbol > input;
		List< Symbol > expected;
		List< Symbol > found;
		
		//test expression with quantifiers
		// "FORALL x, y"	<=> "y FORALL(x)"
		tracker = new SymbolTracker();
		input = Arrays.asList( 
				Quantifier.FORALL , tracker.getNewVariable( "x" ) , Symbol.COMMA ,
				tracker.getNewVariable( "y" )
			);
		expected = Arrays.asList(
				tracker.getVariableByName( "y" ) , newQuantifierList( Quantifier.FORALL , tracker.getVariableByName( "x" ) )
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
		Assert.assertTrue( expected.equals( found ) );		
		
		// "FORALL(x, y) x AND y <=> y AND x"
		// <=> "x y AND y x AND <=> FORALL(x,y)"
		tracker = new SymbolTracker();
		input = Arrays.asList( 
				Quantifier.FORALL , Symbol.LEFT_PAREN , 
				tracker.getNewVariable( "x" ) , tracker.getNewVariable( "y" ) , 
				Symbol.RIGHT_PAREN , Symbol.COMMA ,
				tracker.getVariableByName( "x" ) , Operator.AND , tracker.getVariableByName( "y" ) ,
				Operator.BICONDITIONAL , tracker.getVariableByName( "y" ) , Operator.AND ,
				tracker.getVariableByName( "x" )
			);
		expected = Arrays.asList(
				tracker.getVariableByName( "x" ) , tracker.getVariableByName( "y" ) ,
				Operator.AND , tracker.getVariableByName( "y" ) , tracker.getVariableByName( "x" ) ,
				Operator.AND , Operator.BICONDITIONAL , newQuantifierList( Quantifier.FORALL , tracker.getVariableByName( "x" ) , tracker.getVariableByName( "y" ) )
			);
		found = convertToPostfix( input );
		Assert.assertTrue( expected.equals( found ) );
		
		//check that the algorithm distinguishes quantifiers from different
		//implication/biconditional expressions
		// "FORALL(x, y) x AND y <=> FORALL z, x AND y => z
		// <=> "x y AND x y AND z => FORALL(z) <=> FORALL(x,y)" 
		tracker = new SymbolTracker();
		input = Arrays.asList( 
				Quantifier.FORALL , Symbol.LEFT_PAREN , 
				tracker.getNewVariable( "x" ) , tracker.getNewVariable( "y" ) , 
				Symbol.RIGHT_PAREN , Symbol.COMMA ,
				tracker.getVariableByName( "x" ) , Operator.AND , tracker.getVariableByName( "y" ) ,
				Operator.BICONDITIONAL , Quantifier.FORALL , tracker.getNewVariable( "z" ) , Symbol.COMMA ,
				tracker.getVariableByName( "x" ) , Operator.AND , tracker.getVariableByName( "y" ) ,
				Operator.IMPLICATION , tracker.getVariableByName( "z" )
			);
		expected = Arrays.asList(
				tracker.getVariableByName( "x" ) , tracker.getVariableByName( "y" ) ,
				Operator.AND , tracker.getVariableByName( "x" ) , tracker.getVariableByName( "y" ) ,
				Operator.AND , tracker.getVariableByName( "z" ) , Operator.IMPLICATION , 
				newQuantifierList( Quantifier.FORALL , tracker.getVariableByName( "z" ) ) ,
				Operator.BICONDITIONAL ,
				newQuantifierList( Quantifier.FORALL , tracker.getVariableByName( "x" ) , tracker.getVariableByName( "y" ) )
			);
		found = convertToPostfix( input );
		Assert.assertTrue( expected.equals( found ) );
		
		// "FORALL(x, y), EXISTS z S.T. x AND y => z
		// <=> x y AND z => EXISTS z FORALL(x,y)
		tracker = new SymbolTracker();
		input = Arrays.asList( 
				Quantifier.FORALL , Symbol.LEFT_PAREN , 
				tracker.getNewVariable( "x" ) , tracker.getNewVariable( "y" ) , 
				Symbol.RIGHT_PAREN , Symbol.COMMA ,
				Quantifier.EXISTS , tracker.getNewVariable( "z" ) , Symbol.SUCH_THAT ,
				tracker.getVariableByName( "x" ) , Operator.AND , tracker.getVariableByName( "y" ) ,
				Operator.IMPLICATION , tracker.getVariableByName( "z" )
			);
		expected = Arrays.asList(
				tracker.getVariableByName( "x" ) , tracker.getVariableByName( "y" ) ,
				Operator.AND , tracker.getVariableByName( "z" ) , Operator.IMPLICATION ,
				newQuantifierList( Quantifier.EXISTS , tracker.getVariableByName( "z" ) ) ,
				newQuantifierList( Quantifier.FORALL , tracker.getVariableByName( "x" ) , tracker.getVariableByName( "y" ) )
			);
		found = convertToPostfix( input );
		Assert.assertTrue( expected.equals( found ) );
		
		// "FORALL(w, x, y), w => EXISTS z S.T. x AND y => z
		// <=> w x y AND z => EXISTS(z) => FORALL(w,x,y)
		tracker = new SymbolTracker();
		input = Arrays.asList( 
				Quantifier.FORALL , Symbol.LEFT_PAREN , tracker.getNewVariable( "w" ) ,
				tracker.getNewVariable( "x" ) , tracker.getNewVariable( "y" ) , 
				Symbol.RIGHT_PAREN , Symbol.COMMA , tracker.getVariableByName( "w" ) , Operator.IMPLICATION ,
				Quantifier.EXISTS , tracker.getNewVariable( "z" ) , Symbol.SUCH_THAT ,
				tracker.getVariableByName( "x" ) , Operator.AND , tracker.getVariableByName( "y" ) ,
				Operator.IMPLICATION , tracker.getVariableByName( "z" )
			);
		expected = Arrays.asList(
				tracker.getVariableByName( "w" ) , tracker.getVariableByName( "x" ) , tracker.getVariableByName( "y" ) ,
				Operator.AND , tracker.getVariableByName( "z" ) , Operator.IMPLICATION ,
				newQuantifierList( Quantifier.EXISTS , tracker.getVariableByName( "z" ) ) , Operator.IMPLICATION ,
				newQuantifierList( Quantifier.FORALL , tracker.getVariableByName( "w" ) , tracker.getVariableByName( "x" ) , tracker.getVariableByName( "y" ) )
			);
		found = convertToPostfix( input );
		Assert.assertTrue( expected.equals( found ) );
		
		//test expression with functions and quantifiers
		// "EXISTS(x,y) S.T. GreaterThan(SumInt( x , y ), DiffInt( x , y )) 
		// <=>  "x y SumInt x y DiffInt GreaterThan EXISTS(x,y)"
		tracker = SymbolTracker.fromDataFile( "test/mjchao/mazenav/logic/structures/integerworld.txt" , new IntegerWorld() );
		input = Arrays.asList( 
				Quantifier.EXISTS , Symbol.LEFT_PAREN , tracker.getNewVariable( "x" ) , Symbol.COMMA ,
				tracker.getNewVariable( "y" ) , Symbol.RIGHT_PAREN , Symbol.SUCH_THAT ,
				tracker.getRelation( "GreaterThan" ) , Symbol.LEFT_PAREN , tracker.getFunction( "SumInt" ) ,
				Symbol.LEFT_PAREN , tracker.getVariableByName( "x" ) , tracker.getVariableByName( "y" ) ,
				Symbol.RIGHT_PAREN , Symbol.COMMA , tracker.getFunction( "DiffInt" ) , Symbol.LEFT_PAREN ,
				tracker.getVariableByName( "x" ) , Symbol.COMMA , tracker.getVariableByName( "y" ) , Symbol.RIGHT_PAREN , Symbol.RIGHT_PAREN
			);
		expected = Arrays.asList(
				tracker.getVariableByName( "x" ) , tracker.getVariableByName( "y" ) ,
				tracker.getFunction( "SumInt" ) , 
				tracker.getVariableByName( "x" ) , tracker.getVariableByName( "y" ) ,
				tracker.getFunction( "DiffInt" ) , tracker.getRelation( "GreaterThan" ) ,
				newQuantifierList( Quantifier.EXISTS , tracker.getVariableByName( "x" ) , tracker.getVariableByName( "y" ) )
			);
		found = convertToPostfix( input );
		Assert.assertTrue( expected.equals( found ) );
			
		//test that scope of quantifiers can end
		//"(FORALL(a) EXISTS(x) x) AND (FORALL(b) EXISTS(y) y)		<=> 	[to postfix]
		//x EXISTS(x) FORALL(a) y EXISTS(y) FORALL(b) AND
		tracker = new SymbolTracker();
		input = Arrays.asList(
			Symbol.LEFT_PAREN , Quantifier.FORALL , Symbol.LEFT_PAREN , tracker.getNewVariable( "a" ) , Symbol.RIGHT_PAREN ,
			Quantifier.EXISTS , Symbol.LEFT_PAREN , tracker.getNewVariable( "x" ) , Symbol.RIGHT_PAREN , tracker.getVariableByName( "x" ) , Symbol.RIGHT_PAREN ,
			Operator.AND , Symbol.LEFT_PAREN , Quantifier.FORALL , Symbol.LEFT_PAREN , tracker.getNewVariable( "b" ) , Symbol.RIGHT_PAREN ,
			Quantifier.EXISTS , Symbol.LEFT_PAREN , tracker.getNewVariable( "y" ) , Symbol.RIGHT_PAREN , tracker.getVariableByName( "y" ) , Symbol.RIGHT_PAREN
			);
		expected = Arrays.asList(
			tracker.getVariableByName( "x" ) , newQuantifierList( Quantifier.EXISTS , tracker.getVariableByName( "x" ) ) ,
			newQuantifierList( Quantifier.FORALL , tracker.getVariableByName( "a" ) ) ,
			tracker.getVariableByName( "y" ) , newQuantifierList( Quantifier.EXISTS , tracker.getVariableByName( "y" ) ) ,
			newQuantifierList( Quantifier.FORALL , tracker.getVariableByName( "b" ) ) , Operator.AND
			);
		found = convertToPostfix( input );
		Assert.assertTrue( expected.equals( found ) );
	}
	
	/**
	 * Checks that the ExpressionTree is built correctly by assuring that
	 * the postfix generated from the built ExpressionTree is identical
	 * to the inputed postfix
	 * 
	 * @param inputPostfix		an expression in postfix
	 */
	public void testBuildTree( List< Symbol > inputPostfix ) {
		ExpressionTree exprTree = new ExpressionTree();
		setPostfix( exprTree , inputPostfix );
		buildTree( exprTree );
		ArrayList< Symbol > found = new ArrayList< Symbol >();
		buildPostfixFromExpressionTree( getRoot( exprTree ) , found );
		List< Symbol > expected = inputPostfix;
		Assert.assertTrue( expected.equals( found ) );
	}
	
	@Test
	public void testBuildTreeBAT1() {
		SymbolTracker tracker = new SymbolTracker();
		
		//test a basic expression "x OR y" 	<=>  "x y OR"
		List< Symbol > input = Arrays.asList(
				tracker.getNewVariable( "x" ) , tracker.getNewVariable( "y" ) , Operator.OR
			);
		testBuildTree( input );
	}
	
	@Test
	public void testBuildTreeBAT2() {
		SymbolTracker tracker = new SymbolTracker();
		
		//test a more complicated expression "x AND y OR z"		<=> 	[to postfix] 	
		//"x y AND z OR"
		//that leads to multiple tree levels
		List< Symbol > input = Arrays.asList(
				tracker.getNewVariable( "x" ) , tracker.getNewVariable( "y" ) , Operator.AND ,
				tracker.getNewVariable( "z" ) , Operator.OR
			);
		testBuildTree( input );
	}
	
	@Test
	public void testBuildTreeQuantifiers() {
		SymbolTracker tracker = new SymbolTracker();
		
		//test an expression with quantifiers and nested implications
		//"FORALL(w,x,y) w => EXISTS(z) x AND y => z"		<=> 	[to postfix] 
		//"w x y AND z => EXISTS(z) => FORALL(w,x,y)"
		List< Symbol > input = Arrays.asList(
				tracker.getNewVariable( "w" ) , tracker.getNewVariable( "x" ) , tracker.getNewVariable( "y" ) ,
				Operator.AND , tracker.getNewVariable( "z" ) , Operator.IMPLICATION ,
				newQuantifierList( Quantifier.EXISTS , tracker.getVariableByName( "z" ) ) , Operator.IMPLICATION ,
				newQuantifierList( Quantifier.FORALL , tracker.getVariableByName( "w" ) , tracker.getVariableByName( "x" ) , tracker.getVariableByName( "y" ) )
			);
		testBuildTree( input );
	}
	
	@Test
	public void testBuildTreeQuantifiers2() {
		SymbolTracker tracker = new SymbolTracker();
		
		//test an expression with two quantifiers in a row
		//"FORALL(x,y) EXISTS z z => x AND y"			<=>		[to postfix]
		// x y AND z => EXISTS z FORALL(x,y)
		List< Symbol > input = Arrays.asList(
				tracker.getNewVariable( "x" ) , tracker.getNewVariable( "y" ) ,
				Operator.AND , tracker.getNewVariable( "z" ) , Operator.IMPLICATION ,
				newQuantifierList( Quantifier.EXISTS , tracker.getVariableByName( "z" ) ) ,
				newQuantifierList( Quantifier.FORALL , tracker.getVariableByName( "x" ) , tracker.getVariableByName( "y" ) )
			);
		testBuildTree( input );
	}
	
	@Test
	public void testBuildTreeQuantifiers3() {
		SymbolTracker tracker = new SymbolTracker();
		
		//test that scope of quantifiers can end
		//"(FORALL(a,b) EXISTS(x) x) AND (FORALL(c,d) EXISTS(y) y)		<=> 	[to postfix]
		//x EXISTS(x) FORALL(a,b) y EXISTS(y) FORALL(c,d) AND
		List< Symbol > input = Arrays.asList(
				tracker.getNewVariable( "x" ) , newQuantifierList( Quantifier.EXISTS , tracker.getVariableByName( "x" ) ) ,
				newQuantifierList( Quantifier.FORALL , tracker.getVariableByName( "a" ) , tracker.getVariableByName( "b" ) ) ,
				tracker.getNewVariable( "y" ) , newQuantifierList( Quantifier.EXISTS , tracker.getVariableByName( "y" ) ) ,
				newQuantifierList( Quantifier.FORALL , tracker.getVariableByName( "c" ) , tracker.getVariableByName( "d" ) ) , Operator.AND
			);
		testBuildTree( input );
	}
	
	@Test
	public void testBuildTreeNots() {
		//test building a tree with NOT operators
		// FORALL(x,y,z) z => !x AND !y				<=>			[to postfix]
		// x NOT y NOT AND z => FORALL(x,y,z)
		SymbolTracker tracker = new SymbolTracker();
		List< Symbol > input = Arrays.asList(
				tracker.getNewVariable( "x" ) , Operator.NOT ,
				tracker.getNewVariable( "y" ) , Operator.NOT ,
				Operator.AND , tracker.getNewVariable( "z" ) ,
				Operator.IMPLICATION ,
				newQuantifierList( Quantifier.FORALL , tracker.getVariableByName( "x" ) ,
					tracker.getVariableByName( "y" ) , tracker.getVariableByName( "z" ) )
			);
		testBuildTree( input );
	}
	
	/**
	 * Verifies that the ExpressionTree correctly distributed NOT
	 * operators and eliminated arrows
	 * 
	 * @param inputPostfix		the input expression in postfix
	 * @param expectedPostfix	the expected postfix expression after distributing NOTs and eliminating arrows
	 */
	public void testDistributeNotsAndEliminateArrows( List< Symbol > inputPostfix , List< Symbol > expectedPostfix ) {
		ExpressionTree exprTree = new ExpressionTree();
		setPostfix( exprTree , inputPostfix );
		buildTree( exprTree );
		eliminateArrowsAndDistributeNots( exprTree );
		List< Symbol > found = new ArrayList< Symbol >();
		buildPostfixFromExpressionTree( getRoot(exprTree) , found );
		Assert.assertTrue( expectedPostfix.equals( found ) );
	}
	
	@Test
	public void testDistributeNotsBAT1() {
		SymbolTracker tracker = new SymbolTracker();
		
		//check that two NOTs eliminate each other:
		//[in infix]	"!!x"		<=> 		"x"
		//[in postfix] 	"x ! !"		<=> 		"x"
		List< Symbol > input = Arrays.asList(
				tracker.getNewVariable( "x" ) , Operator.NOT , Operator.NOT
			);
		List< Symbol > expected = Arrays.asList( tracker.getVariableByName( "x" ) );
		testDistributeNotsAndEliminateArrows( input , expected );
	}
	
	@Test
	public void testDistributeNotsBAT2() {
		SymbolTracker tracker = new SymbolTracker();
		
		//check that two of three NOTs eliminate each other:
		//[in infix]	"!!!x"		<=>			"!x"
		//[in postfix]	"x ! ! !"	<=>			"x !"
		List< Symbol > input = Arrays.asList(
				tracker.getNewVariable( "x" ) , Operator.NOT , Operator.NOT , Operator.NOT
			);		
		List< Symbol > expected = Arrays.asList( 
				tracker.getVariableByName( "x" ) , Operator.NOT 
			);
		testDistributeNotsAndEliminateArrows( input , expected );
	}
	
	@Test
	public void testDistributeNotsBAT3() {
		//test distributing nots on "x NOT NOT NOT NOT NOT NOT"  <=> "x"
		SymbolTracker tracker = new SymbolTracker();
		
		//test that many NOTs eliminate each other
		//[in infix]	"!!!!!!x"		<=> 		"x"
		//[in postfix]	"x!!!!!!"		<=>			"x"
		List< Symbol > input = Arrays.asList(
				tracker.getNewVariable( "x" ) , Operator.NOT , Operator.NOT , Operator.NOT ,
				Operator.NOT , Operator.NOT , Operator.NOT
			);		
		List< Symbol > expected = Arrays.asList( 
				tracker.getVariableByName( "x" )
			);
		testDistributeNotsAndEliminateArrows( input , expected );
	}
	
	@Test
	public void testDistributeNotsBAT4() {
		SymbolTracker tracker = new SymbolTracker();
		
		//test basic application of DeMorgan's Laws on OR
		//[in infix]		"!(x OR y)"		<=>		"!x AND !y"
		//[in postfix]		"x y OR !"		<=>		"x ! y! AND"
		List< Symbol > input = Arrays.asList(
				tracker.getNewVariable( "x" ) , tracker.getNewVariable( "y" ) , 
				Operator.OR , Operator.NOT
			);		
		List< Symbol > expected = Arrays.asList( 
				tracker.getVariableByName( "x" ) , Operator.NOT , 
				tracker.getVariableByName( "y" ) , Operator.NOT , Operator.AND
			);
		testDistributeNotsAndEliminateArrows( input , expected );
	}
	
	@Test
	public void testDistributeNotsBAT5() {
		SymbolTracker tracker = new SymbolTracker();
		
		//test basic application of DeMorgan's Laws on AND
		//[in infix]		"!(x AND y)"		<=>		"!x OR !y"
		//[in postfix]		"x y AND !"			<=>		"x ! y ! OR"
		List< Symbol > input = Arrays.asList(
				tracker.getNewVariable( "x" ) , tracker.getNewVariable( "y" ) , 
				Operator.OR , Operator.NOT
			);		
		List< Symbol > expected = Arrays.asList( 
				tracker.getVariableByName( "x" ) , Operator.NOT , 
				tracker.getVariableByName( "y" ) , Operator.NOT , Operator.AND
			);
		testDistributeNotsAndEliminateArrows( input , expected );
	}
	
	
	@Test
	public void testDistributeNotsRoot() {
		SymbolTracker tracker = new SymbolTracker();
		
		//check that NOTs are distributed correctly when the first
		//operator (the root) to be processed is not a NOT
		//[in infix] 	"!!!x OR y"		<=>		"!x OR y"
		//[in postfix]	"x ! ! ! y OR"	<=>		"x ! y OR"
		List< Symbol > input = Arrays.asList(
				tracker.getNewVariable( "x" ) , Operator.NOT , Operator.NOT , Operator.NOT ,
				tracker.getNewVariable( "y" ) , Operator.OR
			);		
		List< Symbol > expected = Arrays.asList( 
				tracker.getVariableByName( "x" ) , Operator.NOT , 
				tracker.getVariableByName( "y" ) , Operator.OR 
			);
		testDistributeNotsAndEliminateArrows( input , expected );
	}
	
	@Test
	public void testDistributeNotsRemovingFromParents() {
		//test that NOTs get distributed down the tree and then
		//removed from correct parents:
		//[in infix]		"!(x OR y) AND x"		<=>		"!x AND !y AND x"
		//[in postfix]		"x x y OR ! AND"		<=>		"x x ! y ! AND AND"
		SymbolTracker tracker = new SymbolTracker();
		List< Symbol > input = Arrays.asList(
				tracker.getNewVariable( "x" ) , tracker.getVariableByName( "x" ) , tracker.getNewVariable( "y" ) , 
				Operator.OR , Operator.NOT , Operator.AND
			);		
		List< Symbol > expected = Arrays.asList( 
				tracker.getVariableByName( "x" ) , tracker.getVariableByName( "x" ) , Operator.NOT , 
				tracker.getVariableByName( "y" ) , Operator.NOT , Operator.AND , Operator.AND
			);
		testDistributeNotsAndEliminateArrows( input , expected );
	}
	
	@Test
	public void testEliminateArrowsBAT1() {
		SymbolTracker tracker = new SymbolTracker();
		
		//test eliminating =>
		//[in infix]	"x => y"		<=>		"!x OR y"
		//[in postfix]	"x y =>"		<=>		"x ! y OR"
		List< Symbol > input = Arrays.asList(
				tracker.getNewVariable( "x" ) , tracker.getNewVariable( "y" ) ,
				Operator.IMPLICATION
			);		
		List< Symbol > expected = Arrays.asList( 
				tracker.getVariableByName( "x" ) , Operator.NOT ,
				tracker.getVariableByName( "y" ) , Operator.OR
			);
		testDistributeNotsAndEliminateArrows( input , expected );
	}
	
	@Test
	public void testEliminateArrowsBAT2() {
		SymbolTracker tracker = new SymbolTracker();
		
		//test eliminating <=>
		//[in infix]	"x <=> y"		<=>		"(!x OR y) AND (!y OR x)"
		//[in postfix]	"x y <=>"		<=>		"x ! y OR y ! x OR AND
		List< Symbol > input = Arrays.asList(
				tracker.getNewVariable( "x" ) , tracker.getNewVariable( "y" ) ,
				Operator.BICONDITIONAL
			);		
		List< Symbol > expected = Arrays.asList( 
				tracker.getVariableByName( "x" ) , Operator.NOT ,
				tracker.getVariableByName( "y" ) , Operator.OR ,
				tracker.getVariableByName( "y" ) , Operator.NOT ,
				tracker.getVariableByName( "x" ) , Operator.OR , Operator.AND
			);
		testDistributeNotsAndEliminateArrows( input , expected );
	}
	
	@Test
	public void testDistributeNotsAndEliminateArrowsBAT1() {
		SymbolTracker tracker = new SymbolTracker();

		//test eliminating a => and then distributing a NOT
		//[in infix] 	"!(x => y)"		<=>		"!(!x OR y)"	<=>		"x AND !y"
		//[in postfix]	"x y <=> !"		<=>								"x y ! AND"
		List< Symbol > input = Arrays.asList(
				tracker.getNewVariable( "x" ) , tracker.getNewVariable( "y" ) ,
				Operator.IMPLICATION , Operator.NOT
			);		
		List< Symbol > expected = Arrays.asList( 
				tracker.getVariableByName( "x" ) , tracker.getVariableByName( "y" ) ,
				Operator.NOT , Operator.AND
			);
		testDistributeNotsAndEliminateArrows( input , expected );
	}
	
	@Test
	public void testDistributeNotsAndEliminateArrowsBAT2() {
		//test distributing nots and eliminating arrows on "x y <=> NOT"
		// "!(x <=> y)"    <=>    "!((!x OR y) AND (!y OR x))"     <=>      
		// "!(!x OR y) OR !(!y OR x)"		<=> 	"(x AND !y) OR (y AND !x)
		//in postfix, this is "x y NOT AND y x NOT AND OR"
		SymbolTracker tracker = new SymbolTracker();
		
		//test eliminating a <=> and then distributing a NOT
		//[in infix]	"!(x <=> y)"	<=>		"!((!x OR y) AND (!y OR x))"	<=>
		//				"!(!x OR y) OR !(!y OR x)"		<=> 	"(x AND !y) OR (y AND !x)"
		//[in postfix]	"x y <=> !"		<=>		"x y ! AND y x ! AND OR"
		List< Symbol > input = Arrays.asList(
				tracker.getNewVariable( "x" ) , tracker.getNewVariable( "y" ) ,
				Operator.BICONDITIONAL , Operator.NOT
			);		
		List< Symbol > expected = Arrays.asList( 
				tracker.getVariableByName( "x" ) , tracker.getVariableByName( "y" ) ,
				Operator.NOT , Operator.AND , 
				tracker.getVariableByName( "y" ) , tracker.getVariableByName( "x" ) ,
				Operator.NOT , Operator.AND , Operator.OR 
			);
		testDistributeNotsAndEliminateArrows( input , expected );
	}
	
	@Test
	public void testDistributeNotsAndEliminateArrowsNested() {
		//test distributing nots and eliminating arrows on "x y => z =>"
		// "(x => y) => z"	<=> 	"(!x OR y) => z" 		<=> 	
		// "!(!x OR y) OR z"	<=>		"x AND !y OR z
		//in postfix this is "x y NOT AND z OR
		SymbolTracker tracker = new SymbolTracker();
		
		//test eliminating nested arrows, which leads to dealing with
		//distributing NOTs over several levels of the expression tree
		//[in infix]	"(x => y) => z"		<=>		"(!x OR y)	=> z"		<=>
		//				"!(!x OR y) OR z"	<=>		"(x AND !y) OR z"
		//[in postfix]	"x y => z =>"		<=>		"x y ! AND z OR"
		List< Symbol > input = Arrays.asList(
				tracker.getNewVariable( "x" ) , tracker.getNewVariable( "y" ) ,
				Operator.IMPLICATION , tracker.getNewVariable( "z" ) , Operator.IMPLICATION
			);		
		List< Symbol > expected = Arrays.asList( 
				tracker.getVariableByName( "x" ) ,
				tracker.getVariableByName( "y" ) , Operator.NOT , Operator.AND ,
				tracker.getVariableByName( "z" ) , Operator.OR
			);
		testDistributeNotsAndEliminateArrows( input , expected );
	}
	
	@Test
	public void testDistributeNotsAndEliminateArrowsQuantifiers() {
		//test distributing nots over quantifiers and arrows on 
		//"x y => FORALL(x,y) !"
		//"!(FORALL(x,y) x => y)" 	<=>		"EXISTS(x,y) !(x => y)"		<=>
		//"EXISTS(x,y) !(!x OR y)"	<=>		"EXISTS(x,y) x AND !y
		//in postfix this is "x y NOT AND EXISTS(x,y)"
		SymbolTracker tracker = new SymbolTracker();
		
		//test distributing a NOT over a quantified implication statement.
		//this requires converting FORALL into EXISTS and multiple NOT distributions
		//[in infix]	"!(FORALL(x,y) x => y)" 	<=>		"EXISTS(x,y) !(x => y)"		<=>
		//				"EXISTS(x,y) !(!x OR y)"	<=>		"EXISTS(x,y) x AND !y"
		//[in postfix]	"x y => FORALL(x,y) !"		<=>		"x y ! AND EXISTS(x,y)"
		List< Symbol > input = Arrays.asList(
				tracker.getNewVariable( "x" ) , tracker.getNewVariable( "y" ) ,
				Operator.IMPLICATION , 
				newQuantifierList( Quantifier.FORALL , tracker.getVariableByName( "x" ) , tracker.getVariableByName( "y" ) ) ,
				Operator.NOT
			);		
		List< Symbol > expected = Arrays.asList( 
				tracker.getVariableByName( "x" ) , tracker.getVariableByName( "y" ) ,
				Operator.NOT , Operator.AND ,
				newQuantifierList( Quantifier.EXISTS , tracker.getVariableByName( "x" ) , tracker.getVariableByName( "y" ) )
			);
		testDistributeNotsAndEliminateArrows( input , expected );
	}
	
	@Test
	public void testDistributeNotsAndEliminateArrowsQuantifiers2() {
		//test distributing nots over two quantifiers and arrows
		//"x y => FORALL(x,y) w z => EXISTS(w,z) => !"
		//!((FORALL(x,y) x => y) => EXISTS(w,z) w => z)			<=>
		//!(!(FORALL(x,y) x => y) OR (EXISTS(w,z) w => z))		<=>
		//FORALL(x,y) x => y AND !(EXISTS(w,z) w => z) 			<=>
		//FORALL(x,y) x => y AND FORALL(w,z) !(w => z)			<=>
		//(FORALL(x,y) !x OR y) AND (FORALL(w,z) w AND !z)
		
		//in postfix, this is
		// x NOT y OR FORALL(x,y) w z NOT AND FORALL(w,z) AND 
		SymbolTracker tracker = new SymbolTracker();
		
		//test distributing NOT over two quantified implications.
		//this tests if the tree can handle distributing NOTs
		//over many levels
		//[in infix]	"!((FORALL(x,y) x => y) => EXISTS(w,z) w => z)"			<=>
		//				"!(!(FORALL(x,y) x => y) OR (EXISTS(w,z) w => z))"		<=>
		//				"FORALL(x,y) x => y AND !(EXISTS(w,z) w => z)"			<=>
		//				"FORALL(x,y) x => y AND FORALL(w,z) !(w => z)"			<=>
		//				"(FORALL(x,y) !x OR y) AND (FORALL(w,z) w AND !z)"
		
		//[in postfix]	"x y => FORALL(x,y) w z => EXISTS(w,z) => !"			<=>
		//				"x ! y OR FORALL(x,y) w z ! AND FORALL(w,z) AND"
		List< Symbol > input = Arrays.asList(
				tracker.getNewVariable( "x" ) , tracker.getNewVariable( "y" ) ,
				Operator.IMPLICATION ,
				newQuantifierList( Quantifier.FORALL , tracker.getVariableByName( "x" ) , tracker.getVariableByName( "y" ) ) ,
				tracker.getNewVariable( "w" ) , tracker.getNewVariable( "z" ) ,
				Operator.IMPLICATION ,
				newQuantifierList( Quantifier.EXISTS , tracker.getVariableByName( "w" ) , tracker.getVariableByName( "z" ) ) ,
				Operator.IMPLICATION , Operator.NOT
			);		
		List< Symbol > expected = Arrays.asList( 
				tracker.getVariableByName( "x" ) , Operator.NOT , tracker.getVariableByName( "y" ) ,
				Operator.OR , newQuantifierList( Quantifier.FORALL , tracker.getVariableByName( "x" ) , tracker.getVariableByName( "y" ) ) ,
				tracker.getVariableByName( "w" ) , tracker.getVariableByName( "z" ) , Operator.NOT , 
				Operator.AND , newQuantifierList( Quantifier.FORALL , tracker.getVariableByName( "w" ) , tracker.getVariableByName( "z" ) ) ,
				Operator.AND
			);
		testDistributeNotsAndEliminateArrows( input , expected );
	}
	
	@Test
	public void testDistributeNotsFunctions() throws IOException {
		SymbolTracker tracker = SymbolTracker.fromDataFile( "test/mjchao/mazenav/logic/structures/integerworld.txt" , new IntegerWorld() );
		
		//test distributing nots with functions
		//it is important that we do not distribute NOTs into a function
		//[in infix]	"FORALL(y) !EXISTS(x) GreaterThan(SumInt(y,y), DiffInt(x,x))"	<=>
		// 				"FORALL(y) FORALL(x) !GreaterThan(SumInt(y,y), DiffInt(x,x))"
		//[in postfix]	"y y SumInt x x DiffInt GreaterThan EXISTS(x) ! FORALL(y)"		<=>
		//				"y y SumInt x x DiffInt GreaterThan ! FORALL(x) FORALL(y)"
		
		List< Symbol > input = Arrays.asList(
				tracker.getNewVariable( "y" ) , tracker.getVariableByName( "y" ) , tracker.getFunction( "SumInt" ) ,
				tracker.getNewVariable( "x" ) , tracker.getVariableByName( "x" ) , tracker.getFunction( "DiffInt" ) ,
				tracker.getRelation( "GreaterThan" ) , 
				newQuantifierList( Quantifier.EXISTS , tracker.getVariableByName( "x" ) ) , Operator.NOT ,
				newQuantifierList( Quantifier.FORALL , tracker.getVariableByName( "y" ) )
			);		
		List< Symbol > expected = Arrays.asList( 
				tracker.getVariableByName( "y" ) , tracker.getVariableByName( "y" ) , tracker.getFunction( "SumInt" ) ,
				tracker.getVariableByName( "x" ) , tracker.getVariableByName( "x" ) , tracker.getFunction( "DiffInt" ) ,
				tracker.getRelation( "GreaterThan" ) ,  Operator.NOT ,
				newQuantifierList( Quantifier.FORALL , tracker.getVariableByName( "x" ) ) ,
				newQuantifierList( Quantifier.FORALL , tracker.getVariableByName( "y" ) )				
			);
		testDistributeNotsAndEliminateArrows( input , expected );
	}
	
	/**
	 * Verifies that the ExpressionTree correctly standardized
	 * and expression. Assumes that the buildTree and eliminateArrowsAndDistributeNots()
	 * functions have been implemented correctly.
	 * 
	 * @param tracker			keeps track of variable names. currently unused, but may be
	 * 							necessary in the future for more specific equality checking
	 * @param inputPostfix		the expression in postfix to standardize 
	 * @param expectedPostfix	the expected standardized result in postfix
	 */
	public void testStandardize( SymbolTracker tracker , List< Symbol > inputPostfix , List< Symbol > expectedPostfix ) {
		ExpressionTree exprTree = new ExpressionTree();
		setPostfix( exprTree , inputPostfix );
		buildTree( exprTree );
		eliminateArrowsAndDistributeNots( exprTree );
		standardize( exprTree , tracker );
		List< Symbol > found = new ArrayList< Symbol >();
		buildPostfixFromExpressionTree( getRoot(exprTree) , found );
		Assert.assertTrue( expectedPostfix.equals( found ) );
	}
	
	private Variable getMockSystemVariableById( int id ) {
		return new Variable( "?" + id , id );
	}
	
	@Test
	public void testStandardizeBAT1() {
		SymbolTracker tracker = new SymbolTracker();
		
		//test that a variable gets standardized to a system variable
		//[in infix]		"x"		<=>		"?0"
		//[in postfix]		"x"		<=>		"?0"
		List< Symbol > input = Arrays.asList(
				tracker.getNewVariable( "x" )
			);		
		List< Symbol > expected = Arrays.asList( getMockSystemVariableById( 0 ) );
		testStandardize( tracker , input , expected );
	}
	
	@Test
	public void testStandardizeBAT2() {
		SymbolTracker tracker = new SymbolTracker();
		
		//test that two variables get standardized
		//[in infix]	"x AND y"	<=>		"?0 AND ?1"
		//[in postfix]	"x y AND"	<=>		"?0 ?1 AND"
		List< Symbol > input = Arrays.asList(
				tracker.getNewVariable( "x" ) , tracker.getNewVariable( "y" ) ,  Operator.AND
			);		
		List< Symbol > expected = Arrays.asList( 
				getMockSystemVariableById( 0 ) , getMockSystemVariableById( 1 ) , Operator.AND );
		testStandardize( tracker , input , expected );
	}
	
	@Test
	public void testStandardizeBAT3() {
		//test standardizing "x AND y AND z"	<=> "x y AND z AND"
		//which should yield "?0 ?1 AND ?2 AND
		SymbolTracker tracker = new SymbolTracker();
		
		//test standardizing 3 variables where
		//[in infix]		"x AND y AND z"		<=>		"?0 AND ?1 AND ?2"
		//[in postfix]		"x y AND z AND"		<=>		"?0 ?1 AND ?2"
		List< Symbol > input = Arrays.asList(
				tracker.getNewVariable( "x" ) , tracker.getNewVariable( "y" ) ,  Operator.AND , 
				tracker.getNewVariable( "z" ) , Operator.AND 
			);		
		List< Symbol > expected = Arrays.asList( 
				getMockSystemVariableById( 0 ) , getMockSystemVariableById( 1 ) , Operator.AND ,
				getMockSystemVariableById( 2 ) , Operator.AND );
		testStandardize( tracker , input , expected );
	}
	
	@Test
	public void testStandardizeQuantifiers() {
		SymbolTracker tracker = new SymbolTracker();
		
		//test that single variable quantifiers get standardized
		//and variables shadow each other appropriately.
		//[in infix]		"FORALL x x => FORALL y y => FORALL x x" 		<=> 
		//					"!(FORALL x x => FORALL y y) OR FORALL x x"		<=>
		//					"!(EXISTS x !x OR FORALL y y) OR FORALL x x"	<=>
		//					"FORALL x x AND EXISTS y !y OR FORALL x x"		<=>
		//					FORALL ?0 ?0 AND EXISTS ?1 !?1 OR FORALL ?2 ?2
		//
		//[in postfix]		"x FORALL(x) y FORALL(y) => x FORALL(x)"		<=>
		//					"?0 FORALL(?0) ?1 ! EXISTS(?1) AND ?2 FORALL(?2) OR
		List< Symbol > input = Arrays.asList(
				tracker.getNewVariable( "x" ) , newQuantifierList( Quantifier.FORALL , tracker.getVariableByName( "x" ) ) ,
				tracker.getNewVariable( "y" ) , newQuantifierList( Quantifier.FORALL , tracker.getVariableByName( "y" ) ) ,
				Operator.IMPLICATION , 
				tracker.getVariableByName( "x" ) , newQuantifierList( Quantifier.FORALL , tracker.getVariableByName( "x" ) ) ,
				Operator.IMPLICATION
			);		
		List< Symbol > expected = Arrays.asList( 
				getMockSystemVariableById( 0 ) , newQuantifierList( Quantifier.FORALL , getMockSystemVariableById( 0 ) ) ,
				getMockSystemVariableById( 1 ) , Operator.NOT , newQuantifierList( Quantifier.EXISTS , getMockSystemVariableById( 1 ) ) ,
				Operator.AND , 
				getMockSystemVariableById( 2 ) , newQuantifierList( Quantifier.FORALL , getMockSystemVariableById( 2 ) ) ,
				Operator.OR
				);
		testStandardize( tracker , input , expected );
	}
	
	@Test
	public void testStandardizeQuantifiers2() {
		SymbolTracker tracker = new SymbolTracker();
		
		//check that multi-variable quantifiers get standardized
		//and variables that shadow others get standardized appropriately
		//[in infix]	"FORALL(x,y,z) x AND y AND z AND FORALL(x,y) x AND y AND z" 	<=>
		//				"FORALL(?0,?1,?2) ?0 AND ?1 AND ?2 AND FORALL(?3,?4) ?3 AND ?4 AND ?5
		//
		//[in postfix]	"x y AND z AND x y AND z AND FORALL(x,y) AND FORALL(x,y,z)"	<=>
		//				"?0 ?1 AND ?2 AND ?3 ?4 AND ?2 AND FORALL(?3,?4) AND FORALL(?0,?1,?2)"
		List< Symbol > input = Arrays.asList(
				tracker.getNewVariable( "x" ) , tracker.getNewVariable( "y" ) , Operator.AND ,
				tracker.getNewVariable( "z" ) , Operator.AND , 
				tracker.getVariableByName( "x" ) , tracker.getVariableByName( "y" ) , Operator.AND , 
				tracker.getVariableByName( "z" ) , Operator.AND ,
				newQuantifierList( Quantifier.FORALL , tracker.getVariableByName( "x" ) , tracker.getVariableByName( "y" ) ) ,
				Operator.AND ,
				newQuantifierList( Quantifier.FORALL , tracker.getVariableByName( "x" ) , tracker.getVariableByName( "y" ) , tracker.getVariableByName( "z" ) )
			);		
		List< Symbol > expected = Arrays.asList( 
				getMockSystemVariableById( 0 ) , getMockSystemVariableById( 1 ) , Operator.AND ,
				getMockSystemVariableById( 2 ) , Operator.AND ,
				getMockSystemVariableById( 3 ) , getMockSystemVariableById( 4 ) , Operator.AND ,
				getMockSystemVariableById( 2 ) , Operator.AND ,
				newQuantifierList( Quantifier.FORALL , getMockSystemVariableById( 3 ) , getMockSystemVariableById( 4 ) ) ,
				Operator.AND , newQuantifierList( Quantifier.FORALL , getMockSystemVariableById( 0 ) , getMockSystemVariableById( 1 ) , getMockSystemVariableById( 2 ) )
				);
		testStandardize( tracker , input , expected );
	}
	
	@Test
	public void testStandardizeQuantifiers3() {
		SymbolTracker tracker = new SymbolTracker();
		
		//test standardizing with two different quantifiers. Should not be much
		//of a difference from using two of the same quantifier.
		//[in infix]	"FORALL(x,y) x AND y AND EXISTS(x,y,z) x OR y OR z"		<=>
		//				"FORALL(?0,?1) ?0 AND ?1 AND EXISTS(?2,?3,?4) ?2 OR ?3 OR ?4
		//[in postfix]	"x y AND x y OR z OR EXISTS(x,y,z) AND FORALL(x,y)"		<=>
		//				"?0 ?1 AND ?2 ?3 OR ?4 OR EXISTS(?2,?3,?4) AND FORALL(?0,?1)"
		List< Symbol > input = Arrays.asList(
				tracker.getNewVariable( "x" ) , tracker.getNewVariable( "y" ) , Operator.AND ,
				tracker.getVariableByName( "x" ) , tracker.getVariableByName( "y" ) , Operator.OR ,
				tracker.getNewVariable( "z" ) , Operator.OR ,
				newQuantifierList( Quantifier.EXISTS , tracker.getVariableByName( "x" ) , tracker.getVariableByName( "y" ) , tracker.getVariableByName( "z" ) ) ,
				Operator.AND , 
				newQuantifierList( Quantifier.FORALL , tracker.getVariableByName( "x" ) , tracker.getVariableByName( "y" ) )
			);		
		List< Symbol > expected = Arrays.asList( 
				getMockSystemVariableById( 0 ) , getMockSystemVariableById( 1 ) , Operator.AND ,
				getMockSystemVariableById( 2 ) , getMockSystemVariableById( 3 ) , Operator.OR ,
				getMockSystemVariableById( 4 ) , Operator.OR ,
				newQuantifierList( Quantifier.EXISTS , getMockSystemVariableById( 2 ) , getMockSystemVariableById( 3 ) , getMockSystemVariableById( 4 ) ) ,
				Operator.AND ,
				newQuantifierList( Quantifier.FORALL , getMockSystemVariableById( 0 ) , getMockSystemVariableById( 1 ) )
				);		
		testStandardize( tracker , input , expected );
	}
	
	@Test
	public void testStandardizeFunctionArguments() throws IOException {
		//test standardizing arguments to functions
		// FORALL(x) GreaterThan(x,x) AND FORALL(x) GreaterThan(x,x) OR EXISTS(x) GreaterThan(SumInt(x,x), DiffInt(x,x))
		//in postfix this is equivalent to
		// x x GreaterThan x x SumInt x x DiffInt GreaterThan EXISTS(x) x x GreaterThan FORALL(x) OR AND FORALL(x)
		//and this standardizes to
		// ?0 ?0 GreaterThan ?1 ?1 SumInt ?1 ?1 DiffInt GreaterThan EXISTS(?1) ?2 ?2 GreaterThan FORALL(?2) OR AND FORALL(?0) 
		SymbolTracker tracker = SymbolTracker.fromDataFile( "test/mjchao/mazenav/logic/structures/integerworld.txt" , new IntegerWorld() );
		
		//test standardizing arguments to functions
		//[in infix]	"FORALL(x) GreaterThan(x,x) AND (EXISTS(x) GreaterThan(SumInt(x,x), DiffInt(x,x)) OR FORALL(x) GreaterThan(x,x))"		<=>
		//				"FORALL(?0) GreaterThan(?0,?0) AND (EXISTS(?1) GreaterThan(SumInt(?1,?1), DiffInt(?1,?1)) OR FORALL(?2) GreaterThan(?2,?2))"
		//
		//[in postfix]	"x x GreaterThan x x SumInt x x DiffInt GreaterThan EXISTS(x) x x GreaterThan FORALL(x) OR AND FORALL(x)"				<=>
		//				"?0 ?0 GreaterThan ?1 ?1 SumInt ?1 ?1 DiffInt GreaterThan EXISTS(?1) ?2 ?2 GreaterThan FORALL(?2) OR AND FORALL (?0)
		List< Symbol > input = Arrays.asList(
				tracker.getNewVariable( "x" ) , tracker.getVariableByName( "x" ) , tracker.getRelation( "GreaterThan" ) ,
				tracker.getVariableByName( "x" ) , tracker.getVariableByName( "x" ) , tracker.getFunction( "SumInt" ) ,
				tracker.getVariableByName( "x" ) , tracker.getVariableByName( "x" ) , tracker.getFunction( "DiffInt" ) ,
				tracker.getRelation( "GreaterThan" ) , newQuantifierList( Quantifier.EXISTS , tracker.getVariableByName( "x" ) ) ,
				tracker.getVariableByName( "x" ) , tracker.getVariableByName( "x" ) , tracker.getRelation( "GreaterThan" ) ,
				newQuantifierList( Quantifier.FORALL , tracker.getVariableByName( "x" ) ) , Operator.OR , Operator.AND ,
				newQuantifierList( Quantifier.FORALL , tracker.getVariableByName( "x" ) )
			);		
		List< Symbol > expected = Arrays.asList( 
				getMockSystemVariableById( 0 ) , getMockSystemVariableById( 0 ) , tracker.getRelation( "GreaterThan" ) ,
				getMockSystemVariableById( 1 ) , getMockSystemVariableById( 1 ) , tracker.getFunction( "SumInt" ) ,
				getMockSystemVariableById( 1 ) , getMockSystemVariableById( 1 ) , tracker.getFunction( "DiffInt" ) ,
				tracker.getRelation( "GreaterThan" ) , newQuantifierList( Quantifier.EXISTS , getMockSystemVariableById( 1 ) ) ,
				getMockSystemVariableById( 2 ) , getMockSystemVariableById( 2 ) , tracker.getRelation( "GreaterThan" ) ,
				newQuantifierList( Quantifier.FORALL , getMockSystemVariableById( 2 ) ) , Operator.OR , Operator.AND ,
				newQuantifierList( Quantifier.FORALL , getMockSystemVariableById( 0 ) )
			);
		testStandardize( tracker , input , expected );
	}
	
	@Test
	public void testStandardizeScopeErrors() throws IOException {
		//test for potential scope errors:
		//EXISTS(x) (EXISTS(x) x) AND x
		//in postfix, this is equivalent to
		// x EXISTS(x) x AND EXISTS(x)
		//which should yield the following when standardized:
		// ?1 EXISTS(?1) ?0 AND EXISTS(?0)
		SymbolTracker tracker = new SymbolTracker();
		
		//test for potential scope errors where a mapping of a user variable
		//to a system variable needs to be "undone"
		//		[EXISTS x]
		//	  /  	      \
		//  [EXISTS x]	  [x]
		//	...
		
		//[in infix]	"EXISTS(x) (EXISTS(x) x) AND x"			<=>
		//				"EXISTS(?0) (EXISTS(?1) ?1) AND ?0"
		//[in postfix]	"x EXISTS(x) x AND EXISTS(x)			<=>
		//				"?1 EXISTS(?1) ?0 AND EXISTS(?0)"
		//
		List< Symbol > input = Arrays.asList(
				tracker.getNewVariable( "x" ) , newQuantifierList( Quantifier.EXISTS , tracker.getVariableByName( "x" ) ) ,
				tracker.getVariableByName( "x" ) , Operator.AND ,
				newQuantifierList( Quantifier.EXISTS , tracker.getVariableByName( "x" ) )
			);		
		List< Symbol > expected = Arrays.asList( 
				getMockSystemVariableById( 1 ) , newQuantifierList( Quantifier.EXISTS , getMockSystemVariableById( 1 ) ) ,
				getMockSystemVariableById( 0 ) , Operator.AND ,
				newQuantifierList( Quantifier.EXISTS , getMockSystemVariableById( 0 ) )
			);	
		testStandardize( tracker , input , expected );
	}
	
	/**
	 * Verifies that the ExpressionTree correctly skolemized existential
	 * quantifiers and dropped quantifiers. Assumes that buildTree(),
	 * eliminateArrowsAndDistribtueNots() and standardize() have been
	 * implemented correctly.
	 * 
	 * @param tracker			keeps track of system variables and skolem functions
	 * @param inputPostfix		an expression in postfix to skolemize and drop quantifiers			
	 * @param expectedPostfix	the expression in postfix after skolemizing and
	 * 							dropping quantifiers
	 */
	public void testSkolemizeAndDropQuantifiers( SymbolTracker tracker , List< Symbol > inputPostfix , List< Symbol > expectedPostfix ) {
		ExpressionTree exprTree = new ExpressionTree();
		setPostfix( exprTree , inputPostfix );
		buildTree( exprTree );
		eliminateArrowsAndDistributeNots( exprTree );
		standardize( exprTree , tracker );
		skolemize( exprTree , tracker );
		dropQuantifiers( exprTree , tracker );
		List< Symbol > found = new ArrayList< Symbol >();
		buildPostfixFromExpressionTree( getRoot(exprTree) , found );
		Assert.assertTrue( expectedPostfix.equals( found ) );
	}
	
	@Test
	public void testSkolemizeAndDropQuantifiersBAT1() {
		SymbolTracker tracker = new SymbolTracker();
		
		//test no skolemization necessary: "x AND y AND z"
		//[in infix]			"x AND y AND z"			<=>		"?0 AND ?1 AND ?2"
		//[in postfix]			"x y AND z AND"			<=>		"?0 ?1 AND ?2 AND"
		List< Symbol > input = Arrays.asList(
				tracker.getNewVariable( "x" ) , tracker.getNewVariable( "y" ) , Operator.AND ,
				tracker.getNewVariable( "z" ) , Operator.AND
			);		
		List< Symbol > expected = Arrays.asList( 
				getMockSystemVariableById( 0 ) , getMockSystemVariableById( 1 ) , Operator.AND ,
				getMockSystemVariableById( 2 ) , Operator.AND
				);
		testSkolemizeAndDropQuantifiers( tracker , input , expected );
	}
	
	@Test
	public void testSkolemizeAndDropQuantifiersBAT2() {
		//test skolemizing "EXISTS(x) x"
		//should yield "$0" after skolemizing and dropping quantifiers
		SymbolTracker tracker = new SymbolTracker();
		
		//test skolemizing a single existential quantifier
		//[in infix]		"EXISTS(x) x"		<=>		"$0()"
		//[in postfix]		"x EXISTS(x)"		<=>		"$0()"
		List< Symbol > input = Arrays.asList(
				tracker.getNewVariable( "x" ) , newQuantifierList( Quantifier.EXISTS , tracker.getVariableByName( "x" ) )
			);		
		List< Symbol > expected = Arrays.asList( 
				new SkolemFunction( 0 )
				);
		testSkolemizeAndDropQuantifiers( tracker , input , expected );
	}
	
	@Test
	public void testSkolemizeAndDropQuantifiersBAT3() {
		SymbolTracker tracker = new SymbolTracker();
		
		//test skolemizing two existential quantifiers
		//[in infix]	"EXISTS(x) x AND EXISTS(x) x"		<=>		"$0() AND $1()"
		//[in postfix]	"x EXISTS(x) x EXISTS(x) AND"		<=>		"$0() $1() AND"
		List< Symbol > input = Arrays.asList(
				tracker.getNewVariable( "x" ) , newQuantifierList( Quantifier.EXISTS , tracker.getVariableByName( "x" ) ) ,
				tracker.getVariableByName( "x" ) , newQuantifierList( Quantifier.EXISTS , tracker.getVariableByName( "x" ) ) ,
				Operator.AND 
			);		
		List< Symbol > expected = Arrays.asList( 
				new SkolemFunction( 0 ) , new SkolemFunction( 1 ) , Operator.AND
				);
		testSkolemizeAndDropQuantifiers( tracker , input , expected );
	}
	
	@Test
	public void testSkolemizeAndDropQuantifiersBAT4() {
		//test skolemizing "FORALL(x) EXISTS(x) x AND EXISTS(x) x"
		//should yield "$0(?0) $1(?0) AND" in postfix after skolemizing
		//and dropping quantifiers
		SymbolTracker tracker = new SymbolTracker();
		
		//test skolemizing existential quantifiers in the scope of a universal
		//quantifier
		//[in infix]	"FORALL(x) EXISTS(x) x AND EXISTS(x) x"			<=>
		//				"FORALL(?0) EXISTS(?1) ?1 AND EXISTS(?2) ?2"	<=>
		//				"$0(?0) AND $1(?0)"
		//[in postfix]	"x EXISTS(x) x EXISTS(x) AND FORALL(x)"			<=>
		//				"$0(?0) $1(?0) AND"
		List< Symbol > input = Arrays.asList(
				tracker.getNewVariable( "x" ) , newQuantifierList( Quantifier.EXISTS , tracker.getVariableByName( "x" ) ) ,
				tracker.getVariableByName( "x" ) , newQuantifierList( Quantifier.EXISTS , tracker.getVariableByName( "x" ) ) ,
				Operator.AND , newQuantifierList( Quantifier.FORALL , tracker.getVariableByName( "x" ) )
			);		
		List< Symbol > expected = Arrays.asList( 
				new SkolemFunction( 0 , getMockSystemVariableById( 0 ) ) , new SkolemFunction( 1 , getMockSystemVariableById( 0 ) ) , Operator.AND
				);
		testSkolemizeAndDropQuantifiers( tracker , input , expected );
	}
	
	@Test
	public void testSkolemizeAndDropQuantifiersUniversalWithExistential1() {
		//test skolemizing "FORALL(x,y) x AND y AND EXISTS(x,y,z) x OR y OR z"
		//which in postfix is "x y AND x y OR z OR EXISTS(x,y,z) AND FORALL(x,y)"
		//should yield "?0 ?1 AND $0(?0,?1) $1(?0,?1) OR $2(?0,?1) OR AND
		SymbolTracker tracker = new SymbolTracker();
		
		//test skolemizing existentially quantifier variables mixed in with
		//universally quantified variables
		//[in infix]	"FORALL(x,y) x AND y AND EXISTS(x,y,z) x OR y OR z"				<=>	
		//				"FORALL(?0,?1) ?0 AND ?1 AND EXISTS(?2,?3,?4) ?2 OR ?3 OR ?4"	<=>
		//				"?0 AND ?1 AND $0(?0,?1) OR $1(?0,?1) OR $2(?0,?1)
		//[in postfix]  "x y AND x y OR z OR EXISTS(x,y,z) AND FORALL(x,y)"		<=>
		//				"?0 ?1 AND $0(?0,?1) $1(?0,?1) OR $2(?0,?1) OR AND"
		
		List< Symbol > input = Arrays.asList(
				tracker.getNewVariable( "x" ) , tracker.getNewVariable( "y" ) , Operator.AND ,
				tracker.getVariableByName( "x" ) , tracker.getVariableByName( "y" ) , Operator.OR ,
				tracker.getNewVariable( "z" ) , Operator.OR ,
				newQuantifierList( Quantifier.EXISTS , tracker.getVariableByName( "x" ) , tracker.getVariableByName( "y" ) , tracker.getVariableByName( "z" ) ) ,
				Operator.AND , 
				newQuantifierList( Quantifier.FORALL , tracker.getVariableByName( "x" ) , tracker.getVariableByName( "y" ) )
			);		
		List< Symbol > expected = Arrays.asList( 
				getMockSystemVariableById( 0 ) , getMockSystemVariableById( 1 ) , Operator.AND ,
				new SkolemFunction( 0 , getMockSystemVariableById( 0 ) , getMockSystemVariableById( 1 ) ) , 
				new SkolemFunction( 1 , getMockSystemVariableById( 0 ) , getMockSystemVariableById( 1 ) ) ,
				Operator.OR , new SkolemFunction( 2 , getMockSystemVariableById( 0 ) , getMockSystemVariableById( 1 ) ) , 
				Operator.OR , Operator.AND
				);
		testSkolemizeAndDropQuantifiers( tracker , input , expected );
	}
	
	@Test
	public void testSkolemizeAndDropQuantifiersUniversalWithExistential2() {
		SymbolTracker tracker = new SymbolTracker();
		
		//test skolemizing existentially quantified variables mixed in with
		//universally quantifier variables. this time, though, an existentially
		//quantifier variable has the same name as another one, but is in a
		//different scope.
		//[in infix]	"FORALL(y) EXISTS(x) x AND y AND EXISTS(x) x"		<=>
		//				"FORALL(?0) EXISTS(?1) ?1 AND ?0 AND EXISTS(?2) ?2"	<=>
		//				"$0(?0) AND ?0 AND $1(?0)"
		//[in postfix]	"x EXISTS(x) y AND x EXISTS(x) AND FORALL(y)"		<=>
		//				"$0(?0) ?0 AND $1(?0)"
		List< Symbol > input = Arrays.asList(
				tracker.getNewVariable( "x" ) , newQuantifierList( Quantifier.EXISTS , tracker.getVariableByName( "x" ) ) ,
				tracker.getNewVariable( "y" ) , Operator.AND ,
				tracker.getVariableByName( "x" ) , newQuantifierList( Quantifier.EXISTS , tracker.getVariableByName( "x" ) ) ,
				Operator.AND , newQuantifierList( Quantifier.FORALL , tracker.getVariableByName( "y" ) )
			);		
		List< Symbol > expected = Arrays.asList( 
				new SkolemFunction( 0 , getMockSystemVariableById( 0 ) ) , getMockSystemVariableById( 0 ) , Operator.AND , new SkolemFunction( 1 , getMockSystemVariableById( 0 ) ) , Operator.AND
				);
		testSkolemizeAndDropQuantifiers( tracker , input , expected );
	}
	
	@Test
	public void testSkolemizeAndDropQuantifiersFunctionArguments() throws IOException {
		//test skolemizing variables inside functions
		//FORALL(x) x AND EXISTS(x) GreaterThan(x, DiffInt(x,x))
		//in postfix this is
		//x x x x DiffInt GreaterThan EXISTS(x) AND FORALL(x)
		//which standardizes and skolemizes to
		//?0 $0(?0) $0(?0) $0(?0) DiffInt GreaterThan AND
		SymbolTracker tracker = SymbolTracker.fromDataFile( "test/mjchao/mazenav/logic/structures/integerworld.txt" , new IntegerWorld() );
		
		//test skolemizing variables inside functions
		//[in infix]		"FORALL(x) x AND EXISTS(x) GreaterThan(x, DiffInt(x,x))"		<=>
		//					"FORALL(?0) ?0 AND EXISTS(?1) GreaterThan(?1, DiffInt(?1,?1))"	<=>
		//					"?0 AND GreaterThan($0(?0), DiffInt($0(?0),$0(?0)))"
		//[in postfix]		"x x x x DiffInt GreaterThan EXISTS(x) AND FORALL(x)"			<=>
		//					"?0 $0(?0) $0?(?0) $0(?0) DiffInt GreaterThan AND"
		List< Symbol > input = Arrays.asList(
				tracker.getNewVariable( "x" ) , tracker.getVariableByName( "x" ) , tracker.getVariableByName( "x" ) ,
				tracker.getVariableByName( "x" ) , tracker.getFunction( "DiffInt" ) , tracker.getRelation( "GreaterThan" ) ,
				newQuantifierList( Quantifier.EXISTS , tracker.getVariableByName( "x" ) ) , Operator.AND ,
				newQuantifierList( Quantifier.FORALL , tracker.getVariableByName( "x" ) )
			);		
		List< Symbol > expected = Arrays.asList( 
				getMockSystemVariableById( 0 ) , new SkolemFunction( 0 , getMockSystemVariableById( 0 ) ) ,
				new SkolemFunction( 0 , getMockSystemVariableById( 0 ) ) , new SkolemFunction( 0 , getMockSystemVariableById( 0 ) ) ,
				tracker.getFunction( "DiffInt" ) , tracker.getRelation( "GreaterThan" ) ,
				Operator.AND
				);
		testSkolemizeAndDropQuantifiers( tracker , input , expected );
	}
	
	@Test
	public void testSkolemizeAndDropQuantifiersTwoClauses() {
		//test skolemizing two clauses:
		//[in infix]	"(FORALL(a,b,c) EXISTS(x) x) AND (FORALL(d,e,f) EXISTS(y) y)"				<=>
		//				"(FORALL(?0,?1,?2) EXISTS(?3) ?3) AND (FORALL(?4,?5,?6) EXISTS(?7),?7)"		<=>
		//				"$0(?0,?1,?2) AND $1(?4,?5,?6)"
		//[in postfix]	"x EXISTS(x), FORALL(a,b,c) y EXISTS(y) FORALL(d,e,f) AND"		<=>
		//				"$0(?0,?1,?2) $1(?4,?5,?6) AND"
		SymbolTracker tracker = new SymbolTracker();
		Variable a = tracker.getNewVariable( "a" );
		Variable b = tracker.getNewVariable( "b" );
		Variable c = tracker.getNewVariable( "c" );
		Variable x = tracker.getNewVariable( "x" );
		Variable d = tracker.getNewVariable( "d" );
		Variable e = tracker.getNewVariable( "e" );
		Variable f = tracker.getNewVariable( "f" );
		Variable y = tracker.getNewVariable( "y" );
		List< Symbol > input = Arrays.asList(
				x , newQuantifierList( Quantifier.EXISTS , x ) , 
				newQuantifierList( Quantifier.FORALL , a , b , c ) ,
				y , newQuantifierList( Quantifier.EXISTS , y ) ,
				newQuantifierList( Quantifier.FORALL , d , e , f ) ,
				Operator.AND
			);		
		List< Symbol > expected = Arrays.asList( 
				new SkolemFunction( 0 , getMockSystemVariableById( 0 ) , getMockSystemVariableById( 1 ) , getMockSystemVariableById( 2 ) ) ,
				new SkolemFunction( 1 , getMockSystemVariableById( 4 ) , getMockSystemVariableById( 5 ) , getMockSystemVariableById( 6 ) ) ,
				Operator.AND
				);
		testSkolemizeAndDropQuantifiers( tracker , input , expected );
	}
	
	/**
	 * Verifies that the ExpressionTree correctly distributed ORs over ANDs.
	 * Assumes that buildTree(), eliminateArrowsAndDistributeNots(), 
	 * standardize(), skolemize(), and dropQuantifiers() have been
	 * implemented correctly.
	 * 
	 * @param tracker			keeps track of system variables and skolem functions
	 * @param inputPostfix		an expression in postfix to distribtue ORs over ANDs
	 * @param expectedPostfix	the expression in postfix after distributing ORs
	 * 							over ANDs
	 */
	public void testDistributeOrOverAnd( SymbolTracker tracker , List< Symbol > inputPostfix , List< Symbol > expectedPostfix ) {
		ExpressionTree exprTree = new ExpressionTree();
		setPostfix( exprTree , inputPostfix );
		buildTree( exprTree );
		eliminateArrowsAndDistributeNots( exprTree );
		standardize( exprTree , tracker );
		skolemize( exprTree , tracker );
		dropQuantifiers( exprTree , tracker );
		distributeOrOverAnd( exprTree );
		List< Symbol > found = new ArrayList< Symbol >();
		buildPostfixFromExpressionTree( getRoot(exprTree) , found );
		Assert.assertTrue( expectedPostfix.equals( found ) );
	}
	
	@Test
	public void testDistributeOrOverAndBAT1() {
		
		//test straightforward P OR (Q AND R)
		//which is "P Q R AND OR" in postfix
		//this should yield (P OR Q) AND (P OR R) 
		//when standardized, this should be
		//?0 ?1 OR ?0 ?2 OR AND
		SymbolTracker tracker = new SymbolTracker();
		
		//test straightforward distribution of one OR over one AND
		//[in infix]		"P OR (Q AND R)"		<=>		"(P OR Q) AND (P OR R)"		<=>
		//					"(?0 OR ?1) AND (?0 AND ?2)"
		//[in postfix]		"P Q R AND OR"			<=>		"?0 ?1 OR ?0 ?2 OR AND"
		List< Symbol > input = Arrays.asList( 
				tracker.getNewVariable( "P" ) , tracker.getNewVariable( "Q" ) , 
				tracker.getNewVariable( "R" ) , Operator.AND , Operator.OR 
			);
		List< Symbol > expected = Arrays.asList( 
				getMockSystemVariableById( 0 ) , getMockSystemVariableById( 1 ) , Operator.OR ,
				getMockSystemVariableById( 0 ) , getMockSystemVariableById( 2 ) , Operator.OR ,
				Operator.AND
			);	
		testDistributeOrOverAnd( tracker , input , expected );
	}
	
	@Test
	public void testDistributeOrOverAndBAT2() {
		//test straightforward (P AND Q) OR R
		//which is "P Q AND R OR" in postfix
		//this should yield (P OR R) AND (Q OR R) 
		//when standardized, this should be
		//?0 ?2 OR ?1 ?2 OR AND
		SymbolTracker tracker = new SymbolTracker();
		
		//test distributing an OR over an AND again, except this time, 
		//the AND comes before the OR.
		//[in infix]	"(P AND Q) OR R"	<=>		"(P OR R) AND (Q OR R)"
		//[in postfix]	"P Q AND R OR"		<=>		"?0 ?2 OR ?1 ?2 OR AND"
		List< Symbol > input = Arrays.asList( 
				tracker.getNewVariable( "P" ) , tracker.getNewVariable( "Q" ) , Operator.AND , 
				tracker.getNewVariable( "R" ) , Operator.OR 
			);
		List< Symbol > expected = Arrays.asList( 
				getMockSystemVariableById( 0 ) , getMockSystemVariableById( 2 ) , Operator.OR ,
				getMockSystemVariableById( 1 ) , getMockSystemVariableById( 2 ) , Operator.OR ,
				Operator.AND
			);
		testDistributeOrOverAnd( tracker , input , expected );
	}
	
	@Test
	public void testDistributeOrOverAndConjunctionsOf2() {
		SymbolTracker tracker = new SymbolTracker();
		
		//test distributing an OR over two conjunctions of 2 terms
		//[in infix]	"(P AND Q) OR (R AND S)"	<=>		"((P OR (R AND S) AND (Q OR (R AND S))"		<=>
		//				"(P OR R) AND (P OR S) AND (Q OR R) AND (Q OR S)"
		//[in postfix]	"P Q AND R S AND OR"		<=>		"?0 ?2 OR ?0 ?3 AND ?1 ?2 OR ?1 ?3 OR AND AND"
		List< Symbol > input = Arrays.asList( 
				tracker.getNewVariable( "P" ) , tracker.getNewVariable( "Q" ) , Operator.AND , 
				tracker.getNewVariable( "R" ) , tracker.getNewVariable( "S" ) , Operator.AND ,
				Operator.OR 
			);
		List< Symbol > expected = Arrays.asList( 
				getMockSystemVariableById( 0 ) , getMockSystemVariableById( 2 ) , Operator.OR ,
				getMockSystemVariableById( 0 ) , getMockSystemVariableById( 3 ) , Operator.OR , Operator.AND ,
				getMockSystemVariableById( 1 ) , getMockSystemVariableById( 2 ) , Operator.OR ,
				getMockSystemVariableById( 1 ) , getMockSystemVariableById( 3 ) , Operator.OR , Operator.AND ,
				Operator.AND 
			);
		testDistributeOrOverAnd( tracker , input , expected );
	}
	
	@Test
	public void testDistrbuteOrOverAndConjunctionsOf3() {  
		SymbolTracker tracker = new SymbolTracker();
		
		//test distributing an OR over two conjunctions of three terms
		//[in infix]	"(P AND Q AND R) OR (S AND T AND U)"		<=>
		//				"(P OR (S AND T AND U)) AND (Q OR (S AND T AND U)) AND (R OR (S AND T AND U))"	<=>
		//				"(P OR S) AND (P OR (T AND U)) AND (Q OR S) AND (Q OR (T AND U)) AND (R OR S) AND (R OR (T AND U))"		<=>
		//				"(P OR S) AND (P OR T) AND (P OR U) AND (Q OR S) AND (Q OR T) AND (Q OR U) AND (R OR S) AND (R OR T) AND (R OR U)"	<=>
		//
		//[in postfix]	"P Q AND R AND S T AND U AND OR"		<=>
		//				?0 ?3 OR ?0 ?4 OR AND ?0 ?5 OR AND ?1 ?3 OR ?1 ?4 OR AND ?1 ?5 OR AND AND ?2 ?3 OR ?2 ?4 OR AND ?2 ?5 OR AND AND"
		List< Symbol > input = Arrays.asList( 
				tracker.getNewVariable( "P" ) , tracker.getNewVariable( "Q" ) , Operator.AND ,
				tracker.getNewVariable( "R" ) , Operator.AND , 
				tracker.getNewVariable( "S" ) , tracker.getNewVariable( "T" ) , Operator.AND ,
				tracker.getNewVariable( "U" ) , Operator.AND , Operator.OR
			);
		List< Symbol > expected = Arrays.asList( 
				getMockSystemVariableById( 0 ) , getMockSystemVariableById( 3 ) , Operator.OR , 
				getMockSystemVariableById( 0 ) , getMockSystemVariableById( 4 ) , Operator.OR , Operator.AND ,
				getMockSystemVariableById( 0 ) , getMockSystemVariableById( 5 ) , Operator.OR , Operator.AND ,
				getMockSystemVariableById( 1 ) , getMockSystemVariableById( 3 ) , Operator.OR , 
				getMockSystemVariableById( 1 ) , getMockSystemVariableById( 4 ) , Operator.OR , Operator.AND ,
				getMockSystemVariableById( 1 ) , getMockSystemVariableById( 5 ) , Operator.OR , Operator.AND , Operator.AND ,
				getMockSystemVariableById( 2 ) , getMockSystemVariableById( 3 ) , Operator.OR ,
				getMockSystemVariableById( 2 ) , getMockSystemVariableById( 4 ) , Operator.OR , Operator.AND ,
				getMockSystemVariableById( 2 ) , getMockSystemVariableById( 5 ) , Operator.OR , Operator.AND ,  Operator.AND 
			);
		testDistributeOrOverAnd( tracker , input , expected );
	}
	
	@Test
	public void testDistributeOrOverAndTwice() {
		//test requiring two ORs be distributed over ANDs
		//P OR (Q OR (R AND S))
		//which is "P Q R S AND OR OR"  in postfix.
		//this should yield
		//P OR ((Q OR R) AND (Q OR S)) 		<=>
		//(P OR Q OR R) AND (P OR Q OR S)
		//in postfix this is
		//P Q R OR OR P Q S OR OR AND
		//when standardized, we get
		//?0 ?1 ?2 OR OR ?0 ?1 ?3 OR OR AND
		SymbolTracker tracker = new SymbolTracker();
		
		//test requiring two ORS be distribtued over ANDs
		//[in infix]	"P OR (Q OR (R AND S))"		<=>		"P OR ((Q OR R) AND (Q OR S))"	<=>
		//				"(P OR Q OR R) AND (P OR Q OR S)"
		//[in postfix]	"P Q R S AND OR OR"			<=>		"?0 ?1 ?2 OR OR ?0 ?1 ?3 OR OR AND"
		List< Symbol > input = Arrays.asList(
				tracker.getNewVariable( "P" ) , tracker.getNewVariable( "Q" ) , 
				tracker.getNewVariable( "R" ) , tracker.getNewVariable( "S" ) , 
				Operator.AND , Operator.OR , Operator.OR 
			);
		List< Symbol > expected = Arrays.asList( 
				getMockSystemVariableById( 0 ) , getMockSystemVariableById( 1 ) ,  getMockSystemVariableById( 2 ) , 
				Operator.OR , Operator.OR , 
				getMockSystemVariableById( 0 ) , getMockSystemVariableById( 1 ) , getMockSystemVariableById( 3 ) , 
				Operator.OR , Operator.OR , Operator.AND
			);	
		testDistributeOrOverAnd( tracker , input , expected );
	}
	
	/**
	 * Mock class that does nothing. It's methods are used to 
	 * define functions for integration test 1.
	 * 
	 * @author mjchao
	 *
	 */
	private class Integration1 {
		
		public Integration1() {
			
		}
		
		public BooleanFOL Animal( ObjectFOL obj ) {
			return BooleanFOL.True();
		}
		
		public BooleanFOL Loves( ObjectFOL arg1 , ObjectFOL arg2 ) {
			return BooleanFOL.True();
		}
	}
	
	/**
	 * Applies the test 
	 * ∀x (∀y Animal(y) => Loves(x,y)) => (∃y Loves(y,x))
	 * 
	 * which yields (slightly modified from Russel and Norvig -
	 * note that Russel and Norvig have a typo and their
	 * G(z) should actually have been G(x))
	 * [Animal(F(x)) ∨ Loves(G(x),x)] ∧ [¬Loves(x,F(x)) ∨ Loves(G(x),x)]
	 * 
	 * and in our standardized postfix, this expression is
	 * $0(?0) Animal $1(?0) ?0 Loves OR ?0 $0(?0) Loves NOT $1(?0) ?0 Loves OR AND
	 * 
	 */
	@Test
	public void integration1() {
		Integration1 definingInstance = new Integration1();
		Relation Animal = new Relation( "Animal" , definingInstance , "Object" );
		Relation Loves = new Relation( "Loves" , definingInstance , "Object" , "Object" );
		
		SymbolTracker tracker = new SymbolTracker();
		tracker.addRelation( "Animal" , Animal );
		tracker.addRelation( "Loves" , Loves );
		
		Variable x = tracker.getNewVariable( "x" );
		Variable y = tracker.getNewVariable( "y" );
		List< Symbol > input = Arrays.asList(
				Quantifier.FORALL , x , Symbol.LEFT_PAREN ,
				Quantifier.FORALL , y , Animal , Symbol.LEFT_PAREN , y , Symbol.RIGHT_PAREN ,
				Operator.IMPLICATION , Loves , Symbol.LEFT_PAREN , x , Symbol.COMMA , y ,
				Symbol.RIGHT_PAREN , Symbol.RIGHT_PAREN , Operator.IMPLICATION ,
				Symbol.LEFT_PAREN , Quantifier.EXISTS , y , Loves , Symbol.LEFT_PAREN , y , Symbol.COMMA , x ,
				Symbol.RIGHT_PAREN , Symbol.RIGHT_PAREN
			);
		
		ExpressionTree exprTree = new ExpressionTree( input );
		List< Symbol > output = exprTree.getCNFPostfix( tracker );
		List< Symbol > expected = Arrays.asList(
				new SkolemFunction( 0 , getMockSystemVariableById( 0 ) ) , Animal , 
				new SkolemFunction( 1 , getMockSystemVariableById( 0 ) ) , getMockSystemVariableById( 0 ) ,
				Loves , Operator.OR , getMockSystemVariableById( 0 ) ,
				new SkolemFunction( 0 , getMockSystemVariableById( 0 ) ) , Loves ,
				Operator.NOT , new SkolemFunction( 1 , getMockSystemVariableById( 0 ) ) ,
				getMockSystemVariableById( 0 ) , Loves , Operator.OR , Operator.AND
			);
		Assert.assertTrue( expected.equals( output ) );	
	}
	
	/**
	 * Mock class that does nothing. It's methods are used
	 * to define functions for integration test 2
	 * @author mjchao
	 *
	 */
	private class Integration2 {
		
		public Integration2() {
			
		}
		
		public BooleanFOL Person( ObjectFOL arg0 ) {
			return BooleanFOL.True();
		}
		
		public ObjectFOL Heart( ObjectFOL arg0 ) {
			return null;
		}
		
		public BooleanFOL Has( ObjectFOL arg0 , ObjectFOL arg1 ) {
			return BooleanFOL.True();
		}
	}
	
	/**
	 * We test converting the expression
	 * "Every person has a heart":
	 * 
	 * ∀x Person(x) => ∃y Heart(y) AND Has(x,y)
	 * 
	 * which is equivalent to
	 * ∀x !Person(x) OR (∃y Heart(y) AND Has(x,y))		<=>
	 * !Person(?0) OR (Heart($0(?0)) AND Has(?0, $0(?0)))	<=>
	 * (!Person(?0) OR Heart($0(?0))) AND (!Person(?0) OR Has(?0, $0(?0)))
	 *
	 * In postfix, this is
	 * ?0 Person NOT $0(?0) Heart OR ?0 Person NOT ?0 $0(?0) HAS OR AND
	 *  
	 * Example taken from
	 * (https://april.eecs.umich.edu/courses/eecs492_w10/wiki/images/6/6b/CNF_conversion.pdf)
	 */
	@Test
	public void integration2() {
		Integration2 definingInstance = new Integration2();
		Relation Person = new Relation( "Person" , definingInstance , "Object" );
		Function Heart = new Function( "Heart" , definingInstance , "Person" );
		Relation Has = new Relation( "Has" , definingInstance , "Person" , "Heart" );
		
		SymbolTracker tracker = new SymbolTracker();
		tracker.addRelation( "Person" , Person );
		tracker.addFunction( "Heart" , Heart );
		tracker.addRelation( "Has" , Has );
		
		Variable x = tracker.getNewVariable( "x" );
		Variable y = tracker.getNewVariable( "y" );
		
		//Input = ∀x Person(x) => ∃y Heart(y) AND Has(x,y)
		List< Symbol > input = Arrays.asList(
				Quantifier.FORALL , x , Person , Symbol.LEFT_PAREN , x , Symbol.RIGHT_PAREN ,
				Operator.IMPLICATION , Quantifier.EXISTS , y , 
				Heart , Symbol.LEFT_PAREN , y , Symbol.RIGHT_PAREN , Operator.AND ,
				Has , Symbol.LEFT_PAREN , x , Symbol.COMMA , y , Symbol.RIGHT_PAREN
			);
		
		ExpressionTree exprTree = new ExpressionTree( input );
		List< Symbol > output = exprTree.getCNFPostfix( tracker );
		
		//Expected = ?0 Person NOT $0(?0) Heart OR ?0 Person NOT ?0 $0(?0) HAS OR AND
		List< Symbol > expected = Arrays.asList(
				getMockSystemVariableById( 0 ) , Person , Operator.NOT , 
				new SkolemFunction( 0 , getMockSystemVariableById( 0 ) ) , 
				Heart , Operator.OR , getMockSystemVariableById( 0 ) , 
				Person , Operator.NOT , getMockSystemVariableById( 0 ) ,
				new SkolemFunction( 0 , getMockSystemVariableById( 0 ) ) ,
				Has , Operator.OR , Operator.AND 
			);
		Assert.assertTrue( expected.equals( output ) );	
	}
	
	private class Integration3 {
		
		public Integration3() {
			
		}
		
		public BooleanFOL Philosopher( ObjectFOL arg0 ) {
			return BooleanFOL.True();
		}
		
		public BooleanFOL StudentOf( ObjectFOL arg0 , ObjectFOL arg1 ) {
			return BooleanFOL.True();
		}
		
		public BooleanFOL Book( ObjectFOL arg0 ) {
			return BooleanFOL.True();
		}
		
		public BooleanFOL Write( ObjectFOL arg0 , ObjectFOL arg1 ) {
			return BooleanFOL.True();
		}
		
		public BooleanFOL Read( ObjectFOL arg0 , ObjectFOL arg1 ) {
			return BooleanFOL.True();
		}
		
	}
	
	/**
	 * We test converting the expression
	 * "All students of philosophy read one of their teacher's books":
	 * 
	 * ∀x ∀y (Philosopher(x) AND StudentOf(y,x) => ∃z(Book(z) AND Write(x,z) AND Read(y,z)))
	 * 
	 * which is equivalent to
	 * ∀x ∀y ( (!Philosopher(x) OR !StudentOf(y,x)) OR ∃z(Book(z) AND Write(x,z) AND Read(y,z)) )		<=>
	 * ∀(?0) ∀(?1) ( (!Philosopher(?0) OR !StudentOf(?1,?0)) OR (Book($0(?0,?1)) AND Write(?0, $0(?0,?1)) AND Read(?1,$0(?0,?1))) )	<=>
	 * ∀(?0) ∀(?1) (!Philosopher(?0) OR !StudentOf(?1,?0) OR Book($0(?0,?1))) AND (!Philosopher(?0) OR !StudentOf(?1,?0) OR Write(?0, $0(?0,?1))) AND (!Philosopher(?0) OR !StudentOf(?1,?0) OR Read(?1,$0(?0,?1))) 
	 * 
	 * which converts to postfix as
	 * ?0 Philosopher NOT ?1 ?0 StudentOf NOT OR $0(?0,?1) Book OR ?0 Philosopher NOT ?1 ?0 StudentOf NOT OR ?0 $0(?0,?1) Write OR AND ?0 Philosopher NOT ?1 ?0 StudentOf NOT OR ?1, $0(?0,?1) READ OR AND
	 * 
	 * Example taken from
	 * http://www.cs.toronto.edu/~sheila/384/w11/Lectures/csc384w11-KR-tutorial.pdf
	 */
	@Test
	public void integration3() {
		Integration3 definingInstance = new Integration3();
		Relation Philosopher = new Relation( "Philosopher" , definingInstance , "Object" );
		Relation StudentOf = new Relation( "StudentOf" , definingInstance , "Object" , "Object" );
		Relation Book = new Relation( "Book" , definingInstance , "Object" );
		Relation Write = new Relation( "Write" , definingInstance , "Object" , "Object" );
		Relation Read = new Relation( "Read" , definingInstance , "Object" , "Object" );
		
		SymbolTracker tracker = new SymbolTracker();
		tracker.addRelation( "Philospher" , Philosopher );
		tracker.addRelation( "StudentOf" , StudentOf );
		tracker.addRelation( "Book" , Book );
		tracker.addRelation( "Write" , Write );
		tracker.addRelation( "Read" , Read );
		
		Variable x = tracker.getNewVariable( "x" );
		Variable y = tracker.getNewVariable( "y" );
		Variable z = tracker.getNewVariable( "z" );
		
		//Input = ∀x ∀y (Philosopher(x) AND StudentOf(y,x) => ∃z(Book(z) AND Write(x,z) AND Read(y,z)))
		List< Symbol > input = Arrays.asList(
				Quantifier.FORALL , x , Quantifier.FORALL , y , Symbol.LEFT_PAREN ,
				Philosopher , Symbol.LEFT_PAREN , x , Symbol.RIGHT_PAREN , Operator.AND ,
				StudentOf , Symbol.LEFT_PAREN , y , Symbol.COMMA , x , Symbol.RIGHT_PAREN ,
				Operator.IMPLICATION , Quantifier.EXISTS , z , Symbol.LEFT_PAREN , 
				Book , Symbol.LEFT_PAREN , z , Symbol.RIGHT_PAREN , Operator.AND , 
				Write , Symbol.LEFT_PAREN , x , Symbol.COMMA , z , Symbol.RIGHT_PAREN , Operator.AND ,
				Read , Symbol.LEFT_PAREN , y , Symbol.COMMA , z , Symbol.RIGHT_PAREN, Symbol.RIGHT_PAREN , Symbol.RIGHT_PAREN
			);
		
		ExpressionTree exprTree = new ExpressionTree( input );
		List< Symbol > output = exprTree.getCNFPostfix( tracker );
		
		Variable v0 = getMockSystemVariableById( 0 );
		Variable v1 = getMockSystemVariableById( 1 );
		SkolemFunction s0 = new SkolemFunction( 0 , v0 , v1 );
		//Expected = ?0 Philosopher NOT ?1 ?0 StudentOf NOT OR $0(?0,?1) Book OR ?0 Philosopher NOT ?1 ?0 StudentOf NOT OR ?0 $0(?0,?1) Write OR AND ?0 Philosopher NOT ?1 ?0 StudentOf NOT OR ?1, $0(?0,?1) READ OR AND
		List< Symbol > expected = Arrays.asList(
				v0 , Philosopher , Operator.NOT , v1 , v0 , StudentOf , Operator.NOT , Operator.OR ,
				s0 , Book , Operator.OR , v0 , Philosopher , Operator.NOT , v1 , v0 , StudentOf , Operator.NOT , Operator.OR , 
				v0 , s0 , Write , Operator.OR , Operator.AND , v0 , Philosopher , Operator.NOT , v1 , v0 , StudentOf , Operator.NOT , Operator.OR , 
				v1 , s0 , Read , Operator.OR , Operator.AND
			);
		Assert.assertTrue( expected.equals( output ) );	
	}
	
	class Integration4 {
		
		public Integration4() {
			
		}
		
		public BooleanFOL Philosopher( ObjectFOL arg0 ) {
			return BooleanFOL.True();
		}
		
		public BooleanFOL StudentOf( ObjectFOL arg0 , ObjectFOL arg1 ) {
			return BooleanFOL.True();
		}
	}
	
	/**
	 * Test converting the expression 
	 * "There exists a philosopher with students":
	 * 
	 * ∃x ∃y (Philosopher(x) AND StudentOf(y,x))
	 * 
	 * which is equivalent to
	 * Philosopher($0()) AND StudentOf($1(),$0()) 
	 *
	 * in postfix, this is
	 * 
	 * $0() Philosopher $1() $0() StudentOf AND
	 * 
	 * Example taken from
	 * http://www.cs.toronto.edu/~sheila/384/w11/Lectures/csc384w11-KR-tutorial.pdf
	 */
	@Test
	public void integration4() {
		Integration4 definingInstance = new Integration4();
		Relation Philosopher = new Relation( "Philosopher" , definingInstance , "Object" );
		Relation StudentOf = new Relation( "StudentOf" , definingInstance , "Object" , "Object" );
		
		SymbolTracker tracker = new SymbolTracker();
		tracker.addRelation( "Philosopher" , Philosopher );
		tracker.addRelation( "StudentOf" , StudentOf );
		
		Variable x = tracker.getNewVariable( "x" );
		Variable y = tracker.getNewVariable( "y" );
		
		//input = ∃x ∃y (Philosopher(x) AND StudentOf(y,x))
		List< Symbol > input = Arrays.asList( 
				Quantifier.EXISTS , x , Quantifier.EXISTS , y , Symbol.LEFT_PAREN ,
				Philosopher , Symbol.LEFT_PAREN , x , Symbol.RIGHT_PAREN , Operator.AND ,
				StudentOf , Symbol.LEFT_PAREN , y , Symbol.COMMA , x , Symbol.RIGHT_PAREN , Symbol.RIGHT_PAREN
			);
		
		ExpressionTree exprTree = new ExpressionTree( input );
		List< Symbol > output = exprTree.getCNFPostfix( tracker );
		
		//expected = $0() Philosopher $1() $0() StudentOf AND
		List< Symbol > expected = Arrays.asList( 
				new SkolemFunction( 0 ) , Philosopher ,
				new SkolemFunction( 1 ) , new SkolemFunction( 0 ) , StudentOf ,
				Operator.AND
			);
		Assert.assertTrue( output.equals( expected ) );
	}
	
	private class Integration5 {
		
		public Integration5() {
			
		}
		
		public BooleanFOL Person( ObjectFOL arg0 ) {
			return BooleanFOL.True();
		}
		
		public BooleanFOL Likes( ObjectFOL arg0 , ObjectFOL arg1 ) {
			return BooleanFOL.True();
		}
	}
	
	/**
	 * Test converting the expression
	 * "There exists a person who likes someone else but dislikes someone that someone else likes:"
	 * 
	 * ∃x ∀y ∀z (Person(x) ∧ ((Likes(x,y) ∧ y != z ) => !Likes(x,z)))
	 * 
	 * which is equivalent to
	 * 
	 * ∃x ∀y ∀z (Person(x) AND ((!Likes(x,y) OR y == z) OR !Likes(x,z)))		<=>
	 * Person( $0() ) AND (!Likes($0(), ?1) OR ?1 == ?2 OR !Likes($0(), ?2))
	 *
	 * which when in postfix becomes
	 * 
	 * $0() Person $0() ?1 Likes ! ?1 ?2 == OR $0() ?2 Likes ! OR AND
	 * 
	 * Example taken from
	 * http://math.stackexchange.com/questions/511119/how-to-convert-this-first-order-sentence-into-conjunctive-normal-form
	 */
	@Test
	public void integration5() {
		Integration5 definingInstance = new Integration5();
		Relation Person = new Relation( "Person" , definingInstance , "Object" );
		Relation Likes = new Relation( "Likes" , definingInstance , "Object" , "Object" );
		
		SymbolTracker tracker = new SymbolTracker();
		tracker.addRelation( "Person" , Person );
		tracker.addRelation( "Likes" ,  Likes );
		
		Variable x = tracker.getNewVariable( "x" );
		Variable y = tracker.getNewVariable( "y" );
		Variable z = tracker.getNewVariable( "z" );
		
		//input = ∃x ∀y ∀z (Person(x) ∧ ((Likes(x,y) ∧ y != z ) => !Likes(x,z)))
		List< Symbol > input = Arrays.asList( 
				Quantifier.EXISTS , x , Quantifier.FORALL , y , Quantifier.FORALL , z ,
				Symbol.LEFT_PAREN , Person , Symbol.LEFT_PAREN , x , Symbol.RIGHT_PAREN ,
				Operator.AND , Symbol.LEFT_PAREN , Symbol.LEFT_PAREN , Likes , Symbol.LEFT_PAREN ,
				x , Symbol.COMMA , y , Symbol.RIGHT_PAREN , Operator.AND , y , Operator.NOT_EQUALS , z ,
				Symbol.RIGHT_PAREN , Operator.IMPLICATION , Operator.NOT , Likes , Symbol.LEFT_PAREN ,
				x , Symbol.COMMA , z , Symbol.RIGHT_PAREN , Symbol.RIGHT_PAREN , Symbol.RIGHT_PAREN
			);
		
		ExpressionTree exprTree = new ExpressionTree( input );
		List< Symbol > output = exprTree.getCNFPostfix( tracker );
		
		//expected = $0() Person $0() ?1 Likes ! ?1 ?2 == OR $0() ?2 Likes ! OR AND
		List< Symbol > expected = Arrays.asList( 
				new SkolemFunction( 0 ) , Person , new SkolemFunction( 0 ) , 
				getMockSystemVariableById( 1 ) , Likes , Operator.NOT ,
				getMockSystemVariableById( 1 ) , getMockSystemVariableById( 2 ) ,
				Operator.EQUALS , Operator.OR , new SkolemFunction( 0 ) , 
				getMockSystemVariableById( 2 ) , Likes , Operator.NOT , Operator.OR ,
				Operator.AND
			);
		Assert.assertTrue( output.equals( expected ) );
	}
	
	private class Integration6 {
		
		public BooleanFOL P( ObjectFOL arg0 ) {
			return BooleanFOL.True();
		}
		
		public BooleanFOL Q( ObjectFOL arg0 , ObjectFOL arg1 ) {
			return BooleanFOL.True();
		}
		
		public ObjectFOL f( ObjectFOL arg0 , ObjectFOL arg1 ) {
			return null;
		}
	}
	
	/**
	 * Test converting the expression
	 * 
	 * ∀x(P(x) => (∀y(P(y) => P(f(x,y))) ^ !(∀y(Q(x,y) => P(y)))))
	 * 
	 * Answer (see <http://pages.cs.wisc.edu/~dyer/cs540/notes/fopc.html> for steps):
	 * (!P(x) v !P(y) v P(f(x,y))) ^ (!P(x) v Q(x,g(x,y))) ^ (!P(x) v !P(g(x,y)))
	 * 
	 * in postfix, this is
	 * ?0 P NOT ?1 P NOT ?0 ?1 f P OR OR ?0 P NOT ?0 $0(?0,?1) Q OR ?0 P NOT $0(?0,?1) P NOT OR AND AND 
	 * Example taken from
	 * http://pages.cs.wisc.edu/~dyer/cs540/notes/fopc.html
	 */
	@Test
	public void integration6() {
		Integration6 definingInstance = new Integration6();
		
		Relation P = new Relation( "P" , definingInstance , "Object" );
		Relation Q = new Relation( "Q" , definingInstance , "Object" , "Object" );
		Function f = new Function( "f" , definingInstance , "Object" , "Object" );
		
		SymbolTracker tracker = new SymbolTracker();
		tracker.addRelation( "P" , P );
		tracker.addRelation( "Q" , Q );
		tracker.addFunction( "f" , f );
		
		Variable x = tracker.getNewVariable( "x" );
		Variable y = tracker.getNewVariable( "y" );
		
		//[∀(x), x, P, ∀(y), y, P, x, y, f, P, =>, ∀(y), x, y, Q, y, P, =>, !, &&, =>]
		//[x, P, ∀(y), y, P, x, y, f, P, =>, ∀(y), x, y, Q, y, P, =>, !, &&, =>, ∀(x)]
		//input = ∀x(P(x) => (∀y(P(y) => P(f(x,y))) ^ !(∀y(Q(x,y) => P(y)))))
		List< Symbol > input = Arrays.asList( 
				Quantifier.FORALL , x , Symbol.LEFT_PAREN , P , Symbol.LEFT_PAREN , x , Symbol.RIGHT_PAREN ,
				Operator.IMPLICATION , Symbol.LEFT_PAREN , Quantifier.FORALL , y , 
				Symbol.LEFT_PAREN , P , Symbol.LEFT_PAREN , y , Symbol.RIGHT_PAREN , Operator.IMPLICATION ,
				P , Symbol.LEFT_PAREN , f , Symbol.LEFT_PAREN , x , Symbol.COMMA , y , Symbol.RIGHT_PAREN , Symbol.RIGHT_PAREN , Symbol.RIGHT_PAREN ,
				Operator.AND , Operator.NOT , Symbol.LEFT_PAREN , Quantifier.FORALL , y , Symbol.LEFT_PAREN , 
				Q , Symbol.LEFT_PAREN , x , Symbol.COMMA , y , Symbol.RIGHT_PAREN , Operator.IMPLICATION ,
				P , Symbol.LEFT_PAREN , y , Symbol.RIGHT_PAREN , Symbol.RIGHT_PAREN , Symbol.RIGHT_PAREN , 
				Symbol.RIGHT_PAREN , Symbol.RIGHT_PAREN
			);
		
		ExpressionTree exprTree = new ExpressionTree( input );
		List< Symbol > output = exprTree.getCNFPostfix( tracker );
		
		Variable v0 = getMockSystemVariableById( 0 );
		Variable v1 = getMockSystemVariableById( 1 );
		SkolemFunction s0 = new SkolemFunction( 0 , v0 , v1 );
		Symbol NOT = Operator.NOT;
		Symbol OR = Operator.OR;
		Symbol AND = Operator.AND;
		
		//expected = ?0 P NOT ?1 P NOT ?0 ?1 f P OR OR ?0 P NOT ?0 $0(?0,?1) Q OR ?0 P NOT $0(?0,?1) P NOT OR AND AND 
		List< Symbol > expected = Arrays.asList( 
				v0 , P , NOT , v1 , P , NOT , v0 , v1 , f , P , OR , OR ,
				v0 , P , NOT , v0 , s0 , Q , OR , v0 , P , NOT , s0 , P , NOT , OR , AND , AND
			);
		Assert.assertTrue( output.equals( expected ) );
	}
	
	/**
	 * Test converting 
	 * 
	 * X <=> Y <=> Z		which is read as X <=> (Y <=> Z) by the program
	 * 
	 * Answer: 
	 * (X OR Y OR Z) AND (X OR !X OR Z) AND (!Y OR Y OR Z) AND (!Y OR !X OR Z) AND (!Z OR !X OR Y) AND (!Z OR !Y OR X)
	 * 
	 * in postfix this is
	 * ?0 ?1 OR ?2 OR ?0 ?0 ! OR ?2 AND ?1 ! ?1 OR ?2 OR ?1 ! ?0 ! OR ?2 OR AND AND ?2 ! ?0 ! ?1 OR OR ?2 ! ?1 ! ?0 OR OR AND AND
	 * 
	 * (verified via truth table by WolframAlpha)
	 */
	@Test
	public void integration7() {
		
		SymbolTracker tracker = new SymbolTracker();
		
		Variable X = tracker.getNewVariable( "X" );
		Variable Y = tracker.getNewVariable( "Y" );
		Variable Z = tracker.getNewVariable( "Z" );
		
		//input = X <=> Y <=> Z
		List< Symbol > input = Arrays.asList( 
				X , Operator.BICONDITIONAL , Y , Operator.BICONDITIONAL , Z
			);
		
		ExpressionTree exprTree = new ExpressionTree( input );
		List< Symbol > output = exprTree.getCNFPostfix( tracker );
		
		Variable v0 = getMockSystemVariableById( 0 );
		Variable v1 = getMockSystemVariableById( 1 );
		Variable v2 = getMockSystemVariableById( 2 );
		Operator NOT = Operator.NOT;
		Operator AND = Operator.AND;
		Operator OR = Operator.OR;
		//expected = ?0 ?1 OR ?2 OR ?0 ?0 ! OR ?2 AND ?1 ! ?1 OR ?2 OR ?1 ! ?0 ! OR ?2 OR AND AND ?2 ! ?0 ! ?1 OR OR ?2 ! ?1 ! ?0 OR OR AND AND
		List< Symbol > expected = Arrays.asList(
				v0 , v1 , OR , v2 , OR , v0 , v0 , NOT , OR , v2 , OR , AND , v1 , NOT , v1 ,
				OR , v2 , OR , v1 , NOT , v0 , NOT , OR , v2 , OR , AND , AND , v2 , NOT , v0 , 
				NOT , v1 , OR , OR , v2 , NOT , v1 , NOT , v0 , OR , OR , AND , AND
			);
		Assert.assertTrue( output.equals( expected ) );
	}
}
