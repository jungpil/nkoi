package simulation;

import util.Globals;
import agent.Agent.SearchType;
import agent.Innovator;
import agent.Provider;

/**
 * LicensingSearch considers both providers and innovators from time = 0.
 * Providers could have the following states: null -> Q -> wait. Each innovator
 * could have one provider as its partner or could have no partner. Innovators
 * with partner could have the following states: null -> M -> [wait for partner]
 * -> Magain -> wait. Innovators without partner could have the following
 * states: null -> M -> wait.
 * 
 * @author ziquan
 */
public class LicensingSearch extends Simulator {

	@Override
	public void startSimulation() {
		Globals.writer.setOutputFile(this.constructOutputFileName());
		while (!this.isDone()) {
			for (Provider provider : Globals.providerList) {
				if (provider.hasUnvisitedNeighbour()) {
					provider.continueSearch();
					this.writeLog(provider);
				}
				// null -> Q
				else if (provider.getSearchType() == null) {
					provider.startNewSearch(SearchType.Q);
					this.writeLog(provider);
				}
				// Q -> wait
				else {
					// provider.getSearchType() == SearchType.Q
					provider.waitAndDoNothing();
					this.writeLog(provider);
				}
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
				} else if (innovator.getSearchType() == SearchType.M) {
					if (!innovator.hasSetPartner()) {
						int bestProviderId = Globals
								.findBestProviderIdForInnovator(innovator);
						innovator.setPartnerId(bestProviderId);
						if (bestProviderId > 0) {
							Globals.providerList.get(bestProviderId)
									.addPartnerId(innovator.getId());
						}
					}
					// here, innovator's partner was set already
					// innovator with partner:
					if (innovator.getPartnerId() >= 0) {
						Provider partner = Globals.providerList.get(innovator
								.getPartnerId());
						// M -> Magain
						if (partner.getSearchType() == SearchType.Q
								&& !partner.hasUnvisitedNeighbour()) {
							innovator.updateLocIdAndScore(innovator
									.getLocIdWithOtherLocId(partner.getLocId(),
											true));
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
		} // done
		Globals.writer.close();
	}

	/**
	 * Return true if all providers have finished their Q searches, all
	 * innovators with partner have finished their Magain searches and all
	 * innovators without partner have finished their M searches.
	 * 
	 * @return true if all providers have finished their Q searches, all
	 *         innovators with partner have finished their Magain searches and
	 *         all innovators without partner have finished their M searches
	 */
	@Override
	protected boolean isDone() {
		for (Provider provider : Globals.providerList) {
			if (provider.getSearchType() == SearchType.Q
					&& !provider.hasUnvisitedNeighbour()) {
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

	@Override
	public String constructOutputFileName() {
		return "o_n" + Globals.ldscp.getInfN() + "k" + Globals.ldscp.getInfK()
				+ "_x" + Globals.innovatorList.size() + "y"
				+ Globals.providerList.size() + "_licensing.txt";
	}
}
