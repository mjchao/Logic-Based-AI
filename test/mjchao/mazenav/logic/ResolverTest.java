package mjchao.mazenav.logic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import mjchao.mazenav.logic.Resolver.Substitution;
import mjchao.mazenav.logic.StatementCNF.Disjunction;
import mjchao.mazenav.logic.StatementCNF.Disjunction.Term;
import mjchao.mazenav.logic.structures.Function;
import mjchao.mazenav.logic.structures.ObjectFOL;
import mjchao.mazenav.logic.structures.SymbolTracker;

import org.junit.Assert;
import org.junit.Test;

public class ResolverTest {

	@Test
	public void testUnifyVarBAT1() {
		//test unification with 2 variables that succeeds
		SymbolTracker tracker = new SymbolTracker();
		String infixTerms = "x AND y";
		List< Term > terms = StatementCNFTest.termsListFromInfix( infixTerms , tracker );
		List< Substitution > subs = Resolver.unifyVar( terms.get( 0 ) , terms.get( 1 ) , new ArrayList< Substitution >() );
		Assert.assertTrue( subs.toString().equals( "[?0/?1]" ) );
	}
	
	@Test
	public void testUnifyVarBAT2() {
		//test unification of a variable with a constant that succeeds
		SymbolTracker tracker = new SymbolTracker();
		String infixTerms = "x AND 100";
		List< Term > terms = StatementCNFTest.termsListFromInfix( infixTerms , tracker );
		List< Substitution > subs = Resolver.unifyVar( terms.get( 0 ) , terms.get( 1 ) , new ArrayList< Substitution >() );
		Assert.assertTrue( subs.toString().equals( "[?0/100]" ) );
	}
	
	@Test
	public void testUnifyVarBAT3() {
		//test unification of a variable with a constant that fails
		//because the variable was already unified with something else
		SymbolTracker tracker = new SymbolTracker();
		String infixTerms = "x AND 100 AND 5";
		List< Term > terms = StatementCNFTest.termsListFromInfix( infixTerms , tracker );
		Term x = terms.get( 0 );
		Term num100 = terms.get( 1 );
		Term num5 = terms.get( 2 );
		
		Substitution s1 = new Substitution( x , num5 );
		List< Substitution > prevSubs = new ArrayList< Substitution >();
		prevSubs.add( s1 );
		
		List< Substitution > subs = Resolver.unifyVar( x , num100 , prevSubs );
		Assert.assertTrue( subs == null );
	}
	
	@Test
	public void testUnifyVarBAT4() {
		//test a repeat unification where we unify a variable again
		//with the same value. this should succeed, but it should not
		//generate a new substitution
		SymbolTracker tracker = new SymbolTracker();
		String infixTerms = "x AND 100";
		List< Term > terms = StatementCNFTest.termsListFromInfix( infixTerms , tracker );
		Term x = terms.get( 0 );
		Term num100 = terms.get( 1 );
		
		Substitution s1 = new Substitution( x , num100 );
		List< Substitution > prevSubs = new ArrayList< Substitution >();
		prevSubs.add( s1 );
		
		List< Substitution > subs = Resolver.unifyVar( x , num100 , prevSubs );
		Assert.assertTrue( subs.toString().equals( "[?0/100]" ) );
	}
	
	@Test
	public void testUnifyVarBAT5() {
		//test propagating substitutions. we unify y with 100 and then x with y
		//so the final substitutions should be "y/100" and "x/100"
		//(note that this wouldn't happen if we unified x with y and then y
		//with 100).
		SymbolTracker tracker = new SymbolTracker();
		String infixTerms = "x AND y AND 100";
		List< Term > terms = StatementCNFTest.termsListFromInfix( infixTerms , tracker );
		Term x = terms.get( 0 );
		Term y = terms.get( 1 );
		Term num100 = terms.get( 2 );
		
		Substitution s1 = new Substitution( y , num100 );
		List< Substitution > prevSubs = new ArrayList< Substitution >();
		prevSubs.add( s1 );
		
		List< Substitution > subs = Resolver.unifyVar( x , y , prevSubs );
		Assert.assertTrue( subs.toString().equals( "[?1/100, ?0/100]" ) );
	}
	
