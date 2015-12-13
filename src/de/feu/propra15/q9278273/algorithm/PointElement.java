package de.feu.propra15.q9278273.algorithm;

import java.awt.Point;

/**
 * The Class PointElement which can be used by the DoubleLinkedList data
 * structure.
 * 
 * @author Teodor Shaterov
 */
public class PointElement {

	/** The point. */
	protected Point point;

	/** The next element. */
	protected PointElement next;
	
	/** The previous element. */
	protected PointElement prev;

	/**
	 * Instantiates a new point element.
	 */
	public PointElement() {
		next = null;
		prev = null;
		point = null;
	}

	/**
	 * Instantiates a new point element.
	 *
	 * @param point
	 *            the point
	 * @param next
	 *            the next element
	 * @param prev
	 *            the previous element
	 */
	public PointElement(Point point, PointElement next, PointElement prev) {
		this.point = point;
		this.next = next;
		this.prev = prev;
	}

	/**
	 * Sets the next.
	 *
	 * @param next
	 *            the new next
	 */
	public void setNext(PointElement next) {
		this.next = next;
	}

	/**
	 * Sets the previous.
	 *
	 * @param prev
	 *            the new previous
	 */
	public void setPrev(PointElement prev) {
		this.prev = prev;
	}

	/**
	 * Gets the next.
	 *
	 * @return the next
	 */
	public PointElement getNext() {
		return next;
	}

	/**
	 * Gets the prev.
	 *
	 * @return the prev
	 */
	public PointElement getPrev() {
		return prev;
	}

	/**
	 * Sets the point.
	 *
	 * @param point
	 *            the new point
	 */
	public void setPoint(Point point) {
		this.point = point;
	}

	/**
	 * Gets the point.
	 *
	 * @return the point
	 */
	public Point getPoint() {
		return point;
	}
}
