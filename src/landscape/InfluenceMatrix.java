package landscape;

import java.util.Arrays;

public class InfluenceMatrix {

	private int myN;
	private int myK;
	/**
	 * raw input matrix, entries are 0's and 1's
	 */
	private int myRawMatrix[][];
	/**
	 * each row consists of element indices [0, N-1] in an increasing manner
	 */
	private int myDependenceMatrix[][];

	/**
	 * If the input matrix is a valid influence matrix, then extract the value
	 * of N and K, and also store the dependent elements for each element
	 * explicitly. N is the number of elements, K is the number of dependent
	 * elements that one element can have.
	 * 
	 * E.g., input matrix is [1, 1, 0] [1, 1, 0] [0, 1, 1] then N = 3, K = 1,
	 * dependent element of element 0 is 1, dependent element of element 1 is 0,
	 * dependent element of element 2 is 1.
	 * 
	 * E.g., input matrix is [1, 0, 1, 1] [1, 1, 1, 0] [0, 1, 1, 1] [1, 1, 0, 1]
	 * then N = 4, K = 2, dependent elements of element 0 is 2 and 3, dependent
	 * elements of element 1 is 0 and 1, dependent elements of element 2 is 1
	 * and 3, dependent elements of element 3 is 0 and 1.
	 * 
	 * A valid input matrix must be a square matrix with 0's and 1's. The
	 * diagonal entries must be all 1's. The number of 1's in each row should be
	 * the same.
	 * 
	 * If the input matrix is not valid, the program will exit.
	 * 
	 * @param matrix
	 *            a matrix that represents an influence matrix
	 */
	public InfluenceMatrix(int matrix[][]) {
		// check for valid input matrix
		this.myN = matrix.length;
		this.myK = 0;

		for (int i = 0; i < myN; i++) {
			// check for square matrix
			if (matrix[i].length != this.myN) {
				System.out
						.println("ERROR : invalid length of influence matrix in row "
								+ i);
				System.exit(1);
			}
			// check for valid entry
			for (int j = 0; j < myN; j++) {
				if (matrix[i][j] != 0 && matrix[i][j] != 1) {
					System.out
							.println("ERROR : invalid entry value of influence matrix in position "
									+ i + ", " + j);
					System.exit(1);
				}
			}
			// check for diagonal
			if (matrix[i][i] == 0) {
				System.out.println("invalid self-dependence in row " + i);
				System.exit(1);
			}
			// check for consistent K
			if (i == 0) {
				for (int j = 0; j < myN; j++) {
					this.myK += matrix[i][j];
				}
				this.myK--; // minus self-dependence
			} else {
				int current_K = 0;
				for (int j = 0; j < myN; j++) {
					current_K += matrix[i][j];
				}
				if (this.myK != --current_K) {
					System.out
							.println("inconsistent K between K(row 0) = "
									+ this.myK + " and K(row " + i + ") = "
									+ current_K);
					System.exit(1);
				}
			}
		}

		// store myRawMatrix for print
		// store myDependenceMatrix for later reference
		if (this.myK == 0) { // save some space
			this.myRawMatrix = null;
			this.myDependenceMatrix = null;
		} else {
			this.myRawMatrix = matrix;
			this.myDependenceMatrix = new int[this.myN][this.myK];
			for (int i = 0; i < this.myN; i++) {
				int current_dependence_index = 0;
				for (int j = 0; j < this.myN; j++) {
					if (i != j && this.myRawMatrix[i][j] == 1) {
						this.myDependenceMatrix[i][current_dependence_index++] = j;
					}
				}
			}
		}
	}

	/**
	 * Create a new influence matrix object which is a copy of the given object.
	 * 
	 * @param inf
	 *            the original copy of influence matrix object
	 */
	public InfluenceMatrix(InfluenceMatrix inf) {
		this.myN = inf.getN();
		this.myK = inf.getK();
		this.myRawMatrix = inf.getRawMatrix();
		this.myDependenceMatrix = new int[this.myN][this.myK];
		for (int i = 0; i < this.myN; i++) {
			this.myDependenceMatrix[i] = inf.getDependentElementsOf(i);
		}
	}

	/**
	 * Return N, the number of elements.
	 * 
	 * @return the number of elements
	 */
	public int getN() {
		return this.myN;
	}

	/**
	 * Return K, the number of dependent elements that one element can have.
	 * 
	 * @return the number of dependent elements that one element can have
	 */
	public int getK() {
		return this.myK;
	}

	/**
	 * Return the influence matrix in a 2-dimensional integer array form.
	 * 
	 * @return the influence matrix in a 2-dimensional integer array form
	 */
	public int[][] getRawMatrix() {
		int result[][] = new int[this.myN][this.myN];
		for (int i = 0; i < this.myN; i++) {
			result[i] = Arrays.copyOf(this.myRawMatrix[i], this.myN);
		}
		return result;
	}

	/**
	 * Return an array of dependent elements of the given element
	 * 
	 * @param element
	 *            an integer between 0 and N-1 (inclusive)
	 * @return an array of dependent elements of the given element
	 */
	public int[] getDependentElementsOf(int element) { // row is in the range
														// [0, N-1]
		if (this.myDependenceMatrix == null) {
			return new int[0];
		}
		return Arrays.copyOf(this.myDependenceMatrix[element], this.myK);
	}

	/**
	 * Return a string representation of the influence matrix
	 * 
	 * @return a string representation of the influence matrix
	 */
	@Override
	public String toString() {
		String result = "";
		result += ("N: " + this.myN + "\nK: " + this.myK + "\n");
		if (this.myK != 0) {
			for (int i = 0; i < this.myN; i++) {
				result += (Arrays.toString(this.myRawMatrix[i]) + "\n");
			}
		}
		return result;
	}
}
