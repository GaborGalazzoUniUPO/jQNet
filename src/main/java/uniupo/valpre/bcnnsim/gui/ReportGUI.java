package uniupo.valpre.bcnnsim.gui;

import javax.swing.*;
import java.awt.*;

public class ReportGUI {
	private JProgressBar progressBar;
	private JTable table;
	private JPanel mainPanel;
	private JScrollPane scroll;
	private JButton stopRunButton;

	public Component getMainPanel() {
		return mainPanel;
	}

	public JTable getTable() {
		return table;
	}

	public JProgressBar getProgressBar() {
		return progressBar;
	}

	public JButton getStopRunButton() {
		return stopRunButton;
	}
}
