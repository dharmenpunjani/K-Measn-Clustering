package Clustering;

import java.util.List;

//Data structure for the Point
public class Point
{
	public List<Double> points;// List of the element of the vector
	public Point(){}//default constructor
	//constructor with list of the elements of the Point
	public int cl_Id = -1;//Id of the cluster
	public int uci_id = -1;
	public Point(List<Double> lstp)
	{
		points = lstp;
	}
	//calculate the eucledian Distance between two point X and Y
	public static double distanceXY(Point x,Point y)
	{
		double dist = 0.0;
		for(int i=0;i<x.points.size();i++)
		{
			dist += Math.pow((x.points.get(i)-y.points.get(i)),2);
		}
		dist = Math.sqrt( dist);
		return dist;
	}
	
	public void printPointClusterId()
	{
		System.out.println("Uci_Cluster: "+uci_id+" This algo Cluster : " + cl_Id);
	}
	
	//Return the list of the elements of the points in a string
	public String print()
	{
		String rep ="";
		for(int i=0;i<points.size();i++)
		{
			rep += ":"+points.get(i);
		}
		return rep;
	}
	//compares two points weather they are equal or not if yes then return true else false
	public boolean isEqual(Point p)
	{
		boolean ret = true;
		for(int i=0;i<points.size();i++)
		{
			if(!this.points.contains(p.points.get(i)))
			{
				ret=false;
				break;
			}
		}
		return ret;
	}
}