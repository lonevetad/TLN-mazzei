package translators.secondWay;

import common.NodeParsedSentence;

public class TestTransferItEng3 {

	public static void main(String[] args) { testSingle(); }

	public static void testSingle() {
		NodeParsedSentence root, converted;
		root = new NodeParsedSentence("coding");
		root.setPos("V");
		root.setDep("ROOT"); // D:
		root.setLemma("code");
		root.addFeatures("Tense", "Inf");

		System.out.println("first root");
		System.out.println(root);
		converted = BuilderTransferTranslatorItEng3_ByHand.TRANSFERER_RULE_BASED.transfer(root);

		System.out.println(converted);
	}
}