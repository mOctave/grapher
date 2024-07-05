package ib.grapher;

import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.Border;

/**
 * The table which stores all the textual data for a graph.
 */
public class DataTable extends JFrame {
	/**
	 * A constructor which initializes the lists of this table, and also adds
	 * listeners and triggers for GUI functionality.
	 */
	public DataTable() {
		this.data = new ArrayList<>();
		this.activeCells = new ArrayList<>();
		this.scrollX = 0;
		this.scrollY = 0;

		this.tableLayout = new GridBagLayout();
		this.table = new JPanel(tableLayout);
		JScrollPane tableView = new JScrollPane(table);

		this.headerLayout = new GridBagLayout();
		this.header = new JPanel(headerLayout);
		tableView.setColumnHeaderView(header);
		header.setVisible(true);

		this.add(tableView);

		fillerT = new JPanel();
		fillerT.setOpaque(false);
		table.add(fillerT);

		fillerH = new JPanel();
		fillerH.setOpaque(false);
		header.add(fillerH);

		this.addComponentListener(new ComponentAdapter() {
			public void componentResized(ComponentEvent componentEvent) {
				update();
			}
		});

		this.setPreferredSize(new Dimension(400, 300));
		this.setMinimumSize(new Dimension(200, 150));
	}

	/** All the base data for this project. */
	private List<Series> data;
	/** The current row of cells to manipulate. */
	private List<Cell> activeCells;
	/** A measure of how far the table has been scrolled horizontally. */
	private int scrollX;
	/** A measure of how far the table has been scrolled vertically. */
	private int scrollY;

	private JPanel fillerT;
	private JPanel fillerH;
	private JPanel table;
	private GridBagLayout tableLayout;
	private JPanel header;
	private GridBagLayout headerLayout;

	/**
	 * Updates the main data table. Called whenever the window is resized or
	 * scrolled, or when the data in it changes.
	 */
	public void update() {
		GridBagConstraints constraints;
		for (Series r : data) {
			constraints = new GridBagConstraints();
			constraints.gridx = indexOf(r);
			constraints.gridy = 0;
			constraints.anchor = GridBagConstraints.FIRST_LINE_START;
			headerLayout.setConstraints(r, constraints);

			for (Cell c : r) {
				constraints = new GridBagConstraints();
				constraints.gridx = indexOf(c.getSeries());
				constraints.gridy = c.getIndex();
				constraints.anchor = GridBagConstraints.FIRST_LINE_START;
				tableLayout.setConstraints(c, constraints);

				c.update();
			}
		}
		if (data.size() > 0) {
			constraints = new GridBagConstraints();
			constraints.gridx = data.size();
			constraints.gridy = data.get(0).getLast().getIndex() + 1;
			constraints.weightx = 1.0;
			constraints.weighty = 1.0;

			tableLayout.setConstraints(fillerT, constraints);
			headerLayout.setConstraints(fillerH, constraints);
		}
	}

	public void addCell(Cell c) {
		table.add(c);
		this.update();
	}

	// Data utility methods

	/**
	 * Gets all the base data for the project.
	 * @return A reference to {@link #data}
	 */
	public List<Series> getData() {
		return data;
	}

	/**
	 * Gets a single series from the base data.
	 * @param i The index of the series to get
	 * @return A reference to the desired {@link Series} object
	 */
	public Series getSeries(int i) {
		return data.get(i);
	}

	/**
	 * Gets the index of the specified series.
	 * @param r The {@link Series} object to search for
	 * @return The index of the the series in the data list, or -1 if it is not
	 * contained in the data list
	 */
	public int indexOf(Series r) {
		return data.indexOf(r);
	}

	/**
	 * Adds the specified series to the data list.
	 * @param r The {@link Series} object to add
	 */
	public void addSeries(Series r) {
		header.add(r);
		data.add(r);
	}

	/**
	 * Inserts a series at a specific position in the data list.
	 * @param i The index to insert the series at
	 * @param r The {@link Series} object to insert
	 */
	public void insertSeries(int i, Series r) {
		header.add(r);
		data.add(i, r);
	}

	/**
	 * Removes the specified series from the data list.
	 * @param r The {@link Series} object to remove
	 */
	public void removeSeries(Series r) {
		data.remove(r);
	}



	// Active Cell Utility Methods

	/**
	 * Gets the currently active row of cells.
	 * @return A reference to the list of {@link #activeCells}
	 */
	public List<Cell> getActiveCells() {
		return activeCells;
	}

	/**
	 * Resets the currently active row of cells, moving them to the first row of
	 * the data table.
	 */
	public void resetActiveCells() {
		activeCells = new ArrayList<>();
		for (Series r : data) {
			activeCells.add(r.getFirst());
		}
	}

	/**
	 * Rolls the currently active row of cells forward (down) one row.
	 */
	public void rollActiveCellsForward() {
		for (int i = 0; i < activeCells.size(); i++) {
			activeCells.set(i, activeCells.get(i).getNext());
		}
	}



	// Other getters and setters

	/**
	 * Gets the current horizontal scroll offset.
	 * @return The horizontal scroll offset
	 */
	public int getScrollX() {
		return scrollX;
	}

	/**
	 * Changes the current horizontal scroll offset.
	 * @param i The new position to scroll to
	 */
	public void setScrollX(int i) {
		scrollX = i;
	}

	/**
	 * Gets the current vertical scroll offset.
	 * @return The vertical scroll offset
	 */
	public int getScrollY() {
		return scrollY;
	}

	/**
	 * Changes the current vertical scroll offset.
	 * @param i The new position to scroll to
	 */
	public void setScrollY(int i) {
		scrollY = i;
	}
}
