package it.unipi.di.export;

import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unipi.di.interfaces.Graph;

import java.util.ArrayList;
import java.util.List;

public class ECCc extends ECCL {
	
	protected List<Int2IntOpenHashMap> cleanGraph; //map.get(i).get(j) -> postion of (i,j) in the clean edges list (filled in both directions)
	protected List<Integer> edgeis, edgejs; //edgeis.get(x) , edgejs.get(x) will contain the edge at the xth position.
	protected int lastCleanEdge; //POSITION of the right-most clean edge in the lists. To be updated after swap removals.
	
	protected int seli, selj;
	private int interval = 1;
	
	public ECCc(Graph graph) {
		super(graph);
	}

	protected int pMaxVal;

	@Override
	public void start() {
		
		solution = new ArrayList<IntOpenHashSet>();

		expand0();
		
		if(checkSolution) checkSolution();
	}
	
	@Override
	public void expand0()
	{
		int maxlabel, position;
		List<Integer> vertices = graph.vertices();
		maxlabel = 0;
		for(int i : vertices) maxlabel = Math.max(i, maxlabel);
		
		//stores clean edges and their position in the edge arrays
		// cleangraph[i][j] -> position if i,j , or NULL if i,j is not a clean edge
		cleanGraph = new ArrayList<Int2IntOpenHashMap>(maxlabel+1);

		edgeis = new ArrayList<Integer>(vertices.size()); //initializing to an O(n) size doesn't worsen memory usage and provides a decent starting capacity
		edgejs = new ArrayList<Integer>(vertices.size());
		
		
		for(int i=0; i <= maxlabel; i++)
		{
			cleanGraph.add(new Int2IntOpenHashMap());
		}
		
		for(int i : vertices)
		{
			for(int j : graph.neighbors(i)) //new edge: (i,j), to consider in both direction for the cgraph, and for the elist only if  i < j
			{
				
				if(i < j && !cleanGraph.get(i).containsKey(j)) //if the edge is in the right direction and is not a duplicate
				{
					edgeis.add(i);
					edgejs.add(j);
					
					position = edgeis.size()-1;
					
					cleanGraph.get(i).put(j, position);
					cleanGraph.get(j).put(i, position);
				}
			}
		}		
		
		lastCleanEdge = edgeis.size()-1;
		
		selectEdgeToExpand();
		while(seli != -1)
		{
			
			/**
			 * -select edge
			 * process
			 * -select another edge until halt
			 */
			
			List<IntOpenHashSet> P = new ArrayList<IntOpenHashSet>();
			P.add(new IntOpenHashSet());
			
			int min, max;
			if(graph.neighbors(seli).size() < graph.neighbors(selj).size())
			{
				min = seli;
				max = selj;
			}else
			{
				min = selj;
				max = seli;
			}
			
			pMaxVal = 0;
			int val;
			
			for(int x : graph.neighbors(min))
			{
				if(graph.areNeighbors(max, x))
					{
						val = 0;
						
						if(cleanGraph.get(min).containsKey(x)) val++;
						if(cleanGraph.get(max).containsKey(x)) val++;
						
						addToP(P, x, val);
					}
			}
			
			
			IntOpenHashSet C = new IntOpenHashSet();
			
			C.add(seli);
			C.add(selj);
			
			expand(C,P);
			
			selectEdgeToExpand();
		}
		

		
	}


	
	
	public void expand(IntOpenHashSet C, List<IntOpenHashSet> P)
	{
		nodes++;
		
		if(pMaxVal > 0) //P is not empty and contains at least a clean node
		{

			//an arbitrary node of max value
			int x;
			try{
				x = P.get(pMaxVal).iterator().nextInt();
			}
				catch( Exception e)
			{
				System.out.println(C);
				System.exit(0);
				return;
			}
			
			process(C, P, x);
			
		} else //P does not contain clean nodes (but is not necessarily empty)
		{
			if(true || P.get(0) == null || P.get(0).isEmpty())
			{
				report(C);
				return;
			}
			
		}
	}
	
