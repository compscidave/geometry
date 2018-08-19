/*
 * Copyright 2011-2014 David Godfrey. All rights reserved.
 */
package com.godfrey.geometry.test.voronoi;

import com.godfrey.geometry.test.voronoi.Site;

public class Node {

	Node leftChild;
	Node rightChild;
	Node parent;
	Node predecessor;
	Node successor;
	BreakPoint breakPoint;
	Site eventSite;
	CircleEvent newCircleEvent;
	
	/**
	 * Constructs a new breakPoint ( internal StatusTreeNode )
	 * @param bp
	 */
	Node(BreakPoint bp) {
		leftChild = null;
		rightChild = null;
		parent = null;
		predecessor = null;
		successor = null;
		breakPoint = new BreakPoint(bp);
	}
	
	/**
	 * Constructs a new site/Arc ( leaf StatusTreeNode )
	 * @param site
	 */
	Node(Site site) {
		leftChild = null;
		rightChild = null;
		parent = null;
		breakPoint = null;
		predecessor = null;
		successor = null;
		newCircleEvent = null;
		setNewPointSite(site);
	}

	private void setNewPointSite(Site site) {
		eventSite = site;
	}

	/**
	 * Copy constructor
	 * Creates a new StatusTreeNode object on the heap,
	 * referencing all of it's parameters fields.
	 * @param node
	 */
	Node(Node node) {
		leftChild = node.leftChild;
		rightChild = node.rightChild;
		parent = node.parent;
		breakPoint = node.breakPoint;
		predecessor = node.predecessor;
		successor = node.successor;
		newCircleEvent = node.newCircleEvent;
		eventSite = node.eventSite;
	}
	
	public Site getSite() {
		return eventSite;
	}
	boolean isLeaf() {
		return (leftChild == null && rightChild == null ? true : false);
	}
	
	public Node getPredecessor() {
		return predecessor;
	}

	public Node getSuccessor() {
		return successor;
	}

	public CircleEvent getCircleEvent() {
		return newCircleEvent;
	}
	
	public Coordinate getCircleEventOrigin() {
		return newCircleEvent.getOrigin();
	}
	
	public boolean newHasCircleEvent() {
		return (newCircleEvent == null? false : true);
	}

}