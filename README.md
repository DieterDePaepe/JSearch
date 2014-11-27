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

You can use JSearch as a Maven dependency (coming soon):
``` xml
<dependency>
   <groupId>com.github.dieterdepaepe</groupId>
   <artifactId>jsearch-core</artifactId>
   <version>0.1</version>
</dependency>
<!-- Contains examples, not needed for actual usage -->
<dependency>
   <groupId>com.github.dieterdepaepe</groupId>
   <artifactId>jsearch-examples</artifactId>
   <version>0.1</version>
</dependency>
```

Or build it yourself:
``` bash
git clone https://github.com/DieterDePaepe/JSearch.git
cd JSearch
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




