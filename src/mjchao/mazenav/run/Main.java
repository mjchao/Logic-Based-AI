package mjchao.mazenav.run;

import java.io.IOException;
import java.util.Scanner;

import mjchao.mazenav.envir.Action;
import mjchao.mazenav.envir.EnvironmentFactory;
import mjchao.mazenav.envir.EnvironmentInterface;
import mjchao.mazenav.envir.EnvironmentUIMap;
import mjchao.mazenav.ui.MainFrame;
import mjchao.mazenav.ui.TileUI;

public class Main {

	public static final void main( String[] args ) throws IOException {
		TileUI.initImages();
		
		Object[] envirData = EnvironmentFactory.createRandom( 5 , 5 );
		
		MainFrame f = new MainFrame( (EnvironmentUIMap) envirData[ 1 ] );
		
		EnvironmentInterface interf = (EnvironmentInterface) envirData[ 0 ];
		Scanner s = new Scanner( System.in );
		while( s.hasNext() ) {
			String nextLine = s.nextLine();
			if ( nextLine.equals( "TL" ) ) {
				interf.performAction( Action.TurnLeft );
			}
			else if ( nextLine.equals( "TR" ) ) {
				interf.performAction( Action.TurnRight );
			}
			else if ( nextLine.equals( "F" ) ) {
				interf.performAction( Action.Forward );
			}
			else if ( nextLine.equals( "S" ) ) {
				interf.performAction( Action.Shoot );
			}
			else if ( nextLine.equals( "G" ) ) {
				interf.performAction( Action.Grab );
			}
			else if ( nextLine.equals( "C" ) ) {
				interf.performAction( Action.Climb );
			}
			f.repaint();
		}
	}
}
