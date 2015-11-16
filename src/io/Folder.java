package io;

import java.io.File;


@SuppressWarnings("serial")
public class Folder extends File {

	public Folder(String pathname) {
		super(pathname);
		this.mkdirs();
	}

	public Folder(File f) {
		super(f.getAbsolutePath());
		this.mkdirs();
	}

	public static Folder newUniqueFolder(String path) {
		int i = 0;
		while (new File(path+i).exists()) {
			i++;
		}
		
		return new Folder(path+i);
	}
	
	public Folder getChildDir(String dir) {
		File f = new File(this.getAbsolutePath()+"\\"+dir);
		if (f.exists()) return new Folder(f);
		else return null;
	}


}
