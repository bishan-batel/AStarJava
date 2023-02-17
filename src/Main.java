import java.util.Arrays;

public class Main {
	private enum GraphNode {
		A, B, C, D, F, G, H, J
	}


	public static void main(String[] args) {
		var graph = new UndirectedGraph<GraphNode>();
		Arrays.stream(GraphNode.values()).forEach(graph::addNode);

		// starting connection
		graph.connect(GraphNode.C, GraphNode.F, 8);
		graph.connect(GraphNode.C, GraphNode.A, 2);
		graph.connect(GraphNode.C, GraphNode.G, 4);
		graph.connect(GraphNode.F, GraphNode.J, 3);
		graph.connect(GraphNode.A, GraphNode.G, 7);
		graph.connect(GraphNode.A, GraphNode.B, 4);
		graph.connect(GraphNode.G, GraphNode.D, 5);
		graph.connect(GraphNode.G, GraphNode.J, 4);
		graph.connect(GraphNode.J, GraphNode.H, 2);
		graph.connect(GraphNode.B, GraphNode.D, 2);
		graph.connect(GraphNode.D, GraphNode.H, 6);


		System.out.println("Run to the mountains");
		graph.getDijkstraPath(GraphNode.A, GraphNode.J).forEach(
				dir -> System.out.printf("%s->", dir)
		);
		System.out.println("Take a selfie");
	}
}
