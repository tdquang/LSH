/*  Authors: Andrew Elenbogen, Quang Tran
	File: Documents.java
	Date: January 17th 2015
	Description: Stores words present in each document
*/

import java.util.HashSet;


public class Document 
{
	private HashSet<Integer> set;
	private int docNum;
	
	public Document(int docNum)
	{
		this.docNum=docNum;
		set=new HashSet<Integer>();
	}
	
	public void add(int toAdd)
	{
		set.add(toAdd);
	}
	
	public HashSet<Integer> getSet()
	{
		return set;
	}
	
	// Returns the union of two documents
	public HashSet<Integer> getUnion(Document anotherDoc)
	{
		HashSet<Integer> union=new HashSet<Integer>(set);
		union.addAll(anotherDoc.getSet());
		return union;
	}
	
	// Computes Jaccard Similarity by dividing the intersection of words of two documents by the union of their words
	public double computeJaccardSimilarity(Document anotherDoc)
	{
		HashSet<Integer> intersect=new HashSet<Integer>(set);
		intersect.retainAll(anotherDoc.getSet());
		
		return ( (double) intersect.size()/ (double) getUnion(anotherDoc).size() );
	}
	
	public boolean hasWord(int word)
	{
		return set.contains(word);
	}
	
	public String toString()
	{
		return docNum+" "+set;
	}
}
