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
import mjchao.mazenav.logic.structures.SkolemFunction;
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
		
		@Override
		public QuantifierList clone() {
			return new QuantifierList( this.quantifier , new ArrayList< Variable >( this.vars ) );
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
	private boolean inCNF = false;
	
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
	
	private void eliminateArrowsAndDistributeNots() {
		if ( this.root != null ) {
			this.root.eliminateArrows();
			this.root.distributeNots();
		}
	}
	
	private void standardize( SymbolTracker tracker ) {
		if ( this.root != null ) {
			this.root.standardize( tracker.getSystemVariableMapping() , tracker );
		}
	}
	
	private void skolemize( SymbolTracker tracker ) {
		if ( this.root != null ) {
			ArrayList< Variable > vars = new ArrayList< Variable >();
			this.root.skolemize( vars , tracker );
		}
	}
	
	private void dropQuantifiers( SymbolTracker tracker ) {
		
		//reset all variables to being not universally quantified.
		//we'll re-update whether each variable is universally quantified
		//as we drop quantifiers
		for ( int i=0 ; i<tracker.getNumSystemVariables() ; ++i ) {
			Variable sysVar = tracker.getSystemVariableById( i );
			sysVar.setUniversallyQuantified( false );
		}
		
		if ( this.root != null ) {
			
			//first make sure the root is not a quantifier
			while ( this.root.getValue() instanceof QuantifierList ) {
				QuantifierList list = (QuantifierList) this.root.getValue();
				if ( list.quantifier.equals( Quantifier.FORALL ) ) {
					for ( Variable v : list.getVariables() ) {
						v.setUniversallyQuantified( true );
					}
				}
				if ( this.root.getChildren().size() == 1 ) {
					this.root = this.root.getChildren().get( 0 );
				}
				else {
					
					//we cannot have a list of variables after the quantifier.
					//for example, we can't just say "FORALL(x) x y z" - we have
					//to say something like "FORALL(x) x AND y AND z"
					throw new IllegalArgumentException( "Quantifying over an invalid expression." );
				}
			}
			this.root.dropQuantifiers();
		}
	}
	
	private void distributeOrOverAnd() {
		if ( this.root != null ) {
			this.root.distributeOrOverAnd();
		}
	}
	
	/**
	 * Builds this tree from the postfix expression it contains
	 * and converts that expression into conjunctive normal form.
	 * The conversion closely follows the process described in
	 * Artificial Intelligence: A Modern Approach 3rd Edition
	 * (Russel and Norvig). The steps are as follows:
	 * <ol>
	 * 	<li> eliminate arrows
	 *  <li> distribute NOTs inward
	 *  <li> standardize variable names
	 *  <li> skolemize existential quantifiers
	 *  <li> drop universal quantifiers
	 *  <li> distribute ORs inward over ANDs
	 * </ol>
	 * @param tracker
	 */
	public void convertToCNF( SymbolTracker tracker ) {
		if ( !inCNF ) {
			buildTree();
			eliminateArrowsAndDistributeNots();	
			standardize( tracker );
			skolemize( tracker );
			dropQuantifiers( tracker );
			distributeOrOverAnd();
			inCNF = true;
		}
	}
	
	private ArrayList< Symbol > toPostfix() {
		ArrayList< Symbol > rtn = new ArrayList< Symbol >();
		if ( root != null ) {
			root.buildPostfix( rtn );
		}
		return rtn;		
	}
	
	/**
	 * @param tracker
	 * @return				the conjunctive normal form in postfix of the
	 * 						expression contained by this tree
	 */
	public List< Symbol > getCNFPostfix( SymbolTracker tracker ) {
		convertToCNF( tracker );
		return toPostfix();
	}
	
	/**
	 * ANDs the expression in this ExpressionTree with the expression
	 * in the other ExpressionTree. All the nodes are deep-copied.
	 * 
	 * @param other
	 * @return
	 */
	public ExpressionTree andDeep( ExpressionTree other ) {
		ExpressionTree rtn = new ExpressionTree();
		ExpressionNode newRoot = new ExpressionNode( Operator.AND );
		newRoot.addChildren( this.root.deepCopy() , other.root.deepCopy() );
		rtn.root = newRoot;
		return rtn;
	}
	
	/**
	 * ANDs the expression in this ExpressionTree with the expression
	 * in the other ExpressionTree. All nodes in this tree are reused
	 * and shallow-copied, but all nodes from the other tree are deep-copied.
	 * 
	 * @param other
	 */
	public void andWith( ExpressionTree other ) {
		ExpressionNode newRoot = new ExpressionNode( Operator.AND );
		newRoot.addChildren( this.root , other.root.deepCopy() );
		this.root = newRoot;
	}
	
	/**
	 * Negates the expression contained by this ExpressionTree. This
	 * ExpressionTree is directly modified. The CNF expression is
	 * automatically rebuilt.
	 */
	public void negate( SymbolTracker tracker ) {
		this.inCNF = false;
		this.postfixExpression.add( Operator.NOT );
		convertToCNF( tracker );
	}
	
	/**
	 * Creates a deep copy of this ExpressionTree
	 */
	@Override
	public ExpressionTree clone() {
		ExpressionTree rtn = new ExpressionTree();
		rtn.root = this.root.deepCopy();
		rtn.inCNF = this.inCNF;
		rtn.postfixExpression = new ArrayList< Symbol >( this.postfixExpression );
		return rtn;
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
			for ( ExpressionNode child : this.children ) {
				child.eliminateArrows();
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
				
				//remove this NOT operator
				//overwrite the root if a NOT operator was originally
				//the root
				if ( this.parent == null ) {
					ExpressionTree.this.root = child;
					child.parent = null;
				}
				else {
					
					//remove this child from its parent 
					for ( int i=0 ; i<parent.getChildren().size() ; ++i ) {
						if ( parent.getChildren().get( i ) == this ) {
							parent.getChildren().set( i , child );
							child.setParent( parent );
						}
					}
				}
				
				//negate the child if necessary
				if ( !this.negated ) {
					child.negate();
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
			if ( this.getValue() instanceof Variable && !tracker.isSystemVariable( (Variable) this.getValue() ) ) {
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
				
				//continue standardizing after we've
				//successfully standardized this variable
				for ( ExpressionNode child : this.getChildren() ) {
					child.standardize( userSystemMapping , tracker );
				}
			}
			else if ( this.getValue() instanceof QuantifierList ) {
				this.value = ((QuantifierList) this.value).clone();
				
				ArrayList< Variable > oldVariables = new ArrayList< Variable >();
				ArrayList< Variable > oldMappings = new ArrayList< Variable >();
				
				for ( Variable v : ((QuantifierList) this.getValue()).getVariables() ) {
					
					//when we quantify over new variables,
					//we override the old mappings
					//but cache the old mappings because they need
					//to be restored when this quantifier goes out of scope
					oldVariables.add( v );
					oldMappings.add( userSystemMapping.get( v ) );

					userSystemMapping.put( v , tracker.getNewSystemVariable() );
					((QuantifierList) this.getValue()).standardizeVariable( v , userSystemMapping.get( v ) );
				}
				
				//continue standardizing with the new
				//user variable to system variable mapping
				for ( ExpressionNode child : this.getChildren() ) {
					child.standardize( userSystemMapping , tracker );
				}
				
				//restore previous mappings when this quantifier
				//goes out of scope
				for ( int i=0 ; i<oldVariables.size() ; ++i ) {
					userSystemMapping.put( oldVariables.get( i ) , oldMappings.get( i ) );
				}
			}
			else {
				
				//continue standardizing
				for ( ExpressionNode child : this.getChildren() ) {
					child.standardize( userSystemMapping , tracker );
				}
			}
		}
		
		/**
		 * Replace all instances of a Variable with the given Skolem Function
		 * 
		 * @param var		the variable to be replaced
		 * @param func		the skolem function to be substituted for the variable
		 */
		public void replaceWithSkolemFunction( Variable var , SkolemFunction func ) {
			if ( this.getValue().equals( var ) ) {
				this.value = func;
			}
			for ( ExpressionNode child : this.getChildren() ) {
				child.replaceWithSkolemFunction( var , func );
			}
		}
		
		/**
		 * Replaces all existential quantifiers with skolem functions.
		 * 
		 * @param universalVariables	a list of universal variables that will affect
		 * 								existential quantifiers
		 * @param tracker				tracker used for getting additional skolem functions
		 */
		public void skolemize( ArrayList< Variable > universalVariables , SymbolTracker tracker ) {
			if ( this.getValue() instanceof QuantifierList ) {
				QuantifierList quantifier = (QuantifierList) this.getValue();
				if ( quantifier.getQuantifier().equals( Quantifier.EXISTS ) ) {
					for ( Variable v : quantifier.getVariables() ) {
						SkolemFunction func = tracker.getNewSkolemFunction( universalVariables );
						for ( ExpressionNode child : this.getChildren() ) {
							child.replaceWithSkolemFunction( v , func );
						}
					}
				}
				else if ( quantifier.getQuantifier().equals( Quantifier.FORALL ) ) {
					
					int variablesAdded = 0;
					for ( Variable v :quantifier.getVariables() ) {
						if ( !universalVariables.contains( v ) ) {
							universalVariables.add( v );
							++variablesAdded;
						}
					}
					
					for ( ExpressionNode child : this.getChildren() ) {
						child.skolemize( universalVariables , tracker );
					}
					
					//once we go out of scope of this universal quantifier,
					//we need to remove the variables it quantifies
					//note: universalVariables is invariant
					//before and after a deeper call to skolemize so 
					//we can just remove from the end of the list
					for ( int i=0 ; i<variablesAdded ; ++i ) {
						universalVariables.remove( universalVariables.size()-1 );
					}
					return;
				}
			}
			
			//propagate skolemization down the tree
			for ( ExpressionNode child : this.getChildren() ) {
				child.skolemize( universalVariables , tracker );
			}
		}
		
		/**
		 * Removes all quantifiers that are descendants of this node
		 */
		public void dropQuantifiers() {
			boolean droppedChildren = false;
			for ( int childIdx = 0 ; childIdx < this.children.size() ; ++childIdx ) {
				if ( children.get( childIdx ).getValue() instanceof QuantifierList ) {
					QuantifierList list = (QuantifierList) children.get( childIdx ).getValue();
					if ( list.quantifier.equals( Quantifier.FORALL ) ) {
						for ( Variable v : list.getVariables() ) {
							v.setUniversallyQuantified( true );
						}
					}
					
					ArrayList< ExpressionNode > quantifierChildren = children.get( childIdx ).getChildren();
					if ( quantifierChildren.size() == 1 ) {
						children.set( childIdx ,  quantifierChildren.get( 0 ) );
						droppedChildren = true;
					}
					else {
						
						//we cannot have a list of variables after the quantifier.
						//for example, we can't just say "FORALL(x) x y z" - we have
						//to say something like "FORALL(x) x AND y AND z"
						throw new IllegalArgumentException( "Quantifying invalid expression." );
					}
				}
			}
			
			//if we dropped some children that were quantifiers, 
			//we need to repeat the process for this node again because
			//the children of those children could be quantifiers
			if ( droppedChildren ) {
				this.dropQuantifiers();
			}
			else {
				for ( ExpressionNode child : this.children ) {
					child.dropQuantifiers();
				}
			}
		}
		
		public void distributeOrOverAnd() {
			if ( this.value.equals( Operator.OR ) ) {
				
				//this node should have two children -
				//if this node does not have two children,
				//the buildTree() function should have
				//thrown an exception earlier
				ExpressionNode operand1 = this.children.get( 0 );
				ExpressionNode operand2 = this.children.get( 1 );
				if ( operand1.getValue().equals( Operator.AND ) ) {
					ExpressionNode P = operand1.getChildren().get( 0 );
					ExpressionNode Q = operand1.getChildren().get( 1 );
					ExpressionNode R = operand2;
					
					//rewrite (P AND Q) OR R as (P OR R) AND (Q OR R)
					this.value = Operator.AND;
					this.children.clear();
					
					//construct the expression (P OR R)
					operand1.value = Operator.OR;
					operand1.children.clear();
					operand1.addChildren( P , R );
					
					//construct the expression (Q OR R)
					//note the R must be deep copied because
					//we already put a refernece to it in the
					//other operand
					operand2 = new ExpressionNode( Operator.OR );
					operand2.addChildren( Q , R.deepCopy() );
					
					//add in the operands to this AND operator
					//so that we have (P OR R) AND (Q OR R)
					this.addChildren( operand1 , operand2 );
				}
				else if ( operand2.getValue().equals( Operator.AND ) ) {
					ExpressionNode P = operand1;
					ExpressionNode Q = operand2.getChildren().get( 0 );
					ExpressionNode R = operand2.getChildren().get( 1 );
					
					//rewrite P OR (Q AND R) as (P OR Q) AND (P OR R)
					this.value = Operator.AND;
					this.children.clear();
					
					//construct the expression (P OR Q)
					operand1 = new ExpressionNode( Operator.OR );
					operand1.addChildren( P , Q );
					
					//construct the expression (P OR R)
					operand2.value = Operator.OR;
					operand2.children.clear();
					operand2.addChildren( P.deepCopy() , R );
					
					//add in the operands to this AND operator
					//so that we have (P OR Q) AND (P OR R)
					this.addChildren( operand1 , operand2 );	
				}
				else {
					for ( ExpressionNode child : this.children ) {
						child.distributeOrOverAnd();
					}
					if ( this.children.get( 0 ).getValue().equals( Operator.AND ) || 
							this.children.get( 1 ).getValue().equals( Operator.AND ) ) {
						
						//if later distributions of ORs over AND caused additional
						//ANDs to shift up to a child of this node,
						//we need to repeat the process
						this.distributeOrOverAnd();
					}
					return;
				}
			}
			for ( ExpressionNode child : this.children ) {
				child.distributeOrOverAnd();
			}
		}
		
		public void buildPostfix( ArrayList< Symbol > postfix ) {
			if ( children.size() == 0 ) {
				postfix.add( this.getValue() );
				if ( this.isNegated() ) {
					postfix.add( Operator.NOT );
				}
				return;
			}
			
			for ( ExpressionNode child : children ) {
				child.buildPostfix( postfix );
			}
			postfix.add( this.getValue() );
			if ( this.isNegated() ) {
				postfix.add( Operator.NOT );
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
