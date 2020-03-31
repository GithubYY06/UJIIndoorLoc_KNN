package console;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.stream.IntStream;

import console.Validation.PredictionError;
import knn.Knn;
import knn.data.DataItem;
import knn.data.OutputVariable;
import knn.distance.ConstantWeight;
import knn.distance.DistanceMeasure;
import knn.distance.DivergenceDistance;
import knn.distance.EuclidianDistance;
import knn.distance.GaussianWeight;
import knn.distance.HassanatDistance;
import knn.distance.InversionWeight;
import knn.distance.JaccardDistance;
import knn.distance.LorentzianDistance;
import knn.distance.ManhattanDistance;
import knn.distance.MaxSymmetricChiSquaredDistance;
import knn.distance.Minkovsky4Distance;
import knn.distance.WeightMeasure;
import reader.UJIIndoorLocReader;

/**
 * @author Arthur Aleksandrovich
 * 
 * Command-Line Interface
 */
public final class Cli {
	// Constants for analysis
	private static final int NUM_OF_NEIGHBOURS_MIN = 1;		// Number of neighbours to start with
	private static final int NUM_OF_NEIGHBOURS_STEP = 1;	// Step
	private static final int NUM_OF_NEIGHBOURS_MAX = 50; 	// Number of neighbours to end 
	
	// File to store temporary analysis data
	private static final String ANALYSIS_TEMP = "analysis.temp";
	
	private static final String[] distaceClasses = new String[] {
			"knn.distance.Minkovsky4Distance",
			"knn.distance.EuclidianDistance",
			"knn.distance.ManhattanDistance",
			"knn.distance.LorentzianDistance",
			"knn.distance.DivergenceDistance",
			"knn.distance.HassanatDistance",
			"knn.distance.JaccardDistance",
			"knn.distance.MaxSymmetricChiSquaredDistance"
	};	// Names of classes that implement distance measures
	
	private static final String[] weightClasses = new String[] {
			"knn.distance.ConstantWeight",
			"knn.distance.InversionWeight",
			"knn.distance.GaussianWeight"
	};	// Names of classes that implement weight kernels
	
	private static enum MODE{
		PREDICTION, // Predict output for given input
		VALIDATION,	// Validate given KNN configuration
		ANALYSIS	// Validate all KNN configurations
	}
	
	
	private static final UJIIndoorLocReader reader = new UJIIndoorLocReader();
	private static final Scanner scanner = new Scanner(System.in);
	private static PrintStream console;
	
	public static void main(String[] args) throws UnsupportedEncodingException {
		console = new PrintStream(System.out, true, "UTF-8");
		MODE mode = getMode();
		
		switch (mode) {
		case PREDICTION:
			runPredictionMode();
			break;
		case VALIDATION:
			runValidationMode();
			break;
		case ANALYSIS:
			runAnalysis();
		}
	}
	
	/**
	 * Get from user programme mode
	 * @return
	 */
	private static MODE getMode() {
		final String askMode = 
				"Lūdzu ievadiet programmas palaišanas režīmu (p – prognozēšana; v – validēšana, a – analīze): ";
		
		MODE mode = null;
		while (mode == null) {
			console.print(askMode);
			
			String response = null;
			response = scanner.nextLine().toLowerCase();
			console.println();
			
			switch (response) {
			case "p":
				mode = MODE.PREDICTION;
				break;
			case "v":
				mode = MODE.VALIDATION;
				break;
			case "a":
				mode = MODE.ANALYSIS;
				break;
			}
		}
		
		return mode;
	}
	
	/**
	 * Predict output for given input
	 */
	private static void runPredictionMode() {
		List<DataItem> trainingSet = readItemSet(true);
		
		Knn predictor = initialiseKnnConfiguration(trainingSet);
		
		double[] input = null;
		do {
			input = getInput();
			if (input != null) {
				OutputVariable[] prediction = predictor.predict(input);
				printPrediction(prediction);
			}
		}while (input != null);
	}
	
	/**
	 * Validate given KNN configuration
	 */
	private static void runValidationMode() {
		List<DataItem> trainingSet = readItemSet(true);
		List<DataItem> validationSet = readItemSet(false);
		
		Knn predictor = initialiseKnnConfiguration(trainingSet);
		
		PredictionError error = Validation.getPredictionError(predictor, validationSet);
		
		printPredictionError(error);
	}
	
