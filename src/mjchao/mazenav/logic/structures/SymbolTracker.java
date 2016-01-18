package mjchao.mazenav.logic.structures;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Tracks any variables that have been generated
 * and any predefined functions, objects, relations, etc.
 * 
 * @author mjchao
 *
 */
public class SymbolTracker {

	private HashMap< String , Function > functions = new HashMap< String , Function >();
	private HashMap< String , Relation > relations = new HashMap< String , Relation >();
	private HashMap< String , ObjectFOL > objects = new HashMap< String , ObjectFOL >();
	
	private ArrayList< Variable > variables = new ArrayList< Variable >();
	
	private int nextVariableId = 0;
	
	public SymbolTracker() {
		
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
	
	public ObjectFOL getConstant( String constantName ) {
		if ( objects.containsKey( constantName ) ) {
			return objects.get( constantName );
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
