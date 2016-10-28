package convex.hull.calculator.algorithm;

import java.awt.Point;
import java.awt.geom.Arc2D;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.feu.propra15.interfaces.IHullCalculator;

/**
 * The Class HullCalculator. It implements the convex hull calculator algorithm
 * and computes the convex set from a given set of points.
 * 
 * @author Teodor Shaterov
 */
public class HullCalculator implements IHullCalculator {

	/** The Constant MATRIKEL_NR. */
	private static final String MATRIKEL_NR = "";

	/** The Constant NAME. */
	private static final String NAME = "Teodor Shaterov";

	/** The Constant EMAIL. */
	private static final String EMAIL = "teodor.shaterov@gmail.com";

	/** The Constant ENCODING. */
	private static final Charset ENCODING = StandardCharsets.ISO_8859_1;

	/** The points which are forming the convex hull. */
	private List<Point> convexHull = Collections
			.synchronizedList(new ArrayList<Point>());

	/** All points from the canvas. */
	private List<Point> allPoints = Collections
			.synchronizedList(new ArrayList<Point>());

	/** The Constant O_POINT. */
	private static final Point O_POINT = new Point(0, 0);

	/** The Constant E_POINT. */
	private static final Point E_POINT = new Point(1, 0);

	/** If UI is used or not. */
	private final boolean isInterfaceUsed;

	/** All angles that should be drawn. */
	private List<Arc2D.Double> angleHull = Collections
			.synchronizedList(new ArrayList<Arc2D.Double>());

	/** All angles used by the angle hull. */
	private static final double[] anglesArray = { Math.toRadians(30),
			Math.toRadians(45), Math.toRadians(60), Math.toRadians(75),
			Math.toRadians(90), Math.toRadians(120), Math.toRadians(150) };

	/**
	 * Instantiates a new hull calculator.
	 *
	 * @param isInterfaceUsed
	 *            is interface used
	 */
	public HullCalculator(boolean isInterfaceUsed) {
		this.isInterfaceUsed = isInterfaceUsed;
	}

	/**
	 * Add a point with given coordinates to the set of all points.
	 * 
	 * @see de.feu.propra15.interfaces.IHullCalculator#addPoint(int, int)
	 * @param x
	 *            the x coordinate
	 * @param y
	 *            the y coordinate
	 */
	@Override
	public void addPoint(int x, int y) {
		allPoints.add(new Point(x, y));
	}

	/**
	 * Add an array of points to the set of all points. array[n][0] is the x
	 * coordinate of the n point and array[n][1] is the y coordinate of it.
	 * 
	 * @see de.feu.propra15.interfaces.IHullCalculator#addPointsFromArray(int[][])
	 * @param pointArray
	 *            the array which will be added to the set
	 */
	@Override
	public void addPointsFromArray(int[][] pointArray) {
		for (int i = 0; i < pointArray.length; i++) {
			allPoints.add(new Point(pointArray[i][0], pointArray[i][1]));
		}
	}

	/**
	 * Add points from a file to the set of all points. All lines, which aren't
	 * containing coordinates with the format "x y" delimited by a space, are
	 * ignored
	 * 
	 * @see de.feu.propra15.interfaces.IHullCalculator#addPointsFromFile(java.lang
	 *      .String)
	 * @param fileName
	 *            the name of the file which would handled
	 * @throws IOException
	 *             throws an exception if the file could not be handled
	 *             correctly
	 */
	@Override
	public void addPointsFromFile(String fileName) throws IOException {
		Path path = Paths.get(fileName);
		List<String> points = Files.readAllLines(path, ENCODING);
		for (String s : points) {
			String[] tokens = s.split(" ");
			if (tokens.length > 1) {
				try {
					addPoint(Integer.parseInt(tokens[0]),
							Integer.parseInt(tokens[1]));
				} catch (NumberFormatException nfe) {
					// Ignore this point, it is not an Integer value
				}
			}
		}
	}

	/**
	 * Clear all the points from the convex set, the set of all points and all
	 * angles of the angle hull, added from the canvas, a file or an array.
	 * 
	 * @see de.feu.propra15.interfaces.IHullCalculator#clear()
	 */
	@Override
	public void clear() {
		allPoints.clear();
		convexHull.clear();
		angleHull.clear();
	}

