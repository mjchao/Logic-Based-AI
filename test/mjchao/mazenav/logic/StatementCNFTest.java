package mjchao.mazenav.logic;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import mjchao.mazenav.logic.StatementCNF.Disjunction;
import mjchao.mazenav.logic.StatementCNF.Disjunction.Term;
import mjchao.mazenav.logic.structures.Function;
import mjchao.mazenav.logic.structures.IntegerWorld;
import mjchao.mazenav.logic.structures.ObjectFOL;
import mjchao.mazenav.logic.structures.SymbolTracker;

import org.junit.Assert;
import org.junit.Test;

public class StatementCNFTest {

	//BAT = Basic Acceptance Test
	@Test
	public void testBuildBAT1() {
		//test simple conjunction of two OR clauses of size 2
		String infix = "(A OR B) AND (A OR C)";
		SymbolTracker tracker = new SymbolTracker();
		StatementCNF cnf = StatementCNF.fromInfixString( infix , tracker );
		Assert.assertEquals( "(?0 OR ?1) AND (?0 OR ?2)" , cnf.toString() );
	}
	
	@Test
	public void testBuildBAT2() {
		//note: cannot use capital f (F) because F = False
		String infix = "(A OR B OR C) AND (D OR E OR f)";
		SymbolTracker tracker = new SymbolTracker();
		StatementCNF cnf = StatementCNF.fromInfixString( infix , tracker );
		Assert.assertEquals( "(?0 OR ?1 OR ?2) AND (?3 OR ?4 OR ?5)" , cnf.toString() );
	}
	
	@Test
	public void testBuildBAT3() {
		//test conjunction of single variables
		String infix = "(A) AND (B) AND (C) AND (D)";
		SymbolTracker tracker = new SymbolTracker();
		StatementCNF cnf = StatementCNF.fromInfixString( infix , tracker );
		Assert.assertEquals( "(?0) AND (?1) AND (?2) AND (?3)" , cnf.toString() );
	}
	
	@Test
	public void testBuildBAT4() {
		//test conjunction with single variables and longer disjunctions
		String infix = "(A) AND (B OR C) AND (D)";
		SymbolTracker tracker = new SymbolTracker();
		StatementCNF cnf = StatementCNF.fromInfixString( infix , tracker );
		Assert.assertEquals( "(?0) AND (?1 OR ?2) AND (?3)" , cnf.toString() );
	}
	
	@Test
	public void testBuildBAT5() {
		//test conjunction with disjunction first, then followed by single variables
		String infix = "(A OR B) AND (C) AND (D)";
		SymbolTracker tracker = new SymbolTracker();
		StatementCNF cnf = StatementCNF.fromInfixString( infix , tracker );
		Assert.assertEquals( "(?0 OR ?1) AND (?2) AND (?3)" , cnf.toString() );
	}
	
	@Test
	public void testBuildBAT6() {
		//test conjunction with negated terms in disjunctions
		String infix = "(!A OR B) AND (A OR B)";
		SymbolTracker tracker = new SymbolTracker();
		StatementCNF cnf = StatementCNF.fromInfixString( infix , tracker );
		Assert.assertEquals( "(!?0 OR ?1) AND (?0 OR ?1)" , cnf.toString() );
	}
	
	@Test
	public void testBuildBAT7() {
		//test conjunction with multiple negated terms in disjunctions
		String infix = "(!A OR !B) AND (!A OR !B)";
		SymbolTracker tracker = new SymbolTracker();
		StatementCNF cnf = StatementCNF.fromInfixString( infix , tracker );
		Assert.assertEquals( "(!?0 OR !?1) AND (!?0 OR !?1)" , cnf.toString() );
	}
	
	@Test
	public void testBuildBAT8() {
		//test conjunction with multiple negated signs in disjunctions
		String infix = "(!!!A OR !B) AND (!A OR !B)";
		SymbolTracker tracker = new SymbolTracker();
		StatementCNF cnf = StatementCNF.fromInfixString( infix , tracker );
		Assert.assertEquals( "(!?0 OR !?1) AND (!?0 OR !?1)" , cnf.toString() );
	}
	