	@Test
	public void testUnifyVarOccurCheck1(){
		//test unifying a variable with its own negation. This should fail.
		//i.e. unifying x with !x should fail.
		SymbolTracker tracker = new SymbolTracker();
		String infixTerms = "x AND !x";
		List< Term > terms = StatementCNFTest.termsListFromInfix( infixTerms , tracker );
		Term x = terms.get( 0 );
		Term y = terms.get( 1 );
		
		List< Substitution > subs = Resolver.unifyVar( x , y , new ArrayList< Substitution >() );
		Assert.assertTrue( subs == null );
	}
	
	/**
	 * Mock class for testing with functions
	 */
	static class FunctionTester {
		
		public ObjectFOL Func1( ObjectFOL arg1 ) {
			return null;
		}
		
		public ObjectFOL Func2( ObjectFOL arg1 , ObjectFOL arg2 ) {
			return null;
		}
		
		public ObjectFOL Func3( ObjectFOL arg1 , ObjectFOL arg2 , ObjectFOL arg3 ) {
			return null;
		}
		
		public static SymbolTracker buildTracker() {
			SymbolTracker tracker = new SymbolTracker();
			FunctionTester definingInstance = new FunctionTester();
			Function Func1 = new Function( "Func1" , definingInstance , "Object" );
			tracker.addFunction( "Func1" , Func1 );
			
			Function Func2 = new Function( "Func2" , definingInstance , "Object" , "Object" );
			tracker.addFunction( "Func2" , Func2 );
			
			Function Func3 = new Function( "Func3" , definingInstance , "Object" , "Object" , "Object" );
			tracker.addFunction( "Func3" , Func3 );
			
			return tracker;
		}
	}
	
	@Test
	public void testUnifyFunctions1() {
		//test unifying variables within functions
		SymbolTracker tracker = FunctionTester.buildTracker();
		String infixTerms = "Func3(u,v,w) AND Func3(x,y,z)";
		List< Term > terms = StatementCNFTest.termsListFromInfix( infixTerms , tracker );
		
		List< Substitution > subs = Resolver.unify( terms.get( 0 ) , terms.get( 1 ) , new ArrayList< Substitution >() );
		Assert.assertTrue( subs.toString().equals( "[?0/?3, ?1/?4, ?2/?5]" ) );
	}
	
	@Test
	public void testUnifyFunctions2() {
		//test unifying a variable in a function with another function
		SymbolTracker tracker = FunctionTester.buildTracker();
		String infixTerms = "Func3(u,v,w) AND Func3(Func1(x),y,z)";
		List< Term > terms = StatementCNFTest.termsListFromInfix( infixTerms , tracker );
		
		List< Substitution > subs = Resolver.unify( terms.get( 0 ) , terms.get( 1 ) , new ArrayList< Substitution >() );
		Assert.assertTrue( subs.toString().equals( "[?0/Func1(?3), ?1/?4, ?2/?5]" ) );
	}
	
	@Test
	public void testUnifyFunctions3() {
		//test unifying a function with a variable (i.e. the algorithm needs
		//to flip it around and unify the variable with the function)
		SymbolTracker tracker = FunctionTester.buildTracker();
		String infixTerms = "Func3(Func1(u),v,w) AND Func3(x,y,z)";
		List< Term > terms = StatementCNFTest.termsListFromInfix( infixTerms , tracker );
		
		List< Substitution > subs = Resolver.unify( terms.get( 0 ) , terms.get( 1 ) , new ArrayList< Substitution >() );
		Assert.assertTrue( subs.toString().equals( "[?3/Func1(?0), ?1/?4, ?2/?5]" ) );
	}
	
