package knn.distance;

/**
 * @author Arthur Aleksandrovich
 * 
 * Max Symmetric Chi Squared Distance
 * Based on measure formulae from <i>V. B. Surya Prasath, aneen Arafat Abu Alfeilat, Ahmad B. A. Hassanat, et. al. Effects of Distance Measure Choice on KNN Classifier Performance - A Review</i> 
 */
public class MaxSymmetricChiSquaredDistance implements DistanceMeasure {

	/**
	 * Generated UID
	 */
	private static final long serialVersionUID = 3644782428496498848L;

	@Override
	public double getDistance(double[] point1, double[] point2) {
		if (point1.length != point2.length) {
			throw new IllegalArgumentException();
		}
		int m = point1.length;
		double epsilon = 1e-6;
		
		double sum1 = 0;
		double sum2 = 0;
		
		for (int i = 0; i < m; i++) {
			double dividend = Math.pow(point1[i] - point2[i], 2);
			
			sum1 += dividend / ((point1[i] == 0) ? epsilon : point1[i]);
			sum2 += dividend / ((point2[i] == 0) ? epsilon : point2[i]);
		}
		
		double distance = Math.max(sum1, sum2);
		
		return distance;
	}

	@Override
	public String getName() {
		return "Maksimālais simetriskais Hi kvadrāta attālums";
	}

}
