package simulation;

import java.util.HashSet;

import util.Globals;
import agent.Agent.SearchType;
import agent.Innovator;
import agent.Provider;

/**
 * Outsourcing considers both providers and innovators. Providers start their
 * searches after all innovators have finished their M. Each provider could be
 * one or more innovators partner or could be no one's partner. Providers with
 * partner(s) could have the following states: wait for innovators -> null -> Q
 * -> wait. Providers without partner do nothing to save computation time. Each
 * innovator could have one provider as its partner or could have no partner.
 * Innovators with partner could have the following states: null -> M -> [wait
 * for other innovators] -> Magain -> wait. Innovators without partner could
 * have the following states: null -> M -> wait.
 * 
 * @author ziquan
 */
public class Outsourcing extends Simulator {
	private boolean myShouldProviderStart;
	private HashSet<Integer> myChosenProviderIds;

	@Override
	public void startSimulation() {
		Globals.writer.setOutputFile(this.constructOutputFileName());

		// providers do not start at time = 0
		// until all innovators have finished their M.
		this.myShouldProviderStart = false;

		// find those providers that are some innovator's partner
		this.myChosenProviderIds = new HashSet<Integer>();
		for (Innovator innovator : Globals.innovatorList) {
			int bestProviderId = Globals
					.findBestProviderIdForInnovator(innovator);
			innovator.setPartnerId(bestProviderId);
			if (bestProviderId > 0) {
				Globals.providerList.get(bestProviderId).addPartnerId(
						innovator.getId());
			}
			this.myChosenProviderIds.add(innovator.getPartnerId());
		}
		this.myChosenProviderIds.remove(-1);

		while (!this.isDone()) {
			if (this.shouldProviderStart()) {
				for (int chosenId : this.myChosenProviderIds) {
					Provider chosenProvider = Globals.providerList
							.get(chosenId);
					if (chosenProvider.hasUnvisitedNeighbour()) {
						chosenProvider.continueSearch();
						this.writeLog(chosenProvider);
					}
					// null -> Q
					else if (chosenProvider.getSearchType() == null) {
						chosenProvider.startNewSearch(SearchType.Q);
						this.writeLog(chosenProvider);
					}
					// Q -> wait
					else {
						chosenProvider.waitAndDoNothing();
						this.writeLog(chosenProvider);
					}
				}
				for (Innovator innovator : Globals.innovatorList) {
					if (innovator.hasUnvisitedNeighbour()) {
						innovator.continueSearch();
						this.writeLog(innovator);
					} else if (innovator.getSearchType() == SearchType.M) {
						// innovator with partner:
						if (innovator.getPartnerId() >= 0) {
							Provider partner = Globals.providerList
									.get(innovator.getPartnerId());
							// M -> Magain
							if (partner.getSearchType() == SearchType.Q
									&& !partner.hasUnvisitedNeighbour()) {
								innovator.updateLocIdAndScore(innovator
										.getLocIdWithOtherLocId(
												partner.getLocId(), true));
								innovator.startNewSearch(SearchType.Magain);
								this.writeLog(innovator);
							}
							// M -> wait for partner
							else {
								innovator.waitAndDoNothing();
								this.writeLog(innovator);
							}
						}
						// innovator without partner: M -> wait
						else {
							innovator.waitAndDoNothing();
							this.writeLog(innovator);
						}
					}
					// Magain -> wait
					else {
						// innovator.getSearchType() == SearchType.Magain
						innovator.waitAndDoNothing();
						this.writeLog(innovator);
					}
				}
			} else {
				// providers with partner: wait for innovators
				for (int chosenId : this.myChosenProviderIds) {
					Globals.providerList.get(chosenId).waitAndDoNothing();
					this.writeLog(Globals.providerList.get(chosenId));
				}
				for (Innovator innovator : Globals.innovatorList) {
					if (innovator.hasUnvisitedNeighbour()) {
						innovator.continueSearch();
						this.writeLog(innovator);
					}
					// null -> M
					else if (innovator.getSearchType() == null) {
						innovator.startNewSearch(SearchType.M);
						this.writeLog(innovator);
					}
					// M -> wait for other innovators
					else {
						// innovator.getSearchType() == SearchType.M
						innovator.waitAndDoNothing();
						this.writeLog(innovator);
					}
				}
			}
		} // done
		Globals.writer.close();
	}

	/**
	 * Return true if all chosen providers have finished their Q searches, all
	 * innovators with partner have finished their Magain searches and all
	 * innovators without partner have finished their M searches.
	 * 
	 * @return true if all chosen providers have finished their Q searches, all
	 *         innovators with partner have finished their Magain searches and
	 *         all innovators without partner have finished their M searches
	 */
	@Override
	protected boolean isDone() {
		for (int chosenId : this.myChosenProviderIds) {
			if (Globals.providerList.get(chosenId).getSearchType() == SearchType.Q
					&& !Globals.providerList.get(chosenId)
							.hasUnvisitedNeighbour()) {
				continue;
			}
			return false;
		}
		for (Innovator innovator : Globals.innovatorList) {
			if (innovator.getSearchType() == SearchType.Magain
					&& !innovator.hasUnvisitedNeighbour()) {
				continue;
			}
			if (innovator.getSearchType() == SearchType.M
					&& innovator.hasSetPartner()
					&& innovator.getPartnerId() < 0) {
				continue;
			}
			return false;
		}
		return true;
	}

	/**
	 * Return true if all innovators have finished their M searches.
	 * 
	 * @return true if all innovators have finished their M searches
	 */
	private boolean shouldProviderStart() {
		if (this.myShouldProviderStart) {
			return true;
		}
		for (Innovator innovator : Globals.innovatorList) {
			if (innovator.getSearchType() == SearchType.M
					&& !innovator.hasUnvisitedNeighbour()) {
				continue;
			}
			if (innovator.getSearchType() == SearchType.Magain) {
				continue;
			}
			return false;
		}
		this.myShouldProviderStart = true;
		return true;
	}

	@Override
	public String constructOutputFileName() {
		return "o_n" + Globals.ldscp.getInfN() + "k" + Globals.ldscp.getInfK()
				+ "_x" + Globals.innovatorList.size() + "y"
				+ Globals.providerList.size() + "_outsourcing.txt";
	}
}