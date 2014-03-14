package simulation;

import java.util.HashMap;
import java.util.HashSet;

import util.Globals;
import agent.Agent;
import agent.Agent.SearchType;
import agent.Innovator;

/**
 * Alliance considers only innovators. Each innovator could have one innovator
 * as its partner or could have no partner. An innovator with partner could
 * either be a Key innovator or a Value innovator. A Key innovator has smaller
 * id than its corresponding Value innovator does. The Key innovators do
 * alliance searches actively, whereas the Value innovators write logs for that
 * pair. Innovators with partner could have the following states: null -> M ->
 * [wait for partner] -> P -> Magain -> wait. Innovators without partner could
 * have the following states: null -> M -> wait.
 * 
 * @author ziquan
 */
public class Alliance extends Simulator {
	private HashMap<Integer, Integer> myIdPairs;
	private HashSet<Integer> mySingleSet;
	private boolean myIsMaxAllianceProcessingPower;

	public Alliance(boolean isMaxAllianceProcessingPower) {
		super();
		this.myIsMaxAllianceProcessingPower = isMaxAllianceProcessingPower;
	}

	@Override
	public void startSimulation() {
		Globals.writer.setOutputFile(this.constructOutputFileName());
		this.randomPairUp();
		while (!this.isDone()) {
			for (Innovator innovator : Globals.innovatorList) {
				// alliance search
				if (innovator.getSearchType() == SearchType.P) {
					// Key innovator:
					if (this.myIdPairs.containsKey(innovator.getId())) {
						if (innovator.hasUnvisitedNeighbour()) {
							innovator.continueAllianceSearch();
						}
						// P -> Magain
						else {
							Innovator partner = Globals.innovatorList
									.get(this.myIdPairs.get(innovator.getId()));
							innovator.startNewSearch(SearchType.Magain);
							partner.startNewSearch(SearchType.Magain, false);
						}
					}
					// Value innovator:
					else {
						this.writeLog(innovator);
					}
				} else if (innovator.hasUnvisitedNeighbour()) {
					innovator.continueSearch();
					this.writeLog(innovator);
				}
				// null -> M
				else if (innovator.getSearchType() == null) {
					innovator.startNewSearch(SearchType.M);
					this.writeLog(innovator);
				} else if (innovator.getSearchType() == SearchType.M) {
					// Key innovator:
					if (this.myIdPairs.containsKey(innovator.getId())) {
						Innovator partner = Globals.innovatorList
								.get(this.myIdPairs.get(innovator.getId()));
						// M -> P
						if (partner.getSearchType() == SearchType.M
								&& !partner.hasUnvisitedNeighbour()) {
							innovator.updateLocIdAndScore(innovator
									.getLocIdWithOtherLocId(partner.getLocId(),
											false));
							partner.updateLocIdAndScore(partner
									.getLocIdWithOtherLocId(
											innovator.getLocId(), true));
							int innovatorPower = innovator.getProcessingPower();
							int partnerPower = partner.getProcessingPower();
							int alliancePower = (this.myIsMaxAllianceProcessingPower ? (innovatorPower > partnerPower ? innovatorPower
									: partnerPower)
									: (innovatorPower < partnerPower ? innovatorPower
											: partnerPower));
							innovator.startAllianceSearch(alliancePower);
							partner.startAllianceSearch(alliancePower);
						}
						// M -> wait for partner
						else {
							innovator.waitAndDoNothing();
						}
					}
					// Value innovator: M -> wait for partner
					else if (this.myIdPairs.containsValue(innovator.getId())) {
						innovator.waitAndDoNothing();
						this.writeLog(innovator);
					}
					// Single innovator: M -> wait
					else {
						// this.mySingleSet.contains(innovator.getId())
						innovator.waitAndDoNothing();
						this.writeLog(innovator);
					}
				}
				// Magain -> wait
				else {
					// innnovator.getSearchType() == SearchType.Magain
					innovator.waitAndDoNothing();
					this.writeLog(innovator);
				}
			}
		} // done
		Globals.writer.close();
	}

	/**
	 * Return true if all alliance innovators have finished Magain searches and
	 * all single innovators have finished their M searches.
	 * 
	 * @return true if all alliance innovators have finished Magain searches and
	 *         all single innovators have finished their M searches
	 */
	@Override
	protected boolean isDone() {
		for (Innovator innovator : Globals.innovatorList) {
			if ((this.myIdPairs.containsKey(innovator.getId()) || this.myIdPairs
					.containsValue(innovator.getId()))
					&& innovator.getSearchType() == SearchType.Magain
					&& !innovator.hasUnvisitedNeighbour()) {
				continue;
			}
			if (this.mySingleSet.contains(innovator.getId())
					&& innovator.getSearchType() == SearchType.M
					&& !innovator.hasUnvisitedNeighbour()) {
				continue;
			}
			return false;
		}
		return true;
	}

	@Override
	public String constructOutputFileName() {
		return "o_n" + Globals.ldscp.getInfN() + "k" + Globals.ldscp.getInfK()
				+ "_x" + Globals.innovatorList.size() + "_alliance"
				+ (this.myIsMaxAllianceProcessingPower ? "_max" : "_min")
				+ ".txt";
	}

	@Override
	protected void writeLog(Agent agt) {
		// always use passive innovators to write log for alliance
		// because they have larger ids than their partners have
		if (this.myIdPairs.containsValue(agt.getId())) {
			super.writeLog(Globals.innovatorList.get(((Innovator) agt)
					.getPartnerId()));
			super.writeLog(agt);
		} else if (this.mySingleSet.contains(agt.getId())) {
			super.writeLog(agt);
		}
	}

	/**
	 * Randomly pair up partnerships. Two innovators could be paired up only if
	 * they have the same P. The innovator with smaller id will be the Key
	 * innovator.
	 */
	private void randomPairUp() {
		this.myIdPairs = new HashMap<Integer, Integer>();
		this.mySingleSet = new HashSet<Integer>();
		for (int i = 0; i < Globals.innovatorList.size(); i++) {
			if (!this.myIdPairs.containsKey(i)
					&& !this.myIdPairs.containsValue(i)
					&& !this.mySingleSet.contains(i)) {
				HashSet<Integer> candidates = new HashSet<Integer>();
				candidates.add(i);
				for (int j = i + 1; j < Globals.innovatorList.size(); j++) {
					if (Globals.innovatorList.get(i).canPartnerWith(
							Globals.innovatorList.get(j))) {
						candidates.add(j);
					}
				}
				if (candidates.size() % 2 == 1) {
					int unluckyId = Globals
							.removeRandomElementFromSet(candidates);
					this.mySingleSet.add(unluckyId);
					Globals.innovatorList.get(unluckyId).setPartnerId(-1);
				}
				while (!candidates.isEmpty()) {
					int luckyId1 = Globals
							.removeRandomElementFromSet(candidates);
					int luckyId2 = Globals
							.removeRandomElementFromSet(candidates);
					// use smaller id as key innovator
					if (luckyId1 < luckyId2) {
						this.myIdPairs.put(luckyId1, luckyId2);
					} else {
						this.myIdPairs.put(luckyId2, luckyId1);
					}
					Globals.innovatorList.get(luckyId1).setPartnerId(luckyId2);
					Globals.innovatorList.get(luckyId2).setPartnerId(luckyId1);
				}
			}
		}
	}
}
