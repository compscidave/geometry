/*
 * Copyright 2011-2014 David Godfrey. All rights reserved.
 */
package com.godfrey.geometry.point;

public class Point {

	public double x;
	public double y;
	
	public Point(double d, double e) {
		this.x = d;
		this.y = e;
	}
	public Point(Point p) {
		this.x = p.x;
		this.y = p.y;
	}
	
	@Override
	public boolean equals(Object o) {
		try {
			Point p = (Point) o;
			return (p.x == x && p.y == y);
		} catch (ClassCastException cce) {
			return false;
		}
	}
	
	
	public double getX() {
		return x;
	}
	
	public double getY() {
		return y;
	}

	public void setX(double d) {
		x = d;
		
	}

	public void setY(double d) {
		y = d;
	}
	
	@Override
	public String toString() {
		return "<" + x + "," + y + ">";
	}
	public double distance(Point cabLocation) {
		return Math.sqrt(Math.pow(x-cabLocation.x, 2) + Math.pow(y-cabLocation.y,2));
	}
}
