package agent;

import java.util.HashSet;

import util.Globals;

public abstract class Agent {
	public enum SearchType {
		M, P, Q, MandP, Magain
	}

	protected int myId;
	protected int myProcessingPower;
	protected long myLocId;
	protected float myScore;
	protected long myTimestamp;
	protected SearchType mySearchType;
	protected HashSet<Long> myVisitedLocIds;
	protected HashSet<Long> myUnvisitedNeighourLocIds;

	/**
	 * Create an agent object with the given processing power. There are two
	 * subclasses, Innovator and Provider.
	 * 
	 * @param processingPower
	 *            a non-negative integer, which indicates the maximum number of
	 *            changes to a configuration
	 */
	public Agent(int processingPower) {
		this.myId = -1;
		this.myProcessingPower = processingPower;
		this.myLocId = -1L;
		this.myScore = -1f;
		this.myTimestamp = 0;
		this.mySearchType = null;
		this.myVisitedLocIds = new HashSet<Long>();
		this.myUnvisitedNeighourLocIds = new HashSet<Long>();
	}

	/**
	 * Return the agent id, which is also the index in Globals.innovatorList or
	 * Globals.providerList.
	 * 
	 * @return the agent id, which is also the index in Globals.innovatorList or
	 *         Globals.providerList
	 */
	public int getId() {
		return this.myId;
	}

	/**
	 * Return the agent's processing power.
	 * 
	 * @return the agent's processing power
	 */
	public int getProcessingPower() {
		return this.myProcessingPower;
	}

	/**
	 * Return the agent's location id.
	 * 
	 * @return the agent's location id
	 */
	public long getLocId() {
		return this.myLocId;
	}

	/**
	 * Return the performance of agent's location id.
	 * 
	 * @return the performance of agent's location id
	 */
	public float getScore() {
		return this.myScore;
	}

	/**
	 * Return the agent's search type. Innovator: null, M, P, MandP, Magain.
	 * Provider: null, Q
	 * 
	 * @return the agent's search type
	 */
	public SearchType getSearchType() {
		return this.mySearchType;
	}

	/**
	 * Set agent id, which is the index in Globals.innovatorList or
	 * Globals.providerList
	 * 
	 * @param id
	 *            an non-negative integer, which is the index in
	 *            Globals.innovatorList or Globals.providerList
	 */
	public void setId(int id) {
		this.myId = id;
	}

	/**
	 * Set agent's location id to the given new location id and update agent's
	 * performance as well.
	 * 
	 * @param newLocId
	 *            a new location id
	 */
	public void updateLocIdAndScore(long newLocId) {
		this.myLocId = newLocId;
		this.myScore = Globals.ldscp.getScoreOfLocId(this.myLocId);
	}

	/**
	 * Initialize agent's location with a random position, update its
	 * corresponding performance, reset time stamp to zero, reset search type to
	 * null, reset partner agent information, reset searching information.
	 */
	public void reset() {
		this.myLocId = Globals.randomGen
				.nextLong(1 << Globals.ldscp.getInfN() + 1);
		this.myScore = Globals.ldscp.getScoreOfLocId(this.myLocId);
		this.myTimestamp = 0;
		this.mySearchType = null;
		this.myVisitedLocIds = new HashSet<Long>();
		this.myUnvisitedNeighourLocIds = new HashSet<Long>();
	}

	/**
	 * Return true if the set of unvisited neighour locations is not empty.
	 * 
	 * @return true if the set of unvisited neighour locations is not empty
	 */
	public boolean hasUnvisitedNeighbour() {
		return !this.myUnvisitedNeighourLocIds.isEmpty();
	}

	/**
	 * Agent starts a new type of searching process and does the first searching
	 * step by calling Agent.continueSearch(). Search type of Innovator: M, P,
	 * MandP, Magain; search type of Provider: Q.
	 * 
	 * @param type
	 *            a search type: Innovator: M, P, MandP, Magain; Provider: Q
	 */
	public abstract void startNewSearch(SearchType type);

	/**
	 * Agent does one more searching step in the current type of searching
	 * process, and increases its time stamp by 1.
	 */
	public abstract void continueSearch();

	/**
	 * Agent increases its time stamp by 1.
	 */
	public void waitAndDoNothing() {
		this.myTimestamp++;
	}

	@Override
	public abstract String toString();
}
