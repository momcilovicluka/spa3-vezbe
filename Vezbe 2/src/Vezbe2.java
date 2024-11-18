import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Scanner;

import edu.princeton.cs.algs4.Digraph;

class BipartiteCheck {
	public static boolean isBipartite(ArrayList<Integer>[] graph) {
		int n = graph.length;
		int[] colors = new int[n];

		for (int start = 0; start < n; start++) {
			if (colors[start] != 0)
				continue;

			Queue<Integer> queue = new LinkedList<>();
			queue.add(start);
			colors[start] = 1;

			while (!queue.isEmpty()) {
				int node = queue.poll();

				for (int neighbor : graph[node])
					if (colors[neighbor] == 0) {
						colors[neighbor] = -colors[node];
						queue.add(neighbor);
					} else if (colors[neighbor] == colors[node])
						return false;

			}
		}
		return true;
	}

	@SuppressWarnings("unchecked")
	public static ArrayList<Integer>[] loadGraphFromFile(String filename) throws FileNotFoundException {
		Scanner scanner = new Scanner(new File(filename));

		int vertices = scanner.nextInt();
		int edges = scanner.nextInt();

		ArrayList<Integer>[] graph = new ArrayList[vertices];
		for (int i = 0; i < vertices; i++)
			graph[i] = new ArrayList<>();

		for (int i = 0; i < edges; i++) {
			int v = scanner.nextInt();
			int w = scanner.nextInt();
			graph[v].add(w);
			graph[w].add(v);
		}

		scanner.close();
		return graph;
	}
}

public class Vezbe2 {
	public static int countComponents(Digraph digraph) {
		return listComponents(digraph).size();
	}

	public static List<List<Integer>> listComponents(Digraph digraph) {
		boolean[] visited = new boolean[digraph.V()];
		List<List<Integer>> components = new ArrayList<>();

		for (int i = 0; i < digraph.V(); i++)
			if (!visited[i]) {
				List<Integer> component = new ArrayList<>();
				dfs(digraph, i, visited, component);
				components.add(component);
			}

		return components;
	}

	private static void dfs(Digraph digraph, int v, boolean[] visited, List<Integer> component) {
		visited[v] = true;
		component.add(v);

		for (int w : digraph.adj(v))
			if (!visited[w])
				dfs(digraph, w, visited, component);

		for (int u = 0; u < digraph.V(); u++)
			if (v != u && !visited[u] && hasEdge(digraph, u, v))
				dfs(digraph, u, visited, component);

	}

	private static boolean hasEdge(Digraph digraph, int u, int v) {
		for (int w : digraph.adj(u))
			if (w == v)
				return true;

		return false;
	}

	public static List<Integer> hasCycle(Digraph digraph) {
		boolean[] visited = new boolean[digraph.V()];
		boolean[] inStack = new boolean[digraph.V()];
		List<Integer> cycle = new ArrayList<>();

		for (int i = 0; i < digraph.V(); i++)
			if (!visited[i])
				if (dfsCycle(digraph, i, visited, inStack, cycle))
					return cycle;

		return null;
	}

	private static boolean dfsCycle(Digraph digraph, int v, boolean[] visited, boolean[] inStack, List<Integer> cycle) {
		visited[v] = true;
		inStack[v] = true;

		cycle.add(v);

		for (int w : digraph.adj(v))
			if (!visited[w]) {
				if (dfsCycle(digraph, w, visited, inStack, cycle))
					return true;
			} else if (inStack[w]) {
				cycle.add(w);
				return true;
			}

		inStack[v] = false;
		cycle.remove(cycle.size() - 1);
		return false;
	}

	public static List<Integer> topologicalSort(Digraph digraph) {
		if (hasCycle(digraph) != null)
			return null;

		boolean[] visited = new boolean[digraph.V()];
		List<Integer> topOrder = new ArrayList<>();

		for (int i = 0; i < digraph.V(); i++)
			if (!visited[i])
				dfsTopological(digraph, i, visited, topOrder);

		Collections.reverse(topOrder);
		return topOrder;
	}

	private static void dfsTopological(Digraph digraph, int v, boolean[] visited, List<Integer> topOrder) {
		visited[v] = true;

		for (int w : digraph.adj(v))
			if (!visited[w])
				dfsTopological(digraph, w, visited, topOrder);

		topOrder.add(v);
	}

	public static void main(String[] args) {
		try {
			ArrayList<Integer>[] graph = BipartiteCheck.loadGraphFromFile("bipartiteG.txt");

			System.out.println("Graph is " + (BipartiteCheck.isBipartite(graph) ? "" : "not ") + "bipartite.");

			Digraph digraph = new Digraph(9);

			digraph.addEdge(0, 3);
			digraph.addEdge(0, 4);
			digraph.addEdge(1, 4);
			digraph.addEdge(1, 5);
			digraph.addEdge(2, 3);
			digraph.addEdge(2, 5);
			digraph.addEdge(6, 0);
			digraph.addEdge(7, 8);
			// for 2 components comment below line
			digraph.addEdge(8, 6);
			// for cycle uncomment below and above line
			// digraph.addEdge(0, 7);

			int componentCount = countComponents(digraph);
			System.out.println("Number of components: " + componentCount);

			List<List<Integer>> components = listComponents(digraph);
			System.out.println("Components:");
			for (List<Integer> component : components)
				System.out.println(component);

			List<Integer> cycle = hasCycle(digraph);
			System.out.println("Graph has cycle: " + (cycle != null));
			if (cycle != null)
				System.out.println("Cycle:" + cycle);

			List<Integer> topSort = topologicalSort(digraph);
			if (topSort == null)
				System.out.println("Topological sort not possible (cycle detected).");
			else {
				System.out.print("Topological sort: ");
				for (int v : topSort)
					System.out.print(v + " ");
				System.out.println();
			}
		} catch (FileNotFoundException e) {
			System.err.println("File not found: " + e.getMessage());
		}
	}
}