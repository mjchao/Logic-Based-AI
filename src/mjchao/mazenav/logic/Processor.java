package mjchao.mazenav.logic;

import java.util.ArrayList;

import mjchao.mazenav.logic.structures.Function;
import mjchao.mazenav.logic.structures.ObjectFOL;
import mjchao.mazenav.logic.structures.Operator;
import mjchao.mazenav.logic.structures.Quantifier;
import mjchao.mazenav.logic.structures.Relation;
import mjchao.mazenav.logic.structures.Symbol;
import mjchao.mazenav.logic.structures.SymbolTracker;
import mjchao.mazenav.logic.structures.Variable;

/**
 * Processes a logic statement (e.g. tokenizing, converting to
 * conjunctive normal form, and performing other operations)
 * 
 * @author mjchao
 *
 */
public class Processor {

	private String statement;
	
	private ArrayList< Symbol > tokens;
	
	private SymbolTracker tracker;
	
	public Processor( String logicalStatement , SymbolTracker tracker ) {
		statement = logicalStatement;
		this.tracker = tracker;

	}

	//TODO test
	public void tokenize() {
		tokens = new ArrayList< Symbol >();
		
		String toTokenize = statement;
		
		//go through and make sure all shorthand operators
		//and quantifiers have a space before and after them
		for ( Operator o : Operator.OPERATOR_LIST ) {
			toTokenize.replaceAll( o.getShorthand() , " " + o.getShorthand() + " " );
		}
		for ( Quantifier q : Quantifier.QUANTIFIER_LIST ) {
			toTokenize.replaceAll( q.getShorthand() , " " + q.getShorthand() + " " );
		}
		
		//also make sure all reserved symbols such as "(", ")", and "," have
		//a space before and after them
		for ( Symbol s : Symbol.SYMBOL_LIST ) {
			toTokenize.replaceAll( s.getName() , " " + s.getName() + " " );
		}
		
		//now every token should be separated by a space
		String[] stringTokens = toTokenize.split( " " );
		
		//now we can go through and consider every token individually
		for ( int i=0 ; i<stringTokens.length ; ++i ) {
			String token = stringTokens[ i ];
			
			//check if its a symbol
			Symbol s = Symbol.parseSymbol( token );
			if ( s != null ) {
				tokens.add( s );
				continue;
			}
			
			//check if its an operator
			Operator op = Operator.parseOperator( token );
			if ( op != null ) {
				tokens.add( op );
				continue;
			}
			
			//check if its a quantifier
			Quantifier q = Quantifier.parseQuantifier( token );
			if ( q != null ) {
				tokens.add( q );
				continue;
			}
			
			//check if its a function
			Function f = tracker.getFunction( token );
			if ( f != null ) {
				tokens.add( f );
				continue;
			}
			
			//check if its a relation
			Relation r = tracker.getRelation( token );
			if ( r != null ) {
				tokens.add( r );
				continue;
			}
			
			//check if its a constant object
			ObjectFOL obj = tracker.getConstant( token );
			if ( obj != null ) {
				tokens.add( obj );
				continue;
			}
			
			//check if its a predefined constant object
			obj = tracker.getPredefinedConstant( token );
			if ( obj != null ) {
				tokens.add( obj );
				continue;
			}
			
			//otherwise, we treat it as a variable
			Variable var = tracker.getNewVariable( token );
			tokens.add( var );
			continue;
		}
	}
}
