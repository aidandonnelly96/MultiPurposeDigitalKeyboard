package synth;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class Runner{
	
	/**
	 * runs the GUI.
	 */
	public static void main(String[] args) {
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				LayeredPaneDemo.createAndShowGUI();
			}
		});
	}
}
