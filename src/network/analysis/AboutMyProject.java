package network.analysis;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

public class AboutMyProject {

	public static void main(String[] args) {
        Debug.out(linesOfCode());
	}
	public static int linesOfCode() {
		try {
			return linesOfCode(new File("src\\")); // network\\
		} catch (IOException e) { e.printStackTrace();
		}
		return -1;
	}
	
	public static int linesOfCode(File dir) throws IOException {
		int ret = 0;
		for (File f : dir.listFiles(new FileFilter(){

			@Override
			public boolean accept(File arg0) {
				return arg0.getName().endsWith(".java")||arg0.isDirectory();
			}}))
			if (f.isDirectory()) ret += linesOfCode(f);
			else {
				ret += FileUtils.readLines(f).size();
			}
		return ret;
	}
	
}
