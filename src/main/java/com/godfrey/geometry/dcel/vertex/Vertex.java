/*
 * Copyright 2011-2014 David Godfrey. All rights reserved.
 */
package com.godfrey.geometry.dcel.vertex;

import com.godfrey.geometry.dcel.halfedge.HalfEdge;
import com.godfrey.geometry.point.Point;


public class Vertex extends Point {

	private HalfEdge incidentEdge;
	
	public Vertex(Point origin) {
		super(origin.x,origin.y);
		incidentEdge = null;
	}
	
	public Vertex(Point p, HalfEdge e) {
		this(p);
		incidentEdge = e;
	}

	public Vertex() {
		super(0.0,0.0);
		incidentEdge = null;
	}

	/**
	 * @return the coords
	 */
	public Point getCoords() {
		return new Point(x,y);
	}
	/**
	 * @param coords the coords to set
	 */
	public void setCoords(Point coords) {
		this.x = coords.x;
		this.y = coords.y;
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
	
	public double getXCoords() {
		return x;
	}
	
	public double getYCoords() {
		return y;
	}
	
	public void setXCoords(double x) {
		this.x = x;
	}
	
	public void setYCoords(double y) {
		this.y = y;
	}
	
}
