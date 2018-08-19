/*
 * Copyright 2011-2014 David Godfrey. All rights reserved.
 */
package com.godfrey.geometry.site;

import com.godfrey.geometry.point.Point;

public abstract class Site extends Point {
	
	public Site(double d, double e) {
		super(d, e);
	}
	
	public Site(Point p) {
		super(p.getX(),p.getY());
	}

	public abstract Point getLocation();

	public abstract void setLocation(double longitude, double latitude);
	
	public abstract void setLocation(Point p);
	
	public abstract Object getData();
	
}
