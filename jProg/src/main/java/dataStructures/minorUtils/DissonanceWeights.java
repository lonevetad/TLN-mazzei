package dataStructures.minorUtils;

import java.io.Serializable;
import java.math.BigInteger;

import dataStructures.NodeComparable;

/** See {@link NodeComparable#computeDissonanceAsLong(NodeComparable)}. */
public class DissonanceWeights implements Serializable {
	private static final long serialVersionUID = 23263214070008L;

	public static final DissonanceWeights WEIGHTS_DEFAULT = new DissonanceWeights() {
		private static final long serialVersionUID = -5410794L;

		@Override
		public DissonanceWeights setWeightMissingNode(int weightMissingNode) { return this; }

		@Override
		public DissonanceWeights setWeightExceedingNode(int weightExceedingNode) { return this; }

		@Override
		public DissonanceWeights setWeightDepth(int weightDepth) { return this; }
	};

	//

	//

	public DissonanceWeights() {
		this.weightMissingNode = this.weightExceedingNode = this.weightDepth = 1;
		this.weightMissingNodeBigInt = this.weightExceedingNodeBigInt = this.weightDepthBigInt = BigInteger.ONE;
	}

	protected int weightMissingNode, weightExceedingNode, weightDepth;
	protected BigInteger weightMissingNodeBigInt, weightExceedingNodeBigInt, weightDepthBigInt;

	public int getWeightMissingNode() { return weightMissingNode; }

	public int getWeightExceedingNode() { return weightExceedingNode; }

	public int getWeightDepth() { return weightDepth; }

	public BigInteger getWeightMissingNodeBigInt() { return weightMissingNodeBigInt; }

	public BigInteger getWeightExceedingNodeBigInt() { return weightExceedingNodeBigInt; }

	public BigInteger getWeightDepthBigInt() { return weightDepthBigInt; }

	public DissonanceWeights setWeightMissingNode(int weightMissingNode) {
		if (weightMissingNode >= 0) {
			this.weightMissingNode = weightMissingNode;
			this.weightMissingNodeBigInt = BigInteger.valueOf(weightMissingNode);
		}
		return this;
	}

	public DissonanceWeights setWeightExceedingNode(int weightExceedingNode) {
		if (weightExceedingNode >= 0) {
			this.weightExceedingNode = weightExceedingNode;
			this.weightExceedingNodeBigInt = BigInteger.valueOf(weightExceedingNode);
		}
		return this;
	}

	public DissonanceWeights setWeightDepth(int weightDepth) {
		if (weightDepth >= 1) {
			this.weightDepth = weightDepth;
			this.weightDepthBigInt = BigInteger.valueOf(weightDepth);
		}
		return this;
	}
}