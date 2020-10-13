package grammar;

import java.io.Serializable;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public abstract class GrammarRule implements Serializable {
	private static final long serialVersionUID = 89640311456L;

	public GrammarRule(RuleElement lhs) {
		super();
		this.lhs = lhs;
	}

	protected final RuleElement lhs;

	public RuleElement getLhs() { return lhs; }

	public abstract Object getRhs();

	public abstract void forEachRHSRulesPart(Consumer<RuleElement> simpleRuleConsumer);

	public void forEachSimpleRule(BiConsumer<RuleElement, RuleElement> simpleRuleConsumer) {
		forEachRHSRulesPart(rhs -> simpleRuleConsumer.accept(lhs, rhs));
	}

	@Override
	public String toString() { return lhs + " ->"; }
}