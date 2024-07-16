package ib.grapher;

import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;

/**
 * The table which stores all the textual data for a graph.
 */
public class DataTable extends JFrame {
	/**
	 * A constructor which initializes the lists of this table, and also adds
	 * listeners and triggers for GUI functionality.
	 */
	public DataTable() {
		// Initialize non-GUI properties
		this.data = new ArrayList<>();
		this.activeCells = new ArrayList<>();

		// Initialize GUI
		this.setLayout(new BorderLayout());

		this.tableLayout = new GridBagLayout();
		this.table = new JPanel(tableLayout);
		JScrollPane tableView = new JScrollPane(table);

		this.headerLayout = new GridBagLayout();
		this.header = new JPanel(headerLayout);
		tableView.setColumnHeaderView(header);
		header.setVisible(true);

		this.rowNumbers = new JPanel();
		this.rowNumbers.setLayout(new GridBagLayout());
		tableView.setRowHeaderView(rowNumbers);
		rowNumbers.setVisible(true);

		this.add(tableView, BorderLayout.CENTER);

		fillerT = new JPanel();
		fillerT.setOpaque(false);
		table.add(fillerT);

		fillerH = new JPanel();
		fillerH.setOpaque(false);
		header.add(fillerH);

		rnFiller = new JPanel();
		rnFiller.setOpaque(false);
		rowNumbers.add(rnFiller);

		this.title = new JLabel("<html><i>Unsaved File</i></html>");
		title.setHorizontalAlignment(SwingConstants.CENTER);
		this.add(title, BorderLayout.NORTH);

		this.setPreferredSize(new Dimension(400, 300));
		this.setMinimumSize(new Dimension(200, 150));
		this.setJMenuBar(Main.getMenuBar());
	}

	/** All the base data for this project. */
	private List<Series> data;
	/** The current row of cells to manipulate. */
	private List<Cell> activeCells;
	/** The row numbers which make up the sidebar. */
	private JPanel rowNumbers;
	/** The current selected cell. */
	private Cell selectedCell;

	/** "Filler" panels to keep the GridBagLayouts aligned with the top left. */
	private JPanel fillerT;
	private JPanel fillerH;
	private JPanel rnFiller;

	private JLabel title;

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

		int len = 0;
		try {
			len = data.get(0).length();
		} catch (IndexOutOfBoundsException e) {}
		while (len > rowNumbers.getComponentCount() - 1)
			addRowNumber();
		while (rowNumbers.getComponentCount() - 1 > len)
			removeRowNumber();

	}

	public void addCell(Cell c) {
		table.add(c);
		update();
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
		resetActiveCells();
		update();
	}

	/**
	 * Inserts a series at a specific position in the data list.
	 * @param i The index to insert the series at
	 * @param r The {@link Series} object to insert
	 */
	public void insertSeries(int i, Series r) {
		header.add(r);
		data.add(i, r);
		resetActiveCells();
		update();
	}

	/**
	 * Removes the specified series from the data list.
	 * @param r The {@link Series} object to remove
	 */
	public void removeSeries(Series r) {
		data.remove(r);
		resetActiveCells();
		update();
	}

	/**
	 * Creates a new {@link javax.swing.JLabel} with the proper formatting for
	 * a row number object, then adds it to the list of row numbers.
	 */
	private void addRowNumber() {
		// Remove filler
		rowNumbers.remove(rowNumbers.getComponentCount() - 1);

		GridBagConstraints rnConstraints = new GridBagConstraints();
		rnConstraints.fill = GridBagConstraints.HORIZONTAL;
		rnConstraints.weightx = 1;
		rnConstraints.gridx = 1;
		rnConstraints.weighty = 0;
		rnConstraints.gridy = rowNumbers.getComponentCount();

		JLabel rowNumber = new JLabel();
		rowNumber.setText(Integer.toString(rowNumbers.getComponentCount() + 1));
		rowNumber.setBorder(BorderFactory.createCompoundBorder(
			new EmptyBorder(new Insets(5, 2, 5, 2)),
			new EtchedBorder(EtchedBorder.RAISED)
		));
		rowNumber.setMinimumSize(new Dimension(20, 30));
		rowNumber.setPreferredSize(new Dimension(20, 30));
	
		rowNumbers.add(rowNumber, rnConstraints);

		// Re-add filler
		rnConstraints.weighty = 1;
		rnConstraints.gridy ++;
		rowNumbers.add(rnFiller, rnConstraints);
	}

	/**
	 * Removes the last row number, updating the filler in doing so.
	 */
	private void removeRowNumber() {
		try {
			// Remove filler
			rowNumbers.remove(rowNumbers.getComponentCount() - 1);

			// Remove actual number
			rowNumbers.remove(rowNumbers.getComponentCount() - 1);

			// Re-add filler
			GridBagConstraints rnConstraints = new GridBagConstraints();
			rnConstraints.weighty = 1;
			rnConstraints.gridy = rowNumbers.getComponentCount();
			rowNumbers.add(rnFiller, rnConstraints);
		} catch (IndexOutOfBoundsException e) {
			System.out.println(
				"Could not remove row number: no row number to remove");
		}
	}

	/**
	 * Changes the background colour of a specified row number label.
	 * @param i The row number to change (starting at 0).
	 * @param c The {@link java.awt.Color} to change it to.
	 */
	public void setRowNumberBackground(int i, Color c) {
		rowNumbers.getComponent(i).setBackground(c);
	}



	// Active Cell Utility Methods

	/**
	 * Gets the currently selected cell.
	 * @return The selected cell.
	 */
	public Cell getSelectedCell() {
		return selectedCell;
	}

	/**
	 * Changes the cell which is marked as selected, without reformatting the
	 * cell.
	 * @param c The newly selected cell.
	 */
	public void setSelectedCell(Cell c) {
		selectedCell = c;
	}

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

	/**
	 * Rolls the currently active row of cells backward (up) one row.
	 */
	public void rollActiveCellsBackward() {
		for (int i = 0; i < activeCells.size(); i++) {
			activeCells.set(i, activeCells.get(i).getPrevious());
		}
	}

	/**
	 * Moves the row of active cells until it aligns with the selected cell.
	 */
	public void matchActiveToSelected() {
		while (activeCells.get(0).getIndex() > selectedCell.getIndex())
			rollActiveCellsBackward();
		while (activeCells.get(0).getIndex() < selectedCell.getIndex())
			rollActiveCellsForward();
	}
}

class ColumnNumber extends JLabel {
	/**
	 * A constructor that allows for the creation of a label with specific
	 * text.
	 * @param s The text to put on the label.
	 */
	public ColumnNumber(String s) {
		this.setText(s);
		this.setPreferredSize(new Dimension(20, 30));
	}

}
