package tools.impl;

import tools.CloserGetter;

public class CloserGetters {
	public static final CloserGetter<Integer> INTEGER_CLOSER_GETTER = (n, n1, n2) -> CloserGetter.getCloserTo(n,
			DifferenceCalculators.INTEGER_DIFFERENCE_CALC, n1, n2);
	public static final CloserGetter<Double> DOUBLE_CLOSER_GETTER = (n, n1, n2) -> CloserGetter.getCloserTo(n,
			DifferenceCalculators.DOUBLE_DIFFERENCE_CALC, n1, n2);
	public static final CloserGetter<Long> LONG_CLOSER_GETTER = (n, n1, n2) -> CloserGetter.getCloserTo(n,
			DifferenceCalculators.LONG_DIFFERENCE_CALC, n1, n2);
	public static final CloserGetter<Short> SHORT_CLOSER_GETTER = (n, n1, n2) -> CloserGetter.getCloserTo(n,
			DifferenceCalculators.SHORT_DIFFERENCE_CALC, n1, n2);
	public static final CloserGetter<Byte> BYTE_CLOSER_GETTER = (n, n1, n2) -> CloserGetter.getCloserTo(n,
			DifferenceCalculators.BYTE_DIFFERENCE_CALC, n1, n2);
	public static final CloserGetter<Float> FLOAT_CLOSER_GETTER = (n, n1, n2) -> CloserGetter.getCloserTo(n,
			DifferenceCalculators.FLOAT_DIFFERENCE_CALC, n1, n2);
	public static final CloserGetter<String> STRING_CLOSER_GETTER = (n, n1, n2) -> CloserGetter.getCloserTo(n,
			DifferenceCalculators.STRING_DIFFERENCE_CALC, n1, n2);
}