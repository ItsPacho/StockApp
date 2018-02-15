package view;

import javax.swing.JFrame;

public class StockView extends JFrame {

	JFrame frame = new JFrame();

	public StockView() {

		frame.setTitle("Basic Window");
		frame.setSize(500, 500);
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);

	}
}

