package gui;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Random;

import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;
import javax.swing.SpringLayout;
import javax.swing.ToolTipManager;
import javax.swing.event.UndoableEditEvent;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.UndoManager;

import algorithm.HullCalculator;
import algorithm.HullObservable;
import utils.Utils;

// TODO: Auto-generated Javadoc
/**
 * The Class MainWindow. It creates and draws the main window of the
 * application.
 * 
 * @author Teodor Shaterov
 */
public class MainWindow extends JFrame implements Observer {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 6329678303636873262L;

	/** The calculator. */
	private HullCalculator calculator;

	/** The observable. */
	private HullObservable observable;

	/** The points area. */
	private PointsArea pointsArea;

	/** The file name. */
	private String fileName = null;

	/** The options menu. */
	private static final int OPTIONS_MENU = 1;

	/** The undo menu item. */
	private static final int OPTIONS_MENU_UNDO = 0;

	/** The redo menu item. */
	private static final int OPTIONS_MENU_REDO = 1;

	/**
	 * Instantiates a new main window. It draws the canvas, where the points are
	 * shown, the menus and the main frame of the application.
	 *
	 * @param calculator
	 *            the calculator
	 * @param observable
	 *            the observable
	 */
	public MainWindow(HullCalculator calculator, HullObservable observable) {
		Utils.setLookAndFeel();

		this.calculator = calculator;
		this.observable = observable;
		observable.addObserver(this);
		URL iconUrl = getClass().getResource("icon.png");
		ImageIcon icon = new ImageIcon(iconUrl);
		setIconImage(icon.getImage());

		setTitle(GuiConstants.WINDOW_MAIN_NAME);
		setBounds(50, 50, 700, 700);

		UndoManager undoManager = new UndoManager();
		undoManager.setLimit(Integer.MAX_VALUE);
		JMenuBar menu = createMenuBar(undoManager);
		setJMenuBar(menu);
		JMenuItem undo = menu.getMenu(OPTIONS_MENU).getItem(OPTIONS_MENU_UNDO);
		JMenuItem redo = menu.getMenu(OPTIONS_MENU).getItem(OPTIONS_MENU_REDO);

		Container container = getContentPane();
		SpringLayout layout = new SpringLayout();

		container.setLayout(layout);
		pointsArea = new PointsArea(calculator, observable, undoManager, undo,
				redo);
		ToolTipManager.sharedInstance().registerComponent(pointsArea);
		container.add(pointsArea);
		layout.putConstraint(SpringLayout.NORTH, pointsArea, 5,
				SpringLayout.NORTH, container);
		layout.putConstraint(SpringLayout.WEST, pointsArea, 5,
				SpringLayout.WEST, container);
		layout.putConstraint(SpringLayout.EAST, pointsArea, -5,
				SpringLayout.EAST, container);
		layout.putConstraint(SpringLayout.SOUTH, pointsArea, -5,
				SpringLayout.SOUTH, container);

		setVisible(true);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
	}

