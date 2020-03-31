package knn.distance;

import java.io.Serializable;

/**
 * @author Arthur Aleksandrovich
 * 
 * Computes distance between two data points 
 */
public interface DistanceMeasure extends Serializable {
	/**
	 * Distance between two data points 
	 * @param point1	First data point
	 * @param point2	Second data point
	 * @return 			Distance between data points
	 */
	double getDistance(double[] point1, double[] point2);
	
	/**
	 * Name of method (in local language)
	 * @return			Method name
	 */
	String getName();
}
