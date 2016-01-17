package mjchao.mazenav.logic.structures;

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
	
	
	public SymbolTracker() {
		
	}
}
