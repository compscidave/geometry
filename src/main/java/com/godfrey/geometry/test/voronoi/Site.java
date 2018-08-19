/*
 * Copyright 2011-2014 David Godfrey. All rights reserved.
 */
package com.godfrey.geometry.test.voronoi;

public abstract class Site extends Coordinate {
	
	public Site(double d, double e) {
		super(d, e);
	}
	
	public Site(Coordinate p) {
		super(p.getX(),p.getY());
	}

	public abstract Coordinate getLocation();

	public abstract void setLocation(double longitude, double latitude);
	
	public abstract void setLocation(Coordinate p);
	
	public abstract Object getData();
	
}
