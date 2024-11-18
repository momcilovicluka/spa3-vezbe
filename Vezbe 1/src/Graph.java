import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

public class Graph {

	private final List<Set<Integer>> adjacencyList;
	private int vertexCount;
	private int edgeCount;

	public Graph(int vertexCount) {
        this.vertexCount = vertexCount;
        this.edgeCount = 0;
        this.adjacencyList = initializeAdjacencyList(vertexCount);
    }

    private List<Set<Integer>> initializeAdjacencyList(int vertexCount) {
        List<Set<Integer>> list = new ArrayList<>(vertexCount);
        for (int i = 0; i < vertexCount; i++)
            list.add(new HashSet<>());
        return list;
    }

    public static Graph loadFromFile(String filePath) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            int vertices = Integer.parseInt(reader.readLine());
            int edges = Integer.parseInt(reader.readLine());
            Graph graph = new Graph(vertices);
            graph.addEdgesFromFile(reader, edges);
            return graph;
        }
    }

    private void addEdgesFromFile(BufferedReader reader, int edges) throws IOException {
        for (int i = 0; i < edges; i++) {
            String[] edge = reader.readLine().split(" ");
            addEdge(Integer.parseInt(edge[0]), Integer.parseInt(edge[1]));
        }
    }

	public int getVertexCount() {
		return vertexCount;
	}

	public int getEdgeCount() {
		return edgeCount;
	}

	public Set<Integer> getNeighbors(int vertex) {
		return Collections.unmodifiableSet(adjacencyList.get(vertex));
	}

	public void addEdge(int u, int v) {
		if (u < vertexCount && v < vertexCount && adjacencyList.get(u).add(v)) {
			adjacencyList.get(v).add(u);
			edgeCount++;
		}
	}

	private void dfs(int vertex, boolean[] visited, Set<Integer> component) {
		visited[vertex] = true;
		component.add(vertex);
		
		for (int neighbor : adjacencyList.get(vertex))
			if (!visited[neighbor])
				dfs(neighbor, visited, component);
	}

	public Set<Integer> findComponent(int startVertex) {
		Set<Integer> component = new HashSet<>();
		boolean[] visited = new boolean[vertexCount];
		dfs(startVertex, visited, component);
		return component;
	}

	public List<Set<Integer>> findAllComponents() {
		List<Set<Integer>> components = new ArrayList<>();
		boolean[] visited = new boolean[vertexCount];
		
		for (int vertex = 0; vertex < vertexCount; vertex++)
			if (!visited[vertex]) {
				Set<Integer> component = new HashSet<>();
				dfs(vertex, visited, component);
				components.add(component);
			}

		return components;
	}

	public boolean pathExists(int start, int end) {
		boolean[] visited = new boolean[vertexCount];
		dfs(start, visited, new HashSet<>());
		return visited[end];
	}

	public List<Integer> findAnyPath(int start, int end) {
		List<Integer> path = new ArrayList<>();
		
		if (findPathRecursive(start, end, new boolean[vertexCount], path))
			Collections.reverse(path);

		return path;
	}

	private boolean findPathRecursive(int current, int target, boolean[] visited, List<Integer> path) {
		if (current == target) {
			path.add(current);
			return true;
		}
		
		visited[current] = true;
		for (int neighbor : adjacencyList.get(current))
			if (!visited[neighbor] && findPathRecursive(neighbor, target, visited, path)) {
				path.add(current);
				return true;
			}

		return false;
	}

	public Set<Integer> iterativeDFS(int startVertex) {
		Set<Integer> component = new HashSet<>();
		boolean[] visited = new boolean[vertexCount];
		Deque<Integer> stack = new ArrayDeque<>();
		
		stack.push(startVertex);

		while (!stack.isEmpty()) {
			int vertex = stack.pop();
			
			if (!visited[vertex]) {
				visited[vertex] = true;
				component.add(vertex);
				
				for (int neighbor : adjacencyList.get(vertex))
					if (!visited[neighbor])
						stack.push(neighbor);
			}
		}
		
		return component;
	}

	public Map<Integer, Integer> bfsDistances(int startVertex) {
		Map<Integer, Integer> distances = new HashMap<>();
		Queue<Integer> queue = new ArrayDeque<>();
		boolean[] visited = new boolean[vertexCount];
		
		queue.add(startVertex);
		distances.put(startVertex, 0);
		visited[startVertex] = true;

		while (!queue.isEmpty()) {
			int vertex = queue.poll();
			int distance = distances.get(vertex);

			for (int neighbor : adjacencyList.get(vertex))
				if (!visited[neighbor]) {
					queue.add(neighbor);
					visited[neighbor] = true;
					distances.put(neighbor, distance + 1);
				}

		}
		return distances;
	}

	public boolean hasCycles() {
		boolean[] visited = new boolean[vertexCount];
		
		for (int vertex = 0; vertex < vertexCount; vertex++)
			if (!visited[vertex] && hasCycle(vertex, -1, visited))
				return true;

		return false;
	}

	private boolean hasCycle(int vertex, int parent, boolean[] visited) {
		visited[vertex] = true;
		
		for (int neighbor : adjacencyList.get(vertex))
			if (!visited[neighbor]) {
				if (hasCycle(neighbor, vertex, visited))
					return true;

			} else if (neighbor != parent)
				return true;

		return false;
	}
	
	public List<Integer> findAnyCycle() {
	    boolean[] visited = new boolean[vertexCount];
	    List<Integer> path = new ArrayList<>();

	    for (int vertex = 0; vertex < vertexCount; vertex++) 
	        if (!visited[vertex]) 
	            if (hasCyclePath(vertex, -1, visited, path)) {
	                Collections.reverse(path);
	                return path;
	            }
	    
	    return Collections.emptyList();
	}

	private boolean hasCyclePath(int current, int parent, boolean[] visited, List<Integer> path) {
	    visited[current] = true;
	    path.add(current);

	    for (int neighbor : adjacencyList.get(current)) {
	        if (!visited[neighbor]) {
	            if (hasCyclePath(neighbor, current, visited, path)) 
	                return true;
	        } else if (neighbor != parent) {
	            path.add(neighbor);
	            return true;
	        }
	    }
	    
	    path.remove(path.size() - 1);
	    return false;
	}

	public static void main(String[] args) throws IOException {
		Graph graph = Graph.loadFromFile("tinyG.txt");
		System.out.println("Vertex Count: " + graph.getVertexCount());
		System.out.println("Edge Count: " + graph.getEdgeCount());
		
		System.out.println("Find component for 0: " + graph.findComponent(0));

		System.out.println("Components:");
		for (Set<Integer> component : graph.findAllComponents()) {
			System.out.println(component);
		}

		int start = 0, end = 3;
		System.out.println("Path Exists from " + start + " to " + end + ": " + graph.pathExists(start, end));
		System.out.println("Any Path from " + start + " to " + end + ": " + graph.findAnyPath(start, end));

		System.out.println("BFS Distances from " + start + ": " + graph.bfsDistances(start));
		System.out.println("Graph has cycles: " + graph.hasCycles());
		System.out.println("Any Cycle: " + graph.findAnyCycle());
	}
}
