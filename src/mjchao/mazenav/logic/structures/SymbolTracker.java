package mjchao.mazenav.logic.structures;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.StringTokenizer;

/**
 * Tracks any variables that have been generated
 * and any predefined functions, objects, relations, etc.
 * 
 * @author mjchao
 *
 */
public class SymbolTracker {
	
	private static final Object getDefiningClassInstance( String name , Object... listOfDefiningClasses ) {
		Object definingClassInstance = null;
		for ( Object def : listOfDefiningClasses ) {
			for ( Method m : def.getClass().getMethods() ) {
				if ( m.getName().equals( name ) ) {
					if ( definingClassInstance == null ) {
						definingClassInstance = def;
					}
					
					//if a function name appears twice, we will throw an 
					//error because parameters are not typed in this
					//language so we can't overload functions
					else {
						throw new IllegalStateException( "Multiple redefinition of function: " + name + "\n" + 
								"First definition in class: " + definingClassInstance.getClass().getName() + "\n" +
								"Second definition in class: " + def.getClass().getName() );						
					}
				}
			}
		}
		return definingClassInstance;
	}
	
	private static final Function parseFunction( String[] tokens , Object... definitionClasses ) {
		
		//read the name of the function
		String name = tokens[ 0 ];
		
		//figure out which definition class contains this function's definition
		Object definingClassInstance = getDefiningClassInstance( name , definitionClasses );
		if ( definingClassInstance == null ) {
			throw new IllegalArgumentException( "Function \"" + name + "\" is never defined." );
		}
		
		//parse the argument types
		int numArgs = Integer.parseInt( tokens[ 1 ] );
		String[] argTypes = new String[ numArgs ];
		for ( int i=0 ; i<numArgs ; ++i ) {
			argTypes[ i ] = tokens[ 2+i ];
		}
		
		//create the function
		return new Function( name , definingClassInstance , argTypes );
	}
	
	private static Relation parseRelation( String[] tokens , Object... definingClasses ) {
		String name = tokens[ 0 ];
		
		Object definingClassInstance = getDefiningClassInstance( name , definingClasses );
		if ( definingClassInstance == null ) {
			throw new IllegalArgumentException( "Function \"" + name + "\" is never defined." );
		}
		
		int numArgs = Integer.parseInt( tokens[ 1 ] );
		String[] argTypes = new String[ numArgs ];
		for ( int i=0 ; i<numArgs ; ++i ) {
			argTypes[ i ] = tokens[ 2+i ];
		}
		
		return new Relation( name , definingClassInstance , argTypes );
	}
	
	private static ObjectFOL parseObject( String[] tokens ) {
		String name = tokens[ 0 ];
		
		int numTypes = Integer.parseInt( tokens[ 1 ] );
		String[] types = new String[ numTypes ];
		for ( int i=0 ; i<numTypes ; ++i ) {
			types[ i ] = tokens[ 2+i ];
		}
		
		return new ObjectFOL( name , name , types );
	}
	
	private static final String DATA_FILE_DELIMITERS = " ,:;()";
	
	private static final String[] tokenize( String input ) {
		//first, remove all leading extraneous whitespace
		//because otherwise, regex gives us a blank first token
		String trimmedInput = input.replaceAll( "^(\\s|\\(|\\)|,|:)*" , "" );
		return trimmedInput.split( "\\s*(\\s|,|:|\\(|\\))\\s*" );
	}
	
