/*
 * Copyright 2011-2014 David Godfrey. All rights reserved.
 */
package com.godfrey.geometry.voronoi.exception;

@SuppressWarnings("serial")
public class SiteEventException extends Exception {
	
	String except;
	
	public String getExcept() {
		return except;
	}

	public void setExcept(String except) {
		this.except = except;
	}

	public SiteEventException() {
		super();
		except = "unknown";
	}

	public SiteEventException(String err) {
		super(err);
		except = err;
	}
	
}
