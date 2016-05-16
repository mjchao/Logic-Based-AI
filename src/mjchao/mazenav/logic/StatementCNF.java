package mjchao.mazenav.logic;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Stack;

import mjchao.mazenav.logic.structures.Function;
import mjchao.mazenav.logic.structures.Operator;
import mjchao.mazenav.logic.structures.SkolemFunction;
import mjchao.mazenav.logic.structures.Symbol;
import mjchao.mazenav.logic.structures.SymbolTracker;
import mjchao.mazenav.logic.structures.Variable;

/**
 * Represents a statement in conjunctive normal form.
 * 
 * @author mjchao
 *
 */
public class StatementCNF {
	
	/**
	 * ANDs a list of StatementCNF objects together
	 * 
	 * @param statements		a list of statements in CNF to AND together
	 * @param tracker			keeps track of variable names
	 * @return					a single statement representing all the inputed
	 * 							statements ANDed together
	 */
	static StatementCNF andTogether( List< StatementCNF > statements , SymbolTracker tracker ) {
		if ( statements.size() == 0 ) {
			throw new IllegalArgumentException( "No statements to AND together." );
		}
		ExpressionTree newExprTree = statements.get( 0 ).exprTree.clone();
		for ( int i=1 ; i<statements.size() ; ++i ) {
			newExprTree.andWith( statements.get( i ).exprTree.clone() );
		}
		return fromPostfix( newExprTree , tracker );
	}
	
	/**
	 * @param statement			a statement in CNF to negate
	 * @param tracker			tracker for recognizing variables and names
	 * @return					the negated inputed statement
	 */
	static StatementCNF negate( StatementCNF statement , SymbolTracker tracker ) {
		ExpressionTree exprTreeCpy = statement.exprTree.clone();
		exprTreeCpy.negate( tracker );
		return fromPostfix( exprTreeCpy , tracker );
	}
	/**
	 * Builds a Statement CNF object from an infix logic expression.
	 * 
	 * @param infix			a logic expression in infix notation
	 * @param tracker		tracker for recognizing variables and names
	 * @return				a StatementCNF object representing the
	 * 						logic expression in CNF
	 */
	public static StatementCNF fromInfixString( String infix , SymbolTracker tracker ) {
		Tokenizer p = new Tokenizer( infix , tracker );
		ExpressionTree exprTree = new ExpressionTree( p.tokenize() );
		return fromPostfix( exprTree , tracker );
	}
	
	/**
	 * Builds a StatementCNF object from a valid
	 * postfix CNF expression. This method assumes the postfix expression 
	 * MUST be valid.
	 * 
	 * @param p				the processor object that contains the postfix
	 * 						for this expression
	 * @param tracker		tracker for recognizing variables and names
	 * @return				a StatementCNF object representing the postfix
	 * 						conjunctive normal form expression.
	 */
	private static StatementCNF fromPostfix( ExpressionTree exprTree , SymbolTracker tracker ) {
		List< Symbol > postfix = exprTree.getCNFPostfix( tracker );
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
				else if ( evalStack.size() == 1 ) {
					disjunctions.add( evalStack.pop() );
				}
				
				//in this case, we just processed two disjunctions that
				//were supposed to be ANDed together, but they've already
				//been placed onto the disjunctions list. This isn't a flaw
				//in the algorithm
				else if ( evalStack.size() == 0 ) {
					continue;
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
					Disjunction arg = evalStack.pop();
					if ( arg.size() == 1 ) {
						args[ f.getNumArgs()-1-i ] = arg.toSingleTerm();
					}
					else {
						throw new IllegalArgumentException( "Cannot have compound statements as function arguments." );
					}
				}
				evalStack.push( new Disjunction( f , args ) );
			}
			
			//if we read in a skolem function, convert it into a function-like
			//Term object
			else if ( s instanceof SkolemFunction ) {
				SkolemFunction f = (SkolemFunction) s;
				Variable[] functionArgs = f.getArgs();
				Disjunction.Term[] args = new Disjunction.Term[ functionArgs.length ];
				for ( int i=0 ; i<functionArgs.length ; ++i ) {
					args[ i ] = new Disjunction.Term( functionArgs[ i ] );
				}
				evalStack.push( new Disjunction( f , args ) );
			}
			
