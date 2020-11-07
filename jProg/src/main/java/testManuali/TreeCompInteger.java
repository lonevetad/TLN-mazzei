package testManuali;

import dataStructures.NodeComparable;
import dataStructures.TreeComparable;
import tools.Comparators;

public class TreeCompInteger extends TreeComparable<Integer> {
	private static final long serialVersionUID = 1L;

	public TreeCompInteger() { super(Comparators.INTEGER_COMPARATOR); }

	public static TreeCompInteger fromString(String s) {
		TreeCompInteger t;
		t = new TreeCompInteger();
		t.root = parseStringBuilder(t, s);
		return t;
	}

	protected static NodeComparable<Integer> parseStringBuilder(TreeComparable<Integer> t, String s) {
		char c;
		NodeComparable<Integer> root;
		c = s.charAt(0);
		root = null;
		if (c == '-' || Character.isDigit(c)) { root = parseStringBuilder(t, s, new int[] { 0 }); }
		return root;
	}

	protected static NodeComparable<Integer> parseStringBuilder(TreeComparable<Integer> t, String s, int[] startIndex) {
		char c;
		int ssize, i;
		NodeComparable<Integer> root, child;
		root = null;
		ssize = s.length();
		i = startIndex[0] - 1;
//		System.out.println("starting from : " + startIndex[0]);
		while (root == null && ++i < ssize) {
			c = s.charAt(i);
			if (c == '-' || Character.isDigit(c)) {
				int endIndex = i - 1;
				while (++endIndex < ssize && //
						(Character.isDigit(c = s.charAt(endIndex)) || c == '-')
				//
				)
					;
//				System.out.println("substringing [" + i + "; " + endIndex + "]");
				root = t.getNodeSupplier().apply(//
						Integer.parseInt(s.substring(i, endIndex)), t.getKeyComparator());
				if (endIndex >= ssize) { return root; }
				i = skipToFirstNonBlankChar(s, endIndex) - 1; // "i" will be "++"
			}
		}
//		System.out.println("root before " + root);
		while (++i < ssize) {
			c = s.charAt(i);
//			System.out.println("c: ." + c + ".");
			if (c == '{') {
				while ((i = skipToFirstNonBlankChar(s, ++i)) != ssize && s.charAt(i) != '}') {
					startIndex[0] = i;
					child = parseStringBuilder(t, s, startIndex);
//					System.out.println("child found: " + child.getKeyIdentifier() + " at index i: " + i + ", ending at "
//							+ startIndex[0]);
					root.addChildNC(child);
					i = startIndex[0] - 1;
				}
			} else if (c == '}' || //
			// check in case of recursion
					c == '-' || Character.isDigit(c)//
			) {
				startIndex[0] = i;
//				System.out.println("returning root:\n" + root);
				return root;
			} else if (Character.isWhitespace(s.charAt(i))) {
				i = skipToFirstNonBlankChar(s, i);
				if (i == ssize) // end of the string
					return root;
				else
					i--;
				// "-1" because "i" will be "++"
			}
			// else : simply skip the character
		}
//		System.out.println("root after " + root);
		return root;
	}

	protected static int skipToFirstNonBlankChar(String s, int startIndex) {
		int ss;
		ss = s.length();
		if (startIndex < ss && Character.isWhitespace(s.charAt(startIndex)))
			while (++startIndex < ss && Character.isWhitespace(s.charAt(startIndex)))
				;
		return startIndex < ss ? startIndex : ss;
	}
}