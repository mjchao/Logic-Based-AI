package mjchao.mazenav.logic.structures;

/**
 * Represents a geometry world. 
 * 
 * @author mjchao
 *
 */
public class GeometryWorld {

	public static class Point {
		public double x;
		public double y;
		
		public Point( double x , double y ) {
			this.x = x;
			this.y = y;
		}
	}
	
	public static class LineSegment {
		public Point p1;
		public Point p2;
		
		public LineSegment( Point p1 , Point p2 ) {
			this.p1 = p1;
			this.p2 = p2;
		}
	}
	
	public static class Line {
		
		public LineSegment seg;
		
		public Line( LineSegment seg ) {
			this.seg = seg;
		}
	}
	
	public static class Circle {
		
		public Point center;
		public Double radius;
		
		public Circle( Point center , Double radius ) {
			this.center = center;
			this.radius = radius;
		}
	}
	
	public static class Angle {
		
		public Double degrees;
		
		public Angle( Double degrees ) {
			this.degrees = degrees;
		}
	}
	
	public static class RightAngle extends Angle {
		
		public RightAngle() {
			super( 90.0 );
		}
	}
	
	public static ObjectFOL fromAngle( double degrees ) {
		return new ObjectFOL( String.valueOf( degrees ) , new Angle( degrees ) , "Angle" );
	}
	
	public Point Point( ObjectFOL x , ObjectFOL y ) {
		//TODO
		return null;
	}
}
