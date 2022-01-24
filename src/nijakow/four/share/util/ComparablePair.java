package nijakow.four.share.util;

public class ComparablePair<K extends Comparable<K>, V> extends Pair<K, V> implements Comparable<ComparablePair<K, V>> {

	public ComparablePair(K first, V second) {
		super(first, second);
	}

	@Override
	public int compareTo(ComparablePair<K, V> o) {
		return getFirst().compareTo(o.getFirst());
	}
	
}
