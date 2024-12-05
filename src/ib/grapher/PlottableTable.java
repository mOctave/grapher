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
	// MARK: Constructor
	/** Sole constructor. */
	public PlottableTable() {
		super();
		dataSets = new ArrayList<>();

	
		// GUI
		setTitle("Data to Plot");
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



	// MARK: Properties
	/** A list of plottable data sets. */
	private List<PlottableData> dataSets;


	// GUI
	/** The main panel that all of the menus are stored in. */
	private final JPanel mainPanel;
	/** Glue which keeps all the JPanels grouped together in the mainPanel. */
	private final Filler glue;



	// MARK: Update
	/**
	 * Refreshes this panel, by calling {@link #invalidate()},
	 * {@link #validate()}, and {@link #repaint()}.
	 */
	public void doUpdate() {
		invalidate();
		validate();
		repaint();
	}



	// MARK: Convenience
	/**
	 * Clears every {@link PlottableData} set from this project.
	 */
	public void clear() {
		dataSets.clear();
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
	 * Removes a plotable data set from the project, as well as its assigned
	 * GUI component.
	 * @param pdm The plottable data menu to remove.
	 */
	public void removePlottableData(PlottableDataMenu pdm) {
		// Glue doesn't need to be removed here, since it always comes after the
		// last element which corresponds to a data set.
		dataSets.remove(pdm.getData());
		mainPanel.remove(pdm);
		Main.updateAllComponents();
	}



	// MARK: Getters / Setters
	/**
	 * Getter: Gets the plottable data for this table.
	 * @return {@link #dataSets}
	 */
	public List<PlottableData> getDataSets() {
		return dataSets;
	}

	/**
	 * Setter: Overwrites this table's list of plottable data.
	 * @param dataSets The new {@link #dataSets} for this table
	 */
	public void setDataSets(List<PlottableData> dataSets) {
		this.dataSets = dataSets;
	}


	// MARK: GUI
	/**
	 * Getter: Gets the main GUI panel for this table.
	 * @return {@link #mainPanel}
	 */
	public JPanel getMainPanel() {
		return mainPanel;
	}



	/**
	 * Getter: Gets the filler glue for this table.
	 * @return {@link #glue}
	 */
	public Filler getGlue() {
		return glue;
	}
}
