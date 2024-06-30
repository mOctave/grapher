package ib.grapher;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;

/**
 * A class which represents a single cell in the data table. An object of this
 * class both functions as a node in a doubly linked list and provides a
 * graphical representation of its data, along with the means to edit it.
 */
public class Cell extends JPanel {
	/**
	 * A basic constructor for a cell, which initializes the text field and
	 * handles graphical layout.
	 */
	public Cell() {
		textField = new JTextField();
		this.setBorder(BorderFactory.createCompoundBorder(
			new EmptyBorder(2, 2, 2, 2),
			new EtchedBorder(EtchedBorder.RAISED)
		));
		this.add(textField);
	}


	/** The cell that comes before this one in its series. */
	private Cell previousCell = null;
	/** The cell that comes after this one in its series. */
	private Cell nextCell = null;
	/** The textual value of this cell. */
	private String value = "";
	/** A graphical text field to allow for data entry. */
	private JTextField textField = null;
	/** The index of this cell in its series. */
	private int index = -1;
	/** The series this cell belongs to. */
	private Series series = null;



	/**
	 * Standard update loop, called whenever the DataTable updates.
	 */
	public void update() {

	}



	/**
	 * Gets the numeric value of this cell. If this cell does not contain
	 * numeric data, this method will throw a NumberFormatException.
	 * @return The double numeric value of this cell
	 */
	public double getNumeric() throws NumberFormatException {
		return Double.parseDouble(this.value);
	}



	/**
	 * Inserts another cell before this one in its series.
	 * @param insertedCell The cell to insert
	 */
	public void insertCellBefore(Cell insertedCell) {
		insertedCell.setSeries(this.series);
		Cell oldPrevious = this.previousCell;

		if (oldPrevious == null) {
			this.series.setFirst(insertedCell);
			if (this.getNext() == null)
				this.series.setLast(insertedCell);
		} else {
			oldPrevious.setNext(insertedCell);
			insertedCell.setPrevious(oldPrevious);
		}

		insertedCell.setNext(this);
		insertedCell.setIndex(this.index);
		this.setPrevious(insertedCell);

		Cell c = this;
		while (c != null) {
			c.setIndex(c.getIndex() + 1);
			c = c.getNext();
		}
	}



	/**
	 * Inserts another cell after this one in its series.
	 * @param insertedCell The cell to insert
	 */
	public void insertCellAfter(Cell insertedCell) {
		insertedCell.setSeries(this.series);
		Cell oldNext = this.nextCell;
		insertedCell.setPrevious(this);
		insertedCell.setIndex(this.index + 1);
		this.setNext(insertedCell);

		if (oldNext == null) {
			this.series.setLast(insertedCell);
			if (this.getPrevious() == null)
				this.series.setFirst(insertedCell);
		} else {
			oldNext.setPrevious(insertedCell);
			insertedCell.setNext(oldNext);
		}

		Cell c = oldNext;
		while (c != null) {
			c.setIndex(c.getIndex() + 1);
			c = c.getNext();
		}
	}



	/**
	 * Removes this cell from its series.
	 */
	public void remove() {
		Cell oldPrevious = this.previousCell;
		Cell oldNext = this.nextCell;

		// Recalculate indices
		Cell c = oldNext;
		while (c != null) {
			c.setIndex(c.getIndex() - 1);
			c = c.getNext();
		}

		// Link the old previous and next cells together
		if (oldPrevious == null)
			this.series.setFirst(this.nextCell);
		else
			oldPrevious.setNext(this.nextCell);

		if (oldNext == null)
			this.series.setLast(this.nextCell);
		else
			oldNext.setPrevious(this.nextCell);
		
		// Reset this cell's parameters
		this.index = -1;
		this.series = null;
		this.previousCell = null;
		this.nextCell = null;
	}



	/**
	 * Swaps this cell with the next one in its series.
	 */
	public void swapWithNext() {
		Cell oldNext = this.nextCell;

		this.setNext(oldNext.getNext());
		oldNext.setPrevious(this.previousCell);
		this.setPrevious(oldNext);
		oldNext.setNext(this);

		this.index ++;
		oldNext.setIndex(oldNext.getIndex() - 1);
	}



	// Getters and setters

	/**
	 * Gets the previous cell in this series.
	 * @return A reference to the previous Cell object
	 */
	public Cell getPrevious() {
		return previousCell;
	}

	/**
	 * Changes which cell comes before this one in this series.
	 * @param c The Cell to make previous
	 */
	public void setPrevious(Cell c) {
		previousCell = c;
	}

	/**
	 * Gets the next cell in this series.
	 * @return A reference to the next Cell object
	 */
	public Cell getNext() {
		return nextCell;
	}

	/**
	 * Changes which cell comes after this one in this series.
	 * @param c The Cell to make next
	 */
	public void setNext(Cell c) {
		nextCell = c;
	}

	/**
	 * Gets the textual value of this cell.
	 * @return The String value
	 */
	public String getValue() {
		return value;
	}

	/**
	 * Sets the textual value of this cell to a new value.
	 * @param s The String to set as the new value
	 */
	public void setValue(String s) {
		value = s;
	}

	// textField has no getters or setters, as it is intended to be used
	// purely by its parent object.

	/**
	 * Gets the index of this cell in its series, where 0 represents the first
	 * cell in the series, and n-1 represents the nth.
	 * @return The integer index of this cell
	 */
	public int getIndex() {
		return index;
	}

	/**
	 * Sets the index of this cell to a new value.
	 * @param i The new index of this cell
	 */
	public void setIndex(int i) {
		index = i;
	}

	/**
	 * Gets the Series object this cell belongs to.
	 * @return The Series of this cell, or null if it does not belong to a
	 * series.
	 */
	public Series getSeries() {
		return series;
	}

	/**
	 * Changes which series this cell belongs to.
	 * @param r The Series to link this cell to
	 */
	public void setSeries(Series r) {
		series = r;
	}
}
