package knn.distance;

public class Minkovsky4Distance implements DistanceMeasure {

	/**
	 * Generated UID
	 */
	private static final long serialVersionUID = 464300608206533086L;

	@Override
	public double getDistance(double[] point1, double[] point2) {
		if (point1.length != point2.length) {
			throw new IllegalArgumentException();
		}
		
		int m = point1.length;
		double distance = 0;
		for (int i = 0; i < m; i++) {
			distance += Math.pow(point1[i] - point2[i], 4);
		}
		
		distance = Math.pow(distance, 1.0 / 4);
		
		return distance;
	}

	@Override
	public String getName() {
		return "Minkovska attālums (^4)";
	}

}