	@Test
	public void testUnifyFunctions4() {
		//test unifying with multiply-nested functions
		SymbolTracker tracker = FunctionTester.buildTracker();
		String infixTerms = "Func3(Func3(Func1(a),Func1(b),Func1(c)),Func1(d),Func2(Func1(e),f)) AND Func3(Func3(u,v,Func1(w)),x,y)";
		List< Term > terms = StatementCNFTest.termsListFromInfix( infixTerms , tracker );
		
		List< Substitution > subs = Resolver.unify( terms.get( 0 ) , terms.get( 1 ) , new ArrayList< Substitution >() );
		Assert.assertTrue( subs.toString().equals( "[?6/Func1(?0), ?7/Func1(?1), ?2/?8, ?9/Func1(?3), ?10/Func2(Func1(?4), ?5)]" ) );
	}
	
	@Test
	public void testUnifyFunctions5() {
		//test failed unification
		SymbolTracker tracker = FunctionTester.buildTracker();
		String infixTerms = "Func3(a,b,Func1(c)) AND Func3(u,v,Func2(w,x))";
		List< Term > terms = StatementCNFTest.termsListFromInfix( infixTerms , tracker );
		
		List< Substitution > subs = Resolver.unify( terms.get( 0 ) , terms.get( 1 ) , new ArrayList< Substitution >() );
		Assert.assertTrue( subs == null );
	}
	
	@Test
	public void testUnifyFunctions6() {
		//test failed unification
		SymbolTracker tracker = FunctionTester.buildTracker();
		String infixTerms = "Func3(Func3(Func1(a),Func1(b),Func1(c)),Func1(d),Func2(Func1(e),f)) AND Func3(Func3(u,v,Func1(w)),Func2(x,y),z)";
		List< Term > terms = StatementCNFTest.termsListFromInfix( infixTerms , tracker );
		
		List< Substitution > subs = Resolver.unify( terms.get( 0 ) , terms.get( 1 ) , new ArrayList< Substitution >() );
		Assert.assertTrue( subs == null );
	}
	
	//-----test cases for unify with functions and previous substitutions-----//
	
	@Test
	public void testUnifyFunctions7() {
		//test unification for functions with previous substitutions
		//here, we substitute f/a and then we check if the substitution
		//b/f propagates to the substitution b/a
		SymbolTracker tracker = FunctionTester.buildTracker();
		String infixTerms = "Func3(Func1(a),Func1(b),Func1(c)) AND Func3(d,e,Func1(f))";
		List< Term > terms = StatementCNFTest.termsListFromInfix( infixTerms , tracker );
		
		Term Func1b = terms.get( 0 ).getArgs()[ 1 ];
		Term b = Func1b.getArgs()[ 0 ];
		Term f = terms.get( 1 ).getArgs()[ 2 ].getArgs()[ 0 ];
		
		List<Substitution> prevSubs = new ArrayList< Substitution >();
		Substitution prevSub1 = new Substitution( f , b );
		prevSubs.add( prevSub1 );
		
		List< Substitution > subs = Resolver.unify( terms.get( 0 ) , terms.get( 1 ) , prevSubs );
		Assert.assertTrue( subs.toString().equals( "[?5/?1, ?3/Func1(?0), ?4/Func1(?1), ?2/?1]" ) );
	}
	
	@Test
	public void testUnifyFunctions8() {
		//test unification for functions with previous substitutions
		//here, we substitute c/Func1(b) and then we check if the 
		//substitution f/c resolves to f/Func1(b), which resolves to
		//c/Func1(Func1(b))
		SymbolTracker tracker = FunctionTester.buildTracker();
		String infixTerms = "Func3(Func1(a),Func1(b),c) AND Func3(d,e,f)";
		List< Term > terms = StatementCNFTest.termsListFromInfix( infixTerms , tracker );
		
		Term Func1b = terms.get( 0 ).getArgs()[ 1 ];
		Term c = terms.get( 0 ).getArgs()[ 2 ];
		
		List<Substitution> prevSubs = new ArrayList< Substitution >();
		Substitution prevSub1 = new Substitution( c , Func1b );
		prevSubs.add( prevSub1 );
		
		List< Substitution > subs = Resolver.unify( terms.get( 0 ) , terms.get( 1 ) , prevSubs );
		Assert.assertTrue( subs.toString().equals( "[?2/Func1(?1), ?3/Func1(?0), ?4/Func1(?1), ?5/Func1(?1)]" ) );
	}
	
