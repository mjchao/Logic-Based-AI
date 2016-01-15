package mjchao.mazenav.run;

import java.io.IOException;

import mjchao.mazenav.ui.MainFrame;
import mjchao.mazenav.ui.TileUI;

public class Main {

	public static final void main( String[] args ) throws IOException {
		TileUI.initImages();
		MainFrame f = new MainFrame();
	}
}
