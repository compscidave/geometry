/*
 * Copyright 2011-2014 David Godfrey. All rights reserved.
 */
package com.godfrey.geometry.dcel.face;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.godfrey.geometry.dcel.halfedge.HalfEdge;
import com.godfrey.geometry.dcel.vertex.Vertex;
import com.godfrey.geometry.point.Point;

public class Face<T> {

	public HalfEdge<T> outerComponent;
	public HalfEdge<T> innerComponent;
	public HalfEdge<T> incidentEdge;
	public T containedSite;
	private double [] color;
	
	public Face() {
		setOuterComponent(null);
		setInnerComponent(null);
		color = new double [3];
	}
	
	public Face(HalfEdge<T> oc) {
		setOuterComponent(oc);
		setInnerComponent(null);
		color = new double [3];
	}
	
	public Face(HalfEdge<T> oc, HalfEdge<T> ic) {
		setOuterComponent(oc);
		setInnerComponent(ic);
		color = new double [3];
	}

	public void setColor(double r, double g, double b) {
		color[0] = r;
		color[1] = g;
		color[2] = b;
	}
	
	public double [] getColor() {
		return color;
	}
	/**
	 * @return the outerComponent
	 */
	public HalfEdge<T> getOuterComponent() {
		return outerComponent;
	}

	/**
	 * @param outerComponent the outerComponent to set
	 */
	public void setOuterComponent(HalfEdge<T> outerComponent) {
		this.outerComponent = outerComponent;
	}

	/**
	 * @return the innerComponent
	 */
	public HalfEdge<T> getInnerComponent() {
		return innerComponent;
	}

	/**
	 * @param innerComponent the innerComponent to set
	 */
	public void setInnerComponent(HalfEdge<T> innerComponent) {
		this.innerComponent = innerComponent;
	}

	/**
	 * @return the incidentEdge
	 */
	public HalfEdge<T> getIncidentEdge() {
		return incidentEdge;
	}

	/**
	 * @param incidentEdge the incidentEdge to set
	 */
	public void setIncidentEdge(HalfEdge<T> incidentEdge) {
		this.incidentEdge = incidentEdge;
	}
	
	public T getContainedSite() {
		return containedSite;
	}
	
	public void setContainedSite(T containedSite) {
		this.containedSite = containedSite;
	}

	public boolean contains(Point point) {
		boolean contains = true;
		Set<HalfEdge<T>> visitedEdges = new HashSet<HalfEdge<T>>();
		HalfEdge<T> edge = incidentEdge;
		while(!visitedEdges.contains(edge)) {

			Vertex origin = edge.origin;
			Vertex dest = edge.twin.origin;
			
			contains &= ((origin.getXCoords() - point.x)*(dest.getYCoords() - point.y)
						- (dest.getXCoords() - point.x)*(origin.getYCoords() - point.y))
						> 0 ? false : true;
			
			visitedEdges.add(edge);
			edge = edge.next;
		}
		return contains;
	}
	
	public List<Face<T>> getNeighbors() {
		List<Face<T>> neighbors = new ArrayList<Face<T>>();
		Set<HalfEdge<T>> visitedEdges = new HashSet<HalfEdge<T>>();
		HalfEdge<T> edge = incidentEdge;
		while(!visitedEdges.contains(edge)) {
			if(edge.twin.incidentFace != null) {
				neighbors.add(edge.twin.incidentFace);
				visitedEdges.add(edge);
			}
			edge = edge.next;
			
		}
		return neighbors;
	}
	
}
