"""
A graph data structure that stores vertices and their connections with distances.
"""


class Graph:
    def __init__(self):
        self.vertices = {}

    # O(1)
    def add_vertex(self, vertex):
        if vertex not in self.vertices:
            self.vertices[vertex] = {}

    # O(1)
    def add_edge(self, v1, v2, distance):
        self.add_vertex(v1)
        self.add_vertex(v2)
        self.vertices[v1][v2] = distance
        self.vertices[v2][v1] = distance

    # O(1)
    def get_distance(self, v1, v2):
        if v1 in self.vertices and v2 in self.vertices[v1]:
            return float(self.vertices[v1][v2])
        else:
            return "Distance not found"
