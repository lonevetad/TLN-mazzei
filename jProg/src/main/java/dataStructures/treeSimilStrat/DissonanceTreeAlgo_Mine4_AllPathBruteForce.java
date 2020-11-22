package dataStructures.treeSimilStrat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

import dataStructures.NodeComparable;
import dataStructures.SortedSetEnhanced;
import tools.Comparators;
import tools.EditDistance;
import tools.EditDistance.EqualityChecker;
import tools.impl.EditDistanceLevenshtein;

/**
 * Suffers from the same problems of
 * {@link DissonanceTreeAlgo_Mine3_AllPathBruteForce}, but it could be a bit
 * faster
 */
public class DissonanceTreeAlgo_Mine4_AllPathBruteForce<T> extends DissonanceTreeAlgo_Mine3_AllPathBruteForce<T> {

	@Override
	public long computeDissonance(NodeAlteringCosts<T> nodeAlteringCost, NodeComparable<T> ff, NodeComparable<T> gg) {
		long[] cost = { -1 }, betterCost;
//		Comparator<NodeComparable<T>> nodeComparatorOnlyByKey;
		Comparator<List<T>> listComparator;
		EqualityChecker<T> equalityCheckerKey;
		SortedSetEnhanced<List<T>> fpaths, gpaths, sharedPath;
		EditDistance ed;
		ed = new EditDistanceLevenshtein();
		// nodeComparatorOnlyByKey = (n1, n2) -> {
//			return n1.getKeyComparator().compare(n1.getKeyIdentifier(), n2.getKeyIdentifier());
//		};
		listComparator = Comparators.newListComparator(ff.getKeyComparator());
		equalityCheckerKey = EqualityChecker.fromComparator(ff.getKeyComparator());

		fpaths = SortedSetEnhanced.newDefaultSet(listComparator);
		gpaths = SortedSetEnhanced.newDefaultSet(listComparator);
		ff.forEachPathKey(newCopierPathKeys(fpaths));
		gg.forEachPathKey(newCopierPathKeys(gpaths));
		// shared paths does not contribute to calculating the cost
		sharedPath = fpaths.intersectionWith(gpaths);

//		System.out.println("intersection got");
//		sharedPath.forEach(l -> System.out.println("p: " + Arrays.toString(l.toArray())));
//		System.out.println("print by tree shape");
//		MapTreeAVLLightweight.SortedSetWrapper ssw_sp;
//		ssw_sp = (MapTreeAVLLightweight.SortedSetWrapper) sharedPath;
//		System.out.println(ssw_sp.getBackTree());
//		System.out.println("foreaching");
//		sharedPath.forEach(l -> System.out.println(l));
//		System.out.println("\n\n");

		{ // Java's Compiler wants final variables inside lambdas ...
			final SortedSetEnhanced<List<T>> ffff, gggg, sp;
			ffff = fpaths;
			gggg = gpaths;
			sp = sharedPath;
//			int[] a = { 0 };
//			System.out.println("intersection removing ...");
			sharedPath.forEach(l -> {
//				Objects.requireNonNull(l, "WHAAAAAT:\n" + l + "\n\n\n" + sp.toString());
//				System.out.println(a[0]++ + " - " + Arrays.toString(l.toArray()));
				ffff.remove(l);
				gggg.remove(l);
			});
		}
		// recycle "fpaths" as "minor set", "gpaths" -> greatest, "sharedPath" -> "temp"
		if (fpaths.size() > gpaths.size()) {
			sharedPath = fpaths;
			fpaths = gpaths;
			gpaths = sharedPath;
		}
		// iterates over the longest to allocate less iterators
		betterCost = new long[] { 0 };
		{ // Java's Compiler wants final variables inside lambdas ...
			final SortedSetEnhanced<List<T>> ffff;
			ffff = fpaths;
			gpaths.forEach(l -> {
				long diff;
				Iterator<List<T>> fpi;
				betterCost[0] = -1;
				fpi = ffff.iterator();
				while (betterCost[0] != 0 && // should never be 0 but ...
				fpi.hasNext()) {
					diff = ed.editDistance(l, fpi.next(), equalityCheckerKey);
					if (betterCost[0] == -1 || diff < betterCost[0]) { betterCost[0] = diff; }
				}
				if (betterCost[0] < 0)
					betterCost[0] = 0;
				if (cost[0] < 0)
					cost[0] = betterCost[0];
				else
					cost[0] += betterCost[0];
			});
		}
		return cost[0] < 0 ? 0 : cost[0];
	}

