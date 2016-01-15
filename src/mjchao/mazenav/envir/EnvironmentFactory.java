package mjchao.mazenav.envir;

public class EnvironmentFactory {

	public static Object[] createRandom( int rows , int cols ) {
		Object[] rtn = new Object[ 2 ];
		Environment envir = Environment.createRandom( rows , cols );
		rtn[ 0 ] = new EnvironmentInterface( envir );
		rtn[ 1 ] = new EnvironmentUIMap( envir );
		return rtn;
	}
}
