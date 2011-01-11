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
     *
     * --------------------------- UNSERE STRATEGIE ---------------------------
     *
     * 1. Noch Kanten markierbar?  //Das überprueft das Programm schon, unnötig
     * 2. Vorzeitiger Spielabbruch //Pre-Termination --- Jens
     * 3. Wege komplettieren       //Way-completing --- Hope
     * 4. Engstellen im Graphen ausfindig machen //Bottlenecks --- Sven
     * 5. Auswahl einer Kante      //Edge-Selection --- Basti
     *
     * ---------------------------- MEILENSTEINE -----------------------------
     *
     * 1. Vier Teil-Strategien implementieren
     * 2. "Workflow"(Auswaehlstrategien) bestimmen
     * 3. Tests und Feinkalibrierung 
     * 
     */


    // Implement your flow strategy ---MAFIA--- TODO
    protected void handle_flow() {
        /*
         * Start of sample strategy. Replace this with your own code.
         * Hier müssen unsere 4 Teilstrategien aufgeführt werden, die
         * nacheinander durchlaufen werden
         */
        System.err.println("flow");
        Edge nextEdge = null;

        //Teil-Strategie 2 - Testen, ob es überhaupt noch einen Weg gibt
        Boolean weitereMoeglichkeiten = true;
        weitereMoeglichkeiten = preTermination();

        //Teil-Strategie 3
        if(nextEdge == null){
            nextEdge = wayCompletion();
        }

        //Teil-Strategie 4
        if(nextEdge == null){
            nextEdge = bottleNecks();
        }

        //Teil-Strategie 4
        if(nextEdge == null){
            nextEdge = edgeSelection();
        }

        // Notloesung
        if(nextEdge == null){
            nextEdge = random();
        }

        // Send final reply indicating that we won't change our mind any more.
        sendReply(nextEdge, true);        
    }

    // Implement your cut strategy ---Polizei---  TODO
    /*
     * Hier müssen unsere 4 Teilstrategien aufgeführt werden, die
     * nacheinander durchlaufen werden
     */
    protected void handle_cut() {
        /*
         * Start of sample strategy. Replace this with your own code.
         */
        System.err.println("cut");
        
        // even poorer strategy: select a random edge
    	Edge nextEdge = null;

        //Teil-Strategie 2 - Testen, ob es überhaupt noch einen Weg gibt
        Boolean weitereMoeglichkeiten = true;
        weitereMoeglichkeiten = preTermination();

        //Teil-Strategie 3
        if(nextEdge == null){
            nextEdge = wayCompletion();
        }

        //Teil-Strategie 4
        if(nextEdge == null){
            nextEdge = bottleNecks();
        }

        //Teil-Strategie 4
        if(nextEdge == null){
            nextEdge = edgeSelection();
        }

        //Notlösung
        if(nextEdge == null){
            nextEdge = random();
        }

        sendReply(nextEdge, true);        
    }

    //TODO
    public Boolean preTermination(){
        Edge nextEdge = null;
        int numNodes = this.capacityMatrix.length;
        return true;
    }

    //TODO
    public Edge wayCompletion(){
        Edge nextEdge = null;
        return nextEdge;
    }
    
    //TODO
    public Edge bottleNecks(){
        Edge nextEdge = null;
        return nextEdge;
    }

    //TODO
    public Edge edgeSelection(){
        Edge nextEdge = null;
        return nextEdge;
    }

    public Edge random(){
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

    //Hier kommen unsere eigenen Methoden hin, die wir verwenden
    public Edge maxCapacity(){
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
        return nextEdge;
    }
    // Do not edit!
    public static void main(String args[]) throws IOException {
        NewStrategy p = new NewStrategy();
        p.connect();
        p.mainLoop();
    }

}
