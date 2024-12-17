package ib.grapher;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

/**
 * A utility class which interfaces between an open file and a project.
 * This class holds entirely static methods, as the program is only
 * intended to handle one project at a time.
 */
public final class FileDataManager {
	// MARK: Constructor
	/** Sole constructor. */
	private FileDataManager() {};


	// MARK: Constants
	/** The number of bytes to allocate to metadata. */
	public static final int METADATA_LENGTH = 937;
	/** The number of bytes to allocate to each {@link PlottableData} object. */
	public static final int PLOTTABLE_LENGTH = 321;
	/** The number of bytes to allocate to each {@link Series} object. */
	public static final int SERIES_LENGTH = 64;
	/** The number of bytes to allocate to {@link Cell} object. */
	public static final int CELL_LENGTH = 128;

	/**
	 * Flag to mark an object as {@link PlottableData} for
	 * {@link #getOffset}.
	 */
	public static final int PLOTTABLE = 0;
	/**
	 * Flag to mark an object as a {@link Series} for
	 * {@link #getOffset}.
	 */
	public static final int SERIES = 1;
	/**
	 * Flag to mark an object as a {@link Cell} for
	 * {@link #getOffset}.
	 */
	public static final int CELL = 2;


	// MARK: Properties
	/** The currently open project file. */
	private static File currentFile;
	/** The currently open project file, as a RandomAccessFile. */
	private static RandomAccessFile currentProject;



	// MARK: Methods
	/**
	 * Calculates the appropriate offset (in bytes) for a data block with a
	 * specified index.
	 * @param dataType One of {@link #PLOTTABLE}, {@link #SERIES}, or {@link #CELL}
	 * @param index The index of the data block relative to other blocks of the
	 * same type.
	 * @return The offset, in bytes, of the data block
	 */
	public static int getOffset(int dataType, int index) {
		int offset = METADATA_LENGTH;
		if (dataType == PLOTTABLE)
			return offset + (PLOTTABLE_LENGTH * index);
		
		offset += PLOTTABLE_LENGTH * Main.getPlottableTable().getDataSets().size();

		if (dataType == SERIES)
			return offset + (SERIES_LENGTH * index);
		
		offset += SERIES_LENGTH * Main.getDataTable().getData().size();
		
		if (dataType == CELL)
			return offset + (CELL_LENGTH * index);
		
		System.err.println("Invalid data type.");
		return -1;
	}



