package grammar;

public abstract class RuleElementCompound extends RuleElement {
	private static final long serialVersionUID = 210201650166500L;

	public RuleElementCompound(GrammarElement firstElement) { super(firstElement); }

	protected String cacheToString = null;

	@Override
	public String toString() { return (cacheToString == null) ? (cacheToString = toStringImpl()) : cacheToString; }

	protected abstract String toStringImpl();
}