	/**
	 * Validate all KNN configurations
	 */
	private static void runAnalysis() {
		final String filePath = "analysis.csv";
		final String isExecuting = "Analīze ir procesā. Lūdzu gaidiet ";
		final String executionEnded = "Analīze ir pabeigta. Analīzes rezultāti ir datnē '" + filePath + "'.";
		
		List<DataItem> trainingSet = readItemSet(true);
		List<DataItem> validationSet = readItemSet(false);
		
		// initialise task
		Runnable task = () -> analysis(trainingSet, validationSet, filePath);
		Thread thread = new Thread(task);
		thread.setPriority(Thread.MAX_PRIORITY);
		thread.start();
		
		final Object LOCK = new Object();
		
		// Show user that programme is executing
		console.print(isExecuting);
		try {
			while(thread.isAlive()) {
				synchronized(LOCK) {
					LOCK.wait(1000);
				}
				console.print('.');
			}
			
		} catch (InterruptedException e) {
			e.printStackTrace();
			return;
		}
		
		// End of execution
		console.println("\n" + executionEnded);
	}
	
	/**
	 * Run analysis (call this method in separate thread)
	 * @param trainingSet	Training set
	 * @param validSet		Validation set
	 * @param filePath		Path to file to store error data
	 */
	private static void analysis(List<DataItem> trainingSet, List<DataItem> validSet, String filePath) {
		// * Load or initialise Knn configuration parameters and errors 
		AnalysisData aData = loadTempAnalysis();
		final int[] ks = aData.ks;
		final DistanceMeasure[] distanceMeasurers = aData.distanceMeasurers;
		final WeightMeasure[] weightMeasurers = aData.weightMeasurers;
		// Dimensions of stored errors: 
		//		1 - weight kernel
		//		2 - distance measure
		//		3 - number of neighbours
		PredictionError[][][] errors = aData.errors;
		
		// * Initialize Knn configuration only with training data set
		Knn knn = new Knn(0, null, null, trainingSet);
		
		// * Get validation error for all configurations
		for (int w = 0; w < weightMeasurers.length; w++) {
			knn.setWeightMeasure(weightMeasurers[w]);
			
			for (int d = 0; d < distanceMeasurers.length; d++) {
				knn.setDistanceMeasure(distanceMeasurers[d]);
				
				for (int k = 0; k < ks.length; k++) {
					knn.setNumOfNeighbours(ks[k]);
					
					// compute errors only if not already computed
					if (errors[w][d][k] == null) {
						errors[w][d][k] = Validation.getPredictionError(knn, validSet);
						// Store temporary analysis data
						saveTempAnalysis(aData);
						console.print(":");
					}
				}
			}
		}
		
		// * Write errors to file
		// Output file is CSV - formatted and is intended to open in spreadsheet programme (like Excel)
		// Contains some tables; number of tables is equal to double number of weight kernel functions
		// Even tables contains correct predictions to validation set size, odd tables contains prediction error
		// Rows of tables - distance measures
		// Columns of tables - numbers of neighbours
		try (PrintWriter pw = new PrintWriter(filePath, "UTF-8")){
			for (int w = 0; w < weightMeasurers.length; w++) {
				// Correct predictions
				// Table name
				pw.println(String.format("%s (pareizi prognozēto stāvu koeficients)", weightMeasurers[w].getName()));
				
				// Column names
				for (int k = 0; k < ks.length; k++) {
					pw.print(',');
					pw.print(ks[k]);
				}
				pw.println();
				for (int d = 0; d < distanceMeasurers.length; d++) {
					// Distance measure name
					pw.print(distanceMeasurers[d].getName());
					
					// Columns
					for (int k = 0; k < ks.length; k++) {
						pw.print(',');
						pw.print(String.format("%.10f", errors[w][d][k].correct));
					}
					pw.println();
				}
				pw.println();
				
				// Prediction error
				// Table name
				pw.println(String.format("%s (RMSE prognozēšanas kļūda)", weightMeasurers[w].getName()));
				
				// Column names
				for (int k = 0; k < ks.length; k++) {
					pw.print(',');
					pw.print(ks[k]);
				}
				pw.println();
				for (int d = 0; d < distanceMeasurers.length; d++) {
					// Distance measure name
					pw.print(distanceMeasurers[d].getName());
					
					// Columns
					for (int k = 0; k < ks.length; k++) {
						pw.print(',');
						pw.print(String.format("%.10f", errors[w][d][k].rmse));
					}
					pw.println();
				}
				pw.println();
			}
		} catch (FileNotFoundException|UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		// Remove temporary analysis data
		removeTempAnalysis();
	}
	
	/**
	 * Save temporary analysis data to file system
	 * @param data	Analysis data
	 */
	private static void saveTempAnalysis(AnalysisData data) {
		try (FileOutputStream fos = new FileOutputStream(ANALYSIS_TEMP, false);
			 ObjectOutputStream oos = new ObjectOutputStream(fos)){
			oos.writeObject(data);
			oos.flush();
		} catch (IOException e) {
			// Do nothing
		}
	}
	
	/**
	 * Load temporary analysis data from file system or initialise it
	 * @return		Analysis data
	 */
	@SuppressWarnings("unchecked")
	private static AnalysisData loadTempAnalysis() {
		AnalysisData data = null;
		
		// Try load data
		try (FileInputStream fis = new FileInputStream(ANALYSIS_TEMP);
			 ObjectInputStream ois = new ObjectInputStream(fis)){
			data = (AnalysisData) ois.readObject();
		} catch (Exception e) {
			// Do nothing
		} 
		
		// If no data is loaded, initialise it
		if (data == null) {
			// Initialise configurations
			
			// Number of neighbours
			int[] ks = IntStream.iterate(NUM_OF_NEIGHBOURS_MIN, 
										 (x) -> x <= NUM_OF_NEIGHBOURS_MAX, 
										 (x) -> x + NUM_OF_NEIGHBOURS_STEP).toArray();
			// Class initialisation from names using reflection
			DistanceMeasure[] distanceMeasurers = new DistanceMeasure[distaceClasses.length];
			WeightMeasure[] weightMeasurers = new WeightMeasure[weightClasses.length];
			try {
				Class<?> aClass;
				
				// Distance measures
				for (int i = 0; i < distaceClasses.length; i++) {
					aClass = Class.forName(distaceClasses[i]);
					
					if (!DistanceMeasure.class.isAssignableFrom(aClass)) {
						throw new ClassNotFoundException("Class not implements DistanceMeasure interface");
					}
					
					Class<DistanceMeasure> distanceClass = (Class<DistanceMeasure>) aClass;
					DistanceMeasure distance = distanceClass.getDeclaredConstructor().newInstance();
					distanceMeasurers[i] = distance;
				}
				
				// Weight kernels
				for (int i = 0; i < weightClasses.length; i++) {
					aClass = Class.forName(weightClasses[i]);
					
					if (!WeightMeasure.class.isAssignableFrom(aClass)) {
						throw new ClassNotFoundException("Class not implements DistanceMeasure interface");
					}
					
					Class<WeightMeasure> weightClass = (Class<WeightMeasure>) aClass;
					WeightMeasure weight = weightClass.getDeclaredConstructor().newInstance();
					weightMeasurers[i] = weight;
				}
			} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | IllegalArgumentException
					| InvocationTargetException | NoSuchMethodException | SecurityException e) {
				e.printStackTrace();
			}
			
			// Initialise errors
			PredictionError[][][] errors = new PredictionError[weightMeasurers.length]
					  [distanceMeasurers.length]
					  [ks.length];
			
			// Initialise data
			data = new AnalysisData(ks, distanceMeasurers, weightMeasurers, errors);
		}
		
		return data;
	}
	
