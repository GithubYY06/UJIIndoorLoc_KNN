package console;

import knn.data.OutputVariable;
import knn.data.Real;

import java.io.Serializable;
import java.util.List;

import knn.Knn;
import knn.data.Class;
import knn.data.DataItem;

/**
 * @author Arthur Aleksandrovich
 * 
 * Functions for KNN algorithm validation for indoor positioning by WiFi fingerprints using UJIIndoorLoc database
 */
public class Validation {
	/**
	 * @author Arthur Aleksandrovich
	 * Validation error
	 */
	public static class PredictionError implements Serializable{
		/**
		 * Generated UID
		 */
		private static final long serialVersionUID = -2921145087175796745L;
		/**
		 * Root Mean Squared Error (meters)
		 */
		public final double rmse;
		/**
		 * Correct predictions of floor to all predictions
		 */
		public final double correct;
		
		/**
		 * Constructor method
		 * @param rsme		Root Mean Squared Error (meters)
		 * @param correct	Correct predictions of floor to all predictions
		 */
		public PredictionError(double rmse, double correct) {
			this.rmse = rmse;
			this.correct = correct;
		}
	}
	
	/**
	 * Compute prediction error (regression)
	 * @param y		Real output
	 * @param yHat	Predicted output
	 * @return		Squared error
	 */
	public static double getSquaredError(OutputVariable[] y, OutputVariable[] yHat) {
		// Get difference between outputs as array of reals
		double[] errs = new double[3];
		errs[0] = ((Real)y[0]).getValue() - ((Real)yHat[0]).getValue(); // Longitude
		errs[1] = ((Real)y[1]).getValue() - ((Real)yHat[1]).getValue(); // Latitude
		errs[2] = (Integer.parseInt(((Class)y[2]).getName()) - 
				   Integer.parseInt(((Class)yHat[2]).getName())) * 2;	// Floor
		
		return Math.pow(errs[0], 2) + Math.pow(errs[1], 2) + Math.pow(errs[2], 2);	// squared distance between points in 3d space
	}
	
	/**
	 * Whether floor prediction is correct
	 * @param y		Real output
	 * @param yHat	Predicted output
	 * @return		Whether prediction is wrong
	 */
	public static boolean isCorrectPrediction(OutputVariable[] y, OutputVariable[] yHat) {
		String firstName = ((Class)y[2]).getName();
		String secondName = ((Class)yHat[2]).getName();
		return firstName.equals(secondName);
	}
	
	/**
	 * Compute prediction error of k-Nearest-Neighbour algorithm's configuration
	 * @param predictior	k-Nearest-Neighbour algorithm's configuration
	 * @param validationSet	Validation set
	 * @return				Validation error
	 */
	public static PredictionError getPredictionError(Knn predictor, List<DataItem> validationSet) {
		int n = validationSet.size();	// Number of items in validation set
		double sum = 0;						// Sum of squared errors
		int corrects = 0;					// Number of correct predictions of floor
		
		for (DataItem i : validationSet) {
			OutputVariable[] y = i.getY();
			OutputVariable[] yHat = predictor.predict(i.getX());
			
			sum += getSquaredError(y, yHat);
			if (isCorrectPrediction(y, yHat)) {
				corrects++;
			}
		}
		
		double rmse = Math.sqrt(sum / n);
		double correct = corrects * 1.0 / n;
		
		return new PredictionError(rmse, correct);
	}
}
