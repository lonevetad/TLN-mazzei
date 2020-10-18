package testManuali;

import java.util.LinkedList;
import java.util.List;

import grammar.Grammar;
import tools.GrammarBuilder;
import tools.LineReader;
import tools.Misc;
import tools.lineReader.LineReaderCollection;
import tools.lineReader.LineReaderFromFile;

public class TestGrammarBuilder {

	public static void main(String[] args) {
		Grammar g;
		g = GrammarBuilder.buildGrammar(fromFile());
		System.out.println(g);
	}

	static LineReader fromFile() { return fromFile(Misc.RESOURCE_PATH + "grammarEng.txt"); }

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