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
		if ( expression.size() == 0 ) {
			return;
		}
		
		boolean removedParentheses = true;
		while( removedParentheses ) {
			removedParentheses = false;
			for ( int i=0 ; i<expression.size() ; ++i ) {
				if ( expression.get( i ).equals( Symbol.LEFT_PAREN ) ) {
					int parenthesesDepth = 1;
					int matchingParenthesesIdx = -1;
					for ( int j=i+1 ; j<expression.size() ; ++j ) {
						if ( expression.get( j ).equals( Symbol.LEFT_PAREN ) ) {
							++parenthesesDepth;
						}
						else if ( expression.get( j ).equals( Symbol.RIGHT_PAREN ) ) {
							--parenthesesDepth;
						}
						if ( parenthesesDepth == 0 ) {
							matchingParenthesesIdx = j;
							break;
						}
					}
					if ( matchingParenthesesIdx == -1 ) {
						throw new RuntimeException( "Missing close parenthesis." );
					}
					
					//check for redundant parentheses y AND ((x OR z)) means we can
					//remove the outer pair around the x OR z
					if ( expression.get( matchingParenthesesIdx-1 ).equals( Symbol.RIGHT_PAREN ) &&
							expression.get( i+1).equals( Symbol.LEFT_PAREN ) ) {
						expression.remove( matchingParenthesesIdx );
						expression.remove( i );
						removedParentheses = true;
						break;
					}
					
					//there's also no point in having parentheses on the outside
					//i.e. (x) should be changed to x
					if ( i == 0 && matchingParenthesesIdx == expression.size()-1 ) {
						expression.remove( matchingParenthesesIdx );
						expression.remove( i );
						removedParentheses = true;
						break;						
					}
					
					//if all operators inside the parentheses have at least the same
					//precedence over neighboring operators outside the parentheses,
					//then the parentheses are redundant. e.g.
					// "y OR (x AND z)"  can be rewritten as y OR x AND z
					//furthermore, we need to ignore operators inside
					//nested parenthetical expressions. for example,
					// "x AND (y AND (z OR w))" can be rewritten as
					// "x AND y AND (z OR w)". The parentheses around
					// "(y AND (z OR w))" are redundant, although there is
					//a lower priority OR operator inside.
					parenthesesDepth = 1;
					int minPrecedenceInParentheses = Integer.MAX_VALUE;
					for ( int j=i+1 ; j<matchingParenthesesIdx ; ++j ) {
						if ( expression.get( j ).equals( Symbol.LEFT_PAREN ) ) {
							++parenthesesDepth;
						}
						else if ( expression.get( j ).equals( Symbol.RIGHT_PAREN ) ) {
							--parenthesesDepth;
						}
						if ( parenthesesDepth == 1 ) {
							if ( expression.get( j ) instanceof Operator ) {
								minPrecedenceInParentheses = Math.min( minPrecedenceInParentheses , 
													((Operator) expression.get( j )).getPrecedence() );
							}
						}
					}
					int maxNeighboringPrecedence = Integer.MIN_VALUE;
					for ( int j=i-1 ; j>=0 ; --j ) {
						if ( expression.get( j ) instanceof Operator ) {
							maxNeighboringPrecedence = Math.max( maxNeighboringPrecedence , 
									((Operator) expression.get( j )).getPrecedence() );
							break;
						}
					}
					for ( int j=matchingParenthesesIdx+1 ; j<expression.size() ; ++j ) {
						if ( expression.get( j ) instanceof Operator ) {
							maxNeighboringPrecedence = Math.max( maxNeighboringPrecedence , 
									((Operator) expression.get( j )).getPrecedence() );
							break;
						}
					}

					if ( minPrecedenceInParentheses == maxNeighboringPrecedence ) {
						expression.remove( matchingParenthesesIdx );
						expression.remove( i );
						removedParentheses = true;
						break;								
					}
				}
			}
		}
	}
	
	/**
	 * Negates an expression with no implications or biconditional
	 * operators. The input is never changed.
	 * 
	 * @param dirtyInput	an expression to be negated
	 * @return				the negated expression
	 */
	private List< Symbol > negate( List< Symbol > dirtyInput ) {
		
		//clean up the input a bit
		List< Symbol > input = new ArrayList< Symbol >( dirtyInput );
		removeExtraParentheses( input );
		removeDoubleNegations( input );

		List< Symbol > negatedExpression = new ArrayList< Symbol >();
		
		int parenthesisDepth = 0;
		
		//first, we look for OR operators outside parentheses.
		//Since there are no implications or biconditionals,
		//OR operators have the lowest precedence. We then
		//split the expression at the OR operator, and
		//evaluate the left and right operands before
		//evaluating this OR expression
		parenthesisDepth = 0;
		for ( int i=0 ; i<input.size() ; ++i ) {
			Symbol currToken = input.get( i );
			if ( currToken.equals( Symbol.LEFT_PAREN ) ) {
				++parenthesisDepth;
			}
			else if ( currToken.equals( Symbol.RIGHT_PAREN ) ) {
				--parenthesisDepth;
			}
			else if ( currToken.equals( Operator.OR ) ) {
				if ( parenthesisDepth == 0 ) {
					
					//apply DeMorgan's laws:
					//!(A OR B) = !A AND !B
					
					List< Symbol > leftOperand = input.subList( 0 , i );
					leftOperand = negate( leftOperand );
					List< Symbol > rightOperand = input.subList( i+1 , input.size() );
					rightOperand = negate( rightOperand );
					
					if ( leftOperand.contains( Operator.OR ) ) {
						negatedExpression.add( Symbol.LEFT_PAREN );
						negatedExpression.addAll( leftOperand );
						negatedExpression.add( Symbol.RIGHT_PAREN );
					}
					else {
						negatedExpression.addAll( leftOperand );
					}
					
					negatedExpression.add( Operator.AND );
					
					if ( rightOperand.contains( Operator.OR ) ) {
						negatedExpression.add( Symbol.LEFT_PAREN );
						negatedExpression.addAll( rightOperand );
						negatedExpression.add( Symbol.RIGHT_PAREN );
					}
					else {
						negatedExpression.addAll( rightOperand );
					}
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
		
		//if there were no OR operators, then 
		//AND operators must have the lowest precedence
		//in this expression, so we look for AND operators
		//outside parentheses and split and evaluate the
		//left and right operands first before evaluating
		//this AND expression
		parenthesisDepth = 0;
		for ( int i=0 ; i<input.size() ; ++i ) {
			Symbol currToken = input.get( i );
			if ( currToken.equals( Symbol.LEFT_PAREN ) ) {
				++parenthesisDepth;
			}
			else if ( currToken.equals( Symbol.RIGHT_PAREN ) ) {
				--parenthesisDepth;
			}
			else if ( currToken.equals( Operator.AND ) ) {
				
				if ( parenthesisDepth == 0 ) {
					List< Symbol > leftOperand = input.subList( 0 , i );
					leftOperand = negate( leftOperand );
					List< Symbol > rightOperand = input.subList( i+1 , input.size() );
					rightOperand = negate( rightOperand );
					
					//apply DeMorgan's laws:
					//!(A AND B) = !A OR !B
					if ( leftOperand.contains( Operator.AND ) ) {
						negatedExpression.add( Symbol.LEFT_PAREN );
						negatedExpression.addAll( leftOperand );
						negatedExpression.add( Symbol.RIGHT_PAREN );
					}
					else {
						negatedExpression.addAll( leftOperand );
					}
					
					negatedExpression.add( Operator.OR );
					
					if ( rightOperand.contains( Operator.AND ) ) {
						negatedExpression.add( Symbol.LEFT_PAREN );
						negatedExpression.addAll( rightOperand );
						negatedExpression.add( Symbol.RIGHT_PAREN );
					}
					else {
						negatedExpression.addAll( rightOperand );
					}
					return negatedExpression;
				}
			}
		}
		
		int numVariablesFound = 0;
		
		//if there were no AND or OR operators, then
		//we just have atomic elements to negate
		for ( int i=0 ; i<input.size() ; ++i ) {
			Symbol currToken = input.get( i );
			if ( currToken instanceof Variable ) {
				++numVariablesFound;
				negatedExpression.add( Operator.NOT );
				negatedExpression.add( currToken );
			}
			else if ( currToken.equals( Operator.NOT ) ) {
				
				//if we have two NOT operators, then
				//they cancel and we don't add either

				//we also have to add the token to which
				//the not operator was applied, but without
				//negating the operand
				if ( i < input.size()-1 ) {
					Symbol nextToken = input.get( i+1 );
					if ( nextToken instanceof Variable ) {
						++numVariablesFound;
					}
					negatedExpression.add( input.get( i+1 ) );
				}
				++i;
			}
			else {
				negatedExpression.add( currToken );
			}
		}

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
	 * Negates a given expression. If the expression is not
	 * already in negation-normal form (NNF), this method will first
	 * convert to NNF and then perform the negation. The input
	 * is never changed.
	 * 
	 * @param input an expression.
	 * @return		the given input negated and presented in
	 * 				NNF
	 */
	private List< Symbol > distributeNots( List< Symbol > input ) {
		List< Symbol > arrowFreeExpression;
		
		//first, make sure the entire expression
		//has no more arrows
		if ( input.contains( Operator.IMPLICATION ) || 
				input.contains( Operator.BICONDITIONAL ) ) {
			arrowFreeExpression = eliminateArrows( input );
		}
		else {
			arrowFreeExpression = new ArrayList< Symbol >( input );
		}
		
		//now we'll build the negated expression
		List< Symbol > distributedExpression = new ArrayList< Symbol >();
		
		//now, we'll look for NOT operators that
		//aren't being applied to atomic elements
		//and distribute them inwards
		for ( int i=0 ; i<arrowFreeExpression.size() ; ++i ) {
			Symbol currToken = arrowFreeExpression.get( i );
			
			if ( currToken.equals( Operator.NOT ) ) {
				
				//make sure there is an operand to which
				//the not is applied
				if ( i >= arrowFreeExpression.size()-1 ) {
					throw new IllegalArgumentException( "NOT operator without an operand." );
				}
				Symbol nextToken = arrowFreeExpression.get( i+1 );
				
				if ( nextToken instanceof Quantifier ) {
					
					//negating a quantifier is treated 
					//as negating an atomic element
					distributedExpression.add( currToken );
					continue;
				}
				else if ( nextToken instanceof Variable ) {
					
					//negating a variable is treated
					//as negating an atomic element
					distributedExpression.add( currToken );
					continue;
				}
				else if ( nextToken.equals( Operator.NOT ) ) {
					
					//remove any double negations
					//by not adding this NOT
					//and not skipping the next NOT
					++i;
					continue;
				}
				else if ( nextToken.equals( Symbol.LEFT_PAREN ) ) {
					
					//if we are negating a parenthetical expression,
					//figure out where it starts and where it ends
					int exprStart = i+1;
					int exprEnd = -1;
					int parenDepth = 1;
					for ( int j=i+2 ; j<arrowFreeExpression.size() ; ++j ) {
						if ( arrowFreeExpression.get( j ).equals( Symbol.LEFT_PAREN ) ) {
							++parenDepth;
						}
						else if ( arrowFreeExpression.get( j ) .equals( Symbol.RIGHT_PAREN ) ) {
							--parenDepth;
							if ( parenDepth == 0 ) {
								exprEnd = j;
								break;
							}
						}
					}
					
					if ( exprEnd == -1 ) {
						throw new IllegalArgumentException( "Missing right parenthesis." );
					}
					
					//then we remove the parentheses
					//and negate what's left in it
					List< Symbol > negated = negate( distributeNots(arrowFreeExpression.subList( exprStart+1 , exprEnd )) );
					distributedExpression.add( Symbol.LEFT_PAREN );
					distributedExpression.addAll( negated );
					distributedExpression.add( Symbol.RIGHT_PAREN );
				
					//skip to the end of the parenthetical
					//expression, because we have already
					//processed it
					i = exprEnd;
				}
			}
			else {
				
				//if not a NOT operator,
				//then there's no negation to be performed and
				//we just add the token to the expression
				distributedExpression.add( currToken );
			}
		}
		
		removeExtraParentheses( distributedExpression );
		return distributedExpression;
		
	}
	
	/**
	 * Eliminates all arrows (implications and biconditionals) from
	 * a logic statement. The input is not changed.
	 * 
	 * @param copyOfExpression	an expression from which to remove
	 * 							all implications and biconditionals
	 * @return			
	 */
	private List< Symbol > eliminateArrows( List< Symbol > copyOfExpression ) {
		List< Symbol > expression = new ArrayList< Symbol >( copyOfExpression );
		removeExtraParentheses( expression );
		if ( expression.contains( Operator.IMPLICATION ) ) {
			int implicationIdx = expression.indexOf( Operator.IMPLICATION );
			
			//scan backwards until we find an extra open parentheses
			//or a <=> operator which has lower precedence.
			//this will mark the start of the implication expression
			int antecedentEnd = implicationIdx;
			
			//by default, assume that the antecedent starts at the very
			//beginning of the expression. we'll change it if we find
			//it starts somewhere else.
			int antecedentStart = 0;
			int parenthesesDepth = 0;
			for ( int i=implicationIdx-1 ; i>=0 ; --i ) {
				if ( expression.get( i ).equals( Symbol.LEFT_PAREN ) ) {
					--parenthesesDepth;
				}
				else if ( expression.get( i ).equals( Symbol.RIGHT_PAREN ) ) {
					++parenthesesDepth;
				}
				if ( parenthesesDepth < 0 ) {
					antecedentStart = i;
					break;
				}
				if ( expression.get( i ).equals( Operator.BICONDITIONAL ) ) {
					antecedentStart = i+1;
					break;
				}
			}
			
			//scan forward until we find an extra close parentheses 
			//or a <=> operator which has lower precedence.
			//this will mark the end of the implication expression
			int consequentStart = implicationIdx+1;
			
			//by default, assume that the consequent
			//ends at the end of the expression. we'll 
			//adjust this if we find close parentheses or
			//a <=> operator
			int consequentEnd = expression.size();
			parenthesesDepth = 0;
			for ( int i=implicationIdx+1 ; i<expression.size() ; ++i ) {
				if ( expression.get( i ).equals( Symbol.LEFT_PAREN ) ) {
					--parenthesesDepth;
				}
				else if ( expression.get( i ).equals( Symbol.RIGHT_PAREN ) ) {
					++parenthesesDepth;
				}
				if ( parenthesesDepth > 0 ) {
					consequentEnd = i+1;
					break;
				}
				if ( expression.get( i ).equals( Operator.BICONDITIONAL ) ) {
					consequentEnd = i;
					break;
				}
			}

			//first remove any extra parentheses from the implication
			List< Symbol > implication = expression.subList( antecedentStart , consequentEnd );
			removeExtraParentheses( implication );
			
			//then rebuild the implication by changing it from P => Q to
			//!P OR Q
			implicationIdx = implication.indexOf( Operator.IMPLICATION );
			List< Symbol > antecedent = new ArrayList< Symbol >( implication.subList( 0 , implicationIdx ) );
			List< Symbol > consequent = new ArrayList< Symbol >( implication.subList( implicationIdx+1 , implication.size() ) );
			
			//compute !P, the negation of the antecedent without any arrows
			List< Symbol > negatedAntecedent = eliminateArrows( antecedent );
			negatedAntecedent = negate( negatedAntecedent );
			
			implication.clear();
			
			//in addition, since we're substituting in "!P OR Q",
			//which has an OR operator, if the expression has an
			//AND operator, we need to make sure to add parentheses
			if ( expression.contains( Operator.AND ) ) {
				implication.add( Symbol.LEFT_PAREN );
			}
			
			implication.addAll( negatedAntecedent );
			
			//if the consequent contains more => or <=>, we need
			//to maintain that the implication we just simplified
			//does not get evaluated with the later => or <=>
			//e.g. P => Q => R  would become
			// !P OR (Q => R) and we need to make sure the
			//parentheses go around the (Q => R)
			if ( consequent.contains( Operator.IMPLICATION ) || consequent.contains( Operator.BICONDITIONAL ) ) {
				implication.add( Operator.OR );
				implication.add( Symbol.LEFT_PAREN );
				implication.addAll( consequent );
				implication.add( Symbol.RIGHT_PAREN );
			}
			else {
				implication.add( Operator.OR );
				implication.addAll( consequent );
			}
			
			if ( expression.contains( Operator.AND ) ) {
				implication.add( Symbol.RIGHT_PAREN );
			}

			//now we just continue removing arrows
			//from the expression
			return eliminateArrows( expression );
		}
		else if ( expression.contains( Operator.BICONDITIONAL ) ) {
			int biconditionalIdx = expression.indexOf( Operator.BICONDITIONAL );
			
			//scan backwards until we find an extra open parentheses.
			//that will indicate the start of this bicondiational expression.
			int antecedentEnd = biconditionalIdx;
			
			//by default, assume that the antecedent starts at the very beginning
			//and we'll correct this if we find out otherwise.
			int antecedentStart = 0;
			int parenthesesDepth = 0;
			for ( int i=biconditionalIdx-1 ; i>=0 ; --i ) {
				if ( expression.get( i ).equals( Symbol.LEFT_PAREN ) ) {
					--parenthesesDepth;
				}
				else if ( expression.get( i ).equals( Symbol.RIGHT_PAREN ) ) {
					++parenthesesDepth;
				}
				if ( parenthesesDepth < 0 ) {
					antecedentStart = i;
					break;
				}
			}
			List< Symbol > antecedent = new ArrayList< Symbol >( expression.subList( antecedentStart , antecedentEnd ) );
			antecedent = eliminateArrows( antecedent );
			
			//scan forward until we find an extra close parentheses
			//or another biconditional operator which has lower precedence.
			//this will mark the end of the biconditional expression
			int consequentStart = biconditionalIdx + 1;
			
			//by default, assume the biconditional extends to the end of the
			//expression. We'll fix this if we find out this is not the case
			int consequentEnd = expression.size();
			parenthesesDepth = 0;
			for ( int i=biconditionalIdx+1 ; i<expression.size() ; ++i ) {
				if ( expression.get( i ).equals( Symbol.LEFT_PAREN ) ) {
					--parenthesesDepth;
				}
				else if ( expression.get( i ).equals( Symbol.RIGHT_PAREN ) ) {
					++parenthesesDepth;
				}
				if ( parenthesesDepth > 0 ) {
					consequentEnd = i+1;
					break;
				}
				if ( expression.get( i ).equals( Operator.BICONDITIONAL ) ) {
					consequentEnd = i;
					break;
				}				
			}
			List< Symbol > consequent = new ArrayList< Symbol >( expression.subList( consequentStart , consequentEnd ) );
			consequent = eliminateArrows( consequent );
			
			//replace the P <=> Q with
			//(P OR !Q) AND (!P OR Q)
			List< Symbol > P = antecedent;
			List< Symbol > notP = negate( antecedent );
			List< Symbol > Q = consequent;
			List< Symbol > notQ = negate( consequent );
			
			//here, we remove the biconditional expression P <=> Q from
			//the original expression and replace it with
			//(P OR !Q) AND (!P OR Q)
			List< Symbol > biconditionalExpression = expression.subList( antecedentStart , consequentEnd );
			biconditionalExpression.clear();
			biconditionalExpression.add( Symbol.LEFT_PAREN );
			biconditionalExpression.addAll( P );
			biconditionalExpression.add( Operator.OR );
			biconditionalExpression.addAll( notQ );
			biconditionalExpression.add( Symbol.RIGHT_PAREN );
			biconditionalExpression.add( Operator.AND );
			biconditionalExpression.add( Symbol.LEFT_PAREN );
			biconditionalExpression.addAll( notP );
			biconditionalExpression.add( Operator.OR );
			biconditionalExpression.addAll( Q );
			biconditionalExpression.add( Symbol.RIGHT_PAREN );
			
			//then continue eliminating arrows
			return eliminateArrows( expression );
		}
		
		//if no arrows were found, then we're done
		return expression;
	}
	
	/**
	 * Converts a given expression into negation-normal form (NNF).
	 * NNF requires that all implications and biconditional operators
	 * be removed.
	 *  
	 * @param expression		an expression to be converted to NNF
	 * @return					the given expression in NNF
	 */
	private List< Symbol > convertToNNF( List< Symbol > expression ) {
		return convertToNNF( eliminateArrows( expression ) );
	}
	
	public void convertToCNF() {
		
	}
	
}
