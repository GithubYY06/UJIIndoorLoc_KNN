package reader;

import knn.data.DataItem;

import java.io.FileNotFoundException;
import java.util.List;

/**
 * @author Arthur Aleksandrovich
 *
 * Reads training and testing data sets from files
 */
public interface DataReader {
	/**
	 * Extract one training or validation item from string line
	 * @param raw			String line
	 * @return				Data item
	 */
	DataItem readItem(String raw);
	
	/**
	 * Read training or validation set from file
	 * @param path			File path
	 * @param trainingSet	Whether is reading training set
	 * @return				Training data set
	 */
	List<DataItem> readItemSet(String path, boolean trainingSet) throws FileNotFoundException;
}
