import java.io.IOException;
import java.lang.Object;
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

		nextEdge = bottleNecks();
		// nextEdge=maxcapacity();

		// Send final reply indicating that we won't change our mind any more.
		sendReply(nextEdge, true);
	}


	// Engstellen im Graphen finden
	// Kapazitaeten auf eins setzten und dann den Mincut finden
	private Edge bottleNecks() {
		Edge nextEdge = null;
		int zero = 0;
		int groesse = this.capacityMatrix.length;
		int tempMatrix[][] = new int[groesse][groesse];

		for (int l = 0; l < groesse; l++) {
			for (int k = 0; k < groesse; k++) {
				int temp = Integer.parseInt(capacityMatrix[l][k]);
				if (temp > 0 && adjacencyMatrix[l][k] == UNSELECTED_EDGE) {
					tempMatrix[l][k] = 1;
				} else {
					tempMatrix[l][k] = 0;
				}
			}
		}
		
		//tempMatrix, getMincut

		return nextEdge;
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


	public int maxFlow(int[][] adjacencyMatrix, String[][] capacityMatrix,
			int source, int sink) {
		int maxflow = 0;
		
		
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