	//--------------test cases for unify with skolem functions----------------//
	
	@Test
	public void testUnifySkolemBAT1() {
		//test simple unification of a variable with a skolem function
		//that takes no arguments
		SymbolTracker tracker = new SymbolTracker();
		String infixTerms = "EXISTS(x) x AND y";
		List< Term > terms = StatementCNFTest.termsListFromInfix( infixTerms , tracker );
		
		List< Substitution > subs = Resolver.unify( terms.get( 0 ) , terms.get( 1 ) , new ArrayList< Substitution >() );
		Assert.assertTrue( subs.toString().equals( "[?1/$0()]" ) );
	}
	
	@Test
	public void testUnifySkolemBAT2() {
		//test simple unification of a variable with a skolem function
		//that takes some arguments
		SymbolTracker tracker = new SymbolTracker();
		String infixTerms = "FORALL(x,y,z) EXISTS(x) x AND y";
		List< Term > terms = StatementCNFTest.termsListFromInfix( infixTerms , tracker );
		
		List< Substitution > subs = Resolver.unify( terms.get( 0 ) , terms.get( 1 ) , new ArrayList< Substitution >() );
		Assert.assertTrue( subs.toString().equals( "[?1/$0(?0, ?1, ?2)]" ) );
	}
	
	@Test
	public void testUnifySkolemBAT3() {
		//test simple unification of two skolem functions
		SymbolTracker tracker = new SymbolTracker();
		String infixTerms = "EXISTS(x) x AND EXISTS(y) y";
		List< Term > terms = StatementCNFTest.termsListFromInfix( infixTerms , tracker );
		
		List< Substitution > subs = Resolver.unify( terms.get( 0 ) , terms.get( 1 ) , new ArrayList< Substitution >() );
		Assert.assertTrue( subs.toString().equals( "[$0()/$1()]" ) );
	}
	
	@Test
	public void testUnifySkolemWithArgs() {
		//test simple unification of two skolem functions
		SymbolTracker tracker = new SymbolTracker();
		String infixTerms = "(FORALL(a,b,c) EXISTS(x) x) AND (FORALL(d,e,f) EXISTS(y) y)";
		List< Term > terms = StatementCNFTest.termsListFromInfix( infixTerms , tracker );
		List< Substitution > subs = Resolver.unify( terms.get( 0 ) , terms.get( 1 ) , new ArrayList< Substitution >() );
		Assert.assertTrue( subs.toString().equals( "[$0(?0, ?1, ?2)/$1(?4, ?5, ?6)]" ) );
	}
	
	@Test
	public void testUnifySkolemPrevSubs1() {
		//test simple unification of two skolem functions with previous substitutions
		//previous substitutions should not really have an effect although the
		//variables appear as skolem function arguments because we don't propagate
		//variable substitutions for arguments to skolem functions at this stage yet.
		SymbolTracker tracker = new SymbolTracker();
		String infixTerms = "(FORALL(a,b,c) EXISTS(x) x) AND (FORALL(d,e,f) EXISTS(y) y) AND k AND m AND n";
		List< Term > terms = StatementCNFTest.termsListFromInfix( infixTerms , tracker );
		Term a = new Term( tracker.getSystemVariableById( 0 ) , false );
		Term b = new Term( tracker.getSystemVariableById( 1 ) , false );
		Term c = new Term( tracker.getSystemVariableById( 2 ), false );
		Term k = terms.get( 2 );
		Term m = terms.get( 3 );
		Term n = terms.get( 4 );
		List< Substitution > prevSubs = new ArrayList< Substitution >();
		Substitution prevSub1 = new Substitution( a ,k );
		prevSubs.add( prevSub1 );
		Substitution prevSub2 = new Substitution( b , m );
		prevSubs.add( prevSub2 );
		Substitution prevSub3 = new Substitution( c , n );
		prevSubs.add( prevSub3 );
		List< Substitution > subs = Resolver.unify( terms.get( 0 ) , terms.get( 1 ) , prevSubs );
		Assert.assertTrue( subs.toString().equals( "[?0/?8, ?1/?9, ?2/?10, $0(?0, ?1, ?2)/$1(?4, ?5, ?6)]" ) );
	}
	
