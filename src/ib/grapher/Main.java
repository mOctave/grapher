package ib.grapher;

import java.awt.Color;
import java.awt.Font;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.SwingUtilities;

/**
 * The main class of the grapher, in charge of managing other windows
 * and storing constants.
 */
public final class Main {
	// MARK: Constructor
	/** Sole constructor. Intended to appease Javadoc, not to be used. */
	private Main() {}

	// MARK: Constants
	// Colours to use for drawing graphical elements
	/** Colour: black (#000000) */
	public static final Color BLACK = new Color(0, 0, 0);
	/** Colour: white (#FFFFFF) */
	public static final Color WHITE = new Color(255, 255, 255);
	/** Colour: light yellow (#FFFFC8) */
	public static final Color LIGHT_YELLOW = new Color(255, 255, 200);
	/** Colour: yellow (#FFFF96) */
	public static final Color YELLOW = new Color(255, 255, 150);
	/** Colour: light blue (#AAD2FF) */
	public static final Color LIGHT_BLUE = new Color(170, 210, 255);
	/** Colour: blue (#96BEFF) */
	public static final Color BLUE = new Color(150, 190, 255);
	/** Colour: grey (#C8C8C8) */
	public static final Color GREY = new Color(200, 200, 200);
	/** Colour: silver (#DCBEFF) */
	public static final Color SILVER = new Color(220, 220, 220);
	/** Colour: transparent (0 alpha) */
	public static final Color TRANSPARENT = new Color(0, 0, 0,0);

	/** The monospaced font to use for small print. */
	public static final Font SMALL = new Font("Monospaced", Font.BOLD, 8);

	/** The charset used to encode this project's text when saving/loading. */
	public static final String CHARSET = "UTF-16LE";

	// Images
	/** Image to use for the add row/column button. */
	public static final BufferedImage BUTTON_ADD = getImageAsset("/button_add.png");
	/** Image to use for the search button. */
	public static final BufferedImage BUTTON_SEARCH = getImageAsset("/button_search.png");
	/** Image to use for the next search match button. */
	public static final BufferedImage BUTTON_NEXT = getImageAsset("/button_next.png");
	/** Image to use for the close search button. */
	public static final BufferedImage BUTTON_END = getImageAsset("/button_end.png");
	/** Image to use for the series colour change button. */
	public static final BufferedImage BUTTON_PALETTE = getImageAsset("/button_palette.png");

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
	/**
	 * Entrypoint to the graphing program. Initializes windows and the menu bar.
	 * @param args Unused args provided by the user when the app is launched
	 * from the command line.
	 */
	public static void main(String[] args) {
		System.out.println("Launching Grapher");

		// Initialization
		menuBar = new MenuBar();
		dataTable = new DataTable();
		plottableTable = new PlottableTable();
		graph = new Graph();

		// GUI
		SwingUtilities.invokeLater(new Runnable() {
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
		saveMetadata();
	}



	// MARK: Methods
	/**
	 * Copies a series name into a byte array.
	 * @param r The series to copy the name of
	 * @param destination The byte array
	 * @param pos The position to copy the start of the series into
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



	// MARK: File Handling
	/**
	 * Saves all data to the project file.
	 */
	public static void saveAllData() {
		if (FileDataManager.getCurrentProject() == null)
			return;

		System.out.println("SAVE: Everything");
		saveMetadata();


		// Plottable Data Sets
		for (PlottableData pd : plottableTable.getDataSets()) {
			pd.save();
		}


		// Series and Cells
		for (Series r : dataTable.getData()) {
			r.save();
			for (Cell c : r) {
				c.save();
			}
		}
	}



	/**
	 * Saves all metadata to the project file. Typically called after every
	 * major change to the project, simply because it's easier than trying to
	 * figure out exactly when it needs to be called.
	 */
	public static void saveMetadata() {
		System.out.println("SAVE: Metadata");
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



	/**
	 * Debug method that prints the method that calls it and whether or not that
	 * method is running on the Event Dispatch Thread.
	 */
	static void whereAmI() {
		if (SwingUtilities.isEventDispatchThread()) {
			System.out.printf(
				"Currently executing %s as EDT%n",
				Thread.currentThread().getStackTrace()[2].getMethodName()
			);
		} else {
			System.out.printf(
				"Currently executing %s (not EDT)%n",
				Thread.currentThread().getStackTrace()[2].getMethodName()
			);
		}
	}



	/**
	 * Loads an image asset from a path (typically within the JAR).
	 * @param path The path to the image resource
	 * @return The image, or null if no image is found
	 */
	public static BufferedImage getImageAsset(String path) {
		try {
			InputStream is = Main.class.getResourceAsStream(path);
			return ImageIO.read(is);
		} catch (Exception e) {
			System.err.printf(
				"Could not load image at %s.%n",
				path
			);
			e.printStackTrace();
		}

		return null;
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
