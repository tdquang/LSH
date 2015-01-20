/**  Authors: Andrew Elenbogen, Quang Tran
	File: Documents.java
	Date: January 17th 2015
	Description: Stores words present in each document. A pretty simple data storage class that isn't much beyond a wrapper for a set and 
	an integer id.
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
	
	// Computes Jaccard Similarity of this document and another given document
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
		return "Document Number: "+docNum+" "+set;
	}
}
