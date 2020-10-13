package tools;

import java.io.PrintStream;
import java.io.Serializable;
import java.util.List;

public interface LoggerMessages extends Serializable {

	public static final String NEW_LINE = "\n";
	public static final LoggerMessages LOGGER_DEFAULT = (LoggerMessages & Serializable) ((t, n) -> {
		if (n)
			System.out.println(t);
		else
			System.out.print(t);
		return true;
	});

	public boolean log(String text, boolean newLineRequired);

	public default boolean log(String text) {
		return log(text, true);
	}

	public default boolean logNoNewLine(String text) {
		return log(text, false);
	}

	public default boolean logAndPrint(String text) {
		return logAndPrint(text, System.out);
	}

	public default boolean logAndPrintError(String text) {
		return logAndPrint(text, System.err);
	}

	public default boolean logAndPrint(String text, PrintStream ps) {
		if (ps != null) ps.println(text);
		return log(text);
	}

	public default boolean logException(Exception e) {
		e.printStackTrace();
		return logAndPrintError("ERROR: exception raised:\n\t" + e.getMessage());
	}

	public default void clearLog() {
		System.out.println("DEFAULT LOG CANNOT BE CLEARED");
	}

	public default List<String> getEntireLog() {
		return null;
	}

	public static void requireLogger(LoggerMessages log) {
		if (log == null) throw new RuntimeException("Logger's null when required.");
	}

	public static LoggerMessages loggerOrDefault(LoggerMessages log) {
		return log == null ? LOGGER_DEFAULT : log;
	}
}