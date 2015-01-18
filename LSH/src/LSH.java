/* Authors: Andrew Elenbogen, Quang Tran
   File: LSH.java
   Date: January 17th 2015
   Description: Main class for our LSH assignment. Calculates Jaccard Similarity with and without hash functions' values. 
*/

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
	/* BELOW IS THE STRING WITH THE DATA FILE LOCATION */
	private static String loc="/Users/dangquang2011/LSH/LSH/src/docword.enron.txt";

	public LSH()
	{
		random=new Random(4);
	}

	/* readFile method. Opens the data file and reads the data from numToRead first documents */
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

		scanner.nextLine();  
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

	/* getJSimilarity returns the Jaccard Similarity by dividing 
	the number of words that both documents share by the union of words of the 2 documents
	 */
	public double getJSimilarity(int docNum1, int docNum2)
	{
		return docs[docNum1].computeJaccardSimilarity(docs[docNum2]);
	}

	/* computeSignatureMatrix computers the signature matrix, and then returns the Jaccard Similarity by dividing 
	the number of identical hash values corresponding to the 2 documents by the total number of hash functions
	*/
	public double computeSignatureMatrix(int numOfHashFunctions, int docNum1, int docNum2)
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
		// Now that we got our signatureMatrix[i][j], we can calculate the Jaccard Similarity by dividing number of indentical hash values by the total
		int num_identical = 0;
		for(int i=0; i<numOfHashFunctions; i++)
		{
			if (signatureMatrix[i][docNum1-1] == signatureMatrix[i][docNum2-1]) {
				num_identical++;
			}
		}

		return ((double) num_identical/ (double) numOfHashFunctions);
	}

	public static void main(String[] args)
	{
		LSH lsh = new LSH();
		Scanner user_input = new Scanner(System.in);
		// Prompting for number of documents
		int num_of_doc;
		System.out.print("Enter number of documents to read: ");
		num_of_doc = user_input.nextInt();

		try
		{
			lsh.readFile(loc, num_of_doc);
		}
		catch(IOException e)
		{
			System.out.println(e);
		}

		// Prompting for first ID
		int firstID;
		System.out.print("Enter the first document's id number: ");
		firstID = user_input.nextInt();
		// Prompting for second ID
		int secondID;
		System.out.print("Enter the second document's id number: ");
		secondID = user_input.nextInt();		

		System.out.println("Jaccard similarity: " +lsh.getJSimilarity(firstID, secondID));
		//Prompting for number of hash functions
		int sm_num_of_rows;
		System.out.print("Enter in number of rows to use for signature matrix: ");
		sm_num_of_rows = user_input.nextInt();
		
		double similarity = lsh.computeSignatureMatrix(sm_num_of_rows, firstID, secondID);
		System.out.println("Jaccard similarity based on signature matrix: " +similarity);

	}


}
