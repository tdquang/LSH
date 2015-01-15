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
	
	public double computeJaccardSimilairty(Document anotherDoc)
	{
		HashSet<Integer> intersect=new HashSet<Integer>(set);
		intersect.retainAll(anotherDoc.getSet());
		HashSet<Integer> union=new HashSet<Integer>(set);
		union.addAll(anotherDoc.getSet());
		
		return ( (double) intersect.size()/ (double) union.size() );
	}
	
	public String toString()
	{
		return docNum+" "+set;
	}
}
