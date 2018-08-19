/*
 * Copyright 2011-2014 David Godfrey. All rights reserved.
 */
package com.godfrey.geometry.test.voronoi;

import com.godfrey.geometry.test.voronoi.Site;

public class Face {

	HalfEdge outerComponent;
	HalfEdge innerComponent;
	HalfEdge incidentEdge;
	Site containedSite;
	
	public Site getContainedSite() {
		return containedSite;
	}
	
	public void setContainedSite(Site incidentSite) {
		this.containedSite = incidentSite;
	}

	private double [] color;
	
	public Face() {
		setOuterComponent(null);
		setInnerComponent(null);
		color = new double [3];
	}
	
	public Face(HalfEdge oc) {
		setOuterComponent(oc);
		setInnerComponent(null);
		color = new double [3];
	}
	
	public Face(HalfEdge oc, HalfEdge ic) {
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
	public HalfEdge getOuterComponent() {
		return outerComponent;
	}

	/**
	 * @param outerComponent the outerComponent to set
	 */
	public void setOuterComponent(HalfEdge outerComponent) {
		this.outerComponent = outerComponent;
	}

	/**
	 * @return the innerComponent
	 */
	public HalfEdge getInnerComponent() {
		return innerComponent;
	}

	/**
	 * @param innerComponent the innerComponent to set
	 */
	public void setInnerComponent(HalfEdge innerComponent) {
		this.innerComponent = innerComponent;
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
	
	
}