	/**
	 * It clears the convex and angle hulls at first, then computes them with
	 * <code>computeConvexHull()</code> and
	 * <code>computeAngleHull(double alpha)</code> methods and then puts the
	 * result of the convex hull to an array.
	 * 
	 * @see de.feu.propra15.interfaces.IHullCalculator#getConvexHull()
	 * @return the int array with the coordinates of all points which are
	 *         forming the convex set
	 */
	@Override
	public int[][] getConvexHull() {
		convexHull.clear();
		angleHull.clear();
		computeConvexHull();
		if (isInterfaceUsed) {
			for (int i = 0; i < anglesArray.length; i++) {
				computeAngleHull(anglesArray[i]);
			}
		}
		int[][] convertedConvexHull = new int[convexHull.size()][2];
		for (int i = 0; i < convexHull.size(); i++) {
			convertedConvexHull[i][0] = convexHull.get(i).x;
			convertedConvexHull[i][1] = convexHull.get(i).y;
		}
		return convertedConvexHull;
	}

	/**
	 * The method which computes the convex hull from the given points. After
	 * the execution of this method, the List convexHull is filled with the
	 * points which are forming the convex set. The algorithm computes the
	 * convex hull after it divides the set of points to upper and lower hull
	 * and then it iterates over the points and put the to the convex set.
	 */
	public void computeConvexHull() {
		ArrayList<Point> sortedPoints = new ArrayList<Point>(allPoints);
		int size = sortedPoints.size();

		Collections.sort(sortedPoints, new PointComparator());
		if (size < 3) {
			if (size == 2 && sortedPoints.get(0).equals(sortedPoints.get(1))) {
				convexHull.add(sortedPoints.get(0));
				return;
			}
			convexHull = sortedPoints;
			return;
		}

		Point[] upperHull = new Point[size];
		Point[] lowerHull = new Point[size];

		upperHull[0] = sortedPoints.get(0);
		upperHull[1] = sortedPoints.get(1);
		int upperSize = 2;

		lowerHull[0] = sortedPoints.get(size - 1);
		lowerHull[1] = sortedPoints.get(size - 2);
		int lowerSize = 2;

		for (int i = 2; i < size; i++) {
			upperSize = fillHullHalf(sortedPoints, upperHull, upperSize, i);
		}

		for (int i = size - 3; i >= 0; i--) {
			lowerSize = fillHullHalf(sortedPoints, lowerHull, lowerSize, i);
		}

		for (int i = 0; i < upperSize; i++) {
			convexHull.add(upperHull[i]);
		}

		for (int i = 1; i < lowerSize - 1; i++) {
			convexHull.add(lowerHull[i]);
		}
	}

	/**
	 * Fills the upper or lower half of the convex hull.
	 *
	 * @param sortedPoints
	 *            ArrayList with all points sorted by their X coordinates.
	 * @param hull
	 *            the hull half which would be filled
	 * @param size
	 *            the size
	 * @param i
	 *            the index of the sorted points
	 * @return the size
	 */
	private int fillHullHalf(ArrayList<Point> sortedPoints, Point[] hull,
			int size, int i) {
		hull[size] = sortedPoints.get(i);
		size++;
		while (size > 2
				&& !onTheRight(hull[size - 3], hull[size - 2], hull[size - 1])) {
			hull[size - 2] = hull[size - 1];
			size--;
		}
		return size;
	}

	/**
	 * Returns the e-mail of the student, who is implementing the task.
	 * 
	 * @see de.feu.propra15.interfaces.IHullCalculator#getEmail()
	 * @return the e-mail of the student
	 */
	@Override
	public String getEmail() {
		return EMAIL;
	}

	/**
	 * Returns the Matrikel Nummer of the student, who is implementing the task.
	 * 
	 * @see de.feu.propra15.interfaces.IHullCalculator#getMatrNr()
	 * @return the Matrikel Nummer of the student
	 */
	@Override
	public String getMatrNr() {
		return MATRIKEL_NR;
	}

	/**
	 * Returns the name of the student, who is implementing the task.
	 * 
	 * @see de.feu.propra15.interfaces.IHullCalculator#getName()
	 */
	@Override
	public String getName() {
		return NAME;
	}

	/**
	 * Returns the array with all points from the canvas.
	 *
	 * @return the points array
	 */
	public List<Point> getPoints() {
		return allPoints;
	}

