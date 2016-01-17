package mjchao.mazenav.envir;

import java.util.ArrayList;

/**
 * Interface for interacting with the maze environment.
 * 
 * @author mjchao
 *
 */
public class EnvironmentInterface {

	private final Environment envir;
	
	public EnvironmentInterface( Environment envir ) {
		this.envir = envir;
	}
	
	public ArrayList< Percept > performAction( Action a ) {
		if ( !envir.alive() ) {
			
			//dead
			return null;
		}
		
		if ( a == Action.TurnLeft ) {
			return envir.turnLeft();
		}
		else if ( a == Action.TurnRight ) {
			return envir.turnRight();
		}
		else if ( a == Action.Forward ) {
			return envir.forward();
		}
		else if ( a == Action.Shoot ) {
			return envir.shoot();
		}
		else if ( a == Action.Grab ) {
			return envir.grab();
		}
		else if ( a == Action.Climb ) {
			return envir.climb();
		}
		else {
			throw new IllegalArgumentException( "Invalid action." );
		}
	}
	
	public Agent getAgentData() {
		return envir.getAgentData();
	}
	
	public ArrayList< Percept > getCurrentPercepts() {
		return envir.getTilePercepts();
	}
}
