package mjchao.mazenav.logic.structures;

/**
 * Represents a geometry world. 
 * 
 * @author mjchao
 *
 */
public class GeometryWorld {

	public static class Point {
		public Double x;
		public Double y;
		
		public Point( Double x , Double y ) {
			this.x = x;
			this.y = y;
		}
		
		@Override
		public String toString() {
			return "Point(" + this.x.toString() + ", " + this.y.toString() + ")";
		}
	}
	
	public static class LineSegment {
		public Point p1;
		public Point p2;
		
		public LineSegment( Point p1 , Point p2 ) {
			this.p1 = p1;
			this.p2 = p2;
		}
		
		@Override
		public String toString() {
			return "LineSegment(" + this.p1.toString() + ", " + this.p2.toString() + ")";
		}
	}
	
	public static class Line {
		
		public LineSegment seg;
		
		public Line( LineSegment seg ) {
			this.seg = seg;
		}
		
		@Override
		public String toString() {
			return "Line(" + seg.toString() + ")";
		}
	}
	
	public static class Circle {
		
		public Point center;
		public Double radius;
		
		public Circle( Point center , Double radius ) {
			this.center = center;
			this.radius = radius;
		}
		
		@Override
		public String toString() {
			return "Circle(" + center.toString() + ", " + radius + ")";
		}
	}
	
	public static class Angle {
		
		public Double degrees;
		
		public Angle( Double degrees ) {
			this.degrees = degrees;
		}
		
		@Override
		public boolean equals( Object other ) {
			if ( other instanceof Angle ) {
				return this.degrees.equals( ((Angle) other).degrees );
			}
			return false;
		}
		
		@Override
		public String toString() {
			return "Angle(" + degrees.toString() + "\u00B0)";
		}
	}
	
	private static RightAngle RIGHT_ANGLE_INSTANCE = new RightAngle();
	public static ObjectFOL RIGHT_ANGLE = new ObjectFOL( RIGHT_ANGLE_INSTANCE.toString() , RIGHT_ANGLE_INSTANCE , "RightAngle" , "Angle" );
	
	public static class RightAngle extends Angle {
		
		public RightAngle() {
			super( new Double(90.0) );
		}
	}
	
	public static ObjectFOL fromAngle( double degrees ) {
		return new ObjectFOL( String.valueOf( degrees ) , new Angle( Double.valueOf(degrees) ) , "Angle" );
	}
	
	public static ObjectFOL Point( ObjectFOL x , ObjectFOL y ) {
		if ( !x.isOfType( "Real" ) || y.isOfType( "Real" ) ) {
			throw new IllegalArgumentException( "Expecting two parameters of type Real." );
		}
		Double xCoord = NumbersFOL.parseDouble( x );
		Double yCoord = NumbersFOL.parseDouble( y );
		Point newPoint = new Point( xCoord , yCoord );
		return new ObjectFOL( newPoint.toString() , newPoint , "Point" );
	}
	
	public static ObjectFOL LineSegment( ObjectFOL p1 , ObjectFOL p2 ) {
		if ( !p1.isOfType( "Point" ) || !p2.isOfType( "Point" ) ) {
			throw new IllegalArgumentException( "Expecting two parameters of type Point." );
		}
		Point point1 = (Point) p1.getValue();
		Point point2 = (Point) p2.getValue();
		LineSegment newLineSeg = new LineSegment( point1 , point2 );
		return new ObjectFOL( newLineSeg.toString() , newLineSeg , "LineSegment" );
	}
	
	public static ObjectFOL Line( ObjectFOL lineSeg ) {
		if ( !lineSeg.isOfType( "LineSegment" ) ) {
			throw new IllegalArgumentException( "Expecting one parameter of type LineSegment." );
		}
		LineSegment seg = (LineSegment) lineSeg.getValue();
		Line newLine = new Line( seg );
		return new ObjectFOL( newLine.toString() , newLine , "Line" );
	}
	
	public static ObjectFOL Circle( ObjectFOL center , ObjectFOL radius ) {
		if ( !center.isOfType( "Point" ) || !radius.isOfType( "Real" ) ) {
			throw new IllegalArgumentException( "Expecting one parameter of type Point and one parameter of type Real." );
		}
		Point ctr = (Point) center.getValue();
		Double r = NumbersFOL.parseDouble( radius );
		Circle newCircle = new Circle( ctr , r );
		return new ObjectFOL( newCircle.toString() , newCircle , "Circle" );
	}
	
	public static ObjectFOL Angle( ObjectFOL degrees ) {
		if ( !degrees.isOfType( "Real" ) ) {
			throw new IllegalArgumentException( "Expecting one parameter of type Real." );
		}
		Double deg = NumbersFOL.parseDouble( degrees );
		if ( deg.doubleValue() == 90 ) {
			return RightAngle();
		}
		else {
			Angle newAngle = new Angle( deg );
			return new ObjectFOL( newAngle.toString() , newAngle , "Angle" );
		}
	}
	
	public static BooleanFOL AngleEquals( ObjectFOL angle1 , ObjectFOL angle2 ) {
		if ( !angle1.isOfType( "Angle" ) && !angle2.isOfType( "Angle" ) ) {
			throw new IllegalArgumentException( "Expecting two parameters of type Angle." );
		}
		Angle a1 = (Angle) angle1.getValue();
		Angle a2 = (Angle) angle2.getValue();
		return BooleanFOL.fromBoolean( a1.equals( a2 ) );
	}
	
	public static ObjectFOL RightAngle() {
		return RIGHT_ANGLE;
	}
}
