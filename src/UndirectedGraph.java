import java.util.*;

public class UndirectedGraph<T> {
	private class Vertex {
		final T data;
		final HashMap<T, VertexCostPair> neighbors;

		Vertex(T data) {
			this.data = data;
			neighbors = new HashMap<>();
		}

		void addNeighbor(Vertex node, int cost) {
			neighbors.put(node.data, new VertexCostPair(node, cost));
			node.neighbors.put(data, new VertexCostPair(this, cost));
		}

		@Override
		public String toString() {
			return data.toString();
		}
	}

	private class VertexCostPair {
		Vertex vertex;
		int weight;

		public VertexCostPair(Vertex node, int cost) {
			this.vertex = node;
			this.weight = cost;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;
			@SuppressWarnings("unchecked") var that = (VertexCostPair) o;
			return weight == that.weight && Objects.equals(vertex, that.vertex);
		}

		@Override
		public int hashCode() {
			return Objects.hash(vertex, weight);
		}

		@Override
		public String toString() {
			return "VertexCostPair{" + "vertex=" + vertex + ", weight=" + weight + '}';
		}
	}

	private final HashMap<T, Vertex> vertices = new HashMap<>();

	public void addNode(T data) {
		vertices.put(data, new Vertex(data));
	}

	public void connect(T data1, T data2, int cost) {
		vertices.get(data1).addNeighbor(vertices.get(data2), cost);
	}

	public int getWeight(T from, T to) {
		return vertices.get(from).neighbors.get(to).weight;
	}

	public List<T> getDijkstraPath(T fromVal, T toVal) {
		Vertex from = vertices.get(fromVal), to = vertices.get(toVal);


		// table of distances from start to each node
		HashMap<T, VertexCostPair> distances = vertices
				.keySet()
				.stream()
				.collect(
						HashMap::new, (m, k) ->
								m.put(k, new VertexCostPair(null, Integer.MAX_VALUE)),
						HashMap::putAll
				);
		distances.get(from.data).weight = 0;

//		var unvisited = new ArrayList<>(vertices.values());
		var unvisited = new PriorityQueue<>(Comparator.comparingInt((Vertex o) -> distances.get(o.data).weight));
		unvisited.addAll(vertices.values());

		while (!unvisited.isEmpty()) {
			Vertex currVert = unvisited.poll();

			if (currVert == to) break;

			var currentWeight = distances.get(currVert.data).weight;

			// for all neighbors that are unvisited
			for (VertexCostPair p : currVert.neighbors.values()) {
				if (unvisited.contains(p.vertex)) {
					var calculatedDistance = currentWeight + p.weight;
					var distPair = distances.get(p.vertex.data);

					if (calculatedDistance < distPair.weight) {
						// print calculatedDistance
//						System.out.println("\t" + p.vertex.data + " = " + calculatedDistance);

						distPair.vertex = currVert;
						distPair.weight = calculatedDistance;

						// update priority queue
						unvisited.remove(p.vertex);
						unvisited.add(p.vertex);
					}
				}
			}
		}

		var path = new LinkedList<T>();

		Vertex curr = to;
		while (curr != null) {
			path.addFirst(curr.data);
			curr = distances.get(curr.data).vertex;
		}

		return path;
	}
}
