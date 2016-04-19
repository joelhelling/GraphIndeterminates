label_graph.py

Requires: matplotlib, numpy be installed
With python 3.x, can use:
    pip install matplotlib
    pip install numpy

run as:
    python label_graph.py <number of vertices>

Will run the labelling algorithm on random graphs with number of vertices
from 2 to the given number of vertices.

Uncomment the printing lines to debug information, but will make output difficult to 
read for any large outputs.

The current implementation of the labelling algorithm uses bitmaps for both 
finding the intersection of vertex labels and the clique subset. Also, 
dictionaries are loaded at the beginning of the label_graph function
to reduce the call times to the Graph object.
