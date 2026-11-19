# Algorithm Visualizer (Java)

Animates 12 sorting and 5 searching algorithms step by step, with live counts of comparisons and writes,
and highlights the line of pseudocode being run. **Race mode** runs two algorithms side by side on the same array.
It has two views on the same algorithm classes: a Swing desktop window and a browser page on localhost.
No libraries are needed (Swing and the HTTP server ship with the JDK). Needs Java 11 or newer.

## Build and run

Linux / macOS:
```
javac -d out $(find src test -name "*.java")
```
Windows (PowerShell):
```
javac -d out (Get-ChildItem -Recurse -Filter *.java src,test).FullName
```
Then, from this folder:
```
java -cp out visualizer.Main             # desktop window
java -cp out visualizer.web.WebServer    # browser version at http://localhost:8080
java -cp out visualizer.AlgorithmTest    # correctness tests
java -cp out visualizer.Benchmark        # comparison counts for every sort
```

> **Linux note:** if the window fails with `HeadlessException`, your default `java` is a headless
> runtime (common on Fedora). Install the full JDK (`sudo dnf install java-latest-openjdk`) or run
> `/usr/lib/jvm/java-latest-openjdk/bin/java -cp out visualizer.Main`.
> If you see `UnsupportedClassVersionError`, your `javac` is newer than your `java`; add `--release 11` to the `javac` command.

## Algorithms

| Group | Algorithm | Time (best / average / worst) | Space | Stable |
| --- | --- | --- | --- | --- |
| Simple sorts | Bubble Sort | O(n) / O(n²) / O(n²) | O(1) | yes |
| | Cocktail Shaker Sort | O(n) / O(n²) / O(n²) | O(1) | yes |
| | Selection Sort | O(n²) / O(n²) / O(n²) | O(1) | no |
| | Insertion Sort | O(n) / O(n²) / O(n²) | O(1) | yes |
| Fast sorts | Shell Sort (Knuth gaps) | O(n log n) / ~O(n^1.25) / O(n^1.5) | O(1) | no |
| | Merge Sort | O(n log n) all cases | O(n) | yes |
| | Heap Sort | O(n log n) all cases | O(1) | no |
| | Quick Sort: last, median-of-3 or random pivot | O(n log n) / O(n log n) / O(n²) | O(log n) | no |
| Non-comparison sorts | Counting Sort | O(n + k) | O(n + k) | yes |
| | Radix Sort (LSD, base 10) | O(d(n + b)) | O(n + b) | yes |
| Searches | Linear Search (any order) | O(1) / O(n) / O(n) | O(1) | |
| | Binary Search | O(1) / O(log n) / O(log n) | O(1) | |
| | Jump Search | O(1) / O(√n) / O(√n) | O(1) | |
| | Interpolation Search | O(1) / O(log log n) / O(n) | O(1) | |
| | Exponential Search | O(1) / O(log i) / O(log n) | O(1) | |

All searches except Linear Search run on a sorted copy of the array.

## Controls (desktop window)

| Control | What it does |
| --- | --- |
| Algorithm box | Any of the 17 algorithms above |
| vs box | Pick a second algorithm of the same kind to race it on the same array (or "(no race)") |
| Array type box | Random, nearly sorted, reversed, few unique values |
| Size / Speed | Number of bars (5 to 100) and animation speed |
| Target | Value to search for (blank = a random value from the array). The run updates as you type. |
| Start / Pause / Resume | Plays the recorded steps |
| Prev / Next | Step backwards or forwards one frame |
| Reset / New array | Replay the same recording from the start, or generate a new array |
| Your values + Use values | Sort or search your own numbers, e.g. `5, 3, 8, 1` (2 to 100 whole numbers from 0 to 999, commas or spaces). Enter also works. Mistakes are shown in red. Changing the array type or size, or New array, goes back to generated arrays. |

The pseudocode panel next to the bars highlights the line each frame belongs to.
In a race both algorithms share one clock, so the one with fewer steps finishes first; the bar at the
bottom announces the winner with its step, comparison and write counts.

