package tools.lineReader;

import java.util.Collection;
import java.util.Iterator;
import java.util.function.Consumer;

import tools.LineReader;

public class LineReaderCollection implements LineReader {

	public LineReaderCollection(Collection<String> coll) {
		super();
		this.coll = coll;
	}

	protected final Collection<String> coll;

	@Override
	public void forEach(Consumer<String> action) { coll.forEach(action); }

	@Override
	public Iterator<String> iterator() { return coll.iterator(); }
}