package knn.data;

import java.util.Comparator;
import java.util.Hashtable;
import java.util.Map.Entry;

/**
 * @author Arthur Aleksandrovich
 * 
 * Reduced ClassOutput variable
 */
public class ReducedClassOutput implements ReducedOutputVariable, Class {
	private Hashtable<String, Double> allClasses; // Set of all classes with weights
	
	/**
	 * Constructor method
	 */
	public ReducedClassOutput() {
		allClasses = new Hashtable<>();
	}
	
	@Override
	public boolean isClass() {
		return true;
	}
	
	private void add(String name, double weight) {
		Double oldValue = allClasses.getOrDefault(name, 0.0);
		allClasses.put(name, oldValue + weight);
	}

	@Override
	public void add(OutputVariable variable, double weight) {
		Class classVariable = (Class) variable;
		String name = classVariable.getName();
		add(name, weight);
	}

	@Override
	public String getName() {
		return allClasses.entrySet().parallelStream()
				.max(Comparator.comparingDouble(Entry::getValue))
				.get()
				.getKey();
	}
}
