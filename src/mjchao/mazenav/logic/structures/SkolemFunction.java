package mjchao.mazenav.logic.structures;

/**
 * Represents a Skolem Function to be used when
 * converting to conjunctive normal form
 * 
 * @author mjchao
 *
 */
public class SkolemFunction extends Symbol {

	/**
	 * Builds the list of arguments to this skolem function as a tuple.
	 * For example, if the vars were x, y, and z, this function returns
	 * (x, y, z) so that we can represent this skolem function as 
	 * $0(x, y, z).
	 * 
	 * @param args		the arguments to this skolem function
	 * @return
	 */
	private static String buildArgList( Variable... args ) {
		StringBuilder rtn = new StringBuilder( "(" );
		if ( args.length == 0 ) {
			//add no arguments
		}
		else if ( args.length == 1 ) {
			rtn.append( args[ 0 ].getShorthand() );
		}
		else {
			rtn.append( args[ 0 ].getShorthand() );
			for ( int i=1 ; i<args.length ; ++i ) {
				rtn.append( ", " + args[ i ].getShorthand() );
			}
		}
		rtn.append( ")" );
		return rtn.toString();
	}
	
	/**
	 * a unique identifier for this skolem function
	 */
	private int id;
	
	/**
	 * The arguments to this skolem function
	 */
	private Variable[] args;
	
	/**
	 * Creates a SkolemFunction with the given id
	 * and arguments.
	 * 
	 * @param id		a unique ID to be assigned to this skolem
	 * 					function
	 * @param args		arguments to this SkolemFunction
	 */
	public SkolemFunction( int id , Variable... args ) {
		super( "$" + id + buildArgList( args ) );
		this.id = id;
		this.args = args;
	}
	
	/**
	 * @return			the unique ID of this skolem function
	 */
	public int getID() {
		return this.id;
	}

	/**
	 * @return		a copy of the list of arguments to this skolem function
	 */
	public Variable[] getArgs() {
		Variable[] rtn = new Variable[ this.args.length ];
		for ( int i=0 ; i<args.length ; ++i ) {
			rtn[ i ] = args[ i ];
		}
		return rtn;
	}
}
