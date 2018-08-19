/*
 * Copyright 2011-2014 David Godfrey. All rights reserved.
 */
package com.godfrey.geometry.test.voronoi;

public class EventNode implements Comparable<EventNode> {

	EventNode leftChild;
	EventNode rightChild;
	EventNode parent;
	LineSegmentEvent lineSegmentEvent;
	
	public EventNode() {
		leftChild = null;
		rightChild = null;
		parent = null;
		lineSegmentEvent = null;
	}
	
	public EventNode(LineSegmentEvent lineSegmentEvent) {
		leftChild = null;
		rightChild = null;
		parent = null;
		this.lineSegmentEvent = lineSegmentEvent;
	}

	public EventNode(EventNode node) {
		leftChild = node.leftChild;
		rightChild = node.rightChild;
		parent = node.parent;
		lineSegmentEvent = node.lineSegmentEvent;
	}

	public boolean isLeaf() {
		return (leftChild == null && rightChild == null ? true : false);
	}
	
	public int compareTo(EventNode en) {
		if(lineSegmentEvent.eventSite.getY() > en.lineSegmentEvent.eventSite.getY() ||
				(lineSegmentEvent.eventSite.getY() == en.lineSegmentEvent.eventSite.getY() &&
						lineSegmentEvent.eventSite.getX() < en.lineSegmentEvent.eventSite.getX()))
			return 1;
		else
			if(lineSegmentEvent.eventSite.equals(en.lineSegmentEvent.eventSite))
				return 0;
			else
				return -1;
	}
	
	@Override
	public boolean equals(Object o) {
		if(!(o instanceof EventNode))
			throw new IllegalArgumentException("Cannot compare EventNode to " + o.getClass().getSimpleName());
		else {
			EventNode en = (EventNode) o;
			if(en.lineSegmentEvent.eventSite.equals(lineSegmentEvent.eventSite))
				return true;
			else
				return false;
		}
	}
	
	
	
}
