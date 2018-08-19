/*
 * Copyright 2011-2014 David Godfrey. All rights reserved.
 */
package com.godfrey.geometry.test.voronoi;

public class CircleEvent extends NewEvent {

	Node eventNode;
	Coordinate origin;
	boolean isValid;
	
	public CircleEvent(Node eventNode, Coordinate eventSite, Coordinate origin) {
		super(eventSite,false);
		this.eventNode = eventNode;
		this.origin = origin;
		isValid = true;
	}
	
	public boolean isValid() {
		return isValid;
	}

	public void setValid(boolean isValid) {
		this.isValid = isValid;
	}

	public Node getEventNode() {
		return eventNode;
	}
	
	public void setEventNode(Node eventNode) {
		this.eventNode = eventNode;
	}
	
	public Coordinate getOrigin() {
		return origin;
	}
	
	public void setOrigin(Coordinate origin) {
		this.origin = origin;
	}
}
