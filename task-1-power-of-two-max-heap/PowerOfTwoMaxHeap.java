import java.util.Arrays;
import java.util.NoSuchElementException;

public class PowerOfTwoMaxHeap {

    private static final int MAX_CHILDREN_EXPONENT = 30; // 1 << 31 taşar
    private static final int DEFAULT_CAPACITY = 16;

    private final int childrenExponent;   // her ebeveynin 2^childrenExponent çocuğu var
    private final int childrenPerParent;  // 2^childrenExponent
    private int[] heap;
    private int size;

    public PowerOfTwoMaxHeap(int childrenExponent) {
        if (childrenExponent < 0 || childrenExponent > MAX_CHILDREN_EXPONENT) {
            throw new IllegalArgumentException(
                    "childrenExponent must be between 0 and " + MAX_CHILDREN_EXPONENT);
        }
        this.childrenExponent = childrenExponent;
        this.childrenPerParent = 1 << childrenExponent;
        this.heap = new int[DEFAULT_CAPACITY];
        this.size = 0;
    }

    public void insert(int value) {
        if (size == heap.length) {
            grow();
        }
        int index = size++;
        while (index > 0) {
            int parentIndex = (index - 1) >>> childrenExponent;
            int parentValue = heap[parentIndex];
            if (parentValue >= value) {
                break;
            }
            heap[index] = parentValue; // ebeveyni aşağı indir
            index = parentIndex;
        }
        heap[index] = value;           // değeri tek seferde yerine yaz
    }

    public int popMax() {
        if (size == 0) {
            throw new NoSuchElementException("Heap is empty");
        }
        int max = heap[0];
        int last = heap[--size];
        if (size > 0) {
            siftDown(last);
        }
        return max;
    }

    private void siftDown(int value) {
        int index = 0;
        while (true) {
            long firstChild = ((long) index << childrenExponent) + 1; // taşmaya karşı long
            if (firstChild >= size) {
                break; // yaprak düğüm
            }
            int end = (int) Math.min(firstChild + childrenPerParent, size);
            int maxChildIndex = (int) firstChild;
            int maxChildValue = heap[maxChildIndex];
            for (int child = maxChildIndex + 1; child < end; child++) {
                if (heap[child] > maxChildValue) {
                    maxChildIndex = child;
                    maxChildValue = heap[child];
                }
            }
            if (maxChildValue <= value) {
                break;
            }
            heap[index] = maxChildValue; // en büyük çocuğu yukarı çek
            index = maxChildIndex;
        }
        heap[index] = value;
    }

    private void grow() {
        int newCapacity = heap.length << 1;
        if (newCapacity < 0 || newCapacity > Integer.MAX_VALUE - 8) {
            if (heap.length >= Integer.MAX_VALUE - 8) {
                throw new IllegalStateException("Heap capacity exceeded");
            }
            newCapacity = Integer.MAX_VALUE - 8;
        }
        heap = Arrays.copyOf(heap, newCapacity);
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }
}