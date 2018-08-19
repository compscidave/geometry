/*
 * Copyright 2011-2014 David Godfrey. All rights reserved.
 */
package com.godfrey.geometry.statustree.breakpoint;

import com.godfrey.geometry.dcel.halfedge.HalfEdge;
import com.godfrey.geometry.point.Point;


public class BreakPoint {

	public Point leftSite;
	public Point rightSite;
	public HalfEdge edge;
	
	/**
	 * Default Constructor
	 */
	public BreakPoint() {
		leftSite = null;
		rightSite = null;
		edge = null;
	}
	
	/**
	 * Constructor. Initializes left and right sites for this BreakPoint,
	 * and constructs a default super. The left and right child and the parent
	 * will need to be set if this constructor is used.
	 * @param leftSite
	 * @param rightSite
	 */
	public BreakPoint(Point leftSite, Point rightSite, HalfEdge edge) {
		this.leftSite = leftSite;
		this.rightSite = rightSite;
		this.edge = edge;
	}
	
	/**
	 * Constructor. Initializes left and right site for this breakpoint.
	 * This constructor does not initialize the corresponding HalfEdge
	 * @param leftSite
	 * @param rightSite
	 */
	public BreakPoint(Point leftSite, Point rightSite) {
		this.leftSite = leftSite;
		this.rightSite = rightSite;
		edge = null;
	}
	
	/**
	 * Copy constructor
	 * @param b The BreakPoint to copy
	 */
	public BreakPoint(BreakPoint b) {
		leftSite = b.leftSite;
		rightSite = b.rightSite;
		edge = b.edge;
	}

	@Override
	public boolean equals(Object o) {
		BreakPoint b = (BreakPoint) o;
		return (leftSite.equals(b.leftSite) &&
				rightSite.equals(b.rightSite));
	}

	public Point getLeftSite() {
		return leftSite;
	}

	public void setLeftSite(Point leftSite) {
		this.leftSite = leftSite;
	}

	public Point getRightSite() {
		return rightSite;
	}

	public void setRightSite(Point rightSite) {
		this.rightSite = rightSite;
	}

	public HalfEdge getEdge() {
		return edge;
	}

	public void setEdge(HalfEdge edge) {
		this.edge = edge;
	}

}