	/**
	 * Creates the menu bar. It fills all categories of the menus and the action
	 * listeners which handle the mouse events from the menu.
	 *
	 * @param undoManager the undo manager
	 * @return the menu bar
	 */
	private JMenuBar createMenuBar(final UndoManager undoManager) {
		JMenuBar menuBar = new JMenuBar();

		JMenu fileMenu = new JMenu(GuiConstants.MENU_FILE);
		JMenuItem fileMenuNew = new JMenuItem(GuiConstants.MENU_FILE_NEW);
		JMenuItem fileMenuOpen = new JMenuItem(GuiConstants.MENU_FILE_OPEN);
		JMenuItem fileMenuSave = new JMenuItem(GuiConstants.MENU_FILE_SAVE);
		JMenuItem fileMenuSaveAs = new JMenuItem(GuiConstants.MENU_FILE_SAVE_AS);
		JMenuItem fileMenuExit = new JMenuItem(GuiConstants.MENU_FILE_EXIT);

		JMenu optionsMenu = new JMenu(GuiConstants.MENU_OPTIONS);
		final JMenuItem optionsMenuUndo = new JMenuItem(
				GuiConstants.MENU_OPTIONS_UNDO);
		final JMenuItem optionsMenuRedo = new JMenuItem(
				GuiConstants.MENU_OPTIONS_REDO);
		JMenu optionsRandomMenu = new JMenu(GuiConstants.MENU_OPTIONS_RANDOM);
		JMenuItem optionsRandomMenu10 = new JMenuItem(
				GuiConstants.MENU_OPTIONS_RANDOM_10);
		JMenuItem optionsRandomMenu50 = new JMenuItem(
				GuiConstants.MENU_OPTIONS_RANDOM_50);
		JMenuItem optionsRandomMenu100 = new JMenuItem(
				GuiConstants.MENU_OPTIONS_RANDOM_100);
		JMenuItem optionsRandomMenu500 = new JMenuItem(
				GuiConstants.MENU_OPTIONS_RANDOM_500);
		JMenuItem optionsRandomMenu1000 = new JMenuItem(
				GuiConstants.MENU_OPTIONS_RANDOM_1000);

		JMenu helpMenu = new JMenu(GuiConstants.MENU_HELP);
		JMenuItem helpMenuHelp = new JMenuItem(GuiConstants.MENU_HELP_HELP);
		JMenuItem helpMenuAbout = new JMenuItem(GuiConstants.MENU_HELP_ABOUT);

		menuBar.add(fileMenu);
		menuBar.add(optionsMenu);
		menuBar.add(helpMenu);

		fileMenu.add(fileMenuNew);
		fileMenu.add(fileMenuOpen);
		fileMenu.addSeparator();
		fileMenu.add(fileMenuSave);
		fileMenu.add(fileMenuSaveAs);
		fileMenu.addSeparator();
		fileMenu.add(fileMenuExit);

		optionsMenu.add(optionsMenuUndo);
		optionsMenu.add(optionsMenuRedo);
		optionsMenuUndo.setEnabled(false);
		optionsMenuRedo.setEnabled(false);
		optionsMenu.add(optionsRandomMenu);
		optionsRandomMenu.add(optionsRandomMenu10);
		optionsRandomMenu.add(optionsRandomMenu50);
		optionsRandomMenu.add(optionsRandomMenu100);
		optionsRandomMenu.add(optionsRandomMenu500);
		optionsRandomMenu.add(optionsRandomMenu1000);

		helpMenu.add(helpMenuHelp);
		helpMenu.addSeparator();
		helpMenu.add(helpMenuAbout);

		fileMenuNew.setAccelerator(KeyStroke
				.getKeyStroke(GuiConstants.SHORTCUT_NEW));
		fileMenuOpen.setAccelerator(KeyStroke
				.getKeyStroke(GuiConstants.SHORTCUT_OPEN));
		fileMenuSave.setAccelerator(KeyStroke
				.getKeyStroke(GuiConstants.SHORTCUT_SAVE));
		fileMenuSaveAs.setAccelerator(KeyStroke
				.getKeyStroke(GuiConstants.SHORTCUT_SAVE_AS));
		fileMenuExit.setAccelerator(KeyStroke
				.getKeyStroke(GuiConstants.SHORTCUT_EXIT));

		optionsMenuUndo.setAccelerator(KeyStroke
				.getKeyStroke(GuiConstants.SHORTCUT_UNDO));
		optionsMenuRedo.setAccelerator(KeyStroke
				.getKeyStroke(GuiConstants.SHORTCUT_REDO));

		helpMenuHelp.setAccelerator(KeyStroke
				.getKeyStroke(GuiConstants.SHORTCUT_HELP));

		fileMenuNew.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				calculator.clear();
				fileName = null;
				undoManager.discardAllEdits();
				optionsMenuUndo.setEnabled(undoManager.canUndo());
				optionsMenuRedo.setEnabled(undoManager.canRedo());
				setTitle(GuiConstants.WINDOW_MAIN_NAME);
			}
		});

		fileMenuOpen.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser fileChooser = new JFileChooser();
				fileChooser.setCurrentDirectory(new File(new File(System
						.getProperty(GuiConstants.USER_DIR)).getParentFile()
						.getAbsolutePath()
						+ GuiConstants.TESTER_FOLDER));
				fileChooser.showOpenDialog(getContentPane());
				File file = fileChooser.getSelectedFile();
				if (file != null) {
					fileName = file.getAbsolutePath();
					setTitle(GuiConstants.WINDOW_MAIN_NAME + " - " + fileName);
					try {
						observable.addPointsFromFile(file.getAbsolutePath());
					} catch (IOException e1) {
						// if an exception is caught, ignore it, no information
						// would be shown in the canvas
					}
				}
			}
		});

		fileMenuSave.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (fileName == null) {
					JFileChooser fileChooser = new JFileChooser();
					fileChooser.setCurrentDirectory(new File(new File(System
							.getProperty(GuiConstants.USER_DIR))
							.getParentFile().getAbsolutePath()
							+ GuiConstants.TESTER_FOLDER));
					fileChooser.showSaveDialog(getContentPane());
					File file = fileChooser.getSelectedFile();
					if (file != null) {
						fileName = file.getAbsolutePath();
						setTitle(GuiConstants.WINDOW_MAIN_NAME + " - "
								+ fileName);
					}
				}
				if (fileName != null) {
					writeToFile(fileName);
				}
				undoManager.discardAllEdits();
			}
		});

		fileMenuSaveAs.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser fileChooser = new JFileChooser();
				fileChooser.setCurrentDirectory(new File(new File(System
						.getProperty(GuiConstants.USER_DIR)).getParentFile()
						.getAbsolutePath()
						+ GuiConstants.TESTER_FOLDER));
				fileChooser.showSaveDialog(getContentPane());
				File file = fileChooser.getSelectedFile();
				if (file != null) {
					fileName = file.getAbsolutePath();
					writeToFile(fileName);
					setTitle(GuiConstants.WINDOW_MAIN_NAME + " - " + fileName);
				}
				undoManager.discardAllEdits();
			}
		});

		fileMenuExit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});

		optionsMenuUndo.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					undoManager.undo();
				} catch (CannotRedoException cre) {
				}
				optionsMenuUndo.setEnabled(undoManager.canUndo());
				optionsMenuRedo.setEnabled(undoManager.canRedo());

			}
		});

		optionsMenuRedo.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					undoManager.redo();
				} catch (CannotRedoException cre) {
				}
				optionsMenuUndo.setEnabled(undoManager.canUndo());
				optionsMenuRedo.setEnabled(undoManager.canRedo());

			}
		});

		optionsRandomMenu10.addActionListener(new RandomPoints(10, undoManager,
				optionsMenuUndo, optionsMenuRedo));
		optionsRandomMenu50.addActionListener(new RandomPoints(50, undoManager,
				optionsMenuUndo, optionsMenuRedo));
		optionsRandomMenu100.addActionListener(new RandomPoints(100,
				undoManager, optionsMenuUndo, optionsMenuRedo));
		optionsRandomMenu500.addActionListener(new RandomPoints(500,
				undoManager, optionsMenuUndo, optionsMenuRedo));
		optionsRandomMenu1000.addActionListener(new RandomPoints(1000,
				undoManager, optionsMenuUndo, optionsMenuRedo));

		helpMenuHelp.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFrame frame = new JFrame(GuiConstants.WINDOW_HELP_NAME);
				URL iconUrl = getClass().getResource("icon.png");
				ImageIcon icon = new ImageIcon(iconUrl);
				frame.setIconImage(icon.getImage());

				frame.setLocationRelativeTo(getContentPane());
				// Add content to the window.

				frame.add(new HelpWindow(getContentPane()));

				// Display the window.
				frame.pack();
				frame.setVisible(true);
				;
			}
		});

		helpMenuAbout.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				new AboutWindow(getContentPane());
			}
		});

		return menuBar;
	}

	/**
	 * This method is called when the observed object is changed.
	 *
	 * @param o the o
	 * @param arg the arg
	 * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
	 */
	@Override
	public void update(Observable o, Object arg) {
		pointsArea.repaint();
	}

	/**
	 * Write to file. Used to store the coordinates of the drawn points to a
	 * file.
	 *
	 * @param fileName
	 *            the file name
	 */
	private void writeToFile(String fileName) {
		Writer writer = null;
		try {
			writer = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(fileName), "utf-8"));
			for (Point point : calculator.getPoints()) {
				writer.write(point.x + " " + point.y + "\n");
			}
		} catch (IOException ex) {
			// ignore at the moment. An error window would be a good idea. Can
			// be implemented in the next part of the project - the Winkelhuelle
		} finally {
			try {
				writer.close();
			} catch (Exception ex) {
				// ignore this exception
			}
		}
	}

	/**
	 * The Class RandomPoints. Used to create random amount of points.
	 */
	private class RandomPoints implements ActionListener {

		/** The size. */
		private int size;

		/** The undo manager. */
		private UndoManager undoManager;

		/** The undo menu item. */
		private JMenuItem undo;

		/** The redo menu item. */
		private JMenuItem redo;

		/**
		 * Instantiates a new random points.
		 *
		 * @param size
		 *            the size
		 * @param undoManager
		 *            the undo manager
		 * @param undo
		 *            the undo menu item
		 * @param redo
		 *            the redo menu item
		 */
		public RandomPoints(int size, UndoManager undoManager, JMenuItem undo,
				JMenuItem redo) {
			this.size = size;
			this.undoManager = undoManager;
			this.undo = undo;
			this.redo = redo;
		}

		/*
		 * Invoked when an action occurs.
		 * 
		 * @see
		 * java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent
		 * )
		 */
		@Override
		public void actionPerformed(ActionEvent e) {
			Dimension screenSize = getContentPane().getSize();
			Random random = new Random();
			int height = (int) screenSize.getHeight() - 20;
			int width = (int) screenSize.getWidth() - 20;
			int min_width_center = (int) width * 1 / 3;
			int max_width_center = (int) width * 2 / 3;
			int min_height_center = (int) height * 1 / 3;
			int max_height_center = (int) height * 2 / 3;
			List<Point> undoablePoints = new ArrayList<Point>();

			for (int i = 0; i < size; i++) {
				Point point = new Point(random.nextInt((max_width_center
						- min_width_center + 1))
						+ min_width_center, random.nextInt((max_height_center
						- min_height_center + 1))
						+ min_height_center);
				undoablePoints.add(point);
				observable.addPoint(point);
			}
			undoManager.undoableEditHappened(new UndoableEditEvent(this,
					new UndoablePoint(undoablePoints, calculator.getPoints(),
							false, false)));
			undo.setEnabled(undoManager.canUndo());
			redo.setEnabled(undoManager.canRedo());
		}
	}
}
