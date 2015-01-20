import java.util.Comparator;


public class DocumentComparator implements Comparator<Document> 
{
	private Document doc;
	
	public DocumentComparator(Document doc) 
	{
		this.doc = doc;
	}

	@Override
	public int compare(Document doc1, Document doc2) 
	{
		return (int) Math.round(doc2.computeJaccardSimilarity(doc)-doc1.computeJaccardSimilarity(doc));
	}
	
}
