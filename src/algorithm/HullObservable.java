package algorithm;

import java.awt.Point;
import java.io.IOException;
import java.util.Observable;

import de.feu.propra15.interfaces.IHullCalculator;

/**
 * The Class HullObservable.
 * 
 * @author Teodor Shaterov
 */
public class HullObservable extends Observable {

	/** The calculator. */
	private IHullCalculator calculator;

	/**
	 * Instantiates a new hull observable.
	 *
	 * @param calculator
	 *            the calculator which would be observed.
	 */
	public HullObservable(IHullCalculator calculator) {
		this.calculator = calculator;
	}

	/**
	 * Adds a point and notifies the observers.
	 *
	 * @param point
	 *            the point which is added
	 */
	public void addPoint(Point point) {
		calculator.addPoint(point.x, point.y);
		setChanged();
		notifyObservers();
	}

	/**
	 * Adds a point from given coordinates and notifies the observers.
	 *
	 * @param x
	 *            the x coordinate
	 * @param y
	 *            the y coordinate
	 */
	public void addPoint(int x, int y) {
		calculator.addPoint(x, y);
		setChanged();
		notifyObservers();
	}

	/**
	 * Adds points from file and notifies the observers.
	 *
	 * @param filename
	 *            the filename
	 * @throws IOException
	 *             Signals that an I/O exception has occurred during the file
	 *             handling.
	 */
	public void addPointsFromFile(String filename) throws IOException {
		calculator.addPointsFromFile(filename);
		setChanged();
		notifyObservers();
	}

	/**
	 * Clears all points from the calculator and notifies the observers.
	 */
	public void clear() {
		calculator.clear();
		setChanged();
		notifyObservers();
	}

	/**
	 * Gets the convex hull, which automatically computes it.
	 *
	 * @return the convex hull
	 */
	public int[][] getConvexHull() {
		int[][] convexHull = calculator.getConvexHull();
		setChanged();
		notifyObservers();
		return convexHull;
	}
}
