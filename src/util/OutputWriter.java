package util;

import java.io.BufferedWriter;
import java.io.FileWriter;

public class OutputWriter {
	private String myFileName = "";
	private FileWriter myFileWriter = null;
	private BufferedWriter myBufWriter = null;

	/**
	 * Set the writer's target file. If the given file name is already existed,
	 * the writer appends new content to the file
	 * 
	 * @param newName
	 *            a string, which directs to the file to be written (append) to
	 */

	public void setOutputFile(String newName) {
		try {
			this.myFileName = newName;
			this.myFileWriter = new FileWriter(myFileName, true);
			this.myBufWriter = new BufferedWriter(myFileWriter);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Simply write a string followed by new line character to the target file
	 * 
	 * @param line
	 *            a string, which contains the content to be written
	 */
	public void writeLine(String line) {
		try {
			this.myBufWriter.write(line + "\n");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Simply close the writer
	 */
	public void close() {
		try {
			this.myBufWriter.flush();
			this.myFileWriter.close();
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}
}
