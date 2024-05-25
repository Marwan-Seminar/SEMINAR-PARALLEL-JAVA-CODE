package seminar.examples.task.counted_completer;

import java.util.concurrent.CountedCompleter;

/*
 * Beispiel aus der JDK Doku des CountedCompleter
 */
public class CountedCompleterJDKMapReduceExample {

}



class MyMapper<E> { E apply(E v) {
	return v; /* ... */ } }
class MyReducer<E> { E apply(E x, E y) {
	return y;  /*... */ } }


class MapReducer<E> extends CountedCompleter<E> {
  final E[] array; final MyMapper<E> mapper;
  final MyReducer<E> reducer; final int lo, hi;
  MapReducer<E> sibling;
  E result;
  MapReducer(CountedCompleter<?> p, E[] array, MyMapper<E> mapper,
             MyReducer<E> reducer, int lo, int hi) {
    super(p);
    this.array = array; this.mapper = mapper;
    this.reducer = reducer; this.lo = lo; this.hi = hi;
  }
  public void compute() {
    if (hi - lo >= 2) {
      int mid = (lo + hi) >>> 1;
      MapReducer<E> left = new MapReducer(this, array, mapper, reducer, lo, mid);
      MapReducer<E> right = new MapReducer(this, array, mapper, reducer, mid, hi);
      left.sibling = right;
      right.sibling = left;
      setPendingCount(1); // only right is pending
      right.fork();
      left.compute();     // directly execute left
    }
    else {
      if (hi > lo)
          result = mapper.apply(array[lo]);
      tryComplete();
    }
  }
  public void onCompletion(CountedCompleter<?> caller) {
    if (caller != this) {
      MapReducer<E> child = (MapReducer<E>)caller;
      MapReducer<E> sib = child.sibling;
      if (sib == null || sib.result == null)
        result = child.result;
      else
        result = reducer.apply(child.result, sib.result);
    }
  }
  
  public E getRawResult() { return result; }

  public static <E> E mapReduce(E[] array, MyMapper<E> mapper, MyReducer<E> reducer) {
    return new MapReducer<E>(null, array, mapper, reducer,
                             0, array.length).invoke();
  }
}
