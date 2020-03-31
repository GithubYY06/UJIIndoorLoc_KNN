package knn.distance;

/**
 * @author Arthur Aleksandrovich
 * 
 * Divergence Distance
 * Based on measure formulae from <i>V. B. Surya Prasath, aneen Arafat Abu Alfeilat, Ahmad B. A. Hassanat, et. al. Effects of Distance Measure Choice on KNN Classifier Performance - A Review</i> 
 */
public class DivergenceDistance implements DistanceMeasure{

	/**
	 * Generate UID
	 */
	private static final long serialVersionUID = 3439725136893221996L;

	@Override
	public double getDistance(double[] point1, double[] point2) {
		if (point1.length != point2.length) {
			throw new IllegalArgumentException();
		}
		int m = point1.length;
		double epsilon = 1e-6;
		
		double sum = 0;
		for (int i = 0; i < m; i++) {
			double dividend = Math.pow(point1[i] - point2[i], 2);
			double divider = Math.pow(point1[i] + point2[i], 2);
			divider = (divider == 0) ? epsilon : divider;
			sum += dividend / (divider);
		}
		
		double distance = 2 * sum;
		
		return distance;
	}

	@Override
	public String getName() {
		return "Diverģences attālums";
	}

}
