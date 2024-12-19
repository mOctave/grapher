package ib.grapher;


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;

/**
 * The table which stores all the textual data for a graph.
 */
public class DataTable extends JFrame {
	// MARK: Constructors
	/**
	 * A constructor which initializes the lists of this table, and also adds
	 * listeners and triggers for GUI functionality.
	 */
	public DataTable() {
		super();
		data = new ArrayList<>();
		activeCells = new ArrayList<>();
		searchMatches = new ArrayList<>();

		// GUI
		setTitle("Grapher");
		setLayout(new BorderLayout());

		tableLayout = new GridBagLayout();
		table = new JPanel(tableLayout);
		table.setOpaque(false);
		table.setVisible(true);
		tableLayeredPane = new JLayeredPane();
		tableLayeredPane.add(table, Integer.valueOf(0), 0);

		JScrollPane tableView = new JScrollPane(tableLayeredPane);

		headerLayout = new GridBagLayout();
		header = new JPanel(headerLayout);
		tableView.setColumnHeaderView(header);
		header.setVisible(true);

		rowNumbers = new JPanel();
		rowNumbers.setLayout(new GridBagLayout());
		tableView.setRowHeaderView(rowNumbers);
		rowNumbers.setVisible(true);

		// Overlay
		overlay = new JPanel();
		overlay.setOpaque(false);
		overlay.setVisible(true);
		overlay.setLayout(null);
		tableLayeredPane.add(overlay, Integer.valueOf(1), 0);

		insertLeft = new JButton(new ImageIcon(Main.BUTTON_ADD));
		insertLeft.setFont(Main.SMALL);
		insertLeft.setHorizontalAlignment(SwingConstants.CENTER);
		insertLeft.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				insertSeriesLeft();
			}
		});
		overlay.add(insertLeft);

		insertRight = new JButton(new ImageIcon(Main.BUTTON_ADD));
		insertRight.setFont(Main.SMALL);
		insertRight.setHorizontalAlignment(SwingConstants.CENTER);
		insertRight.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				insertSeriesRight();
			}
		});
		overlay.add(insertRight);

		insertUp = new JButton(new ImageIcon(Main.BUTTON_ADD));
		insertUp.setFont(Main.SMALL);
		insertUp.setHorizontalAlignment(SwingConstants.CENTER);
		insertUp.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				insertRowAbove();
			}
		});
		overlay.add(insertUp);

		insertDown = new JButton(new ImageIcon(Main.BUTTON_ADD));
		insertDown.setFont(Main.SMALL);
		insertDown.setHorizontalAlignment(SwingConstants.CENTER);
		insertDown.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				insertRowBelow();
			}
		});
		overlay.add(insertDown);

		panelSearch = new JPanel(new BorderLayout());
		tableView.setCorner(JScrollPane.UPPER_LEADING_CORNER, panelSearch);
		
		searchInit = new JButton(new ImageIcon(Main.BUTTON_SEARCH));
		searchInit.setFont(Main.SMALL);
		searchInit.setHorizontalAlignment(SwingConstants.CENTER);
		searchInit.setPreferredSize(new Dimension(getPreferredSize().width, 16));
		searchInit.setBackground(Main.LIGHT_BLUE);
		searchInit.setBorder(null);
		searchInit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				searchAndHighlight();
			}
		});
		searchInit.setEnabled(true);
		panelSearch.add(searchInit, BorderLayout.NORTH);
		
		searchNext = new JButton(new ImageIcon(Main.BUTTON_NEXT));
		searchNext.setFont(Main.SMALL);
		searchNext.setHorizontalAlignment(SwingConstants.CENTER);
		searchNext.setPreferredSize(new Dimension(getPreferredSize().width, 12));
		searchNext.setBackground(Main.LIGHT_BLUE);
		searchNext.setBorder(null);
		searchNext.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				scrollToNextSearchResult();
			}
		});
		searchNext.setEnabled(false);
		panelSearch.add(searchNext, BorderLayout.CENTER);
		
		searchCancel = new JButton(new ImageIcon(Main.BUTTON_END));
		searchCancel.setFont(Main.SMALL);
		searchCancel.setHorizontalAlignment(SwingConstants.CENTER);
		searchCancel.setPreferredSize(new Dimension(getPreferredSize().width, 12));
		searchCancel.setBackground(Main.LIGHT_BLUE);
		searchCancel.setBorder(null);
		searchCancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				endSearch();
			}
		});
		searchCancel.setEnabled(false);
		panelSearch.add(searchCancel, BorderLayout.SOUTH);


		add(tableView, BorderLayout.CENTER);

		fillerT = new JPanel();
		fillerT.setOpaque(false);
		table.add(fillerT);

		fillerH = new JPanel();
		fillerH.setOpaque(false);
		header.add(fillerH);

		rnFiller = new JPanel();
		rnFiller.setOpaque(false);
		rowNumbers.add(rnFiller);

		statView = new JTextArea(6, 80);
		statView.setEditable(false);
		statView.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 10));
		statView.setMargin(new Insets(0, 5, 0, 5));
		add(statView, BorderLayout.SOUTH);

		title = new JLabel("<html><i>Unsaved File</i></html>");
		title.setHorizontalAlignment(SwingConstants.CENTER);
		add(title, BorderLayout.NORTH);

		setPreferredSize(new Dimension(800, 600));
		setMinimumSize(new Dimension(400, 300));
		setJMenuBar(Main.getMenuBar());
	}

	// MARK: Constants
	/** A template for the stat view. */
	private static final String STAT_VIEW_TEMPLATE = """
	Series Statistics
	Minimum: %-21s Non-Empty Cells: %-13s
	Q1: %-26s Numeric Values: %-14s
	Median: %-22s Sum: %-25s
	Q3: %-26s Mean: %-24s
	Maximum: %-21s Variance: %-20s
	Range: %-23s Standard Deviation: %-10s
	""";

	// MARK: Properties
	/** All the base data for this project. */
	private List<Series> data;
	/** The current row of cells to manipulate. */
	private List<Cell> activeCells;
	/** The current selected cell. */
	private Cell selectedCell;

	/** A list of cells that match a search. */
	private List<Cell> searchMatches;
	/** The current index in cycling through search matches. */
	private int searchIndex;


	// GUI
	/** A label to display the title of the data table. */
	private JLabel title;

	/** The panel that displays along the top of the table. */
	private JPanel header;
	/** Layout information for the data table header. */
	private GridBagLayout headerLayout;
	/** Filler to keep the header aligned properly. */
	private JPanel fillerH;

	/** The panel the data table is graphically displayed on. */
	private JPanel table;
	/** Layout information for the data table interface. */
	private GridBagLayout tableLayout;
	/** Filler to keep the table aligned to the top left. */
	private JPanel fillerT;

	/** The row numbers which make up the sidebar. */
	private JPanel rowNumbers;
	/** Filler to keep the row numbers aligned properly. */
	private JPanel rnFiller;

	/** A text panel which displays statistics about the selected cell. */
	private JTextArea statView;

	/** A layered pane to enable an overlay. */
	private JLayeredPane tableLayeredPane;
	/** An overlay panel to hold cell insertion buttons. */
	private JPanel overlay;

	/** A button to insert a column to the left of the selected cell. */
	private JButton insertLeft;
	/** A button to insert a column to the right of the selected cell. */
	private JButton insertRight;
	/** A button to insert a row above the selected cell. */
	private JButton insertUp;
	/** A button to insert a row below the selected cell. */
	private JButton insertDown;

	/** A panel to show search command buttons. */
	private JPanel panelSearch;
	/** The standard search button. */
	private JButton searchInit;
	/** A button to highlight the next search match. */
	private JButton searchNext;
	/** A button to cancel the search. */
	private JButton searchCancel;



	// MARK: Update
	/**
	 * Updates the main data table. Called whenever the window is resized or
	 * scrolled, or when the data in it changes. Resets the
	 * {@link GridBagConstraints} on all elements, updates series statistics,
	 * and then calls {@link #invalidate()}, {@link #validate()}, and
	 * {@link #repaint()}.
	 */
	public void doUpdate() {
		System.out.println("Doing data table update...");

		GridBagConstraints constraints;
		for (Series r : data) {
			constraints = new GridBagConstraints();
			constraints.gridx = indexOf(r);
			constraints.gridy = 0;
			constraints.anchor = GridBagConstraints.FIRST_LINE_START;
			headerLayout.setConstraints(r.getHeader(), constraints);

			for (Cell c : r) {
				constraints = new GridBagConstraints();
				constraints.gridx = indexOf(c.getSeries());
				constraints.gridy = c.getIndex();
				constraints.anchor = GridBagConstraints.FIRST_LINE_START;
				tableLayout.setConstraints(c, constraints);

				c.doUpdate();
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

		if (selectedCell == null) {
			statView.setText("Select a cell to view series statistics.");
		} else {
			System.out.println("A cell has been selected!");
			Series currentSeries = selectedCell.getSeries();
			statView.setText(String.format(
				STAT_VIEW_TEMPLATE,
				currentSeries.getStatistic("Minimum"),
				currentSeries.getStatisticAsInt("Non-Empty Cells"),
				currentSeries.getStatistic("Q1"),
				currentSeries.getStatisticAsInt("Numeric Values"),
				currentSeries.getStatistic("Median"),
				currentSeries.getStatistic("Sum"),
				currentSeries.getStatistic("Q3"),
				currentSeries.getStatistic("Mean"),
				currentSeries.getStatistic("Maximum"),
				currentSeries.getStatistic("Variance"),
				currentSeries.getStatistic("Range"),
				currentSeries.getStatistic("Standard Deviation")
			));
		}

		table.setBounds(0, 0, 110 * data.size(), 30 * len);
		overlay.setBounds(0, 0, 110 * data.size(), 30 * len);
		tableLayeredPane.setPreferredSize(
			new Dimension(110 * data.size(), 30 * len));

		// Update button position
		if (selectedCell == null) {
			insertLeft.setVisible(false);
			insertRight.setVisible(false);
			insertUp.setVisible(false);
			insertDown.setVisible(false);
		} else {
			System.out.println("Adding insertion buttons");
			insertLeft.setBounds(
				indexOf(selectedCell.getSeries()) * 110,
				(selectedCell.getIndex() * 30) + 10,
				10,
				10
			);

			insertRight.setBounds(
				indexOf(selectedCell.getSeries()) * 110 + 100,
				(selectedCell.getIndex() * 30) + 10,
				10,
				10
			);

			insertUp.setBounds(
				indexOf(selectedCell.getSeries()) * 110 + 50,
				(selectedCell.getIndex() * 30),
				10,
				10
			);

			insertDown.setBounds(
				indexOf(selectedCell.getSeries()) * 110 + 50,
				(selectedCell.getIndex() * 30) + 20,
				10,
				10
			);

			insertLeft.setVisible(true);
			insertRight.setVisible(true);
			insertUp.setVisible(true);
			insertDown.setVisible(true);
		}

		System.out.println("IVR Cycle...");

		invalidate();
		validate();
		repaint();

		System.out.println("Done data table update.");
	}



	// MARK: Methods
	/**
	 * Clears both the logical and graphical parts of the data table.
	 * Every {@link Series}, {@link Cell}, and row number is removed.
	 * The {@link #selectedCell} is reset to null.
	 */
	public void clear() {
		selectedCell = null;
		data.clear();
		
		// Remove components
		for (Component comp : table.getComponents()) {
			if (comp instanceof Cell) {
				table.remove(comp);
			}
		}

		for (Component comp : header.getComponents()) {
			if (comp instanceof SeriesHeader) {
				header.remove(comp);
			}
		}

		doUpdate();
	}



	/**
	 * Sorts the table in ascending order by the selected column, then runs
	 * the update method.
	 */
	public void sortBySelectedColumn() {
		Series sortSeries = getSelectedCell().getSeries();
		int passLength = sortSeries.length() - 1;


		while (passLength > 0) {
			int comparisonCounter = 0;
			resetActiveCells();
			Cell currentCell = sortSeries.getFirst();

			while (comparisonCounter < passLength) {
				Cell nextCell = currentCell.getNext();
				if (nextCell == null) {
					passLength = comparisonCounter;
					break;
				} else {
					double currentNumeric = 0;
					double nextNumeric = 0;
					try {
						currentNumeric = currentCell.getNumeric();
					} catch (NumberFormatException e) {}
					try {
						nextNumeric = nextCell.getNumeric();
					} catch (NumberFormatException e) {}

					if (currentNumeric > nextNumeric) {
						for (Cell c : getActiveCells()) {
							c.swapWithNext();
						}
					} else {
						rollActiveCellsForward();
						currentCell = nextCell;
					}
					comparisonCounter++;
				}
			}
			passLength--;
		}
	}



	/** 
	 * Creates a popup menu which lets the user enter a search key, then
	 * searches the data table and highlights every cell whose value matches
	 * the key.
	 */
	public void searchAndHighlight() {
		String key = JOptionPane.showInputDialog("What would you like to search for?");

		if (key == "")
			return;

		searchMatches = new ArrayList<>();
		searchIndex = 0;
		for (Series series : getData()) {
			for (Cell match : series.search(key)) {
				match.paintSearched();
				searchMatches.add(match);
			}
		}

		// Update buttons
		searchInit.setEnabled(false);
		searchNext.setEnabled(true);
		searchCancel.setEnabled(true);
	}


	/**
	 * Does the minimum possible scrolling to show the next search result on the screen.
	 */
	public void scrollToNextSearchResult() {
		if (searchMatches.size() == 0)
			return;

		searchIndex = (searchIndex + 1) % searchMatches.size();
		Cell match = searchMatches.get(searchIndex);
		tableLayeredPane.scrollRectToVisible(match.getBounds());
		setSelectedCell(match);
	}


	/**
	 * Deselects all searched values.
	 */
	public void endSearch() {
		for (Cell match : searchMatches) {
			match.paintDeselected();
		}

		searchMatches = new ArrayList<>();

		// Readjusts the selected cell, in case it was one of the search results
		if (selectedCell != null)
			selectedCell.paintDeselected();
		
		selectedCell = null;

		// Update buttons
		searchInit.setEnabled(true);
		searchNext.setEnabled(false);
		searchCancel.setEnabled(false);
	}



	/**
	 * Gets a series from the data based on its name.
	 * @param s The name of the series to get
	 * @return A reference to the desired {@link Series} object, or null if no
	 * such series was found.
	 */
	public Series getSeriesByName(String s) {
		for (Series r : data) {
			if (r.getName().equals(s)) {
				System.out.printf("%s = %s%n",r.getName(), s);
				return r;
			}
		}

		System.out.printf("No match for series \"%s\".%n", s);
		return null;
	}



	/**
	 * Adds a series to the data table.
	 * @param series The {@link Series} object to add
	 */
	public void addSeries(Series series) {
		header.add(series.getHeader());
		data.add(series);
		resetActiveCells();
		for (SeriesSelector selector : Main.getSelectors()) {
			selector.refresh();
		}
		Main.updateAllComponents();
	}



	/**
	 * Inserts a series at a specific position in the data table.
	 * @param i The index to insert the series at
	 * @param series The {@link Series} object to insert
	 */
	public void insertSeries(int i, Series series) {
		header.add(series.getHeader());
		data.add(i, series);
		resetActiveCells();
		for (SeriesSelector selector : Main.getSelectors()) {
			selector.refresh();
		}
		Main.updateAllComponents();
	}



	/**
	 * Removes the specified series from the data table.
	 * @param series The {@link Series} object to remove
	 */
	public void removeSeries(Series series) {
		header.remove(series.getHeader());
		data.remove(series);
		resetActiveCells();
		for (SeriesSelector selector : Main.getSelectors()) {
			selector.refresh();
		}
		Main.updateAllComponents();
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
		rowNumber.setText(Integer.toString(rowNumbers.getComponentCount() + 1) + " ");
		rowNumber.setHorizontalAlignment(SwingConstants.RIGHT);
		FontMetrics metrics = rowNumber.getFontMetrics(rowNumber.getFont());
		rowNumber.setPreferredSize(new Dimension(
			(int) metrics.getStringBounds(rowNumber.getText(), getGraphics())
				.getWidth() + 5,
			30
		));
		rowNumber.setMinimumSize(rowNumber.getPreferredSize());
	
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


	// Active cell movement
	/**
	 * Resets the currently active row of cells,
	 * moving them to the first row of the data table.
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



	// Insertion
	/**
	 * Inserts a new {@link Series} to the left of the selected {@link Cell}.
	 */
	public void insertSeriesLeft() {
		insertSeries(
			indexOf(getSelectedCell().getSeries()),
			new Series(getData().get(0).length())
		);
		Main.updateAllComponents();
	}



	/**
	 * Inserts a new {@link Series} to the right of the selected {@link Cell}.
	 */
	public void insertSeriesRight() {
		insertSeries(
			indexOf(getSelectedCell().getSeries()) + 1,
			new Series(getData().get(0).length())
		);
		Main.updateAllComponents();
	}



	/**
	 * Inserts a new row of {@link Cell}s above the selected {@link Cell}.
	 */
	public void insertRowAbove() {
		matchActiveToSelected();
		for (Cell c : getActiveCells()) {
			c.insertCellBefore(new Cell());
		}
		Main.updateAllComponents();
	}



	/**
	 * Inserts a new row of {@link Cell}s below the selected {@link Cell}.
	 */
	public void insertRowBelow() {
		matchActiveToSelected();
		for (Cell c : getActiveCells()) {
			c.insertCellAfter(new Cell());
		}
		Main.updateAllComponents();
	}



	// MARK: Convenience
	/**
	 * Adds a cell graphically to the table
	 * @param cell The cell to add
	 */
	public void addCell(Cell cell) {
		table.add(cell);
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
	 * Changes the background colour of a specified row number label.
	 * @param i The index of the label to change (starting at 0)
	 * @param colour The {@link java.awt.Color} to change it to
	 */
	public void setRowNumberBackground(int i, Color colour) {
		rowNumbers.getComponent(i).setBackground(colour);
	}



	// MARK: Getters / Setters
	/**
	 * Getter: Gets all the base data for the project.
	 * @return {@link #data}
	 */
	public List<Series> getData() {
		return data;
	}

	/**
	 * Setter: Overwrites all the base data for the project.
	 * @param data The new value for {@link #data}
	 */
	public void setData(List<Series> data) {
		this.data = data;
	}



	/**
	 * Getter: Gets the currently active row of cells.
	 * @return {@link #activeCells}
	 */
	public List<Cell> getActiveCells() {
		return activeCells;
	}

	/**
	 * Setter: Overwrites the currently active cell list.
	 * @param activeCells The new value for {@link #activeCells}
	 */
	public void setActiveCells(List<Cell> activeCells) {
		this.activeCells = activeCells;
	}


	/**
	 * Getter: Gets the currently selected cell.
	 * @return {@link #selectedCell}
	 */
	public Cell getSelectedCell() {
		return selectedCell;
	}

	/**
	 * Setter: Changes which cell is marked as selected,
	 * then calls {@link Main#updateAllComponents()}.
	 * @param selectedCell The new {@link #selectedCell} for this table
	 */
	public void setSelectedCell(Cell selectedCell) {
		this.selectedCell = selectedCell;
		Main.updateAllComponents();
	}



	/**
	 * Getter: Gets the list of current search matches.
	 * @return {@link #searchMatches}
	 */
	public List<Cell> getSearchMatches() {
		return searchMatches;
	}



	/**
	 * Getter: Gets the currently focused position in the list of search
	 * results.
	 * @return {@link #searchIndex}
	 */
	public int getSearchIndex() {
		return searchIndex;
	}

	// No setter for searchMatches or searchIndex, as they are meant to be
	// calculated internally.


	/**
	 * Getter: Gets the title bar with the current project file on it.
	 * @return {@link #title}
	 */
	public JLabel getTitleBar() {
		return title;
	}
}
