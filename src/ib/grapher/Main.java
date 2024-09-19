package ib.grapher;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The main class of the grapher, in charge of managing other windows.
 */
public class Main {
	public static final Color BLACK = new Color(0, 0, 0);
	public static final Color WHITE = new Color(255, 255, 255);
	public static final Color LIGHT_YELLOW = new Color(255, 255, 200);
	public static final Color YELLOW = new Color(255, 255, 150);
	public static final Color GREY = new Color(200, 200, 200);
	public static final Color SILVER = new Color(220, 220, 220);
	public static final Color TRANSPARENT = new Color(0, 0, 0,0);

	public static final Font SMALL = new Font("Monospaced", Font.BOLD, 8);

	/**
	 * These are the colours that will be selected by default for the graph.
	 * They are specifically designed to be high contrast, including for
	 * colour-blind people. I don't expect that to be an issue with my client,
	 * but since I have to choose arbitrary colours anyways, I might as well
	 * choose the most accessible ones possible. Plus, I like the palette.
	 */
	public static final Color[] WONG_COLORS = {
		new Color(230, 159, 0),
		new Color(86, 180, 233),
		new Color(0, 158, 115),
		new Color(240, 228, 66),
		new Color(0, 114, 178),
		new Color(213, 94, 0),
		new Color(204, 121, 167)
	};

	/** The data table being modified. */
	private static DataTable dataTable;
	/** The table of plottable data being modified. */
	private static PlottableTable plottableTable;
	/** The graph being modified. */
	private static Graph graph;
	/** The menu bar for the application. */
	private static MenuBar menuBar;
	/**
	 * A list of all the Series Selectors that need to be notified when a
	 * {@link Series} is added, deleted, or renamed.
	 */
	private static final List<SeriesSelector> seriesSelectors = new ArrayList<>();

	public static void main(String[] args) {
		System.out.println("Launching Grapher");
		menuBar = new MenuBar();

		FileDataManager.openFile(new File("./test.graph"));
		Byte[] x = {0,1,2,3,5,6,8};
		FileDataManager.writeByteList(Arrays.asList(x), 0);
		Map<Long, Byte> h = new HashMap<>();
		h.put((long) 4,(byte) 4);
		h.put((long) 7,(byte) 7);
		h.put((long) 9,(byte) 9);
		FileDataManager.insertBytes(h);

		dataTable = new DataTable();
		plottableTable = new PlottableTable();
		graph = new Graph();
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				dataTable.setSize(400, 300);
				dataTable.addSeries(new Series(1));
				dataTable.setVisible(true);
				dataTable.doUpdate();

				plottableTable.setSize(400, 300);
				plottableTable.setVisible(true);

				graph.setSize(400, 300);
				graph.setVisible(true);
			}
		});

		//saveAllData();
	}

	/**
	 * Updates the {@link #dataTable}, {@link #plottableTable}, and
	 * {@link #graph}.
	 */
	public static void updateAllComponents() {
		dataTable.doUpdate();
		plottableTable.doUpdate();
		graph.doUpdate();
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

	/**
	 * @return A list of every series selector in the project.
	 */
	public static List<SeriesSelector> getSelectors() {
		return seriesSelectors;
	}

	// dataTable, plottableTable, graph, and menuBar have no setters, because
	// they are intended as composites of the Main class.
}
