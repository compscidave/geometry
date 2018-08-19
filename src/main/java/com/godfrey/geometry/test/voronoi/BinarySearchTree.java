/*
 * Copyright 2011-2014 David Godfrey. All rights reserved.
 */
package com.godfrey.geometry.test.voronoi;

import java.util.NoSuchElementException;

public class BinarySearchTree {

	private EventNode root;

	public BinarySearchTree() {
		root = null;
	}

	public static BinarySearchTree createTree() {
		return new BinarySearchTree();
	}
	
	public void insert(LineSegmentEvent value) {
		root = insert(value, root);
	}
	
	public LineSegmentEvent getEvent(LineSegmentEvent value) {
		return getEvent(value, root);
	}

	private LineSegmentEvent getEvent(LineSegmentEvent value, EventNode entry) {
		while (entry != null) {
			if (value.compareTo(entry.lineSegmentEvent) < 0)
				entry = entry.leftChild;
			else if (value.compareTo(entry.lineSegmentEvent) > 0)
				entry = entry.rightChild;
			else
				return entry.lineSegmentEvent;
		}

		return null;
	}

	public void remove(LineSegmentEvent value) {
		root = remove(value, root);
	}

	public LineSegmentEvent getNextEvent() {
		LineSegmentEvent returnEvent = findMin(root).lineSegmentEvent;
		remove(returnEvent);
		return returnEvent;
	}

	public boolean contains(LineSegmentEvent value) {
		return valueOf(find(value, root)) != null;
	}

	private LineSegmentEvent valueOf(EventNode entry) {
		return entry == null ? null : entry.lineSegmentEvent;
	}

	private EventNode insert(LineSegmentEvent value, EventNode entry) {
		if (entry == null)
			entry = new EventNode(value);
		else if (value.compareTo(entry.lineSegmentEvent) < 0)
			entry.leftChild = insert(value, entry.leftChild);
		else if (value.compareTo(entry.lineSegmentEvent) > 0)
			entry.rightChild = insert(value, entry.rightChild);
		else {
			entry.lineSegmentEvent.addSegment(value.getSegments());
		}
		return entry;
	}

	private EventNode remove(LineSegmentEvent value, EventNode entry) {
		if (entry == null)
			throw new NoSuchElementException("Entry not found : " + value.toString());
		if (value.compareTo(entry.lineSegmentEvent) < 0)
			entry.leftChild = remove(value, entry.leftChild);
		else if (value.compareTo(entry.lineSegmentEvent) > 0)
			entry.rightChild = remove(value, entry.rightChild);
		else {
			// Entry found.
			if (entry.leftChild != null && entry.rightChild != null) {

				// Replace with in-order successor (the left-most child of the right subtree)
				entry.lineSegmentEvent = findMin(entry.rightChild).lineSegmentEvent;
				entry.rightChild = removeInorderSuccessor(entry.rightChild);

				// Replace with in-order predecessor (the right-most child of the left subtree)
				// entry.lineSegmentEvent = findMax(entry.leftChild).element;
				// entry.leftChild = removeInorderPredecessor(entry.leftChild);
			} else
				entry = (entry.leftChild != null) ? entry.leftChild : entry.rightChild;
		}
		return entry;
	}

	private EventNode removeInorderSuccessor(EventNode entry) {
		if (entry == null)
			throw new NoSuchElementException();
		else if (entry.leftChild != null) {
			entry.leftChild = removeInorderSuccessor(entry.leftChild);
			return entry;
		} else
			return entry.rightChild;
	}

	private EventNode removeInorderPredecessor(EventNode entry) {
		if (entry == null)
			throw new NoSuchElementException();
		else if (entry.rightChild != null) {
			entry.rightChild = removeInorderPredecessor(entry.rightChild);
			return entry;
		} else
			return entry.leftChild;
	}

	private EventNode findMin(EventNode entry) {
		if (entry != null)
			while (entry.leftChild != null)
				entry = entry.leftChild;

		return entry;
	}

	private EventNode findMax(EventNode entry) {
		if (entry != null)
			while (entry.rightChild != null)
				entry = entry.rightChild;

		return entry;
	}

	private EventNode find(LineSegmentEvent value, EventNode entry) {
		while (entry != null) {
			if (value.compareTo(entry.lineSegmentEvent) < 0)
				entry = entry.leftChild;
			else if (value.compareTo(entry.lineSegmentEvent) > 0)
				entry = entry.rightChild;
			else
				return entry;
		}

		return null;
	}

	private void printInOrder(EventNode entry) {
		if (entry != null) {
			printInOrder(entry.leftChild);
			System.out.println(entry.lineSegmentEvent);
			printInOrder(entry.rightChild);
		}
	}

	public void printInOrder() {
		System.out.println("************************************************");
		System.out.println("*          Printing EventQueue Tree            *");
		System.out.println("************************************************");
		System.out.println();
		printInOrder(root);
		System.out.println();
		System.out.println("************************************************");
		System.out.println("*         Finished EventQueue Print            *");
		System.out.println("************************************************");
	}

	public boolean isEmpty() {
		return root==null ? true : false;
	}

}