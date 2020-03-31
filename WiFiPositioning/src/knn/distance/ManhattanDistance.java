package knn.distance;

/**
 * @author Arthur Aleksandrovich
 * 
 * Manhattan distance
 * Based on measure formulae from <i>V. B. Surya Prasath, aneen Arafat Abu Alfeilat, Ahmad B. A. Hassanat, et. al. Effects of Distance Measure Choice on KNN Classifier Performance - A Review</i> 
 */
public final class ManhattanDistance implements DistanceMeasure {

	/**
	 * Generated UID
	 */
	private static final long serialVersionUID = 4686766260591608814L;

	@Override
	public double getDistance(double[] point1, double[] point2) {
		if (point1.length != point2.length) {
			throw new IllegalArgumentException();
		}
		
		int m = point1.length;
		double distance = 0;
		for (int i = 0; i < m; i++) {
			distance += Math.abs(point1[i] - point2[i]);
		}
		
		return distance;
	}
	
	@Override
	public String getName() {
		return "Manhetenas attālums";
	}

}
