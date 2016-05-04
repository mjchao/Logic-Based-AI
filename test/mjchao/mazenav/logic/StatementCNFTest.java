package mjchao.mazenav.logic;

import java.io.IOException;

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
}
