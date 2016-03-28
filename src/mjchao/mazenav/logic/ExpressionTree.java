package mjchao.mazenav.logic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Stack;

import mjchao.mazenav.logic.structures.Function;
import mjchao.mazenav.logic.structures.ObjectFOL;
import mjchao.mazenav.logic.structures.Operator;
import mjchao.mazenav.logic.structures.Quantifier;
import mjchao.mazenav.logic.structures.Symbol;
import mjchao.mazenav.logic.structures.SymbolTracker;
import mjchao.mazenav.logic.structures.Variable;

/**
 * Represents an infix boolean expression in the
 * following compact form without parentheses:
 * 
 * <pre>
 *            [operator]
 *           /    | ... \
 *        [arg0][arg1]...[argn]
 * </pre>
 * where child arguments may be additional
 * complex expressions.
 * 
 * @author mjchao
 *
 */
class ExpressionTree {
	
	/**
	 * Represents a list of variables that are quntified, such as
	 * "FORALL( x, y, z)" or "FORALL x" that should
	 * be treated as a unit in the expression.
	 * 
	 * @author mjchao
	 *
	 */
	static class QuantifierList extends Symbol {
		
		private final Quantifier quantifier;
		private final ArrayList< Variable > vars = new ArrayList< Variable >();
		
		public QuantifierList( Quantifier quantifier ) {
			super( quantifier.getShorthand() + "(...)" );
			this.quantifier = quantifier;
		}
		
		public QuantifierList( Quantifier quantifier , Variable[] vars ) {
			this( quantifier );
			for ( Variable var : vars ) {
				this.vars.add( var );
			}
		}
		
		public QuantifierList( Quantifier quantifier , List< Variable > vars ) {
			this( quantifier );
			for ( Variable var : vars ) {
				this.vars.add( var );
			}
		}
		
		public Quantifier getQuantifier() {
			return this.quantifier;
		}
		
		/**
		 * @return		a reference to the variables in this quantifier list. 
		 * 				Don't modify it.
		 */
		public ArrayList< Variable > getVariables() {
			return this.vars;
		}
		
		public boolean isEmpty() {
			return vars.size() == 0;
		}
		
		public void addVariable( Variable var ) {
			this.vars.add( var );
		}
		
		/**
		 * Substitutes a system-defined variable for a user-defined variable
		 * in this quantifier list.
		 * 
		 * @param userDefined
		 * @param systemDefined
		 */
		public void standardizeVariable( Variable userDefined , Variable systemDefined ) {
			for ( int i=0 ; i<vars.size() ; ++i ) {
				if ( vars.get( i ).equals( userDefined ) ) {
					vars.set( i , systemDefined );
				}
			}
		}
		
		@Override
		public String getSymbolName() {
			StringBuilder rtn = new StringBuilder();
			rtn.append( quantifier.getShorthand() );
			rtn.append( "(" );
			if ( vars.size() >= 1 ) {
				rtn.append( vars.get( 0 ).getShorthand() );
			}
			for ( int i=1 ; i<vars.size() ; ++i ) {
				rtn.append( ", " );
				rtn.append( vars.get( i ).getShorthand() );
			}
			rtn.append( ")" );
			return rtn.toString();
		}
		
		@Override
		public String getShorthand() {
			return getSymbolName();
		}
		
		@Override
		public boolean equals( Object o ) {
			if ( o instanceof QuantifierList ) {
				QuantifierList other = (QuantifierList) o;
				return this.quantifier.equals( other.quantifier ) &&
						this.vars.equals( other.vars );
			}
			else {
				return false;
			}
		}
	}
	
