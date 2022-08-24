package uniupo.valpre.bcnnsim.gui;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import uniupo.valpre.bcnnsim.network.QueueNetwork;
import uniupo.valpre.bcnnsim.network.node.Node;

import javax.swing.*;
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

					if (inputNumRun < 1) {
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
					var model = lastModel.get();
					if (model == null) continue;
					output.getTable().setModel(model);
					output.getProgressBar().setValue((int) progress.get());

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

}