	@Test
	public void testBuildBAT9() {
		//test a single disjunction with no ANDs"
		String infix = "!!!A OR B";
		SymbolTracker tracker = new SymbolTracker();
		StatementCNF cnf = StatementCNF.fromInfixString( infix , tracker );
		Assert.assertEquals( "!?0 OR ?1" , cnf.toString() );
	}
	
	@Test
	public void testBuildBAT10() {
		//test a single variable
		String infix = "A";
		SymbolTracker tracker = new SymbolTracker();
		StatementCNF cnf = StatementCNF.fromInfixString( infix , tracker );
		Assert.assertEquals( "?0" , cnf.toString() );
	}
	
	@Test
	public void testMultipleANDOperators() {
		//test the case where when we build the statement, there are two
		//AND operators in a row - e.g. in postfix, we get A B || C D || && P Q || R S || && &&
		//which is a corner case because two conjunctions "A B || C D || &&" and "P Q || R S || &&" get
		//pushed onto the output list leaving a && with no operands.
		//the final && just needs to be ignored
		String infix = "((A OR B) AND (C OR D)) AND ((P OR Q) AND (R OR S))";
		SymbolTracker tracker = new SymbolTracker();
		StatementCNF cnf = StatementCNF.fromInfixString( infix , tracker );
		Assert.assertEquals( "(?0 OR ?1) AND (?2 OR ?3) AND (?4 OR ?5) AND (?6 OR ?7)" , cnf.toString() );
	}
	
	@Test
	public void testBuildWithQuantifier1() {
		//test building with consecutive quantifiers
		String infix = "(FORALL(x) FORALL(y) x AND y)";
		SymbolTracker tracker = new SymbolTracker();
		StatementCNF cnf = StatementCNF.fromInfixString( infix , tracker );
		Assert.assertEquals( "(?0) AND (?1)" , cnf.toString() );
	}
	
	@Test
	public void testBuildWithQuantifier2() {
		//test building with quantifiers whose scopes end
		String infix = "(FORALL(a,b,c) EXISTS(x) x) AND (FORALL(d,e,f) EXISTS(y) y)";
		SymbolTracker tracker = new SymbolTracker();
		StatementCNF cnf = StatementCNF.fromInfixString( infix , tracker );
		Assert.assertEquals( "($0(?0, ?1, ?2)) AND ($1(?4, ?5, ?6))" , cnf.toString() );
	}
	
	@Test
	public void testBuildWithFunction1() throws IOException {
		//test using an existential quantifier on function arguments
		String infix = "EXISTS(x,y,z) GreaterThan(SumInt(x,y),SumInt(y,z))";
		SymbolTracker tracker = SymbolTracker.fromDataFile( "test/mjchao/mazenav/logic/structures/integerworld.txt" , new IntegerWorld() );
		StatementCNF cnf = StatementCNF.fromInfixString( infix , tracker );
		Assert.assertEquals( "GreaterThan(SumInt($0(), $1()), SumInt($1(), $2()))" , cnf.toString() );
	}
	
	@Test
	public void testBuildWithFunction2() throws IOException {
		//test using a universal quantifier and existential quantifiers
		//on function arguments
		String infix = "FORALL(x) EXISTS(y,z) GreaterThan(SumInt(x,y),SumInt(x,z))";
		SymbolTracker tracker = SymbolTracker.fromDataFile( "test/mjchao/mazenav/logic/structures/integerworld.txt" , new IntegerWorld() );
		StatementCNF cnf = StatementCNF.fromInfixString( infix , tracker );
		Assert.assertEquals( "GreaterThan(SumInt(?0, $0(?0)), SumInt(?0, $1(?0)))" , cnf.toString() );
	}
	
	/**
	 * Mock class for third test case involving functions
	 */
	public class BuildWithFunction3 {
		public ObjectFOL FunctionWithNoArgs() {
			return null;
		}
		
		public ObjectFOL FunctionWithOneArg( ObjectFOL arg1 ) {
			return null;
		}
		
		public ObjectFOL FunctionWithThreeArgs( ObjectFOL arg1 , ObjectFOL arg2 , ObjectFOL arg3 ) {
			return null;
		}
	}
	
