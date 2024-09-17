package ib.grapher;

import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;

public class DataTableModel extends AbstractTableModel {
	/** Sole constructor initializes the data table. */
	public DataTableModel() {
		data = new ArrayList<>();
		selectedCell = null;

		data.add(new Series(1, this));
	}

	/** All the base data for this project. */
	private List<Series> data;
	/** The current selected cell. */
	private Cell selectedCell;

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
		data.add(r);
		for (SeriesSelector selector : Main.getSelectors()) {
			selector.refresh();
		}
		Main.updateAllComponents();
	}

	/**
	 * Inserts a series at a specific position in the data list.
	 * @param i The index to insert the series at
	 * @param r The {@link Series} object to insert
	 */
	public void insertSeries(int i, Series r) {
		data.add(i, r);
		for (SeriesSelector selector : Main.getSelectors()) {
			selector.refresh();
		}
		Main.updateAllComponents();
	}

	/**
	 * Removes the specified series from the data list.
	 * @param r The {@link Series} object to remove
	 */
	public void removeSeries(Series r) {
		data.remove(r);
		for (SeriesSelector selector : Main.getSelectors()) {
			selector.refresh();
		}
		Main.updateAllComponents();
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
		Main.updateAllComponents();
	}

	// Interface methods

	@Override
	public int getRowCount() {
		if (data.size() == 0)
			return 0;
		
		return data.get(0).length();
	}

	@Override
	public int getColumnCount() {
		return data.size() + 1;
	}

	@Override
	public String getColumnName(int columnIndex) {
		if (columnIndex == 0)
			return "";
		return data.get(columnIndex - 1).getName();
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		if (columnIndex == 0)
			return Integer.class;

		return String.class;
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		if (columnIndex == 0)
			return false;

		return true;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		if (columnIndex == 0)
			return rowIndex + 1;

		return data.get(columnIndex - 1).get(rowIndex).getValue();
	}

	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {

		data.get(columnIndex - 1).get(rowIndex).setValue((String) aValue);
	}
}
