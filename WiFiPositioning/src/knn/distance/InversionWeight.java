package knn.distance;

/**
 * @author Arthur Aleksandrovich
 * 
 * Inversion based weight kernel
 * Partially based on <i>Hechenbichler, Schliep: Weighted k-Nearest-Neighbor Techniques and Ordinal Classification</i>
 */
public final class InversionWeight implements WeightMeasure {
	/**
	 * Generated UID
	 */
	private static final long serialVersionUID = -5767993760007929988L;
	// Distance to (k + 1)th neighbour
	private double maxD = 1;
	
	@Override
	public void setup(double maxDistance) {
		this.maxD = maxDistance;
	}

	@Override
	public double getWeight(double distance) {
		double normDistance = normalise(distance, maxD);
		return 1 / Math.abs(normDistance);
	}
	
	@Override
	public String getName() {
		return "Apgriezts attālums";
	}
}
