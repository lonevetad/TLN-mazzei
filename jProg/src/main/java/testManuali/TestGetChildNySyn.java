package testManuali;

import common.NodeParsedSentence;
import tools.SynonymSet;

public class TestGetChildNySyn {
	static final SynonymSet[] SYN = { //
			new SynonymSet("a"), //
			new SynonymSet("a", "b"), //
			new SynonymSet("a", "b", "c"), //
			new SynonymSet("b", "d"), //
			new SynonymSet("b", "d", "f"), //
			new SynonymSet("a", "c", "e"), //
			new SynonymSet("a", "c", "d"), //
			new SynonymSet("b", "d", "z", "f"), //
			new SynonymSet("a", "c", "d", "z"), //
			new SynonymSet("a", "c", "e", "f", "g"), //
			new SynonymSet("g"), //
			new SynonymSet("b", "f", "g"), //
	};
	static final SynonymSet[] TESTS;
	static {
		SynonymSet[] testAdditional = new SynonymSet[] { //
				new SynonymSet("NOW MORE STUFFS"), //
				new SynonymSet("c"), //
				new SynonymSet("d"), //
				new SynonymSet("b", "e"), //
				new SynonymSet("c", "e", "f"), //
				new SynonymSet("f", "g"), //
				new SynonymSet("e", "f", "g", "h"), //
				new SynonymSet("f", "g", "h"), //
		};
		TESTS = new SynonymSet[SYN.length + testAdditional.length];
		System.arraycopy(SYN, 0, TESTS, 0, SYN.length);
		System.arraycopy(testAdditional, 0, TESTS, SYN.length, testAdditional.length);
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		NodeParsedSentence n;
		n = new NodeParsedSentence(new SynonymSet());
		for (var sy : SYN) {
			n.addChildNC(new NodeParsedSentence(sy));
		}
		System.out.println(n);
		System.out.println("now querying");
		for (var st : TESTS) {
			System.out.println("\n querying with: " + st);
			System.out.println(n.getChildNCByKey(st));
		}
	}
}