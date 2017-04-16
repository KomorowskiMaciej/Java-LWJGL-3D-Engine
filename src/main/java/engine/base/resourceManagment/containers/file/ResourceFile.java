package engine.base.resourceManagment.containers.file;

import java.io.*;

public class ResourceFile {

	private static final String FILE_SEPARATOR = "/";

	private String path;
	private String name;

	public ResourceFile(String path) {
		this.path = path;
		String[] dirs = path.split(FILE_SEPARATOR);
		this.name = dirs[dirs.length - 1];
	}

	public ResourceFile(String... paths) {
		this.path = "";
		for (String part : paths) {
			this.path += (FILE_SEPARATOR + part);
		}
		String[] dirs = path.split(FILE_SEPARATOR);
		this.name = dirs[dirs.length - 1];
	}

	public ResourceFile(ResourceFile file, String subFile) {
		this.path = file.path + FILE_SEPARATOR + subFile;
		this.name = subFile;
	}

	public ResourceFile(ResourceFile file, String... subFiles) {
		this.path = file.path;
		for (String part : subFiles) {
			this.path += (FILE_SEPARATOR + part);
		}
		String[] dirs = path.split(FILE_SEPARATOR);
		this.name = dirs[dirs.length - 1];
	}

	public String getPath() {
		return path;
	}

	@Override
	public String toString() {
		return getPath();
	}

	public InputStream getInputStream() {
		//return Class.class.getResourceAsStream(path);
		try {
			return new FileInputStream(path);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

	public BufferedReader getReader() throws Exception {
		try {
			InputStreamReader isr = new InputStreamReader(getInputStream());
			BufferedReader reader = new BufferedReader(isr);
			return reader;
		} catch (Exception e) {
			System.err.println("Couldn't get reader for " + path);
			throw e;
		}
	}

	public String getName() {
		return name;
	}

}
