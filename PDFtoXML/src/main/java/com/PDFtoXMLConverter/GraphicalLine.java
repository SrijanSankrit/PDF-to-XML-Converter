package com.PDFtoXMLConverter;

public class GraphicalLine implements Comparable<GraphicalLine> {

	public float X1 = -1;
	public float Y1 = -1;
	public float X2 = -1;
	public float Y2 = -1;
	
	public int compareTo(GraphicalLine temp)			//comparison with another block based on their Y positions
	{
		if(this.Y2 < temp.Y2)
			return -1;
		else if(this.Y2 == temp.Y2)
			return 0;
		else 
			return 1;
		
	}
	
	public void init()
	{
		if(Y2<Y1)
		{
			//System.out.println("INIT " + Y2 + " " + Y1);
			float temp = Y2;
			Y2 = Y1;
			Y1 = temp;
//			System.out.println("INIT " + Y2 + " " + Y1);
		}
		if(X2<X1)
		{
			float temp = X2;
			X2 = X1;
			X1 = temp;			
		}
		
	}
	
}