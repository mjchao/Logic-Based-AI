package mjchao.mazenav.logic;

import org.junit.Assert;
import org.junit.Test;

import mjchao.mazenav.logic.structures.SymbolTracker;

public class StatementCNFTest {

	//BAT = Basic Acceptance Test
	@Test
	public void testBuildBAT1() {
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
		String infix = "(A) AND (B) AND (C) AND (D)";
		SymbolTracker tracker = new SymbolTracker();
		StatementCNF cnf = StatementCNF.fromInfixString( infix , tracker );
		Assert.assertEquals( "(?0) AND (?1) AND (?2) AND (?3)" , cnf.toString() );
	}
	
	@Test
	public void testBuildBAT4() {
		String infix = "(A) AND (B OR C) AND (D)";
		SymbolTracker tracker = new SymbolTracker();
		StatementCNF cnf = StatementCNF.fromInfixString( infix , tracker );
		Assert.assertEquals( "(?0) AND (?1 OR ?2) AND (?3)" , cnf.toString() );
	}
	
	@Test
	public void testBuildBAT5() {
		String infix = "(A OR B) AND (C) AND (D)";
		SymbolTracker tracker = new SymbolTracker();
		StatementCNF cnf = StatementCNF.fromInfixString( infix , tracker );
		Assert.assertEquals( "(?0 OR ?1) AND (?2) AND (?3)" , cnf.toString() );
	}
	
	@Test
	public void testBuildBAT6() {
		String infix = "(!A OR B) AND (A OR B)";
		SymbolTracker tracker = new SymbolTracker();
		StatementCNF cnf = StatementCNF.fromInfixString( infix , tracker );
		Assert.assertEquals( "(!?0 OR ?1) AND (?0 OR ?1)" , cnf.toString() );
	}
	
	@Test
	public void testBuildBAT7() {
		String infix = "(!A OR !B) AND (!A OR !B)";
		SymbolTracker tracker = new SymbolTracker();
		StatementCNF cnf = StatementCNF.fromInfixString( infix , tracker );
		Assert.assertEquals( "(!?0 OR !?1) AND (!?0 OR !?1)" , cnf.toString() );
	}
	
	@Test
	public void testBuildBAT8() {
		String infix = "(!!!A OR !B) AND (!A OR !B)";
		SymbolTracker tracker = new SymbolTracker();
		StatementCNF cnf = StatementCNF.fromInfixString( infix , tracker );
		Assert.assertEquals( "(!?0 OR !?1) AND (!?0 OR !?1)" , cnf.toString() );
	}
	
	@Test
	public void testBuildBAT9() {
		String infix = "!!!A OR B";
		SymbolTracker tracker = new SymbolTracker();
		StatementCNF cnf = StatementCNF.fromInfixString( infix , tracker );
		Assert.assertEquals( "!?0 OR ?1" , cnf.toString() );
	}
	
	@Test
	public void testBuildBAT10() {
		String infix = "A";
		SymbolTracker tracker = new SymbolTracker();
		StatementCNF cnf = StatementCNF.fromInfixString( infix , tracker );
		Assert.assertEquals( "?0" , cnf.toString() );
	}
}
