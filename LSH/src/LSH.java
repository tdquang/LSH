import java.io.File;
import java.io.IOException;
import java.util.Random;
import java.util.Scanner;


public class LSH 
{
	private Document[] docs;
	private Random random;
	
	public LSH()
	{
		random=new Random(4);
	}
	
	public void readFile(String location, int numToRead) throws IOException
	{
		Scanner scanner=new Scanner(new File(location));
		int numOfDocuments=scanner.nextInt();
		scanner.nextInt();
		scanner.nextInt();
		
		if(numToRead>numOfDocuments)
			numToRead=numOfDocuments;
		
		docs=new Document[numToRead+1];
		for(int i=1; i<docs.length; i++)
		{
			docs[i]=new Document(i);
		}
		
		scanner.nextLine(); //This was not my idea -Andrew 
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
		
		for(int i=1; i<docs.length; i++)
		{
			System.out.println(docs[i]);
		}
		
	}
	
	public double getJSimilairty(int docNum1, int docNum2)
	{
		return docs[docNum1].computeJaccardSimilairty(docs[docNum2]);
	}
	
	public static void main(String[] args)
	{
		LSH lsh=new LSH();
		try
		{
			lsh.readFile("/tmp/docword.enron.txt", 500);
		}
		catch(IOException e)
		{
			System.out.println(e);
		}
		System.out.println(lsh.getJSimilairty(24, 340));
	}

}
