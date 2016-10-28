package convex.hull.calculator.algorithm;

import java.awt.Point;
import java.util.Comparator;

/**
 * The Class PointComparator.
 */
public class PointComparator implements Comparator<Point> {

	/**
	 * Compares to points based on their x coordinates. If the x coordinates are
	 * equal, then the y coordinates are compared.
	 * 
	 * @param point1
	 *            the first point to be compared
	 * @param point2
	 *            the second point to be compared
	 **/
	@Override
	public int compare(Point point1, Point point2) {
		Integer intPoint1x = new Integer(point1.x);
		Integer intPoint2x = new Integer(point2.x);
		Integer intPoint1y = new Integer(point1.y);
		Integer intPoint2y = new Integer(point2.y);
		int result = intPoint1x.compareTo(intPoint2x);
		if (result == 0) {
			return intPoint1y.compareTo(intPoint2y);
		}
		return result;
	}

}