	//-----------------end unification test cases-----------------------------//
	
	@Test
	public void testResolveBAT1() {
		//test resolving "A" with "!A", which should yield an empty disjunction
		SymbolTracker tracker = new SymbolTracker();
		String infix = "A AND !A";
		List< Disjunction > disjunctions = StatementCNFTest.disjunctionsFromInfix( infix , tracker );
		List< Disjunction > expected = Arrays.asList( new Disjunction() );
		List< Disjunction > resolveClauses = Resolver.resolve( disjunctions.get( 0 ) , disjunctions.get( 1 ) );
		Assert.assertTrue( expected.equals( resolveClauses ) );
	}
	
	@Test
	public void testResolveBAT2() {
		//test resolving "T" with "F" (true with false), which should not result in anything 
		//resolvable
		SymbolTracker tracker = new SymbolTracker();
		String infix = "T AND F";
		List< Disjunction > disjunctions = StatementCNFTest.disjunctionsFromInfix( infix , tracker );
		List< Disjunction > expected = new ArrayList< Disjunction >();
		List< Disjunction > resolveClauses = Resolver.resolve( disjunctions.get( 0 ) , disjunctions.get( 1 ) );
		Assert.assertEquals( expected , resolveClauses );
	}
	
	@Test
	public void testResolveBAT3() {
		//test resolving "A" with "!B", which should result in failed
		//unification because a variable cannot substitute for a different
		//negated variable
		SymbolTracker tracker = new SymbolTracker();
		String infix = "A AND !B";
		List< Disjunction > disjunctions = StatementCNFTest.disjunctionsFromInfix( infix , tracker );
		List< Disjunction > expected = new ArrayList< Disjunction >();
		List< Disjunction > resolveClauses = Resolver.resolve( disjunctions.get( 0 ) , disjunctions.get( 1 ) );
		Assert.assertEquals( expected , resolveClauses );
	}
	
	@Test
	public void testResolveBAT4() {
		//test resolving "A" with "B", which should result in an empty disjunction
		//because "A" and "B" are different variables and should not 
		//resolve to anything
		SymbolTracker tracker = new SymbolTracker();
		String infix = "A AND B";
		List< Disjunction > disjunctions = StatementCNFTest.disjunctionsFromInfix( infix , tracker );
		List< Disjunction > expected = new ArrayList< Disjunction >();
		List< Disjunction > resolveClauses = Resolver.resolve( disjunctions.get( 0 ) , disjunctions.get( 1 ) );
		Assert.assertEquals( expected , resolveClauses );
	}
	
	@Test
	public void testResolveSkolemBAT1() {
		//test resolving "EXISTS(x) x OR Func1(x)" with "y". When we substitute
		//y/!x, we should get "Func1(x)". When we substitute y/!Func1(x)
		//we should get "x"
		SymbolTracker tracker = FunctionTester.buildTracker();
		String infix = "(EXISTS(x) x OR Func1(x)) AND y";
		List< Disjunction > disjunctions = StatementCNFTest.disjunctionsFromInfix( infix , tracker );
		
		List< Disjunction > expected = new ArrayList< Disjunction >();
		Disjunction d1 = new Disjunction();
		d1.addTerm( disjunctions.get( 0 ).getTerm( 1 ).clone() );
		expected.add( d1 );
		
		Disjunction d2 = new Disjunction();
		d2.addTerm( disjunctions.get( 0 ).getTerm( 0 ).clone() );
		expected.add( d2 );
		
		List< Disjunction > resolveClauses = Resolver.resolve( disjunctions.get( 0 ) , disjunctions.get( 1 ) );
		Assert.assertEquals( expected , resolveClauses );
	}
	
