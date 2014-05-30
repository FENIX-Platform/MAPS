/*
 */
package org.fao.fenix.maps.export;

import java.io.*;
import java.util.*;

public class TempFilesHandler {

	protected File _dir;
	private int minutes;
	private Timer timer;

	private static final String PREFIX = "tEmP";
	
	/**
	 * Periodically starts a process that cleans up the temporary files
	 * every n minutes
	 *
	 */
	public TempFilesHandler(String path, int minutes) 
	{
		_dir = new File(path);
		if( ! _dir.isDirectory()) {
			throw new IllegalArgumentException("Invalid temp directory '" + path + "'");
		}

		timer = new Timer();
		timer.schedule(new RemindTask(),
				0,
				minutes * 60 * 1000);
	}

	public void end() {
		timer.cancel();
	}

	public File getDir() {
		return _dir;
	}

	/**
	 * Creates a temporary File
	 *
	 * @return   a temporary File
	 * @throws   If a file could not be created
	 */
	public File getFile() throws IOException {
		return getFile(".tmp");
	}

	public File getFile(String extension) throws IOException {
		if(!extension.startsWith(".")) {
			extension = "." + extension;
		}

		File tf = File.createTempFile(PREFIX, extension, getDir());
		tf.deleteOnExit();
		return tf;
	}

	// Delete all the files in the temp directory
	class RemindTask extends TimerTask {

		public void run() {
			for(File f : getDir().listFiles()) {
				Calendar last = Calendar.getInstance();
				last.add(Calendar.MINUTE, -minutes);
				// Only files whose name starts with "temp" are deleted
				if(f.getName().startsWith(PREFIX) && last.getTime().after(new Date(f.lastModified()))) {
					f.delete();
				}
			}
		}
	}
}