	/**
	 * Displays a file selection menu, and opens the file chosen by it.
	 * @param ext The desired file extension.
	 * @param desc The description for the desired file extension.
	 * @param saveAs true if the file chooser should open a "save as" dialog,
	 * false if it should open an "open" dialog.
	 * @return The selected file.
	 */
	public static File chooseFile(String ext, String desc, boolean saveAs) {
		// Set up the file chooser
		JFileChooser fileChooser = new JFileChooser("./");
		if (saveAs)
			fileChooser.setSelectedFile(new File("Untitled"+ext));
		fileChooser.setFileFilter(new FileFilter() {
			public String getDescription() {
				return String.format("%s (%s)", desc, ext);
			}

			public boolean accept(File f) {
				if (f.isDirectory()) {
					return true;
				} else {
					return f.getName().toLowerCase().endsWith(ext);
				}
			}
		});
		if (currentProject != null) {
			fileChooser.setCurrentDirectory(
				currentFile.getAbsoluteFile().getParentFile());
		}

		// Get the file
		if (saveAs) {
			if (fileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION)
				return fileChooser.getSelectedFile();
			else
				return null;
		} else {
			if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION)
				return fileChooser.getSelectedFile();
			else
				return null;
		}
		
	}



	/**
	 * Opens the specified file as the project file.
	 * @param f The file to open.
	 */
	public static void openFile(File f) {
		try {
			// Opens in RWD mode because the file is supposed to autosave
			currentFile = f;
			currentProject = new RandomAccessFile(f, "rw");
		} catch (FileNotFoundException e) {
			System.err.printf("No such file as \"%s\".%n", f.getName());
			e.printStackTrace();
		}
	}



	/**
	 * Opens the specified CSV file, overwriting the current data table's
	 * project file with its contents. The first row of CSV data is interpreted
	 * as a header row.
	 * @param f The {@link File} to open.
	 */
	public static void importCSV(File f) {
		try {
			// Opens in RWD mode because the file is supposed to autosave
			Main.getDataTable().clear();
			Scanner s = new Scanner(f);

			int lnum = 0;

			while (s.hasNextLine()) {
				String line = s.nextLine();

				List<String> sublines = splitCSVLine(line);

				if (lnum == 0) {
					// Add series, and set header names
					for (String subline : sublines) {
						Series r = new Series(1);
						Main.getDataTable().addSeries(r);
						r.setName(subline.trim());
					}
				} else if (lnum == 1) {
					// Fill in single empty cell
					for (int i = 0; i < sublines.size(); i++) {
						Main.getDataTable().getSeries(i).getFirst()
							.setValue(sublines.get(i).trim());
					}
				} else {
					// Add a new cell
					for (int i = 0; i < sublines.size(); i++) {
						Main.getDataTable().getSeries(i).getLast()
							.insertCellAfter(new Cell(sublines.get(i).trim()));
					}
				}

				lnum++;
			}

			s.close();
			Main.updateAllComponents();
		} catch (FileNotFoundException e) {
			System.err.printf("No such file as \"%s\".%n", f.getName());
			e.printStackTrace();
		}
	}



	/**
	 * Splits a CSV line. This method usually splits on commas, but will work
	 * properly with quoted text. Escaped double quotes are properly added to
	 * items as single (double) quotes, while unescaped quotes can be used to
	 * avoid splitting the CSV on commas.
	 * @param line The line to split.
	 * @return A {@link List holding the trimmed elements of the line}.
	 */
	public static List<String> splitCSVLine(String line) {
		List<String> entries = new ArrayList<>();
		boolean escape = false;
		boolean quoted = false;
		String currentEntry = "";
		for (char c : line.toCharArray()) {
			if (escape) {
				escape = false;
				if (c == '"') {
					currentEntry += c;
					continue;
				} else {
					quoted = !quoted;
				}
			}

			if (c == ',' && !quoted) {
				entries.add(currentEntry);
				currentEntry = "";
				continue;
			}

			if (c == '"') {
				escape = true;
				continue;
			}

			currentEntry += c;
		}

		entries.add(currentEntry);

		return entries;
	}



	/**
	 * Loads all the data from an opened project, overwriting current data.
	 */
	public static void load() {
		DataTable dt = Main.getDataTable();
		PlottableTable pt = Main.getPlottableTable();
		Graph g = Main.getGraph();

		dt.clear();
		pt.clear();


		g.setGraphTitle(byteListToString(readByteList(0, 400)));
		g.setAxisTitleX(byteListToString(readByteList(400, 200)));
		g.setAxisTitleY(byteListToString(readByteList(600, 200)));

		byte mode = readByteList(928, 1).get(0);
		if (mode == 1)
			g.setGraphType(Graph.SCATTERPLOT);
		else if (mode == 2)
			g.setGraphType(Graph.LINE);
		else if (mode == 3)
			g.setGraphType(Graph.BAR);
		else
			System.err.println("Invalid graph type when loading.");

		int plottableSize = byteArrayToInt(readByteList(929, 4)
			.toArray(new Byte[4]));

		int columns = byteArrayToInt(readByteList(933, 4)
			.toArray(new Byte[4]));

		int offset = METADATA_LENGTH + PLOTTABLE_LENGTH * plottableSize;

		long len = 0;
		try {
			len = currentProject.length();
		} catch (IOException e) {
			e.printStackTrace();
		}

		for (int i = 0; i < columns; i++) {
			Series r = new Series(1);
			r.setName(byteListToString(readByteList(offset, 64)));
			dt.addSeries(r);

			offset += SERIES_LENGTH;
		}

		for (int i = 0; i < columns; i++) {
			dt.getSeries(i).getFirst().setValue(
				byteListToString(readByteList(offset, 128)));

			offset += CELL_LENGTH;
		}

		for (int i = 0; offset < len; i++) {
			dt.getSeries(i % columns).getLast().insertCellAfter(
				new Cell(byteListToString(readByteList(offset, 128))));

			offset += CELL_LENGTH;
		}

		// Plottable data and some graph data updated last because they require series.

		// Gridline series
		g.setGridlinesX(dt.getSeriesByName(
			byteListToString(readByteList(800, 64))));
		g.setGridlinesY(dt.getSeriesByName(
			byteListToString(readByteList(864, 64))));

		// Plottable data
		offset = METADATA_LENGTH;
		for (int i = 0; i < plottableSize; i++) {
			PlottableData plottable = new PlottableData();
			plottable.setName(byteListToString(readByteList(offset, 64)));
			plottable.setDataX(dt.getSeriesByName(
				byteListToString(readByteList(offset + 64, 64))));
			plottable.setDataY(dt.getSeriesByName(
				byteListToString(readByteList(offset + 128, 64))));
			plottable.setErrorBarsX(dt.getSeriesByName(
				byteListToString(readByteList(offset + 192, 64))));
			plottable.setErrorBarsY(dt.getSeriesByName(
				byteListToString(readByteList(offset + 256, 64))));

			byte options = readByteList(offset + 320, 1).get(0);
			if ((options & 1) > 0)
				plottable.setActive(true);
			if ((options & 2) > 0)
				plottable.setLinRegActive(true);
			if ((options & 4) > 0)
				plottable.setXAgainstY(true);
			
			pt.addPlottableData(plottable);
			plottable.getMenu().sync();
			
			offset += PLOTTABLE_LENGTH;
		}

		Main.getGraph().sync();

		Main.updateAllComponents();
	}



	/**
	 * Converts a list of bytes into a string, following the {@code UTF-16LE}
	 * charset.
	 * @param bytes The list of bytes to convert.
	 * @return The final converted string.
	 */
	public static String byteListToString(List<Byte> bytes) {
		if (bytes == null)
			return "";
		
		byte[] ba = new byte[bytes.size()];
		for (int i = 0; i < bytes.size(); i++) {
			ba[i] = bytes.get(i);
		}

		try {
			return new String(ba, Main.CHARSET);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;
		}
	}



	/**
	 * Gets a list of bytes from the specified location in the project file.
	 * @param pos A pointer to the first desired byte
	 * @param len The number of bytes to capture
	 * @return An ArrayList of bytes
	 */
	public static List<Byte> readByteList(long pos, int len) {
		try {
			List<Byte> bytes = new ArrayList<>();
			currentProject.seek(pos);
			for (int i = 0; i < len; i++) {
				int nextByte = currentProject.read();
				if (nextByte == -1) {
					System.err.println("End of file reached.");
					break;
				}
				bytes.add((byte) nextByte);
			}
			return bytes;
		} catch (IOException e) {
			System.err.println("An I/O error occured getting a byte list.");
			e.printStackTrace();
			return new ArrayList<>();
		}
	}



	/**
	 * Overwrites the bytes in a specified location of the project file
	 * with new content.
	 * @param byteList The new bytes
	 * @param pos A pointer to the first byte to overwrite
	 */
	public static void writeByteList(List<Byte> byteList, long pos) {
		System.out.println(byteList);
		try {
			currentProject.seek(pos);
			for (byte nextByte : byteList) {
				currentProject.writeByte(nextByte);
			}
		} catch (IOException e) {
			System.err.println("An I/O error occured writing a byte list.");
			e.printStackTrace();
		}

		// Should be marginally faster this way than with rwd mode.
		try {
			currentProject.getFD().sync();
		} catch (IOException e) {
			System.err.println("Sync failed when writing a byte list.");
		}
	}



	/**
	 * Inserts new bytes at specified locations in the project file.
	 * Because this is an intensive process, this method allows for many
	 * bytes to be inserted at once, even in multiple locations.
	 * @param bytes A map of positions to insert, and bytes to insert.
	 * Note that all positions are relative to the FINAL, not initial, file.
	 */
	public static void insertBytes(Map<Long, Byte> bytes) {
		try {
			long shift = bytes.keySet().size();
			long filePointer;
			currentProject.seek(currentProject.length());
			for (int i = 0; i < bytes.size(); i++)
				currentProject.write(0);

			byte selectedByte = 0;
			while (shift > 0) {
				// The pointer will be one byte ahead of where it should be
				currentProject.seek(currentProject.getFilePointer() - 1);
				filePointer = currentProject.getFilePointer();

				if (bytes.keySet().contains(filePointer)) {
					// Data should be inserted at this location, do so
					byte b = bytes.remove(filePointer);
					currentProject.write(b);
					System.out.println(b);
					shift --;
				} else {
					// There is no data to insert here, so keep shifting data
					currentProject.seek(filePointer - shift);
					selectedByte = currentProject.readByte();
					currentProject.seek(filePointer);
					currentProject.write(selectedByte);
				}
				
				// Iterate backwards through the file
				currentProject.seek(currentProject.getFilePointer() - 1);
			}
		} catch (IOException e) {
			System.err.println("An I/O error occured inserting bytes.");
			e.printStackTrace();
		}
	}



	// MARK: Convenience
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



	/**
	 * Converts a four-byte array into an integer.
	 * @param ba The byte array to convert.
	 * @return The integer value of the array.
	 */
	public static int byteArrayToInt(Byte[] ba) {
		return (
			((ba[0] & 0xFF) << 24)
			| ((ba[1] & 0xFF) << 16)
			| ((ba[2] & 0xFF) << 8)
			| (ba[3] & 0xFF)
		);
	}



	// MARK: Getters / Setters
	/**
	 * Getter: Gets the currently opened file.
	 * @return {@link #currentFile}
	 */
	public File getCurrentFile() {
		return currentFile;
	}

	// currentFile has no setter. It should be set with openFile().



	/**
	 * Getter: Gets the current project file as a RandomAccessFile.
	 * @return {@link #currentProject}
	 */
	public RandomAccessFile getCurrentProject() {
		return currentProject;
	}

	// currentProject has no setter. It should be set with openFile().
}
