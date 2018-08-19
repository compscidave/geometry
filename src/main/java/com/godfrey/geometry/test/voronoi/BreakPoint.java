/*
 * Copyright 2011-2014 David Godfrey. All rights reserved.
 */
package com.godfrey.geometry.test.voronoi;

import com.godfrey.geometry.test.voronoi.Site;

public class BreakPoint {

	Site newLeftSite;
	Site newRightSite;
	HalfEdge edge;
	
	/**
	 * Default Constructor
	 */
	public BreakPoint() {
		edge = null;
	}
	
	/**
	 * Constructor. Initializes left and right sites for this BreakPoint,
	 * and constructs a default super. The left and right child and the parent
	 * will need to be set if this constructor is used.
	 * @param leftSite
	 * @param rightSite
	 */
	public BreakPoint(Site newLeftSite, Site newRightSite, HalfEdge edge) {
		this.newLeftSite = newLeftSite;
		this.newRightSite = newRightSite;
		this.edge = edge;
	}
	/**
	 * Constructor. Initializes left and right site for this breakpoint.
	 * This constructor does not initialize the corresponding HalfEdge
	 * @param leftSite
	 * @param rightSite
	 */
	public BreakPoint(Site newLeftSite, Site newRightSite) {
		this.newLeftSite = newLeftSite;
		this.newRightSite = newRightSite;
		edge = null;
	}
	
	/**
	 * Copy constructor
	 * @param b The BreakPoint to copy
	 */
	public BreakPoint(BreakPoint b) {
		newLeftSite = b.newLeftSite;
		newRightSite = b.newRightSite;
		edge = b.edge;
	}

	@Override
	public boolean equals(Object o) {
		BreakPoint b = (BreakPoint) o;
		return (newLeftSite.equals(b.newLeftSite) &&
				newRightSite.equals(b.newRightSite));
	}
	
	public Site getLeftSite() {
		return newLeftSite;
	}

	public void setLeftSite(Site leftSite) {
		this.newLeftSite = leftSite;
	}

	public Site getRightSite() {
		return newRightSite;
	}

	public void setRightSite(Site rightSite) {
		this.newRightSite = rightSite;
	}

	public HalfEdge getEdge() {
		return edge;
	}

	public void setEdge(HalfEdge edge) {
		this.edge = edge;
	}

}
