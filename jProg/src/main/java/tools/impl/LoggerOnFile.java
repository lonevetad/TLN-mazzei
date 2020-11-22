package tools.impl;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import tools.LoggerMessages;

public class LoggerOnFile implements LoggerMessages {
	private static final long serialVersionUID = 5401484L;

	public LoggerOnFile() throws IOException { this(null); }

	public LoggerOnFile(String filename) throws IOException {
		if (filename == null)
			filename = "logAutoCreated.txt";
		bw = new BufferedWriter(new FileWriter(new File(filename)));
	}

	private transient final BufferedWriter bw;

	@Override
	public boolean log(String text, boolean newLineRequired) {

		try {
			bw.append(text);
			if (newLineRequired)
				bw.append('\n');
			bw.flush();
		} catch (IOException e1) {
			e1.printStackTrace();
			return false;
		}
		return true;
	}

	@Override
	public void finalize() {
		try {
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}