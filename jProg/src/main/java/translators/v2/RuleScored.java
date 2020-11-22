package translators.v2;

import java.util.Comparator;

public class RuleScored {
	public static final Comparator<RuleScored> RuleScored_COMPARATOR = (s1, s2) -> {
		if (s1 == s2)
			return 0;
		if (s1 == null)
			return -1;
		if (s2 == null)
			return 1;
		return Long.compare(s1.scoreDifference, s2.scoreDifference);
	};
	protected Long scoreDifference;
	protected TransferRule rule;

	public RuleScored(Long scoreDifference, TransferRule rule) {
		super();
		this.scoreDifference = scoreDifference;
		this.rule = rule;
	}

	public Long getScoreDifference() { return scoreDifference; }

	public TransferRule getRule() { return rule; }

	@Override
	public String toString() { return "RuleScored [scoreDifference=" + scoreDifference + ",\n\trule=" + rule + "]"; }
}