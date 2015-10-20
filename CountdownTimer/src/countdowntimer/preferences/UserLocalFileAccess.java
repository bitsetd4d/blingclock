package countdowntimer.preferences;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.FileChannel;

import countdowntimer.FullScreenUtil;

public class UserLocalFileAccess {
	
	private static String dataDir;
	
	public static void copyFile(File sourceFile,String destFileName) throws IOException {
		setupDataDirectory();
		File destFile = new File(dataDir + File.separator + destFileName);
		copyFile(sourceFile, destFile);
	}
	
	public static void deleteFilesWithPrefix(String prefix) {
		setupDataDirectory();
		File dir = new File(dataDir);
		File[] files = dir.listFiles();
		for (File f : files) {
			if (f.getName().toLowerCase().startsWith(prefix)) {
				f.delete();
			}
		}
	}
	
	private static void copyFile(File sourceFile, File destFile) throws IOException {
		if (!destFile.exists()) {
			destFile.createNewFile();
		}

		FileChannel source = null;
		FileChannel destination = null;
		try {
			source = new FileInputStream(sourceFile).getChannel();
			destination = new FileOutputStream(destFile).getChannel();
			destination.transferFrom(source, 0, source.size());
		} finally {
			if (source != null) {
				source.close();
			}
			if (destination != null) {
				destination.close();
			}
		}
	}
	
	private static void setupDataDirectory() { 
		String home = System.getProperty("user.home");
		String mydir = home + File.separator + ".blingclock";
		File f = new File(mydir);
		dataDir = f.getPath();
		if (!f.exists()) {
			System.out.println("Making "+mydir);
			f.mkdir();
			hide(f);
			createExplainFile();
		}
	}

	private static void hide(File src) {
		if (FullScreenUtil.isWindows()) {
			try {
				Process p = Runtime.getRuntime().exec("attrib +h " + src.getPath());
				p.waitFor();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	
	private static void createExplainFile() {
		try {
			FileWriter writer = new FileWriter(dataDir + File.separator + "readme.txt");
			writer.append("This directory is to store files for BlingClock");
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static URL getUrlForFile(String fn) {
		setupDataDirectory();
		try {
			return new URL("file:"+dataDir+File.separator+fn);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}


}
