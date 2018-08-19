/*
 * Copyright 2011-2014 David Godfrey. All rights reserved.
 */
package com.godfrey.geometry.test.voronoi;

import com.godfrey.geometry.point.Point;

public class NewEvent {

	Point eventSite;
	boolean isSiteEvent;
	
	public NewEvent() {
		eventSite = new Coordinate(0,0);
	}
	
	public NewEvent(Point point,boolean isSiteEvent) {
		this.eventSite = point;
		this.isSiteEvent = isSiteEvent;
	}
	
	public boolean isSiteEvent() {
		return isSiteEvent;
	}
	
	public Point getEventSite() {
		return eventSite;
	}
	
	public void setEventSite(Coordinate eventSite) {
		this.eventSite = eventSite;
	}
	
	public void setEventSite(double longitude, double latitude) {
		this.eventSite = new Coordinate(longitude,latitude);
	}
}
