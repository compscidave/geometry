/*
 * Copyright 2011-2014 David Godfrey. All rights reserved.
 */
package com.godfrey.geometry.site;

import com.godfrey.geometry.site.Site;
import com.godfrey.geometry.point.Point;

public class EnvelopeSite extends Site {

	public EnvelopeSite(double d, double e) {
		super(d, e);
	}
	
	public EnvelopeSite(Point p) {
		super(p.getX(),p.getY());
	}

	@Override
	public Point getLocation() {
		return new Point(x,y);
	}

	@Override
	public void setLocation(double longitude, double latitude) {
		this.x = longitude;
		this.y = latitude;
	}

	@Override
	public Object getData() {
		return null;
	}

	@Override
	public void setLocation(Point p) {
		// TODO Auto-generated method stub
		
	}

}
