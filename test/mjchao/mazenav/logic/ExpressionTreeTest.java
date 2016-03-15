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
	
	public void distributeNotsAndEliminateArrows( ExpressionTree tree ) {
		Class<?> c = ExpressionTree.class;
		try {
			Method f = c.getDeclaredMethod( "distributeNotsAndEliminateArrows" );
			f.setAccessible( true );
			f.invoke( tree );
		} catch (IllegalArgumentException | IllegalAccessException | SecurityException | NoSuchMethodException | InvocationTargetException e) {
			e.printStackTrace();
			throw new RuntimeException( "Could not apply distributeNots() method to ExpressionTree object." );
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
	public void testConverToPostfixWithQuantifiers() {
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
				newQuantifierList( Quantifier.EXISTS , tracker.getNewVariable( "z" ) ) ,
				newQuantifierList( Quantifier.FORALL , tracker.getNewVariable( "x" ) , tracker.getNewVariable( "y" ) )
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
	public void testDistributeNotsAndEliminateArrows() {
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
		distributeNotsAndEliminateArrows( exprTree );
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
		distributeNotsAndEliminateArrows( exprTree );
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
		distributeNotsAndEliminateArrows( exprTree );
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
		distributeNotsAndEliminateArrows( exprTree );
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
		distributeNotsAndEliminateArrows( exprTree );
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
		distributeNotsAndEliminateArrows( exprTree );
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
		distributeNotsAndEliminateArrows( exprTree );
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
		distributeNotsAndEliminateArrows( exprTree );
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
		distributeNotsAndEliminateArrows( exprTree );
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
		distributeNotsAndEliminateArrows( exprTree );
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
	}
}
