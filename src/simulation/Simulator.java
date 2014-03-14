package simulation;

import util.Globals;
import agent.Agent;

public abstract class Simulator {
	public enum SimulatorType {
		CLOSED, LICENSING, OUTSOURCING, ALLIANCE_MAX, ALLIANCE_MIN
	}

	/**
	 * Simulator starts to simulate.
	 */
	public abstract void startSimulation();

	/**
	 * Return a string, which is the output file name.
	 * 
	 * @return a string, which is the output file name
	 */
	public abstract String constructOutputFileName();

	/**
	 * Return true if the simulation could stop.
	 * 
	 * @return true if the simulation could stop
	 */
	protected abstract boolean isDone();

	/**
	 * Write log to the output file for the given agent.
	 * 
	 * @param agt
	 *            an agent object
	 */
	protected void writeLog(Agent agt) {
		Globals.writer.writeLine(Globals.getSeed() + "\t" + agt);
	}
}
