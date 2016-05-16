package mjchao.mazenav.logic.structures;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

/**
 * Tracks any variables that have been generated
 * and any predefined functions, objects, relations, etc.
 * 
 * @author mjchao
 *
 */
public class SymbolTracker {
	
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
	public static final SymbolTracker fromDataFile( String filename ) throws IOException {
		return fromDataFiles( new String[]{ filename } );
	}
	
	/**
	 * Creates a SymbolTracker from multiple definition files and
	 * definition class instances. Note: This has only been tested with
	 * one file, but it should generalize to multiple files. TODO: test
	 * with multiple files.
	 * 
	 * @param filenames						the filenames that specify the
	 * 										functions, relations and objects in FOL
	 * @return								a SymbolTracker loaded with all the functions
	 * 										relations and objects
	 * @throws IOException
	 */
	public static final SymbolTracker fromDataFiles( String[] filenames ) throws IOException {
		SymbolTracker rtn = new SymbolTracker();
		for ( String filename : filenames ) {
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
				
				if ( dataType.equals( "FUNCTION" ) ) {
					rtn.addFunctions( Arrays.copyOfRange( tokens , 1 , tokens.length ) );
				}
				else if ( dataType.equals( "CONSTANT" ) ) {
					rtn.addConstants( Arrays.copyOfRange( tokens , 1 ,  tokens.length ) );
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
	
	private HashSet< String > functions = new HashSet< String >();
	private HashSet< String > constants = new HashSet< String >();
	
	/**
	 * A list of user-defined variables.
	 */
	private ArrayList< Variable > variables = new ArrayList< Variable >();
	private HashMap< String , Variable > variablesByName = new HashMap< String , Variable >();
	
	/**
	 * A list of system-defined variables. System-defined variables can only
	 * be used by the logic system (i.e. should not be exposed to the user)
	 * and can only have the form "?[number]", for example "?0", "?1", "?2", ...
	 */
	private ArrayList< Variable > systemVariables = new ArrayList< Variable >();
	
	private HashMap< Variable , Variable > systemVariableMapping = new HashMap< Variable , Variable >();
	
	/**
	 * A list of system-defined skolem functions. Skolem functions can only
	 * be used by the logic system (i.e. should not be exposed to the user)
	 * and can only have the form "$[number]", for example "$0", "$1", "$2", ...
	 */
	private ArrayList< SkolemFunction > skolemFunctions = new ArrayList< SkolemFunction >();
	
	public SymbolTracker() {
		
	}
	
	public void addFunctions( String... names ) {
		for ( String name : names ) {
			this.functions.add( name );
		}
	}

	public Function parseFunction( String functionName ) {
		if ( this.functions.contains( functionName ) ) {
			Function rtn = new Function( functionName );
			return rtn;
		}
		else {
			return null;
		}
	}
	
	public void addConstants( String... names ) {
		for ( String name : names ) {
			this.constants.add( name );
		}
	}
	
	public ObjectFOL parseConstant( String constantName ) {
		if ( constants.contains( constantName ) ) {
			
			//in FOL, constants are represented as functions
			//that take no arguments
			ObjectFOL rtn = new ObjectFOL( constantName );
			return rtn;
		}
		else {
			return null;
		}
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
		int nextVariableId = variables.size();
		Variable rtn = new Variable( nextVariableId );
		variables.add( rtn );
		variablesByName.put( rtn.getSymbolName() , rtn );
		return rtn;
	}
	
	public Variable getNewVariable( String name ) {
		if ( !Variable.isValidVariableName( name ) ) {
			throw new IllegalArgumentException( "User-specified variable names must consist of {A-Z}, {a-z}, [0-9] " +
												"or '_' and start with a letter." );
		}
		if ( variablesByName.containsKey( name ) ) {
			throw new IllegalArgumentException( "A variable with name \"" + name + "\" already exists." );
		}
		int nextVariableId = variables.size();
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
	
	/**
	 * Creates and returns a new system-defined variable.
	 * 
	 * @return		a new system-defined variable.
	 */
	public Variable getNewSystemVariable() {
		int nextId = systemVariables.size();
		Variable rtn = new Variable( "?" + nextId , nextId );
		systemVariables.add( rtn );
		return rtn;
	}
	
	/**
	 * @param id
	 * @return		the system variable with the given id
	 */
	public Variable getSystemVariableById( int id ) {
		if ( id >= systemVariables.size() ) {
			throw new IllegalArgumentException( "id of system variable is out of bounds. \n" +
								"id: " + id + "\n" +
								"size: " + systemVariables.size() );
		}
		return systemVariables.get( id );
	}
	
	/**
	 * @return		the number of system variables stored in this tracker
	 */
	public int getNumSystemVariables() {
		return systemVariables.size();
	}
	
	/**
	 * @param var
	 * @return		if the given variable is system-defined (as opposed
	 * 				to user-defined).
	 */
	@SuppressWarnings("static-method")
	public boolean isSystemVariable( Variable var ) {
		
		//all system-defined variables start with "?"
		return var.getSymbolName().startsWith( "?" );
	}
	
	public HashMap< Variable , Variable > getSystemVariableMapping() {
		return this.systemVariableMapping;
	}
	
	/**
	 * Creates and returns a new system-defined skolem function.
	 * 
	 * @param vars	the variables that are the arguments to the skolem function.
	 * @return		a new system-defined skolem function.
	 */
	public SkolemFunction getNewSkolemFunction( Variable... vars ) {
		int nextId = skolemFunctions.size();
		SkolemFunction rtn = new SkolemFunction( nextId , vars );
		skolemFunctions.add( rtn );
		return rtn;
	}
	
	/**
	 * Creates and returns a new system-defined skolem function.
	 * 
	 * @param vars	the variables that are the arguments to the skolem function.
	 * @return		a new system-defined skolem function.
	 */
	public SkolemFunction getNewSkolemFunction( ArrayList< Variable > vars ) {
		int nextId = skolemFunctions.size();
		Variable[] varArray = new Variable[ vars.size() ];
		for ( int i=0 ; i<vars.size() ; ++i ) {
			varArray[ i ] = vars.get( i );
		}
		SkolemFunction rtn = new SkolemFunction( nextId , varArray );
		skolemFunctions.add( rtn );
		return rtn;
	}
	
	/**
	 * @param id
	 * @return		the system-defined skolem function with the given id
	 */
	public SkolemFunction getSkolemFunctionById( int id ) {
		return skolemFunctions.get( id );
	}
}
