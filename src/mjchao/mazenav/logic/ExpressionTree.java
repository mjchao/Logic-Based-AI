package mjchao.mazenav.logic;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import mjchao.mazenav.logic.structures.Function;
import mjchao.mazenav.logic.structures.ObjectFOL;
import mjchao.mazenav.logic.structures.Operator;
import mjchao.mazenav.logic.structures.Quantifier;
import mjchao.mazenav.logic.structures.Symbol;
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
	
	private ArrayList< Symbol > postfixExpression;
	
	private static ArrayList< Symbol > convertToPostfix( List< Symbol > infix ) {
		ArrayList< Symbol > outputQueue = new ArrayList< Symbol >();
		Stack< Symbol > operatorStack = new Stack< Symbol >();
		int currIdx = 0;
		for ( Symbol currToken : infix ) {
			
			//if the token is some kind of object or variable, 
			//we immediately add to postfix output
			if ( currToken instanceof Variable || currToken instanceof ObjectFOL ) {
				outputQueue.add( currToken );
			}
			else if ( currToken instanceof Function ) {
				operatorStack.push( currToken );
			}
			
			//commas serve as function argument separators
			else if ( currToken.equals( Symbol.COMMA ) ) {
				if ( operatorStack.empty() ) {
					throw new IllegalArgumentException( "Missing left parenthesis." );
				}
				
				//remove all operators until we reach the start of the
				//function
				while( !operatorStack.peek().equals( Symbol.LEFT_PAREN ) ) {
					outputQueue.add( operatorStack.pop() );
					if ( operatorStack.empty() ) {
						throw new IllegalArgumentException( "Missing left parenthesis." );
					}
				}
			}
			else if ( currToken instanceof Operator ) {
				Operator op = (Operator) currToken;
				
				//get rid of any operators that should be
				//executed before the current operator
				while( !operatorStack.empty() && operatorStack.peek() instanceof Operator &&
						( (Operator)operatorStack.peek()).getPrecedence() >= op.getPrecedence() ) {
					outputQueue.add( operatorStack.pop() );
				}
				operatorStack.push( op );
			}
			else if ( currToken.equals( Symbol.LEFT_PAREN ) ) {
				operatorStack.push( currToken );
			}
			else if ( currToken.equals( Symbol.RIGHT_PAREN ) ) {
				if ( operatorStack.empty() ) {
					throw new IllegalArgumentException( "Missing left parenthesis." );
				}
				while( !operatorStack.peek().equals( Symbol.LEFT_PAREN ) ) {
					outputQueue.add( operatorStack.pop() );
					if ( operatorStack.empty() ) {
						throw new IllegalArgumentException( "Missing left parenthesis." );
					}
				}
				operatorStack.pop();
				
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
	
	/**
	 * 
	 * @param infixExpression
	 */
	public ExpressionTree( List< Symbol > infixExpression ) {
		this.postfixExpression = convertToPostfix( infixExpression );
	}
	
	
	
	private class ExpressionNode {

		private Symbol operator;
		private ArrayList< Symbol > children = new ArrayList< Symbol >();
		
		public ExpressionNode( Symbol operator ) {
			this.operator = operator;
		}
	}
	
	private class QuantifierNode extends ExpressionNode {
		
		private Variable[] vars;
		
		public QuantifierNode( Quantifier operator , Variable[] vars ) {
			super( operator );
			this.vars = vars;
		}
	}

}
