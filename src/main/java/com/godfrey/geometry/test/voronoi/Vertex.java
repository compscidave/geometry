/*
 * Copyright 2011-2014 David Godfrey. All rights reserved.
 */
package com.godfrey.geometry.test.voronoi;

public class Vertex extends Coordinate {

	HalfEdge incidentEdge;
	
	public Vertex(Coordinate origin) {
		super(origin);
		incidentEdge = null;
	}
	
	public Vertex(Coordinate p, HalfEdge e) {
		super(p);
		incidentEdge = e;
	}
	
	public Vertex(double x, double y) {
		super(x,y);
		incidentEdge = null;
	}

	public Vertex() {
		super();
		incidentEdge = null;
	}

	/**
	 * @return the coords
	 */
	public Coordinate getCoords() {
		return new Coordinate(super.getLongitude(),super.getLatitude());
	}
	/**
	 * @param coords the coords to set
	 */
	public void setCoords(Coordinate coords) {
		super.setLatitude(coords.getLatitude());
		super.setLongitude(coords.getLongitude());
	}
	/**
	 * @return the incidentEdge
	 */
	public HalfEdge getIncidentEdge() {
		return incidentEdge;
	}
	/**
	 * @param incidentEdge the incidentEdge to set
	 */
	public void setIncidentEdge(HalfEdge incidentEdge) {
		this.incidentEdge = incidentEdge;
	}

	/**
	 * TODO: Auto-generated equals method
	 */
	@Override
	public boolean equals(Object obj) {
		Vertex other = (Vertex) obj;
//		if (incidentEdge == null) {
//			if (other.incidentEdge != null)
//				return false;
//		} else if (!incidentEdge.equals(other.incidentEdge))
//			return false;
		if(other.x == x && other.y == y)
			return true;
		else
			return false;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(super.toString());
		return sb.toString();
	}
	
}
