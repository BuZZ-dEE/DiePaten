import java.io.IOException;

/**
 * Fill this class with your own strategy.
 */
public class DiePaten extends FVSPlayer {

    public DiePaten() {
        // Set your team name here
		  // Prior to sending the code in you should turn debugging off.
        super("DiePaten", true);
    }

    /*
     * Note:
     * Available values are 
     *  int source
     *  int sink
     *  int[][] adjacencyMatrix
     *  string[][] capacityMatrix
     * The capacities are encoded as Strings and can be parsed by Integer.parseInt()
     * The values in the adjacency matrix are either UNSELECTED_EDGE, FLOW_EDGE, CUT_EDGE or NO_EDGE
     * 
     * You have only a limited time to make your choice. The program is 
     * aborted after the time limit. If you have not sent any reply by that 
     * time, you don't get any edge.
     * You can send as many replies as you want, only the last one counts.
     * If you are done with your calculations, you can send a flush to the 
     * game controller so that it won't wait unnecessarily long.
     */
    
    // Implement your flow strategy
    protected void handle_flow() {
        /*
         * Start of sample strategy. Replace this with your own code.
         */
        System.err.println("flow");
        
        // poor strategy: select edge with maximum capacity
        int numNodes = this.capacityMatrix.length;
        Edge nextEdge = null;
        int maxCapacity = 0;
        for(int i=0; i<numNodes; i++) {
        	for(int j=0; j<numNodes; j++) {
        		if(this.adjacencyMatrix[i][j] == UNSELECTED_EDGE) {
        			int currentCapacity = Integer.parseInt(capacityMatrix[i][j]);
        			if(currentCapacity>maxCapacity) {
        				maxCapacity=currentCapacity;
        				nextEdge = new Edge(i,j);
        				sendReply(nextEdge, false);
        			}
        		}
        	}
        }
        // Send final reply indicating that we won't change our mind any more.
        sendReply(nextEdge, true);        
    }

    // Implement your cut strategy
    protected void handle_cut() {
        /*
         * Start of sample strategy. Replace this with your own code.
         */
        System.err.println("cut");
        
        // even poorer strategy: select a random edge
    	Edge nextEdge = null;
        int numNodes = this.capacityMatrix.length;
        while (nextEdge == null) {
            int i = (int) (numNodes * Math.random());
            int j = (int) (numNodes * Math.random());
            if (this.adjacencyMatrix[i][j] == UNSELECTED_EDGE)
            	nextEdge = new Edge(i, j);
        }
        sendReply(nextEdge, true);        
    }
    
    // Do not edit!
    public static void main(String args[]) throws IOException {
        NewStrategy p = new NewStrategy();
        p.connect();
        p.mainLoop();
    }

}