	@Test
	public void testResolveSubstitutionPropagation2() {
		//test resolving "x OR Func1(x)" with "EXISTS(y) y". When we substitute
		//x/!y, we should get "Func1(!y)". When we substitute y/!Func1(x)
		//we should get "x"
		SymbolTracker tracker = FunctionTester.buildTracker();
		String infix = "(x OR Func1(x)) AND (EXISTS(y) y)";
		List< Disjunction > disjunctions = StatementCNFTest.disjunctionsFromInfix( infix , tracker );
		
		List< Disjunction > expected = new ArrayList< Disjunction >();
		Disjunction res1 = new Disjunction();
		Term t1 = new Term( tracker.getFunction( "Func1" ) , false , disjunctions.get( 1 ).getTerm( 0 ).clone() );
		t1.getArgs()[ 0 ].negate();
		res1.addTerm( t1 );
		expected.add( res1 );
		
		Disjunction res2 = new Disjunction();
		Term t2 = disjunctions.get( 0 ).getTerm( 0 );
		res2.addTerm( t2 );
		expected.add( res2 );
		
		List< Disjunction > resolveClauses = Resolver.resolve( disjunctions.get( 0 ) , disjunctions.get( 1 ) );
		Assert.assertEquals( expected , resolveClauses );
	}
	
	@Test
	public void testProveHypothesisBAT1() {
		//basic modus ponens:
		//P => Q, P
		//---------			should yield true
		//	  Q
		SymbolTracker tracker = new SymbolTracker();
		StatementCNF kb1 = StatementCNF.fromInfixString( "P => Q" , tracker );
		StatementCNF kb2 = StatementCNF.fromInfixString( "P" , tracker );
		StatementCNF hypothesis = StatementCNF.fromInfixString( "Q" , tracker );
		Assert.assertTrue( Resolver.proveHypothesis( tracker , hypothesis , kb1 , kb2 ) );
	}
	
	@Test
	public void testProveHypothesisBAT2() {
		//basic modus ponens:
		//P => Q, P
		//---------			should yield false
		//	  !Q
		SymbolTracker tracker = new SymbolTracker();
		StatementCNF kb1 = StatementCNF.fromInfixString( "P => Q" , tracker );
		StatementCNF kb2 = StatementCNF.fromInfixString( "P" , tracker );
		StatementCNF hypothesis = StatementCNF.fromInfixString( "!Q" , tracker );
		Assert.assertFalse( Resolver.proveHypothesis( tracker , hypothesis , kb1 , kb2 ) );
	}
	
	@Test
	public void testProveHypothesisBAT3() {
		//test using <=> which will result in many more disjunctions
		//which makes the resolution algorithm deal with many more clauses
		//P <=> Q, P
		//---------			should yield true
		//	  Q
		SymbolTracker tracker = new SymbolTracker();
		StatementCNF kb1 = StatementCNF.fromInfixString( "P <=> Q" , tracker );
		StatementCNF kb2 = StatementCNF.fromInfixString( "P" , tracker );
		StatementCNF hypothesis = StatementCNF.fromInfixString( "Q" , tracker );
		Assert.assertTrue( Resolver.proveHypothesis( tracker , hypothesis , kb1 , kb2 ) );
	}
	
	@Test
	public void testProveHypothesisBAT4() {
		//test using <=> which will result in many more disjunctions
		//which makes the resolution algorithm deal with many more clauses
		//P <=> Q, P
		//---------			should yield false
		//	  !Q
		SymbolTracker tracker = new SymbolTracker();
		StatementCNF kb1 = StatementCNF.fromInfixString( "P <=> Q" , tracker );
		StatementCNF kb2 = StatementCNF.fromInfixString( "P" , tracker );
		StatementCNF hypothesis = StatementCNF.fromInfixString( "!Q" , tracker );
		Assert.assertFalse( Resolver.proveHypothesis( tracker , hypothesis , kb1 , kb2 ) );
	}
	
