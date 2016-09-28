import sys
import argparse
import random
import time
import itertools
import math
import numpy as np
import matplotlib.pyplot as plt

class Graph(object):
    def __init__(self, adj):
        self.adj = adj
        self.neighbors = {}
        self.neighbor_num = {}

        for i, row in enumerate(self.adj):
            self.neighbors[i] = []
            self.neighbor_num[i] = int(''.join(map(str,row)),2)
            for j, edge in enumerate(row):
                if edge != 0:
                    self.neighbors[i].append(j)

    def get_vertex_neighbors(self, vertex):
        if vertex in self.neighbors:
            return self.neighbors[vertex]
        return []

    def get_vertex_neighbors_num(self, vertex):
        if vertex in self.neighbor_num:
            return self.neighbor_num[vertex]
        return 0

    def get_neighbors(self):
        return self.neighbors

    def get_neighbors_num(self):
        return self.neighbor_num

    def get_adjacency_matrix(self):
        return self.adj

    def print_graph(self):
        print("Adjacency Matrix: {}".format(self.adj))
        print("Edge Dict: {}".format(self.neighbors))
        print("Neighbor Numbers: {}".format(self.neighbor_num))


# Read a graph from a file generated by generate_graph.py
# return a list of the encoded adjacency matrices
def read_graph(input):
    vertices = 0
    graphs = []

    with open(input, 'r') as in_file:
        vertices = int(in_file.readline())
        graphs = in_file.readline().split(',')
        graphs.pop() # remove the empty element from the trailing comma

    return graphs

def generate_random_encoding(vertices):
    bits = sum(range(vertices))
    edges = random.randint(0,2**bits-1)
    return bin(edges)[2:].zfill(bits)


# decodes an adjacency matrix from the upper diagonal string
def convert_encoding_to_matrix(vertices, enc):
    matrix = [[0 for x in range(vertices)] for y in range(vertices)]

    upp_diag = [(i,j) for i in range(vertices-1) for j in range(i+1, vertices)]

    for n in range(len(enc)):
        i, j = upp_diag[n]
        matrix[i][j] = matrix[j][i] = int(enc[n])
    return matrix

# generates a random graph adjacency matrix that is undirected
def generate_random_adjacency_matrix(vertices):
    adj = np.random.randint(0,2,(vertices,vertices))
    diag = np.diag_indices(vertices)
    upper_tri = np.triu_indices(vertices)
    adj[diag] = 0
    adj.T[upper_tri] = adj[upper_tri]
    return adj

# generatres a random graph adjacency matrix with a given number of edges and vertices
def generate_random_adjacency_matrix_fixed_edges(vertices, edges):
    enc = 0b0
    max_edges = int(vertices*(vertices-1)/2)
    
    if edges > max_edges: 
        edges = max_edges

    added = 0
    while added != edges:
        shift = np.random.randint(0,max_edges)
        if enc & (0b1<<shift) == 0:
            enc = enc | (0b1<<shift)
            added = added + 1

    return convert_encoding_to_matrix(vertices,bin(enc)[2:].zfill(max_edges))

# Takes a label dictionary and creates graph neighbor list dictionary
def make_graph_from_labels(labels):
    check_edges = {i:[] for i in labels.keys()}
    for i in range(len(labels) - 1):
        for j in range(i+1,len(labels)):
            if labels[i] & labels[j] != 0:
                check_edges[i].append(j)
                check_edges[j].append(i)
    return check_edges

# Takes list of a adjacency list and converts it to an adjacency matrix
def convert_to_mat(adj_list):
    adj = []
    for i in range(len(adj_list)):
        row = []
        for j in range(len(adj_list)):
            if j in adj_list[i]:
                row.append(1)
            else:
                row.append(0)
        adj.append(row)
    return adj

# checks that the labelling of a graph is correct by comparing the adjacency
# lists of the original graph to the graph made from the labeling
def check_labelling(graph_labels, edges):
    for vertex, neighbors in graph_labels.items():
        if vertex not in edges or sorted(neighbors) != sorted(edges[vertex]):
            return False
    return True

