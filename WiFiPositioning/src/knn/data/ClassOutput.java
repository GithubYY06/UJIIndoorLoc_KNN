package knn.data;

/**
 * @author Arthur Aleksandrovich
 * 
 * Implementation of OutputVariable for classification tasks
 * @param <T> Type of stored value
 */
public final class ClassOutput implements OutputVariable, Class {
	private final String name;
	
	/**
	 * Constructor method
	 * @param name 	  Name of the class instance
	 */
	public ClassOutput(String name) {
		this.name = name;
	}
	
	@Override
	public String getName() {
		return name;
	}

	@Override
	public boolean isClass() {
		return true;
	}

}