The browser version has the same features, and its URL can preselect a race, for example
`http://localhost:8080/?algo=Bubble%20Sort&vs=Merge%20Sort&kind=REVERSED&size=40`, or with your own numbers `...&values=5,3,8,1`.

Colours: yellow = being compared, red = swap or write, purple = pivot, middle or heap parent,
light blue = active range, green = sorted or found.

## Project layout

```
src/visualizer/
  Main.java, Benchmark.java
  model/        Role, Step (immutable frame), ArrayModel (the data)
  algorithms/   Algorithm (abstract), SortingAlgorithm, SearchingAlgorithm, AlgorithmFactory
    sorting/    the 12 sorts, DistributionSort, PivotStrategy + 3 pivot rules
    searching/  the 5 searches
  playback/     Player (plays steps, notifies listeners), PlaybackListener (observer), Race (who won)
  ui/           VisualizerFrame (window/controller), AlgorithmView (one algorithm), ArrayPanel, PseudocodePanel
  web/          WebServer (JSON API + serves web/index.html)
test/visualizer/AlgorithmTest.java
web/index.html
```

## Class design

```
Algorithm (abstract, Template Method: run() -> execute())
 |- SortingAlgorithm (abstract: less, swap, write, finish, isStable)
 |   |- BubbleSort, CocktailShakerSort, SelectionSort, InsertionSort
 |   |- ShellSort, MergeSort, HeapSort
 |   |- QuickSort ----has-a----> PivotStrategy (interface, Strategy pattern)
 |   |                             |- LastPivot
 |   |                             |- MedianOfThreePivot
 |   |                             '- RandomPivot
 |   '- DistributionSort (abstract: countingPass)
 |       |- CountingSort
 |       '- RadixSort
 '- SearchingAlgorithm (abstract: compare, found, notFound, binarySearch)
     |- LinearSearch, BinarySearch, JumpSearch
     '- InterpolationSearch, ExponentialSearch

AlgorithmFactory (Factory + registry)     Step, Role, ArrayModel (model)

Player ----notifies----> PlaybackListener (interface, Observer pattern)
                           '- AlgorithmView (bars + counters + PseudocodePanel)
VisualizerFrame: 1 Player + 1 AlgorithmView, or 2 of each in race mode, one shared clock
WebServer: same Algorithm classes, sends steps + pseudocode as JSON to web/index.html
```

| OOP idea | Where |
| --- | --- |
| Abstraction | `Algorithm` declares `execute()` and `pseudocode()`; the views only use the `Algorithm` type |
| Encapsulation | All fields in `Algorithm` are private, changed only through protected helpers; `Step` is immutable; `run()` returns a read-only list |
| Inheritance | Three levels: `Algorithm` → `SortingAlgorithm` → `DistributionSort` → `RadixSort` |
| Polymorphism | `needsTarget()` and `complexity()` are overridden; the GUI never checks which algorithm it has |
| Interface | `PivotStrategy` with three implementations; `PlaybackListener` implemented by `AlgorithmView`; `PivotStrategy.Less` is a functional interface |
| Composition | `QuickSort` has a `PivotStrategy` instead of three QuickSort subclasses |
| Design patterns | Template Method, Strategy, Factory, Observer (`Player` → `PlaybackListener`), Builder (`frame(...).at(line).mark(...).save()`), MVC-style split |
| Exceptions | `IllegalArgumentException` for unknown names, unsorted input to a search, and typed values that aren't valid; the GUI catches it and shows the message |

## Add your own algorithm

1. Create a class in `algorithms/sorting` extending `SortingAlgorithm` (or in `algorithms/searching` extending `SearchingAlgorithm`).
2. Pass its name and complexities to `super(...)`, return your pseudocode lines from `pseudocode()`,
   and implement `execute(int[] a, int target)`. Change `a` with `swap`/`write`, compare with `less`
   (or `compare` for searches), and call `frame(a, "message").at(line).mark(Role.COMPARE, i, j).save()`
   after each action, where `line` is the 1-based pseudocode line it belongs to.
3. Add `add(MyAlgorithm::new);` in `AlgorithmFactory`. It appears in both views and in the tests automatically.