	@Test
	public void testBuildWithFunction3() {
		//test building with functions with varying number of arguments
		//and also a negated arguments
		String infix = "FunctionWithNoArgs() OR FunctionWithOneArg(x) OR FORALL(x,y,z) FunctionWithThreeArgs(!x,!!y,!!!!!z)";
		BuildWithFunction3 definingInstance = new BuildWithFunction3();
		Function f0 = new Function( "FunctionWithNoArgs" , definingInstance );
		Function f1 = new Function( "FunctionWithOneArg" , definingInstance , "Object" );
		Function f3 = new Function( "FunctionWithThreeArgs" , definingInstance , "Boolean" , "Boolean" , "Boolean" );
		SymbolTracker tracker = new SymbolTracker();
		tracker.addFunction( "FunctionWithNoArgs" , f0 );
		tracker.addFunction( "FunctionWithOneArg" , f1 );
		tracker.addFunction( "FunctionWithThreeArgs" , f3 );
		StatementCNF cnf = StatementCNF.fromInfixString( infix , tracker );
		Assert.assertEquals( "FunctionWithNoArgs() OR FunctionWithOneArg(?0) OR FunctionWithThreeArgs(!?1, ?2, !?3)", cnf.toString() );
	}
	
	@Test
	public void testBuildWithFunction4() throws IOException {
		//test negated functions
		String infix = "FORALL(x) EXISTS(y,z) !GreaterThan(SumInt(x,y),!SumInt(x,z))";
		SymbolTracker tracker = SymbolTracker.fromDataFile( "test/mjchao/mazenav/logic/structures/integerworld.txt" , new IntegerWorld() );
		StatementCNF cnf = StatementCNF.fromInfixString( infix , tracker );
		Assert.assertEquals( "!GreaterThan(SumInt(?0, $0(?0)), !SumInt(?0, $1(?0)))" , cnf.toString() );
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testBuildWithFunctionsCompoundStatements() throws IOException {
		//not allowed to have compound statements such as x OR y as
		//function arguments. This would make unification impossible.
		//for example, we might have to predict that "x OR y" could potentially
		//unify to become "z". SAT basically reduces to this problem, so
		//this kind of unification is NP-Complete, so we will not attempt to
		//deal with it.
		String infix = "FORALL(x) EXISTS(y,z) GreaterThan(SumInt(x OR y,y OR z),!SumInt(x,z))";
		SymbolTracker tracker = SymbolTracker.fromDataFile( "test/mjchao/mazenav/logic/structures/integerworld.txt" , new IntegerWorld() );
		StatementCNF cnf = StatementCNF.fromInfixString( infix , tracker );
		cnf.toString();
	}
	
	/**
	 * Converts an infix string of the form "<TERM 1> AND <TERM 2> AND ... AND <TERM N>"
	 * into a list of terms for testing term equality.
	 * 
	 * @param infix						an infix string that is a list of terms ANDed together
	 * @param tracker					keeps track of variable names
	 * @return							a list of terms parsed form the input infix
	 * @throws IllegalArgumentException if the input infix contains an element
	 * 									that is not a term. for example, "(<TERM 1> OR <TERM 2>) AND <TERM 3>"
	 * 									would contain a disjunction of size 2 that is not a valid term
	 */
	public static List< Term > termsListFromInfix( String infix , SymbolTracker tracker ) throws IllegalArgumentException {
		StatementCNF cnf = StatementCNF.fromInfixString( infix , tracker );
		List< Term > rtn = new ArrayList< Term >();
		for ( Disjunction d : cnf.getDisjunctions() ) {
			if ( d.size() == 1 ) {
				rtn.add( d.toSingleTerm() );
			}
			else {
				throw new IllegalArgumentException( "Not a list of terms in infix." );
			}
		}
		return rtn;
	}

	@Test
	public void testTermEqualsBAT1() {
		//check that a term equals itself
		SymbolTracker tracker = new SymbolTracker();
		String list = "A AND A";
		List< Term > terms = termsListFromInfix( list , tracker );
		Assert.assertTrue( terms.get( 0 ).equals( terms.get( 1 ) ) );
	}
	
	@Test
	public void testTermEqualsBAT2() {
		//check that a term does not equal a term with a different name
		SymbolTracker tracker = new SymbolTracker();
		String list = "A AND B";
		List< Term > terms = termsListFromInfix( list , tracker );
		Assert.assertFalse( terms.get( 0 ).equals( terms.get( 1 ) ) );
	}
	
	@Test
	public void testTermEqualsBAT3() {
		//check that negations are handled appropriately
		SymbolTracker tracker = new SymbolTracker();
		String list = "!A AND !A";
		List< Term > terms = termsListFromInfix( list , tracker );
		Assert.assertTrue( terms.get( 0 ).equals( terms.get( 1 ) ) );
	}
	
	@Test
	public void testTermEqualsBAT4() {
		//check that negations are handled appropriately
		SymbolTracker tracker = new SymbolTracker();
		String list = "!A AND !B";
		List< Term > terms = termsListFromInfix( list , tracker );
		Assert.assertFalse( terms.get( 0 ).equals( terms.get( 1 ) ) );
	}
	
	@Test
	public void testTermEqualsFunction1() throws IOException {
		//check that functions arguments are compared correctly
		SymbolTracker tracker = SymbolTracker.fromDataFile( "test/mjchao/mazenav/logic/structures/integerworld.txt" , new IntegerWorld() );
		String list = "GreaterThan(x,x) AND GreaterThan(x,x)";
		List< Term > terms = termsListFromInfix( list , tracker );
		Assert.assertTrue( terms.get( 0 ).equals( terms.get( 1 ) ) );
	}
	
	@Test
	public void testTermEqualsFunction2() throws IOException {
		//check that function arguments are compared correctly
		SymbolTracker tracker = SymbolTracker.fromDataFile( "test/mjchao/mazenav/logic/structures/integerworld.txt" , new IntegerWorld() );
		String list = "GreaterThan(x,x) AND GreaterThan(x,y)";
		List< Term > terms = termsListFromInfix( list , tracker );
		Assert.assertFalse( terms.get( 0 ).equals( terms.get( 1 ) ) );
	}
	
	@Test
	public void testTermEqualsFunction3() throws IOException {
		//check that function arguments are explored recursively
		SymbolTracker tracker = SymbolTracker.fromDataFile( "test/mjchao/mazenav/logic/structures/integerworld.txt" , new IntegerWorld() );
		String list = "GreaterThan(SumInt(SumInt(x,x),SumInt(x,x)),x) AND GreaterThan(SumInt(SumInt(x,x),SumInt(x,x)),x)";
		List< Term > terms = termsListFromInfix( list , tracker );
		Assert.assertTrue( terms.get( 0 ).equals( terms.get( 1 ) ) );
	}
	
	@Test
	public void testTermEqualsFunction4() throws IOException {
		//check that function arguments are explored recursively
		SymbolTracker tracker = SymbolTracker.fromDataFile( "test/mjchao/mazenav/logic/structures/integerworld.txt" , new IntegerWorld() );
		String list = "GreaterThan(SumInt(SumInt(x,x),SumInt(x,x)),x) AND GreaterThan(SumInt(SumInt(x,x),SumInt(x,y)),x)";
		List< Term > terms = termsListFromInfix( list , tracker );
		Assert.assertFalse( terms.get( 0 ).equals( terms.get( 1 ) ) );
	}
	
	@Test
	public void testTermEqualsFunction5() throws IOException {
		//check that function arguments are explored recursively
		SymbolTracker tracker = SymbolTracker.fromDataFile( "test/mjchao/mazenav/logic/structures/integerworld.txt" , new IntegerWorld() );
		String list = "GreaterThan(SumInt(SumInt(x,x),SumInt(x,x)),x) AND GreaterThan(x,y)";
		List< Term > terms = termsListFromInfix( list , tracker );
		Assert.assertFalse( terms.get( 0 ).equals( terms.get( 1 ) ) );
	}
	
	@Test
	public void testTermEqualsFunction6() throws IOException {
		//check that function arguments are explored recursively
		//and an inner negation is noticed
		SymbolTracker tracker = SymbolTracker.fromDataFile( "test/mjchao/mazenav/logic/structures/integerworld.txt" , new IntegerWorld() );
		String list = "GreaterThan(SumInt(SumInt(x,x),SumInt(x,x)),x) AND GreaterThan(SumInt(SumInt(x,x),SumInt(x,!x)),x)";
		List< Term > terms = termsListFromInfix( list , tracker );
		Assert.assertFalse( terms.get( 0 ).equals( terms.get( 1 ) ) );
	}
	
	/**
	 * Converts the expression in infix to CNF and 
	 * returns the disjunctions in the CNF form.
	 * 
	 * @param infix
	 * @param tracker
	 * @return
	 */
	public static List< Disjunction > disjunctionsFromInfix( String infix , SymbolTracker tracker ) {
		StatementCNF cnf = StatementCNF.fromInfixString( infix , tracker );
		List< Disjunction > rtn = new ArrayList< Disjunction >( cnf.getDisjunctions() );
		return rtn;
	}

	@Test
	public void testDisjunctionEqualsBAT1() {
		//check that a disjunction is equal to itself
		SymbolTracker tracker = new SymbolTracker();
		String infix = "(A OR B) AND (A OR B)";
		List< Disjunction > disjunctions = disjunctionsFromInfix( infix , tracker );
		Assert.assertTrue( disjunctions.get( 0 ).equals( disjunctions.get( 1 ) ) );
	}
	
	@Test
	public void testDisjunctionEqualsBAT2() {
		//check that a disjunction is not equal to a slightly different disjunction
		SymbolTracker tracker = new SymbolTracker();
		String infix = "(A OR B) AND (A OR C)";
		List< Disjunction > disjunctions = disjunctionsFromInfix( infix , tracker );
		Assert.assertFalse( disjunctions.get( 0 ).equals( disjunctions.get( 1 ) ) );
	}
	
	@Test
	public void testDisjunctionEqualsBAT3() {
		//test disjunctions with many terms
		SymbolTracker tracker = new SymbolTracker();
		String infix = "(A OR B OR C OR D OR E) AND (A OR B OR C OR D OR E)";
		List< Disjunction > disjunctions = disjunctionsFromInfix( infix , tracker );
		Assert.assertTrue( disjunctions.get( 0 ).equals( disjunctions.get( 1 ) ) );
	}
	
	@Test
	public void testDisjunctionEqualsBAT4() {
		//test disjunctions with many terms
		SymbolTracker tracker = new SymbolTracker();
		String infix = "(A OR B OR C OR D OR E) AND (A OR B OR C OR D OR F)";
		List< Disjunction > disjunctions = disjunctionsFromInfix( infix , tracker );
		Assert.assertFalse( disjunctions.get( 0 ).equals( disjunctions.get( 1 ) ) );
	}
	
	@Test
	public void testDisjunctionEqualsBAT5() {
		//test disjunctions with many terms
		SymbolTracker tracker = new SymbolTracker();
		String infix = "(A OR B OR C OR D OR E) AND (A)";
		List< Disjunction > disjunctions = disjunctionsFromInfix( infix , tracker );
		Assert.assertFalse( disjunctions.get( 0 ).equals( disjunctions.get( 1 ) ) );
	}
	
	@Test
	public void testDisjunctionEqualsBAT6() {
		//test disjunctions with many terms
		SymbolTracker tracker = new SymbolTracker();
		String infix = "(A) AND (A OR B OR C OR D OR E)";
		List< Disjunction > disjunctions = disjunctionsFromInfix( infix , tracker );
		Assert.assertFalse( disjunctions.get( 0 ).equals( disjunctions.get( 1 ) ) );
	}
	
	@Test
	public void testDisjunctionEqualsReordered1() {
		//test disjunctions with reordered terms
		SymbolTracker tracker = new SymbolTracker();
		String infix = "(A OR B) AND (B OR A)";
		List< Disjunction > disjunctions = disjunctionsFromInfix( infix , tracker );
		Assert.assertTrue( disjunctions.get( 0 ).equals( disjunctions.get( 1 ) ) );
	}
	
	@Test
	public void testDisjunctionEqualsReordered2() {
		//test disjunctions with many reordered terms
		SymbolTracker tracker = new SymbolTracker();
		String infix = "(A OR B OR C OR D) AND (B OR A OR D OR C)";
		List< Disjunction > disjunctions = disjunctionsFromInfix( infix , tracker );
		Assert.assertTrue( disjunctions.get( 0 ).equals( disjunctions.get( 1 ) ) );
	}
	
	@Test
	public void testDisjunctionEqualsReordered3() {
		//test disjunctions with many reordered terms
		SymbolTracker tracker = new SymbolTracker();
		String infix = "(A OR B OR C OR D) AND (B OR A OR E OR C)";
		List< Disjunction > disjunctions = disjunctionsFromInfix( infix , tracker );
		Assert.assertFalse( disjunctions.get( 0 ).equals( disjunctions.get( 1 ) ) );
	}
	
	@Test
	public void testDisjunctionEqualsReordered4() {
		//test disjunctions with many reordered terms
		SymbolTracker tracker = new SymbolTracker();
		String infix = "(A OR B OR C OR D) AND (B OR A OR C OR G)";
		List< Disjunction > disjunctions = disjunctionsFromInfix( infix , tracker );
		Assert.assertFalse( disjunctions.get( 0 ).equals( disjunctions.get( 1 ) ) );
	}
	
	@Test
	public void testDisjunctionEqualsRepeated1() {
		//test disjunctions with repeated terms
		//note: A OR A <=> A so repeated terms should not make any difference
		SymbolTracker tracker = new SymbolTracker();
		String infix = "(A OR A) AND (A)";
		List< Disjunction > disjunctions = disjunctionsFromInfix( infix , tracker );
		Assert.assertTrue( disjunctions.get( 0 ).equals( disjunctions.get( 1 ) ) );
	}
	
	@Test
	public void testDisjunctionEqualsRepeated2() {
		//test disjunctions with many repeated terms
		//note: A OR A <=> A so repeated terms should not make any difference
		SymbolTracker tracker = new SymbolTracker();
		String infix = "(A OR A OR A OR B OR B OR B OR C OR F OR G) AND (G OR F OR A OR B OR C)";
		List< Disjunction > disjunctions = disjunctionsFromInfix( infix , tracker );
		Assert.assertTrue( disjunctions.get( 0 ).equals( disjunctions.get( 1 ) ) );
	}
	
	@Test
	public void testDisjunctionEqualsFunction1() throws IOException {
		//test disjunctions with two functions each
		SymbolTracker tracker = SymbolTracker.fromDataFile( "test/mjchao/mazenav/logic/structures/integerworld.txt" , new IntegerWorld() );
		String infix = "(GreaterThan(x,x) OR GreaterThan(x,x)) AND (GreaterThan(x,x) OR GreaterThan(x,x))";
		List< Disjunction > disjunctions = disjunctionsFromInfix( infix , tracker );
		Assert.assertTrue( disjunctions.get( 0 ).equals( disjunctions.get( 1 ) ) );
	}
	
	@Test
	public void testDisjunctionEqualsFunction2() throws IOException {
		//test disjunctions with two functions each
		SymbolTracker tracker = SymbolTracker.fromDataFile( "test/mjchao/mazenav/logic/structures/integerworld.txt" , new IntegerWorld() );
		String infix = "(GreaterThan(x,x) OR GreaterThan(x,x)) AND (GreaterThan(x,x) OR GreaterThan(x,y))";
		List< Disjunction > disjunctions = disjunctionsFromInfix( infix , tracker );
		Assert.assertFalse( disjunctions.get( 0 ).equals( disjunctions.get( 1 ) ) );
	}
	
	@Test
	public void testDisjunctionEqualsFunction3() throws IOException {
		//test disjunctions with multiple functions
		SymbolTracker tracker = SymbolTracker.fromDataFile( "test/mjchao/mazenav/logic/structures/integerworld.txt" , new IntegerWorld() );
		String infix = "(GreaterThan(x,x) OR GreaterThan(y,y) OR GreaterThan(SumInt(z,z),SumInt(u,u))) AND (GreaterThan(x,x) OR GreaterThan(SumInt(z,z),SumInt(u,u)) OR GreaterThan(y,y))";
		List< Disjunction > disjunctions = disjunctionsFromInfix( infix , tracker );
		Assert.assertTrue( disjunctions.get( 0 ).equals( disjunctions.get( 1 ) ) );
	}
}