# count the number of distinct labels in an indeterminate string
def count_symbols(graph_labels):
    symbols = 0b0
    for vertex, labels in graph_labels.items():
        symbols = symbols | labels
    return bin(symbols).count('1')

# count the number of edges in a graph
def count_edges(graph):
    adj_matrix = np.array(graph.get_adjacency_matrix())
    return int(adj_matrix.sum()/2)

# check if all of the members of test are contained in source
def is_subset(test, source):
    for item in test:
        if item not in source:
            return False
    return True

# generates a graph adjacency list such that the graph has the maximum number of edges
# without creating cliques over a given size. For example, a Turan graph of 2 will
# have the maximum number of edges for a given number of vertices without creating a
# clique of size 3. When the max_clique_size == vertices, the graph will be fully 
# connected.
def generate_turan_graph(vertices, max_clique_size):
    if vertices < max_clique_size:
        print("The number of vertices must >= max clique size")
        return {}
    else:
        tgraph = {i:[] for i in range(vertices)}
        parts = [list(range(i,vertices,max_clique_size)) for i in range(max_clique_size)]
        for i in range(len(parts)):
            for val in parts[i]:
                for j in range(len(parts)):
                    if parts[j] != parts[i]:
                        tgraph[val].extend(parts[j])
        return tgraph


# creates a dictionary of labels for each node, which represents an indeterminate string x
# such that G_x = G. The labels are represented with a bitmap to improve performance.
def label_graph(g):
    labels = {i:0b0 for i in range(len(g.get_neighbors()))}
    adj_list = g.get_neighbors()
    vertex_neighbors_num = g.get_neighbors_num()
    num_vertices = len(labels)
    current_symbol = 0

    for v,v_neighbors in adj_list.items():
        if len(v_neighbors) == 0:
            labels[v] = labels[v] | (0b1 << current_symbol)
            current_symbol = current_symbol + 1
        else:
            for w in v_neighbors:
                w_neighbors = adj_list[w]
                if labels[v] & labels[w] == 0:
                    labels[v] = labels[v] | (0b1 << current_symbol)
                    labels[w] = labels[w] | (0b1 << current_symbol)
                    current_clique = 0b1 << ((num_vertices - 1) - w)
                    for q in v_neighbors:
                        if q == w:
                            continue
                        if current_clique & vertex_neighbors_num[q] == current_clique:
                            current_clique = current_clique | 0b1 << ((num_vertices - 1) - q)
                            labels[q] = labels[q] | (0b1 << current_symbol)
                    current_symbol = current_symbol + 1
    return labels

#if __name__ == '__main__':
# Runs label_graph on random graphs of each order (2,vertices-1) and outputs
# a graph of the running time of label_graph in seconds vs number of vertices.
def label_random_graphs(vertices):
    #parser = argparse.ArgumentParser(description='Label random graph with num_vertices vertices.')
    #parser.add_argument('num_vertices',type=int, help='number of vertices in the graph')
    #args = parser.parse_args()

    t_setup = time.clock()
    times = []
    random.seed(time.time())
    #vertices = vars(args)['num_vertices']
    for i in range(2,vertices):
        print("----------------------")
        print("Number of Vertices: {}".format(i))
        adj = generate_random_adjacency_matrix(i).tolist()
        graph = Graph(adj)
        #graph.print_graph()
        print("Setup time: {}".format(time.clock() - t_setup))
        t_label = time.clock()
        labels = label_graph(graph)
        times.append(time.clock() - t_label)
        print("Label time: {}".format(time.clock() - t_label))
        #print("Labelled Graph: {}".format(labels))
        t_check = time.clock()
        graph_labels = make_graph_from_labels(labels)
        is_valid = check_labelling(graph_labels, graph.get_neighbors())
        print("Label check time: {}".format(time.clock() - t_check))
        #print("Graph from Labels: {}".format(graph_labels))
        #print("Labelling is correct: {}".format(is_valid))
        print("Number of edges: {}".format(count_edges(graph)))
        print("Number of symbols: {}".format(count_symbols(labels)))
        print("----------------------")
    plt.plot(list(range(2,vertices)), times)
    plt.xlabel('Number of Vertices', fontsize=14)
    plt.ylabel('Labeling Algorithm Execution Time', fontsize=14)
    plt.show()

