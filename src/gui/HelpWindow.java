package gui;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.io.IOException;
import java.net.URL;

import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLFrameHyperlinkEvent;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeSelectionModel;

import utils.Utils;

/**
 * The help window of the user interface.
 * 
 * @author Teodor Shaterov
 */
public class HelpWindow extends JPanel implements TreeSelectionListener {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1627236833109010961L;

	/** The html pane. */
	private JEditorPane htmlPane;

	/** The tree. */
	private JTree tree;

	/**
	 * Instantiates a new help window. It initializes the tree with the help
	 * categories and the html pane where the information is shown.
	 *
	 * @param container
	 *            the container to which the about window would be relative
	 */
	public HelpWindow(Container container) {
		super(new GridLayout(1, 0));
		Utils.setLookAndFeel();
		
		DefaultMutableTreeNode top = new DefaultMutableTreeNode(
				GuiConstants.HELP_CONVEX_HULL_ROOT);
		createTree(top);
		tree = new JTree(top);
		tree.getSelectionModel().setSelectionMode(
				TreeSelectionModel.SINGLE_TREE_SELECTION);
		tree.addTreeSelectionListener(this);

		htmlPane = new JEditorPane();
		htmlPane.setEditorKit(JEditorPane
				.createEditorKitForContentType("text/html"));
		htmlPane.addHyperlinkListener(new HyperlinkListener() {
			@Override
			public void hyperlinkUpdate(HyperlinkEvent e) {
				if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
					JEditorPane pane = (JEditorPane) e.getSource();
					if (e instanceof HTMLFrameHyperlinkEvent) {
						HTMLFrameHyperlinkEvent evt = (HTMLFrameHyperlinkEvent) e;
						HTMLDocument doc = (HTMLDocument) pane.getDocument();
						doc.processHTMLFrameHyperlinkEvent(evt);
					} else {
						try {
							pane.setPage(e.getURL());
						} catch (Throwable t) {
							// if throwable is caught, ignore it, just don't
							// show any information in the pane
						}
					}
				}
			}
		});
		htmlPane.setEditable(false);

		JScrollPane treeView = new JScrollPane(tree);
		JScrollPane htmlView = new JScrollPane(htmlPane);

		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		splitPane.setTopComponent(treeView);
		splitPane.setBottomComponent(htmlView);

		Dimension minimumSize = new Dimension(160, 200);
		htmlView.setMinimumSize(minimumSize);
		treeView.setMinimumSize(minimumSize);
		splitPane.setDividerLocation(170);
		splitPane.setPreferredSize(new Dimension(500, 400));

		add(splitPane);
	}

	/**
	 * Creates the tree with the categories.
	 *
	 * @param root
	 *            the root element
	 */
	private void createTree(DefaultMutableTreeNode root) {
		DefaultMutableTreeNode category = null;
		DefaultMutableTreeNode help = null;

		help = new DefaultMutableTreeNode(new Help(
				GuiConstants.HELP_INTRODUCTION,
				GuiConstants.HELP_HTML_INTRODUCTION));
		root.add(help);

		category = new DefaultMutableTreeNode(GuiConstants.HELP_GETTING_STARTED);
		root.add(category);
		help = new DefaultMutableTreeNode(new Help(
				GuiConstants.HELP_ADDING_POINTS,
				GuiConstants.HELP_HTML_ADDING_POINTS));
		category.add(help);
		help = new DefaultMutableTreeNode(new Help(
				GuiConstants.HELP_MOVING_POINTS,
				GuiConstants.HELP_HTML_MOVING_POINGS));
		category.add(help);
		help = new DefaultMutableTreeNode(new Help(
				GuiConstants.HELP_REMOVING_POINTS,
				GuiConstants.HELP_HTML_REMOVING_POINTS));
		category.add(help);
		help = new DefaultMutableTreeNode(new Help(GuiConstants.HELP_UNDO_REDO,
				GuiConstants.HELP_HTML_UNDO_REDO));
		category.add(help);

		help = new DefaultMutableTreeNode(new Help(
				GuiConstants.HELP_WORKING_WITH_FILES,
				GuiConstants.HELP_HTML_WORKING_WITH_FILES));
		root.add(help);

		help = new DefaultMutableTreeNode(new Help(
				GuiConstants.HELP_RANDOM_POINTS,
				GuiConstants.HELP_HTML_RANDOM_POINTS));
		root.add(help);

		help = new DefaultMutableTreeNode(new Help(
				GuiConstants.HELP_SHORTCUT_KEYS,
				GuiConstants.HELP_HTML_SHORTCUT_KEYS));
		root.add(help);
	}

	/**
	 * If the selection from the menu tree is changed, the selected element is
	 * handled
	 * 
	 * @see javax.swing.event.TreeSelectionListener#valueChanged(javax.swing.event
	 *      .TreeSelectionEvent)
	 */
	@Override
	public void valueChanged(TreeSelectionEvent e) {
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree
				.getLastSelectedPathComponent();
		if (node == null) {
			return;
		}
		
		Object nodeInfo = node.getUserObject();
		if (node.isLeaf()) {
			Help help = (Help) nodeInfo;
			displayHtml(help.fileName);
		}
	}

	/**
	 * Displays the HTML file where the selected help is stored.
	 *
	 * @param url
	 *            the url of the shown HTML
	 */
	private void displayHtml(URL url) {
		try {
			if (url != null) {
				htmlPane.setPage(url);
			} else {
				htmlPane.setText(GuiConstants.HELP_FILE_NOT_FOUND);
			}
		} catch (IOException e) {
			System.err.println("The given URL " + url + " cannot be read.");
		}
	}

	/**
	 * The Class Help. It is used to create categories and help elements.
	 */
	private class Help {

		/** The help type. */
		public String helpType;

		/** The file name. */
		public URL fileName;

		/**
		 * Instantiates a new help.
		 *
		 * @param helpType
		 *            the help type
		 * @param fileName
		 *            the file name
		 */
		public Help(String helpType, String fileName) {
			this.helpType = helpType;
			this.fileName = getClass().getResource(fileName);
			if (this.fileName == null) {
				System.err.println("File " + fileName + " could not be found.");
			}
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Object#toString()
		 */
		public String toString() {
			return helpType;
		}
	}
}
