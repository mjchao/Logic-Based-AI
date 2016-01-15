package mjchao.mazenav.ui;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

public class TileUI extends JPanel {
	
	public static Image WUMPUS_IMG;
	public static Image PIT_IMG;
	public static Image GOLD_IMG;
	
	public static final void initImages() throws IOException {
		TileUI.WUMPUS_IMG = ImageIO.read( new File( "wumpus.gif" ) );
		TileUI.PIT_IMG = ImageIO.read( new File( "pit.gif" ) );
		TileUI.GOLD_IMG = ImageIO.read( new File( "gold.gif" ) );
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private boolean drawWumpus = false;
	private boolean drawPit = false;
	private boolean drawGold = false;
	private boolean drawStench = false;
	private boolean drawBreeze = false;
	
	public TileUI() {
		
	}
	
	public void setDrawWumpus( boolean b ) {
		drawWumpus = b;
	}
	
	public void setDrawPit( boolean b ) {
		drawPit = b;
	}
	
	public void setDrawGold( boolean b ) {
		drawGold = b;
	}
	
	public void setDrawStench( boolean b ) {
		drawStench = b;
	}
	
	public void setDrawBreeze( boolean b ) {
		drawBreeze = b;
	}
	
	@Override
	public Dimension getPreferredSize() {
		return new Dimension( 100 , 100 );
	}
	
	private void drawImageInCenter( Graphics g , Image img ) {
		int width = this.getWidth();
		int height = this.getHeight();
		int imgWidth = img.getWidth( null );
		int imgHeight = img.getHeight( null );
		
		int centerX = width/2 - imgWidth/2;
		int centerY = height/2 - imgHeight/2;
		g.drawImage( img , centerX , centerY , null );
	}
	
	private void drawImageInTopHalf( Graphics g , Image img ) {
		int width = this.getWidth();
		int imgWidth = img.getWidth( null );
		int topX = width/2 - imgWidth/2;
		int topY = 0;
		g.drawImage( img , topX , topY , null );
	}
	
	private void drawImageInBottomHalf( Graphics g , Image img ) {
		int width = this.getWidth();
		int imgWidth = img.getWidth( null );
		int topX = width/2 - imgWidth/;2
		int topY = this.getHeight() - img.getHeight( null );
		g.drawImage( img , topX , topY , null );
	}
	
	@Override
	public void paintComponent( Graphics g ) {
		super.paintComponent( g );
		
		//if it's a pit, we just draw the pit
		if ( drawPit ) {
			drawImageInCenter( g , TileUI.PIT_IMG );
			return;
		}
		if ( drawWumpus && !drawGold ) {
			drawImageInCenter( g , TileUI.WUMPUS_IMG );
			return;
		}
		if ( !drawWumpus && drawGold ) {
			drawImageInCenter( g , TileUI.GOLD_IMG );
			return;
		}
		if ( drawWumpus && drawGold ) {
			drawImageInTopHalf( g , TileUI.WUMPUS_IMG );
			drawImageInBottomHalf( g , TileUI.GOLD_IMG );
		}
		
	}
}
