package knn.data;

/**
 * @author Arthur Aleksandrovich
 * 
 * Reduced output value that stores predicted value
 */
public interface ReducedOutputVariable extends OutputVariable{
	/**
	 * Add another neighbour's output
	 * @param variable	Neighbour's output 
	 * @param weight	Weight of neighbour
	 */
	void add(OutputVariable variable, double weight);
	
	static ReducedOutputVariable getReducedOutput(OutputVariable other) {
		if (other instanceof Class) {
			return new ReducedClassOutput();
		}
		
		if (other instanceof Real) {
			return new ReducedRealOutput();
		}
		
		return null;
	}
}
