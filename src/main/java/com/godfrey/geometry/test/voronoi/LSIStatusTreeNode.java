/*
 * Copyright 2011-2014 David Godfrey. All rights reserved.
 */
package com.godfrey.geometry.test.voronoi;

public class LSIStatusTreeNode {
	
	LSIStatusTreeNode leftChild;
	LSIStatusTreeNode rightChild;
	LSIStatusTreeNode successor;
	LSIStatusTreeNode predecessor;
	LSIStatusTreeNode parent;
	DCELLineSegment segment;
	boolean isLeftChild;
	
	public LSIStatusTreeNode() {
		leftChild = null;
		rightChild = null;
		successor = null;
		predecessor = null;
		parent = null;
		segment = null;
		isLeftChild = false;
	}
	
	public void clear() {
		leftChild = null;
		rightChild = null;
		successor = null;
		predecessor = null;
		parent = null;
	}

	public LSIStatusTreeNode(DCELLineSegment segment) {
		leftChild = null;
		rightChild = null;
		successor = null;
		predecessor = null;
		parent = null;
		isLeftChild = false;
		this.segment = segment;
	}
	
	public LSIStatusTreeNode(LSIStatusTreeNode entry) {
		leftChild = entry.leftChild;
		rightChild = entry.rightChild;
		successor = entry.successor;
		predecessor = entry.predecessor;
		parent = entry.parent;
		segment = entry.segment;
		isLeftChild = entry.isLeftChild;
	}

		public boolean isLeaf() {
		return leftChild == null && rightChild == null ? true : false;
	}


/*		*//**
	 * An attempt to derive a clean directional check when descending into the status tree
	 * during a removal
	 * @param o
	 * @return
	 *//*
	public int compareToLowerEndpoint(LSIStatusTreeNode o) {
		
	}*/

}	
