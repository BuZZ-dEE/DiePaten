import java.io.IOException;
import java.util.ArrayList;
/**
 * Fill this class with your own strategy.
 */
public class DiePaten extends FVSPlayer {

	public static final int WHITE = 0, GRAY = 1, BLACK = 2;
	private int[] color, minCapacity, parent, queue;
	private int first, last, size;
	int[][] flow, restCapacity;

	
	public DiePaten() {
		// Set your team name here
		// Prior to sending the code in you should turn debugging off.
		super("DiePaten", true);

		size = this.adjacencyMatrix.length;	
	}

	/*
	 * Note: Available values are int source int sink int[][] adjacencyMatrix
	 * string[][] capacityMatrix The capacities are encoded as Strings and can
	 * be parsed by Integer.parseInt() The values in the adjacency matrix are
	 * either UNSELECTED_EDGE, FLOW_EDGE, CUT_EDGE or NO_EDGE
	 * 
	 * You have only a limited time to make your choice. The program is aborted
	 * after the time limit. If you have not sent any reply by that time, you
	 * don't get any edge. You can send as many replies as you want, only the
	 * last one counts. If you are done with your calculations, you can send a
	 * flush to the game controller so that it won't wait unnecessarily long.
	 */

	// Implement your flow strategy
	protected void handle_flow() {

		/*
		 * Start of sample strategy. Replace this with your own code.
		 */
		System.err.println("flow");
		Edge nextEdge = null;

		//nextEdge = bottleNecks();
		// nextEdge=maxcapacity();

		// Send final reply indicating that we won't change our mind any more.
		sendReply(nextEdge, true);
	}


	/**
	 * method to find the bottlenecks in the graph. every edge is set to 1 before
	 * @return nextEges, the list with the bottlenecks-edges
	 */
	private ArrayList<Edge> bottleNecks() {
		ArrayList<Edge> nextEdges = null;

		int tempMatrix[][] = new int[size][size];

		for (int l = 0; l < size; l++) {
			for (int k = 0; k < size; k++) {
				int temp = Integer.parseInt(capacityMatrix[l][k]);
				if (temp > 0 && adjacencyMatrix[l][k] == UNSELECTED_EDGE) {
					tempMatrix[l][k] = 1;
				} else {
					tempMatrix[l][k] = 0;
				}
			}
		} 
		
		//tempMatrix, getMincut
		nextEdges = minCut(tempMatrix);

		return nextEdges;
	}

	private Edge maxcapacity() {
		int maxCapacity = 0;
		int numNodes = this.capacityMatrix.length;
		Edge nextEdge = null;
		for (int i = 0; i < numNodes; i++) {
			for (int j = 0; j < numNodes; j++) {
				if (this.adjacencyMatrix[i][j] == UNSELECTED_EDGE) {
					int currentCapacity = Integer
							.parseInt(capacityMatrix[i][j]);
					if (currentCapacity > maxCapacity) {
						maxCapacity = currentCapacity;
						nextEdge = new Edge(i, j);
						sendReply(nextEdge, false);
					}
					}
			}
		}
		return nextEdge;

	}

	// Implement your cut strategy
	protected void handle_cut() {
		/*
		 * Start of sample strategy. Replace this with your own code.
		 */
		System.err.println("cut");

		// even poorer strategy: select a random edge
		Edge nextEdge = null;
		nextEdge = random();

		sendReply(nextEdge, true);
	}

	private Edge random() {
		Edge nextEdge = null;
		int numNodes = this.capacityMatrix.length;
		while (nextEdge == null) {
			int i = (int) (numNodes * Math.random());
			int j = (int) (numNodes * Math.random());
			if (this.adjacencyMatrix[i][j] == UNSELECTED_EDGE)
				nextEdge = new Edge(i, j);
		}
		return nextEdge;

	}
	
	
	/**
	 * Method to determine the nodes which are in the min-cut-source-set.
	 * @param capacityMatrix
	 * @return source_Set
	 */
	public ArrayList<Integer> sourceSet(int[][] capacityMatrix) {
		ArrayList<Integer> source_Set = new ArrayList<Integer>();
		
		// restCapacity array to string-array
		String[][] restCapacityString = new String[restCapacity.length][restCapacity.length];
		String[][] capacityMatrixString = new String[restCapacity.length][restCapacity.length];
		for(int i = 0; i < restCapacity.length; i++) {
			//restCapacityString[i] = new String[ restCapacity[i].length ];
		    for(int j = 0; j < restCapacity[i].length; j++) {
		    	restCapacityString[i][j] = Integer.toString( restCapacity[i][j] );
		    	capacityMatrixString[i][j] = Integer.toString( capacityMatrix[i][j] );
		    }
		}

		// to compute the max-flow
		maxFlow(adjacencyMatrix, capacityMatrixString, source, sink);
		// to get the source_Set of the global queue-array
		maxFlow(adjacencyMatrix, restCapacityString, source, sink);
		// put it in source_Set arraylist
		for (int i = 0; i < queue.length; i++) {
			source_Set.add(queue[i]);
		}
		
		return source_Set;
	}
	
