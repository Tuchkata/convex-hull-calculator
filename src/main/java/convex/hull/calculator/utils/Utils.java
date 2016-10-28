package convex.hull.calculator.utils;

import javax.swing.UIManager;

/**
 * The utils class.
 * 
 * @author Teodor Shaterov
 */
public class Utils {

	/**
	 * A private constructor used to show that the class is a Utils one.
	 */
	private Utils() {
	}

	/**
	 * The standard look and feel for the system is used.
	 */
	public static void setLookAndFeel() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			System.err.println("Couldn't use the system look.");
		}
	}
}
