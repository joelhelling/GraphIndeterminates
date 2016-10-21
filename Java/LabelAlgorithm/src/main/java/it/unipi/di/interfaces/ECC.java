package it.unipi.di.interfaces;

import java.io.InputStream;

public abstract class ECC implements SearchAlg {
	
	
	
	/**
	 * Overview:
	 * 
	 * 1-all edges have weight 0
	 * 2-select an edge with minimum weight
	 * 3-find 1 (or more?) cliques that use that edge
	 * 4-for any found clique: increase by 1 the weight of any used edge
	 * 5-update some kind of halting condition
	 * 6-back to 2 or stop
	 *
	 */
	
	 	protected int cliques;	// number of maximal cliques
	    protected long nodes;     // number of decisions
	    protected long deaths;
	    protected long timeLimit; // milliseconds
	    protected long startTime;   // milliseconds
	    protected long endTime;	// milliseconds
	    protected int maxSize;    // size of max clique
	    protected boolean storeSolution = true; //false: faster, but only records number of cliques and max size
	    protected boolean checkSolution = true;
		public static boolean DEBUG = (System.getProperty("DEBUG") != null);
	    public boolean aborted = false;
		
	    public ECC (Graph graph) {
		nodes = maxSize = 0;
		startTime = timeLimit = -1;
	    }

	    public ECC (InputStream is) {
	    	throw new RuntimeException("This constructor must be overridden.");
		}
	    
		@Override
		public void search(){
		nodes                = 0;
		deaths = 0;
		
		if(DEBUG) System.out.println("Starting search; Timeout: "+timeLimit+"ms");
		
		startTime              = System.currentTimeMillis();
		
		start();
		
		endTime	= System.currentTimeMillis();

	    }
		
		public abstract void start();
		
		public abstract void selectEdgeToExpand();
		
		public abstract void expand0();
		
		
		@Override
		public void setTimeLimit(long limit){
			this.timeLimit = limit;
		}

		@Override
		public void printStats(){
			System.out.println("Cliques: "+cliques);
			System.out.println("Sum: "+deaths);
			System.out.println("MaxSize: "+maxSize);
//			System.out.println("Nodes: "+nodes);
			System.out.println("Time: "+(endTime-startTime));
			System.out.println("Aborted: "+aborted);
		}
		
		@Override
		public void setAborted(){
			this.endTime = System.currentTimeMillis();
			this.aborted = true;
		}
		


}