# Runs label_graph on all graphs of order <vertices>
def label_all_graphs_order_n(vertices):
    max_labels = 0
    max_graph = None

    num_edges = int(vertices*(vertices-1)/2)
    num_graphs = 2**(num_edges)
    edges_dict = {i:0 for i in range(num_edges+1)}
    total = time.clock()
    for i in range(num_graphs):
        enc = bin(i)[2:].zfill(num_edges)
        mat = convert_encoding_to_matrix(vertices,enc)
        graph = Graph(mat)
        labels = label_graph(graph)
        edges = count_edges(graph)
        num_symbols = count_symbols(labels)
        if num_symbols > edges_dict[edges]:
            edges_dict[edges] = num_symbols
    print("{} vertices time: {}".format(vertices,time.clock()-total))
    print(edges_dict)
    plt.plot(list(edges_dict.keys()), list(edges_dict.values()))
    plt.xlabel('Number of Edges', fontsize=14)
    plt.ylabel('Max Number of Symbols',fontsize=14)
    plt.show()

# Runs label_graph on a random sampling of graphs of order <vertices>
def label_random_matrices_on_n(vertices):
    num_edges = int(vertices*(vertices-1)/2)
    num_graphs = 2*10**5
    edges_dict = {i:0 for i in range(num_edges+1)}
    total = time.clock()
    for i in range(num_graphs):
        mat = generate_random_adjacency_matrix(vertices)
        graph = Graph(mat)
        labels = label_graph(graph)
        edges = count_edges(graph)
        num_symbols = count_symbols(labels)
        if num_symbols > edges_dict[edges]:
            edges_dict[edges] = num_symbols
    print("{} vertices time: {}".format(vertices,time.clock()-total))
    print(edges_dict)
    plt.plot(list(edges_dict.keys()), list(edges_dict.values()))
    plt.xlabel('Number of Edges', fontsize=14)
    plt.ylabel('Max Number of Symbols',fontsize=14)
    plt.show()


# Outputs the number of labels of a Turan Graph without 3-cliques
def label_graphs_without_cliques(vertices):
    adj_list = generate_turan_graph(vertices,2)
    mat = convert_to_mat(adj_list)
    graph = Graph(mat)
    labels = label_graph(graph)
    edges = count_edges(graph)
    num_symbols = count_symbols(labels)

    print("Number of edges: {}".format(edges))
    print("Number of symbols: {}".format(num_symbols))

# Outputs the number of symbols for a graph of order n on a random
# sampling of all numbers of edges
def label_graphs_with_random_edges(vertices):
    max_edges = int(vertices*(vertices-1)/2)
    edges_dict = {i:0 for i in range(max_edges+1)}

    sample_size = 10
    total = time.clock()
    for i in range(max_edges+1):
        for j in range(sample_size):
            mat = generate_random_adjacency_matrix_fixed_edges(vertices,i)
            graph = Graph(mat)
            labels = label_graph(graph)
            num_symbols = count_symbols(labels)
            if num_symbols > edges_dict[i]:
                edges_dict[i] = num_symbols
        print("...{0:.0f}%".format(i/(max_edges+1) * 100))
    print("{} vertices time: {}".format(vertices,time.clock()-total))
    print(edges_dict)
    plt.plot(list(edges_dict.keys()), list(edges_dict.values()))
    plt.xlabel('Number of Edges', fontsize=14)
    plt.ylabel('Max Number of Symbols',fontsize=14)
    plt.show()
    
