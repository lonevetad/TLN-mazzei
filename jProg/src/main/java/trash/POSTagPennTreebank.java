package trash;

public enum POSTagPennTreebank {
	CoordinatingConjunction("CC"), //
	CardinalNumber("CD"), //
	Determiner("DT"), //
	ExistentialThere("EX"), //
	ForeignWord("FW"), //
	PrepositionOrSubordinatingConjunction("IN"), //
	Adjective("JJ"), //
	AdjectiveComparative("JJR"), //
	AdjectiveSuperlative("JJS"), //
	ListItemMarker("LS"), //
	Modal("MD"), //
	NounSingularOrMass("NN"), //
	NounPlural("NNS"), //
	ProperNounSingular("NNP"), //
	ProperNounPlural("NNPS"), //
	Predeterminer("PDT"), //
	PossessiveEnding("POS"), //
	PersonalPronoun("PRP"), //
	PossessivePronoun("PRP$"), //
	Adverb("RB"), //
	AdverbComparative("RBR"), //
	AdverbSuperlative("RBS"), //
	Particle("RP"), //
	Symbol("SYM"), //
	To("TO"), //
	Interjection("UH"), //
	VerbBaseForm("VB"), //
	VerbPastTense("VBD"), //
	VerbGerundOrPresentParticiple("VBG"), //
	VerbPastParticiple("VBN"), //
	VerbNon3rdPersonSingularPresent("VBP"), //
	Verb3rdPersonSingularPresent("VBZ"), //
	Wh_Determiner("WDT"), //
	Wh_Pronoun("WP"), //
	PossessiveSh_Pronoun("WP$"), //
	Wh_Adverb("WRB") //
	;

	public final String pos;

	POSTagPennTreebank(String p) { this.pos = p; }
}