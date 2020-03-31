package knn.distance;

/**
 * @author Arthur Aleksandrovich
 * 
 * Euclidian distance
 * Based on measure formulae from <i>V. B. Surya Prasath, aneen Arafat Abu Alfeilat, Ahmad B. A. Hassanat, et. al. Effects of Distance Measure Choice on KNN Classifier Performance - A Review</i> 
 */
public final class EuclidianDistance implements DistanceMeasure {

	/**
	 * Generated UID
	 */
	private static final long serialVersionUID = -3151135317774419681L;

	@Override
	public double getDistance(double[] point1, double[] point2) {
		if (point1.length != point2.length) {
			throw new IllegalArgumentException();
		}
		
		int m = point1.length;
		double distance = 0;
		for (int i = 0; i < m; i++) {
			distance += Math.pow(point1[i] - point2[i], 2);
		}
		
		distance = Math.sqrt(distance);
		
		return distance;
	}
	
	@Override
	public String getName() {
		return "Eiklīda attālums";
	}

}
