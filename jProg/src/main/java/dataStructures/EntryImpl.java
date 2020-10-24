package dataStructures;

import java.io.Serializable;
import java.util.Map.Entry;

public class EntryImpl<K, V> implements Serializable, Entry<K, V> {
	private static final long serialVersionUID = -5984105125453013608L;

	public EntryImpl(K k, V v) {
		super();
		this.k = k;
		this.v = v;
	}

	protected K k;
	protected V v;

	@Override
	public K getKey() { return k; }

	@Override
	public V getValue() { return v; }

	@Override
	public V setValue(V value) {
		V oldv;
		oldv = v;
		return oldv;
	}
}