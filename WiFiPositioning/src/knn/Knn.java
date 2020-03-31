package knn;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

import knn.data.DataItem;
import knn.data.OutputVariable;
import knn.data.ReducedOutputVariable;
import knn.distance.DistanceMeasure;
import knn.distance.WeightMeasure;
import knn.util.Pair;

/**
 * @author Arthur Aleksandrovich
 * 
 * Realization of k-Nearest-Neighbour algorithm 
 * Partially based on the algorithmic structure of wkNN from <i>Hechenbichler, Schliep: Weighted k-Nearest-Neighbor Techniques and Ordinal Classification</i>
 */
public final class Knn {
	private int k;
	private DistanceMeasure distance;
	private WeightMeasure weight;
	private final List<DataItem> dataItems;
	
	/**
	 * Constructor method
	 * @param k			Number of neighbours
	 * @param distance	Distance measure
	 * @param weight	Weight calculator
	 * @param dataItems	Training data set
	 */
	public Knn(int k, DistanceMeasure distance, WeightMeasure weight, List<DataItem> dataItems) {
		this.k = k;
		this.distance = distance;
		this.weight = weight;
		this.dataItems = Collections.synchronizedList(dataItems);
	}
	
	/**
	 * Setter of number of neighbours
	 * @param k			Number of Neighbours
	 */
	public void setNumOfNeighbours(int k) {
		this.k = k;
	}
	
	/**
	 * Distance measure setter
	 * @param distance	Distance measurer
	 */
	public void setDistanceMeasure(DistanceMeasure distance) {
		this.distance = distance;
	}
	
	/**
	 * Weight measure setter
	 * @param weight	Weight measure
	 */
	public void setWeightMeasure(WeightMeasure weight) {
		this.weight = weight;
	}
	
	/**
	 * Prediction of output variables' values for given data point
	 * @param x	Data point (array of input values)
	 * @return 	Set of predicted output values
	 */
	public OutputVariable[] predict(double[] x) {
		// * Find the k + 1 nearest neighbours to x according to distance function

		LinkedList<Pair<DataItem, Double>> neighDists = dataItems.parallelStream()
						.map(d -> new Pair<DataItem, Double>(d, distance.getDistance(d.getX(), x)))
						.sorted(Comparator.comparing(Pair::getValue1, (a, b) -> Double.compare(a, b)))
						.limit(k + 1)
						.collect(Collectors.toCollection(LinkedList::new));
		
		// * The (k + 1)th neighbour is used for standardisation of the k smallest distances
		Pair<DataItem, Double> nextNeigh =  neighDists.removeLast();
		double maxDistance = nextNeigh.getValue1();
		weight.setup(maxDistance);
		
		// * Transform the normalised distances into weights
		CopyOnWriteArrayList<Pair<DataItem, Double>> neighWeights = neighDists.parallelStream()
						.map(p -> p.changeValue1(weight::getWeight))
						.collect(Collectors.toCollection(CopyOnWriteArrayList::new));
		
		// * Predict class membership or real value
		// Create reduced output array
		ReducedOutputVariable[] reducedOutputs = Arrays.stream(nextNeigh.getValue0().getY())
				.map(o -> ReducedOutputVariable.getReducedOutput(o))
				.toArray(length -> new ReducedOutputVariable[length]);
		
		// reduce list of neighbours
		for (Pair<DataItem, Double> p : neighWeights) {
			for (int i = 0; i < reducedOutputs.length ; i++) {
				reducedOutputs[i].add(p.getValue0().getY()[i], p.getValue1());
			}
		}

		return reducedOutputs;
	}
}
