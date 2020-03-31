package console;

import java.io.Serializable;

import console.Validation.PredictionError;
import knn.distance.DistanceMeasure;
import knn.distance.WeightMeasure;

/**
 * @author Arthur Aleksandrovich
 * 
 * KNN configurations' validation data
 */
public class AnalysisData implements Serializable {
	/**
	 * Generated UID
	 */
	private static final long serialVersionUID = 6141315388421747825L;

	// Number of neighbours
	public final int[] ks;
	
	public final DistanceMeasure[] distanceMeasurers;
	public final WeightMeasure[] weightMeasurers;
	
	// * Dimensions of stored errors: 
	//		1 - weight kernel
	//		2 - distance measure
	//		3 - number of neighbours
	public PredictionError[][][] errors;
	
	public AnalysisData(int[] ks, DistanceMeasure[] distanceMeasurers, WeightMeasure[] weightMeasurers, PredictionError[][][] errors) {
		this.ks = ks;
		this.distanceMeasurers = distanceMeasurers;
		this.weightMeasurers = weightMeasurers;
		this.errors = errors;
	}
}
