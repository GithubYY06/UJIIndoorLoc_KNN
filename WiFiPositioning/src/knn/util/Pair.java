package knn.util;
import java.util.function.Function; 
/**
 * @author Arthur Aleksandrovich
 * 
 * Tuple class realization for compatibility with non-common JVM
 *
 * @param <T0>	Type of first element
 * @param <T1>	Type of second element
 */
public class Pair<T0, T1> {
	private T0 value0;
	private T1 value1;
	
	public Pair(T0 value0, T1 value1) {
		this.value0 = value0;
		this.value1 = value1;
	}
	
	public T0 getValue0() {
		return value0;
	}
	
	public void setValue0(T0 value0) {
		this.value0 = value0;
	}
	
	public Pair<T0, T1> changeValue0(Function<T0,T0> function) {
		setValue0(function.apply(value0));
		
		return this;
	}
	
	public T1 getValue1() {
		return value1;
	}
	
	public void setValue1(T1 value1) {
		this.value1 = value1;
	}
	
	public Pair<T0, T1> changeValue1(Function<T1,T1> function) {
		setValue1(function.apply(value1));
		
		return this;
	}
}