# Outputs an average running time for random graphs in the range 
# 5 to the given number of vertices
def label_random_graph_time(vertices):
    times = {i:0 for i in range(5,vertices+1)}
    sample_size = 100
    for i in range(5,vertices+1):
        print("Number of Vertices: {}".format(i))
        for j in range(sample_size):
            adj = generate_random_adjacency_matrix(i).tolist()
            graph = Graph(adj)
            t_label = time.clock()
            labels = label_graph(graph)
            times[i] += time.clock() - t_label
    avg_times = {k: v/sample_size for k,v in times.items()}
    with open('AverageTimes.txt','w') as out:
        out.write("Vertices:Average Time\n")
        for k,v in avg_times.items():
            out.write("{}: {}\n".format(k,v))

    run_time = []
    for i in range(5,int((vertices+1)/2)):
        j = 2*i
        run_time.append((i,avg_times[j]/avg_times[i]))
    
    
    #plt.plot([i[0] for i in run_time],[i[5] for i in run_time])
    #plt.plot([i[0] for i in run_time],[i[2] for i in run_time])
    #plt.plot([i[0] for i in run_time],[i[3] for i in run_time])
    #plt.plot([i[0] for i in run_time],[i[4] for i in run_time])
    plt.plot([i[0] for i in run_time],[i[1] for i in run_time])
    #plt.legend(['T(n+1)/T(n)','r','r^2','r^3'], loc='upper right')
    plt.xlabel('Number of Vertices', fontsize=14)
    plt.ylabel('Worst Case Estimate', fontsize=14)
    plt.yticks([2**x for x in range(1,4)],['O(n)','O(n^2)','O(n^3)'])
    plt.show()
    
    #plt.plot(list(avg_times.keys()), list(avg_times.values()))
    #plt.xlabel('Number of Vertices', fontsize=14)
    #plt.ylabel('Average Running Time (seconds)', fontsize=14)
    #plt.show()

# Outputs the average number of labels for random graphs in the range
# 2 to given number of vertices
def label_graphs_average_symbols(vertices):
    symbols = {i:0 for i in range(2,vertices+1)}
    sample_size = 100
    for i in range(2,vertices+1):
        print("Number of Vertices: {}".format(i))
        for j in range(sample_size):
            adj = generate_random_adjacency_matrix(i).tolist()
            graph = Graph(adj)
            labels = label_graph(graph)
            symbols[i] += count_symbols(labels)
    avg_labels = {k: v/sample_size for k,v in symbols.items()}
    with open('AverageLabels.txt','w') as out:
        out.write("Vertices:Average Number of Symbols\n")
        for k,v in avg_labels.items():
            out.write("{}: {}\n".format(k,v))

    plt.plot(list(avg_labels.keys()), list(avg_labels.values()))
    plt.xlabel('Number of Vertices', fontsize=14)
    plt.ylabel('Average Number of Labels', fontsize=14)
    plt.show()

if __name__ == '__main__':
    label_all_graphs_order_n(7)

# Bill's graph of 3-cliques
#{   0: [1, 2, 4, 5, 7, 8],
#    1: [0, 2, 3, 5, 6, 8],
#    2: [0, 1, 3, 4, 6, 7],
#    3: [1, 2, 4, 5, 7, 8],
#    4: [0, 2, 3, 5, 6, 8],
#    5: [0, 1, 3, 4, 6, 7],
#    6: [1, 2, 4, 5, 7, 8],
#    7: [0, 2, 3, 5, 6, 8],
#    8: [0, 1, 3, 4, 6, 7]}
# 1: a,b,c,d,e,l,n,p,s
# 2: a,c,e,f,g,i,k,o,r
# 3: a,b,d,f,g,h,j,m,q
# 4: f,h,i,j,k
# 5: b,h,l,m,n
# 6: c,i,l,o,p
# 7: g,m,o,q,r
# 8: d,j,p,q,s
# 9: e,k,n,r,s
    