	protected static <K> Consumer<List<K>> newCopierPathKeys(SortedSetEnhanced<List<K>> setInto) {
		return l -> setInto.add(copyPath(l));
	}

	//

	//

	//

	public long computeDissonance_OLD(NodeAlteringCosts<T> nodeAlteringCost, NodeComparable<T> ff,
			NodeComparable<T> gg) {
		long[] cost = { -1 }, betterCost;
		Comparator<NodeComparable<T>> nodeComparatorOnlyByKey;
		Comparator<List<NodeComparable<T>>> listComparator;
		EqualityChecker<NodeComparable<T>> equalityCheckerKey;
		SortedSetEnhanced<List<NodeComparable<T>>> fpaths, gpaths, sharedPath;
		EditDistance ed;
		ed = new EditDistanceLevenshtein();
		nodeComparatorOnlyByKey = (n1, n2) -> {
			return n1.getKeyComparator().compare(n1.getKeyIdentifier(), n2.getKeyIdentifier());
		};
		listComparator = Comparators.newListComparator(nodeComparatorOnlyByKey);
		equalityCheckerKey = EqualityChecker.fromComparator(nodeComparatorOnlyByKey);

		fpaths = SortedSetEnhanced.newDefaultSet(listComparator);
		gpaths = SortedSetEnhanced.newDefaultSet(listComparator);
		ff.forEachPathNode(newCopierPath(fpaths));
		gg.forEachPathNode(newCopierPath(gpaths));
		// shared paths does not contribute to calculating the cost
		sharedPath = fpaths.intersectionWith(gpaths);

//		System.out.println("intersection got");
//		sharedPath.forEach(l -> System.out.println("p: " + Arrays.toString(l.toArray())));
//		System.out.println("print by tree shape");
//		MapTreeAVLLightweight.SortedSetWrapper ssw_sp;
//		ssw_sp = (MapTreeAVLLightweight.SortedSetWrapper) sharedPath;
//		System.out.println(ssw_sp.getBackTree());
//		System.out.println("foreaching");
//		sharedPath.forEach(l -> System.out.println(l));
//		System.out.println("\n\n");

		{ // Java's Compiler wants final variables inside lambdas ...
			final SortedSetEnhanced<List<NodeComparable<T>>> ffff, gggg, sp;
			ffff = fpaths;
			gggg = gpaths;
			sp = sharedPath;
			int[] a = { 0 };
			System.out.println("intersection removing ...");
			sharedPath.forEach(l -> {
				Objects.requireNonNull(l, "WHAAAAAT:\n" + l + "\n\n\n" + sp.toString());
				System.out.println(a[0]++ + " - " + Arrays.toString(l.toArray()));
				ffff.remove(l);
				gggg.remove(l);
			});
		}
		System.out.println("intersection removed");
		// recycle "fpaths" as "minor set", "gpaths" -> greatest, "sharedPath" -> "temp"
		if (fpaths.size() > gpaths.size()) {
			sharedPath = fpaths;
			fpaths = gpaths;
			gpaths = sharedPath;
		}
		// iterates over the longest to allocate less iterators
		betterCost = new long[] { 0 };
		System.out.println("HERE WE GO");
		{ // Java's Compiler wants final variables inside lambdas ...
			final SortedSetEnhanced<List<NodeComparable<T>>> ffff;
			ffff = fpaths;
			gpaths.forEach(l -> {
				long diff;
				Iterator<List<NodeComparable<T>>> fpi;
				betterCost[0] = -1;
				fpi = ffff.iterator();
				while (betterCost[0] != 0 && // should never be 0 but ...
				fpi.hasNext()) {
					diff = ed.editDistance(l, fpi.next(), equalityCheckerKey);
					if (betterCost[0] == -1 || diff < betterCost[0]) { betterCost[0] = diff; }
				}
				if (betterCost[0] < 0)
					betterCost[0] = 0;
				if (cost[0] < 0)
					cost[0] = betterCost[0];
				else
					cost[0] += betterCost[0];
			});
		}
		return cost[0];
	}

	protected static <K> Consumer<List<NodeComparable<K>>> newCopierPath(
			SortedSetEnhanced<List<NodeComparable<K>>> setInto) {
		return l -> setInto.add(copyPath(l));
	}

	//

	//

	//

	protected static <K> List<K> copyPath(List<K> l) {
//		K[] a;
//		a = l.
		List<K> ll;
		ll = new ArrayList<>(l.size());
		l.forEach(ll::add);
		return ll;
//		return new ArrayList<>(l);
	}
}