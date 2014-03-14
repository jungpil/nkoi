package landscape;

import java.util.Arrays;

import util.Globals;

public class FitnessContributionTable {
	private InfluenceMatrix myInf;
	private int myDim1, myDim2, myDim3;
	/**
	 * A 3-dimensional table, the size is
	 * 
	 * N rows x 2 choices x 2^K choices, K <= 31
	 */
	private float[][][] myTable;

	/**
	 * Internally create a 3-dimensional table, by N by 2 by 2^K, where N and K
	 * are from the influence matrix. Dimension 1 represents N elements.
	 * Dimension 2 represents the 2 choices (i.e. 0 or 1) for one element.
	 * Dimension 3 represents all the possible combination of one element's
	 * dependent elements. The value is a randomly generated fitness
	 * contribution value between 0 to 1.
	 * 
	 * E.g., when N = 6 and K = 5, the value in [0][1][5] gives the fitness
	 * contribution value when "the 0th element" is "1" given
	 * "its 5 dependent elements are 0,0,1,0,1 respectively".
	 * 
	 * E.g., when N = 4 and K = 2, the value in [1][0][1] gives the fitness
	 * contribution value when "the 1st element" is "0" given
	 * "its 2 dependent elements are 0,1 respectively".
	 * 
	 * @param inf
	 *            an influence matrix object
	 */
	public FitnessContributionTable(InfluenceMatrix inf) {
		// assign private field
		this.myInf = inf;

		this.myDim1 = this.myInf.getN();
		this.myDim2 = 2;
		this.myDim3 = (1 << this.myInf.getK());
		this.myTable = new float[this.myDim1][this.myDim2][this.myDim3];

		// fill up the 3-dimensional table
		for (int i = 0; i < this.myDim1; i++) {
			for (int j = 0; j < this.myDim2; j++) {
				for (int k = 0; k < this.myDim3; k++) {
					// firstly generate the value for the time when no shocks
					// have occurred
					this.myTable[i][j][k] = Globals.randomGen.nextFloat();
				}
			}
		}
	}

	/**
	 * Return the fitness contribution value in the internal 3-dimensional table
	 * with the given indices.
	 * 
	 * @return the fitness contribution value in the internal 3-dimensional
	 *         table with the given indices
	 */
	public float getValueOf(int index1, int index2, int index3) {
		return this.myTable[index1][index2][index3];
	}

	/**
	 * Return a string representation of the fitness table.
	 * 
	 * @return a string representation of the fitness table
	 */
	@Override
	public String toString() {
		String result = "";
		for (int i = 0; i < this.myDim1; i++) {
			for (int j = 0; j < this.myDim2; j++) {
				for (int k = 0; k < this.myDim3; k++) {
					Integer kInBinary[] = new Integer[this.myInf.getK()];
					for (int l = 0; l < this.myInf.getK(); l++) {
						kInBinary[l] = ((k >> (this.myInf.getK() - 1 - l)) % 2);
					}
					result += ("d("
							+ i
							+ ") = "
							+ j
							+ " | "
							+ "d"
							+ Arrays.toString(this.myInf
									.getDependentElementsOf(i)) + " = "
							+ Arrays.toString(kInBinary) + " ->\t"
							+ this.getValueOf(i, j, k) + "\n");
				}
			}
		}
		return result;
	}
}
