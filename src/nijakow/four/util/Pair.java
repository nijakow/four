package nijakow.four.util;

public class Pair<K, V> {
	private final K first;
	private final V second;
	
	public K getFirst() { return first; }
	public V getSecond() { return second; }
	
	public Pair(K first, V second) {
		this.first = first;
		this.second = second;
	}
}
