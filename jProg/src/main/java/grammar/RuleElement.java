package grammar;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Iterator;
import java.util.function.Consumer;
import java.util.function.Function;

import tools.Misc;

/**
 * General element for a grammar. Can be formed by a single
 * {@link GrammarElement} or more.
 */
public abstract class RuleElement implements Serializable, Iterable<GrammarElement> {
	private static final long serialVersionUID = 998540478984L;
	public static final Comparator<RuleElement> COMPARATOR_RULE_ELEMENT = (r1, r2) -> {
		if (r1 == r2)
			return 0;
		if (r1 == null)
			return -1;
		if (r2 == null)
			return 1;
		return Misc.STRING_COMPARATOR.compare(r1.toString(), r2.toString());
	};
	public static final Function<RuleElement, String> RULE_ELEMENT_TO_LITERAL = re -> re.toString();

	public RuleElement(GrammarElement firstElement) {
		super();
		this.firstElement = firstElement;
	}

	protected final GrammarElement firstElement;

	public int getElementsAmount() { return 1; }

	public GrammarElement getFirstElement() { return firstElement; }

	//

	@Override
	public void forEach(Consumer<? super GrammarElement> action) { action.accept(firstElement); }

	@Override
	public Iterator<GrammarElement> iterator() {
		return new Iterator<GrammarElement>() {
			boolean neverDone = true;

			@Override
			public boolean hasNext() { return neverDone; }

			@Override
			public GrammarElement next() {
				if (neverDone) {
					neverDone = false;
					return firstElement;
				}
				return null;
			}
		};
	}

	@Override
	public String toString() { return firstElement.literal; }

}