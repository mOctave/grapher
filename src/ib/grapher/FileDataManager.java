package ib.grapher;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

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
public class FileDataManager {
	/** The currently open project file. */
	private static File currentFile;
	/** The currently open project file, as a RandomAccessFile. */
	private static RandomAccessFile currentProject;

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
			currentProject = new RandomAccessFile(f, "rwd");
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
			Main.getDataTable().getModel().getData().clear();
			Scanner s = new Scanner(f);

			int lnum = 0;
			DataTableModel dtm = Main.getDataTable().getModel();

			while (s.hasNextLine()) {
				String line = s.nextLine();

				List<String> sublines = splitCSVLine(line);


				if (lnum == 0) {
					// Add series, and set header names
					for (String subline : sublines) {
						Series r = new Series(1, dtm);
						Main.getDataTable().getModel().addSeries(r);
						r.setName(subline.trim());
					}
				} else if (lnum == 1) {
					// Fill in single empty cell
					for (int i = 0; i < sublines.size(); i++) {
						Main.getDataTable().getModel().getSeries(i).getFirst()
							.setValue(sublines.get(i).trim());
					}
				} else {
					// Add a new cell
					for (int i = 0; i < sublines.size(); i++) {
						dtm.getSeries(i).getLast()
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
		System.out.println("WRITING...");
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
}
