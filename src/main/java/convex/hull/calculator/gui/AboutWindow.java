package convex.hull.calculator.gui;

import java.awt.Component;
import java.awt.Container;
import java.awt.Font;
import java.net.URL;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import convex.hull.calculator.configuration.IConfigurationConstants;
import convex.hull.calculator.configuration.IResourcesPaths;
import convex.hull.calculator.utils.Utils;

/**
 * The about window of the user interface.
 * 
 * @author Teodor Shaterov
 */
public class AboutWindow extends JFrame {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 8879197226683082627L;

	/**
	 * Instantiates a new about window and fills all the information about the
	 * student, who is implementing the task, in it.
	 *
	 * @param container
	 *            the container to which the about window would be relative
	 */
	public AboutWindow(Container container) {
		Utils.setLookAndFeel();
		URL iconUrl = getClass().getClassLoader().getResource(IResourcesPaths.ICON_PATH);
		ImageIcon icon = new ImageIcon(iconUrl);
		setIconImage(icon.getImage());
		
		setTitle(IConfigurationConstants.WINDOW_ABOUT_NAME);
		setLocationRelativeTo(container);
		Container thisContainer = getContentPane();
		thisContainer.setLayout(new BoxLayout(thisContainer, BoxLayout.Y_AXIS));

		JLabel textLabel = new JLabel(IConfigurationConstants.LABEL_ABOUT_PROGRAMM);
		textLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		textLabel.setFont(new Font(Font.SERIF, Font.BOLD, 24));

		JLabel creator = new JLabel(IConfigurationConstants.LABEL_ABOUT_AUTHOR);
		creator.setAlignmentX(Component.CENTER_ALIGNMENT);
		creator.setFont(new Font(Font.SERIF, Font.BOLD, 18));

		thisContainer.add(Box.createVerticalStrut(30));
		thisContainer.add(textLabel);
		thisContainer.add(Box.createVerticalStrut(30));
		thisContainer.add(creator);

		setSize(380, 180);
		setResizable(false);
		setVisible(true);
	}
}
