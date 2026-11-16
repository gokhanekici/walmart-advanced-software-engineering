import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.Random;

public class Main {
    public static void main(String[] args) {
        int[] exponents = {0, 1, 2, 3, 5, 10, 30};
        Random random = new Random(42);

        for (int exponent : exponents) {
            PowerOfTwoMaxHeap heap = new PowerOfTwoMaxHeap(exponent);
            int n = 10_000;
            int[] values = new int[n];
            for (int i = 0; i < n; i++) {
                values[i] = random.nextInt(2001) - 1000;
                heap.insert(values[i]);
            }
            Arrays.sort(values);
            for (int i = n - 1; i >= 0; i--) {
                int popped = heap.popMax();
                if (popped != values[i]) {
                    throw new AssertionError("Hata! exponent=" + exponent);
                }
            }
            System.out.println("x=" + exponent + " OK (" + (1 << exponent) + " çocuk)");
        }

        // Kenar durumlar
        try { new PowerOfTwoMaxHeap(0).popMax(); }
        catch (NoSuchElementException e) { System.out.println("Empty OK"); }

        try { new PowerOfTwoMaxHeap(-1); }
        catch (IllegalArgumentException e) { System.out.println("Negative x OK"); }

        try { new PowerOfTwoMaxHeap(31); }
        catch (IllegalArgumentException e) { System.out.println("Overflowing x OK"); }
    }
}