/*
 * Copyright 2011-2014 David Godfrey. All rights reserved.
 */
package com.godfrey.geometry.linesegment;

import com.godfrey.geometry.dcel.vertex.Vertex;
import com.godfrey.geometry.point.Point;

public class LineSegment {

	private Vertex begin;
	private Vertex end;
	
	public LineSegment(Vertex endpointA, Vertex endpointB) {
		this.setEndpointA(endpointA);
		this.setEndpointB(endpointB);
	}

	public LineSegment() {
		begin = null;
		end = null;
	}

	//Test intersect method.
	public Vertex getIntersection(LineSegment right) {
		double d = ((right.end.getCoords().getY() - right.begin.getCoords().getY())*(end.getCoords().getX()-begin.getCoords().getX()))-
					(right.end.getCoords().getX() - right.begin.getCoords().getX())*(end.getCoords().getY()-begin.getCoords().getY());
		
		double na = ((right.end.getCoords().getX() - right.begin.getCoords().getX())*(begin.getCoords().getY()-right.begin.getCoords().getY())) -
					 (right.end.getCoords().getY() - right.begin.getCoords().getY())*(begin.getCoords().getX()-right.begin.getCoords().getX());
		
		double nb = ((end.getCoords().getX() - begin.getCoords().getX())*(begin.getCoords().getY()-right.begin.getCoords().getY())) -
					((end.getCoords().getY() - begin.getCoords().getY())*(begin.getCoords().getX()-right.begin.getCoords().getX()));
		
		double ua = na / d;
		double ub = nb / d;
		
		if(ua >= 0.0d && ua <= 1.0d && ub >= 0.0d && ub <= 1.0d) {
			return new Vertex(new Point(begin.getCoords().getX() + ua*(end.getCoords().getX()-begin.getCoords().getX()),
							 begin.getCoords().getY() + ua*(end.getCoords().getY()-begin.getCoords().getY())));
		}
		else
//			if(ua >= 0.0d && ua <= 1.0d)
//				System.out.println("The Intersection happens at a vertex. ua = " + ua + " ub = " + ub);
//			else
//				if(ub >= 0.0d && ub <= 1.0d)
//					System.out.println("The Intersection happens at a vertex. ua = " + ua + " ub = " + ub);
//		
			return null;
		
	}
	
	/**
	 * @return the begin
	 */
	public Vertex getEndpointA() {
		return begin;
	}

	/**
	 * @param begin the begin to set
	 */
	public void setEndpointA(Vertex endpointA) {
		this.begin = endpointA;
	}

	/**
	 * @return the end
	 */
	public Vertex getEndpointB() {
		return end;
	}

	/**
	 * @param end the end to set
	 */
	public void setEndpointB(Vertex endpointB) {
		this.end = endpointB;
	}

}
