package mjchao.mazenav.ui;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JScrollPane;

import mjchao.mazenav.envir.EnvironmentFactory;
import mjchao.mazenav.envir.EnvironmentUIMap;

/**
 * The main user interface
 * 
 * @author mjchao
 *
 */
public class MainFrame extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private final EnvironmentUI ui;
	
	public MainFrame() {
		Object[] envirData = EnvironmentFactory.createRandom( 3 , 3 );
		
		ui = new EnvironmentUI( (EnvironmentUIMap) envirData[ 1 ] );
		
		setLayout( new BorderLayout() );
		add( new JScrollPane(ui) , BorderLayout.CENTER );
		
		setVisible( true );
		setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
		pack();
	}
}
