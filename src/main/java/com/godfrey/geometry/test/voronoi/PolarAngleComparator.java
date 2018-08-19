/*
 * Copyright 2011-2014 David Godfrey. All rights reserved.
 */
package com.godfrey.geometry.test.voronoi;

import java.util.Comparator;

public class PolarAngleComparator implements Comparator<Vertex> {

	private Coordinate guidingVector;
	private Vertex Q1;
	private float magnitude;
	private Coordinate origin;
	private Vertex Q3;
	
	public PolarAngleComparator(DCELLineSegment p, Vertex Q1, Vertex Q3) {
		/**
		 * The origin is defined as the intersection point of Q1Q3 and Q2Q4
		 * The guidingVector is defined as a vector from the origin to Q3. This vector is
		 * the vector from which all other vectors along the bounding box are used to calculate
		 * polar angle.
		 * magnitude is the magnitude of the guiding vector, and is used when calculating the dot product
		 * between the guiding vector and another vector
		 */
		guidingVector = new Coordinate(p.getLowerEndpoint().getCoords().getLongitude()-p.getUpperEndpoint().getCoords().getLongitude(),
								  p.getLowerEndpoint().getCoords().getLatitude()-p.getUpperEndpoint().getCoords().getLatitude());
		
//		System.out.println("p.endPointB<"+p.getEndpointB().getCoords().getX()+","+p.getEndpointB().getCoords().getY()+">");
//		System.out.println("p.endPointA<"+p.getEndpointA().getCoords().getX()+","+p.getEndpointA().getCoords().getY()+">");
//		System.out.println("Q1<"+Q1.getCoords().getX()+","+Q1.getCoords().getY()+">");
		
		magnitude = (float) Math.sqrt(Math.pow(guidingVector.getLongitude(),2)+Math.pow(guidingVector.getLatitude(),2));
		
//		System.out.println("Magnitude of p.B-p.A: "+ magnitude);
		
		origin = p.getUpperEndpoint().getCoords();
		
		this.Q1 = Q1;
		this.Q3 = Q3;
	}
	
	public int compare(Vertex v0, Vertex v1) {

		float aCosThetaV0 = (float) Math.acos(cosTheta(v0.getCoords()));
		float aCosThetaV1 = (float) Math.acos(cosTheta(v1.getCoords()));

		if(v0.getCoords().getLongitude() < Q1.getCoords().getLongitude() && v0.getCoords().getLatitude() > Q3.getCoords().getLatitude())
			aCosThetaV0 += Math.PI;
		if(v1.getCoords().getLongitude() < Q1.getCoords().getLongitude() && v1.getCoords().getLatitude() > Q3.getCoords().getLatitude())
			aCosThetaV1 += Math.PI;

//		System.out.println(aCosThetaV0 + "    " + aCosThetaV1);
		
		if(aCosThetaV0 < aCosThetaV1)
			return 1;
		else
			if(aCosThetaV0 > aCosThetaV1)
				return -1;
			else {
				if(Float.isNaN(aCosThetaV0)) {System.out.println("aCosThetaV0 is NaN");}
				else
					if(Float.isNaN(aCosThetaV1)) {System.out.println("aCosThetaV1 is NaN");}
					else {
							System.out.println("The two polar angles are equal!!!");
							System.out.println(aCosThetaV0 + "=" + aCosThetaV1);
						}

				return 0;
			}
		
	}
	
	
	private float cosTheta(Coordinate p) {
		Coordinate vector = new Coordinate(p.getLongitude()-origin.getLongitude(),p.getLatitude()-origin.getLatitude());
//		System.out.println("vector<"+vector.getX()+","+vector.getY()+">");
		double d = (magnitude*Math.sqrt(vector.getLongitude()*vector.getLongitude()+vector.getLatitude()*vector.getLatitude()));
//		System.out.println("d: "+d);
		double n = (guidingVector.getLongitude()*vector.getLongitude()+guidingVector.getLatitude()*vector.getLatitude());
//		System.out.println("n: " + n);
		return (float) (n/d);
	}

}
