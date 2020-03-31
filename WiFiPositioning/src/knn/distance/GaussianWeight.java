package knn.distance;

/**
 * 
 * @author Arthur Aleksandrovich
 * Weight kernel based on Gaussian function
 * Partially based on <i>Hechenbichler, Schliep: Weighted k-Nearest-Neighbor Techniques and Ordinal Classification</i>
 */
public class GaussianWeight implements WeightMeasure {
	/**
	 * Generated UID
	 */
	private static final long serialVersionUID = 8335319919788973064L;
	// Distance to (k + 1)th neighbour
	private double maxD = 1;
	
	@Override
	public void setup(double maxDistance) {
		this.maxD = maxDistance;
	}

	@Override
	public double getWeight(double distance) {
		double normDistance = normalise(distance, maxD);
		return 1 / Math.sqrt(2 * Math.PI) * Math.exp( - Math.pow(normDistance, 2) / 2);
	}
	
	@Override
	public String getName() {
		return "Gausa svars";
	}
}
