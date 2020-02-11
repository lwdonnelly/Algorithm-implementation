/******************************************************************************
 *  Compilation:  javac IndexMinPQ.java
 *  Execution:    java IndexMinPQ
 *  Dependencies: StdOut.java
 *
 *  Minimum-oriented indexed PQ implementation using a binary heap.
 *
 ******************************************************************************/

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 *  The {@code IndexMinPQ} class represents an indexed priority queue of generic keys.
 *  It supports the usual <em>insert</em> and <em>delete-the-minimum</em>
 *  operations, along with <em>delete</em> and <em>change-the-Car</em> 
 *  methods. In order to let the client refer to keys on the priority queue,
 *  an integer between {@code 0} and {@code maxN - 1}
 *  is associated with each Car—the client uses this integer to specify
 *  which Car to delete or change.
 *  It also supports methods for peeking at the minimum Car,
 *  testing if the priority queue is empty, and iterating through
 *  the keys.
 *  <p>
 *  This implementation uses a binary heap along with an array to associate
 *  keys with integers in the given range.
 *  The <em>insert</em>, <em>delete-the-minimum</em>, <em>delete</em>,
 *  <em>change-Car</em>, <em>decrease-Car</em>, and <em>increase-Car</em>
 *  operations take logarithmic time.
 *  The <em>is-empty</em>, <em>size</em>, <em>min-index</em>, <em>min-Car</em>,
 *  <em>contains</em>, and <em>Car-of</em> operations take constant time.
 *  Construction takes time proportional to the specified capacity.
 *  <p>
 *  For additional documentation, see <a href="https://algs4.cs.princeton.edu/24pq">Section 2.4</a> of
 *  <i>Algorithms, 4th Edition</i> by Robert Sedgewick and Kevin Wayne.
 *
 *  @author Robert Sedgewick
 *  @author Kevin Wayne
 *
 *  @param <Car> the generic type of Car on this priority queue
 */
public class IndexMinPQ implements Iterable<Integer> {
    private int n;           // number of elements on PQ
    private int[] pq;        // binary heap using 1-based indexing
    private int[] qp;        // inverse of pq - qp[pq[i]] = pq[qp[i]] = i
    private Car[] keys;      // keys[i] = priority of i

    /**
     * Initializes an empty indexed priority queue with indices between {@code 0}
     * and {@code maxN - 1}.
     * @param  maxN the keys on this priority queue are index from {@code 0}
     *         {@code maxN - 1}
     * @throws IllegalArgumentException if {@code maxN < 0}
     */
    public IndexMinPQ(int size) {
        n = 0;
        keys = (Car[]) new Car[size + 1];    // make this of length maxN??
        pq   = new int[size + 1];
        qp   = new int[size + 1];                   // make this of length maxN??
        for (int i = 0; i <= size; i++)
            qp[i] = -1;
    }

    /**
     * Returns true if this priority queue is empty.
     *
     * @return {@code true} if this priority queue is empty;
     *         {@code false} otherwise
     */
    public boolean isEmpty() {
        return n == 0;
    }

    /**
     * Is {@code i} an index on this priority queue?
     *
     * @param  i an index
     * @return {@code true} if {@code i} is an index on this priority queue;
     *         {@code false} otherwise
     * @throws IllegalArgumentException unless {@code 0 <= i < maxN}
     */
    public boolean contains(int i) {
        if (i < 0 ) throw new IllegalArgumentException();
        return qp[i] != -1;
    }

    /**
     * Returns the number of keys on this priority queue.
     *
     * @return the number of keys on this priority queue
     */
    public int size() {
        return n;
    }

