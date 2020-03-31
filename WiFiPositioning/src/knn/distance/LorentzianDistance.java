package knn.distance;

/**
 * @author Arthur Aleksandrovich
 * 
 * Lorentzian distance
 * Based on measure formulae from <i>V. B. Surya Prasath, aneen Arafat Abu Alfeilat, Ahmad B. A. Hassanat, et. al. Effects of Distance Measure Choice on KNN Classifier Performance - A Review</i> 
 */
public final class LorentzianDistance implements DistanceMeasure {

	/**
	 * Generated UID
	 */
	private static final long serialVersionUID = -2807642756171027788L;

	@Override
	public double getDistance(double[] point1, double[] point2) {
		if (point1.length != point2.length) {
			throw new IllegalArgumentException();
		}
		
		int m = point1.length;
		double distance = 0;
		double abs;
		for (int i = 0; i < m; i++) {
			abs = Math.abs(point1[i] - point2[i]);
			distance += Math.log1p(abs);
		}
		
		return distance;
	}
	
	@Override
	public String getName() {
		return "Lorentziāna attālums";
	}

}
