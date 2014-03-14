package simulation;

import java.util.ArrayList;
import java.util.HashSet;

import landscape.InfluenceMatrix;
import simulation.Simulator.SimulatorType;
import agent.Innovator;
import agent.Provider;

public class Case {
	private int myRuns;
	private InfluenceMatrix myInf;
	private HashSet<SimulatorType> myTypeSet;
	private ArrayList<Innovator> myInnovatorList;
	private ArrayList<Provider> myProviderList;

	/**
	 * Create a new Case object with the given parameters.
	 * 
	 * @param runs
	 *            an integer, which indicates the total number of runs, each run
	 *            takes a different seed
	 * @param inf
	 *            an influence matrix object
	 * @param types
	 *            a set of simulator types
	 * @param innovators
	 *            a list of innovator agent objects
	 * @param providers
	 *            a list of provider agent objects
	 */
	public Case(int runs, InfluenceMatrix inf, HashSet<SimulatorType> types,
			ArrayList<Innovator> innovators, ArrayList<Provider> providers) {
		this.myRuns = runs;
		this.myInf = inf;
		this.myTypeSet = types;
		this.myInnovatorList = innovators;
		this.myProviderList = providers;
	}

	/**
	 * Return the total number of runs.
	 * 
	 * @return the total number of runs
	 */
	public int getRuns() {
		return this.myRuns;
	}

	/**
	 * Return a copy of the influence matrix object.
	 * 
	 * @return a copy of the influence matrix object
	 */
	public InfluenceMatrix getInf() {
		return new InfluenceMatrix(this.myInf);
	}

	/**
	 * Return the set of simulator types.
	 * 
	 * @return the set of simulator types
	 */
	public HashSet<SimulatorType> getTypeSet() {
		return this.myTypeSet;
	}

	/**
	 * Return the innovator list.
	 * 
	 * @return the innovator list
	 */
	public ArrayList<Innovator> getInnovatorList() {
		return new ArrayList<Innovator>(this.myInnovatorList);
	}

	/**
	 * Return the provider list.
	 * 
	 * @return the provider list
	 */
	public ArrayList<Provider> getProviderList() {
		return new ArrayList<Provider>(this.myProviderList);
	}
}
