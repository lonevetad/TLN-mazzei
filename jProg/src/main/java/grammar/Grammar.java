package grammar;

import java.io.Serializable;
import java.util.Iterator;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import dataStructures.MapTreeAVL;

/**
 * A Grammar is defined as a set of rules. Since each rule can have different
 * RHS (one or more), then
 */
public class Grammar implements Serializable {
	private static final long serialVersionUID = -650161514501200L;

	public Grammar() {
		super();
		this.rules = newRulesMap();
	}

	protected final Map<RuleElement, GrammarRuleMultiRHS> rules;

	//

	protected Map<RuleElement, GrammarRuleMultiRHS> newRulesMap() {
		return // new TreeMap<>
		MapTreeAVL.newMap(MapTreeAVL.Optimizations.MinMaxIndexIteration, //
				RuleElement.COMPARATOR_RULE_ELEMENT);
	}

	//

	public Iterator<GrammarRuleMultiRHS> iterator() { return this.rules.values().iterator(); }

	public void forEach(Consumer<GrammarRuleMultiRHS> action) { this.rules.forEach((re, gr) -> action.accept(gr)); }

	/**
	 * Perform an action on each rule ("decomposed" just due to construction reason)
	 * formed by a LHS and a single RHS. <br>
	 * {@link GrammarRuleSimple} cannot be used due to the "final" fields.
	 */
	public void forEachSimpleRule(BiConsumer<RuleElement, RuleElement> simpleRuleAction) {
		SimpleRuleConsumerForForeach srcff;
		srcff = new SimpleRuleConsumerForForeach(simpleRuleAction);
		this.rules.forEach((lhs, grammMultiRhs) -> {
			srcff.lhs = lhs;
			grammMultiRhs.forEachRHSs(srcff);
		});
	}

	//

	public GrammarRuleMultiRHS getRule(RuleElement lhs) { return rules.get(lhs); }

	public boolean containsRule(RuleElement lhs) {
		return rules.containsKey(lhs); // getRule(lhs)!=null;
	}

	public void addRule(GrammarRule gr) {
		final GrammarRuleMultiRHS ruleMultiRHS;
		final RuleElement lhs;
		lhs = gr.getLhs();
		if (rules.containsKey(lhs)) {
			ruleMultiRHS = rules.get(lhs);
		} else {
			ruleMultiRHS = new GrammarRuleMultiRHS(lhs);
			rules.put(lhs, ruleMultiRHS);
		}

//		Object rhs;
//		rhs = gr.getRhs();
//		if (rhs instanceof RuleElement) {
//			ruleMultiRHS.addRhs((RuleElement) rhs);
//		} else if (rhs instanceof Collection<?>) {
//			Collection<?> someRhs;
//			someRhs = (Collection<?>) rhs;
//			someRhs.forEach(rrr -> ruleMultiRHS.addRhs((RuleElement) rrr));
//		} else if (rhs instanceof RuleElement[]) {
//			for (RuleElement r : ((RuleElement[]) rhs)) {
//				ruleMultiRHS.addRhs(r);
//			}
//		}
		gr.forEachRHSRulesPart(singleRhs -> ruleMultiRHS.addRhs(singleRhs));
	}

	public void removeRule(RuleElement lhs) { if (rules.containsKey(lhs)) { rules.remove(lhs); } }

	public void removeRule(GrammarRule gr) {
		final RuleElement lhs;
		final GrammarRuleMultiRHS ruleMultiRHS;
		lhs = gr.getLhs();
		if (rules.containsKey(lhs)) {
			ruleMultiRHS = rules.get(lhs);
			gr.forEachRHSRulesPart(singleRhs -> ruleMultiRHS.removeRhs(singleRhs));
			if (ruleMultiRHS.getRhs().isEmpty()) {
				rules.remove(lhs); // no more RHS on this rule -> remove it
			}
		}
	}

	//

	@Override
	public String toString() {
		StringBuilder sb;
		sb = new StringBuilder();
		sb.append("Grammar \n[rules=");
		rules.forEach((re, gr) -> { sb.append('\n').append(gr.toString()); });
		return sb.toString();
	}

	protected static class SimpleRuleConsumerForForeach implements Consumer<RuleElement> {
		protected RuleElement lhs;
		protected final BiConsumer<RuleElement, RuleElement> simpleRuleAction;

		protected SimpleRuleConsumerForForeach(BiConsumer<RuleElement, RuleElement> simpleRuleAction) {
			super();
			this.simpleRuleAction = simpleRuleAction;
		}

		@Override
		public void accept(RuleElement rhs) { this.simpleRuleAction.accept(lhs, rhs); }
	}
}