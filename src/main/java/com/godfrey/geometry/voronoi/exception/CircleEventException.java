/*
 * Copyright 2011-2014 David Godfrey. All rights reserved.
 */
package com.godfrey.geometry.voronoi.exception;

@SuppressWarnings("serial")
public class CircleEventException extends Exception {
	
	String except;
	
	public String getExcept() {
		return except;
	}

	public void setExcept(String except) {
		this.except = except;
	}

	public CircleEventException() {
		super();
		except = "unknown";
	}

	public CircleEventException(String err) {
		super(err);
		except = err;
	}
}
