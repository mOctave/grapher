package ib.grapher;

import java.awt.Color;

import java.util.Arrays;
import java.util.HashMap;

/**
 * The main class of the grapher, in charge of managing other windows.
 */
public class Main {
	public static final Color WHITE = new Color(255, 255, 255);
	public static final Color LIGHT_YELLOW = new Color(255, 255, 200);
	public static final Color YELLOW = new Color(255, 255, 150);
	public static final Color GREY = new Color(200, 200, 200);
	public static final Color SILVER = new Color(220, 220, 220);
	public static final Color TRANSPARENT = new Color(0, 0, 0,0);

	/** The data table being modified. */
	private static DataTable dataTable;
	/** The table of plottable data being modified. */
	private static PlottableTable plottableTable;
	/** The graph being modified. */
	private static Graph graph;

	public static void main(String[] args) {
		System.out.println("Launching Grapher");

		FileDataManager.openFile("testfile");
		Byte[] x = {0,1,2,3,5,6,8};
		FileDataManager.writeByteList(Arrays.asList(x), 0);
		HashMap<Long, Byte> h = new HashMap<>();
		h.put((long) 4,(byte) 4);
		h.put((long) 7,(byte) 7);
		h.put((long) 9,(byte) 9);
		FileDataManager.insertBytes(h);

		dataTable = new DataTable();
		dataTable.setSize(400, 300);
		dataTable.setVisible(true);

		Series testSeries1 = new Series(new Cell());
		testSeries1.getLast().insertCellAfter(new Cell("Text"));
		testSeries1.getLast().insertCellAfter(new Cell("17.4"));
		dataTable.addSeries(testSeries1);
		Series testSeries2 = new Series(new Cell());
		testSeries2.getFirst().setValue("Second Column");
		testSeries2.getLast().insertCellAfter(new Cell("-19"));
		testSeries2.getLast().insertCellAfter(new Cell("Cucumber"));
		dataTable.addSeries(testSeries2);
		dataTable.update();
	}

	// Getters and setters

	/**
	 * Gets a reference to the main data table.
	 * @return The main data table
	 */
	public static DataTable getDataTable() {
		return dataTable;
	}

	/**
	 * Gets a reference to the table of plottable data.
	 * @return The plottable data table
	 */
	public static PlottableTable getPlottableTable() {
		return plottableTable;
	}

	/**
	 * Gets a reference to the graph object.
	 * @return The project's graph
	 */
	public static Graph getGraph() {
		return graph;
	}

	// dataTable, plottableTable, and graph have no setters, because they are
	// intended as composites of the Main class.
}
