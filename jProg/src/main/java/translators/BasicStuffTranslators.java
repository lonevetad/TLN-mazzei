package translators;

import java.nio.file.Paths;

import simplenlg.framework.NLGFactory;
import simplenlg.lexicon.Lexicon;
import simplenlg.lexicon.XMLLexicon;
import simplenlg.realiser.english.Realiser;
import tools.Misc;

public class BasicStuffTranslators {
	public static final Lexicon LEXICON_IT = new XMLLexicon(Paths.get(Misc.RESOURCE_PATH + "LexiconIt.txt").toUri());
	public static final NLGFactory NLG_FACTORY_IT = new NLGFactory(LEXICON_IT);
	public static final Realiser REALISER_IT = new Realiser(LEXICON_IT);
}