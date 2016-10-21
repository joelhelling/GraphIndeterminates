package it.unipi.di.interfaces;

public interface SearchAlg {

    void search();

    void setTimeLimit(long limit);
    
	void setAborted();

	void printStats();
	
	Object solution();
}
