
public class MinHeap<T extends Comparable<T>> implements MinHeapInterface<T> {
	T[] heap;
	int n = 0;

	public MinHeap(int capacity) {
		heap = (T[]) new Comparable[capacity + 1]; // index 0 is dummy
	}
	
	public MinHeap(T[] arr) {
	
		heap = (T[]) new Comparable[arr.length+1];
		for(int i = 0 ; i < arr.length ; i++) {
			heap[i+1] = arr[i];
		}
		n = arr.length ;
		heapify();
	}

	private void heapify() {
		for(int i  = n/2 ; i >= 1 ; i--) {
			sink(i);
		}
	}

	private void swim(int k) {
		while (k > 1 && greater(k / 2, k)) {
			exch(k, k / 2);
			k = k / 2;
		}

	}

	private void sink(int k) {
	    while (2 * k <= n) {
	        int j = 2 * k;
	        if (j < n && greater(j, j + 1))
	            j++;
	        if (less(k, j))
	            break;
	        exch(k, j);
	        k = j;
	    }
	}

	private boolean less(int i, int j) {
		return heap[i].compareTo(heap[j]) < 0;
	}
	
	private boolean greater(int i, int j) {
		return heap[i].compareTo(heap[j]) > 0;
	}

	@Override
	public void add(T x) {
		if(n > heap.length-2) {
			System.out.println("Heap is at capacity");
			return;
		}
	    heap[++n] = x;
	    swim(n);
	}

	@Override
	public T removeMin() {
		if(isEmpty()) return null;
		T min = heap[1];
		exch(1, n--);
		sink(1);
		return min;
	}

	@Override
	public T getMin() {
		return heap[1];
	}

	@Override
	public boolean isEmpty() {
		return n == 0;
	}

	@Override
	public int getSize() {
		return n;
	}

	private void exch(int i, int j) {
		T t = heap[i];
		heap[i] = heap[j];
		heap[j] = t;
	}

	@Override
	public void clear() {
	    heap = (T[]) new Comparable[heap.length];
	    n = 0;
	}
	
	public void heapSort() {
		for(int i = 1 ; i < heap.length ; i++) {
			removeMin();
		}
		int j = heap.length-1;
		for(int i = 1 ; i <= j ; i++ , j--) {
			exch(i, j);
		}
	}
	
	public String toString() {
		String out = "";
		for(int i = 1 ; i < heap.length ; i++) {
			out += heap[i]+" ";
		}
		return out;
	}
}
