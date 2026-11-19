# Algorithm Visualizer (Java, Swing)

Animates sorting and searching algorithms step by step, with live counts of comparisons and writes.
No libraries are needed: Swing ships with the JDK. Works on Java 8 or newer.

## Run

```
javac -d out *.java
java -cp out Main            # the visualizer window
java -cp out AlgorithmTest   # correctness tests
java -cp out Benchmark       # comparison counts for every sort
java -cp out WebServer       # browser version at http://localhost:8080 (run from this folder)
```

> **Linux note:** if `java -cp out Main` fails with `HeadlessException`, your default `java` is a
> headless JRE (common on Fedora). Install the full JDK (`sudo dnf install java-latest-openjdk`)
> or run it directly with `/usr/lib/jvm/java-latest-openjdk/bin/java -cp out Main`.
> If you see `UnsupportedClassVersionError`, your `javac` is newer than your `java`; compile with
> `javac --release 17 -d out *.java`.

## Browser version

`WebServer.java` uses the JDK's built-in HTTP server, so no extra libraries are needed. It runs the same
`Algorithm` classes, sends the recorded `Step`s as JSON, and `web/index.html` draws them on a canvas.
The algorithm classes didn't change at all to add this second view.

## Controls

| Control | What it does |
| --- | --- |
| Algorithm box | Bubble, Selection, Insertion, Merge, Quick sort; Linear and Binary search |
| Array type box | Random, nearly sorted, reversed, few unique values |
| Size / Speed | Number of bars (5 to 100) and animation speed |
| Target | Value to search for (blank = a random value from the array) |
| Start / Pause / Resume | Plays the recorded steps |
| Prev / Next | Step backwards or forwards one frame |
| Reset / New array | Replay the same array, or generate a new one |

Colours: yellow = being compared, red = swap or write, purple = pivot or middle element,
light blue = active range, green = sorted or found.

## Class design

```
Algorithm (abstract)            Step, Role           ArrayModel
 |- SortingAlgorithm (abstract)   (one frame)          (the data)
 |   |- BubbleSort
 |   |- SelectionSort           AlgorithmFactory      VisualizerFrame
 |   |- InsertionSort             (creates objects      (Swing window,
 |   |- MergeSort                  from names)           talks only to Algorithm)
 |   '- QuickSort
 '- SearchingAlgorithm (abstract)
     |- LinearSearch
     '- BinarySearch
```

## Add your own algorithm

1. Create `MyAlgorithm.java` extending `SortingAlgorithm` (or `SearchingAlgorithm`).
2. Pass its name and complexities to `super(...)`, and implement `execute(int[] a, int target)`.
   Change `a` in place and call `frame(a, "message").mark(Role.COMPARE, i, j).save()` after each action.
3. Add `add(MyAlgorithm::new);` in `AlgorithmFactory`. It appears in the window automatically.
