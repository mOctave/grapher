package ib.grapher;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.FlowLayout;
import java.awt.Insets;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

import javax.swing.JPanel;
import javax.swing.JTextField;
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
		previousCell = null;
		nextCell = null;
		value = "";
		textField = new JTextField(8);
		index = -1;
		series = null;

		setBackground(Main.WHITE);
		FlowLayout layout = new FlowLayout();
		layout.setHgap(0);
		layout.setVgap(0);

		textField.setMargin(new Insets(0,0,0,0));
		setBorder(new EtchedBorder(EtchedBorder.RAISED));
		add(textField);
		setLayout(layout);
		Main.getDataTable().addCell(this);

		textField.addFocusListener(new FocusAdapter() {
			public void focusGained(FocusEvent e) {
				Cell.this.setBackground(Main.YELLOW);
				Cell.this.textField.setBackground(Main.LIGHT_YELLOW);
				Cell.this.series.getHeader().setBackground(Main.GREY);
				Main.getDataTable().setSelectedCell(Cell.this);
				Main.getDataTable().setRowNumberBackground(Cell.this.getIndex(), Main.GREY);
			}

			public void focusLost(FocusEvent e) {
				Cell.this.setBackground(Main.WHITE);
				Cell.this.textField.setBackground(Main.WHITE);
				Cell.this.series.getHeader().setBackground(Main.SILVER);
				Main.getDataTable().setSelectedCell(Cell.this);
				Main.getDataTable().setRowNumberBackground(Cell.this.getIndex(), Main.SILVER);

				// Losing focus also does data entry
				Cell.this.setValue(Cell.this.textField.getText());
				Cell.this.getSeries().calculateStatistics();
			}
		});

		textField.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Cell.this.setValue(Cell.this.textField.getText());
				Cell.this.getSeries().calculateStatistics();
			}
		});
	}

	/**
	 * A slightly more advanced constructor, which allows for the value of the
	 * cell to be set at initialization.
	 */
	public Cell(String s) {
		this();
		setValue(s);
		textField.setText(s);
	}


	/** The cell that comes before this one in its series. */
	private Cell previousCell;
	/** The cell that comes after this one in its series. */
	private Cell nextCell;
	/** The textual value of this cell. */
	private String value;
	/** A graphical text field to allow for data entry. */
	private final JTextField textField;
	/** The index of this cell in its series. */
	private int index;
	/** The series this cell belongs to. */
	private Series series;



	/**
	 * Standard update loop, called whenever the {@link DataTable} updates.
	 * Calls {@link #invalidate()}, {@link #validate()}, and
	 * {@link #repaint()} to refresh the cell element.
	 */
	public void doUpdate() {
		invalidate();
		validate();
		repaint();
	}



	/**
	 * Gets the numeric value of this cell. If this cell does not contain
	 * numeric data, this method will throw a {@link java.lang.NumberFormatException}.
	 * @return The double numeric value of this cell
	 */
	public double getNumeric() throws NumberFormatException {
		try {
			return NumberFormat.getNumberInstance(Locale.getDefault())
				.parse(this.value).doubleValue();
		} catch (ParseException e) {
			throw new NumberFormatException("Invalid number format.");
		}
	}



	/**
	 * Inserts another cell before this one in its series.
	 * @param insertedCell The cell to insert
	 */
	public void insertCellBefore(Cell insertedCell) {
		System.out.println("Inserting cell before #" + this.index);
		insertedCell.setSeries(this.series);
		Cell oldPrevious = this.previousCell;

		if (oldPrevious == null) {
			this.series.setFirst(insertedCell);
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
		System.out.println("Inserting cell after #" + this.index);
		insertedCell.setSeries(this.series);
		Cell oldNext = this.nextCell;
		insertedCell.setPrevious(this);
		insertedCell.setIndex(this.index + 1);
		this.setNext(insertedCell);

		if (oldNext == null) {
			this.series.setLast(insertedCell);
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
	 * @return The {@link java.lang.String} value
	 */
	public String getValue() {
		return value;
	}

	/**
	 * Sets the textual value of this cell to a new value.
	 * @param s The {@link java.lang.String} to set as the new value
	 */
	public void setValue(String s) {
		value = s;
		textField.setText(s);
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
	 * Gets the series this cell belongs to.
	 * @return The {@link Series} of this cell, or null if it does not belong to a
	 * series.
	 */
	public Series getSeries() {
		return series;
	}

	/**
	 * Changes which series this cell belongs to.
	 * @param r The {@link Series} to link this cell to
	 */
	public void setSeries(Series r) {
		series = r;
	}
}
