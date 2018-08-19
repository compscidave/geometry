/*
 * Copyright 2011-2014 David Godfrey. All rights reserved.
 */
package com.godfrey.geometry.test.voronoi;

import com.godfrey.geometry.test.voronoi.Site;

public class HalfEdge {

	Vertex origin;
	HalfEdge twin;
	Face incidentFace;
	HalfEdge next;
	HalfEdge prev;
	Site newIncidentSite;

	//Default Constructor
	public HalfEdge() {
		setOrigin(null);
		setTwin(null);
		setIncidentFace(null);
		setNext(null);
		setPrev(null);
		setnewIncidentSite(null);
	}
	
	//Constructor
	public HalfEdge(Vertex origin,
					HalfEdge twin,
					Face incidentFace,
					HalfEdge next,
					HalfEdge prev) {
		this.setOrigin(origin);
		this.setTwin(twin);
		this.setIncidentFace(incidentFace);
		this.setNext(next);
		this.setPrev(prev);
		newIncidentSite = null;
	}

	public HalfEdge(HalfEdge h) {
		origin = h.origin;
		twin = h.twin;
		incidentFace = h.incidentFace;
		next = h.next;
		prev = h.prev;
		newIncidentSite = h.newIncidentSite;
	}

	/**
	 * @return the origin
	 */
	public Vertex getOrigin() {
		return origin;
	}

	/**
	 * @param origin the origin to set
	 */
	public void setOrigin(Vertex origin) {
		this.origin = origin;
	}

	/**
	 * @return the twin
	 */
	public HalfEdge getTwin() {
		return twin;
	}

	/**
	 * @param twin the twin to set
	 */
	public void setTwin(HalfEdge twin) {
		this.twin = twin;
	}

	/**
	 * @return the incidentFace
	 */
	public Face getIncidentFace() {
		return incidentFace;
	}

	/**
	 * @param incidentFace the incidentFace to set
	 */
	public void setIncidentFace(Face incidentFace) {
		this.incidentFace = incidentFace;
	}

	/**
	 * @return the next
	 */
	public HalfEdge getNext() {
		return next;
	}

	/**
	 * @param next the next to set
	 */
	public void setNext(HalfEdge next) {
		this.next = next;
	}

	/**
	 * @return the prev
	 */
	public HalfEdge getPrev() {
		return prev;
	}

	/**
	 * @param prev the prev to set
	 */
	public void setPrev(HalfEdge prev) {
		this.prev = prev;
	}

	private void setnewIncidentSite(Site newIncidentSite) {
		this.newIncidentSite = newIncidentSite;
	}
}
