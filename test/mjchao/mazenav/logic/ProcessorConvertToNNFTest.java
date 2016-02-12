package mjchao.mazenav.logic;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import mjchao.mazenav.logic.structures.Operator;
import mjchao.mazenav.logic.structures.Symbol;
import mjchao.mazenav.logic.structures.SymbolTracker;
import mjchao.mazenav.logic.structures.Variable;

public class ProcessorConvertToNNFTest {

	public List< Symbol > negate( Processor p , List< Symbol > input ) {
		Class<?> c = Processor.class;
		try {
			Method f = c.getDeclaredMethod( "negate" , List.class );
			f.setAccessible( true );
			return (List<Symbol>) f.invoke( p , input );
		} catch (IllegalArgumentException | IllegalAccessException | SecurityException | NoSuchMethodException | InvocationTargetException e) {
			e.printStackTrace();
			throw new RuntimeException( "Could not apply negate() method to Processor object." );
		}		
	}
	
	@Test
	public void testNegateAlreadyInNNF() {
		SymbolTracker tracker;
		Processor p;
		List< Symbol > input;
		List< Symbol > expected;
		List< Symbol > found;
		
		//basic acceptance tests
		tracker = new SymbolTracker();
		p = new Processor( "" , tracker );
		input = new ArrayList< Symbol >( Arrays.asList( tracker.getNewVariable( "x" ) ) );
		expected = Arrays.asList( Operator.NOT , tracker.getVariableByName( "x" ) );
		found = negate( p , input );
		Assert.assertTrue( expected.equals( found ) );
		
		tracker = new SymbolTracker();
		p = new Processor( "" , tracker );
		input = new ArrayList< Symbol >( Arrays.asList( 
				Operator.LEFT_PAREN , tracker.getNewVariable( "x" ) , Operator.RIGHT_PAREN ) );
		expected = Arrays.asList( 
				Operator.LEFT_PAREN , Operator.NOT , 
				tracker.getVariableByName( "x" ) , Operator.RIGHT_PAREN );
		found = negate( p , input );
		Assert.assertTrue( expected.equals( found ) );
		
		tracker = new SymbolTracker();
		p = new Processor( "" , tracker );
		input = new ArrayList< Symbol >( Arrays.asList( 
				tracker.getNewVariable( "x" ) , Operator.AND , tracker.getNewVariable( "y" ) ) );
		expected = Arrays.asList( 
				Operator.NOT , tracker.getVariableByName( "x" ) , Operator.OR , 
				Operator.NOT , tracker.getVariableByName( "y" ) );
		found = negate( p , input );
		Assert.assertTrue( expected.equals( found ) );
	}
}
