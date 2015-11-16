package io;

import java.io.File;

public class UniqueFile {

	public static File newUniqueFile(String dir, String name, String ext) {
		int i = 0;
		while (new File(dir+name+i+ext).exists()) {
			i++;
		}
		File ret = new File(dir+name+i+ext);
		ret.getParentFile().mkdirs();
		return ret;
	}
	public static File newUniqueFile(File dir, String name, String ext) {
		int i = 0;
		while (new File(dir.getPath()+"\\"+name+i+ext).exists()) {
			i++;
		}
		File ret = new File(dir.getPath()+"\\"+name+i+ext);
		ret.getParentFile().mkdirs();
		return ret;
	}
}
