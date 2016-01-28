package mjchao.mazenav.util;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

public class Utils {

	public static final <T> T[] join( Class<?> type , T[]... arrays ) {
		ArrayList< T > joined = new ArrayList< T >();
		for ( T[] arr : arrays ) {
			joined.addAll( Arrays.asList( arr ) );
		}

		T[] rtnArray = (T[]) Array.newInstance( type , joined.size() );
		joined.toArray( rtnArray );
		return rtnArray;
	}
	
	public static boolean isAlphanumeric( String s ) {
		for ( char c : s.toCharArray() ) {
			if ( !Character.isLetterOrDigit( c ) ) {
				return false;
			}
		}
		return true;
	}
}
