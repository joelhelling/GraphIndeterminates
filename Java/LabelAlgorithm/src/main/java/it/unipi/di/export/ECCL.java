package it.unipi.di.export;

import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unipi.di.interfaces.Graph;
import it.unipi.di.interfaces.ListGraph;

import java.util.*;



public abstract class ECCL extends it.unipi.di.interfaces.ECC {

	protected ListGraph graph;
    protected List<IntOpenHashSet> solution;
	
	public ECCL(Graph graph) {
		super(graph);
		this.graph = (ListGraph) graph;
	}

	@Override
	public List<IntOpenHashSet> solution()
	{
		return this.solution;
	}
	
	public boolean checkSolution()
	{
		if(solution == null){
			System.out.println("No solution");
			return false;
		}
		
		Map<Integer,Set<Integer>> g= new HashMap<Integer,Set<Integer>>();
		
		for(IntOpenHashSet clq : solution)
		{
			for(int i : clq)
			{
				for(int j : clq)
				{
					if(i != j)
					{
						if(!g.containsKey(i)) g.put(i,new HashSet<Integer>());
						
						g.get(i).add(j);
					}
				}
			}
		}
		
		List<Integer> nodes = graph.vertices();
		for(int i :nodes)
		{
			List<Integer> neighs = graph.neighbors(i);
			
			if(neighs != null)
				for (int j : neighs)
				{
					try
					{
						if(!g.get(i).contains(j))
						{
							System.out.println("The edge "+i+","+j+" is not covered in the solution!!");
							return false;
						}
					}
					catch(Exception e)
					{
						System.out.println("The edge "+i+","+j+" raised an exception!!");
						
						System.out.println("g.get(i): "+g.get(i));
						
						return false;
					}
				}
			
		}
		
		System.out.println("The solution is correct.");
		return true;
	}
	
	
}
