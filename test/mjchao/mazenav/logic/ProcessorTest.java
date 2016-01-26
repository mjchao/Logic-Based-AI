package mjchao.mazenav.logic;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import mjchao.mazenav.logic.structures.NumbersFOL;
import mjchao.mazenav.logic.structures.Operator;
import mjchao.mazenav.logic.structures.Quantifier;
import mjchao.mazenav.logic.structures.Symbol;
import mjchao.mazenav.logic.structures.SymbolTracker;
import mjchao.mazenav.logic.structures.Variable;

public class ProcessorTest {

	/**
	 * Accessor to the private tokens field of the Processor
	 * class.
	 * 
	 * @param p
	 * @return
	 */
	public ArrayList< Symbol > getTokens( Processor p ) {
		Class<?> c = Processor.class;
		try {
			Field f = c.getDeclaredField( "tokens" );
			f.setAccessible( true );
			return (ArrayList<Symbol>) f.get( p );
		} catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
			for ( Field f : c.getDeclaredFields() ) {
				System.out.println( f.getName() );
			}
			e.printStackTrace();
			throw new RuntimeException( "Could not apply getTokens() method to Processor object." );
		}
	}
	
	@Test
	public void testWithoutStructures() {
		//test without having to preload a SymbolTracker from
		//some file
		
		Processor test;
		String logicStatement;
		SymbolTracker tracker = new SymbolTracker();
		List<Symbol> tokens;
		List<Symbol> expected;
		
		logicStatement = "FORALL x, x==1";
		test = new Processor( logicStatement , tracker );
		test.tokenize();
		tokens = getTokens( test );
		expected = Arrays.asList( Quantifier.FORALL , tracker.getVariableByName( "x" ) , 
				Symbol.COMMA , tracker.getVariableByName( "x" ) , 
				Operator.EQUALS , NumbersFOL.fromInt( 1 ) );
		Assert.assertTrue( tokens.equals( expected ) );
	}
}