package agent;

import java.util.HashSet;

import util.Globals;

public class Innovator extends Agent {
	private HashSet<Integer> myM;
	private HashSet<Integer> myP;
	private int myMSize, myPSize;
	private int myAllianceProcessingPower;
	private int myPartnerId;
	private boolean myhasSetPartner;

	/**
	 * Create an innovator agent object with given M size and P size. The set M
	 * and P will be initialized only when Innovator.reset() is called.
	 * 
	 * @param processingPower
	 *            a non-negative integer, which indicates the maximum number of
	 *            changes to a configuration
	 * @param m_size
	 *            a non-negative integer, which indicates the size of the set M.
	 * @param p_size
	 *            a non-negative integer, which indicates the size of the set P.
	 */
	public Innovator(int processingPower, int m_size, int p_size) {
		super(processingPower);
		this.myMSize = m_size;
		this.myPSize = p_size;
		this.myAllianceProcessingPower = -1;
		this.myPartnerId = -1;
		this.myhasSetPartner = false;
	}

	/**
	 * Return a copy of the set M.
	 * 
	 * @return a copy of the set M
	 */
	public HashSet<Integer> getM() {
		return new HashSet<Integer>(this.myM);
	}

	/**
	 * Return a copy of the set P.
	 * 
	 * @return a copy of the set P
	 */
	public HashSet<Integer> getP() {
		return new HashSet<Integer>(this.myP);
	}

	/**
	 * Return the size of the set M.
	 * 
	 * @return the size of the set M
	 */
	public int getMSize() {
		return this.myMSize;
	}

	/**
	 * Return the size of the set P.
	 * 
	 * @return the size of the set P
	 */
	public int getPSize() {
		return this.myPSize;
	}

	/**
	 * If isCopy is true, copy the set of digits indicated by the set of P from
	 * the given other location id to the innovator's current location id. If
	 * isCopy is false, the chance of copying is 50%. Finally, return the
	 * resulting location id. This method does not change the current location
	 * id.
	 * 
	 * @param otherLocId
	 *            a location id
	 * @param isCopy
	 *            a boolean, true for direct copying and false for 50% of
	 *            copying
	 * @return a new location id, which is constructed by the innovator's
	 *         current location id and the given other location id
	 */
	public long getLocIdWithOtherLocId(long otherLocId, boolean isCopy) {
		long result = this.myLocId;
		for (int idx : this.myP) {
			int shiftAmount = Globals.ldscp.getInfN() - 1 - idx;
			result += (1 << shiftAmount)
					* ((otherLocId >> shiftAmount) % 2 - (result >> shiftAmount) % 2)
					* (isCopy ? 1 : (Globals.randomGen.nextBoolean() ? 0 : 1));
		}
		return result;
	}

	/**
	 * Return true if the given innovator agent's P is the same as this
	 * innovator's P.
	 * 
	 * @param innovator
	 *            an innovator agent object
	 * @return true if the given innovator agent's P is the same as this
	 *         innovator's P
	 */
	public boolean canPartnerWith(Innovator innovator) {
		return (this.myP.size() == innovator.myP.size() && this.myP
				.containsAll(innovator.myP));
	}

	/**
	 * Return true if the given provider agent's Q is a super set of this
	 * innovator's P.
	 * 
	 * @param provider
	 *            a provider agent object
	 * @return true if the given provider agent's Q is a super set of this
	 *         innovator's P
	 */
	public boolean canPartnerWith(Provider provider) {
		return (provider.getQ().containsAll(this.myP));
	}

	/**
	 * Return true if Agent.setPartnerId has been called before.
	 * 
	 * @return true if Agent.setPartnerId has been called before
	 */
	public boolean hasSetPartner() {
		return this.myhasSetPartner;
	}

	/**
	 * Return the id of innovator's partner.
	 * 
	 * @return the id of innovator's partner
	 */
	public int getPartnerId() {
		return this.myPartnerId;
	}

	/**
	 * Set innovator's partner id to the given partner id.
	 * 
	 * @param partnerId
	 *            a partner agent id
	 */
	public void setPartnerId(int partnerId) {
		this.myPartnerId = partnerId;
		this.myhasSetPartner = true;
	}

	/**
	 * First call Agent.reset(). ({@inheritDoc}) Then initialize the set of M
	 * and the set of P. Finally, reset the alliance processing power to -1.
	 * 
	 */
	@Override
	public void reset() {
		super.reset();
		HashSet<Integer> allElements = new HashSet<Integer>();
		for (int i = 0; i < Globals.ldscp.getInfN(); i++) {
			allElements.add(i);
		}
		this.myM = new HashSet<Integer>();
		for (int i = 0; i < this.myMSize; i++) {
			this.myM.add(Globals.removeRandomElementFromSet(allElements));
		}
		this.myP = new HashSet<Integer>();
		for (int i = 0; i < this.myPSize; i++) {
			this.myP.add(Globals.removeRandomElementFromSet(allElements));
		}
		this.myAllianceProcessingPower = -1;
		this.myPartnerId = -1;
		this.myhasSetPartner = false;
	}

