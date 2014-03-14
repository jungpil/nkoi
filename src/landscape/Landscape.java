package landscape;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

public class Landscape {
	private class LRUCache<K, V> extends LinkedHashMap<K, V> {

		private final int capacity;

		public LRUCache(int capacity) {
			super(capacity, 1.1f, true);
			this.capacity = capacity;
		}

		@Override
		protected boolean removeEldestEntry(Map.Entry eldest) {
			return size() > capacity;
		}
	}

	private InfluenceMatrix myInf;
	private FitnessContributionTable myFit;
	private LRUCache<Long, Float> myCache;
	private final int myCacheCapacity = 1 << 10;

	/**
	 * Internally create a LRU cache of the fitness values. The cache size is
	 * 2^10.
	 * 
	 * @param inf
	 *            an influence matrix object
	 */
	public Landscape(InfluenceMatrix inf) {
		// assign private fields
		this.myInf = inf;
		this.myFit = new FitnessContributionTable(this.myInf);
		this.myCache = new LRUCache<Long, Float>(this.myCacheCapacity);
	}

	/**
	 * Return N of the influence matrix.
	 * 
	 * @return N of the influence matrix
	 */
	public int getInfN() {
		return this.myInf.getN();
	}

	/**
	 * Return K of the influence matrix.
	 * 
	 * @return K of the influence matrix
	 */
	public int getInfK() {
		return this.myInf.getK();
	}

	/**
	 * Return the fitness value of the given location id. If the performance
	 * value was cached, return the cached value. Otherwise, compute and cache
	 * the performance value, then return.
	 * 
	 * @param locId
	 *            a non-negative long, which represents a configuration/location
	 * @return the fitness value of the given location id
	 */
	public float getScoreOfLocId(long locId) {
		if (this.myCache.containsKey(locId)) {
			return this.myCache.get(locId);
		}
		float value = this.computeScoceForLocId(locId);
		this.myCache.put(locId, value);
		return value;
	}

	/**
	 * Define the distance between two configurations/locations to be the number
	 * of different element values.
	 * 
	 * E.g., distance between configurations 1,0,0,0 and 1,0,0,1 is 1, distance
	 * between configurations 1,0,0,0 and 0,1,1,0 is 3.
	 * 
	 * Define the distance between two configurations/locations w.r.t. a set of
	 * element indices to be the number of different element values in and ONLY
	 * in the given set of element indices.
	 * 
	 * E.g., the set of all configurations whose distances to configuration
	 * 1,0,0,0 are 1 w.r.t. element indices {0,1,2} is {[0,0,0,0], [1,1,0,0],
	 * [1,0,1,0]}
	 * 
	 * E.g., the set of all configurations whose distances to configuration
	 * 1,0,0,0 are 2 w.r.t. element indices {0,1,2} is {[1,1,1,0], [0,1,0,0],
	 * [0,0,1,0]}
	 * 
	 * This method returns the set of all location ids whose distances to the
	 * given location id w.r.t. the given elements are smaller or equal to the
	 * given processing power.
	 * 
	 * @param locId
	 *            a non-negative long, which represents a configuration/location
	 * @param elements
	 *            a set of element indices, which indicates the changeable
	 *            elements in a configuration
	 * @param processingPower
	 *            a non-negative integer, which indicates the maximum number of
	 *            changes to a configuration
	 * @return the set of all location ids whose distances to the given location
	 *         id w.r.t the given elements are smaller or equal to the given
	 *         processing power
	 */
	public HashSet<Long> getNeighboursInclusive(long locId,
			HashSet<Integer> elements, int processingPower) {
		HashSet<Long> result = new HashSet<Long>();
		// base case
		if (processingPower == 0 || elements.isEmpty()) {
			result.add(locId);
			return result;
		}
		// recursion
		HashSet<Integer> reducedElements = new HashSet<Integer>(elements);
		Iterator<Integer> itr = reducedElements.iterator();
		long toggledLocId = this.toggleElementInLocId(locId, itr.next());
		itr.remove();

		result = this.getNeighboursInclusive(toggledLocId, reducedElements,
				processingPower - 1);
		result.addAll(this.getNeighboursInclusive(locId, reducedElements,
				processingPower));
		return result;
	}

	/**
	 * Compute and return the fitness value of the given location id.
	 * 
	 * @param locId
	 *            a non-negative long, which represents a configuration/location
	 * @return the fitness value of the given location id
	 */
	private float computeScoceForLocId(long locId) {
		// convert location id to configuration
		int location[] = this.locIdToLocation(locId);
		// compute fitness value based on the configuration using fitness
		// contribution table
		float result = 0.0f;
		for (int i = 0; i < this.myInf.getN(); i++) {
			// indices in fitness contribution table
			int index1 = i;
			int index2 = location[i];
			int index3 = 0;
			int dependence[] = this.myInf.getDependentElementsOf(i);
			for (int j = 0; j < this.myInf.getK(); j++) {
				index3 <<= 1;
				index3 += location[dependence[j]];
			}
			result += this.myFit.getValueOf(index1, index2, index3);
		}
		return result / this.myInf.getN();
	}

	/**
	 * Return an integer array that "looks" like the binary form of the given
	 * location id. E.g., when N = 4, location id = 13, then the array is
	 * [1,1,0,1]
	 * 
	 * @param locId
	 *            a non-negative long, which represents a configuration/location
	 * @return an integer array that "looks" like the binary form of the given
	 *         location id
	 */
	private int[] locIdToLocation(long locId) {
		int location[] = new int[this.myInf.getN()];
		for (int j = 0; j < this.myInf.getN(); j++) {
			location[j] = (int) ((locId >> (this.myInf.getN() - 1 - j)) % 2);
		}
		return location;
	}

	/**
	 * Return a location id whose binary form is one bit different from the
	 * given location id, and the position of the different bit is determined by
	 * the given element index.
	 * 
	 * E.g., when N = 4, location id = 13, element index = 1, then the binary
	 * form is [1,1,0,1]. The new binary form is [1,0,0,1] and the new location
	 * id is 9.
	 * 
	 * @param locId
	 *            a non-negative long, which represents a configuration/location
	 * @param elementIdx
	 *            a non-negative integer, which indicates the position of the
	 *            bit to be toggled
	 * @return a location id whose binary form is one bit different from the
	 *         given location id, and the position of the different bit is
	 *         determined by the given element index.
	 */
	private long toggleElementInLocId(long locId, int elementIdx) {
		int shiftAmount = this.myInf.getN() - 1 - elementIdx;
		return locId + (1 << shiftAmount)
				* ((locId >> shiftAmount) % 2 == 0 ? 1 : -1);
	}
}
