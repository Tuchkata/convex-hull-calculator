package algorithm;

import java.awt.Point;

/**
 * The Class DoubleLinkedList. It is a data structure which is described as
 * follows : every element is connected with the one before it and after it. The
 * previous element of the first one is the last element and the next element of
 * the last one is the first element.
 *
 * @author Teodor Shaterov
 */
public class DoubleLinkedList {

	/** The start element. */
	protected PointElement start;

	/** The last element. */
	protected PointElement end;

	/** The size. */
	public int size;

	/**
	 * Instantiates a new double linked list.
	 */
	public DoubleLinkedList() {
		start = null;
		end = null;
		size = 0;
	}

	/**
	 * Checks if is empty.
	 *
	 * @return true, if is empty
	 */
	public boolean isEmpty() {
		return size == 0;
	}

	/**
	 * Gets the size.
	 *
	 * @return the size
	 */
	public int getSize() {
		return size;
	}

	/**
	 * Insert point at the start of the list.
	 *
	 * @param point
	 *            the point
	 */
	public void insertStart(Point point) {

		PointElement newElement = new PointElement(point, null, null);
		if (start == null) {
			startNotInitialized(newElement);
		} else {
			newElement.setPrev(end);
			end.setNext(newElement);
			start.setPrev(newElement);
			newElement.setNext(start);
			start = newElement;
		}
		size++;
	}

	/**
	 * Insert point at the end of the list.
	 *
	 * @param point
	 *            the point
	 */
	public void insertEnd(Point point) {
		PointElement newElement = new PointElement(point, null, null);
		if (start == null) {
			startNotInitialized(newElement);
		} else {
			newElement.setPrev(end);
			end.setNext(newElement);
			start.setPrev(newElement);
			newElement.setNext(start);
			end = newElement;
		}
		size++;
	}

	/**
	 * Insert a point at a given index.
	 *
	 * @param point
	 *            the point
	 * @param index
	 *            the index
	 */
	public void insertIndex(Point point, int index) {
		PointElement newElement = new PointElement(point, null, null);
		if (index == 0) {
			insertStart(point);
			return;
		}
		PointElement pointer = start;
		for (int i = 1; i <= size; i++) {
			if (i == index) {
				PointElement nextElement = pointer.getNext();
				pointer.setNext(newElement);
				newElement.setPrev(pointer);
				newElement.setNext(nextElement);
				nextElement.setPrev(newElement);
			}
			pointer = pointer.getNext();
		}
		size++;
	}

	/**
	 * Deletes a point from a given index.
	 *
	 * @param index
	 *            the index
	 * @return true, if successful
	 */
	public boolean deleteIndex(int index) {
		if (index == 0) {
			if (size == 1) {
				clearList();
				return true;
			}
			start = start.getNext();
			start.setPrev(end);
			end.setNext(start);
			size--;
			return true;
		}
		if (index == (size - 1)) {
			end = end.getPrev();
			end.setNext(start);
			start.setPrev(end);
			size--;
			return true;
		}
		PointElement pointer = start.getNext();
		for (int i = 1; i < size; i++) {
			if (i == index) {
				PointElement previousElement = pointer.getPrev();
				PointElement nextElement = pointer.getNext();
				previousElement.setNext(nextElement);
				nextElement.setPrev(previousElement);
				size--;
				return true;
			}
			pointer = pointer.getNext();
		}
		return false;
	}

	/**
	 * Check if a list contains an element.
	 *
	 * @param point
	 *            the point
	 * @return true, if successful
	 */
	public boolean contains(PointElement point) {
		if (size == 0) {
			System.out.println("The list is empty");
			return false;
		}
		PointElement pointer = start;
		while (pointer.getNext() != start) {
			if (pointer.equals(point)) {
				return true;
			}
			pointer = pointer.getNext();
		}
		return false;
	}

	/**
	 * Gets the element at a specific index.
	 *
	 * @param index
	 *            the index
	 * @return the element
	 */
	public PointElement getElement(int index) {
		if (index == 0) {
			return start;
		}
		if (index == (size - 1)) {
			return end;
		}
		PointElement pointer = start;
		for (int i = 1; i < size; i++) {
			if (i == index) {
				return pointer.getNext();
			}
			pointer = pointer.getNext();
		}
		return null;
	}

	/**
	 * This method is a help method and is invoked when the list is still empty.
	 *
	 * @param newElement
	 *            the new element
	 */
	private void startNotInitialized(PointElement newElement) {
		newElement.setNext(newElement);
		newElement.setPrev(newElement);
		start = newElement;
		end = start;
	}

	/**
	 * Clears the list.
	 */
	private void clearList() {
		start = null;
		end = null;
		size = 0;
	}
}
