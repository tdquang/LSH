import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.util.HashSet;
import java.util.Random;
import java.util.Scanner;



public class LSH 
{
	private Document[] docs;
	private Random random;
	private static String loc="C:\\Users\\Andrew\\Desktop\\docword.enron.txt";

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
			lsh.readFile(loc, 500);
		}
		catch(IOException e)
		{
			System.out.println(e);
		}
		System.out.println(lsh.getJSimilairty(24, 340));
		int[][] signatureMatrix=lsh.computeSignatureMatrix(1000);
		for(int i=0; i<signatureMatrix.length; i++)
		{
			for(int j=0; j<signatureMatrix[0].length; j++)
			{
				System.out.print(signatureMatrix[i][j]+" ");
			}
			System.out.print("\n");
		}
	}

	public int[][] computeSignatureMatrix(int numOfHashFunctions)
	{
		HashSet<Integer> unionOfAll=new HashSet<Integer>();
		for(int j=1; j<docs.length; j++)
		{
			unionOfAll.addAll(docs[j].getSet());
		}
		int numOfUniqueWords=unionOfAll.size();

		int[][] signatureMatrix= new int[numOfHashFunctions][docs.length];

		for(int i=0; i<numOfHashFunctions; i++)
		{
			for(int j=1; j<docs.length; j++)
			{
				signatureMatrix[i][j-1]=numOfUniqueWords+1;
			}
		}


		HashFunction[] funcs=new HashFunction[numOfHashFunctions];
		for(int i=0; i<funcs.length; i++)
		{
			funcs[i]=new HashFunction(random, numOfUniqueWords);
		}

		int seenWords=0;
		for(Integer currentWord: unionOfAll)
		{
			for(int j=1; j<docs.length; j++)
			{
				if(docs[j].hasWord(currentWord))
				{
					for(int i=0; i<funcs.length; i++)
					{
						int hashedValue=funcs[i].applyHashFunction(seenWords);
						if(hashedValue<signatureMatrix[i][j-1])
						{
							signatureMatrix[i][j-1]=hashedValue;
						}
					}
				}
			}
			seenWords++;
		}


		return signatureMatrix;
	}




}