	/**
	 * Overload the method Innovator.startNewSearch(SearchType type). Innovator
	 * starts a new type of searching process. Innovator does the first
	 * searching step if isToSearchTheFirstStep is true. Search type of
	 * Innovator: M, P, MandP, Magain.
	 * 
	 * @param type
	 *            a search type: Innovator: M, P, MandP
	 * @param isToSearchTheFirstStep
	 *            a boolean
	 * 
	 */
	public void startNewSearch(SearchType type, boolean isToSearchTheFirstStep) {
		HashSet<Integer> elements = null;
		switch (type) {
		case M:
		case Magain:
			elements = this.myM;
			break;
		case P:
			elements = this.myP;
			break;
		case MandP:
			elements = new HashSet<Integer>(this.myM);
			elements.addAll(this.myP);
			break;
		default:
			System.out
					.println("ERROR : Invalid Innovator SearchType : " + type);
			System.exit(1);
		}
		this.mySearchType = type;
		this.myVisitedLocIds = new HashSet<Long>();
		this.myVisitedLocIds.add(this.myLocId);
		this.myUnvisitedNeighourLocIds = Globals.ldscp.getNeighboursInclusive(
				this.myLocId, elements, this.myProcessingPower);
		this.myUnvisitedNeighourLocIds.remove(this.myLocId);
		if (isToSearchTheFirstStep && this.hasUnvisitedNeighbour()) {
			this.continueSearch();
		}
	}

	/**
	 * Innovator starts a new type of searching process and does the first
	 * searching step. Search type of Innovator: M, P, MandP, Magain.
	 * 
	 * @param type
	 *            a search type: Innovator: M, P, MandP, Magain; Provider: Q
	 */
	@Override
	public void startNewSearch(SearchType type) {
		this.startNewSearch(type, true);
	}

	/**
	 * Innovator does one more searching step in the current type of searching
	 * process, and increases its time stamp by 1.
	 */
	@Override
	public void continueSearch() {
		HashSet<Integer> elements = null;
		switch (this.mySearchType) {
		case M:
		case Magain:
			elements = this.myM;
			break;
		case P:
			elements = this.myP;
			break;
		case MandP:
			elements = new HashSet<Integer>(this.myM);
			elements.addAll(this.myP);
			break;
		}
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
					.getNeighboursInclusive(this.myLocId, elements,
							this.myProcessingPower);
			this.myUnvisitedNeighourLocIds.removeAll(this.myVisitedLocIds);
		}
		this.myTimestamp++;
	}

	/**
	 * Innovator starts alliance search with the given alliance processing
	 * power. If the innovator is a Key innovator (with smaller agent id), it
	 * does the first searching step by calling
	 * Innovator.continueAllianceSearch().
	 * 
	 * @param allianceProcessingPower
	 *            a positive integer, which indicates the processing power of
	 *            the alliance
	 */
	public void startAllianceSearch(int allianceProcessingPower) {
		this.mySearchType = SearchType.P;
		this.myAllianceProcessingPower = allianceProcessingPower;
		// key innovator has a smaller id
		if (this.myId < this.myPartnerId) {
			this.myVisitedLocIds = new HashSet<Long>();
			this.myVisitedLocIds.add(this.myLocId);
			this.myUnvisitedNeighourLocIds = Globals.ldscp
					.getNeighboursInclusive(this.myLocId, this.myP,
							this.myAllianceProcessingPower);
			this.myUnvisitedNeighourLocIds.remove(this.myLocId);
			if (this.hasUnvisitedNeighbour()) {
				this.continueAllianceSearch();
			}
		}
	}

	/**
	 * Key innovator does one more searching step in the process of alliance
	 * search. This methods increase the two alliances' time stamps by 1
	 * respectively. The behavior of calling this method by a Value innovator is
	 * undefined.
	 */
	public void continueAllianceSearch() {
		Innovator partner = Globals.innovatorList.get(this.myPartnerId);
		// pick one candidate randomly
		long candidateNeighbour1 = Globals
				.removeRandomElementFromSet(this.myUnvisitedNeighourLocIds);
		// put the candidate in to visited set
		this.myVisitedLocIds.add(candidateNeighbour1);
		// compare
		float newScore1 = Globals.ldscp.getScoreOfLocId(candidateNeighbour1);
		if (newScore1 >= this.myScore) {
			long candidateNeighbour2 = partner.getLocIdWithOtherLocId(
					candidateNeighbour1, true);
			float newScore2 = Globals.ldscp
					.getScoreOfLocId(candidateNeighbour2);
			// pick the better one together
			if (newScore2 >= partner.getScore()) {
				this.myLocId = candidateNeighbour1;
				this.myScore = newScore1;
				partner.myLocId = candidateNeighbour2;
				partner.myScore = newScore2;
				this.myUnvisitedNeighourLocIds = Globals.ldscp
						.getNeighboursInclusive(this.myLocId, this.myP,
								this.myAllianceProcessingPower);
				this.myUnvisitedNeighourLocIds.removeAll(this.myVisitedLocIds);
			}
		}
		this.myTimestamp++;
		partner.myTimestamp++;
	}

	@Override
	public String toString() {
		return this.myTimestamp + "\tINNOVATOR\t" + this.myId + "\t"
				+ this.myProcessingPower + "\t" + this.mySearchType + "\t"
				+ this.myScore + "\t"
				+ (this.myPartnerId < 0 ? "[]" : "[" + this.myPartnerId + "]");
	}
}
