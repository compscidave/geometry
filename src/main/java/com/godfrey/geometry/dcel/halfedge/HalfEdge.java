/*
 * Copyright 2011-2014 David Godfrey. All rights reserved.
 */
package com.godfrey.geometry.dcel.halfedge;

import com.godfrey.geometry.dcel.face.Face;
import com.godfrey.geometry.dcel.vertex.Vertex;


public class HalfEdge<T> {

	public Vertex origin;
	public HalfEdge<T> twin;
	public Face<T> incidentFace;
	public HalfEdge<T> next;
	public HalfEdge<T> prev;
	public T incidentSite;
	
	//Default Constructor
	public HalfEdge() {
		setOrigin(null);
		setTwin(null);
		setIncidentFace(null);
		setNext(null);
		setPrev(null);
		setIncidentSite(null);
	}
	
	//Constructor
	public HalfEdge(Vertex origin,
					HalfEdge<T> twin,
					Face<T> incidentFace,
					HalfEdge<T> next,
					HalfEdge<T> prev,
					T incidentSite) {
		this.setOrigin(origin);
		this.setTwin(twin);
		this.setIncidentFace(incidentFace);
		this.setNext(next);
		this.setPrev(prev);
		this.setIncidentSite(incidentSite);
	}

	public HalfEdge(HalfEdge<T> h) {
		origin = h.origin;
		twin = h.twin;
		incidentFace = h.incidentFace;
		next = h.next;
		prev = h.prev;
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
	public HalfEdge<T> getTwin() {
		return twin;
	}

	/**
	 * @param twin the twin to set
	 */
	public void setTwin(HalfEdge<T> twin) {
		this.twin = twin;
	}

	/**
	 * @return the incidentFace
	 */
	public Face<T> getIncidentFace() {
		return incidentFace;
	}

	/**
	 * @param incidentFace the incidentFace to set
	 */
	public void setIncidentFace(Face<T> incidentFace) {
		this.incidentFace = incidentFace;
	}

	/**
	 * @return the next
	 */
	public HalfEdge<T> getNext() {
		return next;
	}

	/**
	 * @param next the next to set
	 */
	public void setNext(HalfEdge<T> next) {
		this.next = next;
	}

	/**
	 * @return the prev
	 */
	public HalfEdge<T> getPrev() {
		return prev;
	}

	/**
	 * @param prev the prev to set
	 */
	public void setPrev(HalfEdge<T> prev) {
		this.prev = prev;
	}

	public T getIncidentSite() {
		return incidentSite;
	}
	
	public void setIncidentSite(T incidentSite) {
		this.incidentSite = incidentSite;
	}
	
}
