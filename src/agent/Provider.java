package agent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

import util.Globals;

public class Provider extends Agent {
	private HashSet<Integer> myQ;
	private int myQSize;
	private ArrayList<Integer> myPartnerIdList;

	public Provider(int processingPower, int q_size) {
		super(processingPower);
		this.myQSize = q_size;
		this.myPartnerIdList = new ArrayList<Integer>();
	}

	/**
	 * Return the set Q.
	 * 
	 * @return the set Q
	 */
	public HashSet<Integer> getQ() {
		return new HashSet<Integer>(this.myQ);
	}

	/**
	 * Return the size of the set Q.
	 * 
	 * @return the size of the set Q.
	 */
	public int getQSize() {
		return this.myQSize;
	}

	/**
	 * Return true if the given innovator's P is a subset of this provider's Q.
	 * 
	 * @param innovator
	 *            an innovator object
	 * @return true if the given innovator's P is a subset of this provider's Q
	 */
	public boolean canPartnerWith(Innovator innovator) {
		return this.myQ.containsAll(innovator.getP());
	}

	/**
	 * Add a partner id into this provider's partner id list
	 * 
	 * @param partnerId
	 *            a partner agent id
	 */
	public void addPartnerId(int partnerId) {
		this.myPartnerIdList.add(partnerId);
	}

	/**
	 * First call Agent.reset(). ({@inheritDoc}) Then initialize the set of Q.
	 * 
	 */
	@Override
	public void reset() {
		super.reset();
		HashSet<Integer> allElements = new HashSet<Integer>();
		for (int i = 0; i < Globals.ldscp.getInfN(); i++) {
			allElements.add(i);
		}
		this.myQ = new HashSet<Integer>();
		for (int i = 0; i < this.myQSize; i++) {
			this.myQ.add(Globals.removeRandomElementFromSet(allElements));
		}
		this.myPartnerIdList.clear();
	}

	/**
	 * Provider starts a new type of searching process and does the first
	 * searching step by calling Provider.continueSearch(). Search type of
	 * Provider: Q.
	 * 
	 * @param type
	 *            a search type: Provider: Q
	 */
	@Override
	public void startNewSearch(SearchType type) {
		if (type != SearchType.Q) {
			System.out.println("ERROR : Invalid Provider SearchType : " + type);
			System.exit(1);
		}
		this.mySearchType = type;
		this.myVisitedLocIds = new HashSet<Long>();
		this.myVisitedLocIds.add(this.myLocId);
		this.myUnvisitedNeighourLocIds = Globals.ldscp.getNeighboursInclusive(
				this.myLocId, new HashSet<Integer>(this.myQ),
				this.myProcessingPower);
		this.myUnvisitedNeighourLocIds.remove(this.myLocId);
		if (this.hasUnvisitedNeighbour()) {
			this.continueSearch();
		}
	}

	/**
	 * Provider does one more searching step in the current type of searching
	 * process, and increases its time stamp by 1.
	 */
	@Override
	public void continueSearch() {
		// pick one candidate randomly
		long candidateNeighbour = Globals
				.removeRandomElementFromSet(this.myUnvisitedNeighourLocIds);
		// put the candidate in to visited set
		this.myVisitedLocIds.add(candidateNeighbour);
		// compare and pick the better one
		float newScore = Globals.ldscp.getScoreOfLocId(candidateNeighbour);
		if (newScore >= this.myScore) {
			this.myLocId = candidateNeighbour;
			this.myScore = newScore;
			this.myUnvisitedNeighourLocIds = Globals.ldscp
					.getNeighboursInclusive(this.myLocId, this.myQ,
							this.myProcessingPower);
			this.myUnvisitedNeighourLocIds.removeAll(this.myVisitedLocIds);
		}
		this.myTimestamp++;

	}

	@Override
	public String toString() {
		return this.myTimestamp + "\tPROVIDER\t" + this.myId + "\t"
				+ this.myProcessingPower + "\t" + this.mySearchType + "\t"
				+ this.myScore + "\t"
				+ Arrays.toString(this.myPartnerIdList.toArray());
	}
}
