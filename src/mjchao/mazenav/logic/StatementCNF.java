package mjchao.mazenav.logic;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import mjchao.mazenav.logic.structures.Operator;
import mjchao.mazenav.logic.structures.Symbol;
import mjchao.mazenav.logic.structures.SymbolTracker;

/**
 * Represents a statement in conjunctive normal form.
 * @author mjchao
 *
 */
public class StatementCNF {
	
	public static StatementCNF fromInfixString( String infix , SymbolTracker tracker ) {
		Processor p = new Processor( infix , tracker );
		List< Symbol > postfix = p.process();
		return fromPostfix( postfix , tracker );
	}
	
	/**
	 * Builds a StatementCNF object from a valid
	 * postfix CNF expression. This method assumes the postfix expression 
	 * MUST be valid.
	 * 
	 * @param postfix		a valid postfix expression
	 * @param tracker		tracker for the variables and names that we
	 * 						recognize
	 * @return				a StatementCNF object representing the postfix
	 * 						conjunctive normal form expression.
	 */
	private static StatementCNF fromPostfix( List< Symbol > postfix , SymbolTracker tracker ) {
		Stack< Disjunction > evalStack = new Stack< Disjunction >();
		ArrayList< Disjunction > disjunctions = new ArrayList< Disjunction >();

		//apply an algorithm to evaluate postfix:
		for ( Symbol s : postfix ) {
			if ( s.equals( Operator.OR ) ) {
				
				//OR the next 2 disjunctions on the stack together
				Disjunction d = evalStack.pop();
				d.mergeInto( evalStack.peek() );
			}
			else if ( s.equals( Operator.AND ) ) {
				
				//in this case, we are ANDing with another
				//OR statement that is still on the stack
				if ( evalStack.size() >= 2 ) {
					
					//the next two disjunctions on the stack are complete
					//and can be added to the list of complete disjunctions
					//note: the operands are read backwards off the stack
					//so we need to add them to the list in reverse order
					Disjunction operand2 = evalStack.pop();
					Disjunction operand1 = evalStack.pop();
					disjunctions.add( operand1 );
					disjunctions.add( operand2 );
				}
				
				//in this case, we are ANDing with another
				//AND statement that has already been added
				//to the output list
				else {
					disjunctions.add( evalStack.pop() );
				}
			}
			
			//if it's not an AND or OR operator, it must be
			//some kind of variable, skolem function or unit
			//that cannot be decomposed further (i.e. operands
			//to AND and OR operators)
			else {
				evalStack.push( new Disjunction( s ) );
			}
		}
		
		return new StatementCNF( disjunctions );
	}

	public static class Disjunction {
	
		private List< Symbol > terms;
		
		Disjunction( List< Symbol > terms ) {
			this.terms = terms;
		}
		
		Disjunction( Symbol... terms ) {
			this.terms = new ArrayList< Symbol >();
			for ( Symbol term : terms ) {
				this.terms.add( term );
			}
		}
		
		Disjunction() {
			this( new ArrayList< Symbol >() );
		}
		
		void addTerm( Symbol s ) {
			this.terms.add( s );
		}
		
		/**
		 * Merges another disjunction's terms into this one.
		 * For example, merging (A OR B) with (C OR D) gives
		 * (A OR B OR C OR D) in this disjunction.
		 * 
		 * @param other		the other disjunction to be merged in
		 */
		void mergeIn( Disjunction other ) {
			for ( Symbol s : other.terms ) {
				addTerm( s );
			}
		}
		
		/**
		 * Merges this disjunction's terms into another one.
		 * For example, merging (A OR B) into (C OR D) gives
		 * (C OR D OR A OR B) in the other disjuncion.
		 * 
		 * @param other		the other disjunction to be merged into
		 */
		void mergeInto( Disjunction other ) {
			for ( Symbol s : this.terms ) {
				other.addTerm( s );
			}
		}
		
		public int size() {
			return this.terms.size();
		}
		
		@Override
		public String toString() {
			if ( size() == 0 ) {
				return "";
			}
			else if ( size() == 1 ) {
				return terms.get( 0 ).toString();
			}
			else {
				StringBuilder rtn = new StringBuilder( "" );
				rtn.append( terms.get( 0 ).toString() );
				for ( int i=1 ; i<terms.size() ; ++i ) {
					rtn.append( " OR " );
					rtn.append( terms.get( i ).toString() );
				}
				return rtn.toString();
			}
		}
	}
	
	private List< Disjunction > disjunctions;
	
	StatementCNF( List< Disjunction > disjunctions ) {
		this.disjunctions = disjunctions;
	}
	
	@Override
	public String toString() {
		if ( disjunctions.size() == 0 ) {
			return "";
		}
		else if ( disjunctions.size() == 1 ) {
			return disjunctions.get( 0 ).toString();
		}
		else {
			StringBuilder rtn = new StringBuilder( "" );
			rtn.append( "(" );
			rtn.append( disjunctions.get( 0 ).toString() );
			rtn.append( ")" );
			for ( int i=1 ; i<disjunctions.size() ; ++i ) {
				rtn.append( " AND " );
				rtn.append( "(" );
				rtn.append( disjunctions.get( i ).toString() );
				rtn.append( ")" );
			}
			return rtn.toString();
		}
	}
}