	@Test
	public void testProveHypothesisBAT5() {
		//test using irrelevant information
		//P => Q, P
		//---------			should yield true, but P => Q is irrelevant
		//	  P
		SymbolTracker tracker = new SymbolTracker();
		StatementCNF kb1 = StatementCNF.fromInfixString( "P => Q" , tracker );
		StatementCNF kb2 = StatementCNF.fromInfixString( "P" , tracker );
		StatementCNF hypothesis = StatementCNF.fromInfixString( "P" , tracker );
		Assert.assertTrue( Resolver.proveHypothesis( tracker , hypothesis , kb1 , kb2 ) );
	}
	
	@Test
	public void testProveHypothesisBAT6() {
		//test chaining implications
		//P => Q, Q => R, R => S , P
		//--------------------------			should yield true
		//	  		 S
		SymbolTracker tracker = new SymbolTracker();
		StatementCNF kb1 = StatementCNF.fromInfixString( "P => Q" , tracker );
		StatementCNF kb2 = StatementCNF.fromInfixString( "Q => R" , tracker );
		StatementCNF kb3 = StatementCNF.fromInfixString( "R => S" , tracker );
		StatementCNF kb4 = StatementCNF.fromInfixString( "P" , tracker );
		StatementCNF hypothesis = StatementCNF.fromInfixString( "S" , tracker );
		Assert.assertTrue( Resolver.proveHypothesis( tracker , hypothesis , kb1 , kb2 , kb3 , kb4 ) );
	}
	
	@Test
	public void testProveHypothesisBAT7() {
		//test chaining implications
		//P => Q, Q => R, R => S , P
		//--------------------------			should yield false
		//	  		 !S
		SymbolTracker tracker = new SymbolTracker();
		StatementCNF kb1 = StatementCNF.fromInfixString( "P => Q" , tracker );
		StatementCNF kb2 = StatementCNF.fromInfixString( "Q => R" , tracker );
		StatementCNF kb3 = StatementCNF.fromInfixString( "R => S" , tracker );
		StatementCNF kb4 = StatementCNF.fromInfixString( "P" , tracker );
		StatementCNF hypothesis = StatementCNF.fromInfixString( "!S" , tracker );
		Assert.assertFalse( Resolver.proveHypothesis( tracker , hypothesis , kb1 , kb2 , kb3 , kb4 ) );
	}
	
	@Test
	public void testProveHypothesisBAT8() {
		//test with extra irrelevant information
		//P => Z , P => Q , Q => A Q => R, 
		//R => B , R => S S <=> C , P
		//----------------------------			should yield true
		//	  		 S
		SymbolTracker tracker = new SymbolTracker();
		StatementCNF kb1 = StatementCNF.fromInfixString( "P => Z" , tracker );
		StatementCNF kb2 = StatementCNF.fromInfixString( "P => Q" , tracker );
		StatementCNF kb3 = StatementCNF.fromInfixString( "Q => A" , tracker );
		StatementCNF kb4 = StatementCNF.fromInfixString( "Q => R" , tracker );
		StatementCNF kb5 = StatementCNF.fromInfixString( "R => B" , tracker );
		StatementCNF kb6 = StatementCNF.fromInfixString( "R => S" , tracker );
		StatementCNF kb7 = StatementCNF.fromInfixString( "S <=> C" , tracker );
		StatementCNF kb8 = StatementCNF.fromInfixString( "P" , tracker );
		StatementCNF hypothesis = StatementCNF.fromInfixString( "S" , tracker );
		Assert.assertTrue( Resolver.proveHypothesis( tracker , hypothesis , kb1 , kb2 , kb3 , kb4 , kb5 , kb6 , kb7 , kb8 ) );
	}
}