	/**
	 * Checks if a point is on the right or on the left to a given line between
	 * 2 points.
	 *
	 * @param a
	 *            point A
	 * @param b
	 *            point B
	 * @param c
	 *            point C
	 * @return true, if C is on the right to the line between A and B
	 */
	private boolean onTheRight(Point a, Point b, Point c) {
		return (long) (c.getX() - a.getX()) * (long) (c.getY() + a.getY())
				+ (long) (b.getX() - c.getX()) * (long) (b.getY() + c.getY())
				+ (long) (a.getX() - b.getX()) * (long) (a.getY() + b.getY()) < 0;
	}

	/**
	 * Removes the point from the ArrayList where all points are stored.
	 *
	 * @param point
	 *            the point to be removed
	 */
	public void removePoint(Point point) {
		allPoints.remove(point);
	}

	/**
	 * Sets new coordinates to a given point.
	 *
	 * @param index
	 *            the index of the point in the ArrayList
	 * @param point
	 *            the point that would be changed
	 */
	public void setPoint(int index, Point point) {
		allPoints.set(index, point);
	}

	/**
	 * Gets the index of a given point.
	 *
	 * @param point
	 *            the point
	 * @return the index of the given point
	 */
	public int getIndex(Point point) {
		return allPoints.indexOf(point);
	}

	/**
	 * Checks if there is a point, which is close enough to a given coordinates.
	 *
	 * @param x
	 *            the x coordinate
	 * @param y
	 *            the y coordinate
	 * @return the point, if there is a nearby one, else returns null
	 */
	public Point pointCloseEnough(int x, int y) {
		for (Point point : allPoints) {
			if (point.distance(x, y) < 5) {
				return point;
			}
		}
		return null;
	}

	/**
	 * Computes angle hull for the given convex hull and the given angle alpha.
	 * It uses the angle hull algorithm with the help of the angle compare test.
	 *
	 * @param alpha
	 *            the alpha angle in grads
	 */
	public void computeAngleHull(double alpha) {
		if (convexHull.size() > 1) {
			double deltaSPrime = 0;
			double deltaS = 0;
			double deltaE = 0;

			DoubleLinkedList linkedList = new DoubleLinkedList();
			for (int i = 0; i < convexHull.size(); i++) {
				Point point = convexHull.get(i);
				linkedList.insertIndex(point, i);
			}
			PointElement ls = linkedList.getElement(0);
			PointElement rs = ls;

			while (angleCompareTest(ls.getPrev().getPoint(), ls.getPoint(),
					rs.getPoint(), rs.getNext().getPoint(), alpha)) {
				rs = rs.getNext();
			}
			if (ls.getPoint().equals(rs.getPoint())) {
				rs = rs.getNext();
				deltaSPrime = 0;
			} else {
				deltaSPrime = 2 * (angleBetweenLines(rs.getPoint(),
						ls.getPoint(), ls.getPrev().getPoint()) - alpha);
			}

			PointElement lPrime = ls;
			PointElement rPrime = rs;
			PointElement l, r;
			do {
				l = lPrime;
				r = rPrime;
				deltaS = deltaSPrime;
				if (angleCompareTest(l.getPoint(), l.getNext().getPoint(),
						r.getPoint(), r.getNext().getPoint(), alpha)) {
					if (angleCompareTest(l.getPoint(), r.getPoint(),
							r.getPoint(), r.getNext().getPoint(), alpha)) {
						deltaE = 2 * (angleBetweenLines(r.getNext().getPoint(),
								r.getPoint(), l.getPoint()) - alpha);
						deltaSPrime = 2 * angleBetweenLines(l.getPoint(), r
								.getNext().getPoint(), r.getPoint());
					} else {
						deltaE = 0;
						deltaSPrime = 0;
					}
					rPrime = r.getNext();
				} else {
					if (l.getNext().getPoint().equals(r.getPoint())) {
						deltaE = 0;
						deltaSPrime = 0;
						rPrime = r.getNext();
					} else {
						deltaE = 2 * angleBetweenLines(l.getNext().getPoint(),
								l.getPoint(), r.getPoint());
						deltaSPrime = 2 * (angleBetweenLines(r.getPoint(), l
								.getNext().getPoint(), l.getPoint()) - alpha);
					}
					lPrime = l.getNext();
				}
				arcCalculation(l.getPoint(), r.getPoint(), alpha, deltaS,
						deltaE);
			} while (!(lPrime.getPoint().equals(ls.getPoint()))
					|| !(rPrime.getPoint().equals(rs.getPoint())));
		}

	}

