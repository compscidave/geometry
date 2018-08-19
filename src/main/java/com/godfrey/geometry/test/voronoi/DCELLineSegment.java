/*
 * Copyright 2011-2014 David Godfrey. All rights reserved.
 */
package com.godfrey.geometry.test.voronoi;

import com.godfrey.geometry.point.Point;

public class DCELLineSegment {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final double TOLERANCE = 0.0000000001;
	private Vertex upper;
	private Vertex lower;
	
	public DCELLineSegment(Vertex endpointA, Vertex endpointB) {
		if(endpointA == null) {
			throw new IllegalArgumentException("Endpoint A Was Null");
		}
		if(endpointB == null) {
			throw new IllegalArgumentException("Endpoint B Was Null");
		}
		if(endpointA.getLatitude() > endpointB.getLatitude()) {
			upper = endpointA;
			lower = endpointB;
		}
		else
			//If the DCELLineSegment is horizontal,
			if(endpointA.getLatitude() == endpointB.getLatitude()) {
				//the upper endpoint is defined by the smaller x value.
				if(endpointA.getLongitude() < endpointB.getLongitude()) {
					upper = endpointA;
					lower = endpointB;
				}
				else
					/**
					 * The following commented code will be executed for zero-length segments.
					 * Un-commenting will place constraints on handling zero-length segments.
					 * For the line segment intersection problem, it's likely, but unclear whether zero-length
					 * segments would be handled correctly.
					 */
/*					if(endpointA.getLongitude() == endpointB.getLongitude()) 
						throw new IllegalArgumentException("Instantiating DCELLineSegment with overlapping coordinates");
					else
*/
					{
						//endpointA.longitude > endpointB.latitude
						upper = endpointB;
						lower = endpointA;
					}
			}
		//endpointA.latitude < endpointB.latitude
			else {
				upper = endpointB;
				lower = endpointA;
			}
//		if(!(super.p0.equals(lower.getCoords()) && super.p1.equals(upper.getCoords())))
//			super.normalize();
	}

	//Test intersect method.
	public Vertex getIntersection(DCELLineSegment other) {
		double d = ((other.lower.getCoords().getLatitude() - other.upper.getCoords().getLatitude())*(lower.getCoords().getLongitude()-upper.getCoords().getLongitude()))-
					(other.lower.getCoords().getLongitude() - other.upper.getCoords().getLongitude())*(lower.getCoords().getLatitude()-upper.getCoords().getLatitude());
		
		double na = ((other.lower.getCoords().getLongitude() - other.upper.getCoords().getLongitude())*(upper.getCoords().getLatitude()-other.upper.getCoords().getLatitude())) -
					 (other.lower.getCoords().getLatitude() - other.upper.getCoords().getLatitude())*(upper.getCoords().getLongitude()-other.upper.getCoords().getLongitude());
		
		double nb = ((lower.getCoords().getLongitude() - upper.getCoords().getLongitude())*(upper.getCoords().getLatitude()-other.upper.getCoords().getLatitude())) -
					((lower.getCoords().getLatitude() - upper.getCoords().getLatitude())*(upper.getCoords().getLongitude()-other.upper.getCoords().getLongitude()));
		
		double ua = na / d;
		double ub = nb / d;
		
		if(ua >= 0.0d && ua <= 1.0d && ub >= 0.0d && ub <= 1.0d) {

			if(ua < 0.00000000000001)
				ua = 0.0;
			double x = upper.getCoords().getLongitude() + ua*(lower.getCoords().getLongitude()-upper.getCoords().getLongitude());
			double y = upper.getCoords().getLatitude() + ua*(lower.getCoords().getLatitude()-upper.getCoords().getLatitude());

			return new Vertex(x,y);
		}
		else {
//			System.out.println("Other : " + other);
//			System.out.println("This  : " + this);
//			if(other.isHorizontal())
//			if(ua >= 0.0d && ua <= 1.0d) 
//				System.out.println("The Intersection happens at a vertex. ua = " + ua + " ub = " + ub);
//			else
//				if(ub >= 0.0d && ub <= 1.0d)
//					System.out.println("The Intersection happens at a vertex. ua = " + ua + " ub = " + ub);
//		
			return null;
		}
		
	}
	
