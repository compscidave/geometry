/*
 * Copyright 2011-2014 David Godfrey. All rights reserved.
 */
package com.godfrey.geometry.test.voronoi;

import com.godfrey.geometry.point.Point;

public class Coordinate extends Point {

	public Coordinate(double longitude, double latitude) {
		super(longitude,latitude);
		if(Math.abs(longitude) < 0.0000001)
			this.x = 0;
		else
			this.x = longitude;
		if(Math.abs(latitude) < 0.0000001)
			this.y = 0;
		else
			this.y = latitude;
	}
	
	public Coordinate(Coordinate p) {
		super(p.x,p.y);
		this.x = p.x;
		this.y = p.y;
	}
	
	public Coordinate() {
		super(Double.NaN,Double.NaN);
		y = Double.NaN;
		x = Double.NaN;
	}
	
	public double distance(Point cabLocation) {
		return Math.sqrt((cabLocation.x-x)*(cabLocation.x-x)+(cabLocation.y-y)*(cabLocation.y-y));
	}
	
	public double squareDistance(Coordinate p) {
		return (p.x-x)*(p.x-x)+(p.y-y)*(p.y-y);
	}
	
	public double getLongitude() {
		return x;
	}
	
	public double getLatitude() {
		return y;
	}

	public void setLongitude(double d) {
		x = d;
	}

	public void setLatitude(double d) {
		y = d;
	}
	
	@Override
	public String toString() {
		return "<Lon,Lat><" + x + ", " + y + ">";
	}
	
	@Override
	public boolean equals(Object o) {
		try {
		Coordinate p = (Coordinate) o;
		return (p.x == x && p.y == y);
		} catch (ClassCastException cce) {
			try {
				Point q = (Point) o;
				return q.x == x && q.y ==y;
			} catch (ClassCastException cce2) {
				return false;
			}
		}
	}

}
