package mjchao.mazenav.logic;

import java.util.ArrayList;
import java.util.List;

import mjchao.mazenav.logic.Resolver.Substitution;
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
	
	/**
	 * Mock class for testing unification with functions
	 */
	static class UnifyFunctionTests {
		
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
			UnifyFunctionTests definingInstance = new UnifyFunctionTests();
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
		SymbolTracker tracker = UnifyFunctionTests.buildTracker();
		String infixTerms = "Func3(u,v,w) AND Func3(x,y,z)";
		List< Term > terms = StatementCNFTest.termsListFromInfix( infixTerms , tracker );
		
		List< Substitution > subs = Resolver.unify( terms.get( 0 ) , terms.get( 1 ) , new ArrayList< Substitution >() );
		Assert.assertTrue( subs.toString().equals( "[?0/?3, ?1/?4, ?2/?5]" ) );
	}
	
	@Test
	public void testUnifyFunctions2() {
		//test unifying a variable in a function with another function
		SymbolTracker tracker = UnifyFunctionTests.buildTracker();
		String infixTerms = "Func3(u,v,w) AND Func3(Func1(x),y,z)";
		List< Term > terms = StatementCNFTest.termsListFromInfix( infixTerms , tracker );
		
		List< Substitution > subs = Resolver.unify( terms.get( 0 ) , terms.get( 1 ) , new ArrayList< Substitution >() );
		Assert.assertTrue( subs.toString().equals( "[?0/Func1(?3), ?1/?4, ?2/?5]" ) );
	}
	
	@Test
	public void testUnifyFunctions3() {
		//test unifying a function with a variable (i.e. the algorithm needs
		//to flip it around and unify the variable with the function)
		SymbolTracker tracker = UnifyFunctionTests.buildTracker();
		String infixTerms = "Func3(Func1(u),v,w) AND Func3(x,y,z)";
		List< Term > terms = StatementCNFTest.termsListFromInfix( infixTerms , tracker );
		
		List< Substitution > subs = Resolver.unify( terms.get( 0 ) , terms.get( 1 ) , new ArrayList< Substitution >() );
		Assert.assertTrue( subs.toString().equals( "[?3/Func1(?0), ?1/?4, ?2/?5]" ) );
	}
	
	@Test
	public void testUnifyFunctions4() {
		//test unifying with doubly-nested functions
		SymbolTracker tracker = UnifyFunctionTests.buildTracker();
		String infixTerms = "Func3(Func3(Func1(a),Func1(b),Func1(c)),Func1(d),Func2(Func1(e),f)) AND Func3(Func3(u,v,Func1(w)),x,y)";
		List< Term > terms = StatementCNFTest.termsListFromInfix( infixTerms , tracker );
		
		List< Substitution > subs = Resolver.unify( terms.get( 0 ) , terms.get( 1 ) , new ArrayList< Substitution >() );
		Assert.assertTrue( subs.toString().equals( "[?6/Func1(?0), ?7/Func1(?1), ?2/?8, ?9/Func1(?3), ?10/Func2(Func1(?4), ?5)]" ) );
	}
	
	@Test
	public void testUnifyFunctions5() {
		//test failed unification
		SymbolTracker tracker = UnifyFunctionTests.buildTracker();
		String infixTerms = "Func3(a,b,Func1(c)) AND Func3(u,v,Func2(w,x))";
		List< Term > terms = StatementCNFTest.termsListFromInfix( infixTerms , tracker );
		
		List< Substitution > subs = Resolver.unify( terms.get( 0 ) , terms.get( 1 ) , new ArrayList< Substitution >() );
		Assert.assertTrue( subs == null );
	}
	
	@Test
	public void testUnifyFunctions6() {
		//test failed unification
		SymbolTracker tracker = UnifyFunctionTests.buildTracker();
		String infixTerms = "Func3(Func3(Func1(a),Func1(b),Func1(c)),Func1(d),Func2(Func1(e),f)) AND Func3(Func3(u,v,Func1(w)),Func2(x,y),z)";
		List< Term > terms = StatementCNFTest.termsListFromInfix( infixTerms , tracker );
		
		List< Substitution > subs = Resolver.unify( terms.get( 0 ) , terms.get( 1 ) , new ArrayList< Substitution >() );
		Assert.assertTrue( subs == null );
	}
	
}
