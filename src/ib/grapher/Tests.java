package ib.grapher;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// MARK: Test Runner
/**
 * A class that stores and runs tests for debugging.
 */
public final class Tests {
	public static final Test[] ALL_TESTS = new Test[] {
		new TestEmpty(),
		new TestFileManagement(),
	};

	public static void doTests() {
		for (Test test : ALL_TESTS) {
			if (test.run() == false) {
				System.err.println("Test Failed!");
			} else {
				System.err.println("Test Passed!");
			}
		}
	}
}



// MARK: Test Template
/**
 * A template class for tests to extend.
 */
abstract class Test {
	/** 
	 * Runs this test.
	 * @return {@code true} if the test passes, or {@code false} if it fails
	 */
	public abstract boolean run();
}



// MARK: Tests
/**
 * Test:
 * Used to check whether tests are being run properly. This test always passes.
 */
class TestEmpty extends Test {
	/**
	 * Runs this test.
	 * @return {@code true}
	 */
	@Override
	public boolean run() {
		System.out.println("TEST: Tests are Running");
		return true;
	}
}

/**
 * Test:
 * Used to check whether bytes are being written to and read from the project file properly.
 */
class TestFileManagement extends Test {
	/** The desired result of the test. */
	public static final byte[] END_RESULT = new byte[]{66, 68, 77, 70, 79, 80, 74, 76};
	// B D M F O P J L

	/** The bytes to write intially. */
	public static final Byte[] WRITTEN_BYTES = new Byte[]{65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76};
	/** The bytes to insert. */
	public static final byte[] INSERTED_BYTES = new byte[]{77, 78, 79, 80};
	/** Where to insert the bytes. */
	public static final long[] INSERTED_POSITIONS = new long[]{5l, 6l, 9l, 11l};
	/** The bytes to delete. */
	public static final Long[] DELETED_POSITIONS = new Long[]{0l, 2l, 4l, 6l, 8l, 10l, 12l, 14l};

	/**
	 * Runs this test:
	 * (1) Opens a new project file, 
	 * (2) Writes 12 bytes to the file,
	 * (3) Inserts 4 bytes to the file,
	 * (4) Deletes 8 bytes from the file (every second byte),
	 * (5) Loads the data from the file and compares it against an array
	 * @return {@code true} if all tests pass, or {@code false} if one or more fail
	 */
	@Override
	public boolean run() {
		System.out.println("TEST: File Management");
		FileDataManager.openFile(new File("testFileManagement.graph"));
		FileDataManager.writeByteList(Arrays.asList(WRITTEN_BYTES), 0);

		Map<Long, Byte> bytesToInsert = new HashMap<>();
		for (int i = 0; i < INSERTED_BYTES.length; i++) {
			bytesToInsert.put(INSERTED_POSITIONS[i], INSERTED_BYTES[i]);
		}
		FileDataManager.insertBytes(bytesToInsert);

		FileDataManager.deleteBytes(Arrays.asList(DELETED_POSITIONS));

		List<Byte> result = FileDataManager.readByteList(0, 8);
		for (int i = 0; i < result.size(); i++) {
			if ((byte) result.get(i) != END_RESULT[i])
				return false;
		}

		return true;
	}
}
