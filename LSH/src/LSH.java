/** Authors: Andrew Elenbogen, Quang Tran
   File: LSH.java
   Date: January 17th 2015
   Description: Main class for our LSH assignment. Calculates Jaccard Similarity with and without hash functions' values. 

   NOTE TO GRADER: You can find the data file's location at the top of this class
 */

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Random;
import java.util.Scanner;

public class LSH 
{
	private Document[] docs;
	private Random random;
	/*This is the data file's location, stored as a constant*/
	private static final String DATAFILE_LOCATION="/Users/dangquang2011/LSH/LSH/src/docword.enron.txt";

	/*This is the RNG's Seed, set to 4 to make testing easier. Set it to null to make java calculate rather than it being constant.*/
	private static final Integer RNG_SEED=4;

	public LSH()
	{
		if(RNG_SEED!=null)
			random=new Random(LSH.RNG_SEED);
		else
			random=new Random();
	}

	/** Opens the data file and reads the data from the first numToRead documents 
	 * @param numToRead The number of documents to read from the given file
	 * @param location The path to the file from which document information should be read.
	 * */
	public void readFile(String location, int numToRead) throws IOException
	{
		try(Scanner scanner=new Scanner(new File(location)))
		{
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
	}

	/** Calculates Jaccard Similarity by dividing the number of words that both documents share by the union of words of the 2 documents
	 * @param doc1 The unique id of the first doc to perform the calculations on
	 * @param doc2 The unique id of the second doc to perform the calculations on
	 */
	public double getJSimilarity(int docNum1, int docNum2)
	{
		return docs[docNum1].computeJaccardSimilarity(docs[docNum2]);
	}

	/** Computes the signature matrix, and then returns the Jaccard Similarity by dividing 
	the number of identical hash values corresponding to the 2 documents by the total number of hash functions. Note that
	the signature Matrix itself is not output or returned.
	@param signatureMatrixRows The number of rows in the signature matrix.
	@param doc1 The unique id of the first doc to perform the calculations on
	@param doc2 The unique id of the second doc to perform the calculations on
	 */
	public double computeSignatureMatrix(int signatureMatrixRows, int docNum1, int docNum2)
	{
		HashSet<Integer> unionOfAll=new HashSet<Integer>();
		for(int j=1; j<docs.length; j++)
		{
			unionOfAll.addAll(docs[j].getSet());
		}
		int numOfUniqueWords=unionOfAll.size();

		int[][] signatureMatrix= new int[signatureMatrixRows][docs.length];

		for(int i=0; i<signatureMatrixRows; i++)
		{
			for(int j=1; j<docs.length; j++)
			{
				signatureMatrix[i][j-1]=numOfUniqueWords+1;
			}
		}


		HashFunction[] funcs=new HashFunction[signatureMatrixRows];
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
		// Now that we got our signatureMatrix[i][j], we can estimate the Jaccard Similarity by dividing number of identical hash values by the total
		int num_identical = 0;
		for(int i=0; i<signatureMatrixRows; i++)
		{
			if (signatureMatrix[i][docNum1-1] == signatureMatrix[i][docNum2-1]) {
				num_identical++;
			}
		}

		return ((double) num_identical/ (double) signatureMatrixRows);
	}
	
	/**
	 * 
	 * @param k
	 * @param docId
	 * @return The average Jaccard similairty of the k nearestNeighbors
	 */
	public double kNearestNeighbors(int k, int docId)
	{
		PriorityQueue<Document> queue=new PriorityQueue<Document>(k, new DocumentComparator(docs[docId]));
		
		for(int i=1; i<docs.length; i++)
		{
			queue.add(docs[i]);
			if(queue.size()>k)
			{
				queue.poll();
			}
		}
		double total=0.0; 
		while(queue.size()>0)
		{
			total+=docs[docId].computeJaccardSimilarity(queue.poll());
		}
		return total/k;
	}
	
	public double averageOfAverages(int k)
	{
		double totalOfAverages=0;
		for(int i=1; i<docs.length; i++)
		{
			totalOfAverages+=kNearestNeighbors(k, i);
		}
		return totalOfAverages/(docs.length-1);
	}

	public static void main(String[] args)
	{
		LSH lsh = new LSH();
		try(Scanner userInputScanner = new Scanner(System.in))
		{
			// Prompting for number of documents
			int numOfDocs;
			System.out.print("Enter number of documents to read: ");
			numOfDocs = userInputScanner.nextInt();

			try
			{
				lsh.readFile(DATAFILE_LOCATION, numOfDocs);
			}
			catch(IOException e)
			{
				System.out.println(e);
			}

			// Prompting for first ID
			int firstID;
			System.out.print("Enter the first document's id number: ");
			firstID = userInputScanner.nextInt();
			// Prompting for second ID
			int secondID;
			System.out.print("Enter the second document's id number: ");
			secondID = userInputScanner.nextInt();		

			System.out.println("Jaccard similarity: " +lsh.getJSimilarity(firstID, secondID));
			//Prompting for number of hash functions
			int signatureMatrixNumOfRows;
			System.out.print("Enter in number of rows to use for signature matrix: ");
			signatureMatrixNumOfRows = userInputScanner.nextInt();

			double similarity = lsh.computeSignatureMatrix(signatureMatrixNumOfRows, firstID, secondID);
			System.out.println("Jaccard similarity based on signature matrix: " +similarity);
		}

	}


}
