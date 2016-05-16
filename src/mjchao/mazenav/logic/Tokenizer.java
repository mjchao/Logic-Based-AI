package mjchao.mazenav.logic;

import java.util.ArrayList;
import java.util.List;

import mjchao.mazenav.logic.structures.Function;
import mjchao.mazenav.logic.structures.ObjectFOL;
import mjchao.mazenav.logic.structures.Operator;
import mjchao.mazenav.logic.structures.Quantifier;
import mjchao.mazenav.logic.structures.Symbol;
import mjchao.mazenav.logic.structures.SymbolTracker;
import mjchao.mazenav.logic.structures.Variable;
import mjchao.mazenav.util.Utils;

/**
 * Tokenizes a logical expression
 * 
 * @author mjchao
 *
 */
class Tokenizer {

	private String statement;
	private List< Symbol > tokens;
	
	private SymbolTracker tracker;
	
	public Tokenizer( String logicalStatement , SymbolTracker tracker ) {
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

	public List< Symbol > tokenize() {
		if ( tokens != null ) {
			return tokens;
		}
		
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
			//functions must be followed by a left parenthesis.
			//otherwise, it must be an object or variable. This way,
			//we let the user have duplicate names for functions and
			//constants, which might be convenient.
			Function f = tracker.parseFunction( token );
			if ( f != null && i<stringTokens.length-1 && 
					Symbol.parseSymbol( stringTokens[i+1] ).equals( Symbol.LEFT_PAREN ) ) {
				tokens.add( f );
				continue;
			}
			
			//check if its a constant object
			ObjectFOL obj = tracker.parseConstant( token );
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

		//go through and count the number of arguments to each function
		for ( int i=0 ; i<tokens.size() ; ++i ) {
			if ( tokens.get( i ) instanceof Function ) {
				int parenDepth = 0;
				int numArgs = 0;
				boolean readArgs = false;
				for ( int j=i+1 ; j<tokens.size() ; ++j ) {
					if ( tokens.get( j ).equals( Symbol.LEFT_PAREN ) ) {
						++parenDepth;
					}
					else if ( tokens.get( j ).equals( Symbol.RIGHT_PAREN ) ) {
						--parenDepth;
						if ( parenDepth == 0 ) {
							if ( readArgs ) {
								
								//add 1 to account for the last argument
								//right before the closing parenthesis that
								//did not have a comma following it
								++numArgs;
							}
							break;
						}
					}
					else {
						readArgs = true;
						
						//whenever we reach a comma that is at one level
						//of nested parenthesis, we know it delimits another
						//argument to the function
						if ( parenDepth == 1 && tokens.get( j ).equals( Symbol.COMMA ) ) {
							++numArgs;
						}
					}
				}
				
				((Function) tokens.get( i )).setNumArgs( numArgs );
			}
		}
		return tokens;
	}
}
