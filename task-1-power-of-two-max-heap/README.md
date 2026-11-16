# Task 1 – Power of Two Max Heap

## Problem

Implement a max heap in Java where every parent has **2^x children** instead of 2, with `x` passed to the constructor. Only `insert` and `popMax` are required, and performance is critical, since the structure will be benchmarked as a priority queue for a shipping system.

## Design decisions

- **Array-backed storage (`int[]`)** instead of node objects or `ArrayList<Integer>`: no boxing, no per-node overhead, cache-friendly memory layout.
- **Bit shifts instead of multiplication/division.** Because the number of children is a power of two:
  - parent of `i` → `(i - 1) >>> childrenExponent`
  - first child of `i` → `(i << childrenExponent) + 1`
- **"Hole" technique** in sift-up and sift-down: elements are shifted instead of swapped, and the moving value is written only once, which halves the number of array writes.
- **Descriptive naming:** `x` is called `childrenExponent`.
- **Amortized O(1) growth:** capacity doubles when full, with an overflow guard.

## Complexity

With `d = 2^x` children per parent and `n` elements:

| Operation | Time |
|-----------|------|
| `insert` | O(log_d n) |
| `popMax` | O(d · log_d n) |

There is a trade-off: a larger `x` makes the tree shallower, so `insert` gets faster, but `popMax` has to scan more children per level.

## Edge cases handled

| Case | Behaviour |
|------|-----------|
| `x = 0` (1 child per parent) | Works; the heap degenerates into a sorted chain |
| `x < 0` | `IllegalArgumentException` |
| `x > 30` (`1 << 31` overflows `int`) | `IllegalArgumentException` |
| Child index overflow for large `x` | Computed with `long` |
| `popMax` on an empty heap | `NoSuchElementException` |
| Duplicates and negative values | Supported |

## How to run

```bash
javac PowerOfTwoMaxHeap.java Main.java
java Main
```

`Main` inserts 10,000 random values (including negatives and duplicates) for `x ∈ {0, 1, 2, 3, 5, 10, 30}`, pops them all and compares the output against a sorted copy. It also checks the edge cases above.

```
x=0 OK (1 children)
...
x=30 OK (1073741824 children)
Empty heap OK
Negative x OK
Overflowing x OK
```

## Possible improvements

- Make the heap generic (`<T extends Comparable<T>>`) so it can store any comparable object.