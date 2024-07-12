package ib.grapher;

import java.awt.Color;
import java.io.UnsupportedEncodingException;
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
	/** The menu bar for the application. */
	private static MenuBar menuBar;

	public static void main(String[] args) {
		System.out.println("Launching Grapher");
		menuBar = new MenuBar();

		FileDataManager.openFile(FileDataManager.chooseFile("", "All Files", true));
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

		saveAllData();
	}

	/**
	 * Saves all data to the project file.
	 */
	public static void saveAllData() {
		// General metadata
		Byte[] metadata = new Byte[933];
		System.arraycopy(
			stringToByteArray("GRAPH TITLE - 200 CHARS", 400),
			0, metadata, 0, 400
		);
		System.arraycopy(
			stringToByteArray("X AXIS TITLE - 100 CHARS", 200),
			0, metadata, 400, 200
		);
		System.arraycopy(
			stringToByteArray("Y AXIS TITLE - 100 CHARS", 200),
			0, metadata, 600, 200
		);
		System.arraycopy(
			stringToByteArray("X GRIDLINE SERIES - 32 CHARS", 64),
			0, metadata, 800, 64
		);
		System.arraycopy(
			stringToByteArray("Y GRIDLINE SERIES - 32 CHARS", 64),
			0, metadata, 864, 64
		);
		metadata[928] = (byte) (true ? 1 : 0);
		System.arraycopy(
			intToByteArray(7618354),
			0, metadata, 929, 4
		);
		FileDataManager.writeByteList(Arrays.asList(metadata), 0);
	}

	/**
	 * Converts a string to an array of bytes the desired size.
	 * @param s The string to convert.
	 * @param size The size of the byte list. Should be a multiple of 2.
	 * If the string is too long, extra characters are dropped. If the string is
	 * too short, it is padded with zeroes.
	 * @return The byte array.
	 */
	public static Byte[] stringToByteArray(String s, int size) {
		Byte[] ba = new Byte[size];
		byte[] byteArray;
		try {
			byteArray = s.getBytes("UTF-16LE");
		} catch (UnsupportedEncodingException e) {
			System.err.println("Unsuported encoding!");
			return null;
		}

		int i = 0;
		while (i < byteArray.length) {
			ba[i] = byteArray[i];
			i++;
		}
		while (i < size) {
			ba[i] = 0;
			i++;
		}

		return ba;
	}

	/**
	 * Converts an integer into an array of four bytes.
	 * @param i The integer to convert.
	 * @return The byte array.
	 */
	public static Byte[] intToByteArray(int i) {
		return new Byte[] {
			(byte) (i >>> 24),
			(byte) (i >>> 16),
			(byte) (i >>> 8),
			(byte) i
		};
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

	/**
	 * Gets a reference to the global menu bar object.
	 * @return The project's menu bar
	 */
	public static MenuBar getMenuBar() {
		return menuBar;
	}

	// dataTable, plottableTable, graph, and menuBar have no setters, because
	// they are intended as composites of the Main class.
}
