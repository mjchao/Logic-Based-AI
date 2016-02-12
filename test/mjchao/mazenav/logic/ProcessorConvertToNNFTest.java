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
		SymbolTracker tracker = new SymbolTracker();
		Processor p = new Processor( "" , tracker );
		List< Symbol > input = new ArrayList< Symbol >( Arrays.asList( tracker.getNewVariable( "x" ) ) );
		List< Symbol > expected = Arrays.asList( Operator.NOT , tracker.getVariableByName( "x" ) );
		List< Symbol > found = negate( p , input );
		Assert.assertTrue( expected.equals( found ) );
	}
}
