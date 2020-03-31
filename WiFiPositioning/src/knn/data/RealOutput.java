package knn.data;

/**
 * @author Arthur Aleksandrovich
 * 
 * Implementation of OutputVariable for regression tasks
 * @param <T> Type of stored value
 */
public class RealOutput implements OutputVariable, Real {
	private final Double value;
	
	/**
	 * Constructor method
	 * @param value Stored value
	 */
	public RealOutput(Double value) {
		this.value = value;
	}
	
	@Override
	public double getValue() {
		return value;
	}

	@Override
	public boolean isClass() {
		return false;
	}

}
