/*
 * Copyright 2011-2014 David Godfrey. All rights reserved.
 */
package com.godfrey.geometry.statustree.node;

import com.godfrey.geometry.point.Point;
import com.godfrey.geometry.statustree.breakpoint.BreakPoint;
import com.godfrey.geometry.voronoi.event.Event;




public class StatusTreeNode<T extends Point> {

	public StatusTreeNode leftChild;
	public StatusTreeNode rightChild;
	public StatusTreeNode parent;
	public StatusTreeNode predecessor;
	public StatusTreeNode successor;
	public BreakPoint breakPoint;
	public T site;
	public Event circleEvent;
	
	/**
	 * Constructs a new breakPoint ( internal StatusTreeNode )
	 * @param bp
	 */
	public StatusTreeNode(BreakPoint bp) {
		leftChild = null;
		rightChild = null;
		parent = null;
		setPointSite(null);
		predecessor = null;
		successor = null;
		circleEvent = null;
		breakPoint = new BreakPoint(bp);
	}
	
	/**
	 * Constructs a new site/Arc ( leaf StatusTreeNode )
	 * @param site
	 */
	public StatusTreeNode(Point site) {
		leftChild = null;
		rightChild = null;
		parent = null;
		breakPoint = null;
		predecessor = null;
		successor = null;
		circleEvent = null;
		//System.out.println(site.toString());
		setPointSite((T) site);
	}

	/**
	 * Copy constructor
	 * Creates a new StatusTreeNode object on the heap,
	 * referencing all of it's parameters fields.
	 * @param node
	 */
	public StatusTreeNode(StatusTreeNode<T> node) {
		leftChild = node.leftChild;
		rightChild = node.rightChild;
		parent = node.parent;
		breakPoint = node.breakPoint;
		predecessor = node.predecessor;
		successor = node.successor;
		site = node.site;
		circleEvent = node.circleEvent;
	}
	
	public boolean isLeaf() {
		return (leftChild == null && rightChild == null ? true : false);
	}
	
	public StatusTreeNode getPredecessor() {
		return predecessor;
	}

	public StatusTreeNode getSuccessor() {
		return successor;
	}

	/**
	 * @return the site
	 */
	public T getPointSite() {
		return site;
	}

	public Event getCircleEvent() {
		return circleEvent;
	}
	
	public Point getCircleEventOrigin() {
		return circleEvent.getOrigin();
	}
	/**
	 * @param site the site to set
	 */
	public void setPointSite(T pointSite) {
		this.site = pointSite;
	}

	public boolean hasCircleEvent() {
		return (circleEvent == null? false : true);
	}

	public Event getEvent() {
		return circleEvent;
	}
}