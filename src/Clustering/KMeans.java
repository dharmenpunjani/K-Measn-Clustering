package Clustering;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.StringTokenizer;

public class KMeans 
{
	
	int K = 0;//Number of the clusters 
	List<Point> pnts = new ArrayList<Point>();//List of all points
	List<Point> centers = new ArrayList<Point>();//List of the centeroids 
	double dist[][]; //Distance Matrix of Points to the centroids 
	List<Cluster> clstr = new ArrayList<Cluster>();//List of the clusters
	List<Cluster> uciclstr = new ArrayList<Cluster>();//List of the cluster according to the UCI 
	List<Point> mean = new ArrayList<Point>();//List of the old Mean Points 
	List<Point> mean1 = new ArrayList<Point>();//List of the new Mean Points
	double sm[][];
	
	
	public int IntersectionOf(int a,int b)
	{
		int equal = 0;
		for(int i=0;i<pnts.size();i++)
		{
			if((pnts.get(i).uci_id == a) && (pnts.get(i).cl_Id == b))
			{
				equal++;
			}
		}
		return equal;
	}
	
	public int UnionOf(int a,int b)
	{
		int uni = 0;
		for(int i=0;i<pnts.size();i++)
		{
			if((pnts.get(i).uci_id == a) || (pnts.get(i).cl_Id == b))
			{
				uni++;
			}
		}
		return uni;
	}
	// Calculate the accuracy of generated clusters using Similarity matrix 
	public void calcAccuracy()
	{
		int uciSize = uciclstr.size();
		int algoSize = clstr.size();
		int max = uciSize>algoSize?uciSize:algoSize;
		sm = new double[uciSize][algoSize];
		double sum = 0;
		for(int i=0;i<uciSize;i++)
		{
			for(int j=0;j<algoSize;j++)
			{
				sm[i][j] = (double)IntersectionOf(i, j) / (double)UnionOf(i, j) ;
				sum+=sm[i][j];
			}
		}
		sum = (sum / max)*100 ;
		System.out.println("Accuracy: " + sum);
	}
	
