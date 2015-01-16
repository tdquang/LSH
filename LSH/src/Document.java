import java.util.ArrayList;
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
	
	public HashSet<Integer> getUnion(Document anotherDoc)
	{
		HashSet<Integer> union=new HashSet<Integer>(set);
		union.addAll(anotherDoc.getSet());
		return union;
	}
	
	public double computeJaccardSimilairty(Document anotherDoc)
	{
		HashSet<Integer> intersect=new HashSet<Integer>(set);
		intersect.retainAll(anotherDoc.getSet());
		
		return ( (double) intersect.size()/ (double) getUnion(anotherDoc).size() );
	}
	
	public int[][] computeSignatureMatrix(Document anotherDoc)
	{
		int numOfRows=getUnion(anotherDoc).size();
		
		boolean[][] toReturn= new boolean[numOfRows][2];
		
		
		return toReturn;
	}
	
	public String toString()
	{
		return docNum+" "+set;
	}
}
