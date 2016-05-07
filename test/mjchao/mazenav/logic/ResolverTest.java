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
		//test unifying with multiply-nested functions
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
	
	//-----test cases for unify with functions and previous substitutions-----//
	
	@Test
	public void testUnifyFunctions7() {
		//test unification for functions with previous substitutions
		//here, we substitute f/a and then we check if the substitution
		//b/f propagates to the substitution b/a
		SymbolTracker tracker = UnifyFunctionTests.buildTracker();
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
		SymbolTracker tracker = UnifyFunctionTests.buildTracker();
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
	
	//-----test cases for unify with skolem functions and previous substitutions-----//
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
}
