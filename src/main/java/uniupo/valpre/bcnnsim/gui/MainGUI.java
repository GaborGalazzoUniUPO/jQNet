package uniupo.valpre.bcnnsim.gui;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import uniupo.valpre.bcnnsim.network.QueueNetwork;
import uniupo.valpre.bcnnsim.network.node.Node;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileFilter;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.view.mxGraph;
import uniupo.valpre.bcnnsim.sim.MultiRunNetworkReport;
import uniupo.valpre.bcnnsim.sim.Simulator;

public class MainGUI {
	private JPanel mainPanel;
	private JButton selectFileBtn;
	private JTextField fileURIField;
	private JComboBox referenceStationComboBox;
	private JSpinner numCustomerField;
	private JSpinner numRunField;
	private JSpinner accuracyField;
	private JRadioButton relativeRadioButton;
	private JRadioButton absoluteRadioButton;
	private JSpinner precisionField;
	private JButton runSimulationButton;
	private JPanel networkPanel;
	private mxGraphComponent mxGraphComponent;


	private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
	private QueueNetwork currentNetwork;
	private mxGraph graph;

	public MainGUI() {


		$$$setupUI$$$();
		numRunField.setModel((new SpinnerNumberModel(0, 0, 100000000, 1)));
		numCustomerField.setModel((new SpinnerNumberModel(1000, 0, 100000000, 1)));
		accuracyField.setModel(new SpinnerNumberModel(0.95, 0.0, 1.0, 0.01));
		precisionField.setModel(new SpinnerNumberModel(0.05, 0.0, 1.0, 0.01));
		ButtonGroup buttonGroup = new ButtonGroup();
		buttonGroup.add(relativeRadioButton);
		buttonGroup.add(absoluteRadioButton);
		relativeRadioButton.setSelected(true);
		selectFileBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				final JFileChooser fc = new JFileChooser();
				fc.setCurrentDirectory(new File("."));
				fc.setAcceptAllFileFilterUsed(false);
				fc.addChoosableFileFilter(new FileFilter() {
					@Override
					public boolean accept(File f) {
						return f.getName().endsWith(".json");
					}

					@Override
					public String getDescription() {
						return "JSON - *.json files";
					}
				});
				int returnVal = fc.showOpenDialog(mainPanel);

				if (returnVal == JFileChooser.APPROVE_OPTION) {
					File file = fc.getSelectedFile();
					fileURIField.setText(file.getAbsolutePath());
					loadNetwork(file.getAbsolutePath());
					NetworkVisualizer.networkToGraph(currentNetwork, graph);


					referenceStationComboBox.setEnabled(true);
					referenceStationComboBox.removeAllItems();
					for (Node node : currentNetwork.getNodes()) {
						referenceStationComboBox.addItem(node.getName());
					}
				} else {
					fileURIField.setText("");
					runSimulationButton.setEnabled(false);
					referenceStationComboBox.setEnabled(false);
					referenceStationComboBox.removeAllItems();
				}
			}
		});
		referenceStationComboBox.addActionListener(e -> runSimulationButton.setEnabled(referenceStationComboBox.getSelectedItem() != null));
		JFrame frame = new JFrame("Simulation Output");
		frame.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
		ReportGUI output = new ReportGUI();
		frame.add(output.getMainPanel());
		frame.pack();
		runSimulationButton.addActionListener(e -> {

			frame.setVisible(true);
			frame.toFront();

			final AtomicReference<DefaultTableModel> lastModel = new AtomicReference<>(null);
			final AtomicLong progress = new AtomicLong(0);
			final Thread t = new Thread(() -> {
				var simulator = new Simulator();

				var inputNumRun = ((int) numRunField.getValue());
				var numRun = inputNumRun;
				if (numRun < 1) {
					numRun = 100;
				}

				var totalRuns = 0;

				AtomicBoolean forceStop = new AtomicBoolean(false);

				final AtomicBoolean endSym = new AtomicBoolean(false);
				output.getStopRunButton().addActionListener(l -> {
					forceStop.set(true);
					Thread.currentThread().interrupt();
				});
				while (!endSym.get() && !forceStop.get()) {
					output.getProgressBar().setValue(0);
					output.getProgressBar().setMinimum(0);
					output.getProgressBar().setMaximum(((int) numCustomerField.getValue()) * numRun);
					var report = simulator.runSimulation(currentNetwork, numRun,
							Objects.requireNonNull(referenceStationComboBox.getSelectedItem()).toString()
							, ((int) numCustomerField.getValue()),
							progress::set);
					var accReport = report.getResults();
					totalRuns += 100;

					DefaultTableModel model = new DefaultTableModel(new String[]{"Node", "Metrics", "Mean", "SD", "Coefficient", "Min Runs", "AccuracyReached"}, 0);
					endSym.set(true);
					for (Map.Entry<String, HashMap<String, MultiRunNetworkReport.ValueStream>> en : accReport.entrySet()) {
						for (Map.Entry<String, MultiRunNetworkReport.ValueStream> ee : en.getValue().entrySet()) {
							var m = ee.getValue().getMetricStatistics(((double) accuracyField.getValue()),
									absoluteRadioButton.isSelected() ? MultiRunNetworkReport.PrecisionType.Absolute : MultiRunNetworkReport.PrecisionType.Relative,
									((double) precisionField.getValue()));
							model.addRow(new Object[]{en.getKey(), ee.getKey(), m.mean(), m.sd(), m.tc(), m.mss(), m.done()});
							if (m.mss() != null) {
								var es = endSym.getAcquire();
								es = es && (m.mss() < totalRuns);
								endSym.setRelease(es);
							}
						}
					}

					lastModel.set(model);

					if (inputNumRun < 1) {
						if (endSym.get()) {
							int input = JOptionPane.showConfirmDialog(mainPanel, "Run ended and accuracy reached, would you continue?");
							endSym.set(input != 0);
						}

					} else {
						int input = JOptionPane.showConfirmDialog(mainPanel, "Run ended, would you continue?");
						endSym.set(input != 0);
					}
				}
			});
			t.start();
			final Thread tw = new Thread(() -> {
				while (t.isAlive()) {
					try {
						Thread.sleep(100);
					} catch (InterruptedException ex) {
						throw new RuntimeException(ex);
					}

					output.getProgressBar().setValue((int) progress.get());
					var model = lastModel.get();
					if (model == null) continue;
					output.getTable().setModel(model);

				}
			});
			tw.start();


		});
	}

	private void loadNetwork(String file) {
		try {
			var jsonString = Files.readString(Paths.get(file));
			var json = gson.fromJson(jsonString, JsonObject.class);
			currentNetwork = new QueueNetwork(json);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void createUIComponents() {
		graph = new mxGraph();
		mxGraphComponent = new mxGraphComponent(graph);
	}

	public Container getMainPanel() {
		return mainPanel;
	}

	/**
	 * Method generated by IntelliJ IDEA GUI Designer
	 * >>> IMPORTANT!! <<<
	 * DO NOT edit this method OR call it in your code!
	 *
	 * @noinspection ALL
	 */
	private void $$$setupUI$$$() {
		createUIComponents();
		mainPanel = new JPanel();
		mainPanel.setLayout(new GridLayoutManager(4, 5, new Insets(5, 5, 5, 5), -1, -1));
		final JPanel panel1 = new JPanel();
		panel1.setLayout(new GridLayoutManager(2, 6, new Insets(3, 3, 3, 3), -1, -1));
		mainPanel.add(panel1, new GridConstraints(2, 0, 1, 4, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
		panel1.setBorder(BorderFactory.createTitledBorder(null, "Evaluation Coefficients", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
		final JLabel label1 = new JLabel();
		label1.setText("Accuracy: ");
		panel1.add(label1, new GridConstraints(0, 0, 2, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		accuracyField = new JSpinner();
		accuracyField.setDoubleBuffered(false);
		panel1.add(accuracyField, new GridConstraints(0, 1, 2, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		final JLabel label2 = new JLabel();
		label2.setText("PrecisionType: ");
		panel1.add(label2, new GridConstraints(0, 2, 2, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		relativeRadioButton = new JRadioButton();
		relativeRadioButton.setText("Relative");
		panel1.add(relativeRadioButton, new GridConstraints(1, 3, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		absoluteRadioButton = new JRadioButton();
		absoluteRadioButton.setText("Absolute");
		panel1.add(absoluteRadioButton, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		final JLabel label3 = new JLabel();
		label3.setText("Precision: ");
		panel1.add(label3, new GridConstraints(0, 4, 2, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		precisionField = new JSpinner();
		panel1.add(precisionField, new GridConstraints(0, 5, 2, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		final JPanel panel2 = new JPanel();
		panel2.setLayout(new GridLayoutManager(2, 2, new Insets(3, 3, 3, 3), -1, -1));
		mainPanel.add(panel2, new GridConstraints(0, 0, 1, 4, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
		panel2.setBorder(BorderFactory.createTitledBorder(null, "Network Configuration", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
		selectFileBtn = new JButton();
		selectFileBtn.setText("Select Model");
		panel2.add(selectFileBtn, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		fileURIField = new JTextField();
		fileURIField.setEditable(false);
		panel2.add(fileURIField, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
		final JLabel label4 = new JLabel();
		label4.setText("Reference Station:");
		panel2.add(label4, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		referenceStationComboBox = new JComboBox();
		referenceStationComboBox.setEnabled(false);
		panel2.add(referenceStationComboBox, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		final JPanel panel3 = new JPanel();
		panel3.setLayout(new GridLayoutManager(1, 4, new Insets(3, 3, 3, 3), -1, -1));
		mainPanel.add(panel3, new GridConstraints(1, 0, 1, 4, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
		panel3.setBorder(BorderFactory.createTitledBorder(null, "Simulation Configuration", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
		final JLabel label5 = new JLabel();
		label5.setText("Customer for Run:");
		panel3.add(label5, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		numCustomerField = new JSpinner();
		panel3.add(numCustomerField, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		final JLabel label6 = new JLabel();
		label6.setText("Num Run:");
		panel3.add(label6, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		numRunField = new JSpinner();
		panel3.add(numRunField, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		runSimulationButton = new JButton();
		runSimulationButton.setEnabled(false);
		runSimulationButton.setText("Run Simulation");
		mainPanel.add(runSimulationButton, new GridConstraints(3, 0, 1, 4, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		networkPanel = new JPanel();
		networkPanel.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
		mainPanel.add(networkPanel, new GridConstraints(0, 4, 4, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_GROW, new Dimension(500, 500), null, null, 0, false));
		networkPanel.setBorder(BorderFactory.createTitledBorder(null, "Network", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
		networkPanel.add(mxGraphComponent, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
	}

	/**
	 * @noinspection ALL
	 */
	public JComponent $$$getRootComponent$$$() {
		return mainPanel;
	}
}
