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
		
		setLayout( new GridLayout( numRows , numCols , 0 , 0 ) );
		for ( int r=0 ; r<numRows ; ++r ) {
			for ( int c=0 ; c<numCols ; ++c ) {
				tiles[ r ][ c ] = new TileUI();
			}
		}
		for ( int r=numRows-1 ; r>=0 ; --r ) {
			for ( int c=0 ; c<numCols ; ++c ) {
				add( tiles[ r ][ c ] );
			}
		}
	}
	
	@Override
	public void paintComponent( Graphics g ) {
		super.paintComponent( g );
		for ( int r=0 ; r<numRows ; ++r ) {
			for ( int c=0 ; c<numCols ; ++c ) {
				this.tiles[ r ][ c ].setDrawWumpus( map.shouldRenderWumpus( r , c ) );
				this.tiles[ r ][ c ].setDrawPit( map.shouldRenderPit( r , c ) );
				this.tiles[ r ][ c ].setDrawGold( map.shouldRenderGold( r , c ) );
				this.tiles[ r ][ c ].setDrawStench( map.shouldRenderStench( r , c ) );
				this.tiles[ r ][ c ].setDrawBreeze( map.shouldRenderBreeze( r , c ) );
				this.tiles[ r ][ c ].setDrawAgent( map.shouldRenderAgent( r , c ) );
			}
		}
	}
}