	/**
	 * Remove temporary analysis data from file system
	 */
	private static void removeTempAnalysis() {
		Path fileToDeletePath = Paths.get(ANALYSIS_TEMP);
		try {
			Files.delete(fileToDeletePath);
		} catch (IOException e) {
			// Do nothing
		}
	}
	/**
	 * Get path to item set from user and read it
	 * @param trainingSet	Whether read training or validation set
	 * @return				Set of training or validation data
	 */
	private static List<DataItem> readItemSet(boolean trainingSet){
		final String askTrainingFile = "Lūdzu ievadiet ceļu pie apmācības kopas: ";
		final String askValidFile = "Lūdzu ievadiet ceļu pie validēšanas kopas: ";
		
		List<DataItem> itemSet = null;
		
		do {
			console.print((trainingSet) ? askTrainingFile : askValidFile);
			String path;
			path = scanner.nextLine();
			console.println();
			
			try {
				itemSet = reader.readItemSet(path, trainingSet);
			} catch (FileNotFoundException e) {
			}
		}while (itemSet == null);
		
		return itemSet;
	}
	
	/**
	 * Get configuration parameters from user and initialise KNN predictor
	 * @return
	 */
	private static Knn initialiseKnnConfiguration(List<DataItem> trainingSet) {
		final String askK = "Lūdzu ievadiet kaimiņu skaitu: ";
		final String askDistance = "Lūdzu noradiet attāluma metriku (m - Manhetenas attālums; e - Eiklīda attālums; 4 - Minkovska attālums (4. pakāpe); "
				+ "l - Lorentziāna attālums; h - Hassanāta attālums; d - Diverģences attālums; "
				+ "j - Džakkarda attālums; s - Maksimālais simetriskais Hi kvadrāta attālums): ";
		final String askWeight = "Lūdzu noradiet svara funkciju (c - Konstants svars; i - Apgriezts attālums; g - Gausa svars): ";
		
		int k = 0;
		DistanceMeasure distance = null;
		WeightMeasure weight = null;
		
		// Ask number of neighbours
		do {
			console.print(askK);
			String output = scanner.nextLine();
			console.println();
			try {
				k = Integer.parseInt(output);
			} catch (NumberFormatException e) {}
		}while (k == 0);
					
		// Ask distance measure
		do {
			console.print(askDistance);
						
			switch(scanner.nextLine().toLowerCase()) {
			case "m": 
				distance = new ManhattanDistance();
				break;
			case "e":
				distance = new EuclidianDistance();
				break;
			case "4":
				distance = new Minkovsky4Distance();
				break;
			case "l": 
				distance = new LorentzianDistance();
				break;
			case "h": 
				distance = new HassanatDistance();
				break;
			case "d":
				distance = new DivergenceDistance();
				break;
			case "j":
				distance = new JaccardDistance();
				break;
			case "s":
				distance = new MaxSymmetricChiSquaredDistance();
				break;
			}
			console.println();
		}while (distance == null);
					
		// Ask weight function kernel
		do {
			console.print(askWeight);
						
			switch (scanner.nextLine().toLowerCase()) {
			case "c":
				weight = new ConstantWeight();
				break;
			case "i":
				weight = new InversionWeight();
				break;
			case "g":
				weight = new GaussianWeight();
				break;
			}
			console.println();
		}while (weight == null);
		
		Knn knn = new Knn(k, distance, weight, trainingSet);
		
		return knn;
	}
	