	/**
	 * Converts an infix boolean expression into postfix. Applies a modifier
	 * version of Dijkstra's Shunting Yard Algorithm that incorporates
	 * quantifiers.
	 * 
	 * @param infix		an infix expression
	 * @return			the infix expression converted to postfix
	 */
	private static ArrayList< Symbol > convertToPostfix( List< Symbol > infix ) {
		ArrayList< Symbol > outputQueue = new ArrayList< Symbol >();
		Stack< Symbol > operatorStack = new Stack< Symbol >();
		
		//if we've entered a quantifier list, we
		//need to aggregate variables into the list
		//to make a single token. This is null if we
		//haven't entered a quantifier list.
		QuantifierList currQuantifier = null;
		
		//keeps track of whether the quantifier is just
		//for a single variable, e.g. "FORALL x", or if it
		//is for multiple variables e.g. "FORALL(x, y,z)"
		boolean isMultiQuantifier = false;
		
		for ( Symbol currToken : infix ) {
			
			if ( currToken.equals( Symbol.SUCH_THAT ) ) {
				//SUCH_THAT is just a "filler" word for
				//to make expressions sound nice. we just discard it.
				continue;
			}
			
			//if the token is some kind of object
			//we immediately add to postfix output
			else if ( currToken instanceof ObjectFOL ) {
				outputQueue.add( currToken );
			}
			
			//if the token is a variable, check if 
			//it belongs to a quantifier expression or
			//if it should go directly to output
			else if ( currToken instanceof Variable ) {
				if ( operatorStack.empty() ) {
					outputQueue.add( currToken );
				}
				else {
					
					if ( currQuantifier == null ) {
						outputQueue.add( currToken );
					}
					else {
						
						if ( isMultiQuantifier ) {
							currQuantifier.addVariable( (Variable) currToken );
						}
						
						//if we read a variable right after a quantifier listing,
						//then there were no parentheses so the quantifier is for
						//just this one variable. We can add this variable to the
						//quantifier list directly and then add the entire
						//quantifier list to the output queue
						else {
							
							currQuantifier.addVariable( (Variable) currToken ); 
							currQuantifier = null;
						}
					}
				}
			}
			else if ( currToken instanceof Function ) {
				operatorStack.push( currToken );
			}
			else if ( currToken instanceof Quantifier ) {
				QuantifierList newQuantifier = new QuantifierList( (Quantifier) currToken );
				operatorStack.push( newQuantifier );
				currQuantifier = newQuantifier;
				
				//turn off the multi-quantifier flag. we'll turn
				//it back on if we read a left parentheses right after
				isMultiQuantifier = false;
			}
			
			//commas serve as function argument separators
			else if ( currToken.equals( Symbol.COMMA ) ) {
				if ( operatorStack.empty() ) {
					throw new IllegalArgumentException( "Missing left parenthesis." );
				}
				
				if ( operatorStack.peek() instanceof QuantifierList ) {
					
					//it's okay if a comma comes after a quantifier.
					//e.g. "FORALL x, y" is acceptable
					continue;
				}
				else {
				
					//remove all operators until we reach the start of the
					//function
					while( !operatorStack.peek().equals( Symbol.LEFT_PAREN ) ) {
						
						//make sure we aren't quantifying over an
						//operator or function because that's not allowed in FOL
						if ( currQuantifier != null ) {
							throw new IllegalArgumentException( "Cannot quantify over operators/functions in FOL." );
						}
						
						outputQueue.add( operatorStack.pop() );
						if ( operatorStack.empty() ) {
							throw new IllegalArgumentException( "Missing left parenthesis." );
						}
					}
				}
			}
			else if ( currToken instanceof Operator ) {
				Operator op = (Operator) currToken;
				
				//get rid of any operators that should be
				//executed before the current operator
				while( !operatorStack.empty() && operatorStack.peek() instanceof Operator &&
						( (Operator)operatorStack.peek()).preceedsLeftToRight( op ) ) {
					Operator toAdd = (Operator) operatorStack.pop();
					outputQueue.add( toAdd );
					
					//check if there's a quantifier to be added
					//to an implication or biconditional expression
					if ( toAdd.equals( Operator.IMPLICATION ) || toAdd.equals( Operator.BICONDITIONAL ) ) {
						if ( !operatorStack.empty() && operatorStack.peek() instanceof QuantifierList ) {
							outputQueue.add( operatorStack.pop() );
						}
					}
				}
				
				operatorStack.push( op );
			}
			else if ( currToken.equals( Symbol.LEFT_PAREN ) ) {
				
				//turn on the multi-quantifier flag 
				//if this is a left parentheses right after a quantifier
				if ( currQuantifier != null && !operatorStack.empty() ) {
					if ( operatorStack.peek() == currQuantifier ) {
						isMultiQuantifier = true;
					}
				}
				
				operatorStack.push( currToken );
			}
			else if ( currToken.equals( Symbol.RIGHT_PAREN ) ) {
				if ( operatorStack.empty() ) {
					throw new IllegalArgumentException( "Missing left parenthesis." );
				}
				while( !operatorStack.peek().equals( Symbol.LEFT_PAREN ) ) {
					
					//make sure we aren't quantifying over an
					//operator or function because that's not allowed in FOL
					if ( currQuantifier != null ) {
						throw new IllegalArgumentException( "Cannot quantify over operators/functions in FOL." );
					}
					
					outputQueue.add( operatorStack.pop() );
					if ( operatorStack.empty() ) {
						throw new IllegalArgumentException( "Missing left parenthesis." );
					}
				}
				operatorStack.pop();
				
				//if the set of parentheses we just processed enclosed
				//a quantifier list, we need to terminate reading variables
				//into the quantifier list
				if ( !operatorStack.empty() && operatorStack.peek() == currQuantifier ) {
					currQuantifier = null;
				}
				
				//if the set of parentheses we just processed enclosed
				//some function arguments, we need to put the function onto
				//the output as well
				if ( !operatorStack.empty() && operatorStack.peek() instanceof Function ) {
					outputQueue.add( operatorStack.pop() );
				}
			}
		}
		
		while( !operatorStack.empty() ) {
			if ( operatorStack.peek().equals( Symbol.LEFT_PAREN ) ) {
				throw new IllegalArgumentException( "Missing right parenthesis." );
			}
			outputQueue.add( operatorStack.pop() );
		}
		return outputQueue;
	}
	
