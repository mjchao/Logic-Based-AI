package mjchao.mazenav.logic;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Stack;

import mjchao.mazenav.logic.structures.Function;
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
		Queue< Symbol > postfix = new LinkedList< Symbol >( p.process() );
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
	private static StatementCNF fromPostfix( Queue< Symbol > postfix , SymbolTracker tracker ) {
		Stack< Disjunction > evalStack = new Stack< Disjunction >();
		ArrayList< Disjunction > disjunctions = new ArrayList< Disjunction >();

		//apply an algorithm to evaluate postfix:
		
		while( !postfix.isEmpty() ) {
			Symbol s = postfix.poll();
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
			
			//if we read in a function, we need to keep popping off
			//its arguments into a single term
			else if ( s instanceof Function ) {
				Function f = (Function) s;
				Disjunction.Term[] args = new Disjunction.Term[ f.getNumArgs() ];
				for ( int i=0 ; i<f.getNumArgs() ; ++i ) {
					
					//arguments to the function are read off the stack
					//in reverse order, so we fill the arguments list
					//starting from the back
					args[ f.getNumArgs()-1-i ] = evalStack.pop().toSingleTerm();
				}
				evalStack.push( new Disjunction( f , args ) );
			}
			
			//if it's not an AND or OR operator or function, it must be
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
	
	/**
	 * Represents a grouping of terms that have been ORed together.
	 * For example, "A OR B OR C" is a disjunction of length 3.
	 *
	 */
	public static class Disjunction {
		
		static void resolve( Disjunction d1 , Disjunction d2 ) {
			//TODO
		}
		
		/**
		 * Represents a single term in a disjunction.
		 * This structure contains the value of the term
		 * and whether or not it is negated.
		 *
		 */
		private static class Term {
			
			static void unify( Term t1 , Term t2 ) {
				//TODO
				
			}
			
			public Symbol value;
			public Term[] args;
			public boolean negated;
			
			public Term( Function function , boolean negated , Term... args ) {
				this.value = function;
				this.negated = negated;
				this.args = args;
			}
			
			public Term( Symbol value , boolean negated ) {
				this.value = value;
				this.negated = negated;
			}
			
			public Term( Symbol value ) {
				this( value , false );
			}
			
			private String buildArgList() {
				if ( this.args.length == 0 ) {
					return "()";
				}
				else {
					StringBuilder rtn = new StringBuilder( "(" );
					rtn.append( args[ 0 ].toString() );
					for ( int i=1 ; i<args.length ; ++i ) {
						rtn.append( ", " );
						rtn.append( args[ i ].toString() );
					}
					rtn.append( ")" );
					return rtn.toString();
				}
			}
			
			@Override
			public String toString() {
				String rtn = (value instanceof Function) ? 
						value.toString() + buildArgList() : value.toString();
				if ( this.negated ) {
					return "!" + rtn;
				}
				else {
					return rtn;
				}
			}
		}
	
		/**
		 * the terms in this disjunction that are all ORed together.
		 */
		private List< Term > terms;
		
		/**
		 * Converts a list of Symbol objects into Term objects
		 * and creates a Disjunction object that represents those terms
		 * ORed together.
		 * 
		 * @param terms		the terms that are part of this disjunction
		 */
		Disjunction( List< Symbol > terms ) {
			this.terms = new ArrayList< Term >();
			for ( Symbol term : terms ) {
				this.terms.add( new Term( term ) );
			}
		}
		
		/**
		 * Creates a Disjunction object that represents the given
		 * terms ORed together.
		 * 
		 * @param terms		the terms that are part of this disjunction
		 */
		Disjunction( Symbol... terms ) {
			this.terms = new ArrayList< Term >();
			for ( Symbol term : terms ) {
				this.terms.add( new Term( term ) );
			}
		}
		
		/**
		 * Creates a Disjunction object that represents the given
		 * function with specified arguments
		 * 
		 * @param f			a function in this disjunction
		 * @param args		the arguments to the function
		 */	
		Disjunction( Function f , Term[] args ) {
			this();
			addTerm( f , args );
		}
		
		/**
		 * Creates an empty disjunction with no terms.
		 */
		Disjunction() {
			this( new ArrayList< Symbol >() );
		}
		
		/**
		 * Creates a Term representation of the given symbol 
		 * and adds it to the end of this disjunction
		 * 
		 * @param s		the symbol to add
		 */
		void addTerm( Symbol s ) {
			this.terms.add( new Term(s) );
		}
		
		/**
		 * Adds the given term to the end of this disjunction.
		 * 
		 * @param t		the term to add
		 */
		void addTerm( Term t ) {
			this.terms.add( t );
		}
		
		/**
		 * Creates a Term representation of the given function
		 * and adds it to the end of this disjunction
		 * 
		 * @param f		the function to add
		 * @param args	the arguments to the function
		 */
		void addTerm( Function f , Term[] args ) {
			this.terms.add( new Term( f , false , args ) );
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
		 * (C OR D OR A OR B) in the other disjunction.
		 * 
		 * @param other		the other disjunction to be merged into
		 */
		void mergeInto( Disjunction other ) {
			for ( Term s : this.terms ) {
				other.addTerm( s );
			}
		}
		
		/**
		 * Negates this disjunction when it has exactly one term in it.
		 * This throws an exception if the disjunction has more than
		 * one term in it.
		 * 
		 * This is used by the StatementCNF builder when it processes
		 * a NOT operator that is used to negate a single variable
		 * (e.g. when we have the expression "(!x OR !y)" we need 
		 * the x and y terms to indicate they are negated.
		 */
		void negate() {
			if ( size() != 1 ) {
				throw new IllegalStateException( "Input is not in CNF. " + 
							" Should not negate a multi-term disjunction." );
			}
			terms.get( 0 ).negated = !terms.get( 0 ).negated;
		}
		
		/**
		 * @return		the number of terms in this disjunction. For example,
		 * 				(x OR y OR z) has three terms so its size is 3.
		 */
		public int size() {
			return this.terms.size();
		}
		
		/**
		 * Attempts to convert this disjunction to a single term. This
		 * process only succeeds when this disjunction has only one term
		 * in it.
		 * 
		 * @return							the sole term in this disjunction as 
		 * 									a Term object
		 * @throws IllegalStateException	if this disjunction has multiple terms
		 */
		Term toSingleTerm() {
			if ( size() == 1 ) {
				return this.terms.get( 0 );
			}
			else {
				throw new IllegalStateException( "Cannot convert multi-term disjunction to a single term." );
			}
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
	
	/**
	 * the disjunctions that have been ANDed together
	 * in this statement in CNF.
	 */
	private List< Disjunction > disjunctions;
	
	/**
	 * Creates a StatementCNF object that represents the
	 * given list of disjunctions ANDed together.
	 * 
	 * @param disjunctions		a list of disjunctions that should
	 * 							be ANDed together.
	 */
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
