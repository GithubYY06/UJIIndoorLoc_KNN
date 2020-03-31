package knn.distance;

/**
 * @author Arthur Aleksandrovich
 * 
 * Jaccard Distance
 * Based on measure formulae from <i>V. B. Surya Prasath, aneen Arafat Abu Alfeilat, Ahmad B. A. Hassanat, et. al. Effects of Distance Measure Choice on KNN Classifier Performance - A Review</i> 
 */
public class JaccardDistance implements DistanceMeasure {

	/**
	 * Generated UID
	 */
	private static final long serialVersionUID = -1064696335640978371L;

	@Override
	public double getDistance(double[] point1, double[] point2) {
		if (point1.length != point2.length) {
			throw new IllegalArgumentException();
		}
		int m = point1.length;
		double epsilon = 1e-6;
		
		double dividend = 0;
		double squared1 = 0;
		double squared2 = 0;
		double squared12 = 0;
		
		for (int i = 0; i < m; i++) {
			dividend += Math.pow(point1[i] - point2[i], 2);
			squared1 += Math.pow(point1[i], 2);
			squared2 += Math.pow(point2[i], 2);
			squared12 += Math.pow(point1[i] * point2[i], 2);
		}
		
		double divider = squared1 + squared2 - squared12;
		divider = (divider == 0) ? epsilon : divider;
		
		double distance = dividend / divider;
		
		return distance;
	}

	@Override
	public String getName() {
		return "Džakkarda attālums ";
	}

}
