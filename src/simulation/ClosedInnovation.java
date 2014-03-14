package simulation;

import util.Globals;
import agent.Agent.SearchType;
import agent.Innovator;

/**
 * ClosedInnovation considers only innovator agents. Innovators could have the
 * following states: null -> M -> P -> MandP -> wait
 * 
 * @author ziquan
 */
public class ClosedInnovation extends Simulator {

	@Override
	public void startSimulation() {
		Globals.writer.setOutputFile(this.constructOutputFileName());
		while (!this.isDone()) {
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
				// M -> P
				else if (innovator.getSearchType() == SearchType.M) {
					innovator.startNewSearch(SearchType.P);
					this.writeLog(innovator);
				}
				// P -> MandP
				else if (innovator.getSearchType() == SearchType.P) {
					innovator.startNewSearch(SearchType.MandP);
					this.writeLog(innovator);
				}
				// MandP -> wait
				else {
					innovator.waitAndDoNothing();
					this.writeLog(innovator);
				}
			}
		} // done
		Globals.writer.close();
	}

	/**
	 * Return true if all innovators have finished their MandP searches.
	 * 
	 * @return true if all innovators have finished their MandP searches
	 */
	@Override
	protected boolean isDone() {
		for (Innovator innovator : Globals.innovatorList) {
			if (innovator.getSearchType() == SearchType.MandP
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
				+ "_x" + Globals.innovatorList.size() + "_closed.txt";
	}

}
