package testManuali;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import grammar.Grammar;
import tools.GrammarBuilder;
import tools.LineReader;
import tools.lineReader.LineReaderCollection;
import tools.lineReader.LineReaderFromFile;

public class TestGrammarBuilder {

	public static void main(String[] args) {
		Grammar g;
		g = GrammarBuilder.buildGrammar(fromHand()); // TODO SET PATH in modo corretto
		System.out.println(g);
	}

	static LineReader fromFile() {
		return fromFile(
				"." + File.separatorChar + "src" + File.separatorChar + "main" + File.separatorChar + "grammarEng.txt");
	}

	static LineReader fromFile(String pathname) { return new LineReaderFromFile(pathname); }

	static LineReader fromHand() {
		List<String> l;
		l = new LinkedList<>();
		l.add("S -> NP VP");
		l.add("VP -> Verb NP | Verb NP PP | Verb PP");
		l.add("PP -> Preposition NP");
		l.add("NP -> Det Nominal | ProperNoun");
		l.add("Nominal -> Noun | NominalNoun");
		l.add("Det -> a | the");
		return new LineReaderCollection(l);
	}
}