	/**
	 * Method to determine the nodes which are in the min-cut-sink-set.
	 * @param capacityMatrix
	 * @return sink_Set
	 */
	public ArrayList<Integer> sinkSet(int[][] capacityMatrix) {
		ArrayList<Integer> source_Set = new ArrayList<Integer>();
		ArrayList<Integer> sink_Set = new ArrayList<Integer>();
		source_Set = sourceSet(capacityMatrix);
		
		for (int i = 0; i < size; i++) {
			sink_Set.add(i);
			for (int j = 0; j < source_Set.size(); j++) {
				if (source_Set.indexOf(j) == sink_Set.indexOf(i)) {
					sink_Set.remove(i);
				}
			}
		}
		
		return sink_Set;
	}
	
	
	/**
	 * determine the edges from source_set to sink_set
	 * @param capacityMatrix
	 * @return min-cut
	 */
	public ArrayList<Edge> minCut(int[][] capacityMatrix) {
		ArrayList<Edge> min_Cut = new ArrayList<Edge>();
		ArrayList<Integer> source_Set = new ArrayList<Integer>();
		ArrayList<Integer> sink_Set = new ArrayList<Integer>();
		Edge edge;
		
		source_Set = sourceSet(capacityMatrix);
		sink_Set = sinkSet(capacityMatrix);
		
		for (int q = 0; q < source_Set.size(); q++) {
			for (int s = 0; s < sink_Set.size(); s++) {
				if (adjacencyMatrix[source_Set.get(q)][sink_Set.get(s)] == 1 || adjacencyMatrix[source_Set.get(q)][sink_Set.get(s)] == 2) {
					edge = new Edge(source_Set.get(q), sink_Set.get(s));
					min_Cut.add(edge);
				}
			}
		}
		 
		return min_Cut;
	}

	/**
	 * Method to compute the max-flow of the given graph
	 * @param adjacencyMatrix, matrix with the edges that are used
	 * @param capacityMatrix, matrix with the capacities for each edge
	 * @param source, the source-node (start)
	 * @param sink, the sink-node (target)
	 * @return maxflow
	 */
	public int maxFlow(int[][] adjacencyMatrix, String[][] capacityMatrix,
			int source, int sink) {
		int maxflow = 0;
		
		for (int x = 0; x < size; x++) {
			for (int y = 0; y < size; y++) {
				if (adjacencyMatrix[x][y] == CUT_EDGE) {
					capacityMatrix[x][y] = "0";
				}
			}
		}
		
		flow = new int[size][size];
		restCapacity = new int[size][size];
		parent = new int[size];
		minCapacity = new int[size];
		color = new int[size];
		queue = new int[size];
		
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				restCapacity[i][j] = Integer.parseInt(capacityMatrix[i][j]);
			}
		}
		
		while(BFS(source)) {
			maxflow += minCapacity[sink];
			int v = sink, u;
			
			while(v != source) {
				u = parent[v];
				flow[u][v] += minCapacity[sink];
				flow[v][u] -= minCapacity[sink];
				restCapacity[u][v] -= minCapacity[sink];
				restCapacity[v][u] += minCapacity[sink];
				v = u;
			}
		}
		

		return maxflow;
	}
	
	/**
	 * breadth-first search method
	 * @param source, the source-node
	 * @return true, if augmentedPathExits exists
	 */
	private boolean BFS(int source) {
		boolean augmentedPathExits = false;
		
		for (int i = 0; i < size; i++) {
			color[i] = WHITE;
			minCapacity[i] = Integer.MAX_VALUE;
		}
		
		first = last = 0;
		queue[last++] = source;
		color[source] = GRAY;
		
		while (first != last) {
			
			int v = queue[first++];
			for (int u = 0; u < size; u++) {
				if (color[u] == WHITE && restCapacity[v][u] > 0) {
					
					minCapacity[u] = Math.min(minCapacity[v], restCapacity[v][u]);
					parent[u] = v;
					color[u] = GRAY;
					
					if (u == sink) {
						augmentedPathExits = true;
					} else {
						queue[last++] = u;
					}
				}
			}
		}
		
		return augmentedPathExits;
	}

	// Do not edit!
	public static void main(String args[]) throws IOException {
		DiePaten p = new DiePaten();
		p.connect();
		p.mainLoop();
	}
}
