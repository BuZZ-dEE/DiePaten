import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

/**
 * Fill this class with your own strategy.
 * Unsere Strategie:
 * 
 * 1. Wege komplettieren // Wir suchen im Graphen nach Wegen,
 * denen nur noch eine Kante zum Komplettieren fehlt
 * 
 * Diese Strategie benutzen beide Parteien, da die Paten diese Wege
 * schließen möchten und die Polizei das verhindern möchte
 * 
 * 2. Engstellen im Graphen ausfindig machen
 * 
 * Wir setzen alle Kanten im Graphen auf den Wert 1 und finden mittels MINCUT
 * die Engstellen im Graphen. Diese müssen gesichert bzw. zerstört werden, um
 * den Goldfluss zu minimieren bzw. maximieren. Wurde eine Engstelle gefunden,
 * wird die Kante mit der höchsten Kapazität gewählt.
 * 
 * 3. Auswahl einer Kante:
 * 
 * 
 */
public class DiePaten extends FVSPlayer {

	public static final int WHITE = 0, GRAY = 1, BLACK = 2;
	private int[]  color, minCapacity, parent, queue;
	private int first, last, size;
	int[][] flow, restCapacity, restCapacityGlobal;
	Stack<Integer> stack = new Stack<Integer>();
	Stack<Integer> nextstack = new Stack<Integer>();
	List<Stack<Integer>> stackList = new LinkedList<Stack<Integer>>();

	
	public DiePaten() {
		// Set your team name here
		// Prior to sending the code in you should turn debugging off.
		super("DiePaten", true);

		//
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
		size = this.adjacencyMatrix.length;	
		/*
		 * Start of sample strategy. Replace this with your own code.
		 */
		System.err.println("flow");
		
		
		Edge nextEdge = random();
		
		if(adjacencyMatrix[0][3] != 2)
			nextEdge = new Edge(0,3);
		
		else if(adjacencyMatrix[6][7] != 2)
			nextEdge = new Edge(6,7);
		
		else{
			nextEdge = Dfs();
		}
		//nextEdge = bottleNecks().get(0);
		sendReply(nextEdge, true);
		
		// nextEdge=maxcapacity();
		//nextEdge = minCut(stringMatrixToInt(capacityMatrix)).get(0);
		// Send final reply indicating that we won't change our mind any more.
		//sendReply(nextEdge, true);
	}
	
	// Implement your cut strategy
	protected void handle_cut() {
		/*
		 * Start of sample strategy. Replace this with your own code.
		 */
		size = this.adjacencyMatrix.length;	
		System.err.println("cut");
		System.err.println(maxFlow(adjacencyMatrix, capacityMatrix, source, sink));
		// even poorer strategy: select a random edge
		Edge nextEdge = null;
		nextEdge = random();

		sendReply(nextEdge, true);
	}


	/**
	 * method to find the bottlenecks in the graph. every edge is set to 1 before
	 * @return nextEges, the list with the bottlenecks-edges
	 */
	private ArrayList<betterEdge> bottleNecks() {
		ArrayList<betterEdge> nextEdges = new ArrayList<DiePaten.betterEdge>();

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
		
		nextEdges = minCut(tempMatrix);

		return nextEdges;
	}
	
	/**
	 * method to choose the most interesting edge from the bottlenecks arraylist
	 * @param bottleNecks, list with the bootlenecks edges
	 * @return bestEdge, the most interesting edge from bootlenecks
	 */
	public Edge bestBottleNeckEdge(ArrayList<betterEdge> bottleNecks) {
		betterEdge bestEdge = bottleNecks.get(0);
		
		// searching for the edge with maximum capacity
		for (betterEdge edge : bottleNecks) {
			if (edge.getCapacity() > bestEdge.getCapacity()) {
				bestEdge = edge;
			}
		}
		
		return bestEdge;
	}
	
	/**
	 * inner class to extend the edge-class with a few useful attributes
	 * @author BuZZ-dEE
	 *
	 */
	class betterEdge extends Edge {
		private int capacity;
		public betterEdge(int from, int to, int[][] capacity) {
			super(from, to);
			this.capacity = capacity[from][to];
			
		}
	
		// TODO how can we use that???
		public ArrayList<betterEdge> initializeBetterEdge() {
			ArrayList<betterEdge> betterEdgeList = new ArrayList<DiePaten.betterEdge>();
			for (int i = 0; i < size; i++) {
				for (int j = 0; j < size; j++) {
					if (adjacencyMatrix[i][j] == UNSELECTED_EDGE) {
						//betterEdgeList.add(new betterEdge(i, j, capacity));
					}
				}
			}
			return betterEdgeList;
		}

