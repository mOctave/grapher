package ib.grapher;

import java.util.Arrays;
import java.util.HashMap;

/**
 * The main class of the grapher, in charge of managing other windows.
 */
public class Main {
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
	}
}
