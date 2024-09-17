package ib.grapher;


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

/**
 * The table which stores all the textual data for a graph.
 */
public class DataTable extends JFrame {
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

	/**
	 * A constructor which initializes the lists of this table, and also adds
	 * listeners and triggers for GUI functionality.
	 */
	public DataTable() {
		super();
		setTitle("Grapher");
		tableModel = new DataTableModel();

		// Initialize GUI
		setLayout(new BorderLayout());

		table = new JTable(new DataTableModel());
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

		JScrollPane tableView = new JScrollPane(table);

		headerLayout = new GridBagLayout();
		header = new JPanel(headerLayout);
		tableView.setColumnHeaderView(header);
		header.setVisible(true);

		add(tableView, BorderLayout.CENTER);

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

	/** A text panel which displays statistics about the selected cell. */
	private JTextArea statView;

	private JLabel title;

	private JTable table;
	private DataTableModel tableModel;
	private JPanel header;
	private GridBagLayout headerLayout;

	/**
	 * Updates the main data table. Called whenever the window is resized or
	 * scrolled, or when the data in it changes. Resets the
	 * {@link GridBagConstraints} on all elements, updates series statistics,
	 * and then calls {@link #invalidate()}, {@link #validate()}, and
	 * {@link #repaint()}.
	 */
	public void doUpdate() {
		int sc = table.getSelectedColumn();
		System.out.println("Selected Column: " + sc);

		TableColumnModel columnModel = table.getColumnModel();
		for (int i = 0; i < columnModel.getColumnCount(); i++) {
			TableColumn col = columnModel.getColumn(i);
			JTextField field = new JTextField();

			final int j = i;

			field.setText(tableModel.getColumnName(i));
			field.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					DataTable.this.getModel().getData().get(j)
						.setName(field.getText());
					field.setBackground(Color.RED);
				}
			});
			field.addFocusListener(new FocusAdapter() {
				@Override
				public void focusLost(FocusEvent e) {
					DataTable.this.getModel().getData().get(j)
						.setName(field.getText());
				}
			});
			col.setHeaderRenderer(new HeaderCell(field));
		}

		if (sc < 1) {
			statView.setText("Select a cell to view series statistics.");
		} else {
			System.out.println("A cell has been selected!");
			Series currentSeries = tableModel.getData().get(sc - 1);
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

		invalidate();
		validate();
		repaint();
	}

	// Getters

	/**
	 * Get the currently used data table model.
	 * @return The current {@link DataTableModel}.
	 */
	public DataTableModel getModel() {
		return tableModel;
	}
}

class ColumnNumber extends JLabel {
	/**
	 * A constructor that allows for the creation of a label with specific
	 * text.
	 * @param s The text to put on the label.
	 */
	public ColumnNumber(String s) {
		setText(s);
		setPreferredSize(new Dimension(20, 30));
	}

}

class HeaderCell implements TableCellRenderer {
	public HeaderCell(JTextField field) {
		this.field = field;
		this.field.setBorder(UIManager.getBorder("TableHeader.cellBorder"));
	}

	private JTable table;
	private MouseAdapter adapter;
	private JTextField field;
	
	private int column = -1;

	@Override
	public Component getTableCellRendererComponent(
		JTable table, Object value,
		boolean isSelected, boolean hasFocus,
		int row, int col
	) {
		if (table != null && this.table != table) {
			this.table = table;
			JTableHeader header = table.getTableHeader();
			if (header != null) {
				field.setForeground(header.getForeground());
				field.setBackground(header.getBackground());
				field.setFont(header.getFont());
				adapter = new MouseAdapter() {
					private JTableHeader h = header;
					private Component target;

					@Override
					public void mousePressed(MouseEvent e) {
						if (h.getResizingColumn() == null) {
							// Get point of touch
							Point p = e.getPoint();

							// Get selected column
							int c = h.getTable().columnAtPoint(p);
							if (c != column || c == -1)
								return;
							
							// Gets the associated model?
							int i = h.getColumnModel().getColumnIndexAtX(p.x);
							if (i == -1)
								return;
							
							field.setBounds(header.getHeaderRect(i));
							header.add(field);
							field.validate();
							
							// Set targeted text field
							Point convertedPoint = SwingUtilities.convertPoint(
								header, p, field);
							
							target = SwingUtilities.getDeepestComponentAt(
								field, convertedPoint.x, convertedPoint.y);
							
							// Repost event to text field
							repost(e);
						}
					}

					@Override
					public void mouseReleased(MouseEvent e) {
						repost(e);
						target = null;
						header.remove(field);
					}

					public void repost(MouseEvent e) {
						if (target == null) {
							System.out.println(
								"Error posting MouseEvent to header field.");
							return;
						}

						MouseEvent convertedEvent = SwingUtilities
							.convertMouseEvent(header, e, target);
						target.dispatchEvent(convertedEvent);
					}
				};
				field.addMouseListener(adapter);
			}
		}

		column = col;

		return field;
	}

}
