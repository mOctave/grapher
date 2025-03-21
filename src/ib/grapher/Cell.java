package ib.grapher;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Insets;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Arrays;
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
	// MARK: Constructors
	/**
	 * A basic constructor for a cell, which initializes the text field and
	 * handles graphical layout.
	 */
	public Cell() {
		previousCell = null;
		nextCell = null;
		value = "";
		index = -1;
		series = null;


		// GUI
		setBackground(Main.WHITE);
		FlowLayout layout = new FlowLayout();
		layout.setHgap(0);
		layout.setVgap(0);

		textField = new JTextField(8);
		textField.setMargin(new Insets(0,0,0,0));
		setBorder(new EtchedBorder(EtchedBorder.RAISED));
		textField.addFocusListener(new FocusAdapter() {
			public void focusGained(FocusEvent e) {
				Cell.this.paintSelected();
				Main.getDataTable().setSelectedCell(Cell.this);
			}

			public void focusLost(FocusEvent e) {
				if (Main.getDataTable().getSearchMatches().contains(Cell.this))
					Cell.this.paintSearched();
				else
					Cell.this.paintDeselected();

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
		add(textField);

		setLayout(layout);
		setMinimumSize(new Dimension(110, 30));
		Main.getDataTable().addCell(this);
	}



	/**
	 * A slightly more advanced constructor, which allows for the value of the
	 * cell to be set at initialization.
	 * @param value The desired value of the cell.
	 */
	public Cell(String value) {
		this();
		setValue(value);
		textField.setText(value);
	}



	// MARK: Properties
	/** The cell that comes before this one in its series. */
	private Cell previousCell;
	/** The cell that comes after this one in its series. */
	private Cell nextCell;
	/** The textual value of this cell. */
	private String value;
	/** The index of this cell in its series. */
	private int index;
	/** The series this cell belongs to. */
	private Series series;


	// GUI
	/** A graphical text field to allow for data entry. */
	private final JTextField textField;



	// MARK: Update
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



	// MARK: Methods
	/**
	 * Saves this cell to the output file, overwriting an existing
	 * entry for the cell. If this cell has not yet been added,
	 * {@link FileDataManager#encodeForInsertion(Cell)} should be used instead.
	 */
	public void save() {
		System.out.println("SAVE: Cell");
		int seriesIndex = Main.getDataTable().indexOf(series);
		int dataLength = Main.getDataTable().getData().size();

		int offset = FileDataManager.getOffset(
			FileDataManager.CELL,
			dataLength * index + seriesIndex
		);

		Byte[] ba = Main.stringToByteArray(value, 128);

		FileDataManager.writeByteList(Arrays.asList(ba), offset);
	}



	/**
	 * GUI: Selects this cell, painting it yellow and its headers grey.
	 */
	public void paintSelected() {
		Cell.this.setBackground(Main.YELLOW);
		Cell.this.textField.setBackground(Main.LIGHT_YELLOW);
		Cell.this.series.getHeader().setBackground(Main.GREY);
		Main.getDataTable().setRowNumberBackground(Cell.this.getIndex(), Main.GREY);
	}



	/**
	 * GUI: Deselects this cell, painting it grey and its headers silver.
	 */
	public void paintDeselected() {
		Cell.this.setBackground(Main.WHITE);
		Cell.this.textField.setBackground(Main.WHITE);
		Cell.this.series.getHeader().setBackground(Main.SILVER);
		Main.getDataTable().setRowNumberBackground(Cell.this.getIndex(), Main.SILVER);
	}



	/**
	 * GUI: Marks this cell as a search result, painting it blue and its
	 * headers silver.
	 */
	public void paintSearched() {
		Cell.this.setBackground(Main.BLUE);
		Cell.this.textField.setBackground(Main.LIGHT_BLUE);
		Cell.this.series.getHeader().setBackground(Main.SILVER);
		Main.getDataTable().setRowNumberBackground(Cell.this.getIndex(), Main.SILVER);
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

		FileDataManager.encodeForInsertion(insertedCell);
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
		} else {
			oldNext.setPrevious(insertedCell);
			insertedCell.setNext(oldNext);
		}

		Cell c = oldNext;
		while (c != null) {
			c.setIndex(c.getIndex() + 1);
			c = c.getNext();
		}

		FileDataManager.encodeForInsertion(insertedCell);
	}



	/**
	 * Removes this cell from its series.
	 */
	public void remove() {
		FileDataManager.markForDeletion(this);

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
		Cell oldPrevious = this.previousCell;
		Cell oldNext = this.nextCell;

		if (oldNext == null) {
			System.err.println("No cell after "+this.getValue());
			return;
		}

		Cell newNext = oldNext.getNext();

		oldNext.setPrevious(oldPrevious);
		if (oldPrevious == null)
			this.getSeries().setFirst(oldNext);
		else
			oldPrevious.setNext(oldNext);
	
		this.setPrevious(oldNext);
		oldNext.setNext(this);

		this.setNext(newNext);
		if (newNext == null)
			this.getSeries().setLast(this);
		else
			newNext.setPrevious(this);

		oldNext.setIndex(oldNext.getIndex() - 1);

		this.index ++;
	}



	// MARK: Getters / Setters
	/**
	 * Getter: Gets the previous cell in this cell's series.
	 * @return {@link #previousCell}
	 */
	public Cell getPrevious() {
		return previousCell;
	}

	/**
	 * Setter: Changes which cell comes before this one in this cell's series.
	 * @param previousCell The new value for {@link #previousCell}
	 */
	public void setPrevious(Cell previousCell) {
		this.previousCell = previousCell;
	}



	/**
	 * Getter: Gets the next cell in this cell's series.
	 * @return {@link #nextCell}
	 */
	public Cell getNext() {
		return nextCell;
	}

	/**
	 * Setter: Changes which cell comes after this one in this cell's series.
	 * @param nextCell The new value for {@link #nextCell}
	 */
	public void setNext(Cell nextCell) {
		this.nextCell = nextCell;
	}



	/**
	 * Getter: Gets the textual value of this cell.
	 * @return {@link #value}
	 */
	public String getValue() {
		return value;
	}

	/**
	 * Setter: Changes the textual value of this cell.
	 * Also updates this cell's {@link #textField} to match.
	 * @param value The new {@link #value} of this cell
	 */
	public void setValue(String value) {
		this.value = value;
		textField.setText(value);
		save();
	}



	// textField has no getters or setters, as it is intended to be used
	// purely by its parent object.



	/**
	 * Getter: Gets the index of this cell in its series,
	 * where 0 represents the first cell in the series,
	 * and {@code n-1} represents the nth.
	 * @return {@link #index}
	 */
	public int getIndex() {
		return index;
	}

	/**
	 * Setter: Changes the index of this cell.
	 * @param index The new {@link #index} for this cell
	 */
	public void setIndex(int index) {
		this.index = index;
	}



	/**
	 * Getter: Gets the series this cell belongs to, or null if it does
	 * not belong to a series.
	 * @return {@link #series}
	 */
	public Series getSeries() {
		return series;
	}

	/**
	 * Setter: Changes which series this cell belongs to.
	 * @param series The new {@link #series} for this cell
	 */
	public void setSeries(Series series) {
		this.series = series;
	}
}