			//if it's not an AND or OR operator or function, it must be
			//some kind of variable
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
		return new StatementCNF( exprTree , disjunctions );
	}
	
	/**
	 * Represents a grouping of terms that have been ORed together.
	 * For example, "A OR B OR C" is a disjunction of length 3.
	 *
	 */
	public static class Disjunction {
		
		/**
		 * Represents a single term in a disjunction.
		 * This structure contains the value of the term
		 * and whether or not it is negated.
		 *
		 */
		 public static class Term {

			/**
			 * the symbol this term represents
			 */
			private final Symbol value;
			
			/**
			 * arguments to a function this term represents. 
			 * this variable is only used if this term represents a function
			 * or skolem function.
			 * otherwise, it is an empty array of size 0.
			 */
			private final Term[] args;
			
			/**
			 * if this term has been negated.
			 */
			private boolean negated;
			
			/**
			 * Creates a term to represent the a function with the given
			 * arguments. Also, optionally specify if this term has been
			 * negated. 
			 * 	
			 * @param function		the function this term represents
			 * @param negated		if the function has been negated
			 * @param args			the arguments to the function
			 */
			Term( Function function , boolean negated , Term... args ) {
				this.value = function;
				this.negated = negated;
				this.args = args;
			}
			
			/**
			 * Creates a term to represent the a skolem function with the given
			 * arguments. Also, optionally specify if this term has been
			 * negated. 
			 * 	
			 * @param skolem		the function this term represents
			 * @param negated		if the function has been negated
			 * @param args			the arguments to the function
			 */
			Term( SkolemFunction skolem , boolean negated , Term... args ) {
				this.value = skolem;
				this.negated = negated;
				this.args = args;
			}
			
			/**
			 * Creates a term to represent a non-function symbol.
			 * Also, optionally specify if this term has been negated.
			 * 
			 * @param value			the symbol this term represents
			 * @param negated		if the symbol has been negated
			 */
			Term( Symbol value , boolean negated ) {
				this.value = value;
				this.negated = negated;
				this.args = new Term[0];
			}
			
			/**
			 * Creates a term to represent a non-function symbol
			 * that has not been negated.
			 * 
			 * @param value			the symbol this term represents.
			 */
			Term( Symbol value ) {
				this( value , false );
			}
			
			/**
			 * @return		the symbol that is part of this term
			 */
			public Symbol getValue() {
				return this.value;
			}
			
			/**
			 * @return		arguments to this term, if this term
			 * 				represents a function. If this term is
			 * 				not a function, an empty array of size 0
			 * 				 is returned.
			 */
			public Term[] getArgs() {
				return this.args;
			}
			
			void substituteArg( Term original , Term substitute ) {
				for ( int i=0 ; i<args.length ; ++i ) {
					if ( args[ i ].equalsIgnoringNegated( original ) ) {
						if ( args[ i ].negated() == original.negated() ) {
							args[ i ] = substitute;
						}
						else {
							args[ i ] = substitute.clone();
							args[ i ].negate();
						}
					}
					else {
						args[ i ].substituteArg( original , substitute );
					}
				}
			}
			
			/**
			 * @return		if this term has been negated
			 */
			public boolean negated() {
				return this.negated;
			}
			
			/**
			 * negates this term.
			 */
			void negate() {
				this.negated = !this.negated;
			}
			
			/**
			 * Determines if another term is contained inside this term. 
			 * 
			 * @param t		another term
			 * @return		if this term contains the given term as an argument or
			 * 				value
			 */
			public boolean containsTerm( Term t ) {
				if ( this.equals( t ) ) {
					return true;
				}
				for ( Term arg : args ) {
					if ( arg.equals( t ) || arg.containsTermIgnoringNegated( t ) ) {
						return true;
					}
				}
				return false;
			}
			
			/**
			 * Determines if another term is contained inside this term. This
			 * is used for an occur-check
			 * 
			 * @param t		another term
			 * @return		if this term contains the given term as an argument or
			 * 				value
			 */
			public boolean containsTermIgnoringNegated( Term t ) {
				if ( this.equalsIgnoringNegated( t ) ) {
					return true;
				}
				for ( Term arg : args ) {
					if ( arg.equals( t ) || arg.containsTermIgnoringNegated( t ) ) {
						return true;
					}
				}
				return false;
			}
			
			/**
			 * @return		a string that represents the arguments
			 *				to the function this term represents
			 */
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
				String rtn = (value instanceof Function ) ? 
						value.toString() + buildArgList() : value.toString();
				//note: SkolemFunction already has its argument list built
				//in to SkolemFunction.toString() so we treat it like a
				//non-function object and don't build its arguments list again
				if ( this.negated ) {
					return "!" + rtn;
				}
				else {
					return rtn;
				}
			}
			
			/**
			 * @param other
			 * @return				if the arguments stored in this term
			 *						are equal to the arguments stored in
			 *						the other term
			 */
			private boolean argsEqual( Term other ) {
				if ( this.args.length != other.args.length ) {
					return false;
				}
				for ( int i=0 ; i<this.args.length ; ++i ) {
					if ( !this.args[ i ].equals( other.args [ i ] ) ) {
						return false;
					}
				}
				return true;
			}
			
			@Override
			public boolean equals( Object o ) {
				if ( o instanceof Term ) {
					Term t = (Term) o;
					return this.negated == t.negated &&
							this.value.equals( t.value ) &&
							this.argsEqual( t );
				}
				else {
					return false;
				}
			}
			
			public boolean equalsIgnoringNegated( Term t ) {
				return this.value.equals( t.value ) &&
						this.argsEqual( t );
			}
			
			@Override
			public int hashCode() {
				return this.value.hashCode();
			}
			
			@Override
			public Term clone() {
				if ( value instanceof Function ) {
					Function f = (Function) this.value;
					Term[] argCopy = new Term[ this.args.length ];
					for ( int i=0 ; i<argCopy.length ; ++i ) {
						argCopy[ i ] = this.args[ i ].clone();
					}
					return new Term( f , this.negated , argCopy );
				}
				else if ( value instanceof SkolemFunction ) {
					SkolemFunction f = (SkolemFunction) this.value;
					Term[] argCopy = new Term[ this.args.length ];
					for ( int i=0 ; i<argCopy.length ; ++i ) {
						argCopy[ i ] = this.args[ i ].clone();
					}
					return new Term( f , this.negated , argCopy );
				}
				else {
					return new Term( this.value , this.negated );
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
		 * Creates a Disjunction object that represents the given
		 * skolem function with specified arguments
		 * 
		 * @param f			a skolem function in this disjunction
		 * @param args		the arguments to the skolem function
		 */	
		Disjunction( SkolemFunction f , Term[] args ) {
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
		 * Creates a Term representation of the given skolem function
		 * and adds it to the end of this disjunction
		 * 
		 * @param f		the skolem function to add
		 * @param args	the arguments to the skolem function
		 */
		void addTerm( SkolemFunction f , Term[] args ) {
			this.terms.add( new Term( f , false , args ) );
		}
		
		/**
		 * @param idx
		 * @return		the term at the given index in this disjunction
		 */
		Term getTerm( int idx ) {
			return this.terms.get( idx );
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
		
		@Override
		public boolean equals( Object o ) {
			if ( o instanceof Disjunction ) {
				Disjunction d = (Disjunction) o;
				
				//two disjunctions are equal if they contain
				//the exact same terms up to reordering and duplication.
				//so, we put them all the terms of one disjunction
				//into an unordered set and check that the other
				//disjunction has the exact same terms
				HashSet< Term > otherTerms = new HashSet< Term >();
				for ( Term t : d.terms ) {
					otherTerms.add( t );
				}
				
				HashSet< Term > thisTerms = new HashSet< Term >();
				for ( Term t : this.terms ) {
					thisTerms.add( t );
				}
			
				for ( Term t : thisTerms ) {
					if ( !otherTerms.contains( t ) ) {
						return false;
					}
					otherTerms.remove( t );
				}

				return otherTerms.size() == 0;
			}
			else {
				return false;
			}
		}
		
		@Override
		public int hashCode() {
			System.err.println( "Warning: no good hash found for Disjunction yet. " + 
								"It is advised you do not hash Disjunction objects." );
			return 1;
		}
		
		@Override
		public Disjunction clone() {
			Disjunction rtn = new Disjunction();
			for ( Term t : this.terms ) {
				rtn.addTerm( t.clone() );
			}
			return rtn;
		}
	}
	
	/**
	 * the Processor object that contains the ExpressionTree
	 * used to build this StatementCNF
	 */
	private ExpressionTree exprTree;
	
	/**
	 * the disjunctions that have been ANDed together
	 * in this statement in CNF.
	 */
	private List< Disjunction > disjunctions;
	
	/**
	 * Creates a StatementCNF object that represents the
	 * given list of disjunctions ANDed together.
	 * 
	 * @param p 				the processor object for caching the
	 * 							ExpressionTree used to build this StatementCNF
	 * @param disjunctions		a list of disjunctions that should
	 * 							be ANDed together.
	 */
	StatementCNF( ExpressionTree exprTree , List< Disjunction > disjunctions ) {
		this.exprTree = exprTree;
		this.disjunctions = disjunctions;
	}
	
	/**
	 * @param term
	 * @return			if this statement contains the given term
	 */
	boolean containsTerm( Disjunction.Term term ) {
		for ( Disjunction d : disjunctions ) {
			for ( Disjunction.Term t : d.terms ) {
				if ( t.equals( term ) || t.containsTermIgnoringNegated( term ) ) {
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * @return		if this statement contains more than one term
	 */
	boolean containsMoreThanOneTerm() {
		if ( disjunctions.size() == 0 ) {
			return false;
		}
		if ( disjunctions.size() == 1 && disjunctions.size() == 1 ) {
			return false;
		}
		return true;
	}
	
	/**
	 * Gets a shallow copy of the list of disjunctions in this StatementCNF.
	 * The list should not be modified and is only used by the Resolver
	 * in performing the resolution algorithm.
	 * 
	 * @return		a shallow copy of the disjunctions in this StatementCNF.
	 */
	List< Disjunction > getDisjunctions() {
		return this.disjunctions;
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
	
	/**
	 * @return 			a deep copy of this StatementCNF. the underlying
	 * 					expression tree and disjunctions are all deep-copied.
	 */
	@Override
	public StatementCNF clone() {
		ExpressionTree exprTreeCpy = this.exprTree.clone();
		List< Disjunction > disjunctionsCpy = new ArrayList< Disjunction >();
		for ( Disjunction d : this.disjunctions ) {
			disjunctionsCpy.add( d.clone() );
		}
		StatementCNF rtn = new StatementCNF( exprTreeCpy , disjunctionsCpy );
		return rtn;
	}
}
