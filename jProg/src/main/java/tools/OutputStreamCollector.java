package tools;

import java.io.IOException;
import java.io.OutputStream;

public class OutputStreamCollector extends OutputStream {
	StringBuilder sb;

	public OutputStreamCollector() { this(16); }

	OutputStreamCollector(int startSize) { sb = new StringBuilder(startSize); }

	@Override
	public void write(int b) throws IOException { sb.append((char) b); }

	@Override
	public String toString() { return sb.toString(); }
}