	public static void main(String s[]) throws IOException
	{
		KMeans kclst = new KMeans();//Object of the class KMeans
		//Checks proper parameters of input
		if(s.length<3)
		{
			System.out.println("Usage:java Kmeans inputfile(fullpath) outputfile(fullpath) Seed(Base for random number (numeric value))");
			System.exit(0);
		}
		kclst.readPointsFromFile(s[0]);//reads the points from the file and add the points to the list of points(List<Point> pnts)
		//calculates the mean of all the clusters of the UCI
		for(int i=0;i<kclst.uciclstr.size();i++)
			kclst.uciclstr.get(i).calcMean();
		//kclst.K=Integer.parseInt(s[1]);
		kclst.initialize(kclst.pnts,Integer.parseInt(s[2]));//Select the random points as the initial centroids and create the initial cluster without data points but with randomly selected centroids and take them as initial mean values. 
		long start,end;//for time comparison 
		start = System.currentTimeMillis();//start time of algorithm
		kclst.startAlgo();//runs the algorithm
		end = System.currentTimeMillis();//end time of algorithm
		String fname = s[1].substring(0, s[1].length()-4);//for output filename
		File f = new File(fname+"Algo.txt");//output file with the clusters generated by the algorithm
		File f1 = new File(fname+"UCI.txt");//output file with the clusters according to the UCI
		FileWriter fw1 = new FileWriter(f);//file writer 
		FileWriter fw2 = new FileWriter(f1);//file writer 
		BufferedWriter bw1 = new BufferedWriter(fw1);
		BufferedWriter bw2 = new BufferedWriter(fw2);
		for(int l = 0; l<kclst.clstr.size();l++)
		{
			bw1.newLine();//Writes new line to the file
			bw1.write(kclst.clstr.get(l).getCluster());//writes one cluster to the file
		}
		for(int l = 0; l<kclst.uciclstr.size();l++)
		{
			bw2.newLine();
			bw2.write(kclst.uciclstr.get(l).getCluster());
		}
		//closes the all streams to the both the files 
		bw1.close();
		bw2.close();
		fw1.close();
		fw2.close();
		for(int i=0;i<kclst.pnts.size();i++)
		{
			kclst.pnts.get(i).printPointClusterId();
		}
		kclst.calcAccuracy();
		//System.out.println("Accuracy is : " + kclst.calcAccuracy());
		System.out.println("Total Time taken is : " + (end - start)+" Mili Seconds");//prints the time taken by the algorithm
	}
	//Reads data from files from UCI Machine Learning Repository numerical only with CSV (Comma Saperated Value) format 
	// Considering last attribute as a cluster lable.
	public boolean readPointsFromFile(String fname) throws IOException
	{
		boolean val = false;//weather the file is properly read or not 
		FileInputStream fin = new FileInputStream(fname);//takes input stream from file 
		BufferedReader bin = new BufferedReader(new InputStreamReader(fin));// takes input from fileinutstream
		String line = "";//To store the line read from file 
		List<String> cls = new ArrayList<>();//List of the lables of the cluster in UCI database
		int k=0;//Number of lines read
		int uci_id = -1;
		while((line=bin.readLine())!=null)
		{
			StringTokenizer st = new StringTokenizer(line,",");//line read from file is tokenized using "," Coma as seperator  
			List<Double> vec = new ArrayList<Double>();//List of the elements of the vector read from the line
			String s = "";//will hold a single token/element form stringtokenizer/vector
			int len = st.countTokens();//no. of tokens/elements of the vector in single point
			int i=0;// no. of elements in a vector
			//will read all the elements from a line and store them in  List<Double> vec execpt the last one Because last one is the lable of the cluster 
			while(st.hasMoreTokens())
			{
				s = st.nextToken();
				i++;
				if(i<len)
				vec.add(Double.parseDouble(s));
			}
			//will create the clusters according to the UCI cluster labels  
			Point p1 = new Point(vec);
			if(k==0)//for the first line read from the file
			{
				cls.add(s);//adds the label of the cluster to the list of lables
				uci_id = cls.indexOf(s);
				Cluster cl = new Cluster();//creates the cluster 
				cl.lab = s;//give the label 
				cl.addPoint(p1);//add the point to the cluster 
				uciclstr.add(cl);//add the cluster to the list of the UCI cluster
			}
			else //for rest of the lines read from files
			{
				if(cls.contains(s))//if the label is in the list 
				{
					for(int j=0;j<uciclstr.size();j++)
					{
						if(s.equals(uciclstr.get(j).lab))
						{
							uci_id = cls.indexOf(s);
							uciclstr.get(j).addPoint(p1); //add the point to the same labeled cluster 
						}
					}
				}
				else////if the label is not in the list
				{
					cls.add(s);//adds the label of the cluster to the list of lables
					uci_id = cls.indexOf(s);
					Cluster cl = new Cluster();//creates the cluster 
					cl.lab = s;//give the label 
					cl.addPoint(p1);//add the point to the cluster 
					uciclstr.add(cl);//add the cluster to the list of the UCI cluster
				}
			}
			//add the point to the Main list of all the points 
			Point p = new Point(vec);
			p.uci_id = uci_id;
			pnts.add(p);
			k++;
		}
		K = cls.size();//set the no. of cluster K
		return val;
	}
	
	
	//Randomly select the clusters centers initially 
	public void initialize(List<Point> pnts,int n)
	{//int n is the number provided by the user and will be used to generate random number
		Random r = new Random(100000);
		int seed = (n*r.nextInt()) % pnts.size();//generates random number
		if(seed<0)
			seed*=-1;
		//Select the random centroids and add them to the list of initial centroids and mean-valuse(mean1)
		//Creates initial cluster and set selected random points as centers of the clusters 
		for(int i=0;i<K;i++)
		{
			Point p = pnts.get((i+seed));//selects the point of the random number generated from the list of available points 
			centers.add(p);//add the point to the list of centeroids 
			mean1.add(p);//add the point to the list of Mean
			Cluster cl = new Cluster();//create new cluster 
			cl.center = p;//set the selected point as a center of the cluster 
			clstr.add(cl);// add the cluster in the list of the available clusters 
		}
		System.out.println("Initially Randomly Selected Centroids are:");
		for(int i=0;i<clstr.size();i++)
		{
			System.out.println(clstr.get(i).center.print());
		}
		dist = new double[pnts.size()][K+1];//set the dimentions of the Distance Matrix of points to the centroids 
	}
	//checks weather two List of Points are equal or not (for comparing the older and new Mean values)
	public boolean isEqual(List<Point> m1,List<Point>  m2)
	{
		boolean val = true;
		for(int i=0;i<m1.size();i++)
		{
			Point p1=m1.get(i),p2=m2.get(i);
			if(!p1.isEqual(p2))
			{
				val = false;
				break;
			}
		}
		return val;
	}
	
