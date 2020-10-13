package grammar;

import java.util.Iterator;
import java.util.Set;
import java.util.function.Consumer;

import dataStructures.MapTreeAVL;
import tools.Misc;

/** Rule that associate to a single LHS a {@link Set} of RHS. */
public class GrammarRuleMultiRHS extends GrammarRule {
	private static final long serialVersionUID = -210478884845050L;

	public GrammarRuleMultiRHS(RuleElement lhs) {
		super(lhs);
		this.backMapRhs = MapTreeAVL.newMap(MapTreeAVL.Optimizations.MinMaxIndexIteration, Misc.STRING_COMPARATOR);
		this.rhsSet = this.backMapRhs.toSetValue(RuleElement.RULE_ELEMENT_TO_LITERAL);
	}

	protected final MapTreeAVL<String, RuleElement> backMapRhs;
	protected final Set<RuleElement> rhsSet;

	@Override
	public Set<RuleElement> getRhs() { return rhsSet; }

	//

	public boolean containsRhs(RuleElement rhs) { return backMapRhs.containsKey(rhs.toString()); }

	public boolean containsRhs(String rhs) { return backMapRhs.containsKey(rhs); }

	public void addRhs(RuleElement rhs) { backMapRhs.put(rhs.toString(), rhs); }

	public void removeRhs(RuleElement rhs) { backMapRhs.remove(rhs.toString()); }

	public void removeRhs(String rhs) { backMapRhs.remove(rhs); }

	public void forEachRHSs(Consumer<RuleElement> action) { forEachRHSRulesPart(action); } // it's just an alias ...

	@Override
	public void forEachRHSRulesPart(Consumer<RuleElement> simpleRuleConsumer) {
		backMapRhs.forEach((literalRhs, rhs) -> simpleRuleConsumer.accept(rhs));
	}

	public Iterator<RuleElement> iterator() { return backMapRhs.iteratorValue(); }

	@Override
	public String toString() {
		boolean[] b = { true };
		StringBuilder sb;
		sb = new StringBuilder();
		sb.append(super.toString());
		this.rhsSet.forEach(re -> {
			if (b[0]) {
				sb.append(' ');
				b[0] = false;
			} else
				sb.append(" | ");
			sb.append(re.toString());
		});
		return sb.toString();
	}

}