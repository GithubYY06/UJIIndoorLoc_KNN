package reader;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

import knn.data.ClassOutput;
import knn.data.DataItem;
import knn.data.OutputVariable;
import knn.data.RealOutput;

/**
 * @author Arthur Aleksandrovich
 * 
 * Reads training and validation data from UJIIndoorLoc WiFi fingerprint data base
 */
public final class UJIIndoorLocReader implements DataReader{
	public static final double MAX_INPUT = 0;			// Maximal value of WiFi signal strength
	public static final double MIN_INPUT = - 114;		// Minimal value of WiFi signal strength
	public static final double INPUT_NOT_READ = 100;	// Value when WiFi signal hasn't read
	public static final int INPUTS = 520; // Number of input variables
	
	// Whether output must be stored
	private boolean isStoredOutput(int num) {
		return num == 520 ||	// Longitude
			   num == 521 ||	// Latitude
			   num == 522;		// Floor
	}
	
	// Whether is class output
	private boolean isClassOutput(int num) {
		return num == 522; 		// Floor
	}
	
	public static double normalizeInput(double x) {
		double input = x;
		if (input == INPUT_NOT_READ) {
			input = MIN_INPUT;
		}
		
		return (input - MIN_INPUT) / (MAX_INPUT - MIN_INPUT);
	}
	
	@Override
	public DataItem readItem(String raw) {
		double[] inputs = new double[INPUTS];
		ArrayList<OutputVariable> outputsList = new ArrayList<>();
		
		
		String[] values = raw.split(",");
		for (int i = 0; i < values.length; i++) {
			double real = Double.parseDouble(values[i]);
			if (i < INPUTS) {
				inputs[i] = normalizeInput(real);
			}else if (isStoredOutput(i)) {
				if (isClassOutput(i)) {
					outputsList.add(new ClassOutput(values[i]));
				}else {
					outputsList.add(new RealOutput(real));
				}
			}
		}
		
		OutputVariable[] outputs = outputsList.toArray(new OutputVariable[0]);
		
		return new DataItem(inputs, outputs);
	}

	@Override
	public List<DataItem> readItemSet(String path, boolean trainingSet) throws FileNotFoundException {
		CopyOnWriteArrayList<DataItem> itemSet = new CopyOnWriteArrayList<>();
		try(Scanner sc = new Scanner(new FileInputStream(path))){
			boolean first = true;
			while(sc.hasNextLine()) {
				if (first) {
					sc.nextLine();
					first = false;
					continue;
				}
				itemSet.add(readItem(sc.nextLine()));
			}
		}
		
		// Delete empty items
		if (trainingSet || true) {
			itemSet = itemSet.parallelStream()
					.filter(i -> ! Arrays.stream(i.getX()).allMatch(d -> d == 0))
					.collect(Collectors.toCollection(CopyOnWriteArrayList::new));
		}
		
		return itemSet;
	}

}