	/**
	 * Angle compare test. It compares the angle between the lines AB and DC. If
	 * the angle is bigger or equal the angle given as a parameter, it returns
	 * true.
	 *
	 * @param a
	 *            Point A
	 * @param b
	 *            Point B
	 * @param c
	 *            Point C
	 * @param d
	 *            Point D
	 * @param angle
	 *            the angle
	 * @return true, if angle between the lines is bigger or equal the parameter
	 */
	public boolean angleCompareTest(Point a, Point b, Point c, Point d,
			double angle) {
		boolean result = onTheRight(differenceBetweenPoints(b, a),
				differenceBetweenPoints(d, c), O_POINT);
		if (!result) {
			return false;
		} else {
			if (angleBetweenLines(differenceBetweenPoints(a, b), O_POINT,
					differenceBetweenPoints(d, c)) >= angle) {
				return true;
			} else {
				return false;
			}
		}
	}

	/**
	 * The skalar produkt <A, B> of the two points A and B.
	 *
	 * @param a
	 *            Point A
	 * @param b
	 *            Point B
	 * @return the skalar produkt
	 */
	private double skalarProdukt(Point a, Point b) {
		return (a.x * b.x) + (a.y * b.y);
	}

	/*
	 * |A - B|
	 */
	/**
	 * Distance between the two points : |A - B|.
	 *
	 * @param a
	 *            Point A
	 * @param b
	 *            Point B
	 * @return the distance between the points
	 */
	private double distanceBetweenPoints(Point a, Point b) {
		double firstOperand = (a.x - b.x) * (a.x - b.x);
		double secondOperand = (a.y - b.y) * (a.y - b.y);
		return Math.sqrt(firstOperand + secondOperand);
	}

	/**
	 * Difference between points : A - B.
	 *
	 * @param a
	 *            Point A
	 * @param b
	 *            Point B
	 * @return the difference between the two points
	 */
	private Point differenceBetweenPoints(Point a, Point b) {
		return new Point(a.x - b.x, a.y - b.y);
	}

	/**
	 * The angle between the lines BA and BC.
	 *
	 * @param a
	 *            Point A
	 * @param b
	 *            Point B
	 * @param c
	 *            Point C
	 * @return the angle between the lines
	 */
	private double angleBetweenLines(Point a, Point b, Point c) {
		double overTheDivideLine = skalarProdukt(differenceBetweenPoints(a, b),
				differenceBetweenPoints(c, b));
		double underTheDivideLine = distanceBetweenPoints(a, b)
				* distanceBetweenPoints(c, b);
		double arcCosArg = overTheDivideLine / underTheDivideLine;
		return Math.acos(arcCosArg);
	}

	/**
	 * Arc calculation. It calculates the arc from (A, B and Alpha) to (Z, r,
	 * Sigma and Beta)
	 *
	 * @param a
	 *            Point A
	 * @param b
	 *            Point B
	 * @param alpha
	 *            the angle alpha
	 * @param deltaS
	 *            the angle deltaS used for the arcs intersection
	 * @param deltaE
	 *            the angle detlaE used for the arcs intersection
	 */
	private void arcCalculation(Point a, Point b, double alpha, double deltaS,
			double deltaE) {
		double mX = (a.x + b.x) / 2;
		double mY = (a.y + b.y) / 2;
		double d = distanceBetweenPoints(a, b);
		double k = (-1) * (d / (2 * Math.tan(alpha)));
		double wX = (k / d) * (a.y - b.y);
		double wY = (k / d) * (b.x - a.x);
		double zX = mX - wX;
		double zY = mY - wY;
		double radius = d / (2 * Math.sin(alpha));
		double sigma = angleBetweenLines(E_POINT, O_POINT, new Point(a.x
				- (int) zX, a.y - (int) zY));
		if (a.y < zY) {
			sigma = (2 * Math.PI) - sigma;
		}
		double beta = -2 * (Math.PI - alpha);

		angleHull.add(new Arc2D.Double(zX - radius, zY - radius, 2 * radius,
				2 * radius, (-1) * (sigma - deltaS) * (180 / Math.PI), (-1)
						* (beta + deltaS + deltaE) * (180 / Math.PI),
				Arc2D.OPEN));
	}

	/**
	 * Gets the arcs which are computed for the given convex hull.
	 *
	 * @return the arcs
	 */
	public List<Arc2D.Double> getArcs() {
		return angleHull;
	}
}
