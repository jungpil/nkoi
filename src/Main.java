import java.util.ArrayList;

import landscape.Landscape;
import simulation.Alliance;
import simulation.Case;
import simulation.ClosedInnovation;
import simulation.LicensingSearch;
import simulation.Outsourcing;
import simulation.Simulator;
import simulation.Simulator.SimulatorType;
import util.Globals;
import agent.Innovator;
import agent.Provider;

public class Main {
	public static void main(String[] args) {
		if (args.length != 1) {
			System.out
					.println("ERROR : invalid input, please input one xml config file");
			System.exit(1);
		}
		ArrayList<Case> cases = Globals.reader.read(args[0]);
		for (Case c : cases) {
			for (int i = 0; i < c.getRuns(); i++) {
				Globals.setSeed(i);
				Globals.ldscp = new Landscape(c.getInf());
				Globals.innovatorList = c.getInnovatorList();
				Globals.providerList = c.getProviderList();
				for (SimulatorType type : c.getTypeSet()) {
					for (Innovator innovator : Globals.innovatorList) {
						innovator.reset();
					}
					for (Provider provider : Globals.providerList) {
						provider.reset();
					}
					Simulator s = null;
					switch (type) {
					case CLOSED:
						s = new ClosedInnovation();
						s.startSimulation();
						break;
					case LICENSING:
						s = new LicensingSearch();
						s.startSimulation();
						break;
					case OUTSOURCING:
						s = new Outsourcing();
						s.startSimulation();
						break;
					case ALLIANCE_MAX:
						s = new Alliance(true);
						s.startSimulation();
						break;
					case ALLIANCE_MIN:
						s = new Alliance(false);
						s.startSimulation();
						break;
					}
				}
			}
		}
	}

}