		/**
		 * @return the capacity
		 */
		public int getCapacity() {
			return capacity;
		}

		/**
		 * @param capacity the capacity to set
		 */
		public void setCapacity(int capacity) {
			this.capacity = capacity;
		}
	}
	
	/**
	 * determine the number of edges which flow into this node
	 * @param node,
	 * @param matrix
	 * @return in_degree
	 */
	public int inDegree(int node, int[][] matrix) {
		int in_degree = 0;
		for (int i = 0; i < size; i++) {
			if (matrix[i][node] == 1 || matrix[i][node] == 2) {
				in_degree++;
			}
		}
		return in_degree;
	}
	
	/**
	 * determine the number of edges which have its source from this node
	 * @param node,
	 * @param matrix
	 * @return in_degree
	 */
	public int outDegree(int node, int[][] matrix) {
		int out_degree = 0;
		for (int i = 0; i < size; i++) {
			if (matrix[node][i] == 1 || matrix[i][node] == 2) {
				out_degree++;
			}
		}
		return out_degree;
	}

	/**
	 * method to choose the next edge with maximum capacity
	 * @return nextEdge, the next chosen edge
	 */
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



	/**
	 * method to choose the next edge radomly
	 * @return nextEdge, the next chosen edge
	 */
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
		
		String[][] restCapacityString = new String[size][size];
		String[][] capacityMatrixString = new String[size][size];
		
		restCapacityString = intMatrixToString(restCapacityGlobal);
		capacityMatrixString = intMatrixToString(capacityMatrix);

		// to compute the max-flow
		maxFlow(adjacencyMatrix, capacityMatrixString, source, sink);
		// to get the source_Set of the global queue-array
		maxFlow(adjacencyMatrix, restCapacityString, source, sink);
		// put it in source_Set arraylist
		for (int i = 0; i < queue.length; i++) {
			if(!source_Set.contains(queue[i])) {
				source_Set.add(queue[i]);
		}
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
		}
		for (int z = 0; z < source_Set.size(); z++) {
			if (sink_Set.contains(source_Set.get(z))) {
				sink_Set.remove(source_Set.get(z));
			}
		}
		
