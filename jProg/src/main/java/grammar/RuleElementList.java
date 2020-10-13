package grammar;

import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;

public class RuleElementList extends RuleElementCompound {
	private static final long serialVersionUID = 1L;
	protected final List<GrammarElement> productionElements;

	public RuleElementList(List<GrammarElement> productionElements) {
		super(productionElements.get(0));
		this.productionElements = productionElements;
	}

	/** I trust You: do not modify the list */
	public List<GrammarElement> getProductionElements() {
		return productionElements;
	}

	@Override
	public void forEach(Consumer<? super GrammarElement> action) { productionElements.forEach(action); }

	@Override
	public Iterator<GrammarElement> iterator() { return this.productionElements.iterator(); }

	@Override
	public String toStringImpl() {
		StringBuilder sb;
		Iterator<GrammarElement> iter;
		sb = new StringBuilder(productionElements.size() << 1);
		iter = this.iterator();
		sb.append(iter.next().literal);
		while (iter.hasNext()) {
			sb.append(' ').append(iter.next().literal);
		}
		return sb.toString();
	}
}