	/**
	 * @return the upper
	 */
	public Vertex getUpperEndpoint() {
		return upper;
	}

	/**
	 * @return the lower
	 */
	public Vertex getLowerEndpoint() {
		return lower;
	}

	/**
	 * Flips the orientation of the DCELLineSegment, making the upper
	 * endpoint the lower endpoint, and vice versa.
	 */
	public void flipEndpoints() {
		Vertex t = upper;
		upper = lower;
		lower = t;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Upper: "+upper.toString() + ", Lower: "+lower.toString());
		return sb.toString();
	}

	@Override
	public boolean equals(Object o) {
		DCELLineSegment other = (DCELLineSegment) o;
		return upper.equals(other.getUpperEndpoint()) && lower.equals(other.getLowerEndpoint());
	}

	public boolean isHorizontal() {
		return upper.getY() == lower.getY() ?  true: false;
	}

	public double getLength() {
		return upper.distance(lower);
	}
	
	public boolean contains(Point eventSite) {
		double cross = (upper.getX() - lower.getX())*(eventSite.getY() - lower.getY()) - (eventSite.getX() - lower.getX())*(upper.getY() - lower.getY());
		if(Math.abs(cross) > 0.000000000000001)
			return false;
		
		double dot = (eventSite.getX() - lower.getX())*(upper.getX() - lower.getX()) + (eventSite.getY() - lower.getY())*(upper.getY() - lower.getY());
		if(dot < 0)
			return false;
		
		if(dot > upper.squareDistance(lower))
			return false;
		
		return true;
	}
	
	/**
	 * Calculates the orientation of a point with respect to this segment.
	 * 
	 * @param eventSite The point to test for orientation
	 * @return Returns -1, 0 or 1 if the point lies to the left, belongs to, or lies to the right of the segment.
	 */
	public int orientation(Point eventSite) {
		if(this.isHorizontal()) {
			if(eventSite.getX() < this.upper.getX())
				return -1;
			else
				return 1;
		}
		else {
			double cross = (upper.getX() - lower.getX())*(eventSite.getY() - lower.getY()) - (eventSite.getX() - lower.getX())*(upper.getY() - lower.getY());
//			System.out.println(cross);
			if(Math.abs(cross) < 0.000000000000001)
				return 0;
			if(cross > 0)
				return -1;
			else
				if(cross < 0)
					return 1;
				else
					return 0;
				//The following conditional is not a stable way to check if the segment contains the point.
				//In TestFindIntersection.testEventPointContainingMultipleSegments, NoSuchElementException is thrown
				//when searching for a segment lying to the right of this segment. They both share an intersection point
				//but because the point lies in the interior of the querying segment
		}
	}
	
	public boolean isCollinearWith(Coordinate point) {
		if(this.isHorizontal())
			return this.upper.getX() <= point.getX() && this.lower.getX() >= point.getX() ? true: false;
		return (upper.getX() - lower.getX())*(point.getY() - lower.getY()) - (point.getX() - lower.getX())*(upper.getY() - lower.getY()) == 0 ? true : false; 
	}
	
	public boolean isLeftOf(Coordinate point) {
		if(this.isHorizontal())
			return point.getX() < this.upper.getX() ? true : false;
		if(this.contains(point))
			return false;
		return (upper.getX() - lower.getX())*(point.getY() - lower.getY()) - (point.getX() - lower.getX())*(upper.getY() - lower.getY()) < 0 ? false : true;
	}
	
	public boolean compareToLower(DCELLineSegment other) {
		double cross = (upper.getX() - lower.getX())*(other.lower.getY() - lower.getY()) - (other.lower.getX() - lower.getX())*(upper.getY() - lower.getY());
		if(Math.abs(cross) < 0.000000000000001)
			return false;
		else
			return cross < 0 ? false : true;
	}

	public boolean compareToUpper(DCELLineSegment other) {
		double cross = (upper.getX() - lower.getX())*(other.upper.getY() - lower.getY()) - (other.upper.getX() - lower.getX())*(upper.getY() - lower.getY());
		if(Math.abs(cross) < 0.000000000000001)
			return false;
		else
			return cross < 0 ? false : true;
	}

}