		return sink_Set;
	}
	
	
	/**
	 * determine the edges from source_set to sink_set
	 * @param capacityMatrix
	 * @return min-cut
	 */
	public ArrayList<betterEdge> minCut(int[][] capacityMatrix) {
		ArrayList<betterEdge> min_Cut = new ArrayList<betterEdge>();
		ArrayList<Integer> source_Set = new ArrayList<Integer>();
		ArrayList<Integer> sink_Set = new ArrayList<Integer>();
		betterEdge edge;
		
		source_Set = sourceSet(capacityMatrix);
		sink_Set = sinkSet(capacityMatrix);
		
		for (int q = 0; q < source_Set.size(); q++) {
			for (int s = 0; s < sink_Set.size(); s++) {
				if (adjacencyMatrix[source_Set.get(q)][sink_Set.get(s)] == 1 || adjacencyMatrix[source_Set.get(q)][sink_Set.get(s)] == 2) {
					edge = new betterEdge(source_Set.get(q), sink_Set.get(s), capacityMatrix); // TODO is that the right capacity???
					min_Cut.add(edge);
				}
			}
		}
		 
		return min_Cut;
	}
	

	/**
	 * method to convert a stringmatrix to an intmatrix
	 * @param stringMatrix, which is converted to an intmatrix
	 * @return intMatrix
	 */
	public int[][] stringMatrixToInt(String[][] stringMatrix) {
		int[][] intMatrix = new int[size][size];;
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				intMatrix[i][j] = Integer.parseInt(stringMatrix[i][j]);
			}
		}
		return intMatrix;
	}
	
	
	/**
	 * method to convert a intmatrix to an stringmatrix
	 * @param intMatrix, which is converted to a stringmatrix
	 * @return stringMatrix
	 */
	public String[][] intMatrixToString(int[][] intMatrix) {
		String[][] stringMatrix = new String[size][size];;
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				stringMatrix[i][j] = Integer.toString(intMatrix[i][j]);
			}
		}
		return stringMatrix;
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
		
		restCapacity = stringMatrixToInt(capacityMatrix);
		
		
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
		
		restCapacityGlobal = restCapacity;
		
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
	

	/**
	 * Modifizierte Tiefensuche, die jeden Weg im Graphen berechnet
	 * Für diese Suche werden 2 Stacks benutzt. Auf dem ersten Stack werden
	 * die aktuellen Knoten des Weges gespeichert und auf dem anderen Stack
	 * werden die aktuellen Positionen gespeichert um weitere Nachbarknoten zu finden
	 * @return result Edge
	 */

	public Edge Dfs() {
		stack.push(source);
		nextstack.push(0);
		Edge result = null;
		boolean finished = false;

		// Solange wir nicht alle Wege gefunden habe -> führe diese Schleife aus
		while (!finished) {

			// Wenn der Wege-Stack leer ist, sind wir fertig und haben alle Wege
			// gefunden
			// und suchen nun in checkGaps() nach einer Lücke in einem Weg
			if (stack.empty()) {
				finished = true;
				result = checkGaps();
				break;
			}

			// Wenn das oberste Element von nextstack kleiner als die Anzahl der
			// Knoten
			// ist, können wir noch weiter nach Nachbarsknoten suchen
			while (nextstack.peek() <= adjacencyMatrix.length) {

				// Wenn das oberste Element der Anzahl der Knoten entspricht,
				// gibt
				// es keine weiteren Kindsknoten und wir können die oberen
				// Elemente
				// des Stacks entfernen
				if (nextstack.peek() == adjacencyMatrix.length) {
					stack.pop();
					nextstack.pop();
					break;
				}

				// Wir suchen das nächste Kind vom aktuellen Knoten
				int i = nextstack.peek();

				if (adjacencyMatrix[stack.peek()][i] == UNSELECTED_EDGE
						|| adjacencyMatrix[stack.peek()][i] == FLOW_EDGE) {

					// Wenn das Kind die Senke ist, speichern wir den Weg ab
					if (nextstack.peek() == sink) {
						stack.push(i);
						int tmp3 = nextstack.pop();
						nextstack.push(++tmp3);
						stackList.add((Stack<Integer>)stack.clone());
						stack.pop();
						break;
					}
					// Wenn kein Kind gefunden wurde, wird an der nächsten
					// Position gesucht
					// und inkrementieren den nextstack
					int tmp = nextstack.pop();
					nextstack.push(++tmp);

					// Wurde ein Kind gefunden, packen wir es auf den stack und
					// packen eine weiter 0 als Positionsanzeige auf den
					// nextstack
					stack.push(i);
					nextstack.push(0);

				}// Kein Kind gefunden
				else {
					int tmp = nextstack.pop();
					nextstack.push(++tmp);
				}
			}
		}
		return result;
	}


	/**
	 * checkGaps() prüft, ob es Wege gibt, bei denen nur noch eine Kante
	 * zum komplettieren fehlt
	 * @return result Edge
	 */
	public Edge checkGaps() {
		Edge result = null;
		// Geht alle Stacks in der Stackliste durch
		for (int i = 0; i < stackList.size(); i++) {
			int counter = 0;
			// Geht den i-ten Weg der Stackliste durch
			for (int k = 0; k < stackList.get(i).size() - 1; k++) {

				// Prüft, ob es unselektierte Kanten im Weg gibt
				// Wenn es eine gibt, erhöhe den Counter und übergebe die Kante
				if (adjacencyMatrix[stackList.get(i).get(k)][stackList.get(i)
						.get((k + 1))] == 1) {
					counter++;
					result = new Edge(stackList.get(i).get(k), stackList.get(i)
							.get(k + 1));
				}
				// Wenn eine Kante im Weg für die Polizei markiert ist,
				// ignoriere den Weg, da dieser nicht mehr komplettiert werden
				// kann
				else if (adjacencyMatrix[stackList.get(i).get(k)][stackList
						.get(i).get(k + 1)] == 3) {
					k = stackList.get(i).size() - 1;
					break;
				}

			}
			// Wurde ein Weg durchlaufen und es wurde nur eine unselektierte
			// Kante gefunden, breche die Schleife ab
			if (counter == 1) {
				break;
			}
		}
		// Gib die unselektierte Kante aus, die zum Komplettieren des Weges
		// führt
		return result;

	}

	


	// Do not edit!
	public static void main(String args[]) throws IOException {
		DiePaten p = new DiePaten();
		p.connect();
		p.mainLoop();
	}
}