    /**
     * Associates Car with index {@code i}.
     *
     * @param  i an index
     * @param  Car the Car to associate with index {@code i}
     * @throws IllegalArgumentException unless {@code 0 <= i < maxN}
     * @throws IllegalArgumentException if there already is an item associated
     *         with index {@code i}
     */
    public void insert(int i, Car Car) {
        if (i < 0 ) throw new IllegalArgumentException();
        if (contains(i)) throw new IllegalArgumentException("index is already in the priority queue");
        
        
        n++;
        if(n >= pq.length) {
        	int[] tempPQ = new int[pq.length * 2];
        	int[] tempQP = new int[pq.length * 2];
        	Car[] tempKeys = new Car[keys.length * 2];
        	
        	for(int j = 0;j < pq.length;j++) {
        		tempPQ[j] = pq[j];
        		tempQP[j] = qp[j];
        		tempKeys[j] = keys[j];
        	}
        	pq = tempPQ;
        	qp = tempQP;
        	keys = tempKeys;
        }
        
        qp[i] = n;
        pq[n] = i;
        keys[i] = Car;
        swim(n);
    }

    /**
     * Returns an index associated with a minimum Car.
     *
     * @return an index associated with a minimum Car
     * @throws NoSuchElementException if this priority queue is empty
     */
    public int minIndex() {
        if (n == 0) throw new NoSuchElementException("Priority queue underflow");
        return pq[1];
    }

    /**
     * Returns a minimum Car.
     *
     * @return a minimum Car
     * @throws NoSuchElementException if this priority queue is empty
     */
    public Car minKey() {
        if (n == 0) throw new NoSuchElementException("Priority queue underflow");
        return keys[pq[1]];
    }

    /**
     * Removes a minimum Car and returns its associated index.
     * @return an index associated with a minimum Car
     * @throws NoSuchElementException if this priority queue is empty
     */
    public int delMin() {
        if (n == 0) throw new NoSuchElementException("Priority queue underflow");
        int min = pq[1];
        exch(1, n--);
        sink(1);
        assert min == pq[n+1];
        qp[min] = -1;        // delete
        keys[min] = null;    // to help with garbage collection
        pq[n+1] = -1;        // not needed
        return min;
    }

    /**
     * Returns the Car associated with index {@code i}.
     *
     * @param  i the index of the Car to return
     * @return the Car associated with index {@code i}
     * @throws IllegalArgumentException unless {@code 0 <= i < maxN}
     * @throws NoSuchElementException no Car is associated with index {@code i}
     */
    public Car keyOf(int i) {
        if (i < 0 ) throw new IllegalArgumentException();
        if (!contains(i)) throw new NoSuchElementException("index is not in the priority queue");
        else return keys[i];
    }

    /**
     * Change the Car associated with index {@code i} to the specified value.
     *
     * @param  i the index of the Car to change
     * @param  Car change the Car associated with index {@code i} to this Car
     * @throws IllegalArgumentException unless {@code 0 <= i < maxN}
     * @throws NoSuchElementException no Car is associated with index {@code i}
     */
    public void changeKey(int i, Car Car) {
        if (i < 0 ) throw new IllegalArgumentException();
        if (!contains(i)) throw new NoSuchElementException("index is not in the priority queue");
        keys[i] = Car;
        swim(qp[i]);
        sink(qp[i]);
    }

    /**
     * Change the Car associated with index {@code i} to the specified value.
     *
     * @param  i the index of the Car to change
     * @param  Car change the Car associated with index {@code i} to this Car
     * @throws IllegalArgumentException unless {@code 0 <= i < maxN}
     * @deprecated Replaced by {@code changeCar(int, Car)}.
     */
    @Deprecated
    public void change(int i, Car Car) {
        changeKey(i, Car);
    }

    /**
     * Decrease the Car associated with index {@code i} to the specified value.
     *
     * @param  i the index of the Car to decrease
     * @param  Car decrease the Car associated with index {@code i} to this Car
     * @throws IllegalArgumentException unless {@code 0 <= i < maxN}
     * @throws IllegalArgumentException if {@code Car >= CarOf(i)}
     * @throws NoSuchElementException no Car is associated with index {@code i}
     */
    public void decreaseKey(int i, Car Car) {
        if (i < 0 ) throw new IllegalArgumentException();
        if (!contains(i)) throw new NoSuchElementException("index is not in the priority queue");
        if (keys[i].compareTo(Car) <= 0)
            throw new IllegalArgumentException("Calling decreaseCar() with given argument would not strictly decrease the Car");
        keys[i] = Car;
        swim(qp[i]);
    }

