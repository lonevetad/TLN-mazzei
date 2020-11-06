package testManuali;

import java.util.LinkedList;
import java.util.List;

import dataStructures.SortedSetEnhanced;
import tools.SynonymSet;

public class TestSynonimComparator {

	public static void main_(String[] args) {
		SynonymSet s;
		s = new SynonymSet("a");

		System.out.println(
				// SynonymSetComparator.FIRST_DIFFERENT_FIRST
				SortedSetEnhanced.ComparatorFactoriesSSE.SUBSET_THEN_NON_SHARED_KEYS.compare(new SynonymSet(), //
						s));
		var i = s.iterator();
		System.out.println("s stuff");
		while (i.hasNext())
			System.out.println(i.next());

		System.out.println("fine");
	}

	public static void main(String[] args) {
		int c;
//		Comparator<SynonymSet>
		SortedSetEnhanced.ComparatorFactoriesSSE co;
//		List<Comparator<SynonymSet>> comps = Arrays.asList(//
//				SynonymSet.SYNONYM_COMPARATOR, //
//				SynonymSet.SYNONYM_COMPARATOR_SUBSET_RELATION, //
//				SynonymSet.SYNONYM_COMPARATOR_SUBSET_FIRST_SEQUENCE_THEN //
//		);
		SortedSetEnhanced.ComparatorFactoriesSSE[] comps = SortedSetEnhanced.ComparatorFactoriesSSE.values();
		List<Ex> ex;
		ex = getTestPairs();
		System.out.println("START");
		for (Ex e : ex) {
			System.out.println(
					"with s1: " + e.s1 + ",,,, s2: " + e.s2 + " -------> intersect: " + e.s1.intersectionWith(e.s2));
		}
		System.out.println("\n\n\n and then");
		for (int i = 0, len = comps.length; i < len; i++) {
			co = comps[i];
			System.out.println("comp # " + i + " - " + co.name());
			for (Ex e : ex) {
				if ((c = co.factoryDelegate.newComparator(SynonymSet.COMPARATOR_SINGLE_SYNONYM).compare(e.s1,
						e.s2)) != e.expexted[i]) {
					System.out.println("Non matching: got " + c + ", exp: " + e.expexted[i] + ", s1: " + e.s1
							+ ",,,, s2: " + e.s2 + "\n\n");
				} /*
					 * else { System.out.println("ok " + e.s1 + " <-> " + e.s2); }
					 */
			}
		}
		System.out.println("fine");
	}

	public static List<Ex> getTestPairs() {
		List<Ex> ex = new LinkedList<>();
		ex.add(new Ex(//
				new SynonymSet(), //
				new SynonymSet(), //
				0, 0, 0));
		ex.add(new Ex(//
				new SynonymSet("a"), //
				new SynonymSet("a"), //
				0, 0, 0));
		ex.add(new Ex(//
				new SynonymSet("a", "b"), //
				new SynonymSet("a", "b"), //
				0, 0, 0));
		//
		ex.add(new Ex(//
				new SynonymSet(), //
				new SynonymSet("a"), //
				-1, -1, -1));
		ex.add(new Ex(//
				new SynonymSet("a"), //
				new SynonymSet("a", "b"), //
				-1, -1, -1));
		ex.add(new Ex(//
				new SynonymSet("a", "b"), //
				new SynonymSet("a", "b", "c"), //
				-1, -1, -1));
		//
		ex.add(new Ex(//
				new SynonymSet("a"), //
				new SynonymSet(), //
				1, 1, 1));
		ex.add(new Ex(//
				new SynonymSet("a", "b"), //
				new SynonymSet("a"), //
				1, 1, 1));
		ex.add(new Ex(//
				new SynonymSet("a", "b", "c"), //
				new SynonymSet("a", "b"), //
				1, 1, 1));
		//
		//
		ex.add(new Ex(//
				new SynonymSet("a"), //
				new SynonymSet("b"), //
				-1, 0, -1));
		ex.add(new Ex(//
				new SynonymSet("a", "b"), //
				new SynonymSet("a", "c"), //
				-1, 0, -1));
		ex.add(new Ex(//
				new SynonymSet("a", "b", "c"), //
				new SynonymSet("a", "b", "d"), //
				-1, 0, -1));
		//
		ex.add(new Ex(//
				new SynonymSet("a", "c"), //
				new SynonymSet("b", "d"), //
				-1, 0, -1));
		ex.add(new Ex(//
				new SynonymSet("c", "b", "d"), //
				new SynonymSet("b", "c", "e"), //
				-1, 0, -1));
		ex.add(new Ex(//
				new SynonymSet("a", "b", "c"), //
				new SynonymSet("e", "b", "d"), //
				-1, 0, -1));
		//
//		ex.add(new Ex(//
//				new SynonymSet("a"), //
//				new SynonymSet("b"), //
//				1, 0));
//		ex.add(new Ex(//
//				new SynonymSet("a", "b"), //
//				new SynonymSet("a", "c"), //
//				1, 0));
//		ex.add(new Ex(//
//				new SynonymSet("a", "b", "c"), //
//				new SynonymSet("a", "b", "d"), //
//				1, 0));
//		//
		return ex;
	}

	static class Ex {
		final int[] expexted;
		final SynonymSet s1, s2;

		public Ex(SynonymSet s1, SynonymSet s2, int... expexted) {
			super();
			this.s1 = s1;
			this.s2 = s2;
			this.expexted = expexted;
			System.out.println();

		}
	}
}
