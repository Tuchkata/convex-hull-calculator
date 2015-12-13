package de.feu.propra15.q9278273.gui;

import java.awt.Point;
import java.util.List;

import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

/**
 * The Class UndoablePoint. It extends <code>AbstractUndoableEdit</code> so that
 * all events which are undoable can be added to the undo manager.
 * 
 * @author Teodor Shaterov
 */
public class UndoablePoint extends AbstractUndoableEdit {

	/** The points. */
	private List<Point> points = null;

	/** The point list. */
	private List<Point> pointList = null;

	/** The single point. */
	private Point singlePoint = null;

	/** The starting point. */
	private Point startingPoint = null;

	/** The end point. */
	private Point endPoint = null;

	/** The is deletion. */
	private boolean isDeletion;

	/** The is replacer. */
	private boolean isReplacer;

	/** The index. */
	private int index;

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/**
	 * Instantiates a new undoable point. It is used when a single point is
	 * added or deleted from the canvas.
	 *
	 * @param singlePoint
	 *            the single point
	 * @param list
	 *            the list
	 * @param isDeletion
	 *            the is deletion
	 * @param isReplacer
	 *            the is replacer
	 */
	public UndoablePoint(Point singlePoint, List<Point> list,
			boolean isDeletion, boolean isReplacer) {
		this.singlePoint = singlePoint;
		this.points = list;
		this.isDeletion = isDeletion;
		this.isReplacer = isReplacer;
	}

	/**
	 * Instantiates a new undoable point. It is used when more than one point is
	 * added or deleted from the canvas.
	 *
	 * @param pointList
	 *            the point list
	 * @param points
	 *            the points
	 * @param isDeletion
	 *            the is deletion
	 * @param isReplacer
	 *            the is replacer
	 */
	public UndoablePoint(List<Point> pointList, List<Point> points,
			boolean isDeletion, boolean isReplacer) {
		this.pointList = pointList;
		this.points = points;
		this.isDeletion = isDeletion;
		this.isReplacer = isReplacer;
	}

	/**
	 * Instantiates a new undoable point. It is used only when a point which is
	 * already there is moved by the user. Only the start and the end positions
	 * of the point are saved and not the many ones between this two positions.
	 *
	 * @param startingPoint
	 *            the point
	 * @param endPoint
	 *            the end point
	 * @param index
	 *            the index
	 * @param points
	 *            the points
	 * @param isReplacer
	 *            the is replacer
	 */
	public UndoablePoint(Point startingPoint, Point endPoint, int index,
			List<Point> points, boolean isReplacer) {
		this.points = points;
		this.isDeletion = false;
		this.isReplacer = isReplacer;
		this.startingPoint = startingPoint;
		this.endPoint = endPoint;
		this.index = index;
	}

	/**
	 * This method is invoked when the undo button is pressed.
	 * 
	 * @see javax.swing.undo.AbstractUndoableEdit#undo()
	 */
	@Override
	public void undo() throws CannotUndoException {
		super.undo();
		if (isDeletion && !isReplacer) {
			if (singlePoint != null) {
				points.add(singlePoint);
			} else {
				points.addAll(pointList);
			}
		} else if (!isDeletion && !isReplacer) {
			if (singlePoint != null) {
				points.remove(singlePoint);
			} else {
				points.removeAll(pointList);
			}
		} else {
			points.set(index, startingPoint);
		}
	}

	/**
	 * This method is invoked when the redo button is pressed.
	 * 
	 * @see javax.swing.undo.AbstractUndoableEdit#redo()
	 */
	@Override
	public void redo() throws CannotRedoException {
		super.redo();
		if (isDeletion && !isReplacer) {
			if (singlePoint != null) {
				points.remove(singlePoint);
			} else {
				points.removeAll(pointList);
			}
		} else if (!isDeletion && !isReplacer) {
			if (singlePoint != null) {
				points.add(singlePoint);
			} else {
				points.addAll(pointList);
			}
		} else {
			points.set(index, endPoint);

		}
	}
}
