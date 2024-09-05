package ib.grapher;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.Box.Filler;


/**
 * A graphical data table which stores all the data to be entered into a graph.
 */
public class PlottableTable extends JFrame {
	/** Sole constructor. */
	public PlottableTable() {
		super();
		setTitle("Data to Plot");

		// Initialize attributes.
		dataSets = new ArrayList<>();

		// Set up GUI.

		mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

		JPanel heading = new JPanel(new GridBagLayout());

		JLabel labelName = new JLabel("Name", SwingConstants.CENTER);
		labelName.setPreferredSize(new Dimension(120, 20));
		JLabel labelX = new JLabel("X-Axis", SwingConstants.CENTER);
		labelX.setPreferredSize(new Dimension(120, 20));
		JLabel labelY = new JLabel("Y-Axis", SwingConstants.CENTER);
		labelY.setPreferredSize(new Dimension(120, 20));
		JLabel labelBuffer = new JLabel();
		labelBuffer.setPreferredSize(new Dimension(180, 20));
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.weighty = 0;
		constraints.fill = GridBagConstraints.HORIZONTAL;

		constraints.weightx = 0.2;
		heading.add(labelName);
		constraints.gridx++;
		constraints.weightx = 0.3;
		heading.add(labelX);
		constraints.gridx++;
		constraints.weightx = 0.3;
		heading.add(labelY);
		constraints.gridx++;
		constraints.weightx = 0.2;
		heading.add(labelBuffer);
		add(heading, BorderLayout.NORTH);

		glue = (Filler) Box.createVerticalGlue();
		glue.changeShape(
			glue.getMinimumSize(),
			new Dimension(0, Integer.MAX_VALUE),
			glue.getMaximumSize()
		);
		mainPanel.add(glue);

		JScrollPane scrollPane = new JScrollPane(mainPanel);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

		add(scrollPane, BorderLayout.CENTER);

		JButton addButton = new JButton("Add Dataset");
		addButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				PlottableTable.this.addPlottableData(new PlottableData());
			}
		});
		add(addButton, BorderLayout.SOUTH);

		setMinimumSize(new Dimension(600, 200));
	}

	/** A list of plottable data sets. */
	private List<PlottableData> dataSets;

	/** The main panel that all of the menus are stored in. */
	private JPanel mainPanel;

	/** Glue which keeps all the JPanels grouped together in the mainPanel. */
	private Filler glue;


	/**
	 * Refreshes this panel, by calling {@link #invalidate()},
	 * {@link #validate()}, and {@link #repaint()}.
	 */
	public void doUpdate() {
		invalidate();
		validate();
		repaint();
	}


	/**
	 * Adds a new plottable data set to the project, along with a GUI element
	 * for it.
	 * @param pd The plottable data set to add.
	 */
	public void addPlottableData(PlottableData pd) {
		mainPanel.remove(glue);
		dataSets.add(pd);
		mainPanel.add(new PlottableDataMenu(pd, this));
		mainPanel.add(glue);
		Main.updateAllComponents();
	}

	/**
	 * Removes a specific plottable data menu from the GUI, along with its
	 * corresponding data set.
	 * @param pdm The plottable data menu to remove.
	 */
	public void removePlottableData(PlottableDataMenu pdm) {
		// Glue doesn't need to be removed here, since it always comes after the
		// last element which corresponds to a data set.
		dataSets.remove(pdm.getData());
		mainPanel.remove(pdm);
		Main.updateAllComponents();
	}

	// Getters and setters
	/**
	 * @return The plottable data.
	 */
	public List<PlottableData> getDataSets() {
		return dataSets;
	}

	/**
	 * Overwrites the list of plottable data.
	 * @param data The new list of plottable data sets.
	 */
	public void setDataSets(List<PlottableData> data) {
		dataSets = data;
	}
}
