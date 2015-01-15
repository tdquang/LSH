import java.io.File;
import java.io.IOException;
import java.util.Scanner;


public class LSH 
{
	private Document[] docs;
	
	public void readFile(String location, int numToRead) throws IOException
	{
		Scanner scanner=new Scanner(new File(location));
		int numOfDocuments=scanner.nextInt();
		scanner.nextInt();
		scanner.nextInt();
		
		docs=new Document[numOfDocuments];
		for(int i=1; i<docs.length+1; i++)
		{
			docs[i]=new Document(i);
		}
		
		while(scanner.hasNextLine())
		{
			String curLine=scanner.nextLine();
			String[] splitLine=curLine.split(" ");
			if(Integer.parseInt(splitLine[0])>numToRead)
			{
				break;
			}
			docs[Integer.parseInt(splitLine[0])].add(Integer.parseInt(splitLine[1]));
		}
		
	}
	
	public int getJSimilairty(int docNum1, int docNum2)
	{
		return docs[docNum1].computeJaccardSimilairty(docs[docNum2]);
	}
	
	public static void main(String[] args)
	{
		
	}

}
