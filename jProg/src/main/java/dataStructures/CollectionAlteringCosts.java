package dataStructures;

public interface CollectionAlteringCosts<T> {

	public long insertCost(T element);

	public long deleteCost(T element);

	public long renameCost(T element, T newLabel);

	//

	public static <T> CollectionAlteringCosts<T> newDefaultCAC() {
		return new CollectionAlteringCosts<>() {
			@Override
			public long insertCost(T element) { return 1; }

			@Override
			public long deleteCost(T element) { return 1; }

			@Override
			public long renameCost(T element, T newLabel) { return 1; }
		};
	}

	/**
	 * Auxiliary class, to transform methods like
	 * {@link AlteringCosts#insertCost(Object)} and
	 * {@link AlteringCosts#deleteCost(Object)} into functional interfaces.
	 */
	public static interface ActionCost<K> {
		public long getCost(K element);
	}
}
