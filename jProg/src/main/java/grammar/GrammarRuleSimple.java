package grammar;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

/** A simple rule formed by a LHS and a single RHS. */
public class GrammarRuleSimple extends GrammarRule {
	private static final long serialVersionUID = 8964031145L;

	public GrammarRuleSimple(RuleElement lhs, RuleElement rhs) {
		super(lhs);
		this.rhs = rhs;
	}

	protected final RuleElement rhs;

	@Override
	public RuleElement getRhs() { return rhs; }

	@Override
	public void forEachRHSRulesPart(Consumer<RuleElement> simpleRuleConsumer) { simpleRuleConsumer.accept(rhs); }

	@Override
	public void forEachSimpleRule(BiConsumer<RuleElement, RuleElement> simpleRuleConsumer) {
		simpleRuleConsumer.accept(lhs, rhs);
	}

	@Override
	public String toString() { return lhs + " -> " + rhs; }
}