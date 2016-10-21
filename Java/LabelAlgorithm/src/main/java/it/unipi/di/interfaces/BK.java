package it.unipi.di.interfaces;

import java.io.InputStream;

// template for Bron Kerbosch
public abstract class BK implements SearchAlg {

    protected int cliques;	// number of maximal cliques
    protected long nodes;     // number of decisions
    protected long timeLimit; // milliseconds
    protected long startTime;   // milliseconds
    protected long endTime;	// milliseconds
    protected int maxSize;    // size of max clique
    protected boolean storeSolution = true; //false: faster, but only records number of cliques and max size
	public static boolean DEBUG = (System.getProperty("DEBUG") != null);
    public boolean aborted = false;
	
    public BK (Graph graph) {
	nodes = maxSize = 0;
	startTime = timeLimit = -1;
    }

    public BK (InputStream is) {
    	throw new RuntimeException("This constructor must be overridden.");
	}
    
	@Override
	public void search(){
	nodes                = 0;
	
	if(DEBUG) System.out.println("Starting search; Timeout: "+timeLimit+"ms");
	
	startTime              = System.currentTimeMillis();
	
	start();
	
	endTime	= System.currentTimeMillis();

    }
	

//    public abstract void setFilters(List<Integer> toVisit, List<Integer> excluded, Set<Integer> required, int requiredNum, int minSize);
	
	public abstract void start();
	
	
	@Override
	public void setTimeLimit(long limit){
		this.timeLimit = limit;
	}

	@Override
	public void printStats(){
		System.out.println("Cliques: "+cliques);
		System.out.println("MaxSize: "+maxSize);
		System.out.println("Nodes: "+nodes);
		System.out.println("Time: "+(endTime-startTime));
		System.out.println("Aborted: "+aborted);
	}
	
	@Override
	public void setAborted(){
		this.endTime = System.currentTimeMillis();
		this.aborted = true;
	}

}
