package ib.grapher;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * A utility class which interfaces between an open file and a project.
 * This class holds entirely static methods, as the program is only
 * intended to handle one project at a time.
 */
public class FileDataManager {
	/** The currently open project file. */
	private static RandomAccessFile currentProject;

	/**
	 * Opens the file at a specified location.
	 * @param fp The filepath of the file to open.
	 */
	public static void openFile(String fp) {
		File f = new File(fp);
		try {
			// Opens in RWD mode because the file is supposed to autosave
			currentProject = new RandomAccessFile(f, "rwd");
		} catch (FileNotFoundException e) {
			System.err.printf("No such file as \"%s\".%n", fp);
			e.printStackTrace();
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
	public static void insertBytes(HashMap<Long, Byte> bytes) {
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
