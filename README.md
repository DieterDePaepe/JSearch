JSearch
=======

JSearch is a java framework for solving [state space problems](http://en.wikipedia.org/wiki/State_space_search). It focuses on the following aspects:

1. Proper documentation
2. Clean and tested code
3. Reusability
4. Performance

Various AI problem solving techniques are bundled, suited for varying types of problems:
- A*
- Beamsearch
- Depth first search
- IDA* (iterative deepening A*)
- Iterative deepening search
- Recursive best first search
- SMA* (simplified memory-bounded A*)

 
Installation
============

You can use JSearch as a Maven dependency:
``` xml
<dependency>
   <groupId>com.github.dieterdepaepe</groupId>
   <artifactId>jsearch-core</artifactId>
   <version>1.0.0</version>
</dependency>
<!-- Contains examples, not needed for actual usage -->
<dependency>
   <groupId>com.github.dieterdepaepe</groupId>
   <artifactId>jsearch-examples</artifactId>
   <version>1.0.0</version>
</dependency>
```

Or build it yourself:
``` bash
git clone https://github.com/DieterDePaepe/JSearch.git
cd JSearch
# optional: checkout a version
# git checkout 1.0.0
mvn package
# jars are found in the target folders
```


Usage
=====

It is up to the user to define the details of the problem to be solved. This comes down to creating implementations for the following interfaces (more details available in the javadoc):
- SearchNode: the representation of all information relating to the problem for that state
- Problem environment (no interface): the representation of all information shared between all states
- SearchNodeGenerator: defines the possible transitions from one state to another
- Heuristic (optional but recommended): an estimator of the remaining cost for a state

The examples folder contains implementations for the [N-Puzzle](http://en.wikipedia.org/wiki/N-puzzle) problem.

Once defined, you pick an algorithm (an implementation of ```Solver```) and start the search using the ```Solvers``` helper class. For examples, refer to the examples folder.


Alternative Libraries
=====================

[OptaPlanner](http://www.optaplanner.org/) is another optimisation library that is definitely worth checking out. It is more extensive than JSearch and more actively developed. A short comparison is given:
- JSearch focusses on constructive search, so it more suited for problems where the search path is important (Eg: pathfinding in a graph, finding the best way to solve a Rubics Cube,...) or where a garanteed optimal solution is needed.
- OptaPlanner focusses on local search, it is more suited for problems with many variables or problems where it is difficult to determine the quality of a partial solution. It is definitely more scalable than JSearch for large, complex problems.
