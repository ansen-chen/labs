package utils;

import java.io.File;

public class FileUtils {

	public static boolean isParentDirectoryCreationRequired(File file) {
		File parent = file.getParentFile();
		if (parent != null && !parent.exists()) {
			return true;
		} else {
			return false;
		}
	}

	public static boolean createMissingParentDirectories(File file) {
		File parent = file.getParentFile();
		if (parent == null) {
			throw new IllegalStateException(file + " should not have a null parent");
		}
		if (parent.exists()) {
			throw new IllegalStateException(file + " should not have existing parent directory");
		}
		return parent.mkdirs();
	}

}
