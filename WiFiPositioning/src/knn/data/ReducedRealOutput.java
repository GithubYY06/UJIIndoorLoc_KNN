package knn.data;

/**
 * @author Arthur Aleksandrovich
 *
 * Reduced RealOutput variable
 */
public class ReducedRealOutput implements ReducedOutputVariable, Real {
	private double weightsSum;	//sum of all weights
	private double valueSum; // sum of all weighted values
	
	/**
	 * Constructor method
	 */
	public ReducedRealOutput() {
		valueSum = 0;
		weightsSum = 0;
	}
	
	@Override
	public boolean isClass() {
		return false;
	}

	@Override
	public void add(OutputVariable variable, double weight) {
		Real realVariable = (Real)variable;
		this.weightsSum += weight;
		this.valueSum += realVariable.getValue() * weight;
	}

	@Override
	public double getValue() {
		return valueSum / weightsSum;
	}

}