	private List< Symbol > postfixExpression;
	ExpressionNode root = null;
	
	/**
	 * 
	 * @param infixExpression
	 */
	public ExpressionTree( List< Symbol > infixExpression ) {
		this.postfixExpression = convertToPostfix( infixExpression );
		buildTree();
	}
	
	/**
	 * For testing purposes
	 */
	ExpressionTree() {
		
	}
	
	
	/**
	 * Builds this expression tree from the postfix expression
	 * it contains.
	 */
	private void buildTree() {
		Queue< Symbol > postfix = new LinkedList< Symbol >( postfixExpression );
		Stack< ExpressionNode > evalStack = new Stack< ExpressionNode >();
		while( !postfix.isEmpty() ) {
			Symbol nextToken = postfix.poll();
			ExpressionNode newNode = new ExpressionNode( nextToken );
			if ( nextToken instanceof Variable || nextToken instanceof ObjectFOL ) {
				evalStack.push( newNode );
			}
			else if ( nextToken instanceof QuantifierList ) {
				if ( evalStack.empty() ) {
					throw new IllegalArgumentException( "Quantifier does not quantify anything." );
				}
				
				newNode.addChildren( evalStack.pop() );
				evalStack.push( newNode );
			}
			else if ( nextToken instanceof Function ) {
				Function f = (Function) nextToken;
				int numArgs = f.getNumArgs();
				ExpressionNode[] args = new ExpressionNode[ numArgs ];
				
				//because we're using a stack, our operands get read in reverse
				//order. to get back to the correct order, we need to
				//read from the stack in reverse order.
				for ( int i=numArgs-1 ; i>=0 ; --i ) {
					if ( evalStack.empty() ) {
						throw new IllegalArgumentException( "Too few arguments to function " + f.toString() );
					}
					args[ i ] = evalStack.pop();
				}
				newNode.addChildren( args );
				evalStack.push( newNode );
			}
			else if ( nextToken instanceof Operator ) {
				Operator op = (Operator) nextToken;
				int numArgs = op.getNumOperands();
				ExpressionNode[] args = new ExpressionNode[ numArgs ];
				
				//because we're using a stack, our operands get read in reverse
				//order. to get back to the correct order, we need to
				//read from the stack in reverse order.
				for ( int i=numArgs-1 ; i>=0 ; --i ) {
					if ( evalStack.empty() ) {
						throw new IllegalArgumentException( "Too few arguments to operator " + op.toString() );
					}
					args[ i ] = evalStack.pop();
				}
				newNode.addChildren( args );
				evalStack.push( newNode );
			}
			else {
				System.out.println( "Don't recognize class: " + nextToken.getClass().getName() );
			}
		}
		
		if ( evalStack.size() == 0 ) {
			throw new IllegalArgumentException( "Input missing values." );
		}
		else if ( evalStack.size() > 1 ) {
			throw new IllegalArgumentException( "Input has extraneous values." );
		}
		else {
			this.root = evalStack.peek();
		}
	}
	