	public static final SymbolTracker fromDataFile( String filename , Object... definitionClassInstances ) throws IOException {
		BufferedReader f = new BufferedReader( new FileReader( filename ) );
		SymbolTracker rtn = new SymbolTracker();
		
		int lineNumber = 1;
		String nextLine = f.readLine();
		while( nextLine != null ) {
			
			//allow tokens to be separated by any combination of
			//spaces " " and/or commas ","
			
			//ignore comments and blank lines
			if ( nextLine.trim().startsWith( "#" ) || nextLine.trim().isEmpty() ) {
				nextLine = f.readLine();
				++lineNumber;
				continue;
			}
			
			String[] tokens = nextLine.split( " " );
			String dataType = tokens[ 0 ];
			
			String[] data = Arrays.copyOfRange( tokens , 1 , tokens.length );
			if ( dataType.equals( "FUNCTION" ) ) {
				Function func = parseFunction( Arrays.copyOfRange( data , 1 , tokens.length ) , definitionClassInstances );
				rtn.addFunction( func.getSymbolName() , func );
			}
			else if ( dataType.equals( "RELATION" ) ) {
				Relation rel = parseRelation( data , definitionClassInstances );
				rtn.addRelation( rel.getSymbolName() , rel );
			}
			else if ( dataType.equals( "OBJECT" ) ) {
				ObjectFOL obj = parseObject( data );
				rtn.addObject( obj.getSymbolName() , obj );
			}
			else {
				throw new IllegalArgumentException( "Undefined type: " + dataType + 
													" at line " + lineNumber + 
													" in file \"" + filename + "\".\n" +
													"Valid types are \"FUNCTION\", \"RELATION\", and \"OBJECT\"" );
			}
			nextLine = f.readLine();
			++lineNumber;
		}
		
		f.close();
		return rtn;
	}
	
	private HashMap< String , Function > functions = new HashMap< String , Function >();
	private HashMap< String , Relation > relations = new HashMap< String , Relation >();
	private HashMap< String , ObjectFOL > objects = new HashMap< String , ObjectFOL >();
	
	private ArrayList< Variable > variables = new ArrayList< Variable >();
	
	private int nextVariableId = 0;
	
	public SymbolTracker() {
		
	}
	
	public void addFunction( String name , Function f ) {
		this.functions.put( name , f );
	}
	
	public Function getFunction( String functionName ) {
		if ( functions.containsKey( functionName ) ) {
			return functions.get( functionName );
		}
		return null;
	}
	
	public Relation getRelation( String relationName ) {
		if ( relations.containsKey( relationName ) ) {
			return relations.get( relationName );
		}
		return null;
	}
	
	public void addRelation( String name , Relation r ) {
		this.relations.put( name , r );
	}
	
	public ObjectFOL getConstant( String constantName ) {
		if ( objects.containsKey( constantName ) ) {
			return objects.get( constantName );
		}
		return null;
	}
	
	public void addObject( String name , ObjectFOL obj ) {
		this.objects.put( name , obj );
	}
	
	/**
	 * Attempts to convert a token to a predefined constant.
	 * A predefined constant is one that is built in to the
	 * SymbolTracker's logic. Currently, the SymbolTracker 
	 * class supports integers and decimals. 
	 * 
	 * Override this method if you wish to
	 * add your own custom predefined types - do not forget
	 * to call <code>super.getPredefinedConstant()</code>!!!
	 * 
	 * @param token			a token to try and convert
	 * @return				a predefined constant as an ObjectFOL
	 * 						or null if conversion failed
	 */
	public ObjectFOL getPredefinedConstant( String token ) {
		try {
			int intValue = Integer.parseInt( token );
			return ObjectFOL.fromInt( intValue );
		}
		catch ( NumberFormatException e ) {
			//ignore
		}
		try {
			double doubleValue = Double.valueOf( token );
			return ObjectFOL.fromDouble( doubleValue );
		}
		catch ( NumberFormatException e ) {
			//ignore
		}
		return null;
	}
	
	public Variable getNewVariable() {
		Variable rtn = new Variable( nextVariableId );
		variables.add( rtn );
		++nextVariableId;
		return rtn;
	}
	
	public Variable getNewVariable( String name ) {
		Variable rtn = new Variable( name , nextVariableId );
		variables.add( rtn );
		++nextVariableId;
		return rtn;
	}
	
}
