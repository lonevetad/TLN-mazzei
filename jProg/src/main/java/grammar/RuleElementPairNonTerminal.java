package grammar;

import java.util.Iterator;
import java.util.function.Consumer;

/** USed in Chomsky Normal Form. */
public class RuleElementPairNonTerminal extends RuleElementCompound {
	private static final long serialVersionUID = 4150134148465L;

	public RuleElementPairNonTerminal(GrammarElement firstElement, GrammarElement secondElement) {
		super(firstElement);
		if (firstElement.isTerminal() || secondElement.isTerminal()) {
			throw new IllegalArgumentException("Both elements must be non-terminal.");
		}
		this.secondElement = secondElement;
	}

	protected final GrammarElement secondElement;

	@Override
	public void forEach(Consumer<? super GrammarElement> action) {
		action.accept(firstElement);
		action.accept(secondElement);
	}

	@Override
	public Iterator<GrammarElement> iterator() {
		return new Iterator<GrammarElement>() {
			byte status = 0;

			@Override
			public boolean hasNext() { return status < 2; }

			@Override
			public GrammarElement next() {
				if (status < 2) { return (status++ == 0) ? firstElement : secondElement; }
				return null;
			}
		};
	}

	@Override
	public String toStringImpl() { return secondElement + " " + firstElement; }
}