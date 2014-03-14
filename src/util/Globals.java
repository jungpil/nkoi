package util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

import landscape.Landscape;
import random.MersenneTwisterFast;
import agent.Innovator;
import agent.Provider;

public class Globals {
	private final static long myMagicSeed = 900111;
	private static int myCurrentSeed = -1;

	/**
	 * Global random generator object
	 */
	public static MersenneTwisterFast randomGen = null;

	/**
	 * To avoid the correlation among continuous seeds, this method uses a fixed
	 * seed to generate a sequence of random integers and takes the ith random
	 * integer to be the actual seed, where i is the given seed.
	 * 
	 * @param seed
	 *            an non-negative integer, which is actually the run number
	 */
	public static void setSeed(int seed) {
		myCurrentSeed = seed;
		randomGen = new MersenneTwisterFast(myMagicSeed);
		long newSeed = 0;
		for (int i = 0; i < seed; i++) {
			newSeed = randomGen.nextInt();
		}
		randomGen = new MersenneTwisterFast(newSeed);
	}

	/**
	 * Return the run number instead of the actual seed.
	 * 
	 * @return the run number instead of the actual seed
	 */
	public static int getSeed() {
		return myCurrentSeed;
	}

	/**
	 * Global landscape object
	 */
	public static Landscape ldscp = null;

	/**
	 * Global innovator list
	 */
	public static ArrayList<Innovator> innovatorList = null;

	/**
	 * Global provider list
	 */
	public static ArrayList<Provider> providerList = null;

	/**
	 * Return the best provider's id for the given innovator.
	 * 
	 * @param innovator
	 *            an innovator object
	 * @return the best provider's id for the given innovator
	 */
	public static int findBestProviderIdForInnovator(Innovator innovator) {
		float bestScore = -1f;
		int bestProviderId = -1;
		for (Provider provider : Globals.providerList) {
			if (innovator.canPartnerWith(provider)
					&& provider.getScore() > bestScore) {
				bestProviderId = provider.getId();
			}
		}
		return bestProviderId;
	}

	/**
	 * Global config reader
	 */
	public final static ConfigReader reader = new ConfigReader();

	/**
	 * Global output writer
	 */
	public final static OutputWriter writer = new OutputWriter();

	/**
	 * Randomly remove an element from the given set and return that removed
	 * element.
	 * 
	 * @param set
	 *            a set of objects
	 * @return the randomly removed element from the set
	 */
	public static <T> T removeRandomElementFromSet(HashSet<T> set) {
		T result = null;
		int candidateIndex = Globals.randomGen.nextInt(set.size());
		Iterator<T> itr = set.iterator();
		for (int i = 0; i <= candidateIndex; i++) {
			result = itr.next();
		}
		itr.remove();
		return result;
	}
}
