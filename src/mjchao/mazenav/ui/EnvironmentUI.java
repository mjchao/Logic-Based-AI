package mjchao.mazenav.ui;

import java.awt.Graphics;
import java.awt.GridLayout;

import javax.swing.JPanel;

import mjchao.mazenav.envir.EnvironmentUIMap;

/**
 * A graphical representation of a maze
 * 
 * @author mjchao
 *
 */
public class EnvironmentUI extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private final EnvironmentUIMap map;
	private final TileUI[][] tiles;
	private final int numRows;
	private final int numCols;
	
	public EnvironmentUI( EnvironmentUIMap map ) {
		this.map = map;
		this.numRows = map.getNumCols();
		this.numCols = map.getNumCols();
		this.tiles = new TileUI[ numRows ][ numCols ];
		
		setLayout( new GridLayout( numRows , numCols , 5 , 5 ) );
		for ( int r=0 ; r<numRows ; ++r ) {
			for ( int c=0 ; r<numCols ; ++c ) {
				this.tiles[ r ][ c ] = new TileUI();
			}
		}
	}
	
	@Override
	public void paintComponent( Graphics g ) {
		super.paintComponent( g );
		for ( int r=0 ; r<numRows ; ++r ) {
			for ( int c=0 ; c<numCols ; ++c ) {
				
			}
		}
	}
}