	/**
	 * Get input value from user
	 * @return			Input data point
	 */
	@SuppressWarnings("static-access")
	private static double[] getInput() {
		final String askWapFirst = "WAP indekss (lai izietu ievadiet e): ";
		final String askWap = "WAP indekss (lai iegūtu koordinātes ievadiet e): ";
		final String askValue = "\tSignāla stiprums: ";
		final String isRead = "Signāla stiprums ir ielasīts";
		final String notRead = "Signāla stiprums nav ielasīts";
		
		double[] input = new double[reader.INPUTS];
		int index;
		int value;
		Arrays.fill(input, 0);
		
		boolean first = true;
		boolean oneValueRead = false;
		while (true) {
			// asp WiFi Access Point index
			if (first) {
				console.print(askWapFirst);
				first = false;
			}else {
				console.print(askWap);
			}
			String output = scanner.nextLine();
			console.println();
			
			if (output.equalsIgnoreCase("e")) {
				break;
			}
			
			try {
				index = Integer.parseInt(output);
				if (index < 1 || index > reader.INPUTS) {
					throw new NumberFormatException("WAP index is out of range");
				}
			} catch (NumberFormatException e) {
				console.println(notRead);
				continue;
			}
			
			// Ask signal strength
			console.print(askValue);
			output = scanner.nextLine();
			console.println();
			try {
				value = Integer.parseInt(output);
				if (value < reader.MIN_INPUT || value > reader.MAX_INPUT) {
					throw new NumberFormatException("Signal strength is out of range");
				}
			} catch (NumberFormatException e) {
				console.println(notRead);
				continue;
			}
			
			// Save signal value
			input[index - 1] = reader.normalizeInput(value);
			console.println(isRead);
			oneValueRead = true;
		}
		
		return (oneValueRead) ? input : null;
	}
	
	/**
	 * Print to console predicted coordinates
	 * @param output	Predicted coordinates
	 */
	private static void printPrediction(OutputVariable[] output) {
		String outputLine = String.format("\tĢeogrāfiskais platums = %.3f\n\tĢeogrāfiskais garums = %.3f\n\t%s. stāva ", 
									((knn.data.Real)output[0]).getValue(),
									((knn.data.Real)output[1]).getValue(),
									((knn.data.Class)output[2]).getName());
		
		console.println(outputLine);
	}
	
	/**
	 * Print to console prediction errors
	 * @param error		Prediction errors
	 */
	private static void printPredictionError(PredictionError error) {
		String outputLine = String.format("\tPareizi prognozēto stāvu koeficients = %.3f\n\tRMSE prognozēšanas kļūda = %.3f",
									error.correct,
									error.rmse);
		
		console.println(outputLine);
	}
}
