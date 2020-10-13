package testManuali;

import java.io.File;

import grammar.Grammar;
import tools.GrammarBuilder;
import tools.lineReader.LineReaderFromFile;

public class TestGrammarBuilder {

	public static void main(String[] args) {
		Grammar g;
		g = GrammarBuilder.buildGrammar(new LineReaderFromFile("." + File.separatorChar + "src" + File.separatorChar
				+ "main" + File.separatorChar + "grammarEng.txt")); // TODO SET PATH in modo corretto
		System.out.println(g);
	}
}