package knn.data;

/**
 * @author Arthur Aleksandrovich
 * 
 * Represents data item containing input and output values. 
 * Stores training and validation data. 
 */
public class DataItem {
	private final double[] x;
	private final OutputVariable[] y;
	
	/**
	 * Constructor method
	 * @param x	Input values
	 * @param y	Output values
	 */
	public DataItem(double[] x, OutputVariable[] y) {
		this.x = x;
		this.y = y;
	}
	
	/**
	 * Getter for input values
	 * @return Input values
	 */
	public double[] getX() {
		return x;
	}
	
	/**
	 * Getter for output values
	 * @return Input values
	 */
	public OutputVariable[] getY() {
		return y;
	}
}