	// prints current cluster mean points
	public void printMeans()
	{
		System.out.println("----------------------------------------------");
		for(int i=0;i<mean.size();i++)
		{
			System.out.println("Mean Point-"+ i+" :"+ mean.get(i).print());
		}
		System.out.println("----------------------------------------------");
		for(int i=0;i<mean1.size();i++)
		{
			System.out.println("Mean1 Point-"+ i+" :"+ mean1.get(i).print());
		}
		System.out.println("----------------------------------------------");
	}
	// Main procedure to follow of K-Means 
	public void startAlgo()
	{
		do
		{
			mean.removeAll(mean);// clears the list of the old mean points
			//clears the list of points in all the available cluster 
			for(int i=0;i<clstr.size();i++)
			{
				clstr.get(i).reMoveAll();
			}
			findDist(pnts, centers);// Fill the distance matrix of points to the centroids 
			assignCluster(pnts, centers);// assigns the points to the nearest cluster
			//Copies the current mean values to the old mean values.
			for(int i=0;i<clstr.size();i++)
			{
				mean.add(mean1.get(i));
			//	clstr.get(i).printCluster();
			}
			mean1.removeAll(mean1);//clears the new mean values
			//calculates the New mean values and add them to the new mean values list of points (mean1)
			for(int i=0;i<clstr.size();i++)
			{
				Point t = new Point();
				t = clstr.get(i).calcMean();
				mean1.add(t);
			}
			//clears the list of the currents centers 
			centers.removeAll(centers);
			//assign new centers to the list of the centroid
			for(int i=0;i<mean1.size();i++)
			{
				//System.out.print(mean1.get(i).print());
				centers.add(clstr.get(i).meanPoint);
			}
			//printMeans();
		}while(!(isEqual(mean, mean1)));//termination condition comparison of old and new mean values if equal than terminated 
	}
	//fill the distance matrix and stores the index of center with minimum distance to the point
	public void findDist(List<Point> pts,List<Point> cnt )
	{
		int i,j;
		for( i=0;i<pts.size();i++)
		{
			double min = Double.POSITIVE_INFINITY;
			int ind = -1;
			for( j=0; j<cnt.size();j++)
			{
					dist[i][j]= Point.distanceXY(cnt.get(j), pts.get(i));
					if(dist[i][j]<=min)
					{
						min = dist[i][j];
						ind = j;
					}
			}
			dist[i][j] = ind;
		}
	}
	// assigns the points to the nearest cluster
	public void assignCluster(List<Point> pts,List<Point> cnt)
	{
		for(int i=0;i<pts.size();i++)
		{
			clstr.get((int)dist[i][cnt.size()]).addPoint(pts.get(i));
			pts.get(i).cl_Id = (int)dist[i][cnt.size()];
		}
	}
}