	private void distributeNotsAndEliminateArrows() {
		if ( this.root != null ) {
			this.root.distributeNots();
			this.root.eliminateArrows();
		}
	}
	
	private void standardize( SymbolTracker tracker ) {
		if ( this.root != null ) {
			this.root.standardize( new HashMap< Variable , Variable >() , tracker );
		}
	}
	
	class ExpressionNode {

		/**
		 * Stores if the expression stemming
		 * from this node has been negated.
		 */
		public boolean negated = false;
		private Symbol value;
		private ExpressionNode parent = null;
		private ArrayList< ExpressionNode > children = new ArrayList< ExpressionNode >();
		
		public ExpressionNode( Symbol value ) {
			this.value = value;
		}
		
		public void setParent( ExpressionNode node ) {
			this.parent = node;
		}
		
		public void addChildren( ExpressionNode... nodes ) {
			for ( ExpressionNode child : nodes ) {
				this.children.add( child );
				child.setParent( this );
			}
		}
		
		public void addChildren( List< ExpressionNode > nodes ) {
			for ( ExpressionNode child : nodes ) {
				addChildren( child );
			}
		}
		
		public boolean isNegated() {
			return this.negated;
		}
		
		/**
		 * Negates the expression beginning at this
		 * node.
		 */
		public void negate() {
			if ( this.value instanceof Operator ) {
				if ( this.value.equals( Operator.NOT ) ) {
					this.negated = !this.negated;
				}
				else if ( this.value.equals( Operator.EQUALS ) ) {
					this.value = Operator.NOT_EQUALS;
				}
				else if ( this.value.equals( Operator.NOT_EQUALS ) ) {
					this.value = Operator.EQUALS;
				}
				else if ( this.value.equals( Operator.AND ) ) {
					
					//apply DeMorgan's Law: !(P AND Q)  <=> !P OR !Q
					//note: a prior call to buildTree() should 
					//guarantee that there are 2 operands to the 
					//AND operator
					ExpressionNode P = this.children.get( 0 );
					P.negate();
					ExpressionNode Q = this.children.get( 1 );
					Q.negate();
					this.value = Operator.OR;
				}
				else if ( this.value.equals( Operator.OR ) ) {
					
					//apply DeMorgan's Law: !(P OR Q) <=> !P AND !Q
					//note: a prior call to buildTree() should
					//guarantee that there are 2 operands to the
					//OR operator
					ExpressionNode P = this.children.get( 0 );
					P.negate();
					ExpressionNode Q = this.children.get( 1 );
					Q.negate();
					this.value = Operator.AND;
				}
				else if ( this.value.equals( Operator.IMPLICATION ) ) {
					this.eliminateArrows();
					this.negate();
				}
				else if ( this.value.equals( Operator.BICONDITIONAL ) ) {
					this.eliminateArrows();
					this.negate();
				}
			}
			else if ( this.value instanceof QuantifierList ) {
				QuantifierList quantifierList = (QuantifierList) this.value;
				//to negate a quantified expression FORALL(x1, x2, ...) P
				//we change rewrite it as
				//EXISTS(x1, x2, ...) !P
				if ( quantifierList.getQuantifier().equals( Quantifier.FORALL ) ) {
					
					//again, a prior call to buildeTree() guarantees
					//that there is 1 expression over which to the quantifier list
					//quantifies
					ExpressionNode quantifierExpr = this.children.get( 0 );
					quantifierExpr.negate();
					this.value = new QuantifierList( Quantifier.EXISTS , quantifierList.getVariables() );
				}
				
				//to negate a quantified expression EXISTS(x1, x2, ...) P
				//we rewrite it as
				//FORALL(x1, x2, ...) !P
				else if ( quantifierList.getQuantifier().equals( Quantifier.EXISTS ) ) {
					
					ExpressionNode quantifierExpr = this.children.get( 0 );
					quantifierExpr.negate();
					this.value = new QuantifierList( Quantifier.FORALL , quantifierList.getVariables() );
				}
				
			}
			else if ( this.value instanceof Function ) {
				this.negated = !this.negated;
			}
			else if ( this.value instanceof Variable ) {
				this.negated = !this.negated;
			}
		}
		
