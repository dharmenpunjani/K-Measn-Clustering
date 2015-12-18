package Clustering;

import java.util.ArrayList;
import java.util.List;

//Data structure for cluster
public class Cluster
{
	public Point center; // center of the cluster
	List<Point> pl = new ArrayList<Point>();//list of all the points of the cluster
	public String lab;//label of the cluster
	public Point meanPoint = new Point();//Mean point of the cluster
	//add the point to the list of points
	public void addPoint(Point x)
	{
		pl.add(x);
	}
	//sets the center of the cluster 
	public void setCenter(Point p)
	{
		center = p;
	}
	//Removes the point from the list
	public void removePoint(Point x)
	{
		pl.remove(pl.indexOf(x));
	}
	//remove all the points from the list from the cluster
	public void reMoveAll()
	{
		pl.clear();
	}
	public void printCluster()
	{
		System.out.println("Cluster with Center Point : " + center.print());
		System.out.println("----------------------------------------------");
		for(int i = 0;i<pl.size();i++)
		{
			System.out.println(pl.get(i).print());
		}
		System.out.println("----------------------------------------------");
	}
	//same as print cluster but returns the output in string except printing in console 
	public String getCluster()
	{
		String line = "";
		line += "Cluster with Center Point : " + center.print();
		line += "\n----------------------------------------------";
		for(int i = 0;i<pl.size();i++)
		{
			line+="\n"+pl.get(i).print();
		}
		line+="\n----------------------------------------------";
		return line;
	}
	//calculates the mean of the cluster from the current list of the points
	public Point calcMean()
	{
		int cSize = pl.size(),i;
		Point temp = new Point();
		temp.points = new ArrayList<Double>();
		double vec[] = new double[pl.get(0).points.size()];
		for( i = 0;i<cSize;i++)
		{
			for(int j=0;j<vec.length;j++)
			{
				vec[j] += pl.get(i).points.get(j);
			}
		}
		for(int j=0;j<vec.length;j++)
		{
			vec[j] /= i;
			temp.points.add(vec[j]);
		}
		meanPoint = temp;
		setCenter(meanPoint);
		return temp;
	}
}
