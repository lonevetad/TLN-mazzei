package translators.secondWay;

import java.util.function.Consumer;

import common.NodeParsedSentence;

public abstract class ATransferTranslationRuleBased {

	public abstract void addRule(TransferRule rule);

	public abstract NodeParsedSentence transfer(NodeParsedSentence rootSubtree);

	protected abstract TransferRule getBestRuleFor(NodeParsedSentence subtreeToTransfer);

	public abstract void forEachRule(Consumer<TransferRule> c);
}