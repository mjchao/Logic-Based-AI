package mjchao.mazenav.logic.structures;

/**
 * A symbol to be used in a logic expression.
 * 
 * @author mjchao
 *
 */
public class Symbol {

	protected String name;
	
	public Symbol( String name ) {
		this.name = name;
	}
	
	@Override
	public String toString() {
		return this.name;
	}
}
