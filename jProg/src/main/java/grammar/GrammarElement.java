package grammar;

import java.io.Serializable;

/** Single element of a grammar. It just wraps a literal. */
public class GrammarElement implements Serializable {
	private static final long serialVersionUID = -501252584020L;

	public GrammarElement(String literal) {
		super();
		String l = literal;
		if (literal == null || (l = literal.trim()).length() == 0)
			throw new IllegalArgumentException("The given literal cannot be null or empty: " + literal);
		this.literal = l;
	}

	protected final String literal;

	public String getLiteral() { return literal; }

	public boolean isTerminal() { return Character.isLowerCase(literal.charAt(0)); }

	@Override
	public String toString() { return literal; }
}