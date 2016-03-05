package mjchao.mazenav.logic.structures;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

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
	
	/**
	 * Determines if any types in the given list are blank ("")
	 * 
	 * @param types
	 * @return
	 */
	private static final boolean containsBlankTypes( String[] types ) {
		for ( String type : types ) {
			if ( type.trim().isEmpty() ) {
				return true;
			}
		}
		return false;
	}
	
	private static final Function parseFunction( String[] tokens , int lineNum , Object... definitionClasses ) {
		
		//read the name of the function
		String name = tokens[ 0 ];
		
		//figure out which definition class contains this function's definition
		Object definingClassInstance = getDefiningClassInstance( name , definitionClasses );
		if ( definingClassInstance == null ) {
			throw new IllegalArgumentException( "Function \"" + name + "\" is never defined." );
		}
		
		//parse the argument types and make sure
		//they're all defined
		String[] argTypes = Arrays.copyOfRange( tokens , 1 , tokens.length );
		if ( containsBlankTypes( argTypes ) ) {
			throw new IllegalArgumentException( "(Line " + lineNum + ") Function \"" + name + "\" defined with blank types." );
		}
		
		//create the function
		return new Function( name , definingClassInstance , argTypes );
	}
	
	private static Relation parseRelation( String[] tokens , int lineNum , Object... definingClasses ) {
		String name = tokens[ 0 ];
		
		Object definingClassInstance = getDefiningClassInstance( name , definingClasses );
		if ( definingClassInstance == null ) {
			throw new IllegalArgumentException( "Function \"" + name + "\" is never defined." );
		}
		
		String[] argTypes = Arrays.copyOfRange( tokens , 1 , tokens.length );
		if ( containsBlankTypes( argTypes ) ) {
			throw new IllegalArgumentException( "(Line " + lineNum + ") Function \"" + name + "\" defined with blank types." );
		}
		
		return new Relation( name , definingClassInstance , argTypes );
	}
	
	private static Function parseConstant( String[] tokens , int lineNum , Object... definingClasses ) {
		String name = tokens[ 0 ];
		
		String[] types = Arrays.copyOfRange( tokens , 1 , tokens.length );
		if ( containsBlankTypes( types ) ) {
			throw new IllegalArgumentException( "(Line " + lineNum + ") Object \"" + name + "\" defined with blank types." );
		}
		
		Object definingClassInstance = getDefiningClassInstance( name , definingClasses );
		if ( definingClassInstance == null ) {
			throw new IllegalArgumentException( "Constant \"" + name + "\" is never defined." );
		}
		
		//we treat constants a functions that take no parameters
		Function rtn = new Function( name , definingClassInstance , new String[0] );
		
		//check that the function returns an object with the correct types
		try {
			ObjectFOL constant = rtn.operate();
			
			for ( String type : types ) {
				if ( !constant.isOfType( type ) ) {
					throw new IllegalArgumentException( "Constant\"" + name + "\" is not defined properly. " + 
														"The function that represents \"" + name + "\" should return " + 
														"an ObjectFOL of type \"" + type + "\"." );
				}
			}
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			throw new IllegalArgumentException( "Constant \"" + name + "\" is not defined properly. " + 
												"It should be a function that takes 0 arguments." );	
		}
		return rtn;
	}
	
	private static final String[] tokenize( String input ) {
		
		//first, remove all leading extraneous whitespace
		//because otherwise, spltting on the second regex 
		//can give us a blank first token
		String trimmedInput = input.replaceAll( "^(\\s|\\(|\\)|,|:)*" , "" );
		return trimmedInput.split( "\\s*(\\s|,|:|\\(|\\))\\s*" );
	}
	
	/**
	 * Creates a SymbolTracker from one definition file
	 * and multiple definition class instances.
	 * 
	 * 
	 * @param filename
	 * @param definitionClassInstances
	 * @return
	 * @throws IOException
	 * @see {@link SymbolTracker#fromDataFiles(String[], Object...)}
	 */
	public static final SymbolTracker fromDataFile( String filename , Object... definitionClassInstances ) throws IOException {
		return fromDataFiles( new String[]{ filename } , definitionClassInstances );
	}
	
	/**
	 * Creates a SymbolTracker from multiple definition files and
	 * definition class instances. Note: This has only been tested with
	 * one file, but it should generalize to multiple files. TODO: test
	 * with multiple files.
	 * 
	 * @param filenames						the filenames that specify the
	 * 										functions, relations and objects in FOL
	 * @param definitionClassInstances		the objects that have implemented
	 * 										the functions, relations and objects in Java
	 * @return								a SymbolTracker loaded with all the functions
	 * 										relations and objects
	 * @throws IOException
	 */
	public static final SymbolTracker fromDataFiles( String[] filenames , Object... definitionClassInstances ) throws IOException {
		SymbolTracker rtn = new SymbolTracker();
		for ( String filename : filenames ) {
			@SuppressWarnings("resource")
			BufferedReader f = new BufferedReader( new FileReader( filename ) );
			
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
				
				String[] tokens = tokenize( nextLine );
				String dataType = tokens[ 0 ];
				
				String[] data = Arrays.copyOfRange( tokens , 1 , tokens.length );
				if ( dataType.equals( "FUNCTION" ) ) {
					Function func = parseFunction( data , lineNumber , definitionClassInstances );
					rtn.addFunction( func.getSymbolName() , func );
				}
				else if ( dataType.equals( "RELATION" ) ) {
					Relation rel = parseRelation( data , lineNumber , definitionClassInstances );
					rtn.addRelation( rel.getSymbolName() , rel );
				}
				else if ( dataType.equals( "CONSTANT" ) ) {
					Function obj = parseConstant( data , lineNumber , definitionClassInstances );
					rtn.addConstant( obj.getSymbolName() , obj );
				}
				else {
					f.close();
					throw new IllegalArgumentException( "Undefined type: " + dataType + 
														" at line " + lineNumber + 
														" in file \"" + filename + "\".\n" +
														"Valid types are \"FUNCTION\", \"RELATION\", and \"CONSTANT\"" );
				}
				nextLine = f.readLine();
				++lineNumber;
			}
			
			f.close();
		}
		return rtn;
	}
	
	private HashMap< String , Function > functions = new HashMap< String , Function >();
	private HashMap< String , Relation > relations = new HashMap< String , Relation >();
	private HashMap< String , Function > constants = new HashMap< String , Function >();
	
	private ArrayList< Variable > variables = new ArrayList< Variable >();
	private HashMap< String , Variable > variablesByName = new HashMap< String , Variable >();
	
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
		if ( constants.containsKey( constantName ) ) {
			try {
				return constants.get( constantName ).operate();
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				return null;
			}
		}
		return null;
	}
	
	public void addConstant( String name , Function obj ) {
		this.constants.put( name , obj );
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
	@SuppressWarnings("static-method")
	public ObjectFOL getPredefinedConstant( String token ) {
		try {
			int intValue = Integer.parseInt( token );
			return NumbersFOL.fromInt( intValue );
		}
		catch ( NumberFormatException e ) {
			//ignore
		}
		try {
			double doubleValue = Double.valueOf( token ).doubleValue();
			return NumbersFOL.fromDouble( doubleValue );
		}
		catch ( NumberFormatException e ) {
			//ignore
		}
		if ( token.equals( "True" ) || token.equals( "T" ) ) {
			return BooleanFOL.True();
		}
		if ( token.equals( "False" ) || token.equals( "F" ) ) {
			return BooleanFOL.False();
		}
		return null;
	}
	
	public Variable getNewVariable() {
		Variable rtn = new Variable( nextVariableId );
		variables.add( rtn );
		variablesByName.put( rtn.getSymbolName() , rtn );
		++nextVariableId;
		return rtn;
	}
	
	public Variable getNewVariable( String name ) {
		if ( !Variable.isValidVariableName( name ) ) {
			throw new IllegalArgumentException( "User-specified variable names must consist of {A-Z}, {a-z}, [0-9] " +
												"or '_' and start with a letter." );
		}
		Variable rtn = new Variable( name , nextVariableId );
		variables.add( rtn );
		variablesByName.put( name , rtn );
		++nextVariableId;
		return rtn;
	}
	
	public Variable getVariableByName( String name ) {
		return variablesByName.get( name );
	}
	
	public Variable getVariableById( int id ) {
		return variables.get( id );
	}
	
}