		/**
		 * Removes implications and biconditionals
		 * from this node.
		 */
		public void eliminateArrows() {
			
			//replace P => Q with !P OR Q
			if ( this.value.equals( Operator.IMPLICATION ) ) {
				ExpressionNode P = this.children.get( 0 );
				P.negate();
				this.value = Operator.OR;
			}
			else if ( this.value.equals( Operator.BICONDITIONAL ) ) {
				
				//replace P => Q with (!P OR Q) AND (!Q OR P)
				ExpressionNode P = this.children.get( 0 );
				ExpressionNode notP = P.deepCopy();
				notP.negate();
				
				ExpressionNode Q = this.children.get( 1 );
				ExpressionNode notQ = Q.deepCopy();
				notQ.negate();
				
				this.value = Operator.AND;
				
				ExpressionNode child1 = new ExpressionNode( Operator.OR );
				child1.addChildren( notP , Q );
				ExpressionNode child2 = new ExpressionNode( Operator.OR );
				child2.addChildren( notQ , P );
				
				this.children.clear();
				this.addChildren( child1 , child2 );
			}
			else {
				for ( ExpressionNode child : this.children ) {
					child.eliminateArrows();
				}
			}
		}
		
		/**
		 * Distributes all NOT operators as far inward
		 * as possible. Distribution stops when there
		 * are no NOT operators left and only negated
		 * ExpressionNode objects.
		 */
		public void distributeNots() {

			//remove this NOT operator when
			//distributing
			if ( this.value.equals( Operator.NOT ) ) {
				
				//there should only be one child - if there
				//are two children, the method buildTree()
				//should have caught this problem and raised
				//an exception earlier
				ExpressionNode child = this.children.get( 0 );
				
				//negate the child and remove this NOT operator
				if ( !this.negated ) {
					child.negate();
				}
				child.parent = this.parent;
				
				//overwrite the root if a NOT operator was originally
				//the root
				if ( this.parent == null ) {
					ExpressionTree.this.root = child;
				}
				child.distributeNots();
			}
			else {
				for ( ExpressionNode child : children ) {
					child.distributeNots();
				}
			}
		}
		
		/**
		 * Standardizes variables using the given user-variable to system-variable
		 * mappings.
		 * 
		 * @param userSystemMapping		stores which user-variables refer to which system variables
		 * @param tracker				tracker used for getting additional system variables
		 */
		public void standardize( HashMap< Variable , Variable > userSystemMapping , SymbolTracker tracker ) {
			if ( this.getValue() instanceof Variable ) {
				if ( userSystemMapping.get( this.getValue() ) == null ) {
					
					//if no mapping for the user-defined variable 
					//to a system-defined variable exists,
					//then create a new system-defined variable.
					//otherwise, we'll just standardize using
					//the mapping that already exists
					Variable newMapping = tracker.getNewSystemVariable();
					userSystemMapping.put( (Variable)this.getValue() , newMapping );
				}
				this.value = userSystemMapping.get( this.getValue() );
			}
			else if ( this.getValue() instanceof QuantifierList ) {
				for ( Variable v : ((QuantifierList) this.getValue()).getVariables() ) {
					
					//when we quantify over new variables,
					//we override the old mappings
					userSystemMapping.put( v , tracker.getNewSystemVariable() );
					((QuantifierList) this.getValue()).standardizeVariable( v , userSystemMapping.get( v ) );
				}
			}
			for ( ExpressionNode child : this.getChildren() ) {
				child.standardize( userSystemMapping , tracker );
			}
		}
		
		/**
		 * 
		 * @return		the value stored by this node. Do not modify it.
		 */
		public Symbol getValue() {
			return this.value;
		}
		
		/**
		 * @return 		the children of this node. Do not modify the returned list.
		 */
		public ArrayList< ExpressionNode > getChildren() {
			return this.children;
		}
		
		/**
		 * Creates a parent-less deep copy of this node and all its children. 
		 * @return
		 */
		public ExpressionNode deepCopy() {
			ExpressionNode rtn = new ExpressionNode( this.value );
			rtn.negated = this.negated;
			rtn.parent = null;
			for ( ExpressionNode child : this.children ) {
				rtn.addChildren( child.deepCopy() );
			}
			return rtn;
		}
	}
}
