package mjchao.mazenav.logic;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import mjchao.mazenav.logic.ExpressionTree.ExpressionNode;
import mjchao.mazenav.logic.structures.BooleanFOL;
import mjchao.mazenav.logic.structures.IntegerWorld;
import mjchao.mazenav.logic.structures.ObjectFOL;
import mjchao.mazenav.logic.structures.Operator;
import mjchao.mazenav.logic.structures.Quantifier;
import mjchao.mazenav.logic.structures.Relation;
import mjchao.mazenav.logic.structures.SkolemFunction;
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
	
	public void dropQuantifiers( ExpressionTree tree ) {
		Class<?> c = ExpressionTree.class;
		try {
			Method f = c.getDeclaredMethod( "dropQuantifiers" );
			f.setAccessible( true );
			f.invoke( tree );
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
	}
	
	@Test
	public void testBuildTree() {
		SymbolTracker tracker;
		List< Symbol > input;
		ExpressionTree exprTree;
		List< Symbol > expected;
		List< Symbol > found;
		
		//-------basic acceptance tests-------//
		
		
		//try postfix "x"
		tracker = new SymbolTracker();
		input = Arrays.asList(
				tracker.getNewVariable( "x" ) , tracker.getNewVariable( "y" ) , Operator.OR
			);
		exprTree = new ExpressionTree();
		setPostfix( exprTree , input );
		buildTree( exprTree );
		expected = input;
		found = new ArrayList< Symbol >();
		buildPostfixFromExpressionTree( getRoot(exprTree) , found );
		Assert.assertTrue( expected.equals( found ) );		
		
		
		//try postfix "x y AND z OR"
		tracker = new SymbolTracker();
		input = Arrays.asList(
				tracker.getNewVariable( "x" ) , tracker.getNewVariable( "y" ) , Operator.AND ,
				tracker.getNewVariable( "z" ) , Operator.OR
			);
		exprTree = new ExpressionTree();
		setPostfix( exprTree , input );
		buildTree( exprTree );
		expected = input;
		found = new ArrayList< Symbol >();
		buildPostfixFromExpressionTree( getRoot(exprTree) , found );
		Assert.assertTrue( expected.equals( found ) );	
		
		//w x y AND z => EXISTS(z) => FORALL(w,x,y)
		tracker = new SymbolTracker();
		input = Arrays.asList(
				tracker.getNewVariable( "w" ) , tracker.getNewVariable( "x" ) , tracker.getNewVariable( "y" ) ,
				Operator.AND , tracker.getNewVariable( "z" ) , Operator.IMPLICATION ,
				newQuantifierList( Quantifier.EXISTS , tracker.getVariableByName( "z" ) ) , Operator.IMPLICATION ,
				newQuantifierList( Quantifier.FORALL , tracker.getVariableByName( "w" ) , tracker.getVariableByName( "x" ) , tracker.getVariableByName( "y" ) )
			);
		exprTree = new ExpressionTree();
		setPostfix( exprTree , input );
		buildTree( exprTree );
		expected = input;
		found = new ArrayList< Symbol >();
		buildPostfixFromExpressionTree( getRoot(exprTree) , found );
		Assert.assertTrue( expected.equals( found ) );
		
		// x y AND z => EXISTS z FORALL(x,y)
		tracker = new SymbolTracker();
		input = Arrays.asList(
				tracker.getNewVariable( "x" ) , tracker.getNewVariable( "y" ) ,
				Operator.AND , tracker.getNewVariable( "z" ) , Operator.IMPLICATION ,
				newQuantifierList( Quantifier.EXISTS , tracker.getVariableByName( "z" ) ) ,
				newQuantifierList( Quantifier.FORALL , tracker.getVariableByName( "x" ) , tracker.getVariableByName( "y" ) )
			);
		exprTree = new ExpressionTree();
		setPostfix( exprTree , input );
		buildTree( exprTree );
		expected = input;
		found = new ArrayList< Symbol >();
		buildPostfixFromExpressionTree( getRoot(exprTree) , found );
		Assert.assertTrue( expected.equals( found ) );
		
		// x NOT y NOT AND z => FORALL(x,y,z)
		tracker = new SymbolTracker();
		input = Arrays.asList(
				tracker.getNewVariable( "x" ) , Operator.NOT ,
				tracker.getNewVariable( "y" ) , Operator.NOT ,
				Operator.AND , tracker.getNewVariable( "z" ) ,
				Operator.IMPLICATION ,
				newQuantifierList( Quantifier.FORALL , tracker.getVariableByName( "x" ) ,
					tracker.getVariableByName( "y" ) , tracker.getVariableByName( "z" ) )
			);
		exprTree = new ExpressionTree();
		setPostfix( exprTree , input );
		buildTree( exprTree );
		expected = input;
		found = new ArrayList< Symbol >();
		buildPostfixFromExpressionTree( getRoot(exprTree) , found );
		Assert.assertTrue( expected.equals( found ) );		
	}
	
	@Test
	public void testDistributeNotsAndEliminateArrows() throws IOException {
		SymbolTracker tracker;
		List< Symbol > input;
		ExpressionTree exprTree;
		List< Symbol > expected;
		List< Symbol > found;
		
		//test distributing nots on "x NOT NOT"  <=> "x"
		tracker = new SymbolTracker();
		input = Arrays.asList(
				tracker.getNewVariable( "x" ) , Operator.NOT , Operator.NOT
			);		
		exprTree = new ExpressionTree();
		setPostfix( exprTree , input );
		buildTree( exprTree );
		eliminateArrowsAndDistributeNots( exprTree );
		expected = Arrays.asList( tracker.getVariableByName( "x" ) );
		found = new ArrayList< Symbol >();
		buildPostfixFromExpressionTree( getRoot(exprTree) , found );
		Assert.assertTrue( expected.equals( found ) );
		
		//test distributing nots on "x NOT NOT NOT"  <=> "x"
		tracker = new SymbolTracker();
		input = Arrays.asList(
				tracker.getNewVariable( "x" ) , Operator.NOT , Operator.NOT , Operator.NOT
			);		
		exprTree = new ExpressionTree();
		setPostfix( exprTree , input );
		buildTree( exprTree );
		eliminateArrowsAndDistributeNots( exprTree );
		expected = Arrays.asList( 
				tracker.getVariableByName( "x" ) , Operator.NOT 
			);
		found = new ArrayList< Symbol >();
		buildPostfixFromExpressionTree( getRoot(exprTree) , found );
		Assert.assertTrue( expected.equals( found ) );
		
		//test distributing nots on "x NOT NOT NOT NOT NOT NOT"  <=> "x"
		tracker = new SymbolTracker();
		input = Arrays.asList(
				tracker.getNewVariable( "x" ) , Operator.NOT , Operator.NOT , Operator.NOT ,
				Operator.NOT , Operator.NOT , Operator.NOT
			);		
		exprTree = new ExpressionTree();
		setPostfix( exprTree , input );
		buildTree( exprTree );
		eliminateArrowsAndDistributeNots( exprTree );
		expected = Arrays.asList( 
				tracker.getVariableByName( "x" )
			);
		found = new ArrayList< Symbol >();
		buildPostfixFromExpressionTree( getRoot(exprTree) , found );
		Assert.assertTrue( expected.equals( found ) );
		
		//test eliminating arrows on "x y =>"
		//should result in "x NOT y OR
		tracker = new SymbolTracker();
		input = Arrays.asList(
				tracker.getNewVariable( "x" ) , tracker.getNewVariable( "y" ) ,
				Operator.IMPLICATION
			);		
		exprTree = new ExpressionTree();
		setPostfix( exprTree , input );
		buildTree( exprTree );
		eliminateArrowsAndDistributeNots( exprTree );
		expected = Arrays.asList( 
				tracker.getVariableByName( "x" ) , Operator.NOT ,
				tracker.getVariableByName( "y" ) , Operator.OR
			);
		found = new ArrayList< Symbol >();
		buildPostfixFromExpressionTree( getRoot(exprTree) , found );
		Assert.assertTrue( expected.equals( found ) );
		
		//test eliminating arrows on "x y <=>"
		//should result in "x NOT y OR y NOT x OR AND
		tracker = new SymbolTracker();
		input = Arrays.asList(
				tracker.getNewVariable( "x" ) , tracker.getNewVariable( "y" ) ,
				Operator.BICONDITIONAL
			);		
		exprTree = new ExpressionTree();
		setPostfix( exprTree , input );
		buildTree( exprTree );
		eliminateArrowsAndDistributeNots( exprTree );
		expected = Arrays.asList( 
				tracker.getVariableByName( "x" ) , Operator.NOT ,
				tracker.getVariableByName( "y" ) , Operator.OR ,
				tracker.getVariableByName( "y" ) , Operator.NOT ,
				tracker.getVariableByName( "x" ) , Operator.OR , Operator.AND
			);
		found = new ArrayList< Symbol >();
		buildPostfixFromExpressionTree( getRoot(exprTree) , found );
		Assert.assertTrue( expected.equals( found ) );
		
		//test distributing nots and eliminating arrows on "x y => NOT"
		// "!(x => y)"    <=>    "!(!x OR y)"     <=>      "x AND !y"
		//in postfix, this is "x y NOT AND"
		tracker = new SymbolTracker();
		input = Arrays.asList(
				tracker.getNewVariable( "x" ) , tracker.getNewVariable( "y" ) ,
				Operator.IMPLICATION , Operator.NOT
			);		
		exprTree = new ExpressionTree();
		setPostfix( exprTree , input );
		buildTree( exprTree );
		eliminateArrowsAndDistributeNots( exprTree );
		expected = Arrays.asList( 
				tracker.getVariableByName( "x" ) , tracker.getVariableByName( "y" ) ,
				Operator.NOT , Operator.AND
			);
		found = new ArrayList< Symbol >();
		buildPostfixFromExpressionTree( getRoot(exprTree) , found );
		Assert.assertTrue( expected.equals( found ) );
		
		//test distributing nots and eliminating arrows on "x y <=> NOT"
		// "!(x <=> y)"    <=>    "!((!x OR y) AND (!y OR x))"     <=>      
		// "!(!x OR y) OR !(!y OR x)"		<=> 	"(x AND !y) OR (y AND !x)
		//in postfix, this is "x y NOT AND y x NOT AND OR"
		tracker = new SymbolTracker();
		input = Arrays.asList(
				tracker.getNewVariable( "x" ) , tracker.getNewVariable( "y" ) ,
				Operator.BICONDITIONAL , Operator.NOT
			);		
		exprTree = new ExpressionTree();
		setPostfix( exprTree , input );
		buildTree( exprTree );
		eliminateArrowsAndDistributeNots( exprTree );
		expected = Arrays.asList( 
				tracker.getVariableByName( "x" ) , tracker.getVariableByName( "y" ) ,
				Operator.NOT , Operator.AND , 
				tracker.getVariableByName( "y" ) , tracker.getVariableByName( "x" ) ,
				Operator.NOT , Operator.AND , Operator.OR 
			);
		found = new ArrayList< Symbol >();
		buildPostfixFromExpressionTree( getRoot(exprTree) , found );
		Assert.assertTrue( expected.equals( found ) );
		
		//test distributing nots and eliminating arrows on "x y => z =>"
		// "(x => y) => z"	<=> 	"(!x OR y) => z" 		<=> 	
		// "!(!x OR y) OR z"	<=>		"x AND !y OR z
		//in postfix this is "x y NOT AND z OR
		tracker = new SymbolTracker();
		input = Arrays.asList(
				tracker.getNewVariable( "x" ) , tracker.getNewVariable( "y" ) ,
				Operator.IMPLICATION , tracker.getNewVariable( "z" ) , Operator.IMPLICATION
			);		
		exprTree = new ExpressionTree();
		setPostfix( exprTree , input );
		buildTree( exprTree );
		eliminateArrowsAndDistributeNots( exprTree );
		expected = Arrays.asList( 
				tracker.getVariableByName( "x" ) ,
				tracker.getVariableByName( "y" ) , Operator.NOT , Operator.AND ,
				tracker.getVariableByName( "z" ) , Operator.OR
			);
		found = new ArrayList< Symbol >();
		buildPostfixFromExpressionTree( getRoot(exprTree) , found );
		Assert.assertTrue( expected.equals( found ) );
		
		//test distributing nots over quantifiers and arrows on 
		//"x y => FORALL(x,y)"
		//"!(FORALL(x,y) x => y)" 	<=>		"EXISTS(x,y) !(x => y)"		<=>
		//"EXISTS(x,y) !(!x OR y)"	<=>		"EXISTS(x,y) x AND !y
		//in postfix this is "x y NOT AND EXISTS(x,y)"
		tracker = new SymbolTracker();
		input = Arrays.asList(
				tracker.getNewVariable( "x" ) , tracker.getNewVariable( "y" ) ,
				Operator.IMPLICATION , 
				newQuantifierList( Quantifier.FORALL , tracker.getVariableByName( "x" ) , tracker.getVariableByName( "y" ) ) ,
				Operator.NOT
			);		
		exprTree = new ExpressionTree();
		setPostfix( exprTree , input );
		buildTree( exprTree );
		eliminateArrowsAndDistributeNots( exprTree );
		expected = Arrays.asList( 
				tracker.getVariableByName( "x" ) , tracker.getVariableByName( "y" ) ,
				Operator.NOT , Operator.AND ,
				newQuantifierList( Quantifier.EXISTS , tracker.getVariableByName( "x" ) , tracker.getVariableByName( "y" ) )
			);
		found = new ArrayList< Symbol >();
		buildPostfixFromExpressionTree( getRoot(exprTree) , found );
		Assert.assertTrue( expected.equals( found ) );
		
		//test distributing nots over two quantifiers and arrows
		//"x y => FORALL(x,y) w z => EXISTS(w,z) => !"
		//!((FORALL(x,y) x => y) => EXISTS(w,z) w => z)			<=>
		//!(!(FORALL(x,y) x => y) OR (EXISTS(w,z) w => z))		<=>
		//FORALL(x,y) x => y AND !(EXISTS(w,z) w => z) 			<=>
		//FORALL(x,y) x => y AND FORALL(w,z) !(w => z)			<=>
		//(FORALL(x,y) !x OR y) AND (FORALL(w,z) w AND !z)
		
		//in postfix, this is
		// x NOT y OR FORALL(x,y) w z NOT AND FORALL(w,z) AND 
		tracker = new SymbolTracker();
		input = Arrays.asList(
				tracker.getNewVariable( "x" ) , tracker.getNewVariable( "y" ) ,
				Operator.IMPLICATION ,
				newQuantifierList( Quantifier.FORALL , tracker.getVariableByName( "x" ) , tracker.getVariableByName( "y" ) ) ,
				tracker.getNewVariable( "w" ) , tracker.getNewVariable( "z" ) ,
				Operator.IMPLICATION ,
				newQuantifierList( Quantifier.EXISTS , tracker.getVariableByName( "w" ) , tracker.getVariableByName( "z" ) ) ,
				Operator.IMPLICATION , Operator.NOT
			);		
		exprTree = new ExpressionTree();
		setPostfix( exprTree , input );
		buildTree( exprTree );
		eliminateArrowsAndDistributeNots( exprTree );
		expected = Arrays.asList( 
				tracker.getVariableByName( "x" ) , Operator.NOT , tracker.getVariableByName( "y" ) ,
				Operator.OR , newQuantifierList( Quantifier.FORALL , tracker.getVariableByName( "x" ) , tracker.getVariableByName( "y" ) ) ,
				tracker.getVariableByName( "w" ) , tracker.getVariableByName( "z" ) , Operator.NOT , 
				Operator.AND , newQuantifierList( Quantifier.FORALL , tracker.getVariableByName( "w" ) , tracker.getVariableByName( "z" ) ) ,
				Operator.AND
			);
		found = new ArrayList< Symbol >();
		buildPostfixFromExpressionTree( getRoot(exprTree) , found );
		Assert.assertTrue( expected.equals( found ) );
		
		//test distributing nots with functions
		//it is important that we do not distribute NOTs into a function
		// FORALL(y), !EXISTS(x) GreaterThan(SumInt(y,y), DiffInt(x,x))
		//in postfix this is equivalent to
		// y y SumInt x x DiffInt GreaterThan EXISTS(x) ! FORALL(y)
		
		//after distributing NOTs, we should have
		// FORALL(y) !EXISTS(x) GreaterThan(SumInt(y,y), DiffInt(x,x))		<=>
		// FORALL(y) FORALL(x) !GreaterThan(SumInt(y,y), DiffInt(x,x))	
		//in postfix this is equivalent to
		// y y SumInt x x DiffInt GreaterThan ! FORALL(x) FORALL(y)
		tracker = SymbolTracker.fromDataFile( "test/mjchao/mazenav/logic/structures/integerworld.txt" , new IntegerWorld() );
		input = Arrays.asList(
				tracker.getNewVariable( "y" ) , tracker.getVariableByName( "y" ) , tracker.getFunction( "SumInt" ) ,
				tracker.getNewVariable( "x" ) , tracker.getVariableByName( "x" ) , tracker.getFunction( "DiffInt" ) ,
				tracker.getRelation( "GreaterThan" ) , 
				newQuantifierList( Quantifier.EXISTS , tracker.getVariableByName( "x" ) ) , Operator.NOT ,
				newQuantifierList( Quantifier.FORALL , tracker.getVariableByName( "y" ) )
			);		
		exprTree = new ExpressionTree();
		setPostfix( exprTree , input );
		buildTree( exprTree );
		eliminateArrowsAndDistributeNots( exprTree );
		expected = Arrays.asList( 
				tracker.getVariableByName( "y" ) , tracker.getVariableByName( "y" ) , tracker.getFunction( "SumInt" ) ,
				tracker.getVariableByName( "x" ) , tracker.getVariableByName( "x" ) , tracker.getFunction( "DiffInt" ) ,
				tracker.getRelation( "GreaterThan" ) ,  Operator.NOT ,
				newQuantifierList( Quantifier.FORALL , tracker.getVariableByName( "x" ) ) ,
				newQuantifierList( Quantifier.FORALL , tracker.getVariableByName( "y" ) )				
			);
		found = new ArrayList< Symbol >();
		buildPostfixFromExpressionTree( getRoot(exprTree) , found );
		Assert.assertTrue( expected.equals( found ) );
	}
	
	@Test
	public void testStandardize() throws IOException {
		SymbolTracker tracker;
		List< Symbol > input;
		ExpressionTree exprTree;
		List< Symbol > expected;
		List< Symbol > found;	
		
		//test standardizing "x"
		tracker = new SymbolTracker();
		input = Arrays.asList(
				tracker.getNewVariable( "x" )
			);		
		exprTree = new ExpressionTree();
		setPostfix( exprTree , input );
		buildTree( exprTree );
		eliminateArrowsAndDistributeNots( exprTree );
		standardize( exprTree , tracker );
		expected = Arrays.asList( tracker.getSystemVariableById( 0 ) );
		found = new ArrayList< Symbol >();
		buildPostfixFromExpressionTree( getRoot(exprTree) , found );
		Assert.assertTrue( expected.equals( found ) );
		
		//test standardizing "x AND y"		<=> "x y AND"
		//which should give "?0 ?1 AND"
		tracker = new SymbolTracker();
		input = Arrays.asList(
				tracker.getNewVariable( "x" ) , tracker.getNewVariable( "y" ) ,  Operator.AND
			);		
		exprTree = new ExpressionTree();
		setPostfix( exprTree , input );
		buildTree( exprTree );
		eliminateArrowsAndDistributeNots( exprTree );
		standardize( exprTree , tracker );
		expected = Arrays.asList( 
				tracker.getSystemVariableById( 0 ) , tracker.getSystemVariableById( 1 ) , Operator.AND );
		found = new ArrayList< Symbol >();
		buildPostfixFromExpressionTree( getRoot(exprTree) , found );
		Assert.assertTrue( expected.equals( found ) );
		
		//test standardizing "x AND y AND z"	<=> "x y AND z AND"
		//which should yield "?0 ?1 AND ?2 AND
		tracker = new SymbolTracker();
		input = Arrays.asList(
				tracker.getNewVariable( "x" ) , tracker.getNewVariable( "y" ) ,  Operator.AND , 
				tracker.getNewVariable( "z" ) , Operator.AND 
			);		
		exprTree = new ExpressionTree();
		setPostfix( exprTree , input );
		buildTree( exprTree );
		eliminateArrowsAndDistributeNots( exprTree );
		standardize( exprTree , tracker );
		expected = Arrays.asList( 
				tracker.getSystemVariableById( 0 ) , tracker.getSystemVariableById( 1 ) , Operator.AND ,
				tracker.getSystemVariableById( 2 ) , Operator.AND );
		found = new ArrayList< Symbol >();
		buildPostfixFromExpressionTree( getRoot(exprTree) , found );
		Assert.assertTrue( expected.equals( found ) );		

		//test standardizing "x AND y AND x" 	<=> "x y AND x AND"
		//which should give "?0 ?1 AND ?0 AND"
		tracker = new SymbolTracker();
		input = Arrays.asList(
				tracker.getNewVariable( "x" ) , tracker.getNewVariable( "y" ) ,  Operator.AND ,
				tracker.getVariableByName( "x" ) , Operator.AND
			);		
		exprTree = new ExpressionTree();
		setPostfix( exprTree , input );
		buildTree( exprTree );
		eliminateArrowsAndDistributeNots( exprTree );
		standardize( exprTree , tracker );
		expected = Arrays.asList( 
				tracker.getSystemVariableById( 0 ) , tracker.getSystemVariableById( 1 ) , Operator.AND ,
				tracker.getSystemVariableById( 0 ) , Operator.AND 
				);
		found = new ArrayList< Symbol >();
		buildPostfixFromExpressionTree( getRoot(exprTree) , found );
		Assert.assertTrue( expected.equals( found ) );
		
		//test standardizing "FORALL x x => FORALL y y => FORALL x x" 		<=> 
		//					"!(FORALL x x => FORALL y y) OR FORALL x x"		<=>
		//					"!(EXISTS x !x OR FORALL y y) OR FORALL x x"	<=>
		//					"FORALL x x AND EXISTS y !y OR FORALL x x"		<=>
		//					"?0 FORALL(?0) ?1 ! EXISTS(?1) AND ?2 FORALL(?2) OR"
		tracker = new SymbolTracker();
		input = Arrays.asList(
				tracker.getNewVariable( "x" ) , newQuantifierList( Quantifier.FORALL , tracker.getVariableByName( "x" ) ) ,
				tracker.getNewVariable( "y" ) , newQuantifierList( Quantifier.FORALL , tracker.getVariableByName( "y" ) ) ,
				Operator.IMPLICATION , 
				tracker.getVariableByName( "x" ) , newQuantifierList( Quantifier.FORALL , tracker.getVariableByName( "x" ) ) ,
				Operator.IMPLICATION
			);		
		exprTree = new ExpressionTree();
		setPostfix( exprTree , input );
		buildTree( exprTree );
		eliminateArrowsAndDistributeNots( exprTree );
		standardize( exprTree , tracker );
		expected = Arrays.asList( 
				tracker.getSystemVariableById( 0 ) , newQuantifierList( Quantifier.FORALL , tracker.getSystemVariableById( 0 ) ) ,
				tracker.getSystemVariableById( 1 ) , Operator.NOT , newQuantifierList( Quantifier.EXISTS , tracker.getSystemVariableById( 1 ) ) ,
				Operator.AND , 
				tracker.getSystemVariableById( 2 ) , newQuantifierList( Quantifier.FORALL , tracker.getSystemVariableById( 2 ) ) ,
				Operator.OR
				);
		found = new ArrayList< Symbol >();
		buildPostfixFromExpressionTree( getRoot(exprTree) , found );
		Assert.assertTrue( expected.equals( found ) );
		
		//test standardizing "FORALL(x,y,z) x AND y AND z AND FORALL(x,y) x AND y AND z" 	<=>
		//					"x y AND z AND x y AND z AND FORALL(x,y) AND FORALL(x,y,z)"	<=>
		//					"?0 ?1 AND ?2 AND ?3 ?4 AND ?2 AND FORALL(?3,?4) AND FORALL(?0,?1,?2)"
		tracker = new SymbolTracker();
		input = Arrays.asList(
				tracker.getNewVariable( "x" ) , tracker.getNewVariable( "y" ) , Operator.AND ,
				tracker.getNewVariable( "z" ) , Operator.AND , 
				tracker.getVariableByName( "x" ) , tracker.getVariableByName( "y" ) , Operator.AND , 
				tracker.getVariableByName( "z" ) , Operator.AND ,
				newQuantifierList( Quantifier.FORALL , tracker.getVariableByName( "x" ) , tracker.getVariableByName( "y" ) ) ,
				Operator.AND ,
				newQuantifierList( Quantifier.FORALL , tracker.getVariableByName( "x" ) , tracker.getVariableByName( "y" ) , tracker.getVariableByName( "z" ) )
			);		
		exprTree = new ExpressionTree();
		setPostfix( exprTree , input );
		buildTree( exprTree );
		eliminateArrowsAndDistributeNots( exprTree );
		standardize( exprTree , tracker );
		expected = Arrays.asList( 
				tracker.getSystemVariableById( 0 ) , tracker.getSystemVariableById( 1 ) , Operator.AND ,
				tracker.getSystemVariableById( 2 ) , Operator.AND ,
				tracker.getSystemVariableById( 3 ) , tracker.getSystemVariableById( 4 ) , Operator.AND ,
				tracker.getSystemVariableById( 2 ) , Operator.AND ,
				newQuantifierList( Quantifier.FORALL , tracker.getSystemVariableById( 3 ) , tracker.getSystemVariableById( 4 ) ) ,
				Operator.AND , newQuantifierList( Quantifier.FORALL , tracker.getSystemVariableById( 0 ) , tracker.getSystemVariableById( 1 ) , tracker.getSystemVariableById( 2 ) )
				);
		found = new ArrayList< Symbol >();
		buildPostfixFromExpressionTree( getRoot(exprTree) , found );
		Assert.assertTrue( expected.equals( found ) );
		
		//test standardizing "FORALL(x,y) x AND y AND EXISTS(x,y,z) x OR y OR z"
		//which in postfix is "x y AND x y OR z OR EXISTS(x,y,z) AND FORALL(x,y)"
		//should yield "?0 ?1 AND ?2 ?3 OR ?4 OR EXISTS(?2,?3,?4) AND FORALL(?0,?1)"
		tracker = new SymbolTracker();
		input = Arrays.asList(
				tracker.getNewVariable( "x" ) , tracker.getNewVariable( "y" ) , Operator.AND ,
				tracker.getVariableByName( "x" ) , tracker.getVariableByName( "y" ) , Operator.OR ,
				tracker.getNewVariable( "z" ) , Operator.OR ,
				newQuantifierList( Quantifier.EXISTS , tracker.getVariableByName( "x" ) , tracker.getVariableByName( "y" ) , tracker.getVariableByName( "z" ) ) ,
				Operator.AND , 
				newQuantifierList( Quantifier.FORALL , tracker.getVariableByName( "x" ) , tracker.getVariableByName( "y" ) )
			);		
		exprTree = new ExpressionTree();
		setPostfix( exprTree , input );
		buildTree( exprTree );
		eliminateArrowsAndDistributeNots( exprTree );
		standardize( exprTree , tracker );
		expected = Arrays.asList( 
				tracker.getSystemVariableById( 0 ) , tracker.getSystemVariableById( 1 ) , Operator.AND ,
				tracker.getSystemVariableById( 2 ) , tracker.getSystemVariableById( 3 ) , Operator.OR ,
				tracker.getSystemVariableById( 4 ) , Operator.OR ,
				newQuantifierList( Quantifier.EXISTS , tracker.getSystemVariableById( 2 ) , tracker.getSystemVariableById( 3 ) , tracker.getSystemVariableById( 4 ) ) ,
				Operator.AND ,
				newQuantifierList( Quantifier.FORALL , tracker.getSystemVariableById( 0 ) , tracker.getSystemVariableById( 1 ) )
				);		
		found = new ArrayList< Symbol >();
		buildPostfixFromExpressionTree( getRoot(exprTree) , found );
		Assert.assertTrue( expected.equals( found ) );
		
		//test standardizing arguments to functions
		// FORALL(x) GreaterThan(x,x) AND FORALL(x) GreaterThan(x,x) OR EXISTS(x) GreaterThan(SumInt(x,x), DiffInt(x,x))
		//in postfix this is equivalent to
		// x x GreaterThan x x SumInt x x DiffInt GreaterThan EXISTS(x) x x GreaterThan FORALL(x) OR AND FORALL(x)
		//and this standardizes to
		// ?0 ?0 GreaterThan ?1 ?1 SumInt ?1 ?1 DiffInt GreaterThan EXISTS(?1) ?2 ?2 GreaterThan FORALL(?2) OR AND FORALL(?0) 
		tracker = SymbolTracker.fromDataFile( "test/mjchao/mazenav/logic/structures/integerworld.txt" , new IntegerWorld() );
		input = Arrays.asList(
				tracker.getNewVariable( "x" ) , tracker.getVariableByName( "x" ) , tracker.getRelation( "GreaterThan" ) ,
				tracker.getVariableByName( "x" ) , tracker.getVariableByName( "x" ) , tracker.getFunction( "SumInt" ) ,
				tracker.getVariableByName( "x" ) , tracker.getVariableByName( "x" ) , tracker.getFunction( "DiffInt" ) ,
				tracker.getRelation( "GreaterThan" ) , newQuantifierList( Quantifier.EXISTS , tracker.getVariableByName( "x" ) ) ,
				tracker.getVariableByName( "x" ) , tracker.getVariableByName( "x" ) , tracker.getRelation( "GreaterThan" ) ,
				newQuantifierList( Quantifier.FORALL , tracker.getVariableByName( "x" ) ) , Operator.OR , Operator.AND ,
				newQuantifierList( Quantifier.FORALL , tracker.getVariableByName( "x" ) )
			);		
		exprTree = new ExpressionTree();
		setPostfix( exprTree , input );
		buildTree( exprTree );
		eliminateArrowsAndDistributeNots( exprTree );
		standardize( exprTree , tracker );
		expected = Arrays.asList( 
				tracker.getSystemVariableById( 0 ) , tracker.getSystemVariableById( 0 ) , tracker.getRelation( "GreaterThan" ) ,
				tracker.getSystemVariableById( 1 ) , tracker.getSystemVariableById( 1 ) , tracker.getFunction( "SumInt" ) ,
				tracker.getSystemVariableById( 1 ) , tracker.getSystemVariableById( 1 ) , tracker.getFunction( "DiffInt" ) ,
				tracker.getRelation( "GreaterThan" ) , newQuantifierList( Quantifier.EXISTS , tracker.getSystemVariableById( 1 ) ) ,
				tracker.getSystemVariableById( 2 ) , tracker.getSystemVariableById( 2 ) , tracker.getRelation( "GreaterThan" ) ,
				newQuantifierList( Quantifier.FORALL , tracker.getSystemVariableById( 2 ) ) , Operator.OR , Operator.AND ,
				newQuantifierList( Quantifier.FORALL , tracker.getSystemVariableById( 0 ) )
			);		
		found = new ArrayList< Symbol >();
		buildPostfixFromExpressionTree( getRoot(exprTree) , found );
		Assert.assertTrue( expected.equals( found ) );
		
		//test for potential scope errors:
		//EXISTS(x) (EXISTS(x) x) AND x
		//in postfix, this is equivalent to
		// x EXISTS(x) x AND EXISTS(x)
		//which should yield the following when standardized:
		// ?1 EXISTS(?1) ?0 AND EXISTS(?0)
		tracker = new SymbolTracker();
		input = Arrays.asList(
				tracker.getNewVariable( "x" ) , newQuantifierList( Quantifier.EXISTS , tracker.getVariableByName( "x" ) ) ,
				tracker.getVariableByName( "x" ) , Operator.AND ,
				newQuantifierList( Quantifier.EXISTS , tracker.getVariableByName( "x" ) )
			);		
		exprTree = new ExpressionTree();
		setPostfix( exprTree , input );
		buildTree( exprTree );
		eliminateArrowsAndDistributeNots( exprTree );
		standardize( exprTree , tracker );
		expected = Arrays.asList( 
				tracker.getSystemVariableById( 1 ) , newQuantifierList( Quantifier.EXISTS , tracker.getSystemVariableById( 1 ) ) ,
				tracker.getSystemVariableById( 0 ) , Operator.AND ,
				newQuantifierList( Quantifier.EXISTS , tracker.getSystemVariableById( 0 ) )
			);		
		found = new ArrayList< Symbol >();
		buildPostfixFromExpressionTree( getRoot(exprTree) , found );
		Assert.assertTrue( expected.equals( found ) );
	}
	
	@Test
	public void testSkolemizeAndDropQuantifiers() throws IOException {
		SymbolTracker tracker;
		List< Symbol > input;
		ExpressionTree exprTree;
		List< Symbol > expected;
		List< Symbol > found;	
		
		//test no skolemization necessary: "x AND y AND z"
		tracker = new SymbolTracker();
		input = Arrays.asList(
				tracker.getNewVariable( "x" ) , tracker.getNewVariable( "y" ) , Operator.AND ,
				tracker.getNewVariable( "z" ) , Operator.AND
			);		
		exprTree = new ExpressionTree();
		setPostfix( exprTree , input );
		buildTree( exprTree );
		eliminateArrowsAndDistributeNots( exprTree );
		standardize( exprTree , tracker );
		skolemize( exprTree , tracker );
		dropQuantifiers( exprTree );
		expected = Arrays.asList( 
				tracker.getSystemVariableById( 0 ) , tracker.getSystemVariableById( 1 ) , Operator.AND ,
				tracker.getSystemVariableById( 2 ) , Operator.AND
				);
		found = new ArrayList< Symbol >();
		buildPostfixFromExpressionTree( getRoot(exprTree) , found );
		Assert.assertTrue( expected.equals( found ) );			
		
		//test skolemizing "EXISTS(x) x"
		//should yield "$0" after skolemizing and dropping quantifiers
		tracker = new SymbolTracker();
		input = Arrays.asList(
				tracker.getNewVariable( "x" ) , newQuantifierList( Quantifier.EXISTS , tracker.getVariableByName( "x" ) )
			);		
		exprTree = new ExpressionTree();
		setPostfix( exprTree , input );
		buildTree( exprTree );
		eliminateArrowsAndDistributeNots( exprTree );
		standardize( exprTree , tracker );
		skolemize( exprTree , tracker );
		dropQuantifiers( exprTree );
		expected = Arrays.asList( 
				new SkolemFunction( 0 )
				);
		found = new ArrayList< Symbol >();
		buildPostfixFromExpressionTree( getRoot(exprTree) , found );
		Assert.assertTrue( expected.equals( found ) );	
		
		//test skolemizing "EXISTS(x) x AND EXISTS(x) x"
		//should yield "$0 $1 AND" in postfix after skolemizing
		//and dropping quantifiers
		tracker = new SymbolTracker();
		input = Arrays.asList(
				tracker.getNewVariable( "x" ) , newQuantifierList( Quantifier.EXISTS , tracker.getVariableByName( "x" ) ) ,
				tracker.getVariableByName( "x" ) , newQuantifierList( Quantifier.EXISTS , tracker.getVariableByName( "x" ) ) ,
				Operator.AND 
			);		
		exprTree = new ExpressionTree();
		setPostfix( exprTree , input );
		buildTree( exprTree );
		eliminateArrowsAndDistributeNots( exprTree );
		standardize( exprTree , tracker );
		skolemize( exprTree , tracker );
		dropQuantifiers( exprTree );
		expected = Arrays.asList( 
				new SkolemFunction( 0 ) , new SkolemFunction( 1 ) , Operator.AND
				);
		found = new ArrayList< Symbol >();
		buildPostfixFromExpressionTree( getRoot(exprTree) , found );
		Assert.assertTrue( expected.equals( found ) );	
		
		//test skolemizing "FORALL(x) EXISTS(x) x AND EXISTS(x) x"
		//should yield "$0(?0) $1(?0) AND" in postfix after skolemizing
		//and dropping quantifiers
		tracker = new SymbolTracker();
		input = Arrays.asList(
				tracker.getNewVariable( "x" ) , newQuantifierList( Quantifier.EXISTS , tracker.getVariableByName( "x" ) ) ,
				tracker.getVariableByName( "x" ) , newQuantifierList( Quantifier.EXISTS , tracker.getVariableByName( "x" ) ) ,
				Operator.AND , newQuantifierList( Quantifier.FORALL , tracker.getVariableByName( "x" ) )
			);		
		exprTree = new ExpressionTree();
		setPostfix( exprTree , input );
		buildTree( exprTree );
		eliminateArrowsAndDistributeNots( exprTree );
		standardize( exprTree , tracker );
		skolemize( exprTree , tracker );
		dropQuantifiers( exprTree );
		expected = Arrays.asList( 
				new SkolemFunction( 0 , tracker.getSystemVariableById( 0 ) ) , new SkolemFunction( 1 , tracker.getSystemVariableById( 0 ) ) , Operator.AND
				);
		found = new ArrayList< Symbol >();
		buildPostfixFromExpressionTree( getRoot(exprTree) , found );
		Assert.assertTrue( expected.equals( found ) );
		
		//test skolemizing "FORALL(y) EXISTS(x) x AND y AND EXISTS(x) x"
		//should yield "$0(?0) y AND $1(?0) AND" in postfix after skolemizing
		//and dropping quantifiers
		tracker = new SymbolTracker();
		input = Arrays.asList(
				tracker.getNewVariable( "x" ) , newQuantifierList( Quantifier.EXISTS , tracker.getVariableByName( "x" ) ) ,
				tracker.getNewVariable( "y" ) , Operator.AND ,
				tracker.getVariableByName( "x" ) , newQuantifierList( Quantifier.EXISTS , tracker.getVariableByName( "x" ) ) ,
				Operator.AND , newQuantifierList( Quantifier.FORALL , tracker.getVariableByName( "y" ) )
			);		
		exprTree = new ExpressionTree();
		setPostfix( exprTree , input );
		buildTree( exprTree );
		eliminateArrowsAndDistributeNots( exprTree );
		standardize( exprTree , tracker );
		skolemize( exprTree , tracker );
		dropQuantifiers( exprTree );
		expected = Arrays.asList( 
				new SkolemFunction( 0 , tracker.getSystemVariableById( 0 ) ) , tracker.getSystemVariableById( 0 ) , Operator.AND , new SkolemFunction( 1 , tracker.getSystemVariableById( 0 ) ) , Operator.AND
				);
		found = new ArrayList< Symbol >();
		buildPostfixFromExpressionTree( getRoot(exprTree) , found );
		Assert.assertTrue( expected.equals( found ) );	
		
		//test skolemizing "FORALL(x,y) x AND y AND EXISTS(x,y,z) x OR y OR z"
		//which in postfix is "x y AND x y OR z OR EXISTS(x,y,z) AND FORALL(x,y)"
		//should yield "?0 ?1 AND $0(?0,?1) $1(?0,?1) OR $2(?0,?1) OR AND
		tracker = new SymbolTracker();
		input = Arrays.asList(
				tracker.getNewVariable( "x" ) , tracker.getNewVariable( "y" ) , Operator.AND ,
				tracker.getVariableByName( "x" ) , tracker.getVariableByName( "y" ) , Operator.OR ,
				tracker.getNewVariable( "z" ) , Operator.OR ,
				newQuantifierList( Quantifier.EXISTS , tracker.getVariableByName( "x" ) , tracker.getVariableByName( "y" ) , tracker.getVariableByName( "z" ) ) ,
				Operator.AND , 
				newQuantifierList( Quantifier.FORALL , tracker.getVariableByName( "x" ) , tracker.getVariableByName( "y" ) )
			);		
		exprTree = new ExpressionTree();
		setPostfix( exprTree , input );
		buildTree( exprTree );
		eliminateArrowsAndDistributeNots( exprTree );
		standardize( exprTree , tracker );
		skolemize( exprTree , tracker );
		dropQuantifiers( exprTree );
		expected = Arrays.asList( 
				tracker.getSystemVariableById( 0 ) , tracker.getSystemVariableById( 1 ) , Operator.AND ,
				new SkolemFunction( 0 , tracker.getSystemVariableById( 0 ) , tracker.getSystemVariableById( 1 ) ) , 
				new SkolemFunction( 1 , tracker.getSystemVariableById( 0 ) , tracker.getSystemVariableById( 1 ) ) ,
				Operator.OR , new SkolemFunction( 2 , tracker.getSystemVariableById( 0 ) , tracker.getSystemVariableById( 1 ) ) , 
				Operator.OR , Operator.AND
				);
		found = new ArrayList< Symbol >();
		buildPostfixFromExpressionTree( getRoot(exprTree) , found );
		Assert.assertTrue( expected.equals( found ) );	
		
		//test skolemizing variables inside functions
		//FORALL(x) x AND EXISTS(x) GreaterThan(x, DiffInt(x,x))
		//in postfix this is
		//x x x x DiffInt GreaterThan EXISTS(x) AND FORALL(x)
		//which standardizes and skolemizes to
		//?0 $0(?0) $0(?0) $0(?0) DiffInt GreaterThan AND
		tracker = SymbolTracker.fromDataFile( "test/mjchao/mazenav/logic/structures/integerworld.txt" , new IntegerWorld() );
		input = Arrays.asList(
				tracker.getNewVariable( "x" ) , tracker.getVariableByName( "x" ) , tracker.getVariableByName( "x" ) ,
				tracker.getVariableByName( "x" ) , tracker.getFunction( "DiffInt" ) , tracker.getRelation( "GreaterThan" ) ,
				newQuantifierList( Quantifier.EXISTS , tracker.getVariableByName( "x" ) ) , Operator.AND ,
				newQuantifierList( Quantifier.FORALL , tracker.getVariableByName( "x" ) )
			);		
		exprTree = new ExpressionTree();
		setPostfix( exprTree , input );
		buildTree( exprTree );
		eliminateArrowsAndDistributeNots( exprTree );
		standardize( exprTree , tracker );
		skolemize( exprTree , tracker );
		dropQuantifiers( exprTree );
		expected = Arrays.asList( 
				tracker.getSystemVariableById( 0 ) , new SkolemFunction( 0 , tracker.getSystemVariableById( 0 ) ) ,
				new SkolemFunction( 0 , tracker.getSystemVariableById( 0 ) ) , new SkolemFunction( 0 , tracker.getSystemVariableById( 0 ) ) ,
				tracker.getFunction( "DiffInt" ) , tracker.getRelation( "GreaterThan" ) ,
				Operator.AND
				);
		found = new ArrayList< Symbol >();
		buildPostfixFromExpressionTree( getRoot(exprTree) , found );
		Assert.assertTrue( expected.equals( found ) );	
	}
	
	@Test
	public void testDistributeOrOverAnd() {
		SymbolTracker tracker;
		List< Symbol > input;
		ExpressionTree exprTree;
		List< Symbol > expected;
		List< Symbol > found;
		
		//test straightforward P OR (Q AND R)
		//which is "P Q R AND OR" in postfix
		//this should yield (P OR Q) AND (P OR R) 
		//when standardized, this should be
		//?0 ?1 OR ?0 ?2 OR AND
		tracker = new SymbolTracker();
		input = Arrays.asList( 
				tracker.getNewVariable( "P" ) , tracker.getNewVariable( "Q" ) , 
				tracker.getNewVariable( "R" ) , Operator.AND , Operator.OR 
			);
		exprTree = new ExpressionTree();
		setPostfix( exprTree , input );
		buildTree( exprTree );
		eliminateArrowsAndDistributeNots( exprTree );
		standardize( exprTree , tracker );
		skolemize( exprTree , tracker );
		dropQuantifiers( exprTree );
		distributeOrOverAnd( exprTree );
		expected = Arrays.asList( 
				tracker.getSystemVariableById( 0 ) , tracker.getSystemVariableById( 1 ) , Operator.OR ,
				tracker.getSystemVariableById( 0 ) , tracker.getSystemVariableById( 2 ) , Operator.OR ,
				Operator.AND
			);
		found = new ArrayList< Symbol >();
		buildPostfixFromExpressionTree( getRoot(exprTree) , found );
		Assert.assertTrue( expected.equals( found ) );	
		
		//test straightforward (P AND Q) OR R
		//which is "P Q AND R OR" in postfix
		//this should yield (P OR R) AND (Q OR R) 
		//when standardized, this should be
		//?0 ?2 OR ?1 ?2 OR AND
		tracker = new SymbolTracker();
		input = Arrays.asList( 
				tracker.getNewVariable( "P" ) , tracker.getNewVariable( "Q" ) , Operator.AND , 
				tracker.getNewVariable( "R" ) , Operator.OR 
			);
		exprTree = new ExpressionTree();
		setPostfix( exprTree , input );
		buildTree( exprTree );
		eliminateArrowsAndDistributeNots( exprTree );
		standardize( exprTree , tracker );
		skolemize( exprTree , tracker );
		dropQuantifiers( exprTree );
		distributeOrOverAnd( exprTree );
		expected = Arrays.asList( 
				tracker.getSystemVariableById( 0 ) , tracker.getSystemVariableById( 2 ) , Operator.OR ,
				tracker.getSystemVariableById( 1 ) , tracker.getSystemVariableById( 2 ) , Operator.OR ,
				Operator.AND
			);
		found = new ArrayList< Symbol >();
		buildPostfixFromExpressionTree( getRoot(exprTree) , found );
		Assert.assertTrue( expected.equals( found ) );	
		
		//test straightforward (P AND Q) OR R
		//which is "P Q AND R OR" in postfix
		//this should yield (P OR R) AND (Q OR R) 
		//when standardized, this should be
		//?0 ?2 OR ?1 ?2 OR AND
		tracker = new SymbolTracker();
		input = Arrays.asList( 
				tracker.getNewVariable( "P" ) , tracker.getNewVariable( "Q" ) , Operator.AND , 
				tracker.getNewVariable( "R" ) , Operator.OR 
			);
		exprTree = new ExpressionTree();
		setPostfix( exprTree , input );
		buildTree( exprTree );
		eliminateArrowsAndDistributeNots( exprTree );
		standardize( exprTree , tracker );
		skolemize( exprTree , tracker );
		dropQuantifiers( exprTree );
		distributeOrOverAnd( exprTree );
		expected = Arrays.asList( 
				tracker.getSystemVariableById( 0 ) , tracker.getSystemVariableById( 2 ) , Operator.OR ,
				tracker.getSystemVariableById( 1 ) , tracker.getSystemVariableById( 2 ) , Operator.OR ,
				Operator.AND
			);
		found = new ArrayList< Symbol >();
		buildPostfixFromExpressionTree( getRoot(exprTree) , found );
		Assert.assertTrue( expected.equals( found ) );	
		
		//test (P AND Q) OR (R AND S)
		//which is "P Q AND R S AND OR" in postfix
		//this should yield (P OR R) AND (Q OR R) AND (P OR S) AND (Q OR S) 
		//when standardized, this should be
		//?0 ?2 OR ?0 ?3 OR AND ?1 ?2 OR ?1 ?3 OR AND AND
		tracker = new SymbolTracker();
		input = Arrays.asList( 
				tracker.getNewVariable( "P" ) , tracker.getNewVariable( "Q" ) , Operator.AND , 
				tracker.getNewVariable( "R" ) , tracker.getNewVariable( "S" ) , Operator.AND ,
				Operator.OR 
			);
		exprTree = new ExpressionTree();
		setPostfix( exprTree , input );
		buildTree( exprTree );
		eliminateArrowsAndDistributeNots( exprTree );
		standardize( exprTree , tracker );
		skolemize( exprTree , tracker );
		dropQuantifiers( exprTree );
		distributeOrOverAnd( exprTree );
		expected = Arrays.asList( 
				tracker.getSystemVariableById( 0 ) , tracker.getSystemVariableById( 2 ) , Operator.OR ,
				tracker.getSystemVariableById( 0 ) , tracker.getSystemVariableById( 3 ) , Operator.OR , Operator.AND ,
				tracker.getSystemVariableById( 1 ) , tracker.getSystemVariableById( 2 ) , Operator.OR ,
				tracker.getSystemVariableById( 1 ) , tracker.getSystemVariableById( 3 ) , Operator.OR , Operator.AND ,
				Operator.AND 
			);
		found = new ArrayList< Symbol >();
		buildPostfixFromExpressionTree( getRoot(exprTree) , found );
		Assert.assertTrue( expected.equals( found ) );	
		
		//test (P AND Q AND R) OR (S AND T AND U)
		//which is "P Q AND R AND S T AND U AND OR" in postfix
		//this should yield
		//(P OR S) AND (P OR T) AND (P OR U) AND (Q OR S) AND (Q OR T) AND (Q OR U) AND (R OR S) AND (R OR T) AND (R OR U)
		//when standardized, this should be
		//?0 ?3 OR ?0 ?4 OR AND ?0 ?5 OR AND ?1 ?3 OR ?1 ?4 OR AND ?1 ?5 OR AND AND ?2 ?3 OR ?2 ?4 OR AND ?2 ?5 OR AND AND  
		tracker = new SymbolTracker();
		input = Arrays.asList( 
				tracker.getNewVariable( "P" ) , tracker.getNewVariable( "Q" ) , Operator.AND ,
				tracker.getNewVariable( "R" ) , Operator.AND , 
				tracker.getNewVariable( "S" ) , tracker.getNewVariable( "T" ) , Operator.AND ,
				tracker.getNewVariable( "U" ) , Operator.AND , Operator.OR
			);
		exprTree = new ExpressionTree();
		setPostfix( exprTree , input );
		buildTree( exprTree );
		eliminateArrowsAndDistributeNots( exprTree );
		standardize( exprTree , tracker );
		skolemize( exprTree , tracker );
		dropQuantifiers( exprTree );
		distributeOrOverAnd( exprTree );
		expected = Arrays.asList( 
				tracker.getSystemVariableById( 0 ) , tracker.getSystemVariableById( 3 ) , Operator.OR , 
				tracker.getSystemVariableById( 0 ) , tracker.getSystemVariableById( 4 ) , Operator.OR , Operator.AND ,
				tracker.getSystemVariableById( 0 ) , tracker.getSystemVariableById( 5 ) , Operator.OR , Operator.AND ,
				tracker.getSystemVariableById( 1 ) , tracker.getSystemVariableById( 3 ) , Operator.OR , 
				tracker.getSystemVariableById( 1 ) , tracker.getSystemVariableById( 4 ) , Operator.OR , Operator.AND ,
				tracker.getSystemVariableById( 1 ) , tracker.getSystemVariableById( 5 ) , Operator.OR , Operator.AND , Operator.AND ,
				tracker.getSystemVariableById( 2 ) , tracker.getSystemVariableById( 3 ) , Operator.OR ,
				tracker.getSystemVariableById( 2 ) , tracker.getSystemVariableById( 4 ) , Operator.OR , Operator.AND ,
				tracker.getSystemVariableById( 2 ) , tracker.getSystemVariableById( 5 ) , Operator.OR , Operator.AND ,  Operator.AND 
			);
		found = new ArrayList< Symbol >();
		buildPostfixFromExpressionTree( getRoot(exprTree) , found );
		Assert.assertTrue( expected.equals( found ) );	
		

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
		tracker = new SymbolTracker();
		input = Arrays.asList(
				tracker.getNewVariable( "P" ) , tracker.getNewVariable( "Q" ) , 
				tracker.getNewVariable( "R" ) , tracker.getNewVariable( "S" ) , 
				Operator.AND , Operator.OR , Operator.OR 
			);
		exprTree = new ExpressionTree();
		setPostfix( exprTree , input );
		buildTree( exprTree );
		eliminateArrowsAndDistributeNots( exprTree );
		standardize( exprTree , tracker );
		skolemize( exprTree , tracker );
		dropQuantifiers( exprTree );
		distributeOrOverAnd( exprTree );
		expected = Arrays.asList( 
				tracker.getSystemVariableById( 0 ) , tracker.getSystemVariableById( 1 ) ,  tracker.getSystemVariableById( 2 ) , 
				Operator.OR , Operator.OR , 
				tracker.getSystemVariableById( 0 ) , tracker.getSystemVariableById( 1 ) , tracker.getSystemVariableById( 3 ) , 
				Operator.OR , Operator.OR , Operator.AND
			);
		found = new ArrayList< Symbol >();
		buildPostfixFromExpressionTree( getRoot(exprTree) , found );
		Assert.assertTrue( expected.equals( found ) );	
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
	 * and in postfix, this expression is
	 * $0(?0) Animal $1(?0) ?0 Loves OR ?0 $0(?0) Loves NOT $1(?0) ?0 Loves OR AND
	 * 
	 */
	@Test
	public void testIntegration1() {
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
				new SkolemFunction( 0 , tracker.getSystemVariableById( 0 ) ) , Animal , 
				new SkolemFunction( 1 , tracker.getSystemVariableById( 0 ) ) , tracker.getSystemVariableById( 0 ) ,
				Loves , Operator.OR , tracker.getSystemVariableById( 0 ) ,
				new SkolemFunction( 0 , tracker.getSystemVariableById( 0 ) ) , Loves ,
				Operator.NOT , new SkolemFunction( 1 , tracker.getSystemVariableById( 0 ) ) ,
				tracker.getSystemVariableById( 0 ) , Loves , Operator.OR , Operator.AND
			);
		Assert.assertTrue( expected.equals( output ) );	
	}
}
