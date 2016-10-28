package convex.hull.calculator.gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Arc2D;

import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.event.UndoableEditEvent;
import javax.swing.undo.UndoManager;

import convex.hull.calculator.algorithm.HullCalculator;
import convex.hull.calculator.algorithm.HullObservable;

/**
 * The Class PointsArea. The are where the convex hull is drawn.
 * 
 * @author Teodor Shaterov
 */
public class PointsArea extends JPanel implements MouseListener,
		MouseMotionListener, KeyListener {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -5249048855919579873L;

	/** The calculator. */
	private HullCalculator calculator;

	/** The observable. */
	private HullObservable observable;

	/** The undo manager. */
	private final UndoManager undoManager;

	/** The undo menu item. */
	private final JMenuItem undo;

	/** The redo menu item. */
	private final JMenuItem redo;

	/** The radius. */
	private final int radius = 3;

	/** The control pressed. */
	private boolean controlPressed = false;

	/** The point clicked. */
	private volatile boolean pointClicked = false;

	/** The moved point. */
	private volatile Point movedPoint = null;

	/** The starting point used by undo and redo by point movement. */
	private volatile Point startingPoint = null;

	/** The index. */
	private volatile int index = -1;

	/**
	 * Instantiates a new points area.
	 *
	 * @param calculator
	 *            the calculator
	 * @param observable
	 *            the observable
	 * @param undoManager
	 *            the undo manager
	 * @param undo
	 *            the undo menu item
	 * @param redo
	 *            the redo menu item
	 */
	public PointsArea(HullCalculator calculator, HullObservable observable,
			UndoManager undoManager, JMenuItem undo, JMenuItem redo) {
		this.calculator = calculator;
		this.observable = observable;
		this.undoManager = undoManager;
		this.redo = redo;
		this.undo = undo;

		setBackground(Color.WHITE);
		setFocusable(true);
		addKeyListener(this);
		addMouseListener(this);
		addMouseMotionListener(this);
	}

	/**
	 * When the mouse is clicked, a new black point is drawn on the canvas. The
	 * convex and angle hulls are as well drawn by this method.
	 * 
	 * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
	 */
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D graphics = (Graphics2D) g;
		graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);

		for (Point point : calculator.getPoints()) {
			graphics.setColor(Color.BLACK);
			graphics.fillOval(point.x - radius, point.y - radius, radius * 2,
					radius * 2);

		}

		Polygon polygonHull = new Polygon();
		int[][] convexHull = observable.getConvexHull();

		for (int i = 0; i < convexHull.length; i++) {
			polygonHull.addPoint(convexHull[i][0], convexHull[i][1]);
		}

		if (polygonHull.npoints > 0) {
			graphics.setColor(Color.BLUE);
			graphics.drawPolygon(polygonHull);
		}

		for (Arc2D.Double arc : calculator.getArcs()) {
			graphics.setColor(Color.RED);
			graphics.draw(arc);
		}
	}

	/**
	 * When the mouse is dragged, if there is a nearby point, it is moved with
	 * the mouse cursor.
	 * 
	 * @see java.awt.event.MouseMotionListener#mouseDragged(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseDragged(MouseEvent e) {
		if (pointClicked && movedPoint != null) {
			movedPoint.setLocation(e.getX(), e.getY());
			calculator.setPoint(index, movedPoint);
			repaint();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * java.awt.event.MouseMotionListener#mouseMoved(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseMoved(MouseEvent e) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseClicked(MouseEvent e) {
	}

	/**
	 * If the mouse is pressed and there is no nearby point or 'Ctrl' key is not
	 * pressed, then a new one is drawn. If 'Ctrl' is pressed, the nearest point
	 * is deleted. If 'Ctrl' is not pressed and there is a nearby point, the
	 * point will be moved.
	 * 
	 * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
	 */
	@Override
	public void mousePressed(MouseEvent e) {
		Point point = calculator.pointCloseEnough(e.getX(), e.getY());
		if (controlPressed && point != null) {
			calculator.removePoint(point);
			undoManager.undoableEditHappened(new UndoableEditEvent(this,
					new UndoablePoint(point, calculator.getPoints(), true,
							false)));
			undo.setEnabled(undoManager.canUndo());
			redo.setEnabled(undoManager.canRedo());

		} else if (point != null) {
			pointClicked = true;
			movedPoint = point;
			index = calculator.getIndex(point);
			startingPoint = new Point(point.x, point.y);
		} else {
			observable.addPoint(e.getX(), e.getY());
			undoManager.undoableEditHappened(new UndoableEditEvent(this,
					new UndoablePoint(new Point(e.getX(), e.getY()), calculator
							.getPoints(), false, false)));
			undo.setEnabled(undoManager.canUndo());
			redo.setEnabled(undoManager.canRedo());
		}
		repaint();
	}

	/**
	 * When the mouse is released, the flags used for the mouse moving are
	 * reset.
	 * 
	 * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseReleased(MouseEvent e) {
		if (pointClicked && movedPoint != null) {
			movedPoint.setLocation(e.getX(), e.getY());
			calculator.setPoint(index, movedPoint);
			undoManager
					.undoableEditHappened(new UndoableEditEvent(this,
							new UndoablePoint(startingPoint, new Point(
									movedPoint.x, movedPoint.y), index,
									calculator.getPoints(), true)));
			undo.setEnabled(undoManager.canUndo());
			redo.setEnabled(undoManager.canRedo());
			repaint();
		}
		pointClicked = false;
		movedPoint = null;
		index = -1;
		startingPoint = null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseEntered(MouseEvent e) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseExited(MouseEvent e) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.KeyListener#keyTyped(java.awt.event.KeyEvent)
	 */
	@Override
	public void keyTyped(KeyEvent e) {
	}

	/**
	 * If 'Ctrl' key is pressed, a new flag is set.
	 * 
	 * @see java.awt.event.KeyListener#keyPressed(java.awt.event.KeyEvent)
	 */
	@Override
	public void keyPressed(KeyEvent e) {
		if (e.isControlDown()) {
			controlPressed = true;
		}
	}

	/**
	 * If 'Ctrl' key is released, the flag for it is reset.
	 * 
	 * @see java.awt.event.KeyListener#keyReleased(java.awt.event.KeyEvent)
	 */
	@Override
	public void keyReleased(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_CONTROL) {
			controlPressed = false;
		}
	}

	/**
	 * If a point is nearby the mouse coordinates, a tooltip with its
	 * coordinates is shown.
	 * 
	 * @see javax.swing.JComponent#getToolTipText(java.awt.event.MouseEvent)
	 */
	@Override
	public String getToolTipText(MouseEvent event) {
		Point point = event.getPoint();
		String str = null;
		Point closestPoint = calculator.pointCloseEnough(point.x, point.y);

		if (closestPoint != null) {
			str = closestPoint.x + ", " + closestPoint.y;
		}
		return str == null ? getToolTipText() : str;
	}
}
