package mjchao.mazenav.logic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import mjchao.mazenav.logic.Resolver.Substitution;
import mjchao.mazenav.logic.StatementCNF.Disjunction.Term;
import mjchao.mazenav.logic.structures.NumbersFOL;
import mjchao.mazenav.logic.structures.SymbolTracker;
import mjchao.mazenav.logic.structures.Variable;

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
		List< Substitution > prevSubs = Arrays.asList( s1 );
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
		List< Substitution > prevSubs = Arrays.asList( s1 );
		
		List< Substitution > subs = Resolver.unifyVar( x , num100 , prevSubs );
		Assert.assertTrue( subs.toString().equals( "[?0/100]" ) );
	}
	
}
