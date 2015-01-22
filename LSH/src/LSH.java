/** Authors: Andrew Elenbogen, Quang Tran
   File: LSH.java
   Date: January 17th 2015
   Description: Main class for our LSH assignment. Calculates Jaccard Similarity with and without hash functions' values. 

   NOTE TO GRADER: You can find the data file's location at the top of this class
 */

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Random;
import java.util.Scanner;

public class LSH 
{
	private Document[] docs;
	private Random random;
	private ArrayList<HashMap<ArrayList<Integer>, ArrayList<Integer>>> arrayOfCandidateMaps;
	private HashMap<Integer, ArrayList<ArrayList<Integer>>> reverseList;
	/*This is the data file's location, stored as a constant*/
	//private static final String DATAFILE_LOCATION="/Users/dangquang2011/LSH/LSH/src/docword.enron.txt";
	private static final String DATAFILE_LOCATION="C:\\Users\\Andrew\\Desktop\\docword.enron.txt";

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

	/** Computes Jaccard Similarity based on signature matrix
	*@param signatureMatrixRows
	*@param doc1 The unique id of the first doc to perform the calculations on
	* @param doc2 The unique id of the second doc to perform the calculations on
	*/
	public double useSignatureMatrixForJaccardSimilairty(int signatureMatrixRows, int docNum1, int docNum2)
	{
		int[][] signatureMatrix=computeSignatureMatrix(signatureMatrixRows);
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
	/** Computes the signature matrix
	@param signatureMatrixRows The number of rows in the signature matrix
	 */
	public int[][] computeSignatureMatrix(int signatureMatrixRows)
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
		return signatureMatrix;
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
			if(i != docId)
			{
				queue.add(docs[i]);
				if(queue.size()>k)
				{
					queue.poll();
				}
			}
		}
		return getAverageSimilarity(queue, docId);
	}
	
	private double getAverageSimilarity(PriorityQueue<Document> queue, int docId)
	{
		double total=0.0; 
		int k=queue.size();
		while(queue.size()>0)
		{
			Document neighbor=queue.poll();
			total+=docs[docId].computeJaccardSimilarity(neighbor);
		}
		return total/k;
	}
	
	/* Computes average of average similarities for each document using brute force
	* @param number of k nearest neighbors 
	*/
	public double averageOfAveragesBruteForce(int k)
	{
		double totalOfAverages=0;
		for(int i=1; i<docs.length; i++)
		{
			totalOfAverages+=kNearestNeighbors(k, i);
		}
		return totalOfAverages/(docs.length-1);
	}
	/** Computes the average of average similarities for each document using LSH
	*@param number of k nearest neighbors 
	*@param number of rows in the signature matrix
	*@param r number of rows per band
	*/
	public double averageOfAveragesLSH(int k, int numOfRows, int r)
	{
		double totalOfAverages=0;
		for(int i=1; i<docs.length; i++)
		{
			totalOfAverages+=nearestNeighborsLSH(k, numOfRows, r, i);
		}
		return totalOfAverages/(docs.length-1);
	}
	
	/** Computes average similarity for a specific document
	 * @param k number of near neighbors
	 * @param r ruber of rows per band
	 * @param id of the specific document whose neighbors we are looking at
	*/
	public double nearestNeighborsLSH(int numberOfNearNeighbors, int numOfRows, int r, int docIdToFindNeighborsOf)
	{
		// Number of neighbors cannot be larger than number of documents - 1
		if(numberOfNearNeighbors>docs.length-1)
		{
			numberOfNearNeighbors=docs.length-1;
		}
		// Creating arrayOfCandidateMaps an array list of hash maps, with each hash map corresponding to a band. 
		// Each hashmap contains vectors as keys and documents id as values
		// Creating reverseList, which is a hashmap with document id as keys and vectors within that document as values
		if(arrayOfCandidateMaps==null)
		{
			int[][] signatureMatrix=computeSignatureMatrix(numOfRows);
			arrayOfCandidateMaps=new ArrayList<HashMap<ArrayList<Integer>, ArrayList<Integer>>>(numOfRows/r);
			reverseList=new HashMap<Integer, ArrayList<ArrayList<Integer>>>();

			for(int l=0; l< numOfRows/r; l++)
			{
				//If the HashMap that will be used hasn't been created, intialize it
				if(l==arrayOfCandidateMaps.size())
				{
					arrayOfCandidateMaps.add(new HashMap<ArrayList<Integer>, ArrayList<Integer>>());
				}
				for(int j=0; j< signatureMatrix[0].length; j++)
				{
					//For each row, running once every r rows
					for(int i=0; i<signatureMatrix.length; i+=r)
					{
						ArrayList<Integer> vector=new ArrayList<Integer>();
						//Place all of the r rows into the signature matrix
						for(int k=0; k<r; k++)
						{	if(i+k<signatureMatrix.length)
								vector.add(signatureMatrix[i+k][j]);
						}
						if(arrayOfCandidateMaps.get(l).get(vector)==null)
							arrayOfCandidateMaps.get(l).put(vector, new ArrayList<Integer>());
						//+1 because there is nothing in docs[0]
						arrayOfCandidateMaps.get(l).get(vector).add(j+1);
						if(reverseList.get(j+1)==null)
							reverseList.put(j+1, new ArrayList<ArrayList<Integer>>());
						reverseList.get(j+1).add(vector);
					}
				}
			}
		}
		// Creating Hashset of possible candidates
		HashSet<Integer> possibleCandidates = new HashSet<Integer>();
		// Adding documents that appear in the same bucket as the desired document
		for(int i=0; i< arrayOfCandidateMaps.size(); i++)
		{
			possibleCandidates.addAll(arrayOfCandidateMaps.get(i).get(reverseList.get(docIdToFindNeighborsOf).get(i)));
		}
	
		possibleCandidates.remove(docIdToFindNeighborsOf);
		
		while(possibleCandidates.size()<numberOfNearNeighbors)
		{
			possibleCandidates.add(random.nextInt(docs.length-1)+1);
			possibleCandidates.remove(docIdToFindNeighborsOf);
		}
		//Use a priority queue to quickly find the best k canidates
		PriorityQueue<Document> queue=new PriorityQueue<Document>(numberOfNearNeighbors, new DocumentComparator(docs[docIdToFindNeighborsOf]));

		for(Integer candidate: possibleCandidates )
		{
			queue.add(docs[candidate]);
			if(queue.size()>numberOfNearNeighbors)
			{
				queue.poll();
			}
		}
		return getAverageSimilarity(queue, docIdToFindNeighborsOf );		
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

			int k;
			System.out.print("Enter in the number of nearest neighbors k: ");
			k = userInputScanner.nextInt();
			
			int signatureMatrixNumOfRows;
			System.out.print("Enter in number of rows to use for signature matrix: ");
			signatureMatrixNumOfRows = userInputScanner.nextInt();

			int r;
			System.out.print("Enter in number of rows r to use for each band: ");
			r = userInputScanner.nextInt();	
			
			long startTime, endTime;
			
			startTime=System.currentTimeMillis();
			System.out.println("Average of averages using brute force: "+lsh.averageOfAveragesBruteForce(k));
			endTime=System.currentTimeMillis();
			System.out.println("Time taken: "+((double) endTime-(double) startTime)/1000.0);
			startTime=System.currentTimeMillis();
			System.out.println("Average of averages using lsh: "+lsh.averageOfAveragesLSH(k, signatureMatrixNumOfRows, r));
			endTime=System.currentTimeMillis();
			System.out.println("Time taken: "+((double) endTime-(double)startTime)/1000.0);
		}

	}


}
