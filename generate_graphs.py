# Author: Joel Helling
# Generate all of the possible upper diagonal adjacency matricies for undirected graphs
# with the given number of vertices. Write the output to a given file.
#
# Data is stored in the form:
# <number of vertices>
# <upper_diagonal_1>,<upper_diagonal_2>,...,<upper_diagonal_n>
#
# The upper diagonal of the matrix is stored as a one dimensional string
# <upper_diagonal> = '#'^sum(1..n-1) where # is either a 1 or a 0
#
#  For Example: 101 encodes the matrix  0 1 0
#                                       1 0 1
#                                       0 1 0

import sys

def generate_graphs(vertices, output):
    num_graphs = 1
    num_edges = sum(range(vertices))
    graph_encodings = [bin(x)[2:].zfill(num_edges) for x in range(2**num_edges)]

    with open(output, 'w') as out:
        out.write('{}\n'.format(vertices))
        for i in graph_encodings:
            out.write(i + ',')


if __name__ == '__main__':
    if len(sys.argv) != 3:
        print("Usage: generate_graphs.py <number_of_vertices> <output_file>")
        sys.exit(1)

    generate_graphs(int(sys.argv[1]), sys.argv[2])