	public void process(IntOpenHashSet C, List<IntOpenHashSet> P, int x)
	{
		C.add(x);
		
		//removing x from P
		//since the strategy is 'max clean value', the val of x is pMaxVal
		remFromP(P, x, pMaxVal);

		IntOpenHashSet toRemove = new IntOpenHashSet();

		for(int vi = pMaxVal; vi >= 0; vi--)
		{
			for(int n : P.get(vi))
			{
				if(graph.areNeighbors(x, n)) //the node n should still be in P
				{
					if(cleanGraph.get(x).containsKey(n)) //n's clean value increases by 1
					{
						toRemove.add(n);
						addToP(P, n, vi+1);
					}
				} else
				{
					toRemove.add(n);
				}
			}
			
			for(int n : toRemove)
			{
				remFromP(P, n, vi);
			}
			toRemove.clear();
		}
		

		expand(C,P);
		
	}
	
	@Override
	/**
	 * Selects a clean edge, and puts its extremes in 'seli' and 'selj'
	 * If there are no clean edges, 'seli' is put to -1
	 * (all valid node IDs are assumed to be >= 0)
	 */
	public void selectEdgeToExpand() //random
	{
		if(lastCleanEdge < 0)
		{
			seli = -1;
			selj = -1;
		}
		else
		{
			int pos = (int) ((lastCleanEdge+1)*Math.random()); //from 0 to lastCleanEdge included
		
			seli = edgeis.get(pos);
			selj = edgejs.get(pos);
		}

	}
	
	
	/**
	 * Adds C to the solution and marks all edges in C as dirty.
	 */
	public void report(IntOpenHashSet C)
	{
		if(cliques%interval == 0){ System.out.println(cliques+" cliques"); if(interval < 5000) interval = interval*2; else interval = 10000;}
		cliques++;
		deaths += C.size(); //abused variable for solution sum size
		solution.add(C);
		
		int pos;
		
		for(int i : C) //for each node in C
		{
			for(int j : C)
			{
				if(i < j && cleanGraph.get(i).containsKey(j)) //remove the edge from the clean part of the edge list
				{
					pos = cleanGraph.get(i).get(j);
					
					if(edgeis.get(pos) != i || edgejs.get(pos) != j){ //consistency check
						System.out.println("Inverted pos (j->i) was: "+cleanGraph.get(j).get(i)+" instead of "+cleanGraph.get(i).get(j));
						System.out.println("Removing.. expected ("+i+","+j+") in pos "+pos+" to pos "+lastCleanEdge+", found ("+edgeis.get(pos)+","+edgejs.get(pos)+")");
						System.exit(0);
					}

					edgeis.set(pos, edgeis.get(lastCleanEdge));
					edgejs.set(pos, edgejs.get(lastCleanEdge));
					
					edgeis.set(lastCleanEdge, 0);// i); //actually not necessary, this is were the removed edge gets swapped
					edgejs.set(lastCleanEdge, 0);// j); //actually not necessary
					
					lastCleanEdge--;

					cleanGraph.get(edgeis.get(pos)).put((int)edgejs.get(pos), pos); //updating indices of the edge that was swapped down
					cleanGraph.get(edgejs.get(pos)).put((int)edgeis.get(pos), pos);
					
					cleanGraph.get(i).remove(j); //removing the edge from the clean graph
					cleanGraph.get(j).remove(i);
				}
			}
		}
		
	}

	private void addToP(List<IntOpenHashSet> P, int node, int val)
	{
		while(P.size() <= val) P.add(new IntOpenHashSet());
		
		P.get(val).add(node);
		
		if(val > pMaxVal){
			pMaxVal = val;
		}
	}
	
	private void remFromP(List<IntOpenHashSet> P, int node, int val)
	{
		//removing the node
		P.get(val).remove(node);
		//updating the index of the rightmost nonempty set in P
		if(P.get(val).isEmpty() && pMaxVal == val)
		{
			while(pMaxVal > 0 && P.get(pMaxVal).isEmpty()) pMaxVal--;
		}
	}
}