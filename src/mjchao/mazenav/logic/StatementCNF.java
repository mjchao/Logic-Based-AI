package mjchao.mazenav.logic;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import mjchao.mazenav.logic.structures.Operator;
import mjchao.mazenav.logic.structures.Symbol;
import mjchao.mazenav.logic.structures.SymbolTracker;

/**
 * Represents a statement in conjunctive normal form.
 * 
 * @author mjchao
 *
 */
public class StatementCNF {
	
	/**
	 * Builds a Statement CNF object from an infix logic expression.
	 * 
	 * @param infix			a logic expression in infix notation
	 * @param tracker		tracker for recognizing variables and names
	 * @return				a StatementCNF object representing the
	 * 						logic expression in CNF
	 */
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
	 * @param tracker		tracker for recognizing variables and names
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
			
			//if we read in a NOT operator and the given expression
			//was successfully converted to CNF, then we must have
			//just read in a unit type and it must be part of a
			//1-term disjunction on the stack right now. So,
			//we can go ahead and just negate it
			else if ( s.equals( Operator.NOT ) ) {
				evalStack.peek().negate();
			}
			
			//if it's not an AND or OR operator, it must be
			//some kind of variable, skolem function or unit
			//that cannot be decomposed further (i.e. operands
			//to AND and OR operators)
			else {
				evalStack.push( new Disjunction( s ) );
			}
		}
		
		//we could have an extra expression left over
		//that was never added to the list of completed
		//disjunctions if we ended with an OR operator
		if ( evalStack.size() == 1 ) {
			disjunctions.add( evalStack.pop() );
		}
		
		return new StatementCNF( disjunctions );
	}

	public static class Disjunction {
		
		private static class Term {
			public Symbol value;
			public boolean negated;
			
			public Term( Symbol value , boolean negated ) {
				this.value = value;
				this.negated = negated;
			}
			
			public Term( Symbol value ) {
				this( value , false );
			}
			
			@Override
			public String toString() {
				if ( this.negated ) {
					return "!" + value.toString();
				}
				else {
					return value.toString();
				}
			}
		}
	
		private List< Term > terms;
		
		Disjunction( List< Symbol > terms ) {
			this.terms = new ArrayList< Term >();
			for ( Symbol term : terms ) {
				this.terms.add( new Term( term ) );
			}
		}
		
		Disjunction( Symbol... terms ) {
			this.terms = new ArrayList< Term >();
			for ( Symbol term : terms ) {
				this.terms.add( new Term( term ) );
			}
		}
		
		Disjunction() {
			this( new ArrayList< Symbol >() );
		}
		
		void addTerm( Symbol s ) {
			this.terms.add( new Term(s) );
		}
		
		void addTerm( Term t ) {
			this.terms.add( t );
		}
		
		/**
		 * Merges another disjunction's terms into this one.
		 * For example, merging (A OR B) with (C OR D) gives
		 * (A OR B OR C OR D) in this disjunction.
		 * 
		 * @param other		the other disjunction to be merged in
		 */
		void mergeIn( Disjunction other ) {
			for ( Term s : other.terms ) {
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
			for ( Term s : this.terms ) {
				other.addTerm( s );
			}
		}
		
		void negate() {
			if ( size() != 1 ) {
				throw new IllegalStateException( "Input is not in CNF. Should not negate a multi-term disjunction." );
			}
			terms.get( 0 ).negated = !terms.get( 0 ).negated;
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
