package knn.distance;

import java.io.Serializable;

/**
 * @author Arthur Aleksandrovich
 * 
 * Computes weights of k nearest neighbours 
 * Partially based on <i>Hechenbichler, Schliep: Weighted k-Nearest-Neighbor Techniques and Ordinal Classification</i>
 */
public interface WeightMeasure extends Serializable {
	/**
	 * Set settings for weight measurer
	 * @param maxDistance Distance between data point and (k + 1)th neighbour
	 */
	void setup(double maxDistance);
	
	/**
	 * Weight of neighbour
	 * @param distance Distance between data point and neighbour
	 * @return Weight of neighbour
	 */
	double getWeight(double distance);
	
	/**
	 * Distance normalisation
	 * @param d		Not normalised distance
	 * @param maxD	Distance to (k + 1)th neighbour
	 * @return		Normalised distance
	 */
	default double normalise(double d, double maxD) {
		double epsilon = 1e-4;
		
		return d / (maxD + epsilon);
	}
	
	/**
	 * Name of method (in local language)
	 * @return			Method name
	 */
	String getName();
}
