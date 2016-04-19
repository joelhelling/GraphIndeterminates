# Graph Indeterminates

Provides an python implementation of a graph labelling algorithm that takes an undirected graph 
and outputs the labels that will be assigned to each vertex in the graph such that there
is an edge between vertices if and only if both endpoints of the graph have a label in common.

## Usage

Requires numpy and matplotlib. The code currently functions as a library that can be imported and
into other code or a REPL session.

## Implementation

The current implementation of the algorithm uses bitmaps to speed up the checks on shared labels and 
neighbors. The labels in the output dictionary that maps the vertices to labels should have the values 
converted to binary output to show which labels the vertex has been assigned.
