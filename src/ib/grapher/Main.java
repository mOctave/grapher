package ib.grapher;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * The main class of the grapher, in charge of managing other windows.
 */
public abstract class Main {
	// MARK: Constants
	// Colours to use for drawing graphical elements
	/** Colour: black */
	public static final Color BLACK = new Color(0, 0, 0);
	/** Colour: white */
	public static final Color WHITE = new Color(255, 255, 255);
	/** Colour: light yellow */
	public static final Color LIGHT_YELLOW = new Color(255, 255, 200);
	/** Colour: yellow */
	public static final Color YELLOW = new Color(255, 255, 150);
	/** Colour: grey */
	public static final Color GREY = new Color(200, 200, 200);
	/** Colour: silver */
	public static final Color SILVER = new Color(220, 220, 220);
	/** Colour: transparent */
	public static final Color TRANSPARENT = new Color(0, 0, 0,0);

	/** The monospaced font to use for small print. */
	public static final Font SMALL = new Font("Monospaced", Font.BOLD, 8);

	/** The charset used to encode this project's text when saving/loading. */
	public static final String CHARSET = "UTF-16LE";

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



	// MARK: Properties
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



	// MARK: >Main<
	public static void main(String[] args) {
		System.out.println("Launching Grapher");

		// Initialization
		menuBar = new MenuBar();
		dataTable = new DataTable();
		plottableTable = new PlottableTable();
		graph = new Graph();

		// GUI
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
	}



	// MARK: Update
	/**
	 * Updates the {@link #dataTable}, {@link #plottableTable}, and
	 * {@link #graph}.
	 */
	public static void updateAllComponents() {
		dataTable.doUpdate();
		plottableTable.doUpdate();
		graph.doUpdate();
	}



	// MARK: Methods
	/**
	 * Copies a series name.
	 */
	public static void seriesCopy(Series r, Byte[] destination, int pos) {
		try {
			System.arraycopy(
				stringToByteArray(r.getName(), 64),
				0, destination, pos, 64
			);
		} catch (NullPointerException e) {
			Byte[] empty = new Byte[64];
			for (int i = 0; i < 64; i++)
				empty[i] = 0;
			System.arraycopy(
				empty,
				0, destination, pos, 64
			);
		}
	}




	/**
	 * Saves all data to the project file.
	 */
	public static void saveAllData() {
		// General metadata
		Byte[] metadata = new Byte[937];
		System.arraycopy(
			stringToByteArray(graph.getGraphTitle(), 400),
			0, metadata, 0, 400
		);
		System.arraycopy(
			stringToByteArray(graph.getAxisTitleX(), 200),
			0, metadata, 400, 200
		);
		System.arraycopy(
			stringToByteArray(graph.getAxisTitleY(), 200),
			0, metadata, 600, 200
		);
		seriesCopy(graph.getGridlinesX(), metadata, 800);
		seriesCopy(graph.getGridlinesY(), metadata, 864);
		if (graph.getGraphType().equals(Graph.SCATTERPLOT)) {
			metadata[928] = 1;
		} else if (graph.getGraphType().equals(Graph.LINE)) {
			metadata[928] = 2;
		} else if (graph.getGraphType().equals(Graph.BAR)) {
			metadata[928] = 3;
		}
		System.arraycopy(
			FileDataManager.intToByteArray(plottableTable.getDataSets().size()),
			0, metadata, 929, 4
		);
		System.arraycopy(
			FileDataManager.intToByteArray(dataTable.getData().size()),
			0, metadata, 933, 4
		);
		FileDataManager.writeByteList(Arrays.asList(metadata), 0);


		// Plottable Data Sets
		for (PlottableData pd : plottableTable.getDataSets()) {
			pd.save();
		}


		// Series and Cells
		System.out.println(dataTable.getData().size());
		for (Series r : dataTable.getData()) {
			r.save();
			for (Cell c : r) {
				c.save();
			}
		}
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
			byteArray = s.getBytes(CHARSET);
		} catch (UnsupportedEncodingException e) {
			System.err.println("Unsuported encoding!");
			return null;
		}

		try {
			int i = 0;
			while (i < byteArray.length && i < size) {
				ba[i] = byteArray[i];
				i++;
			}
			while (i < ba.length) {
				ba[i] = 0;
				i++;
			}
		} catch (IndexOutOfBoundsException e) {
			System.err.printf("Out of bounds (\"%s\" -> %d)%n", s, size);
		}

		return ba;
	}



	// MARK: Getters / Setters
	/**
	 * Getter: Gets a reference to the main data table.
	 * @return {@link #dataTable}
	 */
	public static DataTable getDataTable() {
		return dataTable;
	}



	/**
	 * Getter: Gets to the main plottable data table.
	 * @return {@link #plottableTable}
	 */
	public static PlottableTable getPlottableTable() {
		return plottableTable;
	}



	/**
	 * Getter: Gets a reference to the project's graph object.
	 * @return {@link #graph}
	 */
	public static Graph getGraph() {
		return graph;
	}



	/**
	 * Getter: Gets a reference to the global menu bar object.
	 * @return {@link #menuBar}
	 */
	public static MenuBar getMenuBar() {
		return menuBar;
	}



	/**
	 * Getter: Gets a list of every series selector in the project.
	 * @return {@link #seriesSelectors}
	 */
	public static List<SeriesSelector> getSelectors() {
		return seriesSelectors;
	}



	// dataTable, plottableTable, graph, and menuBar have no setters, because
	// they are intended as composites of the Main class.
}
