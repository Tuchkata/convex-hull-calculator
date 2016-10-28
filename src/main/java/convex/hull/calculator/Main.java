package convex.hull.calculator;

import convex.hull.calculator.algorithm.HullCalculator;
import convex.hull.calculator.algorithm.HullObservable;
import convex.hull.calculator.gui.MainWindow;
import de.feu.propra15.interfaces.IHullCalculator;
import de.feu.propra15.tester.Tester;

/**
 * The main class.
 * 
 * @author Teodor Shaterov
 */
public class Main {

	/**
	 * The main method.
	 *
	 * @param args
	 *            if -t is given as an argument, the Tester class will execute
	 *            tests over the program, else the User interface will start
	 */
	public static void main(String[] args) {
		if (args.length > 0 && "-t".equals(args[0])) {
			IHullCalculator calculator = new HullCalculator(false);
			Tester tester = new Tester(args, calculator);
			System.out.println(tester.test());
		} else {
			HullCalculator calculator = new HullCalculator(true);
			HullObservable observer = new HullObservable(calculator);
			new MainWindow(calculator, observer);
		}
	}

}
