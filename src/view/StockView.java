package view;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;

import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class StockView extends JFrame {

	JFrame frame = new JFrame();

	StockView() {

		frame.setTitle("Basic Window");
		frame.setSize(500, 500);
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
		
		////////////////////
		// DATA SERIES PANEL
		////////////////////
		
		// Create Panel
		JPanel Panel = new JPanel();
		Panel.setLayout(new GridBagLayout());
		GridBagConstraints gridConstraints = new GridBagConstraints();
		gridConstraints.gridx = 1;
		gridConstraints.gridy = 1;
		gridConstraints.gridwidth = 1;
		gridConstraints.gridheight = 1;
		gridConstraints.weightx = 1;
		gridConstraints.weighty = 1;
		gridConstraints.insets = new Insets(5, 5, 5, 5);
		gridConstraints.anchor = GridBagConstraints.CENTER;
		gridConstraints.weighty = GridBagConstraints.BOTH;
		
		
		
		frame.add(Panel);
		
		// Data Label
		JLabel dataLabel = new JLabel("Data Series");
		dataLabel.setVisible(true);
		Panel.add(dataLabel);
		
		// Data DropDown menu
		String[] dataChoices = {"1", "2", "3", "4"};
		JComboBox<String> dataComboBox = new JComboBox<String>(dataChoices);
		Panel.add(dataComboBox);
		
		////////////////////
		// TIME SERIES PANEL
		////////////////////
		
		// Time Label
		JLabel timeLabel = new JLabel("Time Series");
		timeLabel.setVisible(true);
		Panel.add(timeLabel);
		
		// Data DropDown menu
		String[] timeChoices = {"1", "2", "3", "4"};
		JComboBox<String> timeComboBox = new JComboBox<String>(timeChoices);
		Panel.add(timeComboBox);
		
	}
}

