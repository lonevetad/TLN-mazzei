package tools.impl;

import tools.DifferenceCalculator;

public class DifferenceCalculators {
	public static final DifferenceCalculator<Integer> INTEGER_DIFFERENCE_CALC = (n1, n2) -> n1 - n2;
	public static final DifferenceCalculator<Double> DOUBLE_DIFFERENCE_CALC = (n1, n2) -> Double.valueOf(n1 - n2)
			.longValue();
	public static final DifferenceCalculator<Long> LONG_DIFFERENCE_CALC = (n1, n2) -> n1 - n2;
	public static final DifferenceCalculator<Short> SHORT_DIFFERENCE_CALC = (n1, n2) -> n1 - n2;
	public static final DifferenceCalculator<Byte> BYTE_DIFFERENCE_CALC = (n1, n2) -> n1 - n2;
	public static final DifferenceCalculator<Float> FLOAT_DIFFERENCE_CALC = (n1, n2) -> Double.valueOf(n1 - n2)
			.longValue();
	public static final DifferenceCalculator<String> STRING_DIFFERENCE_CALC = (n1, n2) -> n1.compareTo(n2);;
}