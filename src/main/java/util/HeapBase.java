package util;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * A heap-based implementation from ZTL.
 *
 * @param <T> The type of objects managed in this heap
 * @param <C> The comparison class
 *
 * @author Eric
 */
public class HeapBase <T, C extends Comparator<T>> {
	private final C _Compare;
	private final List<T> heap;
	private final int increment;
	private int count;
	
	public HeapBase(C _Compare) throws IllegalArgumentException {
		this(_Compare, 0, 16);
	}
	
	public HeapBase(C _Compare, int estimatedSize, int increment) throws IllegalArgumentException {
		if (_Compare == null) {
			throw new IllegalArgumentException("The heap base cannot be constructed without its comparison class.");
		}
		this.count = 0;
		this.increment = increment;
		this.heap = new ArrayList<>(estimatedSize);
		// template <class T> C<T>::_Compare
		this._Compare = _Compare;
	}
	
	public final int getCount() {
		return count;
	}
	
	public final boolean isEmpty() {
		return count == 0;
	}
	
	public final T get() {
		return heap.get(0);
	}
	
	public void removeHead() {
		heap.set(0, null);
		if (--count > 0) {
			heap.set(0, heap.get(count));
			adjustDownward();
		}
	}
	
	public T insert(final T t) {
		if (heap.size() <= count) {
			int newCount = heap.size() + (increment == 0 ? heap.size() : increment);
			if (newCount == 0) newCount = 4;
			while (newCount-- > 0) {
				heap.add(null);
			}
		}
		heap.set(count++, t);
		adjustUpward();
		return t;
	}
	
	public T getHeadPosition() {
		return count == 0 ? null : heap.get(0);
	}
	
	public T getNext(Pointer<T> pos) {
		if (pos == null || pos.get() == null) {
			return null;
		}
		int u = heap.indexOf(pos.get()) + 1;
		pos.set(u < count ? heap.get(u) : null);
		return pos.get();
	}
	
	public void removeAt(T pos) {
		if (pos == null) return;
		int index = heap.indexOf(pos);
		if (index < 0) return;
		else {
			heap.set(index, null);
		}
		if (--count > 0 && index < count) {
			heap.set(index, heap.get(count));
			adjustDownward(index);
		}
	}
	
	public void updateAt(T pos) {
		if (count == 1) {
			return;
		}
		int index = heap.indexOf(pos);
		if (index < 0) return;
		if (index < --count) {
			heap.set(index, heap.get(count));
			adjustDownward(index);
			heap.set(count, pos);
		}
		count++;
		adjustUpward();
	}
	
	public void removeAll() {
		heap.clear();
		count = 0;
	}
	
	private void swap(int index1, int index2) {
		T t = heap.get(index1);
		heap.set(index1, heap.get(index2));
		heap.set(index2, t);
	}
	
	private int adjust(int parent) {
		int left = parent * 2 + 1;
		if (left >= count) return 0;
		else if (_Compare.compare(heap.get(parent), heap.get(left)) > 0) {
			if (left < count - 1 && _Compare.compare(heap.get(left), heap.get(left + 1)) > 0) {
				left++;
			}
		} else if (++left >= count || _Compare.compare(heap.get(parent), heap.get(left)) < 0) {
			return 0;
		}
		swap(parent, left);
		return left;
	}
	
	@SuppressWarnings("StatementWithEmptyBody")
	private void adjustDownward(int cur) {
		while ((cur = adjust(cur)) > 0);
	}
	
	private void adjustDownward() { adjustDownward(0); }
	
	@SuppressWarnings("StatementWithEmptyBody")
	private void adjustUpward() {
		int cur = count - 1;
		while (cur >= 1 && adjust(cur = (cur - 1) / 2) > 0);
	}
}
