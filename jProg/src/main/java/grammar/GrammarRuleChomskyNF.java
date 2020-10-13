package grammar;

import java.util.Iterator;

public class GrammarRuleChomskyNF extends GrammarRuleSimple {

	private static final long serialVersionUID = -2120470911112L;

	// checks

	public static RuleElementPairNonTerminal compoundToPairRHS(RuleElementCompound rhs) {
		if (!(rhs instanceof RuleElementPairNonTerminal)) {
			if (rhs.getElementsAmount() == 2) {
				Iterator<GrammarElement> iter;
				GrammarElement first, second;
				iter = rhs.iterator();
				first = iter.next();
				if (first.isTerminal() || (second = iter.next()).isTerminal()) {
					throw new IllegalArgumentException("ChomskyNF RHS must not contain terminal elements.");
				}
				rhs = new RuleElementPairNonTerminal(first, second);
			} else {
				throw new IllegalArgumentException("ChomskyNF RHS must contain exactly 2 elements");
			}
		}
		return (RuleElementPairNonTerminal) rhs;
	}

	protected static RuleElement checkLHS(RuleElement lhs) {
		if (!(lhs instanceof RuleElementSingle || lhs.getElementsAmount() != 1)) {
			throw new IllegalArgumentException(
					"ChomskyNF LHS cannot have non-one elements: " + lhs.getElementsAmount());
		}
		if (lhs.firstElement.isTerminal()) { throw new IllegalArgumentException("ChomskyNF LHS cannot be terminal."); }
		return lhs;
	}

	//

	public GrammarRuleChomskyNF(RuleElement lhs, RuleElementCompound rhs) {
		super(checkLHS(lhs), compoundToPairRHS(rhs));
	}

	@Override
	public RuleElementPairNonTerminal getRhs() { return (RuleElementPairNonTerminal) rhs; }

	@Override
	public String toString() { return lhs + " -> " + rhs; }

}