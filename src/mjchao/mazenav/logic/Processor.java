package mjchao.mazenav.logic;

import java.util.ArrayList;
import java.util.List;

import mjchao.mazenav.logic.structures.Function;
import mjchao.mazenav.logic.structures.ObjectFOL;
import mjchao.mazenav.logic.structures.Operator;
import mjchao.mazenav.logic.structures.Quantifier;
import mjchao.mazenav.logic.structures.Relation;
import mjchao.mazenav.logic.structures.Symbol;
import mjchao.mazenav.logic.structures.SymbolTracker;
import mjchao.mazenav.logic.structures.Variable;
import mjchao.mazenav.util.Utils;

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
	
	/**
	 * Tokenizes a statement treating all reserved symbols as
	 * delimiters, but also including them in the tokenized
	 * list.
	 * 
	 * @param statement
	 * @return 				the given statement with spacing around
	 * 						individual tokens
	 */
	private static String[] tokenizeByReservedSymbols( String statement ) {
		
		//as we pass through tokens in the statement, we will
		//add them to space-separated into this string
		StringBuilder rtn = new StringBuilder( "" );
		
		//get the list of reserved keywords that we're looking for
		//these are valid delimiters for tokens, in addition to
		//whitespace
		List<String> reservedSymbols = Symbol.GET_RESERVED_SYMBOLS();
		
		int startOfLastToken = 0;
		for ( int currIdx = 0 ; currIdx < statement.length() ; /*incrementing happens inside loop*/ ) {
			//our goal is to scan forward and see if we can make any keywords 
			//beginning at the current index
			
			//no reserved keywords may begin with whitespace
			if ( Character.isWhitespace( statement.charAt( currIdx ) ) ) {
				++currIdx;
				continue;
			}
			
			boolean matchedReservedSymbol = false;
			
			//try to scan forward and match any reserved symbols
			for ( String reserved : reservedSymbols ) {
				
				//we can't match a reserved keyword if there
				//aren't enough letters left
				if ( currIdx + reserved.length() > statement.length() ) {
					continue;
				}
				
				//we can't match a reserved keyword that is alphanumeric
				//if the previous character wasn't a space
				if ( Utils.isAlphanumeric( reserved ) ) {
					if ( currIdx > 0 && Character.isLetterOrDigit( statement.charAt( currIdx-1 ) ) ) {
						continue;
					}
				}
				
				//add any keywords we find to the preprocessed list
				if ( reserved.equals( statement.substring( currIdx , currIdx+reserved.length() ) ) ) {
					
					//we can't match a reserved keyword that is alphnumeric
					//if the characters after it are also alphanumeric
					//e.g. we don't want to match OR if there's a variable named
					//ORy
					if ( Utils.isAlphanumeric( reserved ) ) {
						if ( currIdx+reserved.length() < statement.length() ) {
							if ( Character.isLetterOrDigit( statement.charAt( currIdx+reserved.length() ) ) ) {
								continue;
							}
						}
					}
					
					matchedReservedSymbol = true;
					
					//add the token just prior to this one, if
					//one was found
					String previousToken = statement.substring( startOfLastToken , currIdx );
					if ( previousToken.trim().length() > 0 ) {
						rtn.append( " " );
						rtn.append( previousToken.trim() );
					}
					
					rtn.append( " " );
					rtn.append( reserved );
					
					//we stop here to make sure we don't match
					//multiple keywords to the same characters
					//and skip forward to the end of the
					//reserved keyword we just found
					currIdx = currIdx + reserved.length();
					startOfLastToken = currIdx;
					break;
				}
			}
			
			if ( !matchedReservedSymbol ) {
				++currIdx;
				continue;
			}
		}
		
		//check for a token at the very end as well
		if ( startOfLastToken < statement.length() ) {
			String previousToken = statement.substring( startOfLastToken , statement.length() );
			if ( previousToken.trim().length() > 0 ) {
				rtn.append( " " );
				rtn.append( previousToken.trim() );
			}
		}
		
		String trimmed = rtn.toString().trim();
		
		//String.split returns an array of length 1 for the 
		//empty string. We need this to be 0 for our purposes
		//because there are 0 tokens in the input
		if ( trimmed.length() == 0 ) {
			return new String[] {};
		}
		else {
			return trimmed.split( " " );
		}
	}

	public void tokenize() {
		tokens = new ArrayList< Symbol >();
		
		String toTokenize = statement;

		String[] stringTokens = tokenizeByReservedSymbols( toTokenize );
		
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
			//first check if the variable already exists
			Variable var = tracker.getVariableByName( token );
			if ( var != null ) {
				tokens.add( var );
				continue;
			}
			
			//finally, if the variable does not already exist
			//then create a new one
			var = tracker.getNewVariable( token );
			
			tokens.add( var );
			continue;
		}
	}
	
	/**
	 * Removes all double negations from a tokenized expression
	 * 
	 * @param expression		A list, passed by reference,
	 * 							that will be modified to contain
	 * 							no double negations
	 */
	private void removeDoubleNegations( List< Symbol > expression ) {
		for ( int i=0 ; i<expression.size()-1 ; ++i ) {
			Symbol currSymbol = expression.get( i );
			Symbol nextSymbol = expression.get( i+1 );
			if ( currSymbol.equals( Operator.NOT ) && nextSymbol.equals( Operator.NOT ) ) {
				
				//remove the two double negations
				expression.remove( i );
				expression.remove( i );
				
				//we have to decrement the counter because everything
				//shifted back by 1 and we have to offset the post-increment
				i--;
				continue;
			}
		}
	}
	
	/**
	 * Removes extra parentheses around an expression. For example,
	 * (((x))) becomes x
	 * 
	 * @param expression
	 */
	private void removeExtraParentheses( List< Symbol > expression ) {
		while( expression.get( 0 ).equals( Symbol.LEFT_PAREN ) && 
				expression.get( expression.size()-1 ).equals( Symbol.RIGHT_PAREN ) ) {
			expression.remove( expression.size()-1 );
			expression.remove( 0 );
		}
	}
	
	/**
	 * Negates a given expression. If the expression is not
	 * already in negation-normal form (NNF), this method will first
	 * convert to NNF and then perform the negation.
	 * 
	 * @param input an expression.
	 * @return		the given input negated and presented in
	 * 				NNF
	 */
	private List< Symbol > negate( List< Symbol > input ) {
		System.out.println( "Negate: " + input.toString() );
		List< Symbol > nnfExpression;
		
		//first, make sure the entire expression is already in
		//NNF
		if ( input.contains( Operator.IMPLICATION ) || 
				input.contains( Operator.BICONDITIONAL ) ) {
			nnfExpression = convertToNNF( input );
		}
		else {
			nnfExpression = input;
		}
		
		//remove any double negations
		removeDoubleNegations( nnfExpression );
		
		//now we'll build the negated expression
		List< Symbol > negatedExpression = new ArrayList< Symbol >();
		
		//first, we'll look for AND operators
		//outside parentheses and apply DeMorgan's laws
		//(note that AND precedence is before OR)
		int parenthesisDepth = 0;
		for ( int i=0 ; i<nnfExpression.size() ; ++i ) {
			Symbol currToken = nnfExpression.get( i );
			if ( currToken.equals( Symbol.LEFT_PAREN ) ) {
				++parenthesisDepth;
			}
			else if ( currToken.equals( Symbol.RIGHT_PAREN ) ) {
				--parenthesisDepth;
			}
			else if ( currToken.equals( Operator.AND ) ) {
				if ( parenthesisDepth == 0 ) {
					List< Symbol > leftOperand = nnfExpression.subList( 0 , i );
					leftOperand = negate( leftOperand );
					List< Symbol > rightOperand = nnfExpression.subList( i+1 , nnfExpression.size() );
					rightOperand = negate( rightOperand );
					
					//apply DeMorgan's laws:
					//!(A AND B) = !A OR !B
					negatedExpression.addAll( leftOperand );
					negatedExpression.add( Operator.OR );
					negatedExpression.addAll( rightOperand );
					return negatedExpression;
				}
			}
		}
		
		//make sure the parentheses all match up
		if ( parenthesisDepth < 0 ) {
			throw new IllegalArgumentException( "Missing left parenthesis." );
		}
		if ( parenthesisDepth > 0 ) {
			throw new IllegalArgumentException( "Missing right parenthesis." );
		}
		
		//then, we'll look for OR operators
		//outside parentheses and apply DeMorgan's laws
		parenthesisDepth = 0;
		for ( int i=0 ; i<nnfExpression.size() ; ++i ) {
			Symbol currToken = nnfExpression.get( i );
			if ( currToken.equals( Symbol.LEFT_PAREN ) ) {
				++parenthesisDepth;
			}
			else if ( currToken.equals( Symbol.RIGHT_PAREN ) ) {
				--parenthesisDepth;
			}
			else if ( currToken.equals( Operator.OR ) ) {
				if ( parenthesisDepth == 0 ) {
					List< Symbol > leftOperand = nnfExpression.subList( 0 , i );
					leftOperand = negate( leftOperand );
					List< Symbol > rightOperand = nnfExpression.subList( i+1 , nnfExpression.size() );
					rightOperand = negate( rightOperand );
					
					//apply DeMorgan's laws:
					//!(A OR B) = !A AND !B
					negatedExpression.addAll( leftOperand );
					negatedExpression.add( Operator.AND );
					negatedExpression.addAll( rightOperand );
					return negatedExpression;
				}
			}
		}
		
		//if no ANDs or ORs were found, then this expression must
		//just consist of one token, so we'll negate it
		int numVariablesFound = 0;
		for ( int i=0 ; i<nnfExpression.size() ; ++i ) {
			Symbol currSymbol = nnfExpression.get( i );
			if ( currSymbol instanceof Variable ) {
				++numVariablesFound;
				negatedExpression.add( Operator.NOT );
				negatedExpression.add( currSymbol );
			}
			else if ( currSymbol.equals( Operator.NOT ) ) {
				
				//double negations cancel out, so don't add them
				//however, we do need to skip past the token or
				//parenthetical expression to which the negation
				//was applied
				if ( i<nnfExpression.size()-1 ) {
					if ( nnfExpression.get( i+1 ).equals( Symbol.LEFT_PAREN ) ) {
						negatedExpression.add( Symbol.LEFT_PAREN );
						
						//for parentheses
						//skip forward to the end of the parenthetical
						//expression and don't negate anything
						int parenthesesDepth = 1;
						int j = i+2;
						while( j <nnfExpression.size() && parenthesesDepth > 0 ) {
							negatedExpression.add( nnfExpression.get( j ) ) ;
							if ( nnfExpression.get( j ).equals( Symbol.LEFT_PAREN ) ) {
								++parenthesesDepth;
							}
							else if ( nnfExpression.get( j ).equals( Symbol.RIGHT_PAREN ) ) {
								--parenthesesDepth;
							}
							if ( nnfExpression.get( j ) instanceof Variable ) {
								++numVariablesFound;
							}
							++j;
						}
						
						//we don't set i=j+1 because 
						//the postincrement of the loop will do that
						//we can also don't have to check
						//that all parentheses match up because we
						//already did that previously
						i = j;
					}
					else {
						
						//otherwise, just add the next token without
						//negating it
						Symbol nextSymbol = nnfExpression.get( i+1 );
						if ( nextSymbol instanceof Variable ) {
							++numVariablesFound;
							negatedExpression.add( nextSymbol );
							++i;
						}
						else if ( nextSymbol instanceof Quantifier ) {
							negatedExpression.add( nextSymbol );
							++i;
						}
						else if ( nextSymbol.equals( Operator.NOT ) ) {
							//skip over double negations
							++i;
						}
						else {
							throw new IllegalArgumentException( "Can only negate variables and quantifiers." );
						}
					}
				}
			}
			else {
				negatedExpression.add( currSymbol );
			}
		}
		
		//remove any extraneous parenthese after we've finished negating stuff
		removeExtraParentheses( negatedExpression );

		if ( numVariablesFound == 1 ) {
			return negatedExpression;
		}
		else if ( numVariablesFound == 0 ) {
			throw new IllegalArgumentException( "Syntax error. Negation with no operand." );
		}
		else {
			throw new IllegalArgumentException( "Syntax error. " + 
						"Operator-free expression with more than one operand." ); 
		}
	}
	
	/**
	 * Converts a given expression into negation-normal form (NNF).
	 * NNF requires that all implications and biconditional operators
	 * be removed.
	 *  
	 * @param expression		an expression to be converted to NNF
	 * @return					the given expression in NNF
	 */
	private ArrayList< Symbol > convertToNNF( List< Symbol > expression ) {
		return null;
	}
	
	public void convertToCNF() {
		
	}
	
}