    /**
     * Increase the Car associated with index {@code i} to the specified value.
     *
     * @param  i the index of the Car to increase
     * @param  Car increase the Car associated with index {@code i} to this Car
     * @throws IllegalArgumentException unless {@code 0 <= i < maxN}
     * @throws IllegalArgumentException if {@code Car <= CarOf(i)}
     * @throws NoSuchElementException no Car is associated with index {@code i}
     */
    public void increaseKey(int i, Car Car) {
        if (i < 0 ) throw new IllegalArgumentException();
        if (!contains(i)) throw new NoSuchElementException("index is not in the priority queue");
        if (keys[i].compareTo(Car) >= 0)
            throw new IllegalArgumentException("Calling increaseCar() with given argument would not strictly increase the Car");
        keys[i] = Car;
        sink(qp[i]);
    }

    /**
     * Remove the Car associated with index {@code i}.
     *
     * @param  i the index of the Car to remove
     * @throws IllegalArgumentException unless {@code 0 <= i < maxN}
     * @throws NoSuchElementException no Car is associated with index {@code i}
     */
    public void delete(int i) {
        if (i < 0 ) throw new IllegalArgumentException();
        if (!contains(i)) throw new NoSuchElementException("index is not in the priority queue");
        int index = qp[i];
        exch(index, n--);
        swim(index);
        sink(index);
        keys[i] = null;
        qp[i] = -1;
    }
    
    //get minimum for make and model of a car
    public Car minMakeModel(String make, String model) {
    	Car cur;
    	for (int i = 1; i <= n; i++) {
            cur = keys[pq[i]];
            if(cur.getMake().equals(make) && cur.getModel().equals(model)) {
    			return cur;
    		}
    	}
    	
    	return null;
    }


   /***************************************************************************
    * General helper functions.
    ***************************************************************************/
    private boolean greater(int i, int j) {
        return keys[pq[i]].compareTo(keys[pq[j]]) > 0;
    }

    private void exch(int i, int j) {
        int swap = pq[i];
        pq[i] = pq[j];
        pq[j] = swap;
        qp[pq[i]] = i;
        qp[pq[j]] = j;
    }


   /***************************************************************************
    * Heap helper functions.
    ***************************************************************************/
    private void swim(int k) {
        while (k > 1 && greater(k/2, k)) {
            exch(k, k/2);
            k = k/2;
        }
    }

    private void sink(int k) {
        while (2*k <= n) {
            int j = 2*k;
            if (j < n && greater(j, j+1)) j++;
            if (!greater(k, j)) break;
            exch(k, j);
            k = j;
        }
    }


   /***************************************************************************
    * Iterators.
    ***************************************************************************/

    /**
     * Returns an iterator that iterates over the keys on the
     * priority queue in ascending order.
     * The iterator doesn't implement {@code remove()} since it's optional.
     *
     * @return an iterator that iterates over the keys in ascending order
     */
    public Iterator<Integer> iterator() { return new HeapIterator(); }

    private class HeapIterator implements Iterator<Integer> {
        // create a new pq
        private IndexMinPQ copy;

        // add all elements to copy of heap
        // takes linear time since already in heap order so no keys move
        public HeapIterator() {
            copy = new IndexMinPQ(pq.length - 1);
            for (int i = 1; i <= n; i++)
                copy.insert(pq[i], keys[pq[i]]);
        }

        public boolean hasNext()  { return !copy.isEmpty();                     }
        public void remove()      { throw new UnsupportedOperationException();  }

        public Integer next() {
            if (!hasNext()) throw new NoSuchElementException();
            return copy.delMin();
        }
    }


    
}