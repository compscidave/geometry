/*
 * Copyright 2011-2014 David Godfrey. All rights reserved.
 */
package com.godfrey.geometry.voronoi.event;

import com.godfrey.geometry.point.Point;
import com.godfrey.geometry.statustree.node.StatusTreeNode;



public class Event<T> {

	boolean siteEvent;
	boolean valid;
	T eventSite;
	private Point origin;
	StatusTreeNode eventNode;
	
	/**
	 * Constructor for a Site Event
	 * @param point The point that corresponds to the site event
	 */
	public Event(T point) {
		this.setEventSite(point);
		siteEvent = true;
		setEventNode(null);
	}
	
	/**
	 * Constructor for a Circle Event
	 * @param point The point that corresponds to the circle event
	 * @param eventNode The node in the status structure which represents the arc that will disappear in the event
	 */
	public Event(T eventSite, Point origin, StatusTreeNode eventNode) {
		this.setEventSite(eventSite);
		this.siteEvent = false;
		this.setEventNode(eventNode);
		this.setOrigin(origin);
	}
	
	
	public boolean isSiteEvent() {
		return siteEvent;
	}

	public T getSite() {
		return getEventSite();
	}

	public StatusTreeNode getNode() {
		return getEventNode();
	}

	/**
	 * @return the eventSite
	 */
	public T getEventSite() {
		return eventSite;
	}

	/**
	 * @param point the eventSite to set
	 */
	public void setEventSite(T point) {
		this.eventSite = point;
	}

	/**
	 * @return the eventNode
	 */
	public StatusTreeNode getEventNode() {
		return eventNode;
	}

	/**
	 * @param eventNode the eventNode to set
	 */
	public void setEventNode(StatusTreeNode eventNode) {
		this.eventNode = eventNode;
	}

	/**
	 * @return the valid
	 */
	public boolean isValid() {
		return valid;
	}

	/**
	 * @param valid the valid to set
	 */
	public void setValid(boolean valid) {
		this.valid = valid;
	}

	/**
	 * @return the origin
	 */
	public Point getOrigin() {
		return origin;
	}

	/**
	 * @param origin the origin to set
	 */
	public void setOrigin(Point origin) {
		this.origin = origin;
	}
}
