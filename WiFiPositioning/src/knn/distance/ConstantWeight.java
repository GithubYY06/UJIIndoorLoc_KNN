package knn.distance;
/**
 * @author Arthur Aleksandrovich
 * 
 * Constant value for not-weighted k-Nearest-Neighbours algorithm
 */
public final class ConstantWeight implements WeightMeasure {

	/**
	 * Generated UID
	 */
	private static final long serialVersionUID = -669191650861391365L;

	@Override
	public void setup(double maxDistance) {
		// empty method
	}

	@Override
	public double getWeight(double distance) {
		return 1;
	}

	@Override
	public String getName() {
		return "Konstants svars";
	}
}
