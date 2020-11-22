package translators.v2;

import java.util.function.Consumer;
import java.util.function.Function;

import common.NodeParsedSentence;
import common.SentenceParsed;
import simplenlg.phrasespec.SPhraseSpec;
import translators.ITranslator;

public abstract class ATransferTranslationRuleBased implements ITranslator {

	public abstract void addRule(TransferRule rule);

	public abstract NodeParsedSentence transfer(NodeParsedSentence rootSubtree);

	protected abstract TransferRule getBestRuleFor(NodeParsedSentence subtreeToTransfer);

	public abstract void forEachRule(Consumer<TransferRule> c);

	//

	@Override
	public SPhraseSpec translateItEng(SentenceParsed t, Function<String, String> wordTranslator) {
		var tr = this.transfer(t.getRoot());
		// TODO
		return null;
	}
}