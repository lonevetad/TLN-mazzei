package tools.lineReader;

import java.util.Iterator;
import java.util.function.Consumer;
import java.util.stream.Stream;

import tools.LineReader;

public class LineReaderStream implements LineReader {

	public LineReaderStream(Stream<String> stream) { this.stream = stream; }

	protected Stream<String> stream;

	@Override
	public void forEach(Consumer<String> action) { stream.forEach(action); }

	@Override
	public Iterator<String> iterator() { return stream.iterator(); }
}