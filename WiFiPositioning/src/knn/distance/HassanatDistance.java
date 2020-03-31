package knn.distance;

/**
 * @author Arthur Aleksandrovich
 * 
 * Hassanat Distance
 * Based on measure formulae from <i>V. B. Surya Prasath, aneen Arafat Abu Alfeilat, Ahmad B. A. Hassanat, et. al. Effects of Distance Measure Choice on KNN Classifier Performance - A Review</i> 
 */
public final class HassanatDistance implements DistanceMeasure {

	/**
	 * Generated UID
	 */
	private static final long serialVersionUID = 2489961655395273726L;

	@Override
	public double getDistance(double[] point1, double[] point2) {
		if (point1.length != point2.length) {
			throw new IllegalArgumentException();
		}
		
		int m = point1.length;
		double distance = 0;
		double min, max, D;
		for (int i = 0; i < m; i++) {
			min = Math.min(point1[i], point2[i]);
			max = Math.max(point1[i], point2[i]);
			
			if (min >= 0) {
				D = 1 - (1 + min) / (1 + max);
			}else {
				D = 1 - (1 + min + Math.abs(min)) / (1 + max + Math.abs(min));
			}
			distance += D;
		}
		
		return distance;
	}
	
	@Override
	public String getName() {
		return "Hassanāta attālums";
	}

}
