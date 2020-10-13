package tools.lineReader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.function.Consumer;

import tools.LineReader;

public class LineReaderFromFile implements LineReader {

	public LineReaderFromFile(String fileName) { this(new File(fileName)); }

	public LineReaderFromFile(File file) { this.file = file; }

	protected final File file;

	@Override
	public void forEach(Consumer<String> action) {
		try (BufferedReader br = new BufferedReader(new FileReader(this.file))) {
			br.lines().forEach(action);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public Iterator<String> iterator() {
		try (BufferedReader br = new BufferedReader(new FileReader(this.file))) {
			return br.lines